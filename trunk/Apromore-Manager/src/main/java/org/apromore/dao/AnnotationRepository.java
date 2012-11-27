package org.apromore.dao;

import org.apromore.dao.model.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Interface domain model Data access object Annotations.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Annotation
 */
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Integer> {

    /**
     * Find the annotation record by it's native uri id.
     * @param nativeUri the native uri
     * @return the annotation, a list of them for all the different canoncials.
     */
    @Query("SELECT a FROM Annotation a WHERE a.natve.id = ?1")
    List<Annotation> findByUri(final Integer nativeUri);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param processId the processId of the Canonical format.
     * @param versionName the version of the canonical format
     * @param name the name of the annotation to get
     * @return the XML as a string
     */
    @Query("SELECT a FROM Annotation a, ProcessModelVersion p WHERE a.processModelVersion.id = p.id " +
            "AND p.processBranch.id = ?1 AND p.versionName = ?2 AND a.name = ?3")
    Annotation getAnnotation(Integer processId, String versionName, String name);

}
