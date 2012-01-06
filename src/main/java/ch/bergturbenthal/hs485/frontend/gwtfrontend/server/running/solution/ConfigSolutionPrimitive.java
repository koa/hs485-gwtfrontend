package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;

public interface ConfigSolutionPrimitive {
	void activateSolution(ConfigSolutionPrimitive otherEndSolutionPrimitive);

	boolean canCoexistWith(ConfigSolutionPrimitive otherSolution);

	int cost();

	PrimitiveConnection getConnection();
}
