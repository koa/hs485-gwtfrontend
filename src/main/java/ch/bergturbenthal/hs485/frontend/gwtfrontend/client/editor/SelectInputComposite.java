package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.editor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.CommunicationServiceAsync;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll.EventDistributor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.client.poll.EventHandler;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.InputDescription;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.KeyEvent.EventType;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Floor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputConnector;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputDevice;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.Plan;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SelectInputComposite extends Composite {
	private static class KeyData {
		private int									activationCount	= 1;
		private double							currentHum;
		private double							currentTemp;
		private final RadioButton		displayRadioButton;
		private KeyEvent.EventType	lastState;
		private boolean							tfsValue				= false;

		public KeyData(final RadioButton displayRadioButton) {
			super();
			this.displayRadioButton = displayRadioButton;
		}

		public int getActivationCount() {
			return activationCount;
		}

		public double getCurrentHum() {
			return currentHum;
		}

		public double getCurrentTemp() {
			return currentTemp;
		}

		public RadioButton getDisplayRadioButton() {
			return displayRadioButton;
		}

		public KeyEvent.EventType getLastState() {
			return lastState;
		}

		public boolean isTfsValue() {
			return tfsValue;
		}

		public void setCurrentHum(final double currentHum) {
			this.currentHum = currentHum;
		}

		public void setCurrentTemp(final double currentTemp) {
			this.currentTemp = currentTemp;
		}

		public void setLastState(final KeyEvent.EventType lastState) {
			if (lastState == EventType.DOWN && this.lastState == EventType.UP)
				activationCount += 1;
			this.lastState = lastState;
		}

		public void setTfsValue(final boolean tfsValue) {
			this.tfsValue = tfsValue;
		}

	}

	private final CommunicationServiceAsync		communicationService	= CommunicationServiceAsync.Util.getInstance();
	private final String											groupName;
	private final EventHandler								handler;
	private final VerticalPanel								inputListPanel;

	private Plan															plan;
	private final Timer												pollTimer;
	private final NumberFormat								tempFormat						= NumberFormat.getFormat("00.0");
	private final Map<InputAddress, KeyData>	visibleInputs					= new HashMap<InputAddress, SelectInputComposite.KeyData>();

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
				if (!visibleInputs.containsKey(keyAddress))
					addConnectorEntry(keyAddress, false);
				final KeyData keyData = visibleInputs.get(keyAddress);
				keyData.setLastState(keyEvent.getType());
				keyData.setTfsValue(false);
				updateKeyLabel(keyAddress, keyData);
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

		pollTimer = new Timer() {

			@Override
			public void run() {
				communicationService.listInputDevices(new AsyncCallback<Map<InputAddress, InputDescription>>() {
					@Override
					public void onFailure(final Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(final Map<InputAddress, InputDescription> result) {
						for (final Entry<InputAddress, InputDescription> inputConnectorEntry : result.entrySet()) {
							final InputDescription inputDescription = inputConnectorEntry.getValue();
							final InputAddress inputAddress = inputConnectorEntry.getKey();
							if (inputDescription.isHumiditySensor()) {
								if (!visibleInputs.containsKey(inputAddress))
									addConnectorEntry(inputAddress, true);
								communicationService.readHmuidity(inputAddress, new AsyncCallback<Float>() {
									@Override
									public void onFailure(final Throwable caught) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onSuccess(final Float result) {
										final KeyData keyData = visibleInputs.get(inputAddress);
										keyData.setCurrentHum(result.doubleValue());
										keyData.setTfsValue(true);
										updateKeyLabel(inputAddress, keyData);
									}
								});
							}
							if (inputDescription.isTemperatureSensor()) {
								if (!visibleInputs.containsKey(inputAddress))
									addConnectorEntry(inputAddress, true);
								communicationService.readTemperature(inputAddress, new AsyncCallback<Float>() {
									@Override
									public void onFailure(final Throwable caught) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onSuccess(final Float result) {
										final KeyData keyData = visibleInputs.get(inputAddress);
										keyData.setCurrentTemp(result.doubleValue());
										keyData.setTfsValue(true);
										updateKeyLabel(inputAddress, keyData);
									}
								});
							}
						}
					}
				});
			}
		};
		Widget parentWidget = this;
		while (parentWidget != null) {
			if (parentWidget instanceof HasCloseHandlers) {
				((HasCloseHandlers<Object>) parentWidget).addCloseHandler(new CloseHandler<Object>() {
					@Override
					public void onClose(final CloseEvent<Object> event) {
						stopRecording();
					}
				});
				break;
			}
			parentWidget = parentWidget.getParent();
		}
	}

	public Plan getPlan() {
		return plan;
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

	public void setPlan(final Plan plan) {
		this.plan = plan;
	}

	public void setSelectedAddress(final InputAddress address) {
		if (!visibleInputs.containsKey(address)) {
			addConnectorEntry(address, false);
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
		pollTimer.scheduleRepeating(10000);
	}

	public void stopRecording() {
		EventDistributor.removeHandler(handler);
		pollTimer.cancel();
	}

	@Override
	protected void finalize() throws Throwable {
		stopRecording();
		super.finalize();
	}

	private void addConnectorEntry(final InputAddress keyAddress, final boolean isTfs) {
		final String keyAddressString = Integer.toHexString(keyAddress.getDeviceAddress()) + ":" + keyAddress.getInputAddress();
		final RadioButton displayRadioButton = new RadioButton(groupName);
		displayRadioButton.setFormValue(keyAddressString);
		final KeyData keyData = new KeyData(displayRadioButton);
		keyData.setTfsValue(isTfs);
		visibleInputs.put(keyAddress, keyData);
		inputListPanel.add(displayRadioButton);
	}

	private void updateKeyLabel(final InputAddress keyAddress, final KeyData keyData) {
		final StringBuilder labelStringBuilder = new StringBuilder();

		labelStringBuilder.append(Integer.toHexString(keyAddress.getDeviceAddress()));
		labelStringBuilder.append(":");
		labelStringBuilder.append(keyAddress.getInputAddress());
		labelStringBuilder.append(": ");
		if (keyData.isTfsValue()) {
			labelStringBuilder.append(tempFormat.format(keyData.getCurrentTemp()));
			labelStringBuilder.append("°, ");
			labelStringBuilder.append(keyData.getCurrentHum());
			labelStringBuilder.append("%");
		} else {
			labelStringBuilder.append(keyData.getLastState().name());
			labelStringBuilder.append(", ");
			labelStringBuilder.append(keyData.getActivationCount());
		}
		if (plan != null)
			for (final Floor floor : plan.getFloors())
				for (final InputDevice inputDevice : floor.getInputDevices())
					for (final InputConnector inputConnector : inputDevice.getConnectors())
						if (keyAddress.equals(inputConnector.getAddress())) {
							labelStringBuilder.append(" [");
							labelStringBuilder.append(inputDevice.getName() + "-" + inputConnector.getConnectorName());
							labelStringBuilder.append("]");
							break;
						}
		keyData.getDisplayRadioButton().setText(labelStringBuilder.toString());
	}

}
