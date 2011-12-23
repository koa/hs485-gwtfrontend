package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.code.morphia.annotations.Entity;

@Document
@Entity
public class Plan implements Serializable {

	private static final long	serialVersionUID	= 3176259108886016294L;
	private List<Connection>	connections				= new ArrayList<Connection>();
	private List<Floor>				floors						= new ArrayList<Floor>();
	@DBRef
	private IconSet						iconSet;
	private String						name;
	@Id
	private String						planId						= "plan";

	public List<Connection> getConnections() {
		return connections;
	}

	public List<Floor> getFloors() {
		return floors;
	}

	public IconSet getIconSet() {
		return iconSet;
	}

	public String getName() {
		return name;
	}

	public String getPlanId() {
		return planId;
	}

	public void setConnections(final List<Connection> connections) {
		this.connections = connections;
	}

	public void setFloors(final List<Floor> floors) {
		this.floors = floors;
	}

	public void setIconSet(final IconSet iconSet) {
		this.iconSet = iconSet;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlanId(final String planId) {
		this.planId = planId;
	}

}
