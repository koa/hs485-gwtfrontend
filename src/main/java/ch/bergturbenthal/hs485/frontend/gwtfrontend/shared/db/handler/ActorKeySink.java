package ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler;

import org.springframework.data.mongodb.core.mapping.DBRef;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ActorKeySink implements EventSink<KeyEvent>, IsSerializable {
	@DBRef
	private OutputDevice	outputDevice;

	public OutputDevice getOutputDevice() {
		return outputDevice;
	}

	public void setOutputDevice(final OutputDevice outputDevice) {
		this.outputDevice = outputDevice;
	}
}
