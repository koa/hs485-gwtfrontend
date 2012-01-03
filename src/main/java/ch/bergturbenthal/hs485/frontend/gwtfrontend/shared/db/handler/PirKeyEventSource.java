package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class PirKeyEventSource implements KeyEventSource, IsSerializable {
	@DBRef
	private InputConnector	inputConnector;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PirKeyEventSource other = (PirKeyEventSource) obj;
		if (inputConnector == null) {
			if (other.inputConnector != null)
				return false;
		} else if (!inputConnector.equals(other.inputConnector))
			return false;
		return true;
	}

	public InputConnector getInputConnector() {
		return inputConnector;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (inputConnector == null ? 0 : inputConnector.hashCode());
		return result;
	}

	public void setInputConnector(final InputConnector inputConnector) {
		this.inputConnector = inputConnector;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PirKeyEventSource [");
		if (inputConnector != null) {
			builder.append("inputConnector=");
			builder.append(inputConnector);
		}
		builder.append("]");
		return builder.toString();
	}

}
