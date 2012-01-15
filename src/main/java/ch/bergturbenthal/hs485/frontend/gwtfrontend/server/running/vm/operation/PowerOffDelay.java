package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.ChangeListener;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.TimeSpanValue;

public class PowerOffDelay implements Operation<PowerOffDelay> {
	private Variable<BinaryValue>		input;
	private Variable<BinaryValue>		output;
	private Variable<TimeSpanValue>	delay;

	public Variable<TimeSpanValue> getDelay() {
		return delay;
	}

	@Override
	public OperationDescriptor<PowerOffDelay> getDesriptor() {
		return new OperationDescriptor<PowerOffDelay>() {
			private final Variable<BinaryValue>		input		= getInput();
			private final Variable<BinaryValue>		output	= getOutput();
			private final Variable<TimeSpanValue>	delay		= getDelay();

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
					}

					@Override
					public void setupHandler() {
						variableManager.addChangeListener(input, new ChangeListener<BinaryValue>() {

							private final AtomicReference<ScheduledFuture<?>>	schedule	= new AtomicReference<ScheduledFuture<?>>();

							@Override
							public void valueUpdated(final BinaryValue oldValue, final BinaryValue newValue) {
								if (oldValue == newValue)
									return;
								if (newValue == BinaryValue.ON) {
									cancelLastSchedule();
									variableManager.writeVariable(output, newValue);
								} else if (newValue == BinaryValue.OFF) {
									final TimeSpanValue delayValue = variableManager.readVariable(delay);
									cancelLastSchedule();
									schedule.set(executorService.schedule(new Runnable() {
										@Override
										public void run() {
											variableManager.writeVariable(output, newValue);
										}
									}, delayValue.getCount(), delayValue.getUnit()));
								}
							}

							private void cancelLastSchedule() {
								final ScheduledFuture<?> lastSchedule = schedule.getAndSet(null);
								if (lastSchedule != null)
									lastSchedule.cancel(false);
							}
						});
					}
				};
			}

			@Override
			public Collection<Variable<?>> listInputVariables() {
				return Arrays.asList(input, delay);
			}

			@Override
			public List<Variable<BinaryValue>> listOutputVariables() {
				return Collections.singletonList(output);
			}
		};
	}

	public Variable<BinaryValue> getInput() {
		return input;
	}

	public Variable<BinaryValue> getOutput() {
		return output;
	}

	public void setDelay(final Variable<TimeSpanValue> delay) {
		this.delay = delay;
	}

	public void setInput(final Variable<BinaryValue> input) {
		this.input = input;
	}

	public void setOutput(final Variable<BinaryValue> output) {
		this.output = output;
	}

}
