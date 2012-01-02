package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface EventHandler<E extends Event> {
	void takeEvent(E event);
}
