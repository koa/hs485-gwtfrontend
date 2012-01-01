package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ActorKeySink implements EventSink<KeyEvent>, IsSerializable {
	@DBRef
	private OutputDevice	outputDevice;
	private Integer				autoOffTime;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ActorKeySink other = (ActorKeySink) obj;
		if (autoOffTime == null) {
			if (other.autoOffTime != null)
				return false;
		} else if (!autoOffTime.equals(other.autoOffTime))
			return false;
		if (outputDevice == null) {
			if (other.outputDevice != null)
				return false;
		} else if (!outputDevice.equals(other.outputDevice))
			return false;
		return true;
	}

	public Integer getAutoOffTime() {
		return autoOffTime;
	}

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (autoOffTime == null ? 0 : autoOffTime.hashCode());
		result = prime * result + (outputDevice == null ? 0 : outputDevice.hashCode());
		return result;
	}

	public void setAutoOffTime(final Integer autoOffTime) {
		this.autoOffTime = autoOffTime;
	}

	public void setOutputDevice(final OutputDevice outputDevice) {
		this.outputDevice = outputDevice;
	}
}
