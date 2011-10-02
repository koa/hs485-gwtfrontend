package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll.EventDistributor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll.EventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent.EventType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectInputComposite extends Composite {
	private static class KeyData {
		private int									activationCount	= 0;
		private final RadioButton		displayRadioButton;
		private KeyEvent.EventType	lastState;

		public KeyData(final RadioButton displayRadioButton) {
			super();
			this.displayRadioButton = displayRadioButton;
		}

		public int getActivationCount() {
			return activationCount;
		}

		public RadioButton getDisplayRadioButton() {
			return displayRadioButton;
		}

		public KeyEvent.EventType getLastState() {
			return lastState;
		}

		public void setLastState(final KeyEvent.EventType lastState) {
			if (lastState == EventType.DOWN && this.lastState == EventType.UP)
				activationCount += 1;
			this.lastState = lastState;
		}

	}

	private final String											groupName;
	private final EventHandler								handler;
	private final VerticalPanel								inputListPanel;
	private final Map<InputAddress, KeyData>	visibleInputs	= new HashMap<InputAddress, SelectInputComposite.KeyData>();

	public SelectInputComposite() {

		inputListPanel = new VerticalPanel();
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(inputListPanel);
		initWidget(horizontalPanel);
		groupName = "key-input-" + this;
		handler = new EventHandler() {

			@Override
			public synchronized void handleKeyEvent(final KeyEvent keyEvent) {
				final InputAddress keyAddress = keyEvent.getKeyAddress();
				final String keyAddressString = Integer.toHexString(keyAddress.getDeviceAddress()) + ":" + keyAddress.getInputAddress();
				if (!visibleInputs.containsKey(keyAddress))
					addConnectorEntry(keyAddress);
				final KeyData keyData = visibleInputs.get(keyAddress);
				keyData.setLastState(keyEvent.getType());
				keyData.getDisplayRadioButton().setText(keyAddressString + ": " + keyData.getLastState().name() + ", " + keyData.getActivationCount());
			}
		};
		// inputListPanel.add(new RadioButton(groupName, " Connector 1"));
		// inputListPanel.add(new RadioButton(groupName, " Connector 2"));

		final VerticalPanel verticalPanel = new VerticalPanel();
		horizontalPanel.add(verticalPanel);

		final ToggleButton enableListenButton = new ToggleButton("Listen");
		enableListenButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(final ValueChangeEvent<Boolean> event) {
				if (event.getValue().booleanValue())
					startRecording();
				else
					stopRecording();
			}
		});
		verticalPanel.add(enableListenButton);

		final Button resetButton = new Button("Reset");
		resetButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				resetVisibleEntries();
			}
		});
		verticalPanel.add(resetButton);
	}

	private void addConnectorEntry(final InputAddress keyAddress) {
		final String keyAddressString = Integer.toHexString(keyAddress.getDeviceAddress()) + ":" + keyAddress.getInputAddress();
		final RadioButton displayRadioButton = new RadioButton(groupName);
		displayRadioButton.setFormValue(keyAddressString);
		visibleInputs.put(keyAddress, new KeyData(displayRadioButton));
		inputListPanel.add(displayRadioButton);
	}

	@Override
	protected void finalize() throws Throwable {
		stopRecording();
		super.finalize();
	}

	public InputAddress getSelectedAddress() {
		for (final Entry<InputAddress, KeyData> visibleEntry : visibleInputs.entrySet())
			if (visibleEntry.getValue().getDisplayRadioButton().getValue().booleanValue())
				return visibleEntry.getKey();
		return null;
	}

	public void resetVisibleEntries() {
		inputListPanel.clear();
		visibleInputs.clear();
	}

	public void setSelectedAddress(final InputAddress address) {
		if (!visibleInputs.containsKey(address)) {
			addConnectorEntry(address);
			visibleInputs.get(address).getDisplayRadioButton().setText(address.toString());
		}
		final KeyData foundKeyData = visibleInputs.get(address);
		foundKeyData.getDisplayRadioButton().setValue(Boolean.TRUE);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible)
			startRecording();
		else
			stopRecording();
	}

	public void startRecording() {
		// resetVisibleEntries();
		EventDistributor.registerHandler(handler);
	}

	public void stopRecording() {
		EventDistributor.removeHandler(handler);
	}

}
