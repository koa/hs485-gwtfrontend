/**
 * 
 */
package ch.bergturbenthal.hs485.frontend.gwtfrontend.server.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ch.bergturbenthal.hs485.frontend.gwtfrontend.shared.FileData;

/**
 *
 */
public interface FileDataRepository extends CrudRepository<FileData, String> {
	@Query("select f.fileName from FileData f")
	List<String> listAllFiles();

	@Query("select f.fileName from FileData f where f.mimeType=:mimeType")
	List<String> listFilesByMimeType(@Param("mimeType") String mimeType);
}
