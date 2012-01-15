package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.VM;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Value;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.api.Variable;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation.DebugDimmWriter;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation.DebugKeyReader;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation.KeyUpDownOperation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.operation.SetInitialValueOperation;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.BinaryValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.DimmValue;
import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.vm.value.TimeSpanValue;

public class TestVM {
	@Test
	public void testConnectDimmer() throws InterruptedException {

		final VM interpreter = new VM();

		final DebugKeyReader upKeyReader = new DebugKeyReader();
		final Variable<BinaryValue> upInputVariable = new Variable<BinaryValue>(BinaryValue.class);
		upKeyReader.setOutputVariable(upInputVariable);
		upKeyReader.setName("UP");

		interpreter.appendOperation(upKeyReader);

		final DebugKeyReader downKeyReader = new DebugKeyReader();
		final Variable<BinaryValue> downInputVariable = new Variable<BinaryValue>(BinaryValue.class);
		downKeyReader.setOutputVariable(downInputVariable);
		downKeyReader.setName("DOWN");
		interpreter.appendOperation(downKeyReader);

		final Variable<DimmValue> outputVariable = new Variable<DimmValue>();

		final KeyUpDownOperation keyUpDownOperation = new KeyUpDownOperation();
		keyUpDownOperation.setUpInput(upInputVariable);
		keyUpDownOperation.setDownInput(downInputVariable);
		keyUpDownOperation.setOutput(outputVariable);
		keyUpDownOperation.setIncrementAmount(makeConstVariable(interpreter, new DimmValue(16)));
		keyUpDownOperation.setIncrementRepeatWaitTime(makeConstVariable(interpreter, new TimeSpanValue(300, TimeUnit.MILLISECONDS)));
		keyUpDownOperation.setInitialWaitTime(makeConstVariable(interpreter, new TimeSpanValue(900, TimeUnit.MILLISECONDS)));
		interpreter.appendOperation(keyUpDownOperation);

		final DebugDimmWriter dimmWriter = new DebugDimmWriter();
		dimmWriter.setInputValue(outputVariable);
		dimmWriter.setName("Dimmer");

		interpreter.appendOperation(dimmWriter);

		final SetInitialValueOperation<DimmValue> initialValueOperation = new SetInitialValueOperation<DimmValue>(new DimmValue(0), outputVariable);
		interpreter.appendOperation(initialValueOperation);

		interpreter.start();
		upKeyReader.simulateVariableUpdate(BinaryValue.ON);
		Thread.sleep(300);
		upKeyReader.simulateVariableUpdate(BinaryValue.OFF);

		downKeyReader.simulateVariableUpdate(BinaryValue.ON);
		downKeyReader.simulateVariableUpdate(BinaryValue.OFF);
	}

	private <V extends Value> Variable<V> makeConstVariable(final VM interpreter, final V value) {
		final Variable<V> variable = new Variable<V>();
		interpreter.appendOperation(new SetInitialValueOperation<V>(value, variable));
		return variable;
	}
}
