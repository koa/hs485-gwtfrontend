/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Room implements Serializable {
	private static final long					serialVersionUID	= 5336242137115223089L;
	@ManyToOne
	private Floor											floor;
	private String										name;
	@OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
	private Collection<OutputDevice>	outputDevices;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer										roomId;

	public Floor getFloor() {
		return floor;
	}

	public String getName() {
		return name;
	}

	public Collection<OutputDevice> getOutputDevices() {
		return outputDevices;
	}

	public Integer getRoomId() {
		return roomId;
	}

	public void setFloor(final Floor floor) {
		this.floor = floor;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOutputDevices(final Collection<OutputDevice> outputDevices) {
		this.outputDevices = outputDevices;
	}

	public void setRoomId(final Integer roomId) {
		this.roomId = roomId;
	}

}
