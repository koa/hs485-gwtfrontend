package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveOutputDeviceKeyEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.ConfigSolutionPrimitive;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.DistributingMessageHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.HS485DSolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.HS485SSolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.SolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.physically.HS485D;
import ch.eleveneye.hs485.device.physically.HS485S;
import ch.eleveneye.hs485.device.physically.PhysicallyDevice;

public class Configurator {
	private static class AvailableConnectionConfiguration {
		private ConfigSolutionPrimitive	sourceSolution;
		private ConfigSolutionPrimitive	targetSolution;

		boolean checkCoexistenceTo(final AvailableConnectionConfiguration otherConfiguration) {
			if (!canCoexist(sourceSolution, otherConfiguration.sourceSolution))
				return false;
			if (!canCoexist(targetSolution, otherConfiguration.sourceSolution))
				return false;
			if (!canCoexist(sourceSolution, otherConfiguration.targetSolution))
				return false;
			if (!canCoexist(targetSolution, otherConfiguration.targetSolution))
				return false;
			return true;
		}

		boolean checkInternCoexistence() {
			return canCoexist(sourceSolution, targetSolution);
		}

		int costOfConfiguration() {
			return sourceSolution.cost() + targetSolution.cost();
		}
	}

	private static final Logger	logger	= LoggerFactory.getLogger(Configurator.class);

	private static boolean canCoexist(final ConfigSolutionPrimitive solution1, final ConfigSolutionPrimitive solution2) {
		if (!solution1.canCoexistWith(solution2))
			return false;
		return solution2.canCoexistWith(solution1);
	}

	private final Map<PrimitiveConnection, Collection<AvailableConnectionConfiguration>>	variantsPerConnection	= new IdentityHashMap<PrimitiveConnection, Collection<AvailableConnectionConfiguration>>();

	private final Map<Class<? extends PhysicallyDevice>, SolutionBuilder>									solutionBuilders			= new HashMap<Class<? extends PhysicallyDevice>, SolutionBuilder>();

	private final Registry																																registry;

	private int																																						currentBestSolutionCost;

	private AvailableConnectionConfiguration[]																						currentBestSolution;

	public Configurator(final Registry registry, final ScheduledExecutorService executorService) {
		this.registry = registry;
		final HashMap<KeySensor, DistributingMessageHandler> messageHandlers = new HashMap<KeySensor, DistributingMessageHandler>();
		solutionBuilders.put(HS485S.class, new HS485SSolutionBuilder(registry, executorService, messageHandlers));
		solutionBuilders.put(HS485D.class, new HS485DSolutionBuilder(registry, executorService, messageHandlers));
	}

	public void appendAction(final Action<? extends Event> action) {
		final String eventType = action.getEventType();
		if (eventType.equals(KeyEvent.class.getName())) {
			for (final EventSource<? extends Event> source : action.getSources())
				if (source instanceof KeyPairEventSource) {
					final KeyPairEventSource keyPairEventSource = (KeyPairEventSource) source;

					if (keyPairEventSource.getOnInputConnector() != null && keyPairEventSource.getOnInputConnector().getAddress() != null) {
						final PrimitiveKeyEventSource onEventSource = new PrimitiveKeyEventSource();
						onEventSource.setKeyType(KeyType.ON);
						onEventSource.setInput(keyPairEventSource.getOnInputConnector().getAddress());
						appendSinks(action, onEventSource);
					}

					if (keyPairEventSource.getOffInputConnector() != null && keyPairEventSource.getOffInputConnector().getAddress() != null) {
						final PrimitiveKeyEventSource offEventSource = new PrimitiveKeyEventSource();
						offEventSource.setKeyType(KeyType.OFF);
						offEventSource.setInput(keyPairEventSource.getOffInputConnector().getAddress());
						appendSinks(action, offEventSource);
					}
				} else
					throw new RuntimeException("Event source type " + source.getClass() + " unknown");
		} else
			throw new RuntimeException("Unknown Event-Type: " + eventType);
	}

	public void applyConfiguration() {
		final AvailableConnectionConfiguration[] currentSolution = new AvailableConnectionConfiguration[variantsPerConnection.size()];
		final List<Collection<AvailableConnectionConfiguration>> availableVariants = new ArrayList<Collection<AvailableConnectionConfiguration>>(
				variantsPerConnection.values());
		currentBestSolutionCost = Integer.MAX_VALUE;
		currentBestSolution = new AvailableConnectionConfiguration[variantsPerConnection.size()];
		findBestSolution(0, 0, currentSolution, availableVariants);
		if (currentBestSolutionCost == Integer.MAX_VALUE)
			throw new RuntimeException("No Solution found");
		final ArrayList<ConfigSolutionPrimitive> allSelectedPrimitives = new ArrayList<ConfigSolutionPrimitive>(currentBestSolution.length * 2);
		for (final AvailableConnectionConfiguration configuration : currentBestSolution) {
			allSelectedPrimitives.add(configuration.sourceSolution);
			allSelectedPrimitives.add(configuration.targetSolution);
		}
		for (final ConfigSolutionPrimitive.ActivationPhase phase : new ConfigSolutionPrimitive.ActivationPhase[] {
				ConfigSolutionPrimitive.ActivationPhase.PREPARE, ConfigSolutionPrimitive.ActivationPhase.EXECUTE })
			for (final AvailableConnectionConfiguration configuration : currentBestSolution) {
				configuration.sourceSolution.activateSolution(configuration.targetSolution, phase, allSelectedPrimitives);
				configuration.targetSolution.activateSolution(configuration.sourceSolution, phase, allSelectedPrimitives);
			}
		logger.info("Solution Cost: " + currentBestSolutionCost);
	}

	private void appendConnection(final PrimitiveConnection connection) {
		variantsPerConnection.put(connection, buildVariants(connection));
	}

	private void appendSinks(final Action<? extends Event> action, final PrimitiveKeyEventSource eventSource) {
		for (final EventSink<? extends Event> sink : action.getSinks())
			if (sink instanceof ActorKeySink) {
				final ActorKeySink actorSink = (ActorKeySink) sink;
				if (actorSink.getOutputDevice() != null && actorSink.getOutputDevice().getAddress() != null) {
					final PrimitiveOutputDeviceKeyEventSink primitiveSink = new PrimitiveOutputDeviceKeyEventSink();
					primitiveSink.setAddress(actorSink.getOutputDevice().getAddress());
					primitiveSink.setOffTime(actorSink.getAutoOffTime());
					final PrimitiveConnection connection = new PrimitiveConnection();
					connection.setSource(eventSource);
					connection.setSink(primitiveSink);
					appendConnection(connection);
				}
			} else
				throw new RuntimeException("Event sink type " + sink.getClass() + " unknown");
	}

	private Collection<AvailableConnectionConfiguration> buildVariants(final PrimitiveConnection connection) {
		final ArrayList<AvailableConnectionConfiguration> ret = new ArrayList<Configurator.AvailableConnectionConfiguration>();
		final Collection<ConfigSolutionPrimitive> sourceSolutions = makeSourceSolutions(connection);
		final Collection<ConfigSolutionPrimitive> targetSolutions = makeTargetSolutions(connection);
		for (final ConfigSolutionPrimitive source : sourceSolutions)
			for (final ConfigSolutionPrimitive target : targetSolutions) {
				final AvailableConnectionConfiguration availableConnectionConfiguration = new AvailableConnectionConfiguration();
				availableConnectionConfiguration.sourceSolution = source;
				availableConnectionConfiguration.targetSolution = target;
				if (availableConnectionConfiguration.checkInternCoexistence())
					ret.add(availableConnectionConfiguration);
			}
		ret.trimToSize();
		if (ret.isEmpty())
			throw new RuntimeException("Cannot find any Resolution for Connection " + connection);
		return ret;
	}

	private void findBestSolution(final int currentIndex, final int costUntilHere, final AvailableConnectionConfiguration[] currentSolution,
			final List<Collection<AvailableConnectionConfiguration>> availableVariants) {
		configurations: for (final AvailableConnectionConfiguration connectionConfiguration : availableVariants.get(currentIndex)) {
			for (int i = 0; i < currentIndex; i++)
				if (!connectionConfiguration.checkCoexistenceTo(currentSolution[i]))
					continue configurations;
			currentSolution[currentIndex] = connectionConfiguration;
			final int nextCost = costUntilHere + connectionConfiguration.costOfConfiguration();
			if (nextCost >= currentBestSolutionCost)
				continue configurations;
			if (currentIndex < currentSolution.length - 1)
				findBestSolution(currentIndex + 1, nextCost, currentSolution, availableVariants);
			else {
				currentBestSolutionCost = nextCost;
				System.arraycopy(currentSolution, 0, currentBestSolution, 0, currentSolution.length);
			}
		}
	}

	private SolutionBuilder getSolutionBuilderFor(final int deviceAddress) throws IOException {
		final PhysicallyDevice device = registry.getPhysicallyDevice(deviceAddress);
		final SolutionBuilder solutionBuilder = solutionBuilders.get(device.getClass());
		if (solutionBuilder == null)
			throw new IllegalArgumentException("Device " + device + " not supported");
		return solutionBuilder;
	}

	private Collection<ConfigSolutionPrimitive> makeSourceSolutions(final PrimitiveConnection connection) {
		try {
			final PrimitiveEventSource source = connection.getSource();
			if (source instanceof PrimitiveKeyEventSource) {
				final PrimitiveKeyEventSource keyEventSource = (PrimitiveKeyEventSource) source;
				final int deviceAddress = keyEventSource.getInput().getDeviceAddress();
				final SolutionBuilder solutionBuilder = getSolutionBuilderFor(deviceAddress);
				return solutionBuilder.makeSourceSolutionVariants(connection);
			} else
				throw new IllegalArgumentException("Source-Primitive " + source.getClass() + " not supported");
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Collection<ConfigSolutionPrimitive> makeTargetSolutions(final PrimitiveConnection connection) {
		try {
			final PrimitiveEventSink sink = connection.getSink();
			if (sink instanceof PrimitiveOutputDeviceKeyEventSink) {
				final PrimitiveOutputDeviceKeyEventSink outputDeviceSink = (PrimitiveOutputDeviceKeyEventSink) sink;
				final SolutionBuilder solutionBuilder = getSolutionBuilderFor(outputDeviceSink.getAddress().getDeviceAddress());
				return solutionBuilder.makeSinkSolutionVariants(connection);
			} else
				throw new IllegalArgumentException("Sink-Primitive " + sink.getClass() + " not supported");
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
