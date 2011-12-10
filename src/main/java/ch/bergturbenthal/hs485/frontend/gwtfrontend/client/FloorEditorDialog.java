/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.ArrayList;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.FloorEditor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class FloorEditorDialog extends DialogBox {
	private final ConfigServiceAsync	configService	= ConfigServiceAsync.Util.getInstance();
	private Plan											currentPlan;
	private final FloorEditor					floorEditor;
	private final Messages						messages			= GWT.create(Messages.class);

	private final String							planId;

	public FloorEditorDialog(final String planId) {
		this.planId = planId;
		setHTML(messages.editFloors());

		final VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);

		floorEditor = new FloorEditor();
		verticalPanel.add(floorEditor);

		final HorizontalPanel horizontalSplitPanel = new HorizontalPanel();
		horizontalSplitPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.add(horizontalSplitPanel);
		horizontalSplitPanel.setWidth("100%");

		final Button cancelButton = new Button(messages.cancelText());
		cancelButton.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		horizontalSplitPanel.add(cancelButton);

		final Button okButton = new Button(messages.okText());
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				configService.savePlan(currentPlan, new AsyncCallback<Plan>() {

					@Override
					public void onFailure(final Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(final Plan result) {
						currentPlan = result;
						reloadFloors();
					}
				});
			}
		});
		horizontalSplitPanel.add(okButton);
	}

	private void reloadData() {
		configService.readPlan(planId, new AsyncCallback<Plan>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(final Plan result) {
				currentPlan = result;
				reloadFloors();
			}
		});
	}

	private void reloadFloors() {
		final ArrayList<Floor> floors = new ArrayList<Floor>();
		for (final Floor floor : currentPlan.getFloors())
			floors.add(floor);
		floorEditor.setData(floors);
	}

	@Override
	public void show() {
		reloadData();
		super.show();
	}
}
