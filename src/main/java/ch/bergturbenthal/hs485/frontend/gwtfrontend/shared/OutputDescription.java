package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OutputDescription implements IsSerializable {
	private Boolean	hasSwitch;
	private Boolean	hasTimer;
	private Boolean	isDimmer;
	private String	connectionLabel;

	public String getConnectionLabel() {
		return connectionLabel;
	}

	public Boolean getHasSwitch() {
		return hasSwitch;
	}

	public Boolean getHasTimer() {
		return hasTimer;
	}

	public Boolean getIsDimmer() {
		return isDimmer;
	}

	public void setConnectionLabel(final String connectionLabel) {
		this.connectionLabel = connectionLabel;
	}

	public void setHasSwitch(final Boolean hasSwitch) {
		this.hasSwitch = hasSwitch;
	}

	public void setHasTimer(final Boolean hasTimer) {
		this.hasTimer = hasTimer;
	}

	public void setIsDimmer(final Boolean isDimmer) {
		this.isDimmer = isDimmer;
	}

}
