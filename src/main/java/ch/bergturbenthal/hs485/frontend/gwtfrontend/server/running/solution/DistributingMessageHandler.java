package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.util.ArrayList;

import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyMessage;

public class DistributingMessageHandler implements MessageHandler {
	private final ArrayList<MessageHandler>	messageHandlers	= new ArrayList<MessageHandler>();

	public void appendHandler(final MessageHandler handler) {
		messageHandlers.add(handler);
		messageHandlers.trimToSize();
	}

	@Override
	public void handleMessage(final KeyMessage keyMessage) {
		for (final MessageHandler handler : messageHandlers)
			handler.handleMessage(keyMessage);
	}
}