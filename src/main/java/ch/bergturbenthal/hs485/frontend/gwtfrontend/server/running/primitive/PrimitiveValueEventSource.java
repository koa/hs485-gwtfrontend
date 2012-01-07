package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;

public class PrimitiveValueEventSource implements PrimitiveEventSource {
	public static enum SensorType {
		TEMPERATURE, HUMIDITY
	}

	private SensorType		sensorType;
	private InputAddress	input;

	public InputAddress getInput() {
		return input;
	}

	public SensorType getSensorType() {
		return sensorType;
	}

	public void setInput(final InputAddress input) {
		this.input = input;
	}

	public void setSensorType(final SensorType sensorType) {
		this.sensorType = sensorType;
	}

}
