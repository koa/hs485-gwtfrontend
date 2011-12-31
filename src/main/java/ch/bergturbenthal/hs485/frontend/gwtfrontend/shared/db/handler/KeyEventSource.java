package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

public class KeyEventSource implements EventSource {

	@Override
	public Class<? extends Event> getSendingEventType() {
		return KeyEvent.class;
	}
}
