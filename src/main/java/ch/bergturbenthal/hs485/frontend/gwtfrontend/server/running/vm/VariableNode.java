package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm;

import java.util.ArrayList;
import java.util.Collection;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;

public class VariableNode {
	private final Collection<OperationNode>	inputOperations		= new ArrayList<OperationNode>();
	private final Collection<OperationNode>	outputOperations	= new ArrayList<OperationNode>();
	private final Variable<?>								variable;

	public VariableNode(final Variable<?> variable) {
		this.variable = variable;
	}

	public Collection<OperationNode> getInputOperations() {
		return inputOperations;
	}

	public Collection<OperationNode> getOutputOperations() {
		return outputOperations;
	}

	public Variable<?> getVariable() {
		return variable;
	}
}