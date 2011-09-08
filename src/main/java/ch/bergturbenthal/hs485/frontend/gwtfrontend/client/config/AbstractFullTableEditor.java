/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.Messages;

import com.google.gwt.cell.client.ButtonCell;
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
public abstract class AbstractFullTableEditor<E> extends Composite {
	protected CellTable<E>										cellTable;
	protected List<E>													entries;
	final protected Messages									messages			= GWT.create(Messages.class);
	protected final IdentityHashMap<E, Void>	modifiedRows	= new IdentityHashMap<E, Void>();
	protected final IdentityHashMap<E, Void>	newRows				= new IdentityHashMap<E, Void>();
	protected final IdentityHashMap<E, Void>	removedRows		= new IdentityHashMap<E, Void>();

	public AbstractFullTableEditor() {

		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);

		cellTable = new CellTable<E>();
		verticalPanel.add(cellTable);
		fillValueColumns(cellTable);

		final Column<E, String> removeButtonColumn = new Column<E, String>(new ButtonCell()) {
			@Override
			public String getValue(final E object) {
				return "x";
			}
		};
		removeButtonColumn.setFieldUpdater(new FieldUpdater<E, String>() {

			public void update(final int index, final E object, final String value) {
				removeEntry(index);
			}
		});

		cellTable.addColumn(removeButtonColumn, messages.removeText());

		final Button btnAddFloor = new Button(addEntryString());
		btnAddFloor.addClickHandler(new ClickHandler() {

			public void onClick(final ClickEvent event) {
				addEntry(newEmptyEntry());
			}
		});
		verticalPanel.add(btnAddFloor);
	}

	/**
	 * Append a new entry
	 */
	protected void addEntry(final E entity) {
		newRows.put(entity, null);
		entries.add(entity);
		cellTable.setRowData(entries);
	}

	/**
	 * String for the add-Entry-Button
	 * 
	 * @return
	 */
	protected abstract String addEntryString();

	/**
	 * Append Object-Specific Columns
	 * 
	 * @param cellTable2
	 */
	protected abstract void fillValueColumns(CellTable<E> table);

	public List<E> getEntries() {
		return entries;
	}

	/**
	 * Get new and Modified Floors
	 * 
	 * @return
	 */
	public List<E> getModifiedFloors() {
		final ArrayList<E> ret = new ArrayList<E>(newRows.keySet());
		ret.addAll(modifiedRows.keySet());
		return ret;
	}

	/**
	 * Get removed Floors
	 * 
	 * @return
	 */
	public List<E> getRemovedFloors() {
		return new ArrayList<E>(removedRows.keySet());
	}

	/**
	 * @return
	 */
	protected abstract E newEmptyEntry();

	/**
	 * remove Entry from List
	 * 
	 * @param index
	 */
	protected void removeEntry(final int index) {
		final E remValue = entries.get(index);
		if (!newRows.containsKey(remValue)) {
			modifiedRows.remove(remValue);
			removedRows.put(remValue, null);
		} else
			newRows.remove(remValue);
		entries.remove(index);
		cellTable.setRowData(entries);
	}

	public void setData(final Iterable<E> newEntries) {
		this.entries = new ArrayList<E>();
		for (final E entry : newEntries)
			entries.add(entry);
		modifiedRows.clear();
		newRows.clear();
		removedRows.clear();
		cellTable.setRowData(entries);
	}

	/**
	 * Notify that a Value is Modified
	 * 
	 * @param object
	 */
	protected void setEntityModified(final E object) {
		if (!newRows.containsKey(object))
			modifiedRows.put(object, null);
	}

}
