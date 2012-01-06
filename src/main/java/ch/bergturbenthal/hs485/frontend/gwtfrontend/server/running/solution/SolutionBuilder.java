package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.solution;

import java.util.Collection;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.server.running.primitive.PrimitiveConnection;

public interface SolutionBuilder {
	Collection<ConfigSolutionPrimitive> makeSinkSolutionVariants(PrimitiveConnection connection);

	Collection<ConfigSolutionPrimitive> makeSourceSolutionVariants(PrimitiveConnection connection);
}
