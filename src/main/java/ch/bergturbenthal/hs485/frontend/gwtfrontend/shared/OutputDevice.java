package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "OUTPUTDEVICE")
public class OutputDevice implements Serializable {
	private static final long	serialVersionUID	= 2108040144856847494L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer						deviceId;
	private String						name;
	@ManyToOne
	private Room							room;
	private OutputDeviceType	type;

	public Integer getDeviceId() {
		return deviceId;
	}

	public String getName() {
		return name;
	}

	public Room getRoom() {
		return room;
	}

	public OutputDeviceType getType() {
		return type;
	}

	public void setDeviceId(final Integer deviceId) {
		this.deviceId = deviceId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRoom(final Room room) {
		this.room = room;
	}

	public void setType(final OutputDeviceType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "OutputDevice [deviceId=" + deviceId + ", name=" + name + ", type=" + type + "]";
	}

}
