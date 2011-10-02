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
	private InputAddress			address;
	private String						connectorName;

	public InputAddress getAddress() {
		return address;
	}

	public String getConnectorName() {
		return connectorName;
	}

	public void setAddress(final InputAddress address) {
		this.address = address;
	}

	public void setConnectorName(final String connectorName) {
		this.connectorName = connectorName;
	}

}
