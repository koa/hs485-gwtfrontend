package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.PirKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class PirKeyEventInputSourceComposite extends EventSourceConfigPanel<KeyEvent, PirKeyEventSource> {
	public static final class SourceManager implements EventSourceManager<KeyEvent, PirKeyEventSource> {
		private final LabelGenerator	labelGenerator;

		public SourceManager(final LabelGenerator labelGenerator) {
			this.labelGenerator = labelGenerator;
		}

		@Override
		public EventSourceConfigPanel<KeyEvent, PirKeyEventSource> buildPanel() {
			final PirKeyEventInputSourceComposite ret = new PirKeyEventInputSourceComposite();
			ret.setLabelGenerator(labelGenerator);
			return ret;
		}

		@Override
		public String describeSource(final PirKeyEventSource eventSource) {
			final InputConnector inputConnector = eventSource.getInputConnector();
			if (inputConnector == null)
				return "<unset>";
			return labelGenerator.makeLabelForInputConnector(inputConnector);
		}

		@Override
		public void fixReferences(final PirKeyEventSource source, final Map<String, InputConnector> inputConnectors,
				final Map<String, OutputDevice> outputDevices) {
			if (source.getInputConnector() != null && source.getInputConnector().getConnectorId() != null)
				source.setInputConnector(inputConnectors.get(source.getInputConnector().getConnectorId()));
		}

		@Override
		public Class<PirKeyEventSource> getConfigureSourceType() {
			return PirKeyEventSource.class;
		}

		@Override
		public Class<KeyEvent> getEventType() {
			return KeyEvent.class;
		}

		@Override
		public String getName() {
			return "PIR Sensor";
		}

		@Override
		public Collection<InputConnector> listInputConnectorsForSource(final PirKeyEventSource source) {
			final ArrayList<InputConnector> ret = new ArrayList<InputConnector>();
			final InputConnector inputConnector = source.getInputConnector();
			if (inputConnector != null)
				ret.add(inputConnector);
			return ret;
		}

		@Override
		public PirKeyEventSource makeNewEventSource() {
			return new PirKeyEventSource();
		}
	}

	private final Label					inputSourcelabel;
	private final ToggleButton	assignButton;
	private PirKeyEventSource		source;

	public PirKeyEventInputSourceComposite() {

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		initWidget(horizontalPanel);

		horizontalPanel.add(new Label("PIR"));

		inputSourcelabel = new Label("inputSourceLabel");
		inputSourcelabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.add(inputSourcelabel);
		horizontalPanel.setCellHorizontalAlignment(inputSourcelabel, HasHorizontalAlignment.ALIGN_CENTER);
		horizontalPanel.setCellWidth(inputSourcelabel, "100%");

		assignButton = new ToggleButton("Assign");
		horizontalPanel.add(assignButton);
	}

	@Override
	public boolean canReceiveInputConnector(final InputConnector inputConnector) {
		if (assignButton.getValue().booleanValue())
			switch (inputConnector.getType()) {
			case PIR:
				return true;
			}
		return false;
	}

	@Override
	public void setEventSource(final PirKeyEventSource source) {
		this.source = source;
		updateLabel();
	}

	@Override
	public void takeInputConnector(final InputConnector inputConnector) {
		source.setInputConnector(inputConnector);
		updateLabel();
		assignButton.setValue(Boolean.FALSE);
	}

	private void updateLabel() {
		inputSourcelabel.setText(labelGenerator.makeLabelForInputConnector(source.getInputConnector()));
	}

}
