package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Event;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EventDistributor {
	private static final class PollAsyncCallback implements AsyncCallback<Collection<Event>> {
		// private final HTML logHtml;

		private PollAsyncCallback() {
		}

		@Override
		public void onFailure(final Throwable caught) {
			GWT.log("Error while Polling Server", caught);
			new Timer() {
				@Override
				public void run() {
					communicationService.getEvents(PollAsyncCallback.this);
				}
			}.schedule(5000);
		}

		@Override
		public void onSuccess(final Collection<Event> result) {
			if (result.size() > 0)
				for (final Event event : result)
					synchronized (handlers) {
						for (final WeakReference<EventHandler> handlerReference : handlers) {
							final EventHandler handler = handlerReference.get();
							if (handler == null)
								removeHandler(handler);
							else
								handler.handleEvent(event);
						}
					}
			if (communicationRunning)
				new Timer() {
					@Override
					public void run() {
						communicationService.getEvents(PollAsyncCallback.this);
					}
				}.schedule(500);

		}
	}

	private static boolean																	communicationRunning	= false;
	private static final CommunicationServiceAsync					communicationService	= CommunicationServiceAsync.Util.getInstance();
	private static LinkedList<WeakReference<EventHandler>>	handlers							= new LinkedList<WeakReference<EventHandler>>();

	public static void registerHandler(final EventHandler handler) {
		synchronized (handlers) {
			handlers.add(new WeakReference<EventHandler>(handler));
			if (!communicationRunning) {
				communicationService.getEvents(new PollAsyncCallback());
				communicationRunning = true;
			}
		}
	}

	public static void removeHandler(final EventHandler handler) {
		synchronized (handlers) {
			for (final Iterator<WeakReference<EventHandler>> iterator = handlers.iterator(); iterator.hasNext();) {
				final WeakReference<EventHandler> reference = iterator.next();
				if (reference.get() == null || reference.get() == handler)
					iterator.remove();
			}
			if (handlers.size() == 0)
				communicationRunning = false;
		}
	}
}
