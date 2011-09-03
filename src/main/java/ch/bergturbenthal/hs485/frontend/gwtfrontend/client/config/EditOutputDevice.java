package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.Arrays;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
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

	private static EditOutputDeviceUiBinder	uiBinder			= GWT.create(EditOutputDeviceUiBinder.class);
	@UiField
	Button																	cancelButton;
	ConfigServiceAsync											configService	= ConfigServiceAsync.Util.getInstance();
	private OutputDevice										device;

	@UiField
	TextBox																	nameTextInput;

	@UiField
	Button																	saveButton;
	@UiField
	ListBox																	typeListBox;

	public EditOutputDevice(final OutputDevice device) {
		this.device = device;
		setWidget(uiBinder.createAndBindUi(this));
		setModal(true);
		final OutputDeviceType[] values = OutputDeviceType.values();
		for (final OutputDeviceType outputDeviceType : values) {
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
		configService.updateOutputDevices(Arrays.asList(new OutputDevice[] { device }), new AsyncCallback<Iterable<OutputDevice>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
				hide();
			}

			@Override
			public void onSuccess(final Iterable<OutputDevice> result) {
				device = result.iterator().next();
				hide();
			}
		});
	}
}
