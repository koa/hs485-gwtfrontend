package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api;


public class Variable<T extends Value> {
	private Class<T>	type;

	public Variable() {
	}

	public Variable(final Class<T> type) {
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(final Class<T> type) {
		this.type = type;
	}

}
