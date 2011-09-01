/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class FileUploader extends Composite {

	interface FileUploaderUiBinder extends UiBinder<Widget, FileUploader> {
	}

	private static FileUploaderUiBinder	uiBinder					= GWT.create(FileUploaderUiBinder.class);
	@UiField
	Button															cancelButton;

	private Runnable										finishedRunnable	= null;
	@UiField
	FormPanel														formPanel;
	@UiField
	Button															uploadButton;

	/**
	 * Because this class has a default constructor, it can be used as a binder
	 * template. In other words, it can be used in other *.ui.xml files as
	 * follows: <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 * xmlns:g="urn:import:**user's package**">
	 * <g:**UserClassName**>Hello!</g:**UserClassName> </ui:UiBinder> Note that
	 * depending on the widget that is used, it may be necessary to implement
	 * HasHTML instead of HasText.
	 */
	public FileUploader() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("cancelButton")
	void onCancelButtonClick(final ClickEvent event) {
		if (finishedRunnable != null)
			finishedRunnable.run();
	}

	@UiHandler("formPanel")
	void onFormPanelSubmitComplete(final SubmitCompleteEvent event) {
		if (finishedRunnable != null)
			finishedRunnable.run();
	}

	@UiHandler("uploadButton")
	void onUploadButtonClick(final ClickEvent event) {
		formPanel.submit();
	}

	public void setFinishedRunnable(final Runnable finishedRunnable) {
		this.finishedRunnable = finishedRunnable;
	}
}
