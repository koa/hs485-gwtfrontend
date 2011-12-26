package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Connection implements IsSerializable {
	@DBRef
	private InputConnector	inputConnector;
	@DBRef
	private OutputDevice		outputDevice;

	public Connection() {

	}

	public InputConnector getInputConnector() {
		return inputConnector;
	}

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	public void setInputConnector(final InputConnector inputConnector) {
		this.inputConnector = inputConnector;
	}

	public void setOutputDevice(final OutputDevice outputDevice) {
		this.outputDevice = outputDevice;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Connection [");
		if (inputConnector != null) {
			builder.append("inputConnector=");
			builder.append(inputConnector);
			builder.append(", ");
		}
		if (outputDevice != null) {
			builder.append("outputDevice=");
			builder.append(outputDevice);
		}
		builder.append("]");
		return builder.toString();
	}

}
