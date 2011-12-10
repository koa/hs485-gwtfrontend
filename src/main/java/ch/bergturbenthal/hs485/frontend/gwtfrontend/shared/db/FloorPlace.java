/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

/**
 *
 */
public class FloorPlace implements Serializable {
	private Floor				floor;
	private PositionXY	position	= new PositionXY();

	public Floor getFloor() {
		return floor;
	}

	public PositionXY getPosition() {
		return position;
	}

	public void setFloor(final Floor floor) {
		this.floor = floor;
	}

	public void setPosition(final PositionXY position) {
		this.position = position;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("FloorPlace [");
		if (floor != null) {
			builder.append("floor=");
			builder.append(floor);
			builder.append(", ");
		}
		if (position != null) {
			builder.append("position=");
			builder.append(position);
		}
		builder.append("]");
		return builder.toString();
	}

}
