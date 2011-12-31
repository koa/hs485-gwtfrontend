/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FileData implements Serializable {
	private static final long	serialVersionUID	= 720587507323632368L;
	private String						fileDataContent;
	@Id
	private String						fileName;
	private String						mimeType;

	public String getFileDataContent() {
		return fileDataContent;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setFileDataContent(final String fileDataContent) {
		this.fileDataContent = fileDataContent;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public void setMimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

}
