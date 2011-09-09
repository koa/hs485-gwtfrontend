/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputType;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/** 
 *
 */
public class InputConnectorEditorTable extends AbstractFullTableEditor<InputConnector> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.
	 * AbstractFullTableEditor#addEntryString()
	 */
	@Override
	protected String addEntryString() {
		return "add Connector";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.
	 * AbstractFullTableEditor
	 * #fillValueColumns(com.google.gwt.user.cellview.client.CellTable)
	 */
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

		final ArrayList<String> options = new ArrayList<String>();
		for (final InputType inputType : InputType.values())
			options.add(inputType.name());

		final Column<InputConnector, String> typeColumn = new Column<InputConnector, String>(new SelectionCell(options)) {

			@Override
			public String getValue(final InputConnector object) {
				if (object.getType() == null)
					object.setType(InputType.PUSH);
				return object.getType().name();
			}
		};
		typeColumn.setFieldUpdater(new FieldUpdater<InputConnector, String>() {

			@Override
			public void update(final int index, final InputConnector object, final String value) {
				object.setType(InputType.valueOf(value));
			}
		});
		table.addColumn(typeColumn, "Connector Type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.
	 * AbstractFullTableEditor#newEmptyEntry()
	 */
	@Override
	protected InputConnector newEmptyEntry() {
		final InputConnector inputConnector = new InputConnector();
		inputConnector.setConnectorName("Connector " + entries.size());
		inputConnector.setType(InputType.PUSH);
		return inputConnector;
	}
}
