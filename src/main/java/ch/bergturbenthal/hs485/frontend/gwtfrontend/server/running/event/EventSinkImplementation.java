package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface EventSinkImplementation<E extends Event, T extends EventSink<E>> extends EventHandler<E> {
	void applyConfig(T config) throws IOException;
}
