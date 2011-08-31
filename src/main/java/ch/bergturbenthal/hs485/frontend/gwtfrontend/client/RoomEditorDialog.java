/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.RoomEditor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Room;

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
public class RoomEditorDialog extends DialogBox {
	private final ConfigServiceAsync	configService	= ConfigServiceAsync.Util.getInstance();
	private final Messages						messages			= GWT.create(Messages.class);
	private final RoomEditor					roomEditor;

	public RoomEditorDialog() {
		setHTML(messages.editRooms());
		setModal(true);

		final VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		roomEditor = new RoomEditor();
		verticalPanel.add(roomEditor);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		verticalPanel.add(horizontalPanel);

		final Button cancelButton = new Button(messages.cancelText());
		cancelButton.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		horizontalPanel.add(cancelButton);

		final Button okButton = new Button(messages.okText());
		okButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				configService.removeRooms(roomEditor.getRemovedFloors(), new AsyncCallback<Void>() {

					public void onFailure(final Throwable caught) {
						// TODO Auto-generated method stub

					}

					public void onSuccess(final Void result) {
						configService.updateRooms(roomEditor.getModifiedFloors(), new AsyncCallback<Void>() {

							public void onFailure(final Throwable caught) {
								// TODO Auto-generated method stub

							}

							public void onSuccess(final Void result) {
								hide();
							}
						});
					}
				});
			}
		});
		horizontalPanel.add(okButton);
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
				roomEditor.setFloors(result);
			}
		});
		configService.listAllRooms(new AsyncCallback<Iterable<Room>>() {

			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			public void onSuccess(final Iterable<Room> result) {
				roomEditor.setData(result);
			}
		});
	}

	@Override
	public void show() {
		super.show();
		reloadData();
	}
}
