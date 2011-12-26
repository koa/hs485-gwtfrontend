package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.SelectPlanDialog.PlanSelectedHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui.WaitIndicator;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.uploader.FileUploadDialog;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
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
	Button														addFloorButton;
	@UiField
	Button														addInputDeviceButton;
	@UiField
	Button														addOutputDeviceButton;
	@UiField
	Button														cancelButton;
	@UiField
	MenuItem													newPlanItem;
	@UiField
	MenuItem													openPlanItem;
	@UiField
	Button														removeFloorButton;
	@UiField
	Button														savePlanButton;
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

	public PlanEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		showFloorComposite.addFloorEventHandler(new EditDevicesFloorHandler(new Runnable() {
			@Override
			public void run() {
				updateFloorList();
			}
		}));
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
		showFloorComposite.setCurrentPlan(plan);
		updateFloorList();
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

	@UiHandler("addFloorButton")
	void onAddFloorButtonClick(final ClickEvent event) {
		final EditFloorPropertiesDialog inputFloorPropertiesDialog = new EditFloorPropertiesDialog();
		inputFloorPropertiesDialog.setFloor(null);
		inputFloorPropertiesDialog.setCloseRunnable(new Runnable() {
			@Override
			public void run() {
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

	@UiHandler("selectFloorList")
	void onSelectFloorListChange(final ChangeEvent event) {
		final Floor floor = plan.getFloors().get(selectFloorList.getSelectedIndex());
		if (floor != null)
			showFloorComposite.setCurrentFloor(floor);
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
