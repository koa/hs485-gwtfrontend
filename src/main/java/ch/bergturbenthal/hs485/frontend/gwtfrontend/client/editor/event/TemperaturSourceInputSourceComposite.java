package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDeviceType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.TemperatureValueSensorEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TemperaturSourceInputSourceComposite extends EventSourceConfigPanel<ValueEvent, TemperatureValueSensorEventSource> {
	public static final class SourceManager implements EventSourceManager<ValueEvent, TemperatureValueSensorEventSource> {
		private final LabelGenerator	labelGenerator;

		public SourceManager(final LabelGenerator labelGenerator) {
			this.labelGenerator = labelGenerator;
		}

		@Override
		public EventSourceConfigPanel<ValueEvent, TemperatureValueSensorEventSource> buildPanel() {
			final TemperaturSourceInputSourceComposite ret = new TemperaturSourceInputSourceComposite();
			ret.setLabelGenerator(labelGenerator);
			return ret;
		}

		@Override
		public String describeSource(final TemperatureValueSensorEventSource eventSource) {
			final InputConnector inputConnector = eventSource.getInputConnector();
			if (inputConnector == null)
				return "<unset>";
			return labelGenerator.makeLabelForInputConnector(inputConnector);
		}

		@Override
		public void fixReferences(final TemperatureValueSensorEventSource source, final Map<String, InputConnector> inputConnectors,
				final Map<String, OutputDevice> outputDevices) {
			if (source.getInputConnector() != null && source.getInputConnector().getConnectorId() != null)
				source.setInputConnector(inputConnectors.get(source.getInputConnector().getConnectorId()));
		}

		@Override
		public Class<TemperatureValueSensorEventSource> getConfigureSourceType() {
			return TemperatureValueSensorEventSource.class;
		}

		@Override
		public Class<ValueEvent> getEventType() {
			return ValueEvent.class;
		}

		@Override
		public String getName() {
			return "Temperature";
		}

		@Override
		public Collection<InputConnector> listInputConnectorsForSource(final TemperatureValueSensorEventSource eventSource) {
			final ArrayList<InputConnector> ret = new ArrayList<InputConnector>();
			final InputConnector inputConnector = eventSource.getInputConnector();
			if (inputConnector != null)
				ret.add(inputConnector);
			return ret;
		}

		@Override
		public TemperatureValueSensorEventSource makeNewEventSource() {
			return new TemperatureValueSensorEventSource();
		}
	}

	private final Label												connectionLabel;

	private final ToggleButton								assignButton;

	private TemperatureValueSensorEventSource	source;

	public TemperaturSourceInputSourceComposite() {

		final VerticalPanel verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);

		final Label lblTemperature = new Label("Temperature");
		verticalPanel.add(lblTemperature);
		verticalPanel.setCellHorizontalAlignment(lblTemperature, HasHorizontalAlignment.ALIGN_CENTER);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setWidth("100%");
		verticalPanel.setCellWidth(horizontalPanel, "100%");

		connectionLabel = new Label("New label");
		horizontalPanel.add(connectionLabel);

		assignButton = new ToggleButton("Assign");
		horizontalPanel.add(assignButton);
	}

	@Override
	public boolean canReceiveInputConnector(final InputConnector inputConnector) {
		if (assignButton.getValue().booleanValue())
			return inputConnector.getType() == InputDeviceType.TEMPERATURE;
		return false;
	}

	@Override
	public void setEventSource(final TemperatureValueSensorEventSource source) {
		this.source = source;
		updateLabel();
	}

	@Override
	public void takeInputConnector(final InputConnector inputConnector) {
		source.setInputConnector(inputConnector);
		assignButton.setValue(Boolean.FALSE);
		updateLabel();
	}

	private void updateLabel() {
		connectionLabel.setText(labelGenerator.makeLabelForInputConnector(source.getInputConnector()));
	}

}
