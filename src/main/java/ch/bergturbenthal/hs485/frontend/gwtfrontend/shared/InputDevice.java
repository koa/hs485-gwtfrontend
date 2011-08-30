/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Input-Device
 */
@Entity
public class InputDevice implements Serializable {
	private static final long	serialVersionUID	= 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer						inputDeviceId;
	private String						name;
	@ManyToOne
	private Room							room;
	private InputDeviceType		type;

	public Integer getInputDeviceId() {
		return inputDeviceId;
	}

	public String getName() {
		return name;
	}

	public Room getRoom() {
		return room;
	}

	public InputDeviceType getType() {
		return type;
	}

	public void setInputDeviceId(final Integer inputDeviceId) {
		this.inputDeviceId = inputDeviceId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRoom(final Room room) {
		this.room = room;
	}

	public void setType(final InputDeviceType type) {
		this.type = type;
	}

}
