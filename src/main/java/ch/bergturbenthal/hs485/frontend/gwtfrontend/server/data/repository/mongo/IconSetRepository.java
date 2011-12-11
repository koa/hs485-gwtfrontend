package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo;

import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.IconSet;

public interface IconSetRepository extends CrudRepository<IconSet, String> {
}
