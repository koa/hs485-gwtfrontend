/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.uploader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class FileUploadDialog extends DialogBox {

	public FileUploadDialog() {
		setHTML("Upload File");

		final FormPanel formPanel = new FormPanel();
		formPanel.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			public void onSubmitComplete(final SubmitCompleteEvent event) {
				hide();
			}
		});
		formPanel.setAction("Config/file");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		setWidget(formPanel);
		formPanel.setSize("100%", "100%");
		final VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		formPanel.setWidget(verticalPanel);

		final FileUpload fileUpload = new FileUpload();
		fileUpload.setName("file");
		verticalPanel.add(fileUpload);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);

		final Button btnUpload = new Button("upload");
		btnUpload.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				formPanel.submit();
			}
		});
		horizontalPanel.add(btnUpload);

		final Button btnCancel = new Button("cancel");
		btnCancel.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				hide();
			}
		});
		horizontalPanel.add(btnCancel);
	}
}
