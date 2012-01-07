package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;

public class PrimitiveSwitchingOutputDeviceValueEventSink implements PrimitiveEventSink {
	private OutputAddress	address;
	private float					triggerValue;
	private boolean				onWhenBelow;

	public OutputAddress getAddress() {
		return address;
	}

	public float getTriggerValue() {
		return triggerValue;
	}

	public boolean isOnWhenBelow() {
		return onWhenBelow;
	}

	public void setAddress(final OutputAddress address) {
		this.address = address;
	}

	public void setOnWhenBelow(final boolean onWhenBelow) {
		this.onWhenBelow = onWhenBelow;
	}

	public void setTriggerValue(final float triggerValue) {
		this.triggerValue = triggerValue;
	}
}
