package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface EventSinkManager<E extends Event, T extends EventSink<E>> {
	EventSinkConfigPanel<E, T> buildPanel();

	String describeSink(T eventSink);

	void fixReferences(T sink, Map<String, InputConnector> inputConnectors, Map<String, OutputDevice> outputDevices);

	Class<T> getConfigureSinkType();

	Class<E> getEventType();

	String getName();

	Collection<OutputDevice> listOutputDevicesForSink(T eventSink);

	T makeNewEventSink();

}
