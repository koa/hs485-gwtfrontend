package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.InterpreterStarter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.OperationDescriptor;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.RuntimeVariableManager;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation.SetInitialValueOperation;

public class VM implements RuntimeVariableManager {

	private static class VariableHolder {
		final Collection<ChangeListener<? extends Value>>	changeListeners	= new ArrayList<ChangeListener<? extends Value>>();

		private final AtomicReference<Value>							currentValue		= new AtomicReference<Value>();

		public Value getCurrentValue() {
			return currentValue.get();
		}

		public void setCurrentValue(final Value currentValue) {
			this.currentValue.set(currentValue);
		}
	}

	private final Map<Variable<Value>, VariableHolder>	variables		= new IdentityHashMap<Variable<Value>, VariableHolder>();

	private static final Logger													logger			= LoggerFactory.getLogger(VM.class);

	private final Collection<Operation<?>>							operations	= new ArrayList<Operation<?>>();

	@Override
	public <V extends Value> void addChangeListener(final Variable<V> variable, final ChangeListener<V> handler) {
		variables.get(variable).changeListeners.add(handler);
	}

	public void appendOperation(final Operation operation) {
		operations.add(operation);
	}

	@Override
	public <V extends Value> V readVariable(final Variable<V> variable) {
		return (V) variables.get(variable).getCurrentValue();
	}

	@SuppressWarnings("unchecked")
	public void start() {

		final Collection<OperationNode> operationNodes = new ArrayList<OperationNode>();
		final Map<Variable<?>, VariableNode> variableNodes = new HashMap<Variable<?>, VariableNode>();
		for (final Operation<?> operation : operations) {
			final OperationNode operationNode = new OperationNode(operation);
			operationNodes.add(operationNode);

			final OperationDescriptor<?> desriptor = operation.getDesriptor();
			for (final Variable<Value> inputVariable : desriptor.listInputVariables()) {
				VariableNode node = variableNodes.get(inputVariable);
				if (node == null) {
					node = new VariableNode(inputVariable);
					variableNodes.put(inputVariable, node);
				}
				node.getInputOperations().add(operationNode);
				operationNode.getInputVariables().add(node);
			}
			for (final Variable<Value> outputVariable : desriptor.listOutputVariables()) {
				VariableNode node = variableNodes.get(outputVariable);
				if (node == null) {
					node = new VariableNode(outputVariable);
					variableNodes.put(outputVariable, node);
				}
				node.getOutputOperations().add(operationNode);
				operationNode.getOutputVariables().add(node);
			}
		}
		final Collection<VariableNode> foundVariables = variableNodes.values();

		int numOfConstants = 0;
		for (final VariableNode variableNode : foundVariables) {
			final Value contstantValue = getConstantValue(variableNode);
			if (contstantValue != null)
				numOfConstants += 1;
		}
		logger.info(numOfConstants + " Constants found");

		for (final VariableNode variableNode : foundVariables)
			createVariable(variableNode.getVariable());

		final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(2);
		final Collection<InterpreterStarter> starters = new ArrayList<InterpreterStarter>();
		for (final Operation<?> operation : operations) {
			final OperationDescriptor<?> desriptor = operation.getDesriptor();
			final InterpreterStarter starter = desriptor.createStarter(this, threadPool);
			starters.add(starter);
		}

		for (final InterpreterStarter starter : starters)
			starter.setupHandler();
		for (final InterpreterStarter starter : starters)
			starter.initVariables();
	}

	@Override
	public <V extends Value> void writeVariable(final Variable<V> variable, final V value) {
		final VariableHolder variableHolder = variables.get(variable);
		final Value oldValue = variableHolder.getCurrentValue();
		variableHolder.setCurrentValue(value);
		for (final ChangeListener<? extends Value> listener : variableHolder.changeListeners)
			((ChangeListener<Value>) listener).valueUpdated(oldValue, value);
	}

	private void createVariable(final Variable<? extends Value>... newVariables) {
		for (final Variable<? extends Value> variable : newVariables)
			if (!variables.containsKey(variable))
				variables.put((Variable<Value>) variable, new VariableHolder());
	}

	private Value getConstantValue(final VariableNode variableNode) {
		final Collection<OperationNode> outputOperations = variableNode.getOutputOperations();
		if (outputOperations.size() != 1)
			return null;
		final OperationNode operationNode = outputOperations.iterator().next();
		if (operationNode.getOperation() instanceof SetInitialValueOperation) {
			final SetInitialValueOperation<?> setValueOperation = (SetInitialValueOperation<?>) operationNode.getOperation();
			return setValueOperation.getInitialValue();
		} else
			return null;
	}
}
