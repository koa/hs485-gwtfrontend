package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface EventSource<T extends Event> extends IsSerializable {
}
