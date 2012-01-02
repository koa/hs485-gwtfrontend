package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface ActionManager<E extends Event> {
	void configAction(Action<E> action);
}
