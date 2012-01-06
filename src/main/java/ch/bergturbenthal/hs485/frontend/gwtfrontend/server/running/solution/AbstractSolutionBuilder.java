package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveKeyEventSource;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveOutputDeviceKeyEventSink;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.EventType;
import ch.eleveneye.hs485.api.MessageHandler;
import ch.eleveneye.hs485.api.data.KeyEventType;
import ch.eleveneye.hs485.api.data.KeyMessage;
import ch.eleveneye.hs485.api.data.KeyType;
import ch.eleveneye.hs485.device.KeyActor;
import ch.eleveneye.hs485.device.KeySensor;
import ch.eleveneye.hs485.device.Registry;
import ch.eleveneye.hs485.device.SwitchingActor;
import ch.eleveneye.hs485.device.TimedActor;
import ch.eleveneye.hs485.device.config.PairMode;
import ch.eleveneye.hs485.device.config.TimeMode;
import ch.eleveneye.hs485.device.physically.Actor;
import ch.eleveneye.hs485.device.physically.PairedSensorDevice;
import ch.eleveneye.hs485.device.physically.PhysicallySensor;

public class AbstractSolutionBuilder {

	protected static class DistributingMessageHandler implements MessageHandler {
		private final ArrayList<MessageHandler>	messageHandlers	= new ArrayList<MessageHandler>();

		public void appendHandler(final MessageHandler handler) {
			messageHandlers.add(handler);
			messageHandlers.trimToSize();
		}

		@Override
		public void handleMessage(final KeyMessage keyMessage) {
			for (final MessageHandler handler : messageHandlers)
				handler.handleMessage(keyMessage);
		}
	}

	private final Map<KeySensor, DistributingMessageHandler>	messageHandlers	= new HashMap<KeySensor, HS485SSolutionBuilder.DistributingMessageHandler>();
	protected final Registry																	registry;
	private static final Logger																logger					= LoggerFactory.getLogger(HS485SSolutionBuilder.class);
	protected final ScheduledExecutorService									executorService;

	public AbstractSolutionBuilder(final Registry registry, final ScheduledExecutorService executorService) {
		this.registry = registry;
		this.executorService = executorService;
	}

	protected void appendMessageHandler(final KeySensor keySensor, final MessageHandler messageHandler) throws IOException {
		if (!messageHandlers.containsKey(keySensor)) {
			final DistributingMessageHandler handler = new DistributingMessageHandler();
			keySensor.registerHandler(handler);
			messageHandlers.put(keySensor, handler);
		}
		messageHandlers.get(keySensor).appendHandler(messageHandler);
	}

	protected KeyMessage convertEventToMessage(final KeyEvent event) {
		logger.info("Acepting Key:" + event);
		final KeyMessage keyMessage = new KeyMessage();
		switch (event.getEventType()) {
		case DOWN:
			keyMessage.setKeyEventType(KeyEventType.PRESS);
			break;
		case HOLD:
			keyMessage.setKeyEventType(KeyEventType.HOLD);
			break;
		case UP:
			keyMessage.setKeyEventType(KeyEventType.RELEASE);
			break;
		default:
			logger.error("Unknown EventType: " + event.getEventType());
			return null;
		}
		switch (event.getKeyType()) {
		case ON:
			keyMessage.setKeyType(KeyType.UP);
			break;
		case OFF:
			keyMessage.setKeyType(KeyType.DOWN);
			break;
		case TOGGLE:
			keyMessage.setKeyType(KeyType.TOGGLE);
			break;
		default:
			logger.error("Unknown KeyType: " + event.getKeyType());
			return null;
		}
		keyMessage.setHitCount(event.getHitCount());
		return keyMessage;
	}

	protected boolean isKeyTypeToggle(final ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType keyType) {
		return keyType == ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType.TOGGLE;
	}

	protected KeyEvent makeKeyEvent(final KeyMessage keyMessage,
			final ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType keyType) {
		final KeyEvent event = new KeyEvent();
		event.setKeyType(keyType);
		event.setHitCount(keyMessage.getHitCount());
		switch (keyMessage.getKeyEventType()) {
		case PRESS:
			event.setEventType(EventType.DOWN);
			break;
		case HOLD:
			event.setEventType(EventType.HOLD);
			break;
		case RELEASE:
			event.setEventType(EventType.UP);
			break;
		}
		return event;
	}

	protected Collection<ConfigSolutionPrimitive> makeKeyPairInputSolution(final PrimitiveConnection connection, final PrimitiveKeyEventSource source) {
		try {
			final InputAddress inputAddress = source.getInput();
			final PhysicallySensor sensor = registry.getPhysicallySensor(inputAddress.getDeviceAddress(), inputAddress.getInputAddress());
			final KeySensor keySensor = (KeySensor) sensor;
			final ArrayList<ConfigSolutionPrimitive> ret = new ArrayList<ConfigSolutionPrimitive>();
			ret.add(new SoftwareEventSourceSolutionPrimitive() {

				@Override
				public void activateSolution(final ConfigSolutionPrimitive otherEndSolutionPrimitive) {
					try {
						if (otherEndSolutionPrimitive instanceof SoftwareKeyEventTargetSolutionPrimtive) {
							final SoftwareKeyEventTargetSolutionPrimtive eventTarget = (SoftwareKeyEventTargetSolutionPrimtive) otherEndSolutionPrimitive;
							appendMessageHandler(keySensor, new MessageHandler() {

								@Override
								public void handleMessage(final KeyMessage keyMessage) {
									final KeyEvent event = makeKeyEvent(keyMessage, source.getKeyType());
									eventTarget.takeEvent(event);
								}
							});
						} else
							throw new IllegalArgumentException("Cannot send events to type " + otherEndSolutionPrimitive.getClass());
					} catch (final IOException ex) {
						throw new RuntimeException(ex);
					}
				}

				@Override
				public boolean canCoexistWith(final ConfigSolutionPrimitive otherSolution) {
					if (connection == otherSolution.getConnection())
						return otherSolution instanceof SoftwareKeyEventTargetSolutionPrimtive;
					return true;
				}

				@Override
				public int cost() {
					return 10;
				}

				@Override
				public PrimitiveConnection getConnection() {
					return connection;
				}
			});
			final ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType keyType = source.getKeyType();
			if (keyType == ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType.TOGGLE
					|| keyType == ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType.ON && inputAddress.getInputAddress() == 1
					|| keyType == ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType.OFF && inputAddress.getInputAddress() == 0)
				ret.add(new HardwareEventSourceSolutionPrimitive() {

					@Override
					public void activateSolution(final ConfigSolutionPrimitive otherEndSolutionPrimitive) {
						try {
							if (otherEndSolutionPrimitive instanceof HardwareKeyEventTargetSolutionPrimitive) {
								final HardwareKeyEventTargetSolutionPrimitive keyTarget = (HardwareKeyEventTargetSolutionPrimitive) otherEndSolutionPrimitive;
								final PairedSensorDevice pairedDevice = (PairedSensorDevice) registry.getPhysicallyDevice(inputAddress.getDeviceAddress());
								final PairMode pairMode = keyType == ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType.TOGGLE ? PairMode.SPLIT
										: PairMode.JOINT;
								pairedDevice.setInputPairMode(0, pairMode);
								keySensor.addActor(registry.getActor(keyTarget.getDeviceAddress(), pairMode == PairMode.SPLIT ? keyTarget.getOutputAddress() : 0));
							} else
								throw new IllegalArgumentException("Cannot send Events to " + otherEndSolutionPrimitive.getClass().getName());
						} catch (final IOException ex) {
							throw new RuntimeException(ex);
						}
					}

					@Override
					public boolean canCoexistWith(final ConfigSolutionPrimitive otherSolution) {
						if (connection == otherSolution.getConnection())
							return otherSolution instanceof HardwareKeyEventTargetSolutionPrimitive;
						if (otherSolution instanceof HardwareEventSourceSolutionPrimitive) {
							final HardwareEventSourceSolutionPrimitive otherHardwareEventSource = (HardwareEventSourceSolutionPrimitive) otherSolution;
							if (otherHardwareEventSource.getDeviceAddress() == getDeviceAddress())
								return isKeyTypeToggle(otherHardwareEventSource.getKeyType()) == isKeyTypeToggle(getKeyType());
						}
						return true;
					}

					@Override
					public int cost() {
						return 1;
					}

					@Override
					public PrimitiveConnection getConnection() {
						return connection;
					}

					@Override
					public int getDeviceAddress() {
						return inputAddress.getDeviceAddress();
					}

					@Override
					public int getInputAddress() {
						return inputAddress.getInputAddress();
					}

					@Override
					public ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType getKeyType() {
						return keyType;
					}
				});

			return ret;
		} catch (final IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	protected Collection<ConfigSolutionPrimitive> makeOutputSolution(final PrimitiveConnection connection,
			final PrimitiveOutputDeviceKeyEventSink outputDeviceSink) throws IOException {
		final OutputAddress outputAddress = outputDeviceSink.getAddress();
		final Actor actor = registry.getActor(outputAddress.getDeviceAddress(), outputAddress.getOutputAddress());
		final KeyActor keyActor = (KeyActor) actor;
		final SwitchingActor switchingActor = (SwitchingActor) actor;
		final TimedActor timedActor = (TimedActor) actor;
		final ArrayList<ConfigSolutionPrimitive> ret = new ArrayList<ConfigSolutionPrimitive>();

		// full software implementation
		ret.add(new SoftwareKeyEventTargetSolutionPrimtive() {

			private final AtomicLong	offTime	= new AtomicLong(Long.MAX_VALUE);

			@Override
			public void activateSolution(final ConfigSolutionPrimitive otherEndSolutionPrimitive) {
			}

			@Override
			public boolean canCoexistWith(final ConfigSolutionPrimitive otherSolution) {
				if (connection == otherSolution.getConnection())
					return otherSolution instanceof SoftwareEventSourceSolutionPrimitive;
				if (otherSolution instanceof TimerSettingOutputSolution) {
					final TimerSettingOutputSolution timerSolution = (TimerSettingOutputSolution) otherSolution;
					if (timerSolution.getDeviceAddress() == outputAddress.getDeviceAddress()
							&& timerSolution.getOutputAddress() == outputAddress.getOutputAddress())
						if (outputDeviceSink.getOffTime() != null)
							return outputDeviceSink.getOffTime().intValue() <= timerSolution.getTimerValue();
						else
							return timerSolution.getTimerValue() == Integer.MAX_VALUE;
				}
				return true;
			}

			@Override
			public int cost() {
				return 10;
			}

			@Override
			public PrimitiveConnection getConnection() {
				return connection;
			}

			@Override
			public void takeEvent(final KeyEvent event) {
				try {
					if (event.getEventType() == EventType.UP
							&& event.getKeyType() == ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent.KeyType.ON
							&& outputDeviceSink.getOffTime() != null) {
						final int offDelay = outputDeviceSink.getOffTime().intValue();
						offTime.set(System.currentTimeMillis() + offDelay * 1000 - 1000);
						executorService.schedule(new Runnable() {
							@Override
							public void run() {
								if (offTime.get() <= System.currentTimeMillis())
									try {
										switchingActor.setOff();
									} catch (final IOException e) {
										logger.warn("Cannot switch off actor " + switchingActor, e);
									}
							}
						}, offDelay, TimeUnit.SECONDS);
					} else
						offTime.set(Long.MAX_VALUE);
					keyActor.sendKeyMessage(convertEventToMessage(event));
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		// full hardware implementation
		ret.add(new TimerSettingOutputSolution() {

			@Override
			public void activateSolution(final ConfigSolutionPrimitive otherEndSolutionPrimitive) {
				try {
					if (outputDeviceSink.getOffTime() != null) {
						timedActor.setTimeMode(TimeMode.AUTO_OFF);
						timedActor.setTimeValue(outputDeviceSink.getOffTime().intValue());
					} else
						timedActor.setTimeMode(TimeMode.NONE);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public boolean canCoexistWith(final ConfigSolutionPrimitive otherSolution) {
				if (connection == otherSolution.getConnection())
					return otherSolution instanceof HardwareEventSourceSolutionPrimitive;
				if (otherSolution instanceof TimerSettingOutputSolution) {
					final TimerSettingOutputSolution timerSolution = (TimerSettingOutputSolution) otherSolution;
					if (timerSolution.getOutputAddress() == getOutputAddress() && timerSolution.getDeviceAddress() == getDeviceAddress())
						return timerSolution.getTimerValue() == getTimerValue();
				}
				return true;
			}

			@Override
			public int cost() {
				return 1;
			}

			@Override
			public PrimitiveConnection getConnection() {
				return connection;
			}

			@Override
			public int getDeviceAddress() {
				return outputAddress.getDeviceAddress();
			}

			@Override
			public int getOutputAddress() {
				return outputAddress.getOutputAddress();
			}

			@Override
			public int getTimerValue() {
				if (outputDeviceSink.getOffTime() != null)
					return outputDeviceSink.getOffTime().intValue();
				else
					return Integer.MAX_VALUE;
			}
		});
		return ret;
	}

}