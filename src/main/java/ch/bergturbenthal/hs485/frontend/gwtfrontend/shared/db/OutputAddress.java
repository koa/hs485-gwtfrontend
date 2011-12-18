package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db;

import com.google.gwt.user.client.rpc.IsSerializable;

public class OutputAddress implements IsSerializable, Comparable<OutputAddress> {
	private Integer	deviceAddress;
	private Integer	outputAddress;

	public OutputAddress() {
	}

	public OutputAddress(final int deviceAddress, final int outputAddress) {
		this.deviceAddress = deviceAddress;
		this.outputAddress = outputAddress;
	}

	public OutputAddress(final OutputAddress original) {
		this(original.getDeviceAddress(), original.getOutputAddress());
	}

	@Override
	public int compareTo(final OutputAddress o) {
		final int compareDev = deviceAddress.compareTo(o.deviceAddress);
		if (compareDev != 0)
			return compareDev;
		return outputAddress.compareTo(o.outputAddress);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OutputAddress other = (OutputAddress) obj;
		if (deviceAddress == null) {
			if (other.deviceAddress != null)
				return false;
		} else if (!deviceAddress.equals(other.deviceAddress))
			return false;
		if (outputAddress == null) {
			if (other.outputAddress != null)
				return false;
		} else if (!outputAddress.equals(other.outputAddress))
			return false;
		return true;
	}

	public Integer getDeviceAddress() {
		return deviceAddress;
	}

	public Integer getOutputAddress() {
		return outputAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (deviceAddress == null ? 0 : deviceAddress.hashCode());
		result = prime * result + (outputAddress == null ? 0 : outputAddress.hashCode());
		return result;
	}

	public void setDeviceAddress(final Integer deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public void setOutputAddress(final Integer outputAddress) {
		this.outputAddress = outputAddress;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		if (deviceAddress == null)
			builder.append("null");
		else
			builder.append(Integer.toHexString(deviceAddress.intValue()));
		builder.append(":");
		if (outputAddress == null)
			builder.append("null");
		else
			builder.append(Integer.toHexString(outputAddress.intValue()));
		return builder.toString();
	}

}
