package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public class Action implements Serializable {
	private static final long										serialVersionUID	= 3015587848526088602L;
	private List<EventSource<? extends Event>>	sources						= new ArrayList<EventSource<? extends Event>>();
	private String															eventType;
	private List<EventSink<? extends Event>>		sinks							= new ArrayList<EventSink<? extends Event>>();

	public Action() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Action other = (Action) obj;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		if (sinks == null) {
			if (other.sinks != null)
				return false;
		} else if (!new HashSet(sinks).equals(new HashSet(other.sinks)))
			return false;
		if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!new HashSet(sources).equals(new HashSet(other.sources)))
			return false;
		return true;
	}

	public String getEventType() {
		return eventType;
	}

	public List<EventSink<? extends Event>> getSinks() {
		return sinks;
	}

	public List<EventSource<? extends Event>> getSources() {
		return sources;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (eventType == null ? 0 : eventType.hashCode());
		result = prime * result + (sinks == null ? 0 : new HashSet(sinks).hashCode());
		result = prime * result + (sources == null ? 0 : new HashSet(sources).hashCode());
		return result;
	}

	public void setEventType(final String eventType) {
		this.eventType = eventType;
	}

	public void setSinks(final List<EventSink<? extends Event>> sinks) {
		this.sinks = sinks;
	}

	public void setSources(final List<EventSource<? extends Event>> sources) {
		this.sources = sources;
	}

}
