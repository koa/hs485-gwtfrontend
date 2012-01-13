package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.frontend;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorEventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class FrontendComposite extends Composite {
	private final FloorComposite						floorComposite;
	private final CellList<Floor>						floorList;
	private final CommunicationServiceAsync	communicationService	= CommunicationServiceAsync.Util.getInstance();

	public FrontendComposite() {

		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		initWidget(dockLayoutPanel);

		floorList = new CellList<Floor>(new AbstractCell<Floor>() {
			@Override
			public void render(final Context context, final Floor floor, final SafeHtmlBuilder sb) {
				sb.appendEscaped(floor.getName());
			}
		});
		final SingleSelectionModel<Floor> selectionModel = new SingleSelectionModel<Floor>();
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

			@Override
			public void onSelectionChange(final SelectionChangeEvent event) {
				floorComposite.setCurrentFloor(selectionModel.getSelectedObject());
			}
		});
		floorList.setSelectionModel(selectionModel);
		dockLayoutPanel.addWest(floorList, 7.7);

		floorComposite = new FloorComposite();
		dockLayoutPanel.add(floorComposite);

		floorComposite.addFloorEventHandler(new FloorEventHandler() {
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
				final OutputAddress address = outputDevice.getAddress();
				if (address != null)
					communicationService.getOutputSwitchState(address, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(final Throwable caught) {
							GWT.log("Cannot read switch-State: ", caught);
						}

						@Override
						public void onSuccess(final Boolean result) {
							communicationService.setOutputSwitchState(address, !result.booleanValue(), new AsyncCallback<Void>() {

								@Override
								public void onFailure(final Throwable caught) {
									GWT.log("Cannot toggle", caught);
								}

								@Override
								public void onSuccess(final Void result) {
									// TODO Auto-generated method stub

								}
							});
						}
					});
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
		});

	}

	public void setPlan(final Plan plan) {
		floorComposite.setCurrentPlan(plan);
		floorList.setRowData(plan.getFloors());
		floorList.getSelectionModel().setSelected(plan.getFloors().get(0), true);
	}
}
