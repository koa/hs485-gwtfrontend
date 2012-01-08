package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.RunningConfiguration;

public interface RunningConfigurationRepository extends CrudRepository<RunningConfiguration, String> {

}
