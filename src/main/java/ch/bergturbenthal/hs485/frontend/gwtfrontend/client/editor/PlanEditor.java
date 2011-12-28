package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.EditDevicesFloorHandler.CurrentConnectionHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.SelectPlanDialog.PlanSelectedHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui.WaitIndicator;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.uploader.FileUploadDialog;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.SelectableIcon;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Connection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlanEditor extends Composite {

	interface PlanEditorUiBinder extends UiBinder<Widget, PlanEditor> {
	}

	private static PlanEditorUiBinder	uiBinder			= GWT.create(PlanEditorUiBinder.class);
	@UiField
	Button														addConnectionButton;
	@UiField
	Button														addFloorButton;
	@UiField
	Button														addInputDeviceButton;
	@UiField
	Button														addOutputDeviceButton;
	@UiField
	ListBox														connectionsList;
	@UiField
	MenuItem													newPlanItem;
	@UiField
	MenuItem													openPlanItem;
	@UiField
	Button														removeConnectionButton;
	@UiField
	Button														removeFloorButton;
	@UiField
	MenuItem													savePlanItem;
	@UiField
	ListBox														selectFloorList;
	@UiField
	FloorComposite										showFloorComposite;
	@UiField
	MenuItem													uploadFilesItem;
	private final ConfigServiceAsync	configService	= ConfigServiceAsync.Util.getInstance();

	private Plan											plan;
	private Connection								selectedConnection;

	public PlanEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		final EditDevicesFloorHandler handler = new EditDevicesFloorHandler(new Runnable() {
			@Override
			public void run() {
				updateFloorList();
			}
		});
		handler.setCurrentConnectionHandler(new CurrentConnectionHandler() {

			@Override
			public boolean hasCurrentConnection() {
				return selectedConnection != null;
			}

			@Override
			public void setInputConnector(final InputConnector inputConnector) {
				selectedConnection.setInputConnector(inputConnector);
				updateConnectorList();
			}

			@Override
			public void setOutputDevice(final OutputDevice outputDevice) {
				selectedConnection.setOutputDevice(outputDevice);
				updateConnectorList();
			}
		});
		showFloorComposite.addFloorEventHandler(handler);
		newPlanItem.setCommand(new Command() {

			@Override
			public void execute() {
				newPlan();
			}
		});
		openPlanItem.setCommand(new Command() {

			@Override
			public void execute() {
				openPlan();
			}
		});
		savePlanItem.setCommand(new Command() {

			@Override
			public void execute() {
				savePlan();
			}
		});
		uploadFilesItem.setCommand(new Command() {

			@Override
			public void execute() {
				new FileUploadDialog().center();
			}
		});

	}

	public void setCurrentPlan(final Plan plan) {
		this.plan = plan;
		fixPlanReferences();
		showFloorComposite.setCurrentPlan(plan);
		updateFloorList();
		updateConnectorList();
	}

	protected void savePlan() {
		configService.savePlan(plan, new AsyncCallback<Plan>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final Plan result) {
				// TODO Auto-generated method stub

			}
		});
	}

	@UiHandler("addConnectionButton")
	void onAddConnectionButtonClick(final ClickEvent event) {
		plan.getConnections().add(new Connection());
		updateConnectorList();
	}

	@UiHandler("addFloorButton")
	void onAddFloorButtonClick(final ClickEvent event) {
		final EditFloorPropertiesDialog inputFloorPropertiesDialog = new EditFloorPropertiesDialog();
		inputFloorPropertiesDialog.setFloor(null);
		inputFloorPropertiesDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(final CloseEvent<PopupPanel> event) {
				final Floor floor = inputFloorPropertiesDialog.getFloor();
				if (floor != null) {
					plan.getFloors().add(floor);
					showFloorComposite.setCurrentFloor(floor);
					updateFloorList();
				}
			}
		});
		inputFloorPropertiesDialog.center();
	}

	@UiHandler("addInputDeviceButton")
	void onAddInputDeviceButtonClick(final ClickEvent event) {
		final Floor currentFloor = showFloorComposite.getCurrentFloor();
		if (currentFloor == null)
			return;
		final EditInputDevice editInputDevice = new EditInputDevice();
		editInputDevice.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(final CloseEvent<PopupPanel> event) {
				final InputDevice inputDevice = editInputDevice.getInputDevice();
				if (inputDevice == null)
					return;
				inputDevice.getPosition().setX(currentFloor.getIconSize());
				inputDevice.getPosition().setY(currentFloor.getIconSize());
				currentFloor.getInputDevices().add(inputDevice);
				showFloorComposite.redrawAllIcons();
			}
		});
		editInputDevice.center();
	}

	@UiHandler("addOutputDeviceButton")
	void onAddOutputDeviceButtonClick(final ClickEvent event) {
		final Floor currentFloor = showFloorComposite.getCurrentFloor();
		if (currentFloor == null)
			return;

		final EditOutputDevice editOutputDevice = new EditOutputDevice();
		editOutputDevice.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(final CloseEvent<PopupPanel> event) {
				final OutputDevice outputDevice = editOutputDevice.getOutputDevice();
				if (outputDevice == null)
					return;
				outputDevice.getPosition().setX(currentFloor.getIconSize());
				outputDevice.getPosition().setY(currentFloor.getIconSize());
				currentFloor.getOutputDevices().add(outputDevice);
				showFloorComposite.redrawAllIcons();
			}
		});
		editOutputDevice.center();
	}

	@UiHandler("connectionsList")
	void onConnectionsListChange(final ChangeEvent event) {
		final int selectedIndex = connectionsList.getSelectedIndex();
		final List<Connection> connections = plan.getConnections();
		if (selectedIndex < 0 || selectedIndex >= connections.size())
			selectedConnection = null;
		else
			selectedConnection = connections.get(selectedIndex);
		highlightSelectedConnection();
	}

	@UiHandler("removeConnectionButton")
	void onRemoveConnectionButtonClick(final ClickEvent event) {
		if (selectedConnection != null)
			plan.getConnections().remove(selectedConnection);
		selectedConnection = null;
		updateConnectorList();
	}

	@UiHandler("selectFloorList")
	void onSelectFloorListChange(final ChangeEvent event) {
		final Floor floor = plan.getFloors().get(selectFloorList.getSelectedIndex());
		if (floor != null)
			showFloorComposite.setCurrentFloor(floor);
	}

	private InputDevice findInputDeviceOfConnector(final InputConnector inputConnector) {
		if (inputConnector == null)
			return null;
		InputDevice foundDevice = null;
		for (final Floor floor : plan.getFloors())
			for (final InputDevice inputDevice : floor.getInputDevices())
				if (inputDevice.getConnectors().contains(inputConnector))
					foundDevice = inputDevice;
		return foundDevice;
	}

	private void fixPlanReferences() {
		final Map<String, InputConnector> inputConnectors = new HashMap<String, InputConnector>();
		final Map<String, OutputDevice> outputDevices = new HashMap<String, OutputDevice>();
		for (final Floor floor : plan.getFloors()) {
			for (final InputDevice inputDevice : floor.getInputDevices())
				for (final InputConnector inputConnector : inputDevice.getConnectors())
					if (inputConnector.getConnectorId() != null)
						inputConnectors.put(inputConnector.getConnectorId(), inputConnector);
			for (final OutputDevice outputDevice : floor.getOutputDevices())
				if (outputDevice.getDeviceId() != null)
					outputDevices.put(outputDevice.getDeviceId(), outputDevice);
		}
		for (final Connection connection : plan.getConnections()) {
			final InputConnector inputConnector = connection.getInputConnector();
			if (inputConnector != null && inputConnector.getConnectorId() != null)
				connection.setInputConnector(inputConnectors.get(inputConnector.getConnectorId()));
			final OutputDevice outputDevice = connection.getOutputDevice();
			if (outputDevice != null && outputDevice.getDeviceId() != null)
				connection.setOutputDevice(outputDevices.get(outputDevice.getDeviceId()));
		}
	}

	private void highlightSelectedConnection() {
		final List<SelectableIcon> selectedIcons = new ArrayList<SelectableIcon>(2);
		if (selectedConnection != null) {
			final InputConnector inputConnector = selectedConnection.getInputConnector();
			if (inputConnector != null) {
				final InputDevice foundInputDeviceOfConnector = findInputDeviceOfConnector(inputConnector);
				if (foundInputDeviceOfConnector != null)
					selectedIcons.add(foundInputDeviceOfConnector);
			}
			final OutputDevice outputDevice = selectedConnection.getOutputDevice();
			if (outputDevice != null)
				selectedIcons.add(outputDevice);
		}
		showFloorComposite.setSelectedIcons(selectedIcons);
	}

	private void newPlan() {
		// TODO Auto-generated method stub

	}

	private void openPlan() {
		final SelectPlanDialog selectPlanDialog = new SelectPlanDialog(new PlanSelectedHandler() {

			@Override
			public void planSelected(final Plan selectedPlan) {
				if (selectedPlan.getIconSet() == null) {
					WaitIndicator.showWait();
					configService.loadIconSets(new AsyncCallback<List<IconSet>>() {

						@Override
						public void onFailure(final Throwable caught) {
							WaitIndicator.hideWait();
						}

						@Override
						public void onSuccess(final List<IconSet> result) {
							selectedPlan.setIconSet(result.get(0));
							setCurrentPlan(selectedPlan);
							WaitIndicator.hideWait();
						}
					});
				} else
					setCurrentPlan(selectedPlan);
			}
		});
		selectPlanDialog.center();

	}

	private void updateConnectorList() {
		if (plan == null)
			return;
		connectionsList.clear();
		for (final Connection connection : plan.getConnections()) {
			final StringBuffer connectionLabel = new StringBuffer();
			final InputConnector inputConnector = connection.getInputConnector();
			if (inputConnector == null)
				connectionLabel.append("<unset>");
			else {
				final InputDevice foundDevice = findInputDeviceOfConnector(inputConnector);
				if (foundDevice == null)
					connectionLabel.append("<orphan>");
				else
					connectionLabel.append(foundDevice.getName() + ": " + inputConnector.getConnectorName());
			}
			connectionLabel.append(" -> ");
			final OutputDevice outputDevice = connection.getOutputDevice();
			if (outputDevice == null)
				connectionLabel.append("<unset>");
			else
				connectionLabel.append(outputDevice.getName());
			connectionsList.addItem(connectionLabel.toString());
			if (connection == selectedConnection)
				connectionsList.setSelectedIndex(connectionsList.getItemCount() - 1);
		}
		highlightSelectedConnection();
	}

	private void updateFloorList() {
		if (plan == null)
			return;
		selectFloorList.clear();
		final Floor visibleFloor = showFloorComposite.getCurrentFloor();
		for (final Floor floor : plan.getFloors()) {
			selectFloorList.addItem(floor.getName());
			if (floor == visibleFloor)
				selectFloorList.setSelectedIndex(selectFloorList.getItemCount() - 1);
		}
	}
}
