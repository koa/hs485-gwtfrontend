package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.Collections;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class EditPlanPropertiesDialog extends DialogBox {
	protected List<IconSet>						availableIconSets	= Collections.emptyList();
	private final ConfigServiceAsync	configService			= ConfigServiceAsync.Util.getInstance();
	private Plan											currentPlan;
	private final ListBox							listBox;
	private final IntegerBox					maximumOnTimeIntegerBox;
	private final TextBox							planNameTextBox;

	public EditPlanPropertiesDialog() {
		setText("Edit Plan Properties");

		final FlexTable flexTable = new FlexTable();
		setWidget(flexTable);
		flexTable.setSize("100%", "100%");

		flexTable.setWidget(0, 0, new Label("Name"));

		planNameTextBox = new TextBox();
		planNameTextBox.setVisibleLength(10);
		planNameTextBox.setText("");
		flexTable.setWidget(0, 1, planNameTextBox);

		flexTable.setWidget(1, 0, new Label("Icon Set"));

		listBox = new ListBox();
		flexTable.setWidget(1, 1, listBox);
		listBox.setWidth("100%");

		final Label lblMaximumOntime = new Label("Maximum On-Time");
		flexTable.setWidget(2, 0, lblMaximumOntime);

		maximumOnTimeIntegerBox = new IntegerBox();
		maximumOnTimeIntegerBox.setVisibleLength(5);
		flexTable.setWidget(2, 1, maximumOnTimeIntegerBox);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		flexTable.setWidget(3, 0, horizontalPanel);

		final Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		horizontalPanel.add(btnCancel);

		final Button btnSave = new Button("Save");
		btnSave.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				if (currentPlan == null) {
					currentPlan = new Plan();
					currentPlan.setPlanId(null);
				}
				currentPlan.setName(planNameTextBox.getValue());
				currentPlan.setIconSet(availableIconSets.get(listBox.getSelectedIndex()));
				currentPlan.setMaximumOnTime(maximumOnTimeIntegerBox.getValue());
				configService.savePlan(currentPlan, new AsyncCallback<Plan>() {

					@Override
					public void onFailure(final Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(final Plan result) {
						currentPlan = result;
						hide();
					}
				});
			}
		});
		horizontalPanel.add(btnSave);
		flexTable.getFlexCellFormatter().setColSpan(3, 0, 2);
		flexTable.getCellFormatter().setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_RIGHT);

		configService.loadIconSets(new AsyncCallback<List<IconSet>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final List<IconSet> result) {
				availableIconSets = result;
				listBox.clear();
				for (final IconSet iconSet : result) {
					listBox.addItem(iconSet.getName());
					if (currentPlan != null && currentPlan.getIconSet().getIconsetId().equals(iconSet.getIconsetId()))
						listBox.setSelectedIndex(listBox.getItemCount() - 1);
				}
			}
		});
	}

	public Plan getCurrentPlan() {
		return currentPlan;
	}

	public void setCurrentPlan(final Plan plan) {
		currentPlan = plan;
		if (currentPlan != null) {
			maximumOnTimeIntegerBox.setValue(currentPlan.getMaximumOnTime());
			planNameTextBox.setValue(currentPlan.getName());
			if (availableIconSets.size() > 0)
				for (final IconSet iconSet : availableIconSets)
					if (currentPlan.getIconSet().getIconsetId().equals(iconSet.getIconsetId()))
						listBox.setSelectedIndex(listBox.getItemCount() - 1);
		}
	}

}
