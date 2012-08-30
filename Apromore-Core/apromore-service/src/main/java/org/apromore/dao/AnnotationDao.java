package org.apromore.dao;

import java.util.List;

import org.apromore.dao.model.Annotation;

/**
 * Interface domain model Data access object Annotations.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 * @see org.apromore.dao.model.Process
 */
public interface AnnotationDao {

    /**
     * Find the annotation record by it's native uri id.
     * @param nativeUri the native uri
     * @return the annotation, a list of them for all the different canoncials.
     */
    List<Annotation> findByUri(final Integer nativeUri);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param annotationName the name of the annotation to get
     * @return the XML as a string
     */
    Annotation getAnnotation(Integer processId, String version, String annotationName);


    /**
     * Save the annotation.
     * @param annotation the annotation to persist
     */
    void save(Annotation annotation);

    /**
     * Update the process.
     * @param annotation the annotation to update
     * @return the updated object.
     */
    Annotation update(Annotation annotation);

    /**
     * Remove the annotation.
     * @param annotation the annotation to remove
     */
    void delete(Annotation annotation);

}
