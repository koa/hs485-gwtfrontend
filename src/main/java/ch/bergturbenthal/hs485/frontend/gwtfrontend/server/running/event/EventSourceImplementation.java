package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface EventSourceImplementation<E extends Event, T extends EventSource<E>> {
	void appendEventHandler(EventHandler<E> handler);

	void applyConfig(T config) throws IOException;
}
