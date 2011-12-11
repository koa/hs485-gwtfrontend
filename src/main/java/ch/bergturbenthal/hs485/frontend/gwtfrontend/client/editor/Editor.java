package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorEventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Editor implements EntryPoint {
	private final ConfigServiceAsync	configService	= ConfigServiceAsync.Util.getInstance();

	@Override
	public void onModuleLoad() {
		final RootPanel rootPanel = RootPanel.get("main");
		final FloorComposite floor = new FloorComposite();
		floor.addFloorEventHandler(new FloorEventHandler() {

			private int						clientX;
			private int						clientY;
			private Runnable			iconUpdater	= null;
			private InputDevice		inputDevice	= null;
			private float					originalX;
			private float					originalY;
			private OutputDevice	outputDevice;
			private float					scale;

			@Override
			public void onInputDeviceClick(final ClickEvent event, final InputDevice inputDevice, final float scale, final Runnable iconUpdater) {
				System.out.println("Clicked on " + inputDevice.getName());
				inputDevice.getPosition().setX(inputDevice.getPosition().getX() + 200);
				iconUpdater.run();

			}

			@Override
			public void onInputDeviceMouseDown(final MouseDownEvent event, final InputDevice inputDevice, final float scale, final Runnable iconUpdater) {
				this.inputDevice = inputDevice;
				this.scale = scale;
				this.iconUpdater = iconUpdater;
				clientX = event.getClientX();
				clientY = event.getClientY();
				originalX = inputDevice.getPosition().getX();
				originalY = inputDevice.getPosition().getY();
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
				inputDevice = null;
				outputDevice = null;
				iconUpdater = null;
			}

			@Override
			public void onMouseUp(final MouseUpEvent event) {
				inputDevice = null;
				outputDevice = null;
				iconUpdater = null;
			}

			@Override
			public void onOutputDeviceClick(final ClickEvent event, final OutputDevice outputDevice, final float scale, final Runnable iconUpdater) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onOutputDeviceMouseDown(final MouseDownEvent event, final OutputDevice outputDevice, final float scale, final Runnable iconUpdater) {
				this.outputDevice = outputDevice;
				this.scale = scale;
				this.iconUpdater = iconUpdater;
				clientX = event.getClientX();
				clientY = event.getClientY();
				originalX = outputDevice.getPosition().getX();
				originalY = outputDevice.getPosition().getY();
			}
		});
		rootPanel.add(floor);
		configService.readPlan("plan", new AsyncCallback<Plan>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(final Plan result) {
				floor.setCurrentPlan(result);
			}
		});
	}

}
