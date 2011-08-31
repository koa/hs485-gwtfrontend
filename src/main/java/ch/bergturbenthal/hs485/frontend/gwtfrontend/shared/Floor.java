/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

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
	@Lob
	private String						plan;

	public Integer getFloorId() {
		return floorId;
	}

	public String getName() {
		return name;
	}

	public void setFloorId(final Integer floorId) {
		this.floorId = floorId;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Floor [");
		if (floorId != null) {
			builder.append("floorId=");
			builder.append(floorId);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
		}
		builder.append("]");
		return builder.toString();
	}

}
