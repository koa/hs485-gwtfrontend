/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config.FileUploader;

import com.google.gwt.user.client.ui.DialogBox;

/**
 *
 */
public class FileUploadDialog extends DialogBox {

	public FileUploadDialog() {
		setHTML("Upload File");

		final FileUploader fileUploader = new FileUploader();
		setWidget(fileUploader);
		fileUploader.setSize("100%", "100%");
		fileUploader.setFinishedRunnable(new Runnable() {

			public void run() {
				hide();
			}
		});
	}

}
