package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ui.WaitIndicator;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectPlanDialog extends DialogBox {
	public static interface PlanSelectedHandler {
		void planSelected(Plan selectedPlan);
	}

	protected int											savedEntriesCount	= 0;
	private final ConfigServiceAsync	configService			= ConfigServiceAsync.Util.getInstance();
	private final ListBox							listBox;

	public SelectPlanDialog(final PlanSelectedHandler selectedHandler) {
		setText("select a Plan from Server");

		final VerticalPanel verticalPanel = new VerticalPanel();
		setWidget(verticalPanel);
		verticalPanel.setSize("100%", "100%");

		listBox = new ListBox();
		verticalPanel.add(listBox);
		listBox.setWidth("100%");
		listBox.setVisibleItemCount(5);

		final Grid grid = new Grid(1, 2);
		verticalPanel.add(grid);
		grid.setWidth("100%");

		final Button btnCancel = new Button("Cancel");
		grid.setWidget(0, 0, btnCancel);
		btnCancel.setWidth("100%");
		btnCancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});

		final Button btnOk = new Button("Ok");
		btnOk.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();
				if (selectedHandler != null) {
					final int selectedIndex = listBox.getSelectedIndex();
					if (savedEntriesCount > selectedIndex) {
						WaitIndicator.showWait();
						configService.readPlan(listBox.getValue(selectedIndex), new AsyncCallback<Plan>() {

							@Override
							public void onFailure(final Throwable caught) {
								WaitIndicator.hideWait();
							}

							@Override
							public void onSuccess(final Plan result) {
								WaitIndicator.hideWait();
								selectedHandler.planSelected(result);
							}
						});
					} else {
						final Plan plan = new Plan();
						plan.setName(listBox.getValue(selectedIndex));
						plan.setPlanId(null);
						selectedHandler.planSelected(plan);
					}
				}
			}
		});
		grid.setWidget(0, 1, btnOk);
		btnOk.setWidth("100%");
		configService.listAllPlans(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(final Throwable caught) {
			}

			@Override
			public void onSuccess(final Map<String, String> result) {
				savedEntriesCount = result.size();
				listBox.clear();
				for (final Entry<String, String> planEntry : result.entrySet())
					listBox.addItem(planEntry.getValue(), planEntry.getKey());
			}
		});
	}
}
