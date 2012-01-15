package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm;

import java.util.ArrayList;
import java.util.Collection;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Operation;

public class OperationNode {
	private final Operation<?>							operation;

	private final Collection<VariableNode>	inputVariables	= new ArrayList<VariableNode>();

	private final Collection<VariableNode>	outputVariables	= new ArrayList<VariableNode>();

	public OperationNode(final Operation<?> operation) {
		this.operation = operation;
	}

	public Collection<VariableNode> getInputVariables() {
		return inputVariables;
	}

	public Operation<?> getOperation() {
		return operation;
	}

	public Collection<VariableNode> getOutputVariables() {
		return outputVariables;
	}

}