package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "OUTPUTDEVICE")
public class OutputDevice implements Serializable {
	public static enum Type {
		DIMMER, SWITCH
	}

	private static final long	serialVersionUID	= 2108040144856847494L;

	private int								deviceId;
	private String						name;
	private Type							type;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
		return "OutputDevice [deviceId=" + ", name=" + name + ", type=" + type + "]";
	}

}
