package ch.bergturbenthal.hs485.frontend.gwtfrontend.server;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationService;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent.EventType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.eleveneye.hs485.api.BroadcastHandler;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.protocol.IMessage;
import ch.eleveneye.hs485.protocol.IMessage.KeyEventType;

public class CommunicationServiceImpl extends AutowiringRemoteServiceServlet implements CommunicationService {
	private static final long																			serialVersionUID	= 8948548851433479912L;
	private Registry																							hs485registry;
	private final Collection<WeakReference<BlockingQueue<Event>>>	listeningQueues		= new ArrayList<WeakReference<BlockingQueue<Event>>>();

	private KeyEvent deceodeKeyMessage(final IMessage message) {
		final byte[] data = message.getData();
		if (data.length != 4)
			return null;
		if (data[0] != 'K')
			return null;
		final KeyEvent event = new KeyEvent();
		final KeyEventType eventType = message.readKeyEventType();
		switch (eventType) {
		case PRESS:
			event.setType(EventType.DOWN);
			break;
		case HOLD:
			event.setType(EventType.HOLD);
			break;
		case RELEASE:
			event.setType(EventType.UP);
			break;
		}
		event.setKeyAddress(new InputAddress(message.getSourceAddress(), data[1]));
		return event;
	}

	private void distributeEvent(final Event event) {
		for (final Iterator<WeakReference<BlockingQueue<Event>>> iterator = listeningQueues.iterator(); iterator.hasNext();) {
			final WeakReference<BlockingQueue<Event>> reference = iterator.next();
			final BlockingQueue<Event> blockingQueue = reference.get();
			if (blockingQueue == null) {
				iterator.remove();
				continue;
			}
			blockingQueue.add(event);
		}

	}

	@Override
	public Collection<Event> getEvents() {
		try {
			final BlockingQueue<Event> queue = getQueue();
			try {
				final Event event = queue.poll(10, TimeUnit.SECONDS);
				if (event == null)
					return Collections.emptyList();
				final ArrayList<Event> ret = new ArrayList<Event>();
				ret.add(event);
				queue.drainTo(ret);
				return ret;
			} catch (final InterruptedException e) {
				return Collections.emptyList();
			}
		} catch (final Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		}
	}

	private BlockingQueue<Event> getQueue() {
		final HttpServletRequest request = getThreadLocalRequest();
		final HttpSession session = request.getSession();
		session.setMaxInactiveInterval(120);
		final BlockingQueue<Event> foundQueue = (BlockingQueue<Event>) session.getAttribute("queue");
		if (foundQueue != null)
			return foundQueue;
		final LinkedBlockingQueue<Event> newQueue = new LinkedBlockingQueue<Event>();
		session.setAttribute("queue", newQueue);
		listeningQueues.add(new WeakReference<BlockingQueue<Event>>(newQueue));
		return newQueue;
	}

	@Override
	public void init() throws ServletException {
		super.init();
		final ApplicationContext ctx = SpringUtil.getSpringContext();
		hs485registry = ctx.getBean("hs485registry", Registry.class);
		hs485registry.getBus().addBroadcastHandler(new BroadcastHandler() {

			@Override
			public void handleBroadcastMessage(final IMessage message) {
				final KeyEvent event = deceodeKeyMessage(message);
				if (event != null)
					distributeEvent(event);
			}
		});

	}

}
