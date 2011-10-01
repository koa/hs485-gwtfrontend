package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class KeyAddress implements IsSerializable {
	private int	inputAddress;
	private int	moduleAddress;

	public KeyAddress() {

	}

	public KeyAddress(final int moduleAddress, final int inputAddress) {
		this.moduleAddress = moduleAddress;
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
		final KeyAddress other = (KeyAddress) obj;
		if (inputAddress != other.inputAddress)
			return false;
		if (moduleAddress != other.moduleAddress)
			return false;
		return true;
	}

	public int getInputAddress() {
		return inputAddress;
	}

	public int getModuleAddress() {
		return moduleAddress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + inputAddress;
		result = prime * result + moduleAddress;
		return result;
	}

	public void setInputAddress(final int inputAddress) {
		this.inputAddress = inputAddress;
	}

	public void setModuleAddress(final int moduleAddress) {
		this.moduleAddress = moduleAddress;
	}

}
