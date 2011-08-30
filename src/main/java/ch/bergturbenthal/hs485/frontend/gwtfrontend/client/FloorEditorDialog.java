/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import java.util.ArrayList;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.FloorEditor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;

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

	public FloorEditorDialog() {
		setHTML("Edit Floors");

		final VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);

		floorEditor = new FloorEditor();
		verticalPanel.add(floorEditor);

		final HorizontalPanel horizontalSplitPanel = new HorizontalPanel();
		horizontalSplitPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.add(horizontalSplitPanel);

		final Button cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		horizontalSplitPanel.add(cancelButton);

		final Button okButton = new Button("Ok");
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				final List<Floor> modifiedFloors = floorEditor.getModifiedFloors();
				configService.updateFloors(modifiedFloors, new AsyncCallback<Void>() {

					public void onFailure(final Throwable caught) {
						// TODO Auto-generated method stub

					}

					public void onSuccess(final Void result) {
						hide();
					}
				});
			}
		});
		horizontalSplitPanel.add(okButton);
	}

	@Override
	public void show() {
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
		super.show();
	}
}
