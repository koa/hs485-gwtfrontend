package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.dummy;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SerializationHelperDummy implements IsSerializable {
	private EventSource<?>	source;
	private EventSink				sink;
	private String					eventType;

	public Class<? extends Event> getEventType() {
		return null;
	}

	public EventSink getSink() {
		return sink;
	}

	public EventSource<?> getSource() {
		return source;
	}

	public void setEventType(final Class<? extends Event> eventType) {
		this.eventType = eventType.getName();
	}

	public void setSink(final EventSink sink) {
		this.sink = sink;
	}

	public void setSource(final EventSource<?> source) {
		this.source = source;
	}
}
