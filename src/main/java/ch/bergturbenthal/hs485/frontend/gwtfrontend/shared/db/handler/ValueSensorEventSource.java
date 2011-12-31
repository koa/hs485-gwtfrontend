package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ValueSensorEventSource implements EventSource, IsSerializable {
	private InputConnector	inputConnector;
	private int							pollIntervall;

	public InputConnector getInputConnector() {
		return inputConnector;
	}

	public int getPollIntervall() {
		return pollIntervall;
	}

	@Override
	public Class<? extends Event> getSendingEventType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setInputConnector(final InputConnector inputConnector) {
		this.inputConnector = inputConnector;
	}

	public void setPollIntervall(final int pollIntervall) {
		this.pollIntervall = pollIntervall;
	}
}
