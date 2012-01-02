package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;
import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyMessage;
import ch.eleveneye.hs485.device.Registry;

public class KeyPairSourceImplementation extends AbstractKeySourceImplementation<KeyPairEventSource> {
	public static class KeyPairSourceImplementationFactory implements Factory<KeyPairSourceImplementation> {

		private Registry	registry;

		@Override
		public KeyPairSourceImplementation makeObject() {
			return new KeyPairSourceImplementation(registry);
		}

		@Override
		public void setRegistry(final Registry registry) {
			this.registry = registry;
		}

	}

	private static final Logger	logger	= LoggerFactory.getLogger(KeyPairSourceImplementation.class);

	public KeyPairSourceImplementation(final Registry hs485Registry) {
		super(hs485Registry);
	}

	@Override
	public void applyConfig(final KeyPairEventSource config) throws IOException {
		final InputConnector onInputConnector = config.getOnInputConnector();
		if (onInputConnector != null)
			registerHandler(onInputConnector.getAddress(), new MessageHandler() {

				@Override
				public void handleMessage(final KeyMessage keyMessage) {
					logger.info("Receiving Message " + keyMessage + " on " + config + " for keyType on");
					final KeyEvent event = makeKeyEvent(KeyType.ON, keyMessage);
					if (event == null)
						return;
					sendEvent(event);
				}
			});
		final InputConnector offInputConnector = config.getOffInputConnector();
		if (offInputConnector != null)
			registerHandler(offInputConnector.getAddress(), new MessageHandler() {

				@Override
				public void handleMessage(final KeyMessage keyMessage) {
					logger.info("Receiving Message " + keyMessage + " on " + config + " for keyType off");
					final KeyEvent event = makeKeyEvent(KeyType.OFF, keyMessage);
					if (event == null)
						return;
					sendEvent(event);
				}
			});
	}

}
