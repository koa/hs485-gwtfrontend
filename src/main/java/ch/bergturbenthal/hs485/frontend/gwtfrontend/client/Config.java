package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.SvgFloorEditor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.uploader.FileUploadDialog;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Config implements EntryPoint {

	private final Messages	messages	= GWT.create(Messages.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		final RootPanel rootPanel = RootPanel.get("main");

		final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		rootPanel.add(dockLayoutPanel);
//		dockLayoutPanel.setSize("100%", "100%");

		final MenuBar menuBar = new MenuBar(false);
		dockLayoutPanel.addNorth(menuBar, 2);
		final MenuBar menuBar_1 = new MenuBar(true);

		final MenuItem mntmFile = new MenuItem(messages.mntmFile_text(), false, menuBar_1);
		menuBar.addItem(mntmFile);
		final MenuBar menuBar_2 = new MenuBar(true);

		final MenuItem settingsMenu = new MenuItem(messages.mntmNewMenu_text(), false, menuBar_2);

		final MenuItem editFloorsItem = new MenuItem(messages.editFloorsItem(), false, new Command() {
			public void execute() {
				new FloorEditorDialog("plan").show();
			}
		});
		menuBar_2.addItem(editFloorsItem);

		final MenuItem editFilesItem = new MenuItem(messages.mntmNewItem_text_1(), false, new Command() {
			public void execute() {
				new FileUploadDialog().show();
			}
		});
		menuBar_2.addItem(editFilesItem);
		menuBar.addItem(settingsMenu);

		final HTML logHtml = new HTML("", true);
		logHtml.getElement().getStyle().setOverflow(Overflow.AUTO);
		// EventDistributor.registerHandler(new EventHandler() {
		// TreeMap<String, KeyEvent.EventType> lastValue = new TreeMap<String,
		// KeyEvent.EventType>();
		//
		// @Override
		// public void handleKeyEvent(final KeyEvent keyEvent) {
		// final String eventSourceAddress =
		// Integer.toHexString(keyEvent.getKeyAddress().getModuleAddress()) + ":"
		// + keyEvent.getKeyAddress().getInputAddress();
		// lastValue.put(eventSourceAddress, keyEvent.getType());
		// final StringBuffer stateDescr = new StringBuffer();
		// for (final Entry<String, EventType> valueEntry : lastValue.entrySet()) {
		// stateDescr.append(valueEntry.getKey());
		// stateDescr.append(": ");
		// stateDescr.append(valueEntry.getValue().name());
		// stateDescr.append("</br>");
		//
		// }
		// logHtml.setHTML(stateDescr.toString());
		// }
		// });
		// dockLayoutPanel.addSouth(new SelectInputComposite(), 15);
		// communicationService.getEvents(new PollAsyncCallback(logHtml));

		final SvgFloorEditor svgFloorEditor = new SvgFloorEditor();
		dockLayoutPanel.add(svgFloorEditor);
		// svgFloorEditor.setHeight("100%");

	}
}
