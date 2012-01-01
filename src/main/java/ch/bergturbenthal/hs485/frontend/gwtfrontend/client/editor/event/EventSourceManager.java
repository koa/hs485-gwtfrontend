package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface EventSourceManager<E extends Event, T extends EventSource<E>> {
	EventSourceConfigPanel<E, T> buildPanel();

	String describeSource(T eventSource);

	void fixReferences(T source, Map<String, InputConnector> inputConnectors, Map<String, OutputDevice> outputDevices);

	Class<T> getConfigureSourceType();

	Class<E> getEventType();

	String getName();

	Collection<InputConnector> listInputConnectorsForSource(T eventSource);

	T makeNewEventSource();
}
