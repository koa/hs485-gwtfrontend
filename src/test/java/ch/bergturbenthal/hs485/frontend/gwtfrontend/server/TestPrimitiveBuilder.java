package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.Configurator;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.physically.PhysicallyDevice;
import ch.eleveneye.hs485.device.utils.AbstractDevice;
import ch.eleveneye.hs485.dummy.HS485Dummy;

public class TestPrimitiveBuilder {
	private static final Logger	logger	= LoggerFactory.getLogger(TestPrimitiveBuilder.class);

	@Test
	public void testBuildPrimitive() throws IOException {
		final Registry registry = new Registry(new HS485Dummy());
		final Plan plan = makePlan();
		final Configurator primitiveBuilder = new Configurator(registry, Executors.newScheduledThreadPool(2));
		registry.doInTransaction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				registry.resetAllDevices();
				for (final Action<Event> action : plan.getActions())
					primitiveBuilder.appendAction(action);
				primitiveBuilder.applyConfiguration();
				return null;
			}
		});
		for (final PhysicallyDevice device : registry.listPhysicalDevices()) {
			logger.info("Device: " + device);
			if (device instanceof AbstractDevice) {
				final AbstractDevice abstractDevice = (AbstractDevice) device;
				abstractDevice.dumpVariables();
			}
		}
	}

	private Plan makePlan() {
		final Plan plan = new Plan();
		final Floor floor = new Floor();
		final InputDevice inputPanel = new InputDevice();
		inputPanel.setName("Experiment Panel");

		final InputConnector onKeyConnector = new InputConnector();
		onKeyConnector.setAddress(new InputAddress(0x440, 1));
		onKeyConnector.setType(InputDeviceType.PUSH);
		inputPanel.getConnectors().add(onKeyConnector);

		final InputConnector offKeyConnector = new InputConnector();
		offKeyConnector.setAddress(new InputAddress(0x440, 0));
		offKeyConnector.setType(InputDeviceType.PUSH);

		floor.getInputDevices().add(inputPanel);

		final OutputDevice lamp1 = new OutputDevice();
		lamp1.setAddress(new OutputAddress(0x440, 0));
		lamp1.setType(OutputDeviceType.LIGHT);
		floor.getOutputDevices().add(lamp1);

		final OutputDevice lamp2 = new OutputDevice();
		lamp2.setAddress(new OutputAddress(0x450, 0));
		lamp2.setType(OutputDeviceType.LIGHT);
		floor.getOutputDevices().add(lamp2);

		plan.getFloors().add(floor);

		final Action<KeyEvent> switchLamp2Action = new Action<KeyEvent>();
		switchLamp2Action.setEventType(KeyEvent.class.getName());
		final KeyPairEventSource source = new KeyPairEventSource();
		source.setOnInputConnector(onKeyConnector);
		source.setOffInputConnector(offKeyConnector);
		switchLamp2Action.getSources().add(source);

		final ActorKeySink lamp2KeySink = new ActorKeySink();
		lamp2KeySink.setOutputDevice(lamp2);
		switchLamp2Action.getSinks().add(lamp2KeySink);

		plan.getActions().add((Action<Event>) (Action<?>) switchLamp2Action);
		return plan;
	}
}
