package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface EventSource {
	Class<? extends Event> getSendingEventType();
}
