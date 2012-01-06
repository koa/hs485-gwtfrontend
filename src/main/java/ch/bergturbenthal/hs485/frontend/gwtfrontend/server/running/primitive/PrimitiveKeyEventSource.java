package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType;

public class PrimitiveKeyEventSource implements PrimitiveEventSource {
	private InputAddress	input;
	private KeyType				keyType;

	public InputAddress getInput() {
		return input;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setInput(final InputAddress input) {
		this.input = input;
	}

	public void setKeyType(final KeyType keyType) {
		this.keyType = keyType;
	}

}
