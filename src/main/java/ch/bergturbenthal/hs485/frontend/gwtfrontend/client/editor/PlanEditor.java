package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.EditDevicesFloorHandler.CurrentConnectionHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.SelectPlanDialog.PlanSelectedHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event.EventSourceConfigPanel;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event.EventSourcePanelBuilder;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event.EventTypeManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event.LabelGenerator;
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
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.Action;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlanEditor extends Composite {

	interface PlanEditorUiBinder extends UiBinder<Widget, PlanEditor> {
	}

	private static PlanEditorUiBinder																	uiBinder				= GWT.create(PlanEditorUiBinder.class);
	@UiField
	Button																														addConnectionButton;
	@UiField
	Button																														addFloorButton;
	@UiField
	Button																														addInputDeviceButton;
	@UiField
	Button																														addOutputDeviceButton;
	@UiField
	ListBox																														actionList;
	@UiField
	MenuItem																													editIconsetsItem;
	@UiField
	MenuItem																													editPlanPropertiesItem;
	@UiField
	MenuItem																													loadExistingConnctionsMenuItem;
	@UiField
	MenuItem																													newPlanItem;
	@UiField
	MenuItem																													openPlanItem;
	@UiField
	Label																															planNameLabel;
	@UiField
	Button																														removeConnectionButton;
	@UiField
	Button																														removeFloorButton;
	@UiField
	MenuItem																													savePlanItem;
	@UiField
	ListBox																														selectFloorList;
	@UiField
	FloorComposite																										showFloorComposite;
	@UiField
	MenuItem																													uploadFilesItem;
	@UiField
	ListBox																														eventTypeListBox;
	@UiField
	VerticalPanel																											inputConfigPanel;
	@UiField
	VerticalPanel																											outputConfigPanel;
	@UiField
	ListBox																														addInputConnectorList;
	@UiField
	Button																														addInputConnectorButton;
	private final ConfigServiceAsync																	configService		= ConfigServiceAsync.Util.getInstance();

	private Plan																											plan;
	private Action																										selectedAction;
	private final LabelGenerator																			labelGenerator	= new LabelGenerator() {

																																											@Override
																																											public String makeLabelForInputConnector(
																																													final InputConnector inputConnector) {
																																												if (inputConnector == null)
																																													return "<unset>";
																																												else {
																																													final InputDevice foundDevice = findInputDeviceOfConnector(inputConnector);
																																													if (foundDevice == null)
																																														return "<orphan>";
																																													else
																																														return foundDevice.getName() + ": "
																																																+ inputConnector.getConnectorName();
																																												}
																																											}

																																											@Override
																																											public String makeLabelForOutputDevice(
																																													final OutputDevice outputDevice) {
																																												if (outputDevice == null)
																																													return "<unset>";
																																												else
																																													return outputDevice.getName();
																																											}
																																										};
	private EventTypeManager																					actionComponentPanelBuilder;
	private List<String>																							eventTypes;
	private List<EventSourcePanelBuilder<Event, EventSource<Event>>>	panelsForCurrentEvent;
	private List<EventSourceConfigPanel>															visibleInputPanels;

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
			public boolean canHandleInputconnector(final InputConnector inputConnector) {
				if (selectedAction == null)
					return false;
				for (final EventSourceConfigPanel sourcePanel : visibleInputPanels)
					if (sourcePanel.canReceiveInputConnector(inputConnector))
						return true;
				return false;
			}

			@Override
			public boolean canHandleOutputDevice(final OutputDevice outputDevice) {
				// if (selectedConnection == null)
				return false;
				// return selectedConnection.canHandleOutputDevice(outputDevice);
			}

			@Override
			public boolean hasCurrentConnection() {
				return selectedAction != null;
			}

			@Override
			public void setInputConnector(final InputConnector inputConnector) {
				if (selectedAction == null)
					return;
				for (final EventSourceConfigPanel sourcePanel : visibleInputPanels)
					if (sourcePanel.canReceiveInputConnector(inputConnector))
						sourcePanel.takeInputConnector(inputConnector);
				updateActionList();
			}

			@Override
			public void setOutputDevice(final OutputDevice outputDevice) {
				updateActionList();
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
		editPlanPropertiesItem.setCommand(new Command() {

			@Override
			public void execute() {
				editPlanProperties();
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
		editIconsetsItem.setCommand(new Command() {

			@Override
			public void execute() {
				new EditIconSetsDialog().center();
			}
		});
		loadExistingConnctionsMenuItem.setCommand(new Command() {

			@Override
			public void execute() {
				WaitIndicator.showWait();
				configService.readExistingConnections(plan, new AsyncCallback<Plan>() {

					@Override
					public void onFailure(final Throwable caught) {
						WaitIndicator.hideWait();
					}

					@Override
					public void onSuccess(final Plan result) {
						setCurrentPlan(result);
						WaitIndicator.hideWait();
					}
				});
			}
		});
		actionComponentPanelBuilder = new EventTypeManager(labelGenerator);
		eventTypes = new ArrayList<String>(actionComponentPanelBuilder.listAvailableEvents());
		for (final String eventClass : eventTypes) {
			final int lastPt = eventClass.lastIndexOf('.');
			eventTypeListBox.addItem(eventClass.substring(lastPt + 1));
		}
		visibleInputPanels = new ArrayList<EventSourceConfigPanel>();
	}

	public void setCurrentPlan(final Plan plan) {
		this.plan = plan;
		fixPlanReferences();
		showFloorComposite.setCurrentPlan(plan);
		updateFloorList();
		updateActionList();
		planNameLabel.setText(plan.getName());
	}

	protected void savePlan() {
		GWT.log("Save");
		WaitIndicator.showWait();
		configService.savePlan(plan, new AsyncCallback<Plan>() {

			@Override
			public void onFailure(final Throwable caught) {
				GWT.log("Error: ", caught);
				// TODO Auto-generated method stub
				WaitIndicator.hideWait();
			}

			@Override
			public void onSuccess(final Plan result) {
				GWT.log("Ok");
				setCurrentPlan(result);
				WaitIndicator.hideWait();
			}
		});
	}

	@UiHandler("actionList")
	void onActionListChange(final ChangeEvent event) {
		final int selectedIndex = actionList.getSelectedIndex();
		final List<Action> actions = plan.getActions();
		if (selectedIndex < 0 || selectedIndex >= actions.size())
			selectedAction = null;
		else
			selectedAction = actions.get(selectedIndex);
		highlightSelectedConnection();
		if (selectedAction == null)
			return;
		final int actionIndex = eventTypes.indexOf(selectedAction.getEventType());
		eventTypeListBox.setSelectedIndex(actionIndex);
		panelsForCurrentEvent = new ArrayList<EventSourcePanelBuilder<Event, EventSource<Event>>>(
				actionComponentPanelBuilder.listInputPanelsForEvent(selectedAction.getEventType()));
		addInputConnectorList.clear();
		for (final EventSourcePanelBuilder<Event, EventSource<Event>> panelBuilder : panelsForCurrentEvent)
			addInputConnectorList.addItem(panelBuilder.getName());
		updateInputList();
	}

	@UiHandler("addConnectionButton")
	void onAddConnectionButtonClick(final ClickEvent event) {
		final Action action = new Action();
		action.setEventType(KeyEvent.class.getName());
		plan.getActions().add(action);
		updateActionList();
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

	@UiHandler("addInputConnectorButton")
	void onAddInputConnectorButtonClick(final ClickEvent event) {
		final int selectedIndex = addInputConnectorList.getSelectedIndex();
		if (selectedIndex < 0 || selectedIndex >= panelsForCurrentEvent.size())
			return;
		final EventSourcePanelBuilder<Event, EventSource<Event>> panelBuilder = panelsForCurrentEvent.get(selectedIndex);
		selectedAction.getSources().add(panelBuilder.makeNewEventSource());
		updateInputList();
	}

	@UiHandler("addInputDeviceButton")
	void onAddInputDeviceButtonClick(final ClickEvent event) {
		final Floor currentFloor = showFloorComposite.getCurrentFloor();
		if (currentFloor == null)
			return;
		final EditInputDevice editInputDevice = new EditInputDevice();
		editInputDevice.setPlan(plan);
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
		editOutputDevice.setPlan(plan);
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

	@UiHandler("removeConnectionButton")
	void onRemoveConnectionButtonClick(final ClickEvent event) {
		if (selectedAction != null)
			plan.getActions().remove(selectedAction);
		selectedAction = null;
		updateActionList();
	}

	@UiHandler("selectFloorList")
	void onSelectFloorListChange(final ChangeEvent event) {
		final Floor floor = plan.getFloors().get(selectFloorList.getSelectedIndex());
		if (floor != null)
			showFloorComposite.setCurrentFloor(floor);
	}

	private void editPlanProperties() {
		final EditPlanPropertiesDialog editPlanPropertiesDialog = new EditPlanPropertiesDialog();
		editPlanPropertiesDialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(final CloseEvent<PopupPanel> event) {
				setCurrentPlan(editPlanPropertiesDialog.getCurrentPlan());
			}
		});
		editPlanPropertiesDialog.setCurrentPlan(plan);
		editPlanPropertiesDialog.center();
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
		for (final Action action : plan.getActions()) {
			for (final EventSource<?> source : action.getSources())
				actionComponentPanelBuilder.fixReferences(source, inputConnectors, outputDevices);
			for (final EventSink sink : action.getSinks()) {

			}
		}
	}

	private void highlightSelectedConnection() {
		final List<SelectableIcon> selectedIcons = new ArrayList<SelectableIcon>();
		if (selectedAction != null) {
			final Collection<EventSource<?>> sources = selectedAction.getSources();
			for (final EventSource source : sources) {
				final Collection<InputConnector> connectors = actionComponentPanelBuilder.inputConnectorsOf(source);
				for (final InputConnector inputConnector : connectors)
					selectedIcons.add(findInputDeviceOfConnector(inputConnector));
			}
		}
		showFloorComposite.setSelectedIcons(selectedIcons);
	}

	private void newPlan() {
		final EditPlanPropertiesDialog editPlanPropertiesDialog = new EditPlanPropertiesDialog();
		editPlanPropertiesDialog.addCloseHandler(new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(final CloseEvent<PopupPanel> event) {
				final Plan currentPlan = editPlanPropertiesDialog.getCurrentPlan();
				if (currentPlan != null)
					setCurrentPlan(currentPlan);
			}
		});
		editPlanPropertiesDialog.center();
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

	private void updateActionList() {
		if (plan == null)
			return;
		actionList.clear();
		for (final Action connection : plan.getActions()) {
			final StringBuffer connectionLabel = new StringBuffer();
			final Collection<EventSource<?>> sources = connection.getSources();
			if (sources.size() == 0)
				connectionLabel.append("<unset>");
			else
				for (final EventSource eventSource : sources) {
					connectionLabel.append(actionComponentPanelBuilder.describeEventSource(eventSource));
					connectionLabel.append(" ");
				}
			actionList.addItem(connectionLabel.toString());
			if (connection == selectedAction)
				actionList.setSelectedIndex(actionList.getItemCount() - 1);
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

	private void updateInputList() {
		inputConfigPanel.clear();
		visibleInputPanels.clear();
		for (final EventSource source : selectedAction.getSources()) {
			final EventSourcePanelBuilder builder = actionComponentPanelBuilder.getBuilderFor(source.getClass());
			final EventSourceConfigPanel panel = builder.buildPanel();
			panel.setEventSource(source);
			final HorizontalPanel horizontalPanel = new HorizontalPanel();
			horizontalPanel.setWidth("100%");
			horizontalPanel.add(panel);
			final Button removeButton = new Button("X");
			removeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					selectedAction.getSources().remove(source);
					updateInputList();
					updateActionList();
				}
			});
			horizontalPanel.add(removeButton);
			inputConfigPanel.add(horizontalPanel);
			visibleInputPanels.add(panel);
		}
	}
}
