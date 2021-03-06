package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.io.Closeable;
import java.util.Collection;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;

public interface ConfigSolutionPrimitive extends Closeable {
	public static enum ActivationPhase {
		PREPARE, EXECUTE
	}

	void activateSolution(ConfigSolutionPrimitive otherEndSolutionPrimitive, ActivationPhase phase,
			Collection<ConfigSolutionPrimitive> allSelectedPrimitives);

	boolean canCoexistWith(ConfigSolutionPrimitive otherSolution);

	int cost();

	PrimitiveConnection getConnection();
}
