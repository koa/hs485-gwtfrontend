package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RunningConfiguration implements Serializable {

	private static final long	serialVersionUID	= -6654901367674923750L;
	private String						planId;
	@Id
	private String						id;

	public String getId() {
		return id;
	}

	public String getPlanId() {
		return planId;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setPlanId(final String planId) {
		this.planId = planId;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("RunningConfiguration [");
		if (planId != null) {
			builder.append("planId=");
			builder.append(planId);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
		}
		builder.append("]");
		return builder.toString();
	}

}
