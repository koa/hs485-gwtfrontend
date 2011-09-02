/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Room;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;

/**
 *
 */
public class RoomEditor extends AbstractFullTableEditor<Room> {

	private Column<Room, String>	floorSelectionColumn;

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.AbstractFullTableEditor#addEntryString()
	 */
	@Override
	protected String addEntryString() {
		return messages.addRoom();
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.AbstractFullTableEditor#fillValueColumns(com.google.gwt.user.cellview.client.CellTable)
	 */
	@Override
	protected void fillValueColumns(final CellTable<Room> table) {

		// Title
		final Column<Room, String> titleColumn = new Column<Room, String>(new EditTextCell()) {
			@Override
			public String getValue(final Room object) {
				return object.getName();
			}
		};
		titleColumn.setFieldUpdater(new FieldUpdater<Room, String>() {

			public void update(final int index, final Room object, final String value) {
				object.setName(value);
				setEntityModified(object);
			}
		});
		table.addColumn(titleColumn, messages.room());

		// Floor
		floorSelectionColumn = new Column<Room, String>(new TextCell()) {

			@Override
			public String getValue(final Room object) {
				return object.getFloor().getName();
			}
		};
		table.addColumn(floorSelectionColumn, messages.floor());
	}

	/**
	 * @see ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.AbstractFullTableEditor#newEmptyEntry()
	 */
	@Override
	protected Room newEmptyEntry() {
		final Room room = new Room();
		room.setName(messages.newRoom());
		return room;
	}

	/**
	 * Update available Floors in Editor
	 * 
	 * @param availableFloors
	 */
	public void setFloors(final Iterable<Floor> availableFloors) {

		final int columnIndex = cellTable.getColumnIndex(floorSelectionColumn);

		final LinkedHashMap<String, Floor> floorIndex = new LinkedHashMap<String, Floor>();
		for (final Floor floor : availableFloors)
			floorIndex.put(floor.getName(), floor);
		floorSelectionColumn = new Column<Room, String>(new SelectionCell(new ArrayList<String>(floorIndex.keySet()))) {

			@Override
			public String getValue(final Room object) {
				final Floor floor = object.getFloor();
				if (floor == null)
					return null;
				return floor.getName();
			}
		};
		floorSelectionColumn.setFieldUpdater(new FieldUpdater<Room, String>() {
			public void update(final int index, final Room object, final String value) {
				object.setFloor(floorIndex.get(value));
				setEntityModified(object);
			}
		});
		cellTable.removeColumn(columnIndex);
		cellTable.insertColumn(columnIndex, floorSelectionColumn);
	}
}
