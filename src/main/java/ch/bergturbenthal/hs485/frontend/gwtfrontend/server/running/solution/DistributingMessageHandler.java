package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.util.ArrayList;

import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyMessage;

public class DistributingMessageHandler implements MessageHandler {
	private final ArrayList<MessageHandler>	messageHandlers	= new ArrayList<MessageHandler>();

	public synchronized void appendHandler(final MessageHandler handler) {
		messageHandlers.add(handler);
		messageHandlers.trimToSize();
	}

	@Override
	public synchronized void handleMessage(final KeyMessage keyMessage) {
		for (final MessageHandler handler : messageHandlers)
			handler.handleMessage(keyMessage);
	}

	public synchronized boolean isEmpty() {
		return messageHandlers.isEmpty();
	}

	public synchronized void removeHandler(final MessageHandler messageHandler) {
		messageHandlers.remove(messageHandler);
		messageHandlers.trimToSize();
	}
}