package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class OutputDevice implements Serializable {
	private static final long	serialVersionUID	= 2108040144856847494L;

	@Id
	private Integer						deviceId;
	private FloorPlace				floorPlace				= new FloorPlace();
	private String						name;
	private OutputDeviceType	type;

	public Integer getDeviceId() {
		return deviceId;
	}

	public FloorPlace getFloorPlace() {
		return floorPlace;
	}

	public String getName() {
		return name;
	}

	public OutputDeviceType getType() {
		return type;
	}

	public void setDeviceId(final Integer deviceId) {
		this.deviceId = deviceId;
	}

	public void setFloorPlace(final FloorPlace floorPlace) {
		this.floorPlace = floorPlace;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setType(final OutputDeviceType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("OutputDevice [");
		if (deviceId != null) {
			builder.append("deviceId=");
			builder.append(deviceId);
			builder.append(", ");
		}
		if (floorPlace != null) {
			builder.append("floorPlace=");
			builder.append(floorPlace);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (type != null) {
			builder.append("type=");
			builder.append(type);
		}
		builder.append("]");
		return builder.toString();
	}

}
