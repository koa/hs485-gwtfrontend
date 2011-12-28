package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InputDescription implements IsSerializable {
	private boolean	humiditySensor;
	private boolean	keySensor;
	private boolean	temperatureSensor;

	public boolean isHumiditySensor() {
		return humiditySensor;
	}

	public boolean isKeySensor() {
		return keySensor;
	}

	public boolean isTemperatureSensor() {
		return temperatureSensor;
	}

	public void setHumiditySensor(final boolean humiditySensor) {
		this.humiditySensor = humiditySensor;
	}

	public void setKeySensor(final boolean keySensor) {
		this.keySensor = keySensor;
	}

	public void setTemperatureSensor(final boolean temperatureSensor) {
		this.temperatureSensor = temperatureSensor;
	}

}
