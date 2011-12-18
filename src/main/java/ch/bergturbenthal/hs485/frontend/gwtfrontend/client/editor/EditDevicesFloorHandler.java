package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorEventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;

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
import com.google.gwt.user.client.ui.PopupPanel;

public final class EditDevicesFloorHandler implements FloorEventHandler {
	private int						clientX;
	private int						clientY;
	private Runnable			iconUpdater	= null;
	private InputDevice		inputDevice	= null;
	private float					originalX;
	private float					originalY;
	private OutputDevice	outputDevice;
	private float					scale;

	public EditDevicesFloorHandler() {
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
			menuBar.addItem("edit ...", new Command() {

				@Override
				public void execute() {
					inputDevicePopupPanel.hide();
					final EditInputDevice editInputDevice = new EditInputDevice(inputDevice);
					editInputDevice.center();
				}
			});
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
			final PopupPanel inputDevicePopupPanel = new PopupPanel(true);
			final MenuBar menuBar = new MenuBar(true);
			menuBar.addItem("edit ...", new Command() {

				@Override
				public void execute() {
					inputDevicePopupPanel.hide();
					final EditOutputDevice editOutputDevice = new EditOutputDevice(outputDevice);
					editOutputDevice.addCloseHandler(new CloseHandler<PopupPanel>() {

						@Override
						public void onClose(final CloseEvent<PopupPanel> event) {
							iconUpdater.run();
						}
					});
					editOutputDevice.center();
				}
			});
			inputDevicePopupPanel.add(menuBar);
			inputDevicePopupPanel.setPopupPosition(event.getClientX(), event.getClientY());
			inputDevicePopupPanel.show();
			event.stopPropagation();
			event.preventDefault();
			break;
		}
	}

	private void stopDragging() {
		inputDevice = null;
		outputDevice = null;
		iconUpdater = null;
	}
}