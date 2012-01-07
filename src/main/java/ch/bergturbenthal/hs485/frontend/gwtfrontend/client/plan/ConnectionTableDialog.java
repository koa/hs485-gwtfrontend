package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event.LabelGenerator;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.InputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.OutputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.RowCountChangeEvent;

public class ConnectionTableDialog extends DialogBox {
	private final CommunicationServiceAsync	communicationService	= CommunicationServiceAsync.Util.getInstance();

	public ConnectionTableDialog(final LabelGenerator labelGenerator, final Plan plan) {

		final Map<InputAddress, InputConnector> inputIndex = new HashMap<InputAddress, InputConnector>();
		final Map<OutputAddress, OutputDevice> outputIndex = new HashMap<OutputAddress, OutputDevice>();
		if (plan != null)
			for (final Floor floor : plan.getFloors())
				for (final InputDevice inputDevice : floor.getInputDevices()) {
					for (final InputConnector inputConnector : inputDevice.getConnectors())
						if (inputConnector.getAddress() != null)
							inputIndex.put(inputConnector.getAddress(), inputConnector);
					for (final OutputDevice outputDevice : floor.getOutputDevices())
						if (outputDevice.getAddress() != null)
							outputIndex.put(outputDevice.getAddress(), outputDevice);
				}
		setHTML("Connections");

		final VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);

		verticalPanel.add(new Label("Input Connections"));

		final CellTable<Entry<InputAddress, InputDescription>> inputTable = new CellTable<Entry<InputAddress, InputDescription>>();
		verticalPanel.add(inputTable);

		final TextColumn<Entry<InputAddress, InputDescription>> inputAddressColumn = new TextColumn<Entry<InputAddress, InputDescription>>() {
			@Override
			public String getValue(final Entry<InputAddress, InputDescription> object) {
				return Integer.toHexString(object.getKey().getDeviceAddress()) + ": " + object.getKey().getInputAddress();
			}
		};
		inputTable.addColumn(inputAddressColumn, "Address");

		final TextColumn<Entry<InputAddress, InputDescription>> inputLabelColumn = new TextColumn<Entry<InputAddress, InputDescription>>() {

			@Override
			public String getValue(final Entry<InputAddress, InputDescription> object) {
				return object.getValue().getConnectionLabel();
			}
		};
		inputTable.addColumn(inputLabelColumn, "Label");
		inputTable.setColumnWidth(inputLabelColumn, "150px");

		final TextColumn<Entry<InputAddress, InputDescription>> inputConnectionColumn = new TextColumn<Entry<InputAddress, InputDescription>>() {

			@Override
			public String getValue(final Entry<InputAddress, InputDescription> object) {
				return labelGenerator.makeLabelForInputConnector(inputIndex.get(object.getKey()));
			}
		};
		inputTable.addColumn(inputConnectionColumn, "Connection");
		inputTable.setColumnWidth(inputLabelColumn, "200px");

		verticalPanel.add(new Label("Output Connections"));

		final Button closeButton = new Button("Close");
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});

		final CellTable<Entry<OutputAddress, OutputDescription>> outputTable = new CellTable<Entry<OutputAddress, OutputDescription>>();
		verticalPanel.add(outputTable);

		final TextColumn<Entry<OutputAddress, OutputDescription>> addressOutputColumn = new TextColumn<Entry<OutputAddress, OutputDescription>>() {

			@Override
			public String getValue(final Entry<OutputAddress, OutputDescription> object) {
				return Integer.toHexString(object.getKey().getDeviceAddress()) + ": " + object.getKey().getOutputAddress();
			}
		};
		outputTable.addColumn(addressOutputColumn, "Address");

		final TextColumn<Entry<OutputAddress, OutputDescription>> labelOutputColumn = new TextColumn<Entry<OutputAddress, OutputDescription>>() {

			@Override
			public String getValue(final Entry<OutputAddress, OutputDescription> object) {
				return object.getValue().getConnectionLabel();
			}
		};
		outputTable.addColumn(labelOutputColumn, "Label");

		final TextColumn<Entry<OutputAddress, OutputDescription>> connectionOutputColumn = new TextColumn<Entry<OutputAddress, OutputDescription>>() {

			@Override
			public String getValue(final Entry<OutputAddress, OutputDescription> object) {
				return labelGenerator.makeLabelForOutputDevice(outputIndex.get(object.getKey()));
			}
		};
		outputTable.addColumn(connectionOutputColumn, "Connection");
		verticalPanel.add(closeButton);
		verticalPanel.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
		communicationService.listInputDevices(new AsyncCallback<Map<InputAddress, InputDescription>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final Map<InputAddress, InputDescription> result) {
				final ArrayList<Entry<InputAddress, InputDescription>> list = new ArrayList<Entry<InputAddress, InputDescription>>(result.entrySet());
				Collections.sort(list, new Comparator<Entry<InputAddress, InputDescription>>() {
					@Override
					public int compare(final Entry<InputAddress, InputDescription> o1, final Entry<InputAddress, InputDescription> o2) {
						final InputAddress key1 = o1.getKey();
						final InputAddress key2 = o2.getKey();
						final int compare = Integer.valueOf(key1.getDeviceAddress()).compareTo(Integer.valueOf(key2.getDeviceAddress()));
						if (compare != 0)
							return compare;
						return Integer.valueOf(key1.getInputAddress()).compareTo(Integer.valueOf(key2.getInputAddress()));
					}
				});
				inputTable.setRowData(list);
			}
		});
		communicationService.listOutputDevices(new AsyncCallback<Map<OutputAddress, OutputDescription>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final Map<OutputAddress, OutputDescription> result) {
				final ArrayList<Entry<OutputAddress, OutputDescription>> list = new ArrayList<Entry<OutputAddress, OutputDescription>>(result.entrySet());
				Collections.sort(list, new Comparator<Entry<OutputAddress, OutputDescription>>() {
					@Override
					public int compare(final Entry<OutputAddress, OutputDescription> o1, final Entry<OutputAddress, OutputDescription> o2) {
						final OutputAddress key1 = o1.getKey();
						final OutputAddress key2 = o2.getKey();
						final int compare = Integer.valueOf(key1.getDeviceAddress()).compareTo(Integer.valueOf(key2.getDeviceAddress()));
						if (compare != 0)
							return compare;
						return Integer.valueOf(key1.getOutputAddress()).compareTo(Integer.valueOf(key2.getOutputAddress()));
					}
				});
				outputTable.setRowData(list);
			}
		});
		inputTable.addRowCountChangeHandler(new RowCountChangeEvent.Handler() {

			@Override
			public void onRowCountChange(final RowCountChangeEvent event) {
				center();
			}
		});
	}

}
