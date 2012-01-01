package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.KeyPairEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class KeyPairInputSourceComposite extends EventSourceConfigPanel<KeyEvent, KeyPairEventSource> {
	public static final class SourceManager implements EventSourceManager<KeyEvent, KeyPairEventSource> {
		private final LabelGenerator	labelGenerator;

		public SourceManager(final LabelGenerator labelGenerator) {
			this.labelGenerator = labelGenerator;
		}

		@Override
		public EventSourceConfigPanel<KeyEvent, KeyPairEventSource> buildPanel() {
			final KeyPairInputSourceComposite ret = new KeyPairInputSourceComposite();
			ret.setLabelGenerator(labelGenerator);
			return ret;
		}

		@Override
		public String describeSource(final KeyPairEventSource eventSource) {
			final StringBuilder sourceLabel = new StringBuilder();
			sourceLabel.append(labelGenerator.makeLabelForInputConnector(eventSource.getOnInputConnector()));
			sourceLabel.append(":");
			sourceLabel.append(labelGenerator.makeLabelForInputConnector(eventSource.getOffInputConnector()));
			return sourceLabel.toString();
		}

		@Override
		public void fixReferences(final KeyPairEventSource source, final Map<String, InputConnector> inputConnectors,
				final Map<String, OutputDevice> outputDevices) {
			if (source.getOnInputConnector() != null && source.getOnInputConnector().getConnectorId() != null)
				source.setOnInputConnector(inputConnectors.get(source.getOnInputConnector().getConnectorId()));
			if (source.getOffInputConnector() != null && source.getOffInputConnector().getConnectorId() != null)
				source.setOffInputConnector(inputConnectors.get(source.getOffInputConnector().getConnectorId()));

		}

		@Override
		public Class<KeyPairEventSource> getConfigureSourceType() {
			return KeyPairEventSource.class;
		}

		@Override
		public Class<KeyEvent> getEventType() {
			return KeyEvent.class;
		}

		@Override
		public String getName() {
			return "Key Pair";
		}

		@Override
		public Collection<InputConnector> listInputConnectorsForSource(final KeyPairEventSource eventSource) {
			final ArrayList<InputConnector> list = new ArrayList<InputConnector>(2);
			if (eventSource.getOnInputConnector() != null)
				list.add(eventSource.getOnInputConnector());
			if (eventSource.getOffInputConnector() != null)
				list.add(eventSource.getOffInputConnector());
			return list;
		}

		@Override
		public KeyPairEventSource makeNewEventSource() {
			return new KeyPairEventSource();
		}
	}

	private KeyPairEventSource	source;

	private final Label					onConnectorLabel;

	private final ToggleButton	onConnectorButton;

	private final Label					offConnectorLabel;

	private final ToggleButton	offConnectorButton;

	public KeyPairInputSourceComposite() {

		final FlexTable flexTable = new FlexTable();
		initWidget(flexTable);
		flexTable.setWidth("100%");

		final Label lblKeyPairConnector = new Label("Key Pair Connector");
		lblKeyPairConnector.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(0, 0, lblKeyPairConnector);
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 1);

		flexTable.setWidget(1, 0, new Label("On"));

		onConnectorLabel = new Label("On");
		flexTable.setWidget(1, 1, onConnectorLabel);
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 3);

		onConnectorButton = new ToggleButton("Assign");
		flexTable.setWidget(1, 2, onConnectorButton);

		flexTable.setWidget(2, 0, new Label("Off"));

		offConnectorLabel = new Label("Off");
		flexTable.setWidget(2, 1, offConnectorLabel);

		offConnectorButton = new ToggleButton("Assign");
		flexTable.setWidget(2, 2, offConnectorButton);
	}

	@Override
	public boolean canReceiveInputConnector(final InputConnector inputConnector) {
		if (onConnectorButton.getValue().booleanValue() || offConnectorButton.getValue().booleanValue())
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
	public void setEventSource(final KeyPairEventSource source) {
		this.source = source;
		updateLabels();
	}

	@Override
	public void takeInputConnector(final InputConnector inputConnector) {
		if (onConnectorButton.getValue().booleanValue())
			source.setOnInputConnector(inputConnector);
		onConnectorButton.setValue(Boolean.FALSE);
		if (offConnectorButton.getValue().booleanValue())
			source.setOffInputConnector(inputConnector);
		offConnectorButton.setValue(Boolean.FALSE);
		updateLabels();
	}

	private void updateLabels() {
		onConnectorLabel.setText(labelGenerator.makeLabelForInputConnector(source.getOnInputConnector()));
		offConnectorLabel.setText(labelGenerator.makeLabelForInputConnector(source.getOffInputConnector()));
	}

}
