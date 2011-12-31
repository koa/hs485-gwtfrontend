package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ToggleKeyEventSource extends KeyEventSource implements IsSerializable {
	private InputConnector	inputConnector;

	public InputConnector getInputConnector() {
		return inputConnector;
	}

	public void setInputConnector(final InputConnector inputConnector) {
		this.inputConnector = inputConnector;
	}
}
