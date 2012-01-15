package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.DimmValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.OutputAddress;

public class HS485DimmWriter implements Operation<HS485DimmWriter> {
	private Variable<DimmValue>	inputValue;
	private OutputAddress				outputAddress;

	@Override
	public OperationDescriptor<HS485DimmWriter> getDesriptor() {
		return new OperationDescriptor<HS485DimmWriter>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
					}

					@Override
					public void setupHandler() {
						// TODO Auto-generated method stub

					}
				};
			}

			@Override
			public List<Variable<DimmValue>> listInputVariables() {
				return Collections.singletonList(inputValue);
			}

			@Override
			public <V extends Value> Collection<Variable<V>> listOutputVariables() {
				return Collections.emptyList();
			}

		};
	}

	public Variable<DimmValue> getInputValue() {
		return inputValue;
	}

	public OutputAddress getOutputAddress() {
		return outputAddress;
	}

	public void setInputValue(final Variable<DimmValue> inputValue) {
		this.inputValue = inputValue;
	}

	public void setOutputAddress(final OutputAddress outputAddress) {
		this.outputAddress = outputAddress;
	}

}
