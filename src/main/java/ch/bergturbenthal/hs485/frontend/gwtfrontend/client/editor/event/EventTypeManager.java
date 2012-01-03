package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public class EventTypeManager {

	private final Collection<EventSourceManager<?, ?>>																																												inputPanelManagers	= new ArrayList<EventSourceManager<?, ?>>();
	private final Map<Class<? extends EventSourceManager<?, ?>>, EventSourceManager<? extends Event, ? extends EventSource<? extends Event>>>	sourceIndex					= new HashMap<Class<? extends EventSourceManager<?, ?>>, EventSourceManager<? extends Event, ? extends EventSource<? extends Event>>>();
	private final Collection<EventSinkManager<?, ?>>																																													sinkManagers				= new ArrayList<EventSinkManager<?, ?>>();
	private final Map<Class<? extends EventSinkManager<?, ?>>, EventSinkManager<? extends Event, ? extends EventSink<? extends Event>>>				sinkIndex						= new HashMap<Class<? extends EventSinkManager<?, ?>>, EventSinkManager<? extends Event, ? extends EventSink<? extends Event>>>();

	@SuppressWarnings("unchecked")
	public EventTypeManager(final LabelGenerator labelGenerator) {
		inputPanelManagers.add(new ToggleInputSourceComposite.SourceManager(labelGenerator));
		inputPanelManagers.add(new KeyPairInputSourceComposite.SourceManager(labelGenerator));
		inputPanelManagers.add(new PirKeyEventInputSourceComposite.SourceManager(labelGenerator));

		sinkManagers.add(new KeyOutputDeviceSinkComposite.SinkManager(labelGenerator));

		for (final EventSourceManager<?, ?> builder : inputPanelManagers)
			sourceIndex.put((Class<? extends EventSourceManager<?, ?>>) builder.getConfigureSourceType(), builder);
		for (final EventSinkManager<?, ?> manager : sinkManagers)
			sinkIndex.put((Class<? extends EventSinkManager<?, ?>>) manager.getConfigureSinkType(), manager);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String describeEventSink(final EventSink sink) {
		return getSinkManagerFor(sink.getClass()).describeSink(sink);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String describeEventSource(final EventSource source) {
		return getSourceManagerFor(source.getClass()).describeSource(source);
	}

	@SuppressWarnings("unchecked")
	public void fixReferences(final EventSink<?> sink, final Map<String, InputConnector> inputConnectors, final Map<String, OutputDevice> outputDevices) {
		getSinkManagerFor(sink.getClass()).fixReferences(sink, inputConnectors, outputDevices);
	}

	@SuppressWarnings("unchecked")
	public void fixReferences(final EventSource<?> source, final Map<String, InputConnector> inputConnectors,
			final Map<String, OutputDevice> outputDevices) {
		getSourceManagerFor(source.getClass()).fixReferences(source, inputConnectors, outputDevices);
	}

	@SuppressWarnings("unchecked")
	public <E extends Event, T extends EventSink<E>> EventSinkManager<E, T> getSinkManagerFor(final Class<T> sinkClass) {
		return (EventSinkManager<E, T>) sinkIndex.get(sinkClass);
	}

	@SuppressWarnings("unchecked")
	public <E extends Event, T extends EventSource<E>> EventSourceManager<E, T> getSourceManagerFor(final Class<T> type) {
		return (EventSourceManager<E, T>) sourceIndex.get(type);
	}

	@SuppressWarnings("unchecked")
	public Collection<InputConnector> inputConnectorsOf(final EventSource<?> source) {
		return getSourceManagerFor(source.getClass()).listInputConnectorsForSource(source);
	}

	public Collection<String> listAvailableEvents() {
		final Collection<String> ret = new HashSet<String>();
		for (final EventSourceManager<?, ?> builder : inputPanelManagers)
			ret.add(builder.getEventType().getName());
		final HashSet<String> outputTypes = new HashSet<String>();
		for (final EventSinkManager<?, ?> outputManager : sinkManagers)
			outputTypes.add(outputManager.getEventType().getName());
		ret.retainAll(outputTypes);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> Collection<EventSourceManager<T, EventSource<T>>> listInputPanelsForEvent(final String className) {
		final Collection<EventSourceManager<T, EventSource<T>>> ret = new ArrayList<EventSourceManager<T, EventSource<T>>>();
		for (final EventSourceManager<?, ?> builder : inputPanelManagers)
			if (builder.getEventType().getName().equals(className))
				ret.add((EventSourceManager<T, EventSource<T>>) builder);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> Collection<EventSinkManager<T, EventSink<T>>> listOutputPanelsForEvent(final String className) {
		final Collection<EventSinkManager<T, EventSink<T>>> ret = new ArrayList<EventSinkManager<T, EventSink<T>>>();
		for (final EventSinkManager<?, ?> manager : sinkManagers)
			if (manager.getEventType().getName().equals(className))
				ret.add((EventSinkManager<T, EventSink<T>>) manager);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public Collection<OutputDevice> outputDevicesOf(final EventSink<?> sink) {
		return getSinkManagerFor(sink.getClass()).listOutputDevicesForSink(sink);
	}
}
