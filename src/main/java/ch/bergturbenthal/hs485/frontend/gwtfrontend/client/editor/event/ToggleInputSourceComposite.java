package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ToggleKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ToggleInputSourceComposite extends EventSourceConfigPanel<KeyEvent, ToggleKeyEventSource> {
	public static final class PanelBuilder implements EventSourcePanelBuilder<KeyEvent, ToggleKeyEventSource> {
		private final LabelGenerator	labelGenerator;

		public PanelBuilder(final LabelGenerator labelGenerator) {
			this.labelGenerator = labelGenerator;
		}

		@Override
		public EventSourceConfigPanel<KeyEvent, ToggleKeyEventSource> buildPanel() {
			final ToggleInputSourceComposite ret = new ToggleInputSourceComposite();
			ret.setLabelGenerator(labelGenerator);
			return ret;
		}

		@Override
		public String describeSource(final ToggleKeyEventSource eventSource) {
			final InputConnector inputConnector = eventSource.getInputConnector();
			if (inputConnector == null)
				return "<unset>";
			return labelGenerator.makeLabelForInputConnector(inputConnector);
		}

		@Override
		public void fixReferences(final ToggleKeyEventSource source, final Map<String, InputConnector> inputConnectors,
				final Map<String, OutputDevice> outputDevices) {
			if (source.getInputConnector() != null && source.getInputConnector().getConnectorId() != null)
				source.setInputConnector(inputConnectors.get(source.getInputConnector().getConnectorId()));
		}

		@Override
		public Class<ToggleKeyEventSource> getConfigureSourceType() {
			return ToggleKeyEventSource.class;
		}

		@Override
		public Class<KeyEvent> getEventType() {
			return KeyEvent.class;
		}

		@Override
		public String getName() {
			return "Toggle Key";
		}

		@Override
		public Collection<InputConnector> listInputConnectorsForSource(final ToggleKeyEventSource source) {
			final ArrayList<InputConnector> ret = new ArrayList<InputConnector>();
			final InputConnector inputConnector = source.getInputConnector();
			if (inputConnector != null)
				ret.add(inputConnector);
			return ret;
		}

		@Override
		public ToggleKeyEventSource makeNewEventSource() {
			return new ToggleKeyEventSource();
		}
	}

	private final Label						label;
	private final ToggleButton		tglbtnAssignConnector;
	private ToggleKeyEventSource	source;

	private final HorizontalPanel	horizontalPanel_1;

	public ToggleInputSourceComposite() {

		final VerticalPanel horizontalPanel = new VerticalPanel();
		initWidget(horizontalPanel);
		horizontalPanel.setWidth("100%");

		final Label label_1 = new Label("Toggle");
		label_1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellHorizontalAlignment(label_1, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(label_1);

		horizontalPanel_1 = new HorizontalPanel();
		horizontalPanel.add(horizontalPanel_1);
		horizontalPanel_1.setWidth("100%");

		label = new Label("<none>");
		horizontalPanel_1.add(label);

		tglbtnAssignConnector = new ToggleButton("Assign");
		horizontalPanel_1.add(tglbtnAssignConnector);
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
