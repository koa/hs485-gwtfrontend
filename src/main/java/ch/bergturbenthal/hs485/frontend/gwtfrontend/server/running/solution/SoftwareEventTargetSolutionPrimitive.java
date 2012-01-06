package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.event.Event;

public interface SoftwareEventTargetSolutionPrimitive<E extends Event> extends ConfigSolutionPrimitive {
	void takeEvent(E event);
}
