package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class IconSet implements Serializable {
	private static final long									serialVersionUID	= -7868682272749448498L;
	@Id
	private String														iconsetId;
	private IconEntry													inputIcon					= new IconEntry();
	private String														name;
	private Map<OutputDeviceType, IconEntry>	outputIcons				= new HashMap<OutputDeviceType, IconEntry>();

	public String getIconsetId() {
		return iconsetId;
	}

	public IconEntry getInputIcon() {
		return inputIcon;
	}

	public String getName() {
		return name;
	}

	public Map<OutputDeviceType, IconEntry> getOutputIcons() {
		return outputIcons;
	}

	public void setIconsetId(final String iconsetId) {
		this.iconsetId = iconsetId;
	}

	public void setInputIcon(final IconEntry inputIcon) {
		this.inputIcon = inputIcon;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOutputIcons(final Map<OutputDeviceType, IconEntry> outputIcons) {
		this.outputIcons = outputIcons;
	}

}
