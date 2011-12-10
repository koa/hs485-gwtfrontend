/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository.mongo;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.db.FileData;

public interface FileDataRepository extends QueryDslPredicateExecutor<FileData>, CrudRepository<FileData, String> {
}
