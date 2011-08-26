package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

public class OutputDevice {
	public static enum Type {
		DIMMER, SWITCH
	}

	private String	name;
	private Type		type;

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setType(final Type type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "OutputDevice [name=" + name + ", type=" + type + "]";
	}

}
