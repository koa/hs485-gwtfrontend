package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;

import com.google.gwt.user.client.rpc.IsSerializable;

public class KeyEvent implements Event, IsSerializable {
	public static enum EventType {
		DOWN, HOLD, UP
	}

	public static enum KeyType {
		OFF, ON, TOGGLE
	}

	private static final long	serialVersionUID	= 1184146590744072872L;

	private EventType					eventType;

	private InputAddress			keyAddress;
	private KeyType						keyType;

	public EventType getEventType() {
		return eventType;
	}

	public InputAddress getKeyAddress() {
		return keyAddress;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setEventType(final EventType type) {
		eventType = type;
	}

	public void setKeyAddress(final InputAddress keyAddress) {
		this.keyAddress = keyAddress;
	}

	public void setKeyType(final KeyType keyType) {
		this.keyType = keyType;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("KeyEvent [");
		if (keyAddress != null) {
			builder.append("keyAddress=");
			builder.append(keyAddress);
			builder.append(", ");
		}
		if (eventType != null) {
			builder.append("type=");
			builder.append(eventType);
		}
		builder.append("]");
		return builder.toString();
	}

}
