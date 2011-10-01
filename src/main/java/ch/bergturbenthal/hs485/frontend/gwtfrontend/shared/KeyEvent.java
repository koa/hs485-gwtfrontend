package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class KeyEvent implements Event, IsSerializable {
	public static enum EventType {
		DOWN, HOLD, UP
	}

	private static final long	serialVersionUID	= 1184146590744072872L;

	private KeyAddress				keyAddress;
	private EventType					type;

	public KeyAddress getKeyAddress() {
		return keyAddress;
	}

	public EventType getType() {
		return type;
	}

	public void setKeyAddress(final KeyAddress keyAddress) {
		this.keyAddress = keyAddress;
	}

	public void setType(final EventType type) {
		this.type = type;
	}

}
