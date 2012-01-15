package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;

public class DebugKeyReader implements Operation<DebugKeyReader> {
	private String																		name;
	private Variable<BinaryValue>											outputVariable;
	private final Collection<RuntimeVariableManager>	variableManagers	= new ArrayList<RuntimeVariableManager>();

	@Override
	public OperationDescriptor<DebugKeyReader> getDesriptor() {
		return new OperationDescriptor<DebugKeyReader>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
					}

					@Override
					public void setupHandler() {
						variableManagers.add(variableManager);
					}
				};
			}

			@Override
			public <V extends Value> Collection<Variable<V>> listInputVariables() {
				return Collections.emptyList();
			}

			@Override
			public Collection<Variable<BinaryValue>> listOutputVariables() {
				return Collections.singletonList(outputVariable);
			}

		};
	}

	public String getName() {
		return name;
	}

	public Variable<BinaryValue> getOutputVariable() {
		return outputVariable;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOutputVariable(final Variable<BinaryValue> outputVariable) {
		this.outputVariable = outputVariable;
	}

	public void simulateVariableUpdate(final BinaryValue newValue) {
		for (final RuntimeVariableManager manager : variableManagers)
			manager.writeVariable(outputVariable, newValue);
	}

}
