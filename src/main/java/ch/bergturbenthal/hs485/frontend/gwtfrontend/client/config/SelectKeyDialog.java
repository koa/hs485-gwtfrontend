package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.SelectInputComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.user.client.ui.DialogBox;

public class SelectKeyDialog extends DialogBox {

	public SelectKeyDialog(final Plan plan) {
		setHTML("New dialog");

		final SelectInputComposite selectKeyComposite = new SelectInputComposite();
		selectKeyComposite.setPlan(plan);
		setWidget(selectKeyComposite);
		selectKeyComposite.setSize("100%", "100%");
	}

}
