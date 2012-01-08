package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.frontend;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorEventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.TabBar;

public class FrontendComposite extends Composite {

	private final DeckPanel					mainPanel;
	private final TabBar						tabBar;
	private final FloorEventHandler	handler;

	public FrontendComposite() {

		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		initWidget(dockLayoutPanel);

		tabBar = new TabBar();
		tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(final SelectionEvent<Integer> event) {
				mainPanel.showWidget(event.getSelectedItem());
			}
		});
		dockLayoutPanel.addNorth(tabBar, 2.0);
		tabBar.addTab("One");
		tabBar.addTab("Two");
		tabBar.addTab("Three");

		mainPanel = new DeckPanel();
		dockLayoutPanel.add(mainPanel);

		handler = new FloorEventHandler() {
			@Override
			public void onInputDeviceClick(final ClickEvent event, final InputDevice inputDevice, final float scale, final Runnable iconUpdater) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onInputDeviceMouseDown(final MouseDownEvent event, final InputDevice inputDevice, final float scale, final Runnable iconUpdater) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMouseMove(final MouseMoveEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMouseOut(final MouseOutEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onMouseUp(final MouseUpEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onOutputDeviceClick(final ClickEvent event, final OutputDevice outputDevice, final float scale, final Runnable iconUpdater) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onOutputDeviceMouseDown(final MouseDownEvent event, final OutputDevice outputDevice, final float scale, final Runnable iconUpdater) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setCurrentFloor(final Floor floor) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setCurrentPlan(final Plan plan) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setFullRedrawRunnable(final Runnable runnable) {
				// TODO Auto-generated method stub

			}
		};

	}

	public void setPlan(final Plan plan) {
		mainPanel.clear();
		while (tabBar.getTabCount() > 0)
			tabBar.removeTab(0);
		// decoratedTabPanel.clear();
		if (plan != null && plan.getFloors().size() > 0) {
			for (final Floor floor : plan.getFloors()) {
				final FloorComposite floorComposite = new FloorComposite();
				floorComposite.addFloorEventHandler(handler);
				tabBar.addTab(floor.getName());
				floorComposite.setCurrentPlan(plan);
				floorComposite.setCurrentFloor(floor);
				mainPanel.add(floorComposite);
			}
			GWT.log("Selected: " + tabBar.selectTab(0, true));
		}
	}
}
