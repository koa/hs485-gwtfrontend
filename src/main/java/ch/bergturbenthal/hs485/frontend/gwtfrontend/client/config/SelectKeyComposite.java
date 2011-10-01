package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll.EventDistributor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll.EventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent.EventType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SelectKeyComposite extends Composite {
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

	private final Map<KeyAddress, KeyData>	activationHistory	= new HashMap<KeyAddress, SelectKeyComposite.KeyData>();
	private final EventHandler							handler;
	private final VerticalPanel							verticalPanel;

	public SelectKeyComposite() {

		verticalPanel = new VerticalPanel();
		initWidget(verticalPanel);
		handler = new EventHandler() {

			@Override
			public synchronized void handleKeyEvent(final KeyEvent keyEvent) {
				final KeyAddress keyAddress = keyEvent.getKeyAddress();
				final String keyAddressString = Integer.toHexString(keyAddress.getModuleAddress()) + ":" + keyAddress.getInputAddress();
				if (!activationHistory.containsKey(keyAddress)) {
					final RadioButton displayRadioButton = new RadioButton("key-input-" + this);
					displayRadioButton.setFormValue(keyAddressString);
					activationHistory.put(keyAddress, new KeyData(displayRadioButton));
					verticalPanel.add(displayRadioButton);
				}
				final KeyData keyData = activationHistory.get(keyAddress);
				keyData.setLastState(keyEvent.getType());
				keyData.getDisplayRadioButton().setText(keyAddressString + ": " + keyData.getLastState().name() + ", " + keyData.getActivationCount());
			}
		};
		EventDistributor.registerHandler(handler);
	}

	@Override
	protected void finalize() throws Throwable {
		EventDistributor.removeHandler(handler);
		super.finalize();
	}

	public KeyAddress getSelectedAddress() {
		for (final Entry<KeyAddress, KeyData> visibleEntry : activationHistory.entrySet())
			if (visibleEntry.getValue().getDisplayRadioButton().getValue().booleanValue())
				return visibleEntry.getKey();
		return null;
	}

	public void resetVisibleEntries() {
		verticalPanel.clear();
		activationHistory.clear();
	}

}
