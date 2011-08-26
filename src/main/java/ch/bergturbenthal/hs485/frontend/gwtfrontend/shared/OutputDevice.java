package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OUTPUTDEVICE")
public class OutputDevice {
	public static enum Type {
		DIMMER, SWITCH
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int			deviceId;
	private String	name;
	private Type		type;

	public int getDeviceId() {
		return deviceId;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setDeviceId(final int deviceId) {
		this.deviceId = deviceId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "OutputDevice [name=" + name + ", type=" + type + "]";
	}

}
