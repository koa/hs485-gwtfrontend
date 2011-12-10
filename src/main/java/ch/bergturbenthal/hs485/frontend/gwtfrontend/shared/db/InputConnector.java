/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.mongodb.core.index.Indexed;

/**
 *
 */
public class InputConnector implements Serializable {
	private static final long	serialVersionUID	= -6176793023621697995L;
	@Indexed
	private InputAddress			address						= new InputAddress();
	private String						connectorName;
	private InputDeviceType		type;

	public InputAddress getAddress() {
		return address;
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

	public void setConnectorName(final String connectorName) {
		this.connectorName = connectorName;
	}

	public void setType(final InputDeviceType type) {
		this.type = type;
	}

}
