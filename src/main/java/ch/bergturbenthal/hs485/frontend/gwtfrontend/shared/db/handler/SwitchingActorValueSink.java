package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SwitchingActorValueSink implements EventSink<ValueEvent>, IsSerializable {
	@DBRef
	private OutputDevice	outputDevice;
	private float					triggerLevel;
	private boolean				onBelow;

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	public float getTriggerLevel() {
		return triggerLevel;
	}

	public boolean isOnBelow() {
		return onBelow;
	}

	public void setOnBelow(final boolean onBelow) {
		this.onBelow = onBelow;
	}

	public void setOutputDevice(final OutputDevice outputDevice) {
		this.outputDevice = outputDevice;
	}

	public void setTriggerLevel(final float triggerLevel) {
		this.triggerLevel = triggerLevel;
	}
}
