/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 *
 */
@Embeddable
public class InputConnector implements Serializable {
	private static final long	serialVersionUID	= -6176793023621697995L;
	private ConnectorAddress	address;
	private String						connectorName;
	private InputType					type;

	public ConnectorAddress getAddress() {
		return address;
	}

	public String getConnectorName() {
		return connectorName;
	}

	public InputType getType() {
		return type;
	}

	public void setAddress(final ConnectorAddress address) {
		this.address = address;
	}

	public void setConnectorName(final String connectorName) {
		this.connectorName = connectorName;
	}

	public void setType(final InputType type) {
		this.type = type;
	}

}
