package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public class EventTypeManager {

	private final Collection<EventSourcePanelBuilder<?, ?>>	inputPanelBuilders	= new ArrayList<EventSourcePanelBuilder<?, ?>>();
	private final Map<Class, EventSourcePanelBuilder>				builderIndex				= new HashMap<Class, EventSourcePanelBuilder>();

	@SuppressWarnings("unchecked")
	public EventTypeManager(final LabelGenerator labelGenerator) {
		inputPanelBuilders.add(new ToggleInputSourceComposite.PanelBuilder(labelGenerator));
		inputPanelBuilders.add(new KeyPairInputSourceComposite.PanelBuilder(labelGenerator));
		for (final EventSourcePanelBuilder<?, ?> builder : inputPanelBuilders)
			builderIndex.put(builder.getConfigureSourceType(), builder);
	}

	public String describeEventSource(final EventSource source) {
		return getBuilderFor(source.getClass()).describeSource(source);
	}

	public void fixReferences(final EventSource<?> source, final Map<String, InputConnector> inputConnectors,
			final Map<String, OutputDevice> outputDevices) {
		getBuilderFor(source.getClass()).fixReferences(source, inputConnectors, outputDevices);
	}

	@SuppressWarnings("unchecked")
	public <E extends Event, T extends EventSource<E>> EventSourcePanelBuilder<E, T> getBuilderFor(final Class<T> type) {
		return builderIndex.get(type);
	}

	public Collection<InputConnector> inputConnectorsOf(final EventSource<?> source) {
		return getBuilderFor(source.getClass()).listInputConnectorsForSource(source);
	}

	public Collection<String> listAvailableEvents() {
		final Collection<String> ret = new HashSet<String>();
		for (final EventSourcePanelBuilder<?, ?> builder : inputPanelBuilders)
			ret.add(builder.getEventType().getName());
		return ret;
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> Collection<EventSourcePanelBuilder<T, EventSource<T>>> listInputPanelsForEvent(final String className) {
		final Collection<EventSourcePanelBuilder<T, EventSource<T>>> ret = new ArrayList<EventSourcePanelBuilder<T, EventSource<T>>>();
		for (final EventSourcePanelBuilder<?, ?> builder : inputPanelBuilders)
			if (builder.getEventType().getName().equals(className))
				ret.add((EventSourcePanelBuilder<T, EventSource<T>>) builder);
		return ret;
	}
}
