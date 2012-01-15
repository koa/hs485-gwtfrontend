package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.ChangeListener;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.TimeSpanValue;

public class PowerOnLimit implements Operation<PowerOnLimit> {
	private Variable<BinaryValue>		input;
	private Variable<BinaryValue>		output;
	private Variable<TimeSpanValue>	limit;

	@Override
	public OperationDescriptor<PowerOnLimit> getDesriptor() {
		return new OperationDescriptor<PowerOnLimit>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
					}

					@Override
					public void setupHandler() {
						variableManager.addChangeListener(input, new ChangeListener<BinaryValue>() {

							private final AtomicReference<ScheduledFuture<?>>	delayOffSchedule	= new AtomicReference<ScheduledFuture<?>>();

							@Override
							public void valueUpdated(final BinaryValue oldValue, final BinaryValue newValue) {
								if (oldValue == newValue)
									return;
								if (newValue == BinaryValue.ON) {
									final TimeSpanValue limitValue = variableManager.readVariable(limit);
									variableManager.writeVariable(output, BinaryValue.ON);
									final ScheduledFuture<?> oldSchedule = delayOffSchedule.getAndSet(executorService.schedule(new Runnable() {

										@Override
										public void run() {
											variableManager.writeVariable(output, BinaryValue.OFF);
										}
									}, limitValue.getCount(), limitValue.getUnit()));
									if (oldSchedule != null)
										oldSchedule.cancel(false);
								} else if (newValue == BinaryValue.OFF) {
									final ScheduledFuture<?> oldSchedule = delayOffSchedule.getAndSet(null);
									if (oldSchedule != null)
										oldSchedule.cancel(false);
									variableManager.writeVariable(output, BinaryValue.OFF);
								}
							}
						});
					}
				};
			}

			@Override
			public Collection<Variable<? extends Value>> listInputVariables() {
				return Arrays.asList(input, limit);
			}

			@Override
			public Collection<Variable<BinaryValue>> listOutputVariables() {
				return Arrays.asList(output);
			}
		};
	}

	public Variable<BinaryValue> getInput() {
		return input;
	}

	public Variable<TimeSpanValue> getLimit() {
		return limit;
	}

	public Variable<BinaryValue> getOutput() {
		return output;
	}

	public void setInput(final Variable<BinaryValue> input) {
		this.input = input;
	}

	public void setLimit(final Variable<TimeSpanValue> limit) {
		this.limit = limit;
	}

	public void setOutput(final Variable<BinaryValue> output) {
		this.output = output;
	}

}
