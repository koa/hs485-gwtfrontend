package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.eleveneye.hs485.api.data.KeyMessage;

public interface KeyEventSink extends EventSink {
	void handleKeyEvent(KeyMessage keyMessage);
}
