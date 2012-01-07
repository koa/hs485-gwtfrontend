package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class EditOutputDevice extends DialogBox {

	interface EditOutputDeviceUiBinder extends UiBinder<Widget, EditOutputDevice> {
	}

	private static EditOutputDeviceUiBinder				uiBinder							= GWT.create(EditOutputDeviceUiBinder.class);
	@UiField
	protected Button															cancelButton;
	@UiField
	protected TextBox															nameTextInput;
	@UiField
	protected Button															saveButton;
	@UiField
	protected ListBox															typeListBox;
	@UiField
	ListBox																				selectAddressListBox;
	@UiField
	ToggleButton																	toggleButton;
	private List<OutputAddress>										addressList;
	private final CommunicationServiceAsync				communicationService	= CommunicationServiceAsync.Util.getInstance();
	private OutputDevice													outputDevice;
	private Map<OutputAddress, OutputDescription>	outputDevicesMap			= Collections.emptyMap();
	private Plan																	plan;

	public EditOutputDevice() {
		setWidget(uiBinder.createAndBindUi(this));
		setModal(true);
		updateDeviceTypes();
		selectAddressListBox.setVisible(false);
		communicationService.listOutputDevices(new AsyncCallback<Map<OutputAddress, OutputDescription>>() {

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("Error reading Output-Devices", caught);
			}

			@Override
			public void onSuccess(final Map<OutputAddress, OutputDescription> outputDevicesMap) {
				EditOutputDevice.this.outputDevicesMap = outputDevicesMap;
				updateDeviceList();
			}

		});
		nameTextInput.setText("new output device");

	}

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	public void setOutputDevice(final OutputDevice device) {
		outputDevice = device;
		if (device == null)
			return;
		nameTextInput.setText(device.getName());
		updateDeviceTypes();
		updateDeviceList();
	}

	public void setPlan(final Plan plan) {
		this.plan = plan;
		updateDeviceList();
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(final ClickEvent event) {
		if (outputDevice == null)
			outputDevice = new OutputDevice();
		outputDevice.setName(nameTextInput.getValue());
		final int typeSelectedIndex = typeListBox.getSelectedIndex();
		if (typeSelectedIndex >= 0)
			outputDevice.setType(OutputDeviceType.valueOf(typeListBox.getValue(typeSelectedIndex)));
		else
			outputDevice.setType(null);
		final int addressSelectedIndex = selectAddressListBox.getSelectedIndex();
		if (addressSelectedIndex >= 0)
			outputDevice.setAddress(addressList.get(addressSelectedIndex));
		else
			outputDevice.setAddress(null);
		hide();
	}

	@UiHandler("selectAddressListBox")
	void onSelectAddressListBoxChange(final ChangeEvent event) {
		updateToggleButton();
	}

	@UiHandler("selectAddressListBox")
	void onSelectAddressListBoxKeyUp(final KeyUpEvent event) {
		updateToggleButton();
	}

	@UiHandler("toggleButton")
	void onToggleButtonClick(final ClickEvent event) {
		final OutputAddress currentAddress = readCurrentAddress();
		if (currentAddress == null)
			return;
		final boolean state = toggleButton.getValue();
		communicationService.setOutputSwitchState(currentAddress, state, new AsyncCallback<Void>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(final Void result) {
				// TODO Auto-generated method stub
			}
		});
	}

	private OutputAddress readCurrentAddress() {
		final int selectedIndex = selectAddressListBox.getSelectedIndex();
		final OutputAddress address;
		if (selectedIndex < 0 || selectedIndex >= addressList.size())
			address = null;
		else
			address = addressList.get(selectedIndex);
		return address;
	}

	private void updateDeviceList() {
		selectAddressListBox.clear();
		addressList = new ArrayList<OutputAddress>(outputDevicesMap.size());
		for (final Entry<OutputAddress, OutputDescription> outputAddressEntry : outputDevicesMap.entrySet()) {
			final OutputAddress currentAddress = outputAddressEntry.getKey();
			addressList.add(currentAddress);
			final OutputDescription description = outputAddressEntry.getValue();
			final StringBuffer entryText = new StringBuffer();
			entryText.append(description.getConnectionLabel());
			if (description.getIsDimmer())
				entryText.append(" (Dimmer)");
			if (plan != null)
				for (final Floor floor : plan.getFloors())
					for (final OutputDevice outputDevice : floor.getOutputDevices())
						if (currentAddress.equals(outputDevice.getAddress())) {
							entryText.append(" <");
							entryText.append(outputDevice.getName());
							entryText.append(">");
						}
			selectAddressListBox.addItem(entryText.toString());
			if (outputDevice != null && currentAddress.equals(outputDevice.getAddress()))
				selectAddressListBox.setSelectedIndex(selectAddressListBox.getItemCount() - 1);
		}
		selectAddressListBox.setVisible(true);
	}

	private void updateDeviceTypes() {
		typeListBox.clear();
		for (final OutputDeviceType outputDeviceType : OutputDeviceType.values()) {
			typeListBox.addItem(outputDeviceType.name());
			if (outputDevice != null && outputDeviceType.equals(outputDevice.getType()))
				typeListBox.setSelectedIndex(typeListBox.getItemCount() - 1);
		}
	}

	private void updateToggleButton() {
		final OutputAddress address = readCurrentAddress();
		if (address == null)
			return;
		communicationService.getOutputSwitchState(address, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final Boolean result) {
				toggleButton.setDown(result);
			}
		});
	}
}
