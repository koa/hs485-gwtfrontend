package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

public class OutputDevice implements Serializable {
	private static final long	serialVersionUID	= 2108040144856847494L;

	private OutputAddress			address						= new OutputAddress();
	private String						name;
	private PositionXY				position					= new PositionXY();
	private OutputDeviceType	type;

	public OutputAddress getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

	public PositionXY getPosition() {
		return position;
	}

	public OutputDeviceType getType() {
		return type;
	}

	public void setAddress(final OutputAddress address) {
		this.address = address;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPosition(final PositionXY position) {
		this.position = position;
	}

	public void setType(final OutputDeviceType type) {
		this.type = type;
	}

}
