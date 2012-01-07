package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.handler.SwitchingActorValueSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.ValueEvent;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;

public class ValueSwitchingOutputDeviceSinkComposite extends EventSinkConfigPanel<ValueEvent, SwitchingActorValueSink> {
	public static class SinkManager implements EventSinkManager<ValueEvent, SwitchingActorValueSink> {
		private final LabelGenerator	labelGenerator;

		public SinkManager(final LabelGenerator labelGenerator) {
			this.labelGenerator = labelGenerator;
		}

		@Override
		public EventSinkConfigPanel<ValueEvent, SwitchingActorValueSink> buildPanel() {
			final ValueSwitchingOutputDeviceSinkComposite composite = new ValueSwitchingOutputDeviceSinkComposite();
			composite.setLabelGenerator(labelGenerator);
			return composite;
		}

		@Override
		public String describeSink(final SwitchingActorValueSink eventSink) {
			return labelGenerator.makeLabelForOutputDevice(eventSink.getOutputDevice());
		}

		@Override
		public void fixReferences(final SwitchingActorValueSink sink, final Map<String, InputConnector> inputConnectors,
				final Map<String, OutputDevice> outputDevices) {
			if (sink.getOutputDevice() != null && sink.getOutputDevice().getDeviceId() != null)
				sink.setOutputDevice(outputDevices.get(sink.getOutputDevice().getDeviceId()));

		}

		@Override
		public Class<SwitchingActorValueSink> getConfigureSinkType() {
			return SwitchingActorValueSink.class;
		}

		@Override
		public Class<ValueEvent> getEventType() {
			return ValueEvent.class;
		}

		@Override
		public String getName() {
			return "Switching Actor";
		}

		@Override
		public Collection<OutputDevice> listOutputDevicesForSink(final SwitchingActorValueSink eventSink) {
			final ArrayList<OutputDevice> ret = new ArrayList<OutputDevice>(1);
			if (eventSink.getOutputDevice() != null)
				ret.add(eventSink.getOutputDevice());
			return ret;
		}

		@Override
		public SwitchingActorValueSink makeNewEventSink() {
			final SwitchingActorValueSink ret = new SwitchingActorValueSink();
			ret.setOnBelow(true);
			return ret;
		}

	}

	private SwitchingActorValueSink	sink;

	private final Label							outputLabel;

	private final DoubleBox					valueInputBox;

	private final ToggleButton			assignButton;
	private final CheckBox					onWhenBelowCheckbox;

	public ValueSwitchingOutputDeviceSinkComposite() {

		final FlexTable flexTable = new FlexTable();
		initWidget(flexTable);

		final Label lblSwitchOutputdevice = new Label("Switch Outputdevice");
		flexTable.setWidget(0, 0, lblSwitchOutputdevice);
		flexTable.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		final Label lblOutput = new Label("Output");
		flexTable.setWidget(1, 0, lblOutput);

		outputLabel = new Label("outputDevice");
		flexTable.setWidget(1, 1, outputLabel);

		assignButton = new ToggleButton("Assign");
		flexTable.setWidget(1, 2, assignButton);

		final Label lblSwitchingValue = new Label("Switching Value");
		flexTable.setWidget(2, 0, lblSwitchingValue);

		valueInputBox = new DoubleBox();
		valueInputBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(final ValueChangeEvent<Double> event) {
				sink.setTriggerLevel(event.getValue().floatValue());
			}
		});
		valueInputBox.setVisibleLength(5);
		flexTable.setWidget(2, 1, valueInputBox);

		onWhenBelowCheckbox = new CheckBox("on when Below");
		onWhenBelowCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(final ValueChangeEvent<Boolean> event) {
				sink.setOnBelow(event.getValue().booleanValue());
			}
		});
		flexTable.setWidget(2, 2, onWhenBelowCheckbox);
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 3);
	}

	@Override
	public boolean canReceiveOutputDevice(final OutputDevice outputDevice) {
		return assignButton.getValue().booleanValue();
	}

	@Override
	public void setEventSink(final SwitchingActorValueSink sink) {
		this.sink = sink;
		updateData();
	}

	@Override
	public void takeOutputDevice(final OutputDevice outputDevice) {
		sink.setOutputDevice(outputDevice);
		assignButton.setValue(Boolean.FALSE);
		updateData();
	}

	private void updateData() {
		outputLabel.setText(labelGenerator.makeLabelForOutputDevice(sink.getOutputDevice()));
		valueInputBox.setValue(Double.valueOf(sink.getTriggerLevel()));
		onWhenBelowCheckbox.setValue(Boolean.valueOf(sink.isOnBelow()));
	}

}
