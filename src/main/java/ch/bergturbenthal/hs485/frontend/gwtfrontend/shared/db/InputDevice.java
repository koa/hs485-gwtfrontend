/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;

/**
 * Input-Device
 */
@Entity
public class InputDevice implements Serializable {
	private static final long			serialVersionUID	= 1L;
	@ElementCollection
	@OrderColumn
	private List<InputConnector>	connectors				= new ArrayList<InputConnector>();
	@Embedded
	private FloorPlace						floorPlace				= new FloorPlace();
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer								inputDeviceId;
	private String								name;

	public List<InputConnector> getConnectors() {
		return connectors;
	}

	public FloorPlace getFloorPlace() {
		return floorPlace;
	}

	public Integer getInputDeviceId() {
		return inputDeviceId;
	}

	public String getName() {
		return name;
	}

	public void setConnectors(final List<InputConnector> connectors) {
		this.connectors = connectors;
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

}
