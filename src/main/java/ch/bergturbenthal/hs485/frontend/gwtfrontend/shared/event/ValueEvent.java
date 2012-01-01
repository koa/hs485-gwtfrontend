package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ValueEvent implements Event, IsSerializable {
	private float	value;

	public float getValue() {
		return value;
	}

	public void setValue(final float value) {
		this.value = value;
	}

}
