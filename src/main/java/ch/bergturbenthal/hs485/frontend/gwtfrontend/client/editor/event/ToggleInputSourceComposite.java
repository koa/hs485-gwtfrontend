package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class ToggleInputSourceComposite extends EventSourceConfigPanel<KeyEvent, ToggleKeyEventSource> {
	private final Label						label;
	private final ToggleButton		tglbtnAssignConnector;
	private ToggleKeyEventSource	source;

	public ToggleInputSourceComposite() {

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		initWidget(horizontalPanel);

		horizontalPanel.add(new Label("Toggle Connector"));

		label = new Label("<none>");
		horizontalPanel.add(label);

		tglbtnAssignConnector = new ToggleButton("Assign Connector");
		horizontalPanel.add(tglbtnAssignConnector);
	}

	@Override
	public boolean canReceiveInputConnector(final InputConnector inputConnector) {
		if (tglbtnAssignConnector.getValue().booleanValue())
			switch (inputConnector.getType()) {
			case PIR:
				return true;
			case PUSH:
				return true;
			case SWITCH:
				return true;
			}
		return false;
	}

	@Override
	public void setEventSource(final ToggleKeyEventSource source) {
		this.source = source;
		label.setText(labelGenerator.makeLabelForInputConnector(source.getInputConnector()));
	}

	@Override
	public void takeInputConnector(final InputConnector inputConnector) {
		source.setInputConnector(inputConnector);
		label.setText(labelGenerator.makeLabelForInputConnector(inputConnector));
		tglbtnAssignConnector.setValue(Boolean.FALSE);
	}

}
