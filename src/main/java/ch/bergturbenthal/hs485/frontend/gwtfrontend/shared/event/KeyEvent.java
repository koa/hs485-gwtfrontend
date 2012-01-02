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

	private EventType			eventType;
	private InputAddress	keyAddress;
	private KeyType				keyType;
	private int						hitCount;

	public EventType getEventType() {
		return eventType;
	}

	public int getHitCount() {
		return hitCount;
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

	public void setHitCount(final int hitCount) {
		this.hitCount = hitCount;
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
		if (eventType != null) {
			builder.append("eventType=");
			builder.append(eventType);
			builder.append(", ");
		}
		if (keyAddress != null) {
			builder.append("keyAddress=");
			builder.append(keyAddress);
			builder.append(", ");
		}
		if (keyType != null) {
			builder.append("keyType=");
			builder.append(keyType);
			builder.append(", ");
		}
		builder.append("hitCount=");
		builder.append(hitCount);
		builder.append("]");
		return builder.toString();
	}

}
