/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 */
@Entity
public class FileData implements Serializable {
	private static final long	serialVersionUID	= 720587507323632368L;
	@Lob
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
