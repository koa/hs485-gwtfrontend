package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.KeyEvent;

public interface HardwareEventSourceSolutionPrimitive extends ConfigSolutionPrimitive {
	int getDeviceAddress();

	int getInputAddress();

	KeyEvent.KeyType getKeyType();
}
