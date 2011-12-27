package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditFloorPropertiesDialog extends DialogBox {

	interface EditFloorPropertiesDialogUiBinder extends UiBinder<Widget, EditFloorPropertiesDialog> {
	}

	private static EditFloorPropertiesDialogUiBinder	uiBinder			= GWT.create(EditFloorPropertiesDialogUiBinder.class);
	@UiField
	Button																						cancelButton;
	@UiField
	TextBox																						floorNameTextBox;
	@UiField
	DoubleBox																					iconSizeTextBox;
	@UiField
	Button																						okButton;
	@UiField
	ListBox																						selectDrawingList;
	private final ConfigServiceAsync									configService	= ConfigServiceAsync.Util.getInstance();
	private Floor																			floor;

	public EditFloorPropertiesDialog() {
		setText("Properties of the Floor");
		setModal(true);
		setWidget(uiBinder.createAndBindUi(this));
		setFloor(null);
		configService.listFilesByMime("image/svg+xml", new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final List<String> result) {
				selectDrawingList.clear();
				final String currentFilename;
				if (floor != null && floor.getDrawing() != null)
					currentFilename = floor.getDrawing().getFileName();
				else
					currentFilename = null;
				if (result != null)
					for (final String value : result) {
						selectDrawingList.addItem(value);
						if (currentFilename != null && currentFilename.equals(value))
							selectDrawingList.setSelectedIndex(selectDrawingList.getItemCount() - 1);
					}
			}
		});
	}

	public Floor getFloor() {
		return floor;
	}

	public void setFloor(final Floor floor) {
		this.floor = floor;
		if (floor == null) {
			floorNameTextBox.setValue("Name of Floor");
			iconSizeTextBox.setValue(Double.valueOf(2000));
		} else {
			floorNameTextBox.setValue(floor.getName());
			iconSizeTextBox.setValue(Double.valueOf(floor.getIconSize().doubleValue()));
		}
	}

	@UiFactory
	protected EditFloorPropertiesDialog createDialog() {
		return this;
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("okButton")
	void onOkButtonClick(final ClickEvent event) {
		if (floor == null)
			floor = new Floor();
		floor.setName(floorNameTextBox.getValue());
		floor.setIconSize(iconSizeTextBox.getValue().floatValue());
		final String filename = selectDrawingList.getValue(selectDrawingList.getSelectedIndex());
		configService.getFile(filename, new AsyncCallback<FileData>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
				hide();
			}

			@Override
			public void onSuccess(final FileData result) {
				floor.setDrawing(result);
				hide();
			}
		});
	}
}
