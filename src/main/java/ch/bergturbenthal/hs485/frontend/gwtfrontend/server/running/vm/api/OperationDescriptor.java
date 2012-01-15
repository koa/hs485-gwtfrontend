package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

public interface OperationDescriptor<O extends Operation<O>> {
	InterpreterStarter createStarter(RuntimeVariableManager variableManager, ScheduledExecutorService executorService);

	<V extends Value> Collection<Variable<V>> listInputVariables();

	<V extends Value> Collection<Variable<V>> listOutputVariables();
}
