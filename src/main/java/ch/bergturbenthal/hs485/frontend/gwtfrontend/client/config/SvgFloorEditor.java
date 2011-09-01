/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class SvgFloorEditor extends Composite {

	interface SvgFloorEditorUiBinder extends UiBinder<Widget, SvgFloorEditor> {
	}

	private static SvgFloorEditorUiBinder	uiBinder	= GWT.create(SvgFloorEditorUiBinder.class);
	@UiField
	Button																loadSvgButton;
	@UiField
	FileUpload														openFileInput;
	@UiField
	Button																removeFloorButton;
	@UiField
	ListBox																selectFloorListBox;

	/**
	 * Because this class has a default constructor, it can be used as a binder
	 * template. In other words, it can be used in other *.ui.xml files as
	 * follows: <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 * xmlns:g="urn:import:**user's package**">
	 * <g:**UserClassName**>Hello!</g:**UserClassName> </ui:UiBinder> Note that
	 * depending on the widget that is used, it may be necessary to implement
	 * HasHTML instead of HasText.
	 */
	public SvgFloorEditor() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public SvgFloorEditor(final String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("loadSvgButton")
	void onLoadSvgButtonClick(final ClickEvent event) {
		final String filename = openFileInput.getFilename();
		Window.open("file:///" + filename, null, null);
	}
}
