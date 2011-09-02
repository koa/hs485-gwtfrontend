/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.ArrayList;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.FloorEditor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;

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
	private final FloorEditor					floorEditor;
	private final Messages						messages			= GWT.create(Messages.class);

	public FloorEditorDialog() {
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
				configService.updateFloors(floorEditor.getModifiedFloors(), new AsyncCallback<Void>() {

					public void onFailure(final Throwable caught) {
						reloadData();
					}

					public void onSuccess(final Void result) {
						configService.removeFloors(floorEditor.getRemovedFloors(), new AsyncCallback<Void>() {

							public void onFailure(final Throwable caught) {
								reloadData();
							}

							public void onSuccess(final Void result) {
								hide();
							}
						});
					}
				});
			}
		});
		horizontalSplitPanel.add(okButton);
	}

	/**
	 * 
	 */
	private void reloadData() {
		configService.listAllFloors(new AsyncCallback<Iterable<Floor>>() {

			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			public void onSuccess(final Iterable<Floor> result) {
				final ArrayList<Floor> floors = new ArrayList<Floor>();
				for (final Floor floor : result)
					floors.add(floor);
				floorEditor.setData(floors);
			}
		});
	}

	@Override
	public void show() {
		reloadData();
		super.show();
	}
}
