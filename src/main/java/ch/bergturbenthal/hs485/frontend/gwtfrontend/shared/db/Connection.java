package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Connection implements IsSerializable {
	@DBRef
	private InputConnector	inputConnector;
	@DBRef
	private OutputDevice		outputDevice;

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

}
