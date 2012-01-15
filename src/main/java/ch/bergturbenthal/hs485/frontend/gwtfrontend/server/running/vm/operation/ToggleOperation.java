package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.ChangeListener;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;

public class ToggleOperation implements Operation<ToggleOperation> {

	private Variable<BinaryValue>	input;
	private Variable<BinaryValue>	output;

	@Override
	public OperationDescriptor<ToggleOperation> getDesriptor() {
		return new OperationDescriptor<ToggleOperation>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
					}

					@Override
					public void setupHandler() {
						variableManager.addChangeListener(input, new ChangeListener<BinaryValue>() {
							@Override
							public void valueUpdated(final BinaryValue oldValue, final BinaryValue newValue) {
								if (oldValue == newValue)
									return;
								if (newValue != BinaryValue.ON)
									return;
								final BinaryValue oldOutput = variableManager.readVariable(output);
								if (oldOutput != BinaryValue.ON)
									variableManager.writeVariable(output, BinaryValue.ON);
								else
									variableManager.writeVariable(output, BinaryValue.OFF);
							}
						});
					}
				};
			}

			@Override
			public List<Variable<BinaryValue>> listInputVariables() {
				return Arrays.asList(input);
			}

			@Override
			public List<Variable<BinaryValue>> listOutputVariables() {
				return Arrays.asList(output);
			}
		};
	}

	public Variable<BinaryValue> getInput() {
		return input;
	}

	public Variable<BinaryValue> getOutput() {
		return output;
	}

	public void setInput(final Variable<BinaryValue> input) {
		this.input = input;
	}

	public void setOutput(final Variable<BinaryValue> output) {
		this.output = output;
	}

}
