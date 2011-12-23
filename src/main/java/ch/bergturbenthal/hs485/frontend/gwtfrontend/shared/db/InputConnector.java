/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class InputConnector implements Serializable {
	private static final long	serialVersionUID	= -6176793023621697995L;
	private InputAddress			address						= new InputAddress();
	@Id
	private String						connectorId;
	private String						connectorName;
	private InputDeviceType		type;

	public InputConnector() {
	}

	public InputConnector(final InputConnector inputConnector) {
		setType(inputConnector.getType());
		setConnectorName(inputConnector.getConnectorName());
		setAddress(new InputAddress(inputConnector.getAddress()));
	}

	public InputAddress getAddress() {
		return address;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public String getConnectorName() {
		return connectorName;
	}

	public InputDeviceType getType() {
		return type;
	}

	public void setAddress(final InputAddress address) {
		this.address = address;
	}

	public void setConnectorId(final String connectorId) {
		this.connectorId = connectorId;
	}

	public void setConnectorName(final String connectorName) {
		this.connectorName = connectorName;
	}

	public void setType(final InputDeviceType type) {
		this.type = type;
	}

}
