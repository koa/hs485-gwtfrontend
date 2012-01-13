package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.ConfigServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconEntry;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDeviceType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class EditIconSetsDialog extends DialogBox {
	private final ConfigServiceAsync							configService		= ConfigServiceAsync.Util.getInstance();
	private IconSet																currentIconSet	= null;
	private List<String>													filenames				= Collections.emptyList();
	private List<IconSet>													iconSets				= Collections.emptyList();
	private TextBox																nameTextBox;
	private final Map<OutputDeviceType, ListBox>	outputListboxes	= new HashMap<OutputDeviceType, ListBox>();
	private final ListBox													selectIconsetListbox;
	private ListBox																selectInputIconList;
	private List<IconEntry>												svgFiles				= Collections.emptyList();

	public EditIconSetsDialog() {
		setText("Edit Iconsets");

		final FlexTable flexTable = new FlexTable();
		setWidget(flexTable);

		selectIconsetListbox = new ListBox();
		selectIconsetListbox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(final ChangeEvent event) {
				if (currentIconSet != null)
					updateCurrentIconSet();
				showCurrentSelectedIconset();
			}
		});
		final ChangeHandler updateIconsetChangeHandler = new ChangeHandler() {
			@Override
			public void onChange(final ChangeEvent event) {
				updateCurrentIconSet();
			}
		};

		flexTable.setWidget(0, 0, selectIconsetListbox);
		selectIconsetListbox.setSize("100%", "100%");
		selectIconsetListbox.setVisibleItemCount(5);

		flexTable.setWidget(0, 1, new Label("Name"));

		nameTextBox = new TextBox();
		nameTextBox.addChangeHandler(updateIconsetChangeHandler);
		flexTable.setWidget(0, 2, nameTextBox);

		flexTable.setWidget(1, 1, new Label("Inputdevice icon"));

		final ListBox selectFanList = new ListBox();
		final ListBox selectHeatList = new ListBox();
		final ListBox selectLightList = new ListBox();
		final ListBox selectSocketList = new ListBox();
		selectLightList.addChangeHandler(updateIconsetChangeHandler);
		outputListboxes.put(OutputDeviceType.FAN, selectFanList);
		outputListboxes.put(OutputDeviceType.HEAT, selectHeatList);
		outputListboxes.put(OutputDeviceType.LIGHT, selectLightList);
		outputListboxes.put(OutputDeviceType.SOCKET, selectSocketList);
		for (final ListBox listbox : outputListboxes.values())
			listbox.addChangeHandler(updateIconsetChangeHandler);

		selectInputIconList = new ListBox();
		selectInputIconList.addChangeHandler(updateIconsetChangeHandler);
		flexTable.setWidget(1, 2, selectInputIconList);
		selectInputIconList.setWidth("100%");

		flexTable.setWidget(2, 1, new Label("Light icon"));

		flexTable.setWidget(2, 2, selectLightList);
		selectLightList.setWidth("100%");

		flexTable.setWidget(3, 1, new Label("Heat icon"));

		flexTable.setWidget(3, 2, selectHeatList);
		selectHeatList.setWidth("100%");

		flexTable.setWidget(4, 1, new Label("Fan icon"));

		flexTable.setWidget(4, 2, selectFanList);
		selectFanList.setWidth("100%");

		flexTable.setWidget(5, 1, new Label("Socket Icon"));

		flexTable.setWidget(5, 2, selectSocketList);
		selectSocketList.setWidth("100%");

		final Grid grid = new Grid(2, 2);
		flexTable.setWidget(6, 0, grid);
		grid.setSize("100%", "100%");

		final Button btnAddIconset = new Button("Add iconset");
		btnAddIconset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final IconSet iconSet = new IconSet();
				iconSet.setName("new Iconset");
				iconSets.add(iconSet);
				updateIconSetListboxContent();
			}
		});
		grid.setWidget(0, 0, btnAddIconset);
		btnAddIconset.setWidth("100%");

		final Button btnRemoveIconset = new Button("Remove Iconset");
		btnRemoveIconset.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final int selectedIndex = selectIconsetListbox.getSelectedIndex();
				if (selectedIndex < 0 || selectedIndex >= iconSets.size())
					return;
				iconSets.remove(selectedIndex);
				updateIconSetListboxContent();
				showCurrentSelectedIconset();
			}
		});
		grid.setWidget(0, 1, btnRemoveIconset);
		btnRemoveIconset.setWidth("100%");

		final Button btnSave = new Button("Save");
		btnSave.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				configService.saveIconsets(iconSets, new AsyncCallback<Void>() {

					@Override
					public void onFailure(final Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(final Void result) {
						hide();
					}
				});
			}
		});
		grid.setWidget(1, 1, btnSave);
		btnSave.setWidth("100%");

		final Button btnCancel = new Button("Cancel");
		btnCancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		grid.setWidget(1, 0, btnCancel);
		btnCancel.setWidth("100%");
		flexTable.getFlexCellFormatter().setColSpan(6, 0, 3);
		flexTable.getFlexCellFormatter().setRowSpan(0, 0, 6);
		FlexTableHelper.fixRowSpan(flexTable);

		configService.loadIconSets(new AsyncCallback<List<IconSet>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final List<IconSet> result) {
				iconSets = result;
				updateIconSetListboxContent();
			}
		});
		configService.listFilesByMime("image/svg+xml", new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(final Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(final List<String> fileListResult) {
				svgFiles = new ArrayList<IconEntry>();
				filenames = new ArrayList<String>();
				for (final String filename : fileListResult) {
					final IconEntry iconEntry = new IconEntry();
					final FileData image = new FileData();
					image.setFileName(filename);
					iconEntry.setImage(image);
					svgFiles.add(iconEntry);
					filenames.add(filename);
				}
				final List<ListBox> selecttionListboxes = new ArrayList<ListBox>(outputListboxes.values());
				selecttionListboxes.add(selectInputIconList);
				for (final ListBox listBox : selecttionListboxes)
					listBox.clear();
				for (final String filename : filenames)
					for (final ListBox listBox : selecttionListboxes)
						listBox.addItem(filename);
				// for (final String filename : fileListResult)
				// configService.getFile(filename, new AsyncCallback<FileData>() {
				//
				// @Override
				// public void onFailure(final Throwable caught) {
				// // TODO Auto-generated method stub
				//
				// }
				//
				// @Override
				// public void onSuccess(final FileData fileDataResult) {
				// final IconEntry iconEntry = new IconEntry();
				// iconEntry.setImage(fileDataResult);
				// svgFiles.add(iconEntry);
				// filenames.add(filename);
				// final List<ListBox> selecttionListboxes = new
				// ArrayList<ListBox>(outputListboxes.values());
				// selecttionListboxes.add(selectInputIconList);
				// for (final ListBox listBox : selecttionListboxes)
				// listBox.clear();
				// for (final String filename : filenames)
				// for (final ListBox listBox : selecttionListboxes)
				// listBox.addItem(filename);
				// }
				// });
			}
		});
	}

	private void showCurrentSelectedIconset() {
		final int selectedIndex = selectIconsetListbox.getSelectedIndex();
		if (selectedIndex < 0 || selectedIndex >= iconSets.size())
			return;
		currentIconSet = iconSets.get(selectedIndex);
		updateDialog();
	}

	private void updateCurrentIconSet() {
		if (currentIconSet == null)
			return;
		final String newName = nameTextBox.getValue();
		final String oldName = currentIconSet.getName();
		currentIconSet.setName(newName);
		currentIconSet.setInputIcon(svgFiles.get(selectInputIconList.getSelectedIndex()));
		for (final OutputDeviceType outputType : OutputDeviceType.values())
			currentIconSet.getOutputIcons().put(outputType, svgFiles.get(outputListboxes.get(outputType).getSelectedIndex()));
		if (!newName.equals(oldName))
			updateIconSetListboxContent();
	}

	private void updateDialog() {
		if (currentIconSet == null)
			return;
		nameTextBox.setValue(currentIconSet.getName());
		selectInputIconList.setSelectedIndex(filenames.indexOf(currentIconSet.getInputIcon().getImage().getFileName()));
		for (final OutputDeviceType outputType : OutputDeviceType.values())
			outputListboxes.get(outputType).setSelectedIndex(filenames.indexOf(currentIconSet.getOutputIcons().get(outputType).getImage().getFileName()));

	}

	private void updateIconSetListboxContent() {
		final int oldIndex = selectIconsetListbox.getSelectedIndex();
		selectIconsetListbox.clear();
		for (final IconSet iconSet : iconSets)
			selectIconsetListbox.addItem(iconSet.getName());
		if (oldIndex < iconSets.size())
			selectIconsetListbox.setSelectedIndex(oldIndex);
	}

}
