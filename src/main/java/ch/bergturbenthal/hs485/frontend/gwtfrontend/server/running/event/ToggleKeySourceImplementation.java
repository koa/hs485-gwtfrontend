package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;
import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyMessage;
import ch.eleveneye.hs485.device.Registry;

public class ToggleKeySourceImplementation extends AbstractKeySourceImplementation<ToggleKeyEventSource> {
	public static class ToggleKeySourceImplementationFactory implements Factory<ToggleKeySourceImplementation> {

		private Registry	registry;

		@Override
		public ToggleKeySourceImplementation makeObject() {
			return new ToggleKeySourceImplementation(registry);
		}

		@Override
		public void setRegistry(final Registry registry) {
			this.registry = registry;
		}

	}

	public ToggleKeySourceImplementation(final Registry hs485Registry) {
		super(hs485Registry);
	}

	@Override
	public void applyConfig(final ToggleKeyEventSource config) throws IOException {
		final InputConnector inputConnector = config.getInputConnector();
		if (inputConnector != null)
			registerHandler(inputConnector.getAddress(), new MessageHandler() {

				@Override
				public void handleMessage(final KeyMessage keyMessage) {
					final KeyEvent event = makeKeyEvent(KeyType.TOGGLE, keyMessage);
					if (event == null)
						return;
					sendEvent(event);
				}
			});
	}

}
