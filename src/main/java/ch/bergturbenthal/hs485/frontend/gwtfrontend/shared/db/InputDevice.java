/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

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
	@ManyToOne
	private Floor							floor;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer						inputDeviceId;
	private String						name;
	private InputDeviceType		type;

	public Floor getFloor() {
		return floor;
	}

	public Integer getInputDeviceId() {
		return inputDeviceId;
	}

	public String getName() {
		return name;
	}

	public InputDeviceType getType() {
		return type;
	}

	public void setFloor(final Floor floor) {
		this.floor = floor;
	}

	public void setInputDeviceId(final Integer inputDeviceId) {
		this.inputDeviceId = inputDeviceId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setType(final InputDeviceType type) {
		this.type = type;
	}

}
