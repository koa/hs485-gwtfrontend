/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.code.morphia.annotations.Entity;

/**
 * a Floor of a Building.
 */
@Document
@Entity
public class Floor implements Serializable {

	private static final long				serialVersionUID	= 2901805918126067682L;
	private final List<InputDevice>	inputDevices			= new ArrayList<InputDevice>();
	private String									name;
	@DBRef
	private FileData								plan;
	private Float										scale;

	public List<InputDevice> getInputDevices() {
		return inputDevices;
	}

	public String getName() {
		return name;
	}

	public FileData getPlan() {
		return plan;
	}

	public Float getScale() {
		return scale;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlan(final FileData plan) {
		this.plan = plan;
	}

	public void setScale(final Float scale) {
		this.scale = scale;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Floor [");
		if (inputDevices != null) {
			builder.append("inputDevices=");
			builder.append(inputDevices);
			builder.append(", ");
		}
		if (name != null) {
			builder.append("name=");
			builder.append(name);
			builder.append(", ");
		}
		if (plan != null) {
			builder.append("plan=");
			builder.append(plan);
			builder.append(", ");
		}
		if (scale != null) {
			builder.append("scale=");
			builder.append(scale);
		}
		builder.append("]");
		return builder.toString();
	}

}
