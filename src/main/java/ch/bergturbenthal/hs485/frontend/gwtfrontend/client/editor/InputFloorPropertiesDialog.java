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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class InputFloorPropertiesDialog extends DialogBox {

	interface InputFloornameDialogUiBinder extends UiBinder<Widget, InputFloorPropertiesDialog> {
	}

	private static InputFloornameDialogUiBinder	uiBinder			= GWT.create(InputFloornameDialogUiBinder.class);
	@UiField
	Button																			cancelButton;
	@UiField
	TextBox																			floorNameTextBox;
	@UiField
	Button																			okButton;
	@UiField
	ListBox																			selectDrawingList;
	@UiField
	ListBox																			selectIconSetList;
	private Runnable														closeRunnable;
	private final ConfigServiceAsync						configService	= ConfigServiceAsync.Util.getInstance();
	private Floor																floor;

	public InputFloorPropertiesDialog() {
		setText("Properties of the Floor");
		setModal(true);
		setWidget(uiBinder.createAndBindUi(this));
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

	public void setCloseRunnable(final Runnable closeRunnable) {
		this.closeRunnable = closeRunnable;
	}

	public void setFloor(final Floor floor) {
		this.floor = floor;
		if (floor == null)
			floorNameTextBox.setValue("Name of Floor");
		else
			floorNameTextBox.setValue(floor.getName());
	}

	@UiFactory
	protected InputFloorPropertiesDialog createDialog() {
		return this;
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		if (closeRunnable != null)
			closeRunnable.run();
		hide();
	}

	@UiHandler("okButton")
	void onOkButtonClick(final ClickEvent event) {
		if (floor == null)
			floor = new Floor();
		floor.setName(floorNameTextBox.getValue());
		final String filename = selectDrawingList.getValue(selectDrawingList.getSelectedIndex());
		configService.getFile(filename, new AsyncCallback<FileData>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
				if (closeRunnable != null)
					closeRunnable.run();
				hide();
			}

			@Override
			public void onSuccess(final FileData result) {
				floor.setDrawing(result);
				if (closeRunnable != null)
					closeRunnable.run();
				hide();
			}
		});
	}
}
