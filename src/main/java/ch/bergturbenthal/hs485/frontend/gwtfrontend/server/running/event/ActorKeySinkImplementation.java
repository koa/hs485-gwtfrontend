package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.eleveneye.hs485.api.data.KeyEventType;
import ch.eleveneye.hs485.api.data.KeyMessage;
import ch.eleveneye.hs485.api.data.KeyType;
import ch.eleveneye.hs485.device.KeyActor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.physically.Actor;

public class ActorKeySinkImplementation implements EventSinkImplementation<KeyEvent, ActorKeySink> {
	public static class ActorKeySinkFactory implements Factory<ActorKeySinkImplementation> {

		private Registry	registry;

		@Override
		public ActorKeySinkImplementation makeObject() {
			return new ActorKeySinkImplementation(registry);
		}

		@Override
		public void setRegistry(final Registry registry) {
			this.registry = registry;
		}

	}

	private final Registry			hs485Registry;
	private KeyActor						keyActor;
	private static final Logger	logger	= LoggerFactory.getLogger(ActorKeySinkImplementation.class);

	public ActorKeySinkImplementation(final Registry hs485Registry) {
		this.hs485Registry = hs485Registry;
	}

	@Override
	public void applyConfig(final ActorKeySink config) throws IOException {
		final OutputDevice outputDevice = config.getOutputDevice();
		if (outputDevice == null)
			return;
		final OutputAddress address = outputDevice.getAddress();
		if (address == null || address.getDeviceAddress() == 0)
			return;
		final Actor actor = hs485Registry.getActor(address.getDeviceAddress(), address.getOutputAddress());
		if (actor instanceof KeyActor)
			keyActor = (KeyActor) actor;
		else
			logger.error("Actor " + actor + " is not a KeyActor");

	}

	@Override
	public void takeEvent(final KeyEvent event) {
		final KeyMessage keyMessage = convertEventToMessage(event);
		if (keyMessage != null && keyActor != null)
			try {
				logger.info("Sending Message " + keyMessage + " to " + keyActor);
				keyActor.sendKeyMessage(keyMessage);
			} catch (final IOException e) {
				logger.warn("Cannot send Key " + keyMessage + "to Receiver");
				throw new RuntimeException("Cannot Send Message " + keyMessage, e);
			}
	}

	private KeyMessage convertEventToMessage(final KeyEvent event) {
		logger.info("Acepting Key:" + event);
		final KeyMessage keyMessage = new KeyMessage();
		switch (event.getEventType()) {
		case DOWN:
			keyMessage.setKeyEventType(KeyEventType.PRESS);
			break;
		case HOLD:
			keyMessage.setKeyEventType(KeyEventType.HOLD);
			break;
		case UP:
			keyMessage.setKeyEventType(KeyEventType.RELEASE);
			break;
		default:
			logger.error("Unknown EventType: " + event.getEventType());
			return null;
		}
		switch (event.getKeyType()) {
		case ON:
			keyMessage.setKeyType(KeyType.UP);
			break;
		case OFF:
			keyMessage.setKeyType(KeyType.DOWN);
			break;
		case TOGGLE:
			keyMessage.setKeyType(KeyType.TOGGLE);
			break;
		default:
			logger.error("Unknown KeyType: " + event.getKeyType());
			return null;
		}
		keyMessage.setHitCount(event.getHitCount());
		return keyMessage;
	}

}
