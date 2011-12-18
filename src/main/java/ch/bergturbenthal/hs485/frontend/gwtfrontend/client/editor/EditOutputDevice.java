package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditOutputDevice extends DialogBox {

	interface EditOutputDeviceUiBinder extends UiBinder<Widget, EditOutputDevice> {
	}

	private static EditOutputDeviceUiBinder	uiBinder	= GWT.create(EditOutputDeviceUiBinder.class);
	@UiField
	protected Button												cancelButton;
	@UiField
	protected TextBox												nameTextInput;
	@UiField
	protected Button												saveButton;
	@UiField
	protected ListBox												typeListBox;
	private final OutputDevice							device;

	public EditOutputDevice(final OutputDevice device) {
		this.device = device;
		setWidget(uiBinder.createAndBindUi(this));
		setModal(true);
		for (final OutputDeviceType outputDeviceType : OutputDeviceType.values()) {
			typeListBox.addItem(outputDeviceType.name());
			if (outputDeviceType.equals(device.getType()))
				typeListBox.setSelectedIndex(typeListBox.getItemCount() - 1);
		}
		nameTextInput.setText(device.getName());
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(final ClickEvent event) {
		device.setName(nameTextInput.getValue());
		final int selectedIndex = typeListBox.getSelectedIndex();
		if (selectedIndex >= 0)
			device.setType(OutputDeviceType.valueOf(typeListBox.getValue(selectedIndex)));
		hide();
	}
}
