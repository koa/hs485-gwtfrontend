package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

@Document
public class Plan implements Serializable {

	private static final long		serialVersionUID	= 3176259108886016294L;
	private List<Action<Event>>	actions						= new ArrayList<Action<Event>>();
	private List<Floor>					floors						= new ArrayList<Floor>();
	@DBRef
	private IconSet							iconSet;
	private Integer							maximumOnTime;
	private String							name;
	@Id
	private String							planId						= "plan";

	public List<Action<Event>> getActions() {
		return actions;
	}

	public List<Floor> getFloors() {
		return floors;
	}

	public IconSet getIconSet() {
		return iconSet;
	}

	public Integer getMaximumOnTime() {
		return maximumOnTime;
	}

	public String getName() {
		return name;
	}

	public String getPlanId() {
		return planId;
	}

	public void setActions(final List<Action<Event>> actions) {
		this.actions = actions;
	}

	public void setFloors(final List<Floor> floors) {
		this.floors = floors;
	}

	public void setIconSet(final IconSet iconSet) {
		this.iconSet = iconSet;
	}

	public void setMaximumOnTime(final Integer maximumOnTime) {
		this.maximumOnTime = maximumOnTime;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPlanId(final String planId) {
		this.planId = planId;
	}

}
