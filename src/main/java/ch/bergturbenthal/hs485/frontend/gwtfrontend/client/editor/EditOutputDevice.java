package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EditOutputDevice extends DialogBox {

	interface EditOutputDeviceUiBinder extends UiBinder<Widget, EditOutputDevice> {
	}

	private static EditOutputDeviceUiBinder	uiBinder							= GWT.create(EditOutputDeviceUiBinder.class);
	@UiField
	protected Button												cancelButton;
	@UiField
	protected TextBox												nameTextInput;
	@UiField
	protected Button												saveButton;
	@UiField
	protected ListBox												typeListBox;
	@UiField
	ListBox																	selectAddressListBox;
	private List<OutputAddress>							addressList;
	private final CommunicationServiceAsync	communicationService	= CommunicationServiceAsync.Util.getInstance();
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
		selectAddressListBox.setVisible(false);
		communicationService.listOutputDevices(new AsyncCallback<Map<OutputAddress, OutputDescription>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final Map<OutputAddress, OutputDescription> result) {
				selectAddressListBox.clear();
				addressList = new ArrayList<OutputAddress>(result.size());
				System.out.println("Current address: " + device.getAddress());
				for (final Entry<OutputAddress, OutputDescription> outputAddressEntry : result.entrySet()) {
					final OutputAddress currentAddress = outputAddressEntry.getKey();
					addressList.add(currentAddress);
					final OutputDescription description = outputAddressEntry.getValue();
					final StringBuffer entryText = new StringBuffer();
					entryText.append(Integer.toHexString(currentAddress.getDeviceAddress().intValue()));
					entryText.append(":");
					entryText.append(Integer.toHexString(currentAddress.getOutputAddress().intValue()));
					if (description.getHasSwitch())
						entryText.append(" Switch");
					if (description.getHasTimer())
						entryText.append(" Timer");
					if (description.getIsDimmer())
						entryText.append(" Dimmer");
					selectAddressListBox.addItem(entryText.toString());
					if (currentAddress.equals(device.getAddress()))
						selectAddressListBox.setSelectedIndex(selectAddressListBox.getItemCount() - 1);
				}
				selectAddressListBox.setVisible(true);
			}
		});
		// for(String outputAddressEntry)
		nameTextInput.setText(device.getName());

	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		hide();
	}

	@UiHandler("saveButton")
	void onSaveButtonClick(final ClickEvent event) {
		device.setName(nameTextInput.getValue());
		final int typeSelectedIndex = typeListBox.getSelectedIndex();
		if (typeSelectedIndex >= 0)
			device.setType(OutputDeviceType.valueOf(typeListBox.getValue(typeSelectedIndex)));
		else
			device.setType(null);
		final int addressSelectedIndex = selectAddressListBox.getSelectedIndex();
		if (addressSelectedIndex >= 0)
			device.setAddress(addressList.get(addressSelectedIndex));
		else
			device.setAddress(null);
		System.out.println(device.getAddress());
		hide();
	}
}
