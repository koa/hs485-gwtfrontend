package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.plan.FloorComposite;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Main extends Composite {

	interface MainUiBinder extends UiBinder<Widget, Main> {
	}

	private static MainUiBinder				uiBinder			= GWT.create(MainUiBinder.class);

	@UiField
	FloorComposite										floorComposite;
	private final ConfigServiceAsync	configService	= ConfigServiceAsync.Util.getInstance();

	public Main() {
		initWidget(uiBinder.createAndBindUi(this));
		configService.readPlan("plan", new AsyncCallback<Plan>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(final Plan result) {
				floorComposite.setCurrentFloor(result.getFloors().get(0));
			}
		});
	}

}
