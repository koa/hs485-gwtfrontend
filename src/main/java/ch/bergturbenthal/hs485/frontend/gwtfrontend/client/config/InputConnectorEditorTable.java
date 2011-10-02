/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/** 
 *
 */
public class InputConnectorEditorTable extends AbstractFullTableEditor<InputConnector> {
	private SelectInputComposite	selectKeyComposite;

	public InputConnectorEditorTable() {
	}

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
		return inputConnector;
	}

	public void setSelectKeyComposite(final SelectInputComposite selectKeyComposite) {
		this.selectKeyComposite = selectKeyComposite;
	}
}
