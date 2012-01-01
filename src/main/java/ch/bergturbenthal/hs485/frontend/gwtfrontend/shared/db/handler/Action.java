package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public class Action implements Serializable {
	private static final long										serialVersionUID	= 3015587848526088602L;
	private List<EventSource<? extends Event>>	sources						= new ArrayList<EventSource<? extends Event>>();
	private String															eventType;
	private List<EventSink>											sinks							= new ArrayList<EventSink>();

	public Action() {
	}

	public String getEventType() {
		return eventType;
	}

	public List<EventSink> getSinks() {
		return sinks;
	}

	public List<EventSource<? extends Event>> getSources() {
		return sources;
	}

	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}

	public void setSinks(final List<EventSink> sinks) {
		this.sinks = sinks;
	}

	public void setSources(final List<EventSource<? extends Event>> sources) {
		this.sources = sources;
	}

}
