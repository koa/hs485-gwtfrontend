package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.code.morphia.annotations.Entity;

@Entity
@Document
public class IconSet implements Serializable {
	private static final long										serialVersionUID	= -7868682272749448498L;
	@Id
	private String															iconsetId;
	private Map<InputDeviceType, IconSetEntry>	inputIcons				= new HashMap<InputDeviceType, IconSetEntry>();

	public String getIconsetId() {
		return iconsetId;
	}

	public Map<InputDeviceType, IconSetEntry> getInputIcons() {
		return inputIcons;
	}

	public void setIconsetId(final String iconsetId) {
		this.iconsetId = iconsetId;
	}

	public void setInputIcons(final Map<InputDeviceType, IconSetEntry> inputIcons) {
		this.inputIcons = inputIcons;
	}

}
