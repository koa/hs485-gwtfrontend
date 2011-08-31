/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 *
 */
public class FloorEditor extends AbstractFullTableEditor<Floor> {

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.
	 *      AbstractFullTableEditor#addEntryString()
	 */
	@Override
	protected String addEntryString() {
		return messages.addFloor();
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.
	 *      AbstractFullTableEditor
	 *      #fillValueColumns(com.google.gwt.user.cellview.client.CellTable)
	 */
	@Override
	protected void fillValueColumns(final CellTable<Floor> table) {
		final Column<Floor, String> titleColumn = new Column<Floor, String>(new EditTextCell()) {
			@Override
			public String getValue(final Floor object) {
				return object.getName();
			}
		};
		titleColumn.setFieldUpdater(new FieldUpdater<Floor, String>() {

			public void update(final int index, final Floor object, final String value) {
				object.setName(value);
				setEntityModified(object);
			}
		});
		table.addColumn(titleColumn, messages.floor());
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.AbstractFullTableEditor#newEmptyEntry()
	 */
	@Override
	protected Floor newEmptyEntry() {
		final Floor floor = new Floor();
		floor.setName(messages.newFloor());
		return floor;
	}
}
