package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.ChangeListener;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.DimmValue;

public class DebugDimmWriter implements Operation<DebugDimmWriter> {
	private String							name;
	private Variable<DimmValue>	inputValue;
	private static final Logger	logger	= LoggerFactory.getLogger(DebugDimmWriter.class);

	@Override
	public OperationDescriptor<DebugDimmWriter> getDesriptor() {
		return new OperationDescriptor<DebugDimmWriter>() {

			@Override
			public InterpreterStarter createStarter(final RuntimeVariableManager variableManager, final ScheduledExecutorService executorService) {
				return new InterpreterStarter() {

					@Override
					public void initVariables() {
					}

					@Override
					public void setupHandler() {
						variableManager.addChangeListener(inputValue, new ChangeListener<DimmValue>() {

							@Override
							public void valueUpdated(final DimmValue oldValue, final DimmValue newValue) {
								logger.info("DebugWriter: " + getName() + ", Value: " + newValue.getValue());
							}

						});
					}
				};
			}

			@Override
			public <V extends Value> Collection<Variable<V>> listInputVariables() {
				return Collections.singletonList((Variable<V>) inputValue);
			}

			@Override
			public Collection<Variable<Value>> listOutputVariables() {
				return Collections.emptyList();
			}

		};
	}

	public Variable<DimmValue> getInputValue() {
		return inputValue;
	}

	public String getName() {
		return name;
	}

	public void setInputValue(final Variable<DimmValue> inputValue) {
		this.inputValue = inputValue;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
