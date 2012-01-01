package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Connection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.ConnectionTargetAction;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.Sensor;
import ch.eleveneye.hs485.device.TimedActor;
import ch.eleveneye.hs485.device.config.PairMode;
import ch.eleveneye.hs485.device.config.TimeMode;
import ch.eleveneye.hs485.device.physically.Actor;
import ch.eleveneye.hs485.device.physically.IndependentConfigurableSensor;
import ch.eleveneye.hs485.device.physically.PairableSensor;
import ch.eleveneye.hs485.device.physically.PairedSensorDevice;
import ch.eleveneye.hs485.device.physically.PhysicallyDevice;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;

@Service
public class ConfigurationService {
	private static class DummyPhysicallySensor implements PhysicallySensor {

		private final int	moduleAddr;
		private final int	sensorAddr;

		public DummyPhysicallySensor(final int moduleAddr, final int sensorAddr) {
			this.moduleAddr = moduleAddr;
			this.sensorAddr = sensorAddr;
		}

		@Override
		public int getModuleAddr() {
			return moduleAddr;
		}

		@Override
		public int getSensorNr() {
			return sensorAddr;
		}

	}

	@Autowired
	private Registry	hs485registry;

	void appendExistingConnections(final Plan plan) {
		try {
			final ArrayList<Action> actions = new ArrayList<Action>(plan.getActions());

			for (final PhysicallyDevice device : hs485registry.listPhysicalDevices()) {
				final Set<Integer> pairedSensors = new HashSet<Integer>();
				if (pairedSensors instanceof PairedSensorDevice) {
					final PairedSensorDevice pairedSensorDevice = (PairedSensorDevice) pairedSensors;
					for (int i = 0; i < pairedSensorDevice.getInputPairCount(); i++)
						if (pairedSensorDevice.getInputPairMode(i) == PairMode.JOINT)
							pairedSensors.add(i);
				}
				for (final Sensor sensor : device.listSensors())
					if (sensor instanceof KeySensor) {
						final KeySensor keySensor = (KeySensor) sensor;
						if (keySensor instanceof IndependentConfigurableSensor) {
							final IndependentConfigurableSensor independentConfigurableSensor = (IndependentConfigurableSensor) keySensor;
							final ConnectionTargetAction targetAction;
							switch (independentConfigurableSensor.getInputMode()) {
							case UP:
								targetAction = ConnectionTargetAction.ON;
								break;
							case DOWN:
								targetAction = ConnectionTargetAction.OFF;
								break;
							case TOGGLE:
								targetAction = ConnectionTargetAction.TOGGLE;
								break;
							default:
								continue;
							}
							addAllConnectionsOfSensor(plan, actions, keySensor, independentConfigurableSensor, targetAction);
						} else if (sensor instanceof PairableSensor) {
							final PairableSensor pairableSensor = (PairableSensor) sensor;
							addAllConnectionsOfSensor(plan, actions, keySensor, pairableSensor, pairableSensor.isPaired() ? ConnectionTargetAction.ON
									: ConnectionTargetAction.TOGGLE);
							if (pairableSensor.isPaired())
								addAllConnectionsOfSensor(plan, actions, keySensor,
										new DummyPhysicallySensor(pairableSensor.getModuleAddr(), pairableSensor.getSensorNr() + 1), ConnectionTargetAction.OFF);
						}
					}
			}
			final Map<Set<EventSink<? extends Event>>, Action> actionsBySink = new HashMap<Set<EventSink<? extends Event>>, Action>();
			for (final Action action : actions) {
				final Set<EventSink<? extends Event>> actionKey = new HashSet<EventSink<? extends Event>>(action.getSinks());
				final Action oldAction = actionsBySink.get(actionKey);
				if (oldAction == null) {
					action.setSinks(new ArrayList<EventSink<? extends Event>>(actionKey));
					actionsBySink.put(actionKey, action);
					continue;
				}
				final Collection<EventSource<? extends Event>> oldSources = new HashSet<EventSource<? extends Event>>(oldAction.getSources());
				for (final EventSource<? extends Event> source : action.getSources())
					if (source instanceof KeyPairEventSource) {
						final KeyPairEventSource newKeyPairEventSource = (KeyPairEventSource) source;
						if (newKeyPairEventSource.getOffInputConnector() == null && newKeyPairEventSource.getOnInputConnector() == null)
							continue;
						if (newKeyPairEventSource.getOffInputConnector() != null && newKeyPairEventSource.getOnInputConnector() != null) {
							oldSources.add(newKeyPairEventSource);
							continue;
						}
						boolean found = false;
						for (final EventSource<? extends Event> oldSource : oldSources)
							if (oldSource instanceof KeyPairEventSource) {
								final KeyPairEventSource oldEventSource = (KeyPairEventSource) oldSource;
								if (newKeyPairEventSource.getOnInputConnector() != null && oldEventSource.getOnInputConnector() == null) {
									oldEventSource.setOnInputConnector(newKeyPairEventSource.getOnInputConnector());
									found = true;
								}
								if (newKeyPairEventSource.getOffInputConnector() != null && oldEventSource.getOffInputConnector() == null) {
									oldEventSource.setOffInputConnector(newKeyPairEventSource.getOffInputConnector());
									found = true;
								}
								if (found)
									break;
							}
						if (!found)
							oldSources.add(newKeyPairEventSource);
					} else
						oldSources.add(source);
				oldAction.setSources(new ArrayList<EventSource<? extends Event>>(oldSources));
			}
			final Map<Set<EventSource<? extends Event>>, Action> actionsBySource = new HashMap<Set<EventSource<? extends Event>>, Action>();
			for (final Action action : actionsBySink.values()) {
				final Set<EventSource<? extends Event>> actionKey = new HashSet<EventSource<? extends Event>>(action.getSources());
				final Action oldAction = actionsBySource.get(actionKey);
				if (oldAction == null) {
					action.setSources(new ArrayList<EventSource<? extends Event>>(actionKey));
					actionsBySource.put(actionKey, action);
					continue;
				}
				final Collection<EventSink<? extends Event>> sinks = new HashSet<EventSink<? extends Event>>(oldAction.getSinks());
				sinks.addAll(action.getSinks());
				oldAction.setSinks(new ArrayList<EventSink<? extends Event>>(sinks));
			}
			plan.setActions(new ArrayList<Action>(actionsBySource.values()));
		} catch (final IOException e) {
			throw new RuntimeException("Problem reading existing connections", e);
		}

	}

	private void addAllConnectionsOfSensor(final Plan plan, final ArrayList<Action> actions, final KeySensor keySensor,
			final PhysicallySensor pairableSensor, final ConnectionTargetAction targetAction) throws IOException {
		final InputConnector foundInputConnector = findInputConnector(plan, pairableSensor);
		if (foundInputConnector == null)
			return;
		for (final Actor assignedActor : keySensor.listAssignedActors()) {
			final OutputDevice foundOutputDevice = findOutputDevice(plan, assignedActor);
			if (foundOutputDevice == null)
				continue;
			updateConnection(actions, foundInputConnector, foundOutputDevice, assignedActor, targetAction);
		}
	}

	private void appendIncompleteConnection(final Map<String, Connection> connections, final Connection connection) {
		connections.put(UUID.randomUUID().toString(), connection);
	}

	private InputConnector findInputConnector(final Plan plan, final PhysicallySensor sensor) {
		for (final Floor floor : plan.getFloors())
			for (final InputDevice inputDevice : floor.getInputDevices())
				for (final InputConnector inputConnector : inputDevice.getConnectors()) {
					final InputAddress inputAddress = inputConnector.getAddress();
					if (inputAddress != null && inputAddress.getDeviceAddress() == sensor.getModuleAddr()
							&& inputAddress.getInputAddress() == sensor.getSensorNr())
						return inputConnector;
				}
		return null;
	}

	private OutputDevice findOutputDevice(final Plan plan, final Actor assignedActor) {
		for (final Floor floor : plan.getFloors())
			for (final OutputDevice outputDevice : floor.getOutputDevices()) {
				final OutputAddress address = outputDevice.getAddress();
				if (address != null && address.getDeviceAddress() == assignedActor.getModuleAddr()
						&& address.getOutputAddress() == assignedActor.getActorNr())
					return outputDevice;
			}
		return null;
	}

	private void updateConnection(final ArrayList<Action> actions, final InputConnector foundInputConnector, final OutputDevice foundOutputDevice,
			final Actor assignedActor, final ConnectionTargetAction targetAction) throws IOException {

		final Action action = new Action();

		switch (targetAction) {
		case ON: {
			final KeyPairEventSource keyPairEventSource = new KeyPairEventSource();
			keyPairEventSource.setOnInputConnector(foundInputConnector);
			action.getSources().add(keyPairEventSource);
		}
			break;
		case OFF: {
			final KeyPairEventSource keyPairEventSource = new KeyPairEventSource();
			keyPairEventSource.setOffInputConnector(foundInputConnector);
			action.getSources().add(keyPairEventSource);
		}
			break;
		case TOGGLE: {
			final ToggleKeyEventSource toggleKeyEventSource = new ToggleKeyEventSource();
			toggleKeyEventSource.setInputConnector(foundInputConnector);
			action.getSources().add(toggleKeyEventSource);
		}
			break;
		}
		final ActorKeySink actorKeySink = new ActorKeySink();
		actorKeySink.setOutputDevice(foundOutputDevice);
		if (assignedActor instanceof TimedActor) {
			final TimedActor timedActor = (TimedActor) assignedActor;
			if (timedActor.getTimeMode() != TimeMode.NONE)
				actorKeySink.setAutoOffTime(Integer.valueOf(timedActor.getTimeValue()));
		}
		action.getSinks().add(actorKeySink);
		action.setEventType(KeyEvent.class.getName());
		actions.add(action);
	}
}
