package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class KeyPairEventSource extends KeyEventSource implements IsSerializable {
	@DBRef
	private InputConnector	offInputConnector;
	@DBRef
	private InputConnector	onInputConnector;

	public InputConnector getOffInputConnector() {
		return offInputConnector;
	}

	public InputConnector getOnInputConnector() {
		return onInputConnector;
	}

	public void setOffInputConnector(final InputConnector offInputConnector) {
		this.offInputConnector = offInputConnector;
	}

	public void setOnInputConnector(final InputConnector onInputConnector) {
		this.onInputConnector = onInputConnector;
	}
}
