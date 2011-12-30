package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

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
		setText("Edit Input Device");
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

	public void setPlan(final Plan plan) {
		connectorEditor.setPlan(plan);
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(final ClickEvent event) {
		if (inputDevice == null)
			inputDevice = new InputDevice();
		final LinkedHashMap<String, InputConnector> originalConnectorsById = new LinkedHashMap<String, InputConnector>();
		final LinkedHashMap<String, InputConnector> originalConnectorsByName = new LinkedHashMap<String, InputConnector>();
		final IdentityHashMap<InputConnector, Boolean> foundConnectors = new IdentityHashMap<InputConnector, Boolean>();
		for (final InputConnector inputConnector : inputDevice.getConnectors()) {
			if (inputConnector.getConnectorId() != null)
				originalConnectorsById.put(inputConnector.getConnectorId(), inputConnector);
			originalConnectorsByName.put(inputConnector.getConnectorName(), inputConnector);
			foundConnectors.put(inputConnector, Boolean.FALSE);
		}
		final List<InputConnector> addConnectors = new ArrayList<InputConnector>();
		for (final InputConnector newConnector : connectorEditor.getEntries()) {
			final InputConnector connectorToOverwrite;
			if (newConnector.getConnectorId() != null)
				connectorToOverwrite = originalConnectorsById.get(newConnector.getConnectorId());
			else
				connectorToOverwrite = originalConnectorsByName.get(newConnector.getConnectorName());

			if (connectorToOverwrite == null || foundConnectors.get(connectorToOverwrite).booleanValue()) {
				newConnector.setConnectorId(null);
				addConnectors.add(newConnector);
				continue;
			}
			connectorToOverwrite.setConnectorId(newConnector.getConnectorId());
			connectorToOverwrite.setAddress(newConnector.getAddress());
			connectorToOverwrite.setType(newConnector.getType());
			connectorToOverwrite.setConnectorName(newConnector.getConnectorName());
			foundConnectors.put(connectorToOverwrite, Boolean.TRUE);
		}
		for (final Iterator<InputConnector> iterator = inputDevice.getConnectors().iterator(); iterator.hasNext();)
			if (!foundConnectors.get(iterator.next()).booleanValue())
				iterator.remove();
		inputDevice.getConnectors().addAll(addConnectors);
		inputDevice.setName(nameTextInput.getValue());
		hide();
	}
}
