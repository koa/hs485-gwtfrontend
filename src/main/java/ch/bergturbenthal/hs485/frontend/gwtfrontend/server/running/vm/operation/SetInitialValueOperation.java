package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;

public class SetInitialValueOperation<V extends Value> implements Operation<SetInitialValueOperation<V>> {

	private final V						initialValue;
	private final Variable<V>	outputVariable;

	public SetInitialValueOperation(final V initialValue, final Variable<V> outputVariable) {
		this.initialValue = initialValue;
		this.outputVariable = outputVariable;
	}

	@Override
	public OperationDescriptor<SetInitialValueOperation<V>> getDesriptor() {
		return new OperationDescriptor<SetInitialValueOperation<V>>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
						variableManager.writeVariable(outputVariable, initialValue);
					}

					@Override
					public void setupHandler() {
					}
				};
			}

			@Override
			public Collection<Variable<Value>> listInputVariables() {
				return Collections.emptyList();
			}

			@Override
			public Collection<Variable<V>> listOutputVariables() {
				return Collections.singletonList(outputVariable);
			}
		};
	}

	public V getInitialValue() {
		return initialValue;
	}

	public Variable<V> getOutputVariable() {
		return outputVariable;
	}

}
