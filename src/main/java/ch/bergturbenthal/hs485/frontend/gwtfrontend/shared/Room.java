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

@Entity
public class Room implements Serializable {
	private static final long	serialVersionUID	= 5336242137115223089L;
	@ManyToOne
	private Floor							floor;
	private String						name;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer						roomId;

	public Floor getFloor() {
		return floor;
	}

	public String getName() {
		return name;
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

	public void setRoomId(final Integer roomId) {
		this.roomId = roomId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Room [floor=");
		builder.append(floor);
		builder.append(", name=");
		builder.append(name);
		builder.append(", roomId=");
		builder.append(roomId);
		builder.append("]");
		return builder.toString();
	}

}
