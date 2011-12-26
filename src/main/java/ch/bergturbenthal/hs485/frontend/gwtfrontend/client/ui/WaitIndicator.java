package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

public class WaitIndicator extends PopupPanel {
	private static WaitIndicator	instance	= new WaitIndicator();

	public static void hideWait() {
		instance.hide();
	}

	public static void showWait() {
		instance.center();
	}

	private WaitIndicator() {

		final Label lblPleaseWait = new Label("please Wait");
		setWidget(lblPleaseWait);
		lblPleaseWait.setSize("100%", "100%");
	}
}
