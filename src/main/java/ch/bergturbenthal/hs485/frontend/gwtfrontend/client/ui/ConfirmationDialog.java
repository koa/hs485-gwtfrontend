package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ConfirmationDialog extends DialogBox {

	public ConfirmationDialog(final String question, final ConfirmationCallback callback) {
		setText("Question");

		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		setWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		final Label label = new Label(question);
		verticalPanel.add(label);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setWidth("");

		final Button btnCancel = new Button("Cancel");
		btnCancel.setText("No");
		btnCancel.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
				callback.onDecline();
			}
		});

		final Button btnOk = new Button("Ok");
		horizontalPanel.add(btnOk);
		btnOk.setText("Yes");
		btnOk.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
				callback.onConfirm();
			}
		});
		horizontalPanel.add(btnCancel);
	}
}
