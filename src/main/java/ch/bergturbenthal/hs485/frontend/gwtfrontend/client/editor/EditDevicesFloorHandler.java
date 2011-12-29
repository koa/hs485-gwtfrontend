package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorEventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui.ConfirmationCallback;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui.ConfirmationDialog;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public class EditDevicesFloorHandler implements FloorEventHandler {
	public static interface CurrentConnectionHandler {
		boolean canHandleInputconnector(InputConnector inputConnector);

		boolean canHandleOutputDevice(OutputDevice outputDevice);

		boolean hasCurrentConnection();

		void setInputConnector(InputConnector inputConnector);

		void setOutputDevice(OutputDevice outputDevice);
	}

	private int												clientX;
	private int												clientY;
	private CurrentConnectionHandler	currentConnectionHandler	= new CurrentConnectionHandler() {

																																@Override
																																public boolean canHandleInputconnector(final InputConnector inputConnector) {
																																	return false;
																																}

																																@Override
																																public boolean canHandleOutputDevice(final OutputDevice outputDevice) {
																																	// TODO
																																	// Auto-generated
																																	// method stub
																																	return false;
																																}

																																@Override
																																public boolean hasCurrentConnection() {
																																	return false;
																																}

																																@Override
																																public void setInputConnector(final InputConnector inputConnector) {
																																}

																																@Override
																																public void setOutputDevice(final OutputDevice outputDevice) {
																																}

																															};
	private Floor											currentFloor;
	private Plan											currentPlan;
	private Runnable									fullRedrawRunnable;
	private Runnable									iconUpdater								= null;
	private InputDevice								inputDevice								= null;
	private float											originalX;
	private float											originalY;
	private OutputDevice							outputDevice;
	private float											scale;
	private final Runnable						updateRunnable;

	public EditDevicesFloorHandler(final Runnable updateRunnable) {
		this.updateRunnable = updateRunnable;
	}

	@Override
	public void onInputDeviceClick(final ClickEvent event, final InputDevice inputDevice, final float scale, final Runnable iconUpdater) {
	}

	@Override
	public void onInputDeviceMouseDown(final MouseDownEvent event, final InputDevice inputDevice, final float scale, final Runnable iconUpdater) {
		switch (event.getNativeButton()) {
		case NativeEvent.BUTTON_LEFT:
			this.inputDevice = inputDevice;
			this.scale = scale;
			this.iconUpdater = iconUpdater;
			clientX = event.getClientX();
			clientY = event.getClientY();
			originalX = inputDevice.getPosition().getX();
			originalY = inputDevice.getPosition().getY();
			break;
		case NativeEvent.BUTTON_RIGHT:
			final PopupPanel inputDevicePopupPanel = new PopupPanel(true);
			final MenuBar menuBar = new MenuBar(true);
			menuBar.addItem("remove input device", new Command() {
				@Override
				public void execute() {
					inputDevicePopupPanel.hide();
					new ConfirmationDialog("Are you sure to delete input device " + inputDevice.getName() + "?", new ConfirmationCallback() {

						@Override
						public void onConfirm() {
							currentFloor.getInputDevices().remove(inputDevice);
							fullRedrawRunnable.run();
						}

						@Override
						public void onDecline() {
							// do nothing
						}
					}).center();
				}
			});
			menuBar.addItem("edit input device...", new Command() {
				@Override
				public void execute() {
					inputDevicePopupPanel.hide();
					final EditInputDevice editInputDevice = new EditInputDevice();
					editInputDevice.setInputDevice(inputDevice);
					editInputDevice.center();
				}
			});
			final MenuBar subMenu = new MenuBar(true);
			boolean inputDeviceAdded = false;
			for (final InputConnector inputConnector : inputDevice.getConnectors())
				if (currentConnectionHandler.canHandleInputconnector(inputConnector)) {
					inputDeviceAdded = true;
					subMenu.addItem(inputConnector.getConnectorName(), new Command() {
						@Override
						public void execute() {
							inputDevicePopupPanel.hide();
							currentConnectionHandler.setInputConnector(inputConnector);
						}
					});
				}
			if (inputDeviceAdded)
				menuBar.addItem(new MenuItem("Inputdevice of Connection", false, subMenu));
			inputDevicePopupPanel.add(menuBar);
			inputDevicePopupPanel.setPopupPosition(event.getClientX(), event.getClientY());
			inputDevicePopupPanel.show();
			event.stopPropagation();
			event.preventDefault();
			break;
		}
	}

	@Override
	public void onMouseMove(final MouseMoveEvent event) {
		if (iconUpdater == null)
			return;
		final int xOffset = event.getClientX() - clientX;
		final int yOffset = event.getClientY() - clientY;
		final float newX = originalX + xOffset / scale;
		final float newY = originalY + yOffset / scale;
		if (inputDevice != null) {
			inputDevice.getPosition().setX(newX);
			inputDevice.getPosition().setY(newY);
		} else if (outputDevice != null) {
			outputDevice.getPosition().setX(newX);
			outputDevice.getPosition().setY(newY);
		}
		iconUpdater.run();
	}

	@Override
	public void onMouseOut(final MouseOutEvent event) {
		stopDragging();
	}

	@Override
	public void onMouseUp(final MouseUpEvent event) {
		stopDragging();
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			final PopupPanel inputDevicePopupPanel = new PopupPanel(true);
			final MenuBar menuBar = new MenuBar(true);
			menuBar.addItem("edit plan...", new Command() {

				@Override
				public void execute() {
					inputDevicePopupPanel.hide();
					final EditFloorPropertiesDialog dialog = new EditFloorPropertiesDialog();
					dialog.setFloor(currentFloor);
					dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
						@Override
						public void onClose(final CloseEvent<PopupPanel> event) {
							if (fullRedrawRunnable != null)
								fullRedrawRunnable.run();
							if (updateRunnable != null)
								updateRunnable.run();
						}
					});
					dialog.center();
				}
			});
			inputDevicePopupPanel.add(menuBar);
			inputDevicePopupPanel.setPopupPosition(event.getClientX(), event.getClientY());
			inputDevicePopupPanel.show();
			event.stopPropagation();
			event.preventDefault();
		}
	}

	@Override
	public void onOutputDeviceClick(final ClickEvent event, final OutputDevice outputDevice, final float scale, final Runnable iconUpdater) {
	}

	@Override
	public void onOutputDeviceMouseDown(final MouseDownEvent event, final OutputDevice outputDevice, final float scale, final Runnable iconUpdater) {
		switch (event.getNativeButton()) {
		case NativeEvent.BUTTON_LEFT:

			this.outputDevice = outputDevice;
			this.scale = scale;
			this.iconUpdater = iconUpdater;
			clientX = event.getClientX();
			clientY = event.getClientY();
			originalX = outputDevice.getPosition().getX();
			originalY = outputDevice.getPosition().getY();
			break;
		case NativeEvent.BUTTON_RIGHT:
			final PopupPanel outputDevicePopupPanel = new PopupPanel(true);
			final MenuBar menuBar = new MenuBar(true);
			outputDevicePopupPanel.hide();
			menuBar.addItem("remove input device", new Command() {
				@Override
				public void execute() {
					outputDevicePopupPanel.hide();
					new ConfirmationDialog("Are you sure to delete output device " + outputDevice.getName() + "?", new ConfirmationCallback() {

						@Override
						public void onConfirm() {
							currentFloor.getOutputDevices().remove(outputDevice);
							fullRedrawRunnable.run();
						}

						@Override
						public void onDecline() {
							// do nothing
						}
					}).center();
				}
			});
			menuBar.addItem("edit output device...", new Command() {
				@Override
				public void execute() {
					outputDevicePopupPanel.hide();
					final EditOutputDevice editOutputDevice = new EditOutputDevice();
					editOutputDevice.setPlan(currentPlan);
					editOutputDevice.setOutputDevice(outputDevice);
					editOutputDevice.addCloseHandler(new CloseHandler<PopupPanel>() {

						@Override
						public void onClose(final CloseEvent<PopupPanel> event) {
							iconUpdater.run();
						}
					});
					editOutputDevice.center();
				}
			});
			if (currentConnectionHandler.canHandleOutputDevice(outputDevice))
				menuBar.addItem("Outputdevice of Connection", new Command() {

					@Override
					public void execute() {
						currentConnectionHandler.setOutputDevice(outputDevice);
						outputDevicePopupPanel.hide();
					}
				});

			outputDevicePopupPanel.add(menuBar);
			outputDevicePopupPanel.setPopupPosition(event.getClientX(), event.getClientY());
			outputDevicePopupPanel.show();
			event.stopPropagation();
			event.preventDefault();
			break;
		}
	}

	public void setCurrentConnectionHandler(final CurrentConnectionHandler currentConnectionHandler) {
		this.currentConnectionHandler = currentConnectionHandler;
	}

	@Override
	public void setCurrentFloor(final Floor floor) {
		currentFloor = floor;
	}

	@Override
	public void setCurrentPlan(final Plan plan) {
		currentPlan = plan;
	}

	@Override
	public void setFullRedrawRunnable(final Runnable runnable) {
		fullRedrawRunnable = runnable;
	}

	private void stopDragging() {
		inputDevice = null;
		outputDevice = null;
		iconUpdater = null;
		if (updateRunnable != null)
			updateRunnable.run();
	}

}