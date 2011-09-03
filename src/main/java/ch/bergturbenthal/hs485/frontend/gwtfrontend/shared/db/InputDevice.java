/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Input-Device
 */
@Entity
public class InputDevice implements Serializable {
	private static final long	serialVersionUID	= 1L;
	@Embedded
	private FloorPlace				floorPlace				= new FloorPlace();
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer						inputDeviceId;
	private String						name;
	private InputDeviceType		type;

	public FloorPlace getFloorPlace() {
		return floorPlace;
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

	public void setFloorPlace(final FloorPlace floorPlace) {
		this.floorPlace = floorPlace;
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
