package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Connection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.ConnectionTargetAction;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.ConnectionType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
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
			final Map<String, Connection> connections = new LinkedHashMap<String, Connection>();
			for (final Connection connection : plan.getConnections()) {
				final InputConnector inputConnector = connection.getInputConnector();
				if (inputConnector == null || inputConnector.getAddress() == null) {
					appendIncompleteConnection(connections, connection);
					continue;
				}
				final OutputDevice outputDevice = connection.getOutputDevice();
				if (outputDevice == null || outputDevice.getAddress() == null) {
					appendIncompleteConnection(connections, connection);
					continue;
				}
				final InputAddress inputAddress = inputConnector.getAddress();
				final OutputAddress outputAddress = outputDevice.getAddress();
				connections.put(makeKey(inputAddress, outputAddress), connection);
			}
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
							addAllConnectionsOfSensor(plan, connections, keySensor, independentConfigurableSensor, targetAction);
						} else if (sensor instanceof PairableSensor) {
							final PairableSensor pairableSensor = (PairableSensor) sensor;
							addAllConnectionsOfSensor(plan, connections, keySensor, pairableSensor, pairableSensor.isPaired() ? ConnectionTargetAction.ON
									: ConnectionTargetAction.TOGGLE);
							if (pairableSensor.isPaired())
								addAllConnectionsOfSensor(plan, connections, keySensor,
										new DummyPhysicallySensor(pairableSensor.getModuleAddr(), pairableSensor.getSensorNr() + 1), ConnectionTargetAction.OFF);
						}
					}
			}
			plan.setConnections(new ArrayList<Connection>(connections.values()));
		} catch (final IOException e) {
			throw new RuntimeException("Problem reading existing connections", e);
		}

	}

	private void addAllConnectionsOfSensor(final Plan plan, final Map<String, Connection> connections, final KeySensor keySensor,
			final PhysicallySensor pairableSensor, final ConnectionTargetAction targetAction) throws IOException {
		final InputConnector foundInputConnector = findInputConnector(plan, pairableSensor);
		if (foundInputConnector == null)
			return;
		for (final Actor assignedActor : keySensor.listAssignedActors()) {
			final OutputDevice foundOutputDevice = findOutputDevice(plan, assignedActor);
			if (foundOutputDevice == null)
				continue;
			updateConnection(connections, foundInputConnector, foundOutputDevice, assignedActor, targetAction);
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

	private String makeKey(final InputAddress inputAddress, final OutputAddress outputAddress) {
		return inputAddress.getDeviceAddress() + "_" + inputAddress.getInputAddress() + ":" + outputAddress.getDeviceAddress() + "_"
				+ outputAddress.getOutputAddress();
	}

	private void updateConnection(final Map<String, Connection> connections, final InputConnector foundInputConnector,
			final OutputDevice foundOutputDevice, final Actor assignedActor, final ConnectionTargetAction targetAction) throws IOException {
		final String connectionKey = makeKey(foundInputConnector.getAddress(), foundOutputDevice.getAddress());
		final Connection connection;
		if (connections.containsKey(connectionKey))
			connection = connections.get(connectionKey);
		else {
			connection = new Connection();
			connection.setConnectionTargetAutoOff(false);
			connections.put(connectionKey, connection);
		}
		connection.setConnectionType(ConnectionType.EVENT);
		connection.setConnectionTargetAction(targetAction);
		connection.setInputConnector(foundInputConnector);
		connection.setOutputDevice(foundOutputDevice);
		if (assignedActor instanceof TimedActor) {
			final TimedActor timedActor = (TimedActor) assignedActor;
			connection.setConnectionTargetAutoOff(timedActor.getTimeMode() != TimeMode.NONE);
			connection.setConnectionTargetTimeout(timedActor.getTimeValue());
		}
	}
}
