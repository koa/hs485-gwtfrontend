package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveOutputDeviceKeyEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveSwitchingOutputDeviceValueEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveValueEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveValueEventSource.SensorType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.ConfigSolutionPrimitive;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.DistributingMessageHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.HS485DSolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.HS485SSolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.IO127SolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.SolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution.TFSSolutionBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.PirKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.SwitchingActorValueSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.TemperatureValueSensorEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.physically.HS485D;
import ch.eleveneye.hs485.device.physically.HS485S;
import ch.eleveneye.hs485.device.physically.IO127;
import ch.eleveneye.hs485.device.physically.PhysicallyDevice;
import ch.eleveneye.hs485.device.physically.TFS;

import com.google.gwt.event.shared.UmbrellaException;

public class ConfigurationBuilder {
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

	private static final class MadeConfigurator implements Configurator {
		private final ArrayList<AvailableConnectionConfiguration>	runningSolution;

		public MadeConfigurator(final ArrayList<AvailableConnectionConfiguration> runningSolution) {
			this.runningSolution = runningSolution;
		}

		@Override
		public void close() throws IOException {
			final HashSet<Throwable> exceptions = new HashSet<Throwable>();
			for (final AvailableConnectionConfiguration configuration : runningSolution) {
				try {
					configuration.sourceSolution.close();
				} catch (final Throwable t) {
					exceptions.add(t);
				}
				try {
					configuration.targetSolution.close();
				} catch (final Throwable t) {
					exceptions.add(t);
				}
			}
			if (!exceptions.isEmpty())
				if (exceptions.size() == 1)
					throw new RuntimeException(exceptions.iterator().next());
				else
					throw new UmbrellaException(exceptions);
		}

		@Override
		public void run() {
			final Collection<ConfigSolutionPrimitive> allSelectedPrimitives = new ArrayList<ConfigSolutionPrimitive>(runningSolution.size() * 2);
			for (final AvailableConnectionConfiguration configuration : runningSolution) {
				allSelectedPrimitives.add(configuration.sourceSolution);
				allSelectedPrimitives.add(configuration.targetSolution);
			}
			for (final ConfigSolutionPrimitive.ActivationPhase phase : new ConfigSolutionPrimitive.ActivationPhase[] {
					ConfigSolutionPrimitive.ActivationPhase.PREPARE, ConfigSolutionPrimitive.ActivationPhase.EXECUTE })
				for (final AvailableConnectionConfiguration configuration : runningSolution) {
					configuration.sourceSolution.activateSolution(configuration.targetSolution, phase, allSelectedPrimitives);
					configuration.targetSolution.activateSolution(configuration.sourceSolution, phase, allSelectedPrimitives);
				}
		}
	}

	private static final Logger	logger	= LoggerFactory.getLogger(ConfigurationBuilder.class);

	private static boolean canCoexist(final ConfigSolutionPrimitive solution1, final ConfigSolutionPrimitive solution2) {
		if (!solution1.canCoexistWith(solution2))
			return false;
		return solution2.canCoexistWith(solution1);
	}

	private final List<Collection<AvailableConnectionConfiguration>>			existingVariants	= new ArrayList<Collection<AvailableConnectionConfiguration>>();

	private final Map<Class<? extends PhysicallyDevice>, SolutionBuilder>	solutionBuilders	= new HashMap<Class<? extends PhysicallyDevice>, SolutionBuilder>();

	private final Registry																								registry;

	private int																														currentBestSolutionCost;

	private AvailableConnectionConfiguration[]														currentBestSolution;

	public ConfigurationBuilder(final Registry registry, final ScheduledExecutorService executorService) {
		this.registry = registry;
		final HashMap<KeySensor, DistributingMessageHandler> messageHandlers = new HashMap<KeySensor, DistributingMessageHandler>();
		solutionBuilders.put(HS485S.class, new HS485SSolutionBuilder(registry, executorService, messageHandlers));
		solutionBuilders.put(HS485D.class, new HS485DSolutionBuilder(registry, executorService, messageHandlers));
		solutionBuilders.put(IO127.class, new IO127SolutionBuilder(registry, executorService, messageHandlers));
		solutionBuilders.put(TFS.class, new TFSSolutionBuilder(registry, executorService));
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
						appendKeyEventSinks(action, onEventSource);
					}

					if (keyPairEventSource.getOffInputConnector() != null && keyPairEventSource.getOffInputConnector().getAddress() != null) {
						final PrimitiveKeyEventSource offEventSource = new PrimitiveKeyEventSource();
						offEventSource.setKeyType(KeyType.OFF);
						offEventSource.setInput(keyPairEventSource.getOffInputConnector().getAddress());
						appendKeyEventSinks(action, offEventSource);
					}
				} else if (source instanceof ToggleKeyEventSource) {
					final ToggleKeyEventSource toggleKeyEventSource = (ToggleKeyEventSource) source;
					if (toggleKeyEventSource.getInputConnector() != null && toggleKeyEventSource.getInputConnector().getAddress() != null) {
						final PrimitiveKeyEventSource onEventSource = new PrimitiveKeyEventSource();
						onEventSource.setKeyType(KeyType.TOGGLE);
						onEventSource.setInput(toggleKeyEventSource.getInputConnector().getAddress());
						appendKeyEventSinks(action, onEventSource);
					}
				} else if (source instanceof PirKeyEventSource) {
					final PirKeyEventSource pirKeyEventSource = (PirKeyEventSource) source;
					if (pirKeyEventSource.getInputConnector() != null && pirKeyEventSource.getInputConnector().getAddress() != null) {
						final PrimitiveKeyEventSource onEventSource = new PrimitiveKeyEventSource();
						onEventSource.setKeyType(KeyType.ON);
						onEventSource.setInput(pirKeyEventSource.getInputConnector().getAddress());
						appendKeyEventSinks(action, onEventSource);
					}
				} else
					throw new RuntimeException("Event source type " + source.getClass() + " unknown");
		} else if (eventType.equals(ValueEvent.class.getName())) {
			for (final EventSource<? extends Event> source : action.getSources())
				if (source instanceof TemperatureValueSensorEventSource) {
					final TemperatureValueSensorEventSource temperatureValueSensorEventSource = (TemperatureValueSensorEventSource) source;
					if (temperatureValueSensorEventSource.getInputConnector() != null
							&& temperatureValueSensorEventSource.getInputConnector().getAddress() != null) {
						final PrimitiveValueEventSource valueEventSource = new PrimitiveValueEventSource();
						valueEventSource.setInput(temperatureValueSensorEventSource.getInputConnector().getAddress());
						valueEventSource.setSensorType(SensorType.TEMPERATURE);
						appendValueEventSinks(action, valueEventSource);
					}
				} else
					throw new RuntimeException("Event source type " + source.getClass() + " unknown");
		} else
			throw new RuntimeException("Unknown Event-Type: " + eventType);
	}

	public Configurator buildConfiguration() {
		final Map<AvailableConnectionConfiguration, Set<AvailableConnectionConfiguration>> coexistanceErrors = new IdentityHashMap<ConfigurationBuilder.AvailableConnectionConfiguration, Set<AvailableConnectionConfiguration>>();
		final Collection<List<Collection<AvailableConnectionConfiguration>>> variantClusters = new LinkedList<List<Collection<AvailableConnectionConfiguration>>>();
		for (final Collection<AvailableConnectionConfiguration> variants : existingVariants) {
			final List<Collection<AvailableConnectionConfiguration>> singletonCluster = new LinkedList<Collection<AvailableConnectionConfiguration>>();
			singletonCluster.add(variants);
			variantClusters.add(singletonCluster);
		}
		int totalCount = 0;
		int errorCount = 0;
		logger.info("Cluster count before: " + variantClusters.size());
		logger.info("Start checking dependencies");
		for (int i = 1; i < existingVariants.size(); i++) {
			final Collection<AvailableConnectionConfiguration> outerVariants = existingVariants.get(i);
			for (int j = 0; j < i; j++) {
				final Collection<AvailableConnectionConfiguration> innerVariants = existingVariants.get(j);
				for (final AvailableConnectionConfiguration outerVariant : outerVariants)
					for (final AvailableConnectionConfiguration innerVariant : innerVariants) {
						totalCount += 1;
						if (!outerVariant.checkCoexistenceTo(innerVariant)) {
							appendCoexistanceError(coexistanceErrors, outerVariant, innerVariant);
							appendCoexistanceError(coexistanceErrors, innerVariant, outerVariant);
							errorCount += 1;
							mergeClusters(variantClusters, outerVariant, innerVariant);
						}
					}
			}
		}
		logger.info("Dependencies checked: " + totalCount + " combinations with " + errorCount + " unavailable denpencies");
		logger.info("Cluster count after: " + variantClusters.size());

		final ArrayList<AvailableConnectionConfiguration> runningSolution = new ArrayList<ConfigurationBuilder.AvailableConnectionConfiguration>(
				existingVariants.size());

		for (final List<Collection<AvailableConnectionConfiguration>> cluster : variantClusters)
			runningSolution.addAll(findBestClusterSolution(cluster, coexistanceErrors));
		logger.info("best Solution found");

		return new MadeConfigurator(runningSolution);
	}

	private void appendCoexistanceError(final Map<AvailableConnectionConfiguration, Set<AvailableConnectionConfiguration>> coexistanceErrors,
			final AvailableConnectionConfiguration variant1, final AvailableConnectionConfiguration variant2) {
		final Set<AvailableConnectionConfiguration> innerSet = coexistanceErrors.get(variant1);
		if (innerSet == null) {
			final HashSet<AvailableConnectionConfiguration> newSet = new HashSet<ConfigurationBuilder.AvailableConnectionConfiguration>();
			newSet.add(variant2);
			coexistanceErrors.put(variant1, newSet);
		} else
			innerSet.add(variant2);
	}

	private void appendConnection(final PrimitiveConnection connection) {
		final Collection<AvailableConnectionConfiguration> builtVariants = buildVariants(connection);
		if (builtVariants.size() > 0)
			existingVariants.add(builtVariants);
	}

	private void appendKeyEventSinks(final Action<? extends Event> action, final PrimitiveKeyEventSource eventSource) {
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

	private void appendValueEventSinks(final Action<? extends Event> action, final PrimitiveValueEventSource valueEventSource) {
		for (final EventSink<? extends Event> sink : action.getSinks())
			if (sink instanceof SwitchingActorValueSink) {
				final SwitchingActorValueSink actorSink = (SwitchingActorValueSink) sink;
				if (actorSink.getOutputDevice() != null && actorSink.getOutputDevice().getAddress() != null) {
					final PrimitiveSwitchingOutputDeviceValueEventSink eventSink = new PrimitiveSwitchingOutputDeviceValueEventSink();
					eventSink.setAddress(actorSink.getOutputDevice().getAddress());
					eventSink.setTriggerValue(actorSink.getTriggerLevel());
					eventSink.setOnWhenBelow(actorSink.isOnBelow());
					final PrimitiveConnection connection = new PrimitiveConnection();
					connection.setSource(valueEventSource);
					connection.setSink(eventSink);
					appendConnection(connection);
				}
			}
	}

	private Collection<AvailableConnectionConfiguration> buildVariants(final PrimitiveConnection connection) {
		final ArrayList<AvailableConnectionConfiguration> ret = new ArrayList<ConfigurationBuilder.AvailableConnectionConfiguration>();
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
		Collections.sort(ret, new Comparator<AvailableConnectionConfiguration>() {

			@Override
			public int compare(final AvailableConnectionConfiguration o1, final AvailableConnectionConfiguration o2) {
				return Integer.valueOf(o1.costOfConfiguration()).compareTo(Integer.valueOf(o2.costOfConfiguration()));
			}
		});
		ret.trimToSize();
		if (ret.isEmpty())
			throw new RuntimeException("Cannot find any Resolution for Connection " + connection);
		return ret;
	}

	private Collection<AvailableConnectionConfiguration> findBestClusterSolution(final List<Collection<AvailableConnectionConfiguration>> cluster,
			final Map<AvailableConnectionConfiguration, Set<AvailableConnectionConfiguration>> coexistanceErrors) {
		final AvailableConnectionConfiguration[] currentSolution = new AvailableConnectionConfiguration[cluster.size()];
		currentBestSolutionCost = Integer.MAX_VALUE;
		currentBestSolution = new AvailableConnectionConfiguration[cluster.size()];
		findBestSolution(0, 0, currentSolution, cluster, coexistanceErrors);
		return Arrays.asList(currentBestSolution);
	}

	private void findBestSolution(final int currentIndex, final int costUntilHere, final AvailableConnectionConfiguration[] currentSolution,
			final List<Collection<AvailableConnectionConfiguration>> availableVariants,
			final Map<AvailableConnectionConfiguration, Set<AvailableConnectionConfiguration>> coexistanceErrors) {
		configurations: for (final AvailableConnectionConfiguration connectionConfiguration : availableVariants.get(currentIndex)) {
			final Set<AvailableConnectionConfiguration> errorSetOfConnection = coexistanceErrors.get(connectionConfiguration);
			if (errorSetOfConnection != null)
				for (int i = 0; i < currentIndex; i++)
					if (errorSetOfConnection.contains(currentSolution[i]))
						continue configurations;
			currentSolution[currentIndex] = connectionConfiguration;
			final int nextCost = costUntilHere + connectionConfiguration.costOfConfiguration();
			if (nextCost >= currentBestSolutionCost)
				continue configurations;
			if (currentIndex < currentSolution.length - 1)
				findBestSolution(currentIndex + 1, nextCost, currentSolution, availableVariants, coexistanceErrors);
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
			} else if (source instanceof PrimitiveValueEventSource) {
				final PrimitiveValueEventSource valueEventSource = (PrimitiveValueEventSource) source;
				final int deviceAddress = valueEventSource.getInput().getDeviceAddress();
				return getSolutionBuilderFor(deviceAddress).makeSourceSolutionVariants(connection);
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
			} else if (sink instanceof PrimitiveSwitchingOutputDeviceValueEventSink) {
				final PrimitiveSwitchingOutputDeviceValueEventSink eventSink = (PrimitiveSwitchingOutputDeviceValueEventSink) sink;
				final SolutionBuilder solutionBuilder = getSolutionBuilderFor(eventSink.getAddress().getDeviceAddress());
				return solutionBuilder.makeSinkSolutionVariants(connection);
			} else
				throw new IllegalArgumentException("Sink-Primitive " + sink.getClass() + " not supported");
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void mergeClusters(final Collection<List<Collection<AvailableConnectionConfiguration>>> variantClusters,
			final AvailableConnectionConfiguration outerVariant, final AvailableConnectionConfiguration innerVariant) {
		Collection<Collection<AvailableConnectionConfiguration>> innerClusterHit = null;
		Collection<Collection<AvailableConnectionConfiguration>> outerClusterHit = null;
		for (final Collection<Collection<AvailableConnectionConfiguration>> cluster : variantClusters)
			for (final Collection<AvailableConnectionConfiguration> clusterMember : cluster) {
				if (innerClusterHit == null && clusterMember.contains(innerVariant))
					innerClusterHit = cluster;
				if (outerClusterHit == null && clusterMember.contains(outerVariant))
					outerClusterHit = cluster;
				if (innerClusterHit != null && outerClusterHit != null) {
					if (innerClusterHit != outerClusterHit) {
						variantClusters.remove(innerClusterHit);
						outerClusterHit.addAll(innerClusterHit);
					}
					return;
				}
			}
	}
}
