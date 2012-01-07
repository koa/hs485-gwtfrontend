package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TemperatureValueSensorEventSource implements EventSource<ValueEvent>, IsSerializable {
	@DBRef
	private InputConnector	inputConnector;
	private int							pollIntervall;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TemperatureValueSensorEventSource other = (TemperatureValueSensorEventSource) obj;
		if (inputConnector == null) {
			if (other.inputConnector != null)
				return false;
		} else if (!inputConnector.equals(other.inputConnector))
			return false;
		if (pollIntervall != other.pollIntervall)
			return false;
		return true;
	}

	public InputConnector getInputConnector() {
		return inputConnector;
	}

	public int getPollIntervall() {
		return pollIntervall;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inputConnector == null ? 0 : inputConnector.hashCode());
		result = prime * result + pollIntervall;
		return result;
	}

	public void setInputConnector(final InputConnector inputConnector) {
		this.inputConnector = inputConnector;
	}

	public void setPollIntervall(final int pollIntervall) {
		this.pollIntervall = pollIntervall;
	}
}
