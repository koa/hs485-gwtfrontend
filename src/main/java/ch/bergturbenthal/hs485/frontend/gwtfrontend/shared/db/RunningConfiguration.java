package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RunningConfiguration implements Serializable {

	private static final long	serialVersionUID	= -6654901367674923750L;
	@DBRef
	private Plan							plan;
	@Id
	private String						id								= "running";

	public String getId() {
		return id;
	}

	public Plan getPlan() {
		return plan;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setPlan(final Plan plan) {
		this.plan = plan;
	}
}
