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
public class ConnectorAddress implements Serializable {
	private static final long	serialVersionUID	= 1L;
	private int								connectorAddress;
	private int								deviceAddress;

	public int getConnectorAddress() {
		return connectorAddress;
	}

	public int getDeviceAddress() {
		return deviceAddress;
	}

	public void setConnectorAddress(final int connectorAddress) {
		this.connectorAddress = connectorAddress;
	}

	public void setDeviceAddress(final int deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

}
