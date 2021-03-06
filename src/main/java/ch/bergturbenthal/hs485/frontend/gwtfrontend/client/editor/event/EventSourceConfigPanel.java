package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.EventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

import com.google.gwt.user.client.ui.Composite;

public abstract class EventSourceConfigPanel<E extends Event, T extends EventSource<E>> extends Composite {
	protected LabelGenerator	labelGenerator;

	public boolean canReceiveInputConnector(final InputConnector inputConnector) {
		return false;
	}

	abstract public void setEventSource(T source);

	public void setLabelGenerator(final LabelGenerator labelGenerator) {
		this.labelGenerator = labelGenerator;
	}

	public void takeInputConnector(final InputConnector inputConnector) {
		throw new IllegalArgumentException("Cannot take this InputConnector");
	}
}
