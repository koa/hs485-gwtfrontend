package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import com.google.gwt.user.client.ui.DialogBox;

public class SelectKeyDialog extends DialogBox {

	public SelectKeyDialog() {
		setHTML("New dialog");

		final SelectInputComposite selectKeyComposite = new SelectInputComposite();
		setWidget(selectKeyComposite);
		selectKeyComposite.setSize("100%", "100%");
	}

}
