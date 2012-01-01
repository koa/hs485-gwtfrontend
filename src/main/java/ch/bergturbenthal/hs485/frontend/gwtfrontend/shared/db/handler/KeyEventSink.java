package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.eleveneye.hs485.api.data.KeyMessage;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface KeyEventSink extends EventSink, IsSerializable {
	void handleKeyEvent(KeyMessage keyMessage);
}
