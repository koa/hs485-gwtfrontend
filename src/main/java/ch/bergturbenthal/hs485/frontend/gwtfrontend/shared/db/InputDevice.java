/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.SelectableIcon;

/**
 * Input-Device
 */
public class InputDevice implements Serializable, SelectableIcon {
	private static final long			serialVersionUID	= 1L;
	@DBRef
	private List<InputConnector>	connectors				= new ArrayList<InputConnector>();
	@Id
	private String								inputDeviceId;
	private String								name;
	private PositionXY						position					= new PositionXY();

	public List<InputConnector> getConnectors() {
		return connectors;
	}

	public String getInputDeviceId() {
		return inputDeviceId;
	}

	public String getName() {
		return name;
	}

	public PositionXY getPosition() {
		return position;
	}

	public void setConnectors(final List<InputConnector> connectors) {
		this.connectors = connectors;
	}

	public void setInputDeviceId(final String inputDeviceId) {
		this.inputDeviceId = inputDeviceId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPosition(final PositionXY position) {
		this.position = position;
	}

}
