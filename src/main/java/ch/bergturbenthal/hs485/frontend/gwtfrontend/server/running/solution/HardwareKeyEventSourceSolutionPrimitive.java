package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

public interface HardwareKeyEventSourceSolutionPrimitive extends ConfigSolutionPrimitive, KeyEventSourceSolution {
	int getDeviceAddress();

	int getInputAddress();

	KeyEvent.KeyType getKeyType();
}
