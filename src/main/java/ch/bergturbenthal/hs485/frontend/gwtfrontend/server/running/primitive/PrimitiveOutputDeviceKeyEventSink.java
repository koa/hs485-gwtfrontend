package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;

public class PrimitiveOutputDeviceKeyEventSink implements PrimitiveEventSink {
	private Integer				offTime;
	private OutputAddress	address;

	public OutputAddress getAddress() {
		return address;
	}

	public Integer getOffTime() {
		return offTime;
	}

	public void setAddress(final OutputAddress address) {
		this.address = address;
	}

	public void setOffTime(final Integer offTime) {
		this.offTime = offTime;
	}
}
