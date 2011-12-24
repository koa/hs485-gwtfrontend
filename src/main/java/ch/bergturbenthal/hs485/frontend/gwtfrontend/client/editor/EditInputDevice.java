package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
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
	@UiField
	TextBox																	nameTextInput;
	@UiField
	Button																	saveButton;
	@UiField
	SelectInputComposite										selectKeyComposite;
	private InputDevice											inputDevice;

	public EditInputDevice() {
		setWidget(uiBinder.createAndBindUi(this));
		setModal(true);
		setTitle("Edit Input Device");
		nameTextInput.setValue("new Inputdevice");
		connectorEditor.setSelectKeyComposite(selectKeyComposite);
		connectorEditor.setData(new LinkedList<InputConnector>());

	}

	public InputDevice getInputDevice() {
		return inputDevice;
	}

	public void setInputDevice(final InputDevice device) {
		inputDevice = device;
		if (device != null)
			nameTextInput.setValue(device.getName());
		final List<InputConnector> inputConnectors = new ArrayList<InputConnector>();
		if (device != null)
			if (device.getConnectors() != null)
				for (final InputConnector inputConnector : device.getConnectors())
					inputConnectors.add(new InputConnector(inputConnector));
		connectorEditor.setData(inputConnectors);
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(final ClickEvent event) {
		if (inputDevice == null)
			inputDevice = new InputDevice();
		inputDevice.setConnectors(connectorEditor.getEntries());
		inputDevice.setName(nameTextInput.getValue());
		hide();
	}
}
