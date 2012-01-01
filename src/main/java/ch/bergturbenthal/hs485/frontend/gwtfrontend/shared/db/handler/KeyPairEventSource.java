package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class KeyPairEventSource implements KeyEventSource, IsSerializable {
	@DBRef
	private InputConnector	offInputConnector;
	@DBRef
	private InputConnector	onInputConnector;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final KeyPairEventSource other = (KeyPairEventSource) obj;
		if (offInputConnector == null) {
			if (other.offInputConnector != null)
				return false;
		} else if (!offInputConnector.equals(other.offInputConnector))
			return false;
		if (onInputConnector == null) {
			if (other.onInputConnector != null)
				return false;
		} else if (!onInputConnector.equals(other.onInputConnector))
			return false;
		return true;
	}

	public InputConnector getOffInputConnector() {
		return offInputConnector;
	}

	public InputConnector getOnInputConnector() {
		return onInputConnector;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (offInputConnector == null ? 0 : offInputConnector.hashCode());
		result = prime * result + (onInputConnector == null ? 0 : onInputConnector.hashCode());
		return result;
	}

	public void setOffInputConnector(final InputConnector offInputConnector) {
		this.offInputConnector = offInputConnector;
	}

	public void setOnInputConnector(final InputConnector onInputConnector) {
		this.onInputConnector = onInputConnector;
	}
}
