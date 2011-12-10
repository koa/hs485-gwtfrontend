package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

public class PositionXY implements Serializable {
	private static final long	serialVersionUID	= -5769901005298597592L;
	private Float							x;
	private Float							y;

	public PositionXY() {
		super();
	}

	public PositionXY(final Float x, final Float y) {
		this.x = x;
		this.y = y;
	}

	public Float getX() {
		return x;
	}

	public Float getY() {
		return y;
	}

	public void setX(final Float x) {
		this.x = x;
	}

	public void setY(final Float y) {
		this.y = y;
	}

}
