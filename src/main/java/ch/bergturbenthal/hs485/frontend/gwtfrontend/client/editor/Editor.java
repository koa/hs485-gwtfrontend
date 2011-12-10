package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Editor implements EntryPoint {
	private final ConfigServiceAsync	configService	= ConfigServiceAsync.Util.getInstance();

	@Override
	public void onModuleLoad() {
		final RootPanel rootPanel = RootPanel.get("main");
		final FloorComposite floor = new FloorComposite();
		rootPanel.add(floor);
		configService.readPlan("plan", new AsyncCallback<Plan>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(final Plan result) {
				floor.setCurrentFloor(result.getFloors().get(0));
			}
		});
	}

}
