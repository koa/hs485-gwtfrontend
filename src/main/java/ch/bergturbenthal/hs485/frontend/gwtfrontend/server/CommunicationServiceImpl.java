package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.InputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.EventType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;
import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyMessage;
import ch.eleveneye.hs485.device.Dimmer;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.SwitchingActor;
import ch.eleveneye.hs485.device.TFSensor;
import ch.eleveneye.hs485.device.TimedActor;
import ch.eleveneye.hs485.device.config.ConfigurableInputDescription;
import ch.eleveneye.hs485.device.config.ConfigurableOutputDescription;
import ch.eleveneye.hs485.device.physically.Actor;
import ch.eleveneye.hs485.device.physically.PhysicallyDevice;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;

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
	private MessageHandler																				broadcastHandler;

	@Override
	public void destroy() {
		hs485registry.getBus().removeBroadcastHandler(broadcastHandler);
		super.destroy();
	}

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
		broadcastHandler = new MessageHandler() {

			@Override
			public void handleMessage(final KeyMessage keyMessage) {
				final KeyEvent event = decodeKeyMessage(keyMessage);
				if (event != null)
					distributeEvent(event);

			}
		};
		hs485registry.getBus().addBroadcastHandler(broadcastHandler);
		// try {
		// final Collection<PhysicallyDevice> physicalDevices =
		// hs485registry.listPhysicalDevices();
		// for (final PhysicallyDevice physicallyDevice : physicalDevices)
		// if (physicallyDevice instanceof AbstractDevice) {
		// logger.info("Device : " +
		// Integer.toHexString(physicallyDevice.getAddress()));
		// final AbstractDevice abstractDevice = (AbstractDevice)
		// physicallyDevice;
		// abstractDevice.dumpVariables();
		// }
		// } catch (final IOException e) {
		// logger.warn("Cannot read devices ", e);
		// }
	}

	@Override
	public Map<InputAddress, InputDescription> listInputDevices() {
		try {
			final HashMap<InputAddress, InputDescription> ret = new HashMap<InputAddress, InputDescription>();
			for (final PhysicallyDevice device : hs485registry.listPhysicalDevices())
				for (final ConfigurableInputDescription input : device.listConfigurableInputs()) {
					final InputDescription inputDescription = new InputDescription();
					inputDescription.setKeySensor(KeySensor.class.isAssignableFrom(input.getImplementionSensor()));
					final boolean isTfs = TFSensor.class.isAssignableFrom(input.getImplementionSensor());
					inputDescription.setHumiditySensor(isTfs);
					inputDescription.setTemperatureSensor(isTfs);
					inputDescription.setConnectionLabel(input.getLabeledName());
					ret.put(new InputAddress(device.getAddress(), input.getSensorNr()), inputDescription);
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

	private KeyEvent decodeKeyMessage(final KeyMessage keyMessage) {
		logger.info("Message: " + keyMessage);
		final KeyEvent event = new KeyEvent();
		switch (keyMessage.getKeyEventType()) {
		case PRESS:
			event.setEventType(EventType.DOWN);
			break;
		case HOLD:
			event.setEventType(EventType.HOLD);
			break;
		case RELEASE:
			event.setEventType(EventType.UP);
			break;
		}
		switch (keyMessage.getKeyType()) {
		case UP:
			event.setKeyType(KeyType.ON);
			break;
		case DOWN:
			event.setKeyType(KeyType.OFF);
			break;
		case TOGGLE:
			event.setKeyType(KeyType.TOGGLE);
			break;
		}
		event.setKeyAddress(new InputAddress(keyMessage.getSourceAddress(), keyMessage.getSourceSensor()));
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
				for (final PhysicallyDevice device : hs485registry.listPhysicalDevices()) {
					final List<ConfigurableOutputDescription> listConfigurableOutputs = device.listConfigurableOutputs();
					for (final ConfigurableOutputDescription output : listConfigurableOutputs) {
						final OutputDescription description = new OutputDescription();
						description.setHasSwitch(Boolean.valueOf(SwitchingActor.class.isAssignableFrom(output.getImplementingActor())));
						description.setHasTimer(Boolean.valueOf(TimedActor.class.isAssignableFrom(output.getImplementingActor())));
						description.setIsDimmer(Boolean.valueOf(Dimmer.class.isAssignableFrom(output.getImplementingActor())));
						description.setConnectionLabel(output.getLabeledName());
						final OutputAddress address = new OutputAddress(device.getAddress(), output.getActorNr());
						final OutputData outputData = new OutputData();
						outputData.actor = hs485registry.getActor(device.getAddress(), output.getActorNr());
						outputData.outputDescription = description;
						outputTable.put(address, outputData);
					}
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
