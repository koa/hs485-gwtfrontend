package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class PositionXY implements Serializable {
	private static final long	serialVersionUID	= -5769901005298597592L;
	float											x;
	float											y;

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(final float x) {
		this.x = x;
	}

	public void setY(final float y) {
		this.y = y;
	}

}
