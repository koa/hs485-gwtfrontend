package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveValueEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;
import ch.eleveneye.hs485.api.data.TFSValue;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.TFSensor;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;

public class TFSSolutionBuilder implements SolutionBuilder {
	private static final Logger							logger	= LoggerFactory.getLogger(TFSSolutionBuilder.class);

	private final Registry									registry;
	private final ScheduledExecutorService	executorService;

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

			@Override
			public void activateSolution(final ConfigSolutionPrimitive otherEndSolutionPrimitive, final ActivationPhase phase,
					final Collection<ConfigSolutionPrimitive> allSelectedPrimitives) {
				if (connection.getSource() instanceof PrimitiveValueEventSource) {
					final PrimitiveValueEventSource valueSource = (PrimitiveValueEventSource) connection.getSource();
					final SoftwareValueEventTargetSolutionPrimtive eventReceiver = (SoftwareValueEventTargetSolutionPrimtive) otherEndSolutionPrimitive;
					if (phase == ActivationPhase.EXECUTE)
						executorService.scheduleWithFixedDelay(new Runnable() {

							@Override
							public void run() {
								try {
									final PhysicallySensor sensor = registry.getPhysicallySensor(valueSource.getInput().getDeviceAddress(), valueSource.getInput()
											.getInputAddress());
									final TFSensor tfSensor = (TFSensor) sensor;
									final TFSValue tfsValue = tfSensor.readTF();
									final ValueEvent event = new ValueEvent();
									switch (valueSource.getSensorType()) {
									case TEMPERATURE:
										event.setValue((float) tfsValue.readTemperatur());
										break;
									case HUMIDITY:
										event.setValue(tfsValue.getHumidity());
										break;
									default:
										throw new IllegalArgumentException("Sensortype " + valueSource.getSensorType() + " unknown");
									}
									eventReceiver.takeEvent(event);
								} catch (final IOException e) {
									logger.error("Cannot read sensor " + valueSource.getInput(), e);
								}

							}
						}, 20, 120, TimeUnit.SECONDS);
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
			public int cost() {
				return 100;
			}

			@Override
			public PrimitiveConnection getConnection() {
				return connection;
			}
		});
	}
}
