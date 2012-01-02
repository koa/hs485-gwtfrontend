package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.EventType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;
import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyMessage;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;

public abstract class AbstractKeySourceImplementation<T extends EventSource<KeyEvent>> implements EventSourceImplementation<KeyEvent, T> {

	private static final Logger												logger					= LoggerFactory.getLogger(AbstractKeySourceImplementation.class);
	private final Registry														hs485Registry;
	private final Collection<EventHandler<KeyEvent>>	targetHandlers	= new ArrayList<EventHandler<KeyEvent>>();

	public AbstractKeySourceImplementation(final Registry hs485Registry) {
		this.hs485Registry = hs485Registry;
	}

	@Override
	public void appendEventHandler(final EventHandler<KeyEvent> handler) {
		targetHandlers.add(handler);
	}

	protected KeyEvent makeKeyEvent(final KeyType keyType, final KeyMessage keyMessage) {
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
		default:
			return null;
		}
		event.setHitCount(keyMessage.getHitCount());
		event.setKeyType(keyType);
		return event;
	}

	protected void registerHandler(final InputAddress address, final MessageHandler handler) throws IOException {
		if (address == null)
			return;
		if (address.getDeviceAddress() == 0)
			return;
		final PhysicallySensor sensor = hs485Registry.getPhysicallySensor(address.getDeviceAddress(), address.getInputAddress());
		if (sensor instanceof KeySensor) {
			final KeySensor keySensor = (KeySensor) sensor;
			keySensor.registerHandler(new MessageHandler() {

				@Override
				public void handleMessage(final KeyMessage keyMessage) {
					logger.info("Incoming Message " + keyMessage + " from " + sensor);
					handler.handleMessage(keyMessage);
				}
			});
		} else
			logger.error("Sensor " + sensor + " is not a KeySensor");
	}

	protected void sendEvent(final KeyEvent event) {
		for (final EventHandler<KeyEvent> targetHandler : targetHandlers)
			targetHandler.takeEvent(event);
	}

}