/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.Floor;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
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

	private CellTable<Floor>				cellTable;
	private List<Floor>							floors;
	private final HashSet<Integer>	modifiedRows	= new HashSet<Integer>();

	public FloorEditor() {

		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);

		cellTable = new CellTable<Floor>();
		verticalPanel.add(cellTable);

		final Column<Floor, String> column = new Column<Floor, String>(new EditTextCell()) {
			@Override
			public String getValue(final Floor object) {
				return object.getName();
			}
		};
		column.setFieldUpdater(new FieldUpdater<Floor, String>() {

			public void update(final int index, final Floor object, final String value) {
				object.setName(value);
				modifiedRows.add(index);
			}
		});
		cellTable.addColumn(column, "Floor");

		final Button btnAddFloor = new Button("add Floor");
		btnAddFloor.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				final Floor floor = new Floor();
				floor.setName("new floor");
				modifiedRows.add(floors.size());
				floors.add(floor);
				cellTable.setRowData(floors);
			}
		});
		verticalPanel.add(btnAddFloor);
	}

	public List<Floor> getModifiedFloors() {
		final ArrayList<Floor> ret = new ArrayList<Floor>();
		for (final Integer modifiedIndex : modifiedRows)
			ret.add(floors.get(modifiedIndex));
		return ret;
	}

	public void setData(final List<Floor> floors) {
		this.floors = floors;
		modifiedRows.clear();
		cellTable.setRowData(floors);
	}
}
