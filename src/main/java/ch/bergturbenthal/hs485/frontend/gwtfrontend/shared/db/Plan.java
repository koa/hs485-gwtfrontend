package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.code.morphia.annotations.Entity;

@Document
@Entity
public class Plan implements Serializable {

	private static final long	serialVersionUID	= 3176259108886016294L;
	private List<Floor>				floors						= new ArrayList<Floor>();
	private String						name;
	@Id
	private String						planId						= "plan";

	public List<Floor> getFloors() {
		return floors;
	}

	public String getName() {
		return name;
	}

	public String getPlanId() {
		return planId;
	}

	public void setFloors(final List<Floor> floors) {
		this.floors = floors;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlanId(final String planId) {
		this.planId = planId;
	}

}
