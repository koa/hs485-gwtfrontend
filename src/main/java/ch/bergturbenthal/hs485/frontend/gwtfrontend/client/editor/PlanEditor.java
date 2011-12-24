package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlanEditor extends Composite {

	interface PlanEditorUiBinder extends UiBinder<Widget, PlanEditor> {
	}

	private static PlanEditorUiBinder	uiBinder	= GWT.create(PlanEditorUiBinder.class);
	@UiField
	Button														addFloorButton;
	@UiField
	Button														addInputDeviceButton;
	@UiField
	Button														addOutputDeviceButton;
	@UiField
	Button														cancelButton;
	@UiField
	Button														removeFloorButton;
	@UiField
	Button														savePlanButton;
	@UiField
	ListBox														selectFloorList;
	@UiField
	FloorComposite										showFloorComposite;
	private Plan											plan;

	public PlanEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		showFloorComposite.addFloorEventHandler(new EditDevicesFloorHandler(new Runnable() {
			@Override
			public void run() {
				updateFloorList();
			}
		}));
	}

	public void setCurrentPlan(final Plan plan) {
		this.plan = plan;
		showFloorComposite.setCurrentPlan(plan);
		updateFloorList();
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
