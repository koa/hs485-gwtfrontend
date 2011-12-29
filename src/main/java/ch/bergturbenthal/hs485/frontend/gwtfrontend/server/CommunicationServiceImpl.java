package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import ch.eleveneye.hs485.device.physically.PhysicallySensor;
import ch.eleveneye.hs485.protocol.IMessage;
import ch.eleveneye.hs485.protocol.IMessage.KeyEventType;

public class CommunicationServiceImpl extends AutowiringRemoteServiceServlet implements CommunicationService {
	private static final long																			serialVersionUID	= 8948548851433479912L;
	@Autowired
	private Registry																							hs485registry;
	private final Collection<WeakReference<BlockingQueue<Event>>>	listeningQueues		= new ArrayList<WeakReference<BlockingQueue<Event>>>();
	private final Logger																					logger						= LoggerFactory.getLogger(CommunicationServiceImpl.class);

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
		try {
			final Map<OutputAddress, OutputDescription> descriptions = new TreeMap<OutputAddress, OutputDescription>();
			for (final Actor actor : hs485registry.listPhysicallyActors()) {
				final OutputDescription description = new OutputDescription();
				description.setHasSwitch(Boolean.valueOf(actor instanceof SwitchingActor));
				description.setHasTimer(Boolean.valueOf(actor instanceof TimedActor));
				description.setIsDimmer(Boolean.valueOf(actor instanceof Dimmer));
				descriptions.put(new OutputAddress(actor.getModuleAddr(), actor.getActorNr()), description);
			}
			return descriptions;
		} catch (final Exception e) {
			logger.warn("Error listing actors", e);
			throw new RuntimeException(e);
		}
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

}
