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
import javax.persistence.OneToMany;

/**
 * a Floor of a Building.
 */
@Entity
public class Floor implements Serializable {

	private static final long	serialVersionUID	= 2901805918126067682L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer						floorId;
	private String						name;
	@OneToMany(mappedBy = "floor", fetch = FetchType.LAZY)
	private Collection<Room>	rooms;

	public Integer getFloorId() {
		return floorId;
	}

	public String getName() {
		return name;
	}

	public Collection<Room> getRooms() {
		return rooms;
	}

	public void setFloorId(final Integer floorId) {
		this.floorId = floorId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setRooms(final Collection<Room> rooms) {
		this.rooms = rooms;
	}

}
