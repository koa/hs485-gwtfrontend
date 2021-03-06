package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.FlexTableHelper;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.ActorKeySink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class KeyOutputDeviceSinkComposite extends EventSinkConfigPanel<KeyEvent, ActorKeySink> {
	public static class SinkManager implements EventSinkManager<KeyEvent, ActorKeySink> {
		private final LabelGenerator	labelGenerator;

		public SinkManager(final LabelGenerator labelGenerator) {
			this.labelGenerator = labelGenerator;
		}

		@Override
		public EventSinkConfigPanel<KeyEvent, ActorKeySink> buildPanel() {
			final KeyOutputDeviceSinkComposite keyOutputDeviceSinkComposite = new KeyOutputDeviceSinkComposite();
			keyOutputDeviceSinkComposite.setLabelGenerator(labelGenerator);
			return keyOutputDeviceSinkComposite;
		}

		@Override
		public String describeSink(final ActorKeySink eventSink) {
			return labelGenerator.makeLabelForOutputDevice(eventSink.getOutputDevice());
		}

		@Override
		public void fixReferences(final ActorKeySink sink, final Map<String, InputConnector> inputConnectors,
				final Map<String, OutputDevice> outputDevices) {
			if (sink.getOutputDevice() != null && sink.getOutputDevice().getDeviceId() != null)
				sink.setOutputDevice(outputDevices.get(sink.getOutputDevice().getDeviceId()));
		}

		@Override
		public Class<ActorKeySink> getConfigureSinkType() {
			return ActorKeySink.class;
		}

		@Override
		public Class<KeyEvent> getEventType() {
			return KeyEvent.class;
		}

		@Override
		public String getName() {
			return "Key Actor";
		}

		@Override
		public Collection<OutputDevice> listOutputDevicesForSink(final ActorKeySink eventSink) {
			final ArrayList<OutputDevice> ret = new ArrayList<OutputDevice>(1);
			if (eventSink.getOutputDevice() != null)
				ret.add(eventSink.getOutputDevice());
			return ret;
		}

		@Override
		public ActorKeySink makeNewEventSink() {
			return new ActorKeySink();
		}

	}

	private ActorKeySink				sink;
	private final Label					outputDeviceLabel;
	private final ToggleButton	assignButton;
	private final IntegerBox		timeoutTimeBox;

	public KeyOutputDeviceSinkComposite() {

		final FlexTable flexTable = new FlexTable();
		initWidget(flexTable);
		flexTable.setWidth("100%");

		final Label lblKeyOutputdevice = new Label("Key OutputDevice");
		lblKeyOutputdevice.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.setWidget(0, 0, lblKeyOutputdevice);

		flexTable.setWidget(1, 0, new Label("Output"));

		outputDeviceLabel = new Label("New label");
		flexTable.setWidget(1, 1, outputDeviceLabel);
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 3);
		flexTable.getFlexCellFormatter().setRowSpan(1, 0, 2);

		assignButton = new ToggleButton("Assign");
		flexTable.setWidget(1, 2, assignButton);

		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.setWidget(2, 1, horizontalPanel);

		timeoutTimeBox = new IntegerBox();
		horizontalPanel.add(timeoutTimeBox);
		timeoutTimeBox.setVisibleLength(4);

		final Label lblS = new Label("s");
		horizontalPanel.add(lblS);
		lblS.setWidth("100%");
		timeoutTimeBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			@Override
			public void onValueChange(final ValueChangeEvent<Integer> event) {
				if (sink == null)
					return;
				sink.setAutoOffTime(event.getValue());
			}
		});
		FlexTableHelper.fixRowSpan(flexTable);
	}

	@Override
	public boolean canReceiveOutputDevice(final OutputDevice outputDevice) {
		if (assignButton.getValue().booleanValue())
			return true;
		return false;
	}

	@Override
	public void setEventSink(final ActorKeySink sink) {
		this.sink = sink;
		updateValues();
	}

	@Override
	public void takeOutputDevice(final OutputDevice outputDevice) {
		sink.setOutputDevice(outputDevice);
		updateValues();
		assignButton.setValue(Boolean.FALSE);
	}

	private void updateValues() {
		outputDeviceLabel.setText(labelGenerator.makeLabelForOutputDevice(sink.getOutputDevice()));
		timeoutTimeBox.setValue(sink.getAutoOffTime());
	}

}
