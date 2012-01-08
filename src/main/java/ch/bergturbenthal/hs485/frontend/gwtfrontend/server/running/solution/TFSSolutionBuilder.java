package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveValueEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;
import ch.eleveneye.hs485.api.data.TFSValue;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.TFSensor;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;

public class TFSSolutionBuilder implements SolutionBuilder {
	private class PollRunnable implements Runnable, Closeable {
		private final InputAddress								inputAddress;
		private final Collection<TFValueHandler>	handlers	= new ArrayList<TFSSolutionBuilder.TFValueHandler>();
		private final ScheduledFuture<?>					future;

		public PollRunnable(final InputAddress address) {
			inputAddress = address;
			future = executorService.scheduleWithFixedDelay(this, 20, 120, TimeUnit.SECONDS);
		}

		public synchronized void appendValueHandler(final TFValueHandler handler) {
			handlers.add(handler);
		}

		@Override
		public void close() throws IOException {
			if (future != null)
				future.cancel(false);
		}

		public boolean isEmpty() {
			return handlers.isEmpty();
		}

		public synchronized void removeHandler(final TFValueHandler handler) {
			handlers.remove(handler);
		}

		@Override
		public synchronized void run() {
			try {
				final PhysicallySensor sensor = registry.getPhysicallySensor(inputAddress.getDeviceAddress(), inputAddress.getInputAddress());
				final TFSensor tfSensor = (TFSensor) sensor;
				final TFSValue tfsValue = tfSensor.readTF();
				for (final TFValueHandler handler : handlers)
					handler.takeValue(tfsValue);
			} catch (final Throwable e) {
				logger.error("Cannot access to TFS " + inputAddress);
			}
		}
	}

	private static interface TFValueHandler {
		void takeValue(TFSValue value);
	}

	private static final Logger										logger		= LoggerFactory.getLogger(TFSSolutionBuilder.class);

	private final Registry												registry;
	private final ScheduledExecutorService				executorService;
	private final Map<InputAddress, PollRunnable>	runnables	= new HashMap<InputAddress, TFSSolutionBuilder.PollRunnable>();

	public TFSSolutionBuilder(final Registry registry, final ScheduledExecutorService executorService) {
		this.registry = registry;
		this.executorService = executorService;
	}

	@Override
	public Collection<ConfigSolutionPrimitive> makeSinkSolutionVariants(final PrimitiveConnection connection) {
		throw new IllegalArgumentException("This Solution builder has no output");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<ConfigSolutionPrimitive> makeSourceSolutionVariants(final PrimitiveConnection connection) {
		return (Collection<ConfigSolutionPrimitive>) (Collection<?>) Collections.singletonList(new ConfigSolutionPrimitive() {

			private TFValueHandler	handler;

			@Override
			public void activateSolution(final ConfigSolutionPrimitive otherEndSolutionPrimitive, final ActivationPhase phase,
					final Collection<ConfigSolutionPrimitive> allSelectedPrimitives) {
				if (connection.getSource() instanceof PrimitiveValueEventSource) {
					final PrimitiveValueEventSource valueSource = (PrimitiveValueEventSource) connection.getSource();
					final SoftwareValueEventTargetSolutionPrimtive eventReceiver = (SoftwareValueEventTargetSolutionPrimtive) otherEndSolutionPrimitive;
					if (phase == ActivationPhase.EXECUTE) {
						handler = new TFValueHandler() {

							@Override
							public void takeValue(final TFSValue value) {
								final ValueEvent event = new ValueEvent();
								switch (valueSource.getSensorType()) {
								case TEMPERATURE:
									event.setValue((float) value.readTemperatur());
									break;
								case HUMIDITY:
									event.setValue(value.getHumidity());
									break;
								default:
									throw new IllegalArgumentException("Sensortype " + valueSource.getSensorType() + " unknown");
								}
								eventReceiver.takeEvent(event);
							}
						};
						appendHandler(valueSource.getInput(), handler);
					}
				} else
					throw new IllegalArgumentException("Cannot take Event-Source of Type " + connection.getSource().getClass());
			}

			@Override
			public boolean canCoexistWith(final ConfigSolutionPrimitive otherSolution) {
				if (otherSolution.getConnection() == connection)
					return otherSolution instanceof SoftwareValueEventTargetSolutionPrimtive;
				return true;
			}

			@Override
			public void close() throws IOException {
				if (connection.getSource() instanceof PrimitiveValueEventSource) {
					final PrimitiveValueEventSource valueSource = (PrimitiveValueEventSource) connection.getSource();
					removeHandler(valueSource.getInput(), handler);
				}
			}

			@Override
			public int cost() {
				return 100;
			}

			@Override
			public PrimitiveConnection getConnection() {
				return connection;
			}
		});
	}

	private synchronized void appendHandler(final InputAddress address, final TFValueHandler handler) {
		if (!runnables.containsKey(address))
			runnables.put(address, new PollRunnable(address));
		runnables.get(address).appendValueHandler(handler);
	}

	private synchronized void removeHandler(final InputAddress address, final TFValueHandler handler) {
		final PollRunnable pollRunnable = runnables.get(address);
		if (pollRunnable == null)
			return;
		pollRunnable.removeHandler(handler);
		if (pollRunnable.isEmpty()) {
			try {
				pollRunnable.close();
			} catch (final IOException e) {
				logger.error("Cannot close Pollrunnable", e);
			}
			runnables.remove(address);
		}
	}
}
