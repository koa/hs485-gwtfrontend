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
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.InputAddress;

public class HS485KeyReader implements Operation<HS485KeyReader> {
	private Variable<BinaryValue>	outputVariable;
	private InputAddress					sourceAddress;

	@Override
	public OperationDescriptor<HS485KeyReader> getDesriptor() {
		return new OperationDescriptor<HS485KeyReader>() {

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
			public <V extends Value> Collection<Variable<V>> listInputVariables() {
				return Collections.emptyList();
			}

			@Override
			public List<Variable<BinaryValue>> listOutputVariables() {
				return Collections.singletonList(outputVariable);
			}

		};
	}

	public Variable<BinaryValue> getOutputVariable() {
		return outputVariable;
	}

	public InputAddress getSourceAddress() {
		return sourceAddress;
	}

	public void setOutputVariable(final Variable<BinaryValue> outputVariable) {
		this.outputVariable = outputVariable;
	}

	public void setSourceAddress(final InputAddress sourceAddress) {
		this.sourceAddress = sourceAddress;
	}
}
