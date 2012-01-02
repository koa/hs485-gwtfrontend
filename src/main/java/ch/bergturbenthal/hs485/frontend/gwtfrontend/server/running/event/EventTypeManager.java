package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.eleveneye.hs485.device.Registry;

public class EventTypeManager {
	private final class KeyActionManagerImplementation implements ActionManager<KeyEvent> {
		private final Map<Class<? extends EventSource<KeyEvent>>, Factory<? extends EventSourceImplementation<KeyEvent, ? extends EventSource<KeyEvent>>>>	sourceImpl	= new HashMap<Class<? extends EventSource<KeyEvent>>, Factory<? extends EventSourceImplementation<KeyEvent, ? extends EventSource<KeyEvent>>>>();
		private final Map<Class<? extends EventSink<KeyEvent>>, Factory<? extends EventSinkImplementation<KeyEvent, ? extends EventSink<KeyEvent>>>>				sinkImpl		= new HashMap<Class<? extends EventSink<KeyEvent>>, Factory<? extends EventSinkImplementation<KeyEvent, ? extends EventSink<KeyEvent>>>>();

		public KeyActionManagerImplementation() {
			sourceImpl.put(ToggleKeyEventSource.class, new ToggleKeySourceImplementation.ToggleKeySourceImplementationFactory());
			sourceImpl.put(KeyPairEventSource.class, new KeyPairSourceImplementation.KeyPairSourceImplementationFactory());
			sinkImpl.put(ActorKeySink.class, new ActorKeySinkImplementation.ActorKeySinkFactory());
			for (final Factory<? extends EventSourceImplementation<KeyEvent, ? extends EventSource<KeyEvent>>> factory : sourceImpl.values())
				factory.setRegistry(hs485Registry);
			for (final Factory<? extends EventSinkImplementation<KeyEvent, ? extends EventSink<KeyEvent>>> factory : sinkImpl.values())
				factory.setRegistry(hs485Registry);
		}

		@Override
		public void configAction(final Action<KeyEvent> action) {
			final Collection<EventHandler<KeyEvent>> handlers = new ArrayList<EventHandler<KeyEvent>>();
			for (final EventSink<KeyEvent> sink : action.getSinks()) {
				final EventSinkImplementation<KeyEvent, EventSink<KeyEvent>> sinkHandler = (EventSinkImplementation<KeyEvent, EventSink<KeyEvent>>) sinkImpl
						.get(sink.getClass()).makeObject();
				try {
					sinkHandler.applyConfig(sink);
					handlers.add(sinkHandler);
				} catch (final IOException e) {
					logger.error("Cannot apply sink " + sink, e);
				}
			}
			if (handlers.size() == 0)
				return;
			for (final EventSource<KeyEvent> source : action.getSources()) {
				final EventSourceImplementation<KeyEvent, EventSource<KeyEvent>> sourceHandler = (EventSourceImplementation<KeyEvent, EventSource<KeyEvent>>) sourceImpl
						.get(source.getClass()).makeObject();
				try {
					sourceHandler.applyConfig(source);
					for (final EventHandler<KeyEvent> eventHandler : handlers)
						sourceHandler.appendEventHandler(eventHandler);
				} catch (final IOException e) {
					logger.error("Cannot apply source " + source, e);
				}
			}

		}
	}

	private static final Logger																logger					= LoggerFactory.getLogger(EventTypeManager.class);

	private final Map<String, ActionManager<? extends Event>>	actionManagers	= new HashMap<String, ActionManager<? extends Event>>();
	private final Registry																		hs485Registry;

	public EventTypeManager(final Registry hs485Registry) {
		this.hs485Registry = hs485Registry;
		actionManagers.put(KeyEvent.class.getName(), new KeyActionManagerImplementation());
	}

	public void applyAction(final Action action) {
		final ActionManager<? extends Event> actionManager = actionManagers.get(action.getEventType());
		if (actionManager == null) {
			logger.error("No Manager found for " + action.getEventType());
			return;
		}
		actionManager.configAction(action);
	}
}
