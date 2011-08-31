/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.Messages;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class FloorEditor extends Composite {

	private CellTable<Floor>										cellTable;
	private List<Floor>													floors;
	private final Messages											messages			= GWT.create(Messages.class);
	private final IdentityHashMap<Floor, Void>	modifiedRows	= new IdentityHashMap<Floor, Void>();
	private final IdentityHashMap<Floor, Void>	newRows				= new IdentityHashMap<Floor, Void>();
	private final IdentityHashMap<Floor, Void>	removedRows		= new IdentityHashMap<Floor, Void>();

	public FloorEditor() {

		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);

		cellTable = new CellTable<Floor>();
		verticalPanel.add(cellTable);

		final Column<Floor, String> titleColumn = new Column<Floor, String>(new EditTextCell()) {
			@Override
			public String getValue(final Floor object) {
				return object.getName();
			}
		};
		titleColumn.setFieldUpdater(new FieldUpdater<Floor, String>() {

			public void update(final int index, final Floor object, final String value) {
				object.setName(value);
				if (!newRows.containsKey(object))
					modifiedRows.put(object, null);
			}
		});
		cellTable.addColumn(titleColumn, messages.floor());

		final Column<Floor, String> removeButtonColumn = new Column<Floor, String>(new ButtonCell()) {
			@Override
			public String getValue(final Floor object) {
				return "x";
			}
		};
		removeButtonColumn.setFieldUpdater(new FieldUpdater<Floor, String>() {

			public void update(final int index, final Floor object, final String value) {
				if (!newRows.containsKey(object)) {
					modifiedRows.remove(object);
					removedRows.put(object, null);
				} else
					newRows.remove(object);
				floors.remove(index);
				cellTable.setRowData(floors);
			}
		});

		cellTable.addColumn(removeButtonColumn, messages.removeText());

		final Button btnAddFloor = new Button(messages.addFloor());
		btnAddFloor.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				final Floor floor = new Floor();
				floor.setName(messages.newFloor());
				newRows.put(floor, null);
				floors.add(floor);
				cellTable.setRowData(floors);
			}
		});
		verticalPanel.add(btnAddFloor);
	}

	/**
	 * Get new and Modified Floors
	 * 
	 * @return
	 */
	public List<Floor> getModifiedFloors() {
		final ArrayList<Floor> ret = new ArrayList<Floor>(newRows.keySet());
		ret.addAll(modifiedRows.keySet());
		return ret;
	}

	/**
	 * Get removed Floors
	 * 
	 * @return
	 */
	public List<Floor> getRemovedFloors() {
		return new ArrayList<Floor>(removedRows.keySet());
	}

	public void setData(final List<Floor> floors) {
		this.floors = floors;
		modifiedRows.clear();
		newRows.clear();
		removedRows.clear();
		cellTable.setRowData(floors);
	}
}
