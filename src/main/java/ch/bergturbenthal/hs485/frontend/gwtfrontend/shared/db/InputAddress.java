package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import com.google.gwt.user.client.rpc.IsSerializable;

public class InputAddress implements IsSerializable {
	private int	deviceAddress;
	private int	inputAddress;

	public InputAddress() {

	}

	public InputAddress(final int moduleAddress, final int inputAddress) {
		deviceAddress = moduleAddress;
		this.inputAddress = inputAddress;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final InputAddress other = (InputAddress) obj;
		if (inputAddress != other.inputAddress)
			return false;
		if (deviceAddress != other.deviceAddress)
			return false;
		return true;
	}

	public int getDeviceAddress() {
		return deviceAddress;
	}

	public int getInputAddress() {
		return inputAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + inputAddress;
		result = prime * result + deviceAddress;
		return result;
	}

	public void setDeviceAddress(final int moduleAddress) {
		deviceAddress = moduleAddress;
	}

	public void setInputAddress(final int inputAddress) {
		this.inputAddress = inputAddress;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(Integer.toHexString(deviceAddress));
		builder.append(":");
		builder.append(inputAddress);
		return builder.toString();
	}

}
