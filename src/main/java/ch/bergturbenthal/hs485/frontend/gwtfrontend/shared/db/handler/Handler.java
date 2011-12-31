package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import java.util.ArrayList;
import java.util.Collection;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Handler implements IsSerializable {
	private Collection<EventSource>	sources	= new ArrayList<EventSource>();
	private Class<? extends Event>	eventType;

	public Class<? extends Event> getEventType() {
		return eventType;
	}

	public Collection<EventSource> getSources() {
		return sources;
	}

	public void setEventType(final Class<? extends Event> eventType) {
		this.eventType = eventType;
	}

	public void setSources(final Collection<EventSource> sources) {
		this.sources = sources;
	}
}
