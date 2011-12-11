package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.DBRef;

public class IconSetEntry implements Serializable {

	private static final long	serialVersionUID	= 3130975511563877820L;
	@DBRef
	private FileData					image;

	public IconSetEntry() {
	}

	public IconSetEntry(final FileData image) {
		this.image = image;
	}

	public FileData getImage() {
		return image;
	}

	public void setImage(final FileData image) {
		this.image = image;
	}

}
