package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

public class EventHandler {
	/**
	 * Takes every Event
	 * 
	 * @param event
	 */
	public void handleEvent(final Event event) {
		if (event instanceof KeyEvent) {
			final KeyEvent keyEvent = (KeyEvent) event;
			handleKeyEvent(keyEvent);
		}
	}

	/**
	 * Takes all Key events
	 * 
	 * @param keyEvent
	 */
	public void handleKeyEvent(final KeyEvent keyEvent) {
	}
}
