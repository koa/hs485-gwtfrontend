package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.ChangeListener;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.DimmValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.TimeSpanValue;

public class KeyUpDownOperation implements Operation<KeyUpDownOperation> {
	public final class KeyUpDownInterpreterStarter implements InterpreterStarter {
		private final RuntimeVariableManager							variableManager;
		private final ScheduledExecutorService						executorService;
		private final AtomicReference<ScheduledFuture<?>>	incrementScheduler	= new AtomicReference<ScheduledFuture<?>>();
		private final AtomicReference<ScheduledFuture<?>>	decrementScheduler	= new AtomicReference<ScheduledFuture<?>>();

		public KeyUpDownInterpreterStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
			this.variableManager = variableManager;
			this.executorService = executorService;
		}

		@Override
		public void initVariables() {
			// TODO Auto-generated method stub

		}

		@Override
		public void setupHandler() {
			variableManager.addChangeListener(upInput, new ChangeListener<BinaryValue>() {

				private long	onTime	= 0;

				@Override
				public void valueUpdated(final BinaryValue oldValue, final BinaryValue newValue) {
					if (oldValue != BinaryValue.OFF && newValue == BinaryValue.OFF) {
						final TimeSpanValue initalWaitValue = variableManager.readVariable(initialWaitTime);
						if (System.currentTimeMillis() - onTime < initalWaitValue.convert(TimeUnit.MILLISECONDS))
							variableManager.writeVariable(output, new DimmValue(255));
						final ScheduledFuture<?> runningIncrementScheduler = incrementScheduler.get();
						if (runningIncrementScheduler != null)
							runningIncrementScheduler.cancel(false);
					}
					if (oldValue != BinaryValue.ON && newValue == BinaryValue.ON) {
						final ScheduledFuture<?> runningScheduler = decrementScheduler.get();
						if (runningScheduler != null)
							runningScheduler.cancel(false);
						onTime = System.currentTimeMillis();
						final TimeSpanValue initalWaitValue = variableManager.readVariable(initialWaitTime);
						final TimeSpanValue incrementRepeatTimeValue = variableManager.readVariable(incrementRepeatWaitTime);
						final byte incrementCount = variableManager.readVariable(incrementAmount).getValue();
						incrementScheduler.set(executorService.scheduleAtFixedRate(new Runnable() {
							@Override
							public void run() {
								final DimmValue oldDimmValue = variableManager.readVariable(output);
								final int newValue = oldDimmValue.getValue() + incrementCount;
								if (newValue >= 255) {
									variableManager.writeVariable(output, new DimmValue(255));
									final ScheduledFuture<?> runningScheduler = incrementScheduler.get();
									if (runningScheduler != null)
										runningScheduler.cancel(false);
								} else
									variableManager.writeVariable(output, new DimmValue(newValue));
							}
						}, initalWaitValue.convert(TimeUnit.MILLISECONDS), incrementRepeatTimeValue.convert(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS));
					}
				}
			});
			variableManager.addChangeListener(downInput, new ChangeListener<BinaryValue>() {
				private long	onTime	= 0;

				@Override
				public void valueUpdated(final BinaryValue oldValue, final BinaryValue newValue) {
					if (oldValue != BinaryValue.OFF && newValue == BinaryValue.OFF) {
						final TimeSpanValue initalWaitValue = variableManager.readVariable(initialWaitTime);
						if (System.currentTimeMillis() - onTime < initalWaitValue.convert(TimeUnit.MILLISECONDS))
							variableManager.writeVariable(output, new DimmValue(0));
						final ScheduledFuture<?> runningScheduler = decrementScheduler.get();
						if (runningScheduler != null)
							runningScheduler.cancel(false);
					}
					if (oldValue != BinaryValue.ON && newValue == BinaryValue.ON) {
						final ScheduledFuture<?> runningScheduler = incrementScheduler.get();
						if (runningScheduler != null)
							runningScheduler.cancel(false);
						onTime = System.currentTimeMillis();
						final TimeSpanValue initalWaitValue = variableManager.readVariable(initialWaitTime);
						final TimeSpanValue incrementRepeatTimeValue = variableManager.readVariable(incrementRepeatWaitTime);
						final byte decrementCount = variableManager.readVariable(incrementAmount).getValue();
						decrementScheduler.set(executorService.scheduleAtFixedRate(new Runnable() {

							@Override
							public void run() {
								final DimmValue oldDimmValue = variableManager.readVariable(output);
								final int newValue = oldDimmValue.getValue() - decrementCount;
								if (newValue <= 0) {
									variableManager.writeVariable(output, new DimmValue(0));
									final ScheduledFuture<?> runningScheduler = decrementScheduler.get();
									if (runningScheduler != null)
										runningScheduler.cancel(false);
								} else
									variableManager.writeVariable(output, new DimmValue(newValue));
							}
						}, initalWaitValue.convert(TimeUnit.MILLISECONDS), incrementRepeatTimeValue.convert(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS));
					}
				}
			});
		}
	}

	private Variable<DimmValue>			output;
	private Variable<BinaryValue>		upInput;
	private Variable<BinaryValue>		downInput;
	private Variable<TimeSpanValue>	initialWaitTime;
	private Variable<TimeSpanValue>	incrementRepeatWaitTime;
	private Variable<DimmValue>			incrementAmount;

	@Override
	public OperationDescriptor<KeyUpDownOperation> getDesriptor() {
		return new OperationDescriptor<KeyUpDownOperation>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new KeyUpDownInterpreterStarter(variableManager, executorService);
			}

			@Override
			public List<Variable<? extends Value>> listInputVariables() {
				return Arrays.asList(upInput, downInput, initialWaitTime, incrementRepeatWaitTime, incrementAmount);
			}

			@Override
			public Collection<Variable<DimmValue>> listOutputVariables() {
				return Collections.singletonList(output);
			}
		};
	}

	public Variable<BinaryValue> getDownInput() {
		return downInput;
	}

	public Variable<DimmValue> getIncrementAmount() {
		return incrementAmount;
	}

	public Variable<TimeSpanValue> getIncrementRepeatWaitTime() {
		return incrementRepeatWaitTime;
	}

	public Variable<TimeSpanValue> getInitialWaitTime() {
		return initialWaitTime;
	}

	public Variable<DimmValue> getOutput() {
		return output;
	}

	public Variable<BinaryValue> getUpInput() {
		return upInput;
	}

	public void setDownInput(final Variable<BinaryValue> downInput) {
		this.downInput = downInput;
	}

	public void setIncrementAmount(final Variable<DimmValue> incrementAmount) {
		this.incrementAmount = incrementAmount;
	}

	public void setIncrementRepeatWaitTime(final Variable<TimeSpanValue> incrementRepeatWaitTime) {
		this.incrementRepeatWaitTime = incrementRepeatWaitTime;
	}

	public void setInitialWaitTime(final Variable<TimeSpanValue> initialWaitTime) {
		this.initialWaitTime = initialWaitTime;
	}

	public void setOutput(final Variable<DimmValue> output) {
		this.output = output;
	}

	public void setUpInput(final Variable<BinaryValue> upInput) {
		this.upInput = upInput;
	}
}
