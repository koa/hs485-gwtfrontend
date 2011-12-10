package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;

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

public class EditInputDevice extends DialogBox {

	interface EditInputDeviceUiBinder extends UiBinder<Widget, EditInputDevice> {
	}

	private static EditInputDeviceUiBinder	uiBinder	= GWT.create(EditInputDeviceUiBinder.class);
	@UiField
	Button																	cancelButton;
	@UiField
	InputConnectorEditorTable								connectorEditor;
	private final InputDevice								device;
	@UiField
	TextBox																	nameTextInput;
	private final Runnable									refreshRunnable;
	@UiField
	Button																	saveButton;
	@UiField
	SelectInputComposite										selectKeyComposite;
	@UiField
	ListBox																	typeListBox;

	public EditInputDevice(final InputDevice inputDevice, final Runnable refreshRunnable) {
		device = inputDevice;
		this.refreshRunnable = refreshRunnable;
		setWidget(uiBinder.createAndBindUi(this));
		setModal(true);
		nameTextInput.setValue(inputDevice.getName());
		if (device.getConnectors() == null)
			device.setConnectors(new ArrayList<InputConnector>());
		connectorEditor.setData(device.getConnectors());
		for (final InputDeviceType inputDeviceType : InputDeviceType.values())
			typeListBox.addItem(inputDeviceType.name());
		// if (inputDeviceType.equals(inputDevice.getType()))
		// typeListBox.setSelectedIndex(typeListBox.getItemCount() - 1);
		connectorEditor.setSelectKeyComposite(selectKeyComposite);
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(final ClickEvent event) {
		device.setConnectors(connectorEditor.getEntries());
		device.setName(nameTextInput.getValue());
		// device.setConnectors(connectorEditor.gete)
		final int selectedIndex = typeListBox.getSelectedIndex();
		if (selectedIndex >= 0)
			// device.setType(InputDeviceType.valueOf(typeListBox.getValue(selectedIndex)));
			hide();
		refreshRunnable.run();
	}
}
