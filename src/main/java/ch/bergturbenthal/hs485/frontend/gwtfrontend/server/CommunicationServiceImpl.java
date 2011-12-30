package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.InputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent.EventType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.eleveneye.hs485.api.BroadcastHandler;
import ch.eleveneye.hs485.device.Dimmer;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.SwitchingActor;
import ch.eleveneye.hs485.device.TFSensor;
import ch.eleveneye.hs485.device.TimedActor;
import ch.eleveneye.hs485.device.physically.Actor;
import ch.eleveneye.hs485.device.physically.PhysicallyDevice;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;
import ch.eleveneye.hs485.device.utils.AbstractDevice;
import ch.eleveneye.hs485.protocol.IMessage;
import ch.eleveneye.hs485.protocol.IMessage.KeyEventType;

public class CommunicationServiceImpl extends AutowiringRemoteServiceServlet implements CommunicationService {
	private static class OutputData {
		private Actor							actor;
		private OutputDescription	outputDescription;
	}

	private static final long																			serialVersionUID	= 8948548851433479912L;
	@Autowired
	private Registry																							hs485registry;
	private final Collection<WeakReference<BlockingQueue<Event>>>	listeningQueues		= new ArrayList<WeakReference<BlockingQueue<Event>>>();
	private final Logger																					logger						= LoggerFactory.getLogger(CommunicationServiceImpl.class);
	private Map<OutputAddress, OutputData>												outputTable				= null;

	@Override
	public Collection<Event> getEvents() {
		try {
			final BlockingQueue<Event> queue = getQueue();
			try {
				final Event event = queue.poll(10, TimeUnit.SECONDS);
				if (event == null)
					return Collections.emptyList();
				final ArrayList<Event> ret = new ArrayList<Event>();
				ret.add(event);
				queue.drainTo(ret);
				return ret;
			} catch (final InterruptedException e) {
				return Collections.emptyList();
			}
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	@Override
	public Boolean getOutputSwitchState(final OutputAddress device) {
		final OutputData outputData = loadOutputTable().get(device);
		if (outputData == null)
			return null;
		try {
			return ((SwitchingActor) outputData.actor).isOn();
		} catch (final Exception e) {
			logger.warn("Error reading actor state", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		hs485registry.getBus().addBroadcastHandler(new BroadcastHandler() {

			@Override
			public void handleBroadcastMessage(final IMessage message) {
				final KeyEvent event = deceodeKeyMessage(message);
				if (event != null)
					distributeEvent(event);
			}
		});
		try {
			final Collection<PhysicallyDevice> physicalDevices = hs485registry.listPhysicalDevices();
			for (final PhysicallyDevice physicallyDevice : physicalDevices)
				if (physicallyDevice instanceof AbstractDevice) {
					logger.info("Device : " + Integer.toHexString(physicallyDevice.getAddress()));
					final AbstractDevice abstractDevice = (AbstractDevice) physicallyDevice;
					abstractDevice.dumpVariables();
				}
		} catch (final IOException e) {
			logger.warn("Cannot read devices ", e);
		}

	}

	@Override
	public Map<InputAddress, InputDescription> listInputDevices() {
		try {
			final HashMap<InputAddress, InputDescription> ret = new HashMap<InputAddress, InputDescription>();
			for (final PhysicallySensor sensor : hs485registry.listPhysicallySensors()) {
				final InputDescription inputDescription = new InputDescription();
				inputDescription.setKeySensor(sensor instanceof KeySensor);
				final boolean isTfs = sensor instanceof TFSensor;
				inputDescription.setHumiditySensor(isTfs);
				inputDescription.setTemperatureSensor(isTfs);
				ret.put(new InputAddress(sensor.getModuleAddr(), sensor.getSensorNr()), inputDescription);
			}
			return ret;
		} catch (final Exception e) {
			logger.warn("Error listing sensors", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Map<OutputAddress, OutputDescription> listOutputDevices() {
		final Map<OutputAddress, OutputDescription> descriptions = new TreeMap<OutputAddress, OutputDescription>();
		for (final Entry<OutputAddress, OutputData> outputEntry : loadOutputTable().entrySet())
			descriptions.put(outputEntry.getKey(), outputEntry.getValue().outputDescription);
		return descriptions;
	}

	@Override
	public float readHmuidity(final InputAddress address) {
		try {
			final PhysicallySensor sensor = hs485registry.getPhysicallySensor(address.getDeviceAddress(), address.getInputAddress());
			if (sensor instanceof TFSensor) {
				final TFSensor tfsSensor = (TFSensor) sensor;
				return tfsSensor.readTF().getHumidity();
			}
			return -100;
		} catch (final IOException e) {
			logger.warn("Error reading humidity", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public float readTemperature(final InputAddress address) {
		try {
			final PhysicallySensor sensor = hs485registry.getPhysicallySensor(address.getDeviceAddress(), address.getInputAddress());
			if (sensor instanceof TFSensor) {
				final TFSensor tfsSensor = (TFSensor) sensor;
				return (float) tfsSensor.readTF().readTemperatur();
			}
			return -100;
		} catch (final IOException e) {
			logger.warn("Error reading temperature", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setOutputSwitchState(final OutputAddress device, final boolean state) {
		final OutputData outputData = loadOutputTable().get(device);
		if (outputData == null)
			return;
		try {
			final SwitchingActor switchingActor = (SwitchingActor) outputData.actor;
			if (state)
				switchingActor.setOn();
			else
				switchingActor.setOff();
		} catch (final Exception e) {
			logger.warn("Error setting actor state", e);
			throw new RuntimeException(e);
		}
	}

	private KeyEvent deceodeKeyMessage(final IMessage message) {
		logger.info("Message: " + message);
		final byte[] data = message.getData();
		if (data.length != 4)
			return null;
		if (data[0] != 'K')
			return null;
		final KeyEvent event = new KeyEvent();
		final KeyEventType eventType = message.readKeyEventType();
		switch (eventType) {
		case PRESS:
			event.setType(EventType.DOWN);
			break;
		case HOLD:
			event.setType(EventType.HOLD);
			break;
		case RELEASE:
			event.setType(EventType.UP);
			break;
		}
		event.setKeyAddress(new InputAddress(message.getSourceAddress(), data[1]));
		logger.info("Event: " + event);
		return event;
	}

	private void distributeEvent(final Event event) {
		for (final Iterator<WeakReference<BlockingQueue<Event>>> iterator = listeningQueues.iterator(); iterator.hasNext();) {
			final WeakReference<BlockingQueue<Event>> reference = iterator.next();
			final BlockingQueue<Event> blockingQueue = reference.get();
			if (blockingQueue == null) {
				iterator.remove();
				continue;
			}
			blockingQueue.add(event);
		}

	}

	private BlockingQueue<Event> getQueue() {
		final HttpServletRequest request = getThreadLocalRequest();
		final HttpSession session = request.getSession();
		session.setMaxInactiveInterval(120);
		final BlockingQueue<Event> foundQueue = (BlockingQueue<Event>) session.getAttribute("queue");
		if (foundQueue != null)
			return foundQueue;
		final LinkedBlockingQueue<Event> newQueue = new LinkedBlockingQueue<Event>();
		session.setAttribute("queue", newQueue);
		listeningQueues.add(new WeakReference<BlockingQueue<Event>>(newQueue));
		return newQueue;
	}

	private Map<OutputAddress, OutputData> loadOutputTable() {
		if (outputTable != null)
			return outputTable;
		synchronized (this) {
			if (outputTable != null)
				return outputTable;
			outputTable = new HashMap<OutputAddress, CommunicationServiceImpl.OutputData>();
			try {
				for (final Actor actor : hs485registry.listPhysicallyActors()) {
					final OutputDescription description = new OutputDescription();
					description.setHasSwitch(Boolean.valueOf(actor instanceof SwitchingActor));
					description.setHasTimer(Boolean.valueOf(actor instanceof TimedActor));
					description.setIsDimmer(Boolean.valueOf(actor instanceof Dimmer));
					final OutputAddress address = new OutputAddress(actor.getModuleAddr(), actor.getActorNr());
					final OutputData outputData = new OutputData();
					outputData.actor = actor;
					outputData.outputDescription = description;
					outputTable.put(address, outputData);
				}
				return outputTable;
			} catch (final Exception e) {
				outputTable = null;
				logger.warn("Error listing output-table", e);
				throw new RuntimeException(e);
			}
		}
	}

}
