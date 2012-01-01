package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

public class PanelBuilder {
	private final Collection<EventSourcePanelBuilder<?, ?>>	inputPanelBuilders	= new ArrayList<EventSourcePanelBuilder<?, ?>>();
	private final Map<Class, EventSourcePanelBuilder>				builderIndex				= new HashMap<Class, EventSourcePanelBuilder>();

	@SuppressWarnings("unchecked")
	public PanelBuilder(final LabelGenerator labelGenerator) {
		inputPanelBuilders.add(new EventSourcePanelBuilder<KeyEvent, ToggleKeyEventSource>() {

			@Override
			public EventSourceConfigPanel<KeyEvent, ToggleKeyEventSource> buildPanel() {
				final ToggleInputSourceComposite ret = new ToggleInputSourceComposite();
				ret.setLabelGenerator(labelGenerator);
				return ret;
			}

			@Override
			public String describeSource(final ToggleKeyEventSource eventSource) {
				final InputConnector inputConnector = eventSource.getInputConnector();
				if (inputConnector == null)
					return "<unset>";
				return labelGenerator.makeLabelForInputConnector(inputConnector);
			}

			@Override
			public Class<ToggleKeyEventSource> getConfigureSourceType() {
				return ToggleKeyEventSource.class;
			}

			@Override
			public Class<KeyEvent> getEventType() {
				return KeyEvent.class;
			}

			@Override
			public String getName() {
				return "Toggle Key";
			}

			@Override
			public Collection<InputConnector> listInputConnectorsForSource(final ToggleKeyEventSource source) {
				final ArrayList<InputConnector> ret = new ArrayList<InputConnector>();
				final InputConnector inputConnector = source.getInputConnector();
				if (inputConnector == null)
					ret.add(inputConnector);
				return ret;
			}

			@Override
			public ToggleKeyEventSource makeNewEventSource() {
				return new ToggleKeyEventSource();
			}

		});
		for (final EventSourcePanelBuilder<?, ?> builder : inputPanelBuilders)
			builderIndex.put(builder.getConfigureSourceType(), builder);
	}

	public String describeEventSource(final EventSource source) {
		return getBuilderFor(source.getClass()).describeSource(source);
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
