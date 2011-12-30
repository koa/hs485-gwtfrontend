/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.AbstractFullTableEditor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/** 
 *
 */
public class InputConnectorEditorTable extends AbstractFullTableEditor<InputConnector> {
	private SelectInputComposite	selectKeyComposite;

	public InputConnectorEditorTable() {
	}

	public void setPlan(final Plan plan) {
		selectKeyComposite.setPlan(plan);
	}

	public void setSelectKeyComposite(final SelectInputComposite selectKeyComposite) {
		this.selectKeyComposite = selectKeyComposite;
	}

	@Override
	protected String addEntryString() {
		return "add Connector";
	}

	@Override
	protected void fillValueColumns(final CellTable<InputConnector> table) {
		final Column<InputConnector, String> titleColumn = new Column<InputConnector, String>(new EditTextCell()) {
			@Override
			public String getValue(final InputConnector object) {
				return object.getConnectorName();
			}
		};
		titleColumn.setFieldUpdater(new FieldUpdater<InputConnector, String>() {

			public void update(final int index, final InputConnector object, final String value) {
				object.setConnectorName(value);
				setEntityModified(object);
			}
		});
		table.addColumn(titleColumn, "Name");

		final List<String> deviceTypes = new ArrayList<String>();
		for (final InputDeviceType type : InputDeviceType.values())
			deviceTypes.add(type.name());
		final Column<InputConnector, String> typeColumn = new Column<InputConnector, String>(new SelectionCell(deviceTypes)) {

			@Override
			public String getValue(final InputConnector object) {
				return object.getType().name();
			}
		};
		typeColumn.setFieldUpdater(new FieldUpdater<InputConnector, String>() {

			@Override
			public void update(final int index, final InputConnector object, final String value) {
				object.setType(InputDeviceType.valueOf(value));
				setEntityModified(object);
			}
		});
		table.addColumn(typeColumn, "Type");

		final Column<InputConnector, String> addressColumn = new Column<InputConnector, String>(new ButtonCell()) {

			@Override
			public String getValue(final InputConnector object) {
				final InputAddress address = object.getAddress();
				if (address == null)
					return "-";
				return address.toString();
			}
		};
		addressColumn.setFieldUpdater(new FieldUpdater<InputConnector, String>() {

			@Override
			public void update(final int index, final InputConnector object, final String value) {
				object.setAddress(selectKeyComposite.getSelectedAddress());
				setEntityModified(object);
				table.redraw();
			}
		});
		table.addColumn(addressColumn, "Address");
	}

	@Override
	protected InputConnector newEmptyEntry() {
		final InputConnector inputConnector = new InputConnector();
		inputConnector.setType(InputDeviceType.SWITCH);
		inputConnector.setConnectorName("Connector " + entries.size());
		return inputConnector;
	}
}
