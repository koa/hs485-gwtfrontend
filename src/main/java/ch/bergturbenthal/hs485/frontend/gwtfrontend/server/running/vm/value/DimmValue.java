package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;

public class DimmValue implements Value {
	private final byte	value;

	public DimmValue(final byte value) {
		this.value = value;
	}

	public DimmValue(final int value) {
		this.value = (byte) value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final DimmValue other = (DimmValue) obj;
		if (value != other.value)
			return false;
		return true;
	}

	public byte getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("DimmValue [value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

}
