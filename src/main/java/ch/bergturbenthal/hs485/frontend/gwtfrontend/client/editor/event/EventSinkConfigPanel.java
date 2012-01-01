package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

import com.google.gwt.user.client.ui.Composite;

public abstract class EventSinkConfigPanel<E extends Event, T extends EventSink<E>> extends Composite {
	protected LabelGenerator	labelGenerator;

	public boolean canReceiveOutputDevice(final OutputDevice outputDevice) {
		return false;
	}

	abstract public void setEventSink(T sink);

	public void setLabelGenerator(final LabelGenerator labelGenerator) {
		this.labelGenerator = labelGenerator;
	}

	public void takeOutputDevice(final OutputDevice outputDevice) {
		throw new IllegalArgumentException("Cannot take this OutputDevice");
	}
}
