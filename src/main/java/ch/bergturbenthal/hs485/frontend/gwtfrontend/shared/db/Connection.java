package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import org.springframework.data.mongodb.core.mapping.DBRef;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Connection implements IsSerializable {
	private float										connectionSourceTriggerValue;
	private ConnectionTargetAction	connectionTargetAction;
	private boolean									connectionTargetAutoOff;
	private int											connectionTargetTimeout;
	private ConnectionType					connectionType;
	@DBRef
	private InputConnector					inputConnector;
	@DBRef
	private OutputDevice						outputDevice;

	public Connection() {

	}

	public boolean canHandleInputConnector(final InputConnector connector) {
		if (connectionType == null)
			return false;
		switch (connectionType) {
		case EVENT:
			switch (connector.getType()) {
			case PUSH:
				return true;
			case SWITCH:
				return true;
			default:
				return false;
			}
		case VALUE:
			switch (connector.getType()) {
			case HUMIDITY:
				return true;
			case TEMPERATURE:
				return true;
			default:
				return false;
			}
		default:
			return false;
		}
	}

	public boolean canHandleOutputDevice(final OutputDevice device) {
		return true;
	}

	public float getConnectionSourceTriggerValue() {
		return connectionSourceTriggerValue;
	}

	public ConnectionTargetAction getConnectionTargetAction() {
		return connectionTargetAction;
	}

	public int getConnectionTargetTimeout() {
		return connectionTargetTimeout;
	}

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public InputConnector getInputConnector() {
		return inputConnector;
	}

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	public boolean isConnectionTargetAutoOff() {
		return connectionTargetAutoOff;
	}

	public void setConnectionSourceTriggerValue(final float connectionSourceTriggerValue) {
		this.connectionSourceTriggerValue = connectionSourceTriggerValue;
	}

	public void setConnectionTargetAction(final ConnectionTargetAction connectionTargetAction) {
		this.connectionTargetAction = connectionTargetAction;
	}

	public void setConnectionTargetAutoOff(final boolean connectionTargetAutoOff) {
		this.connectionTargetAutoOff = connectionTargetAutoOff;
	}

	public void setConnectionTargetTimeout(final int connectionTargetTimeout) {
		this.connectionTargetTimeout = connectionTargetTimeout;
	}

	public void setConnectionType(final ConnectionType connectionType) {
		this.connectionType = connectionType;
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
