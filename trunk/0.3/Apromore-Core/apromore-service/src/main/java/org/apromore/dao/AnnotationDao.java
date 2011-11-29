package org.apromore.dao;

import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.exception.AnnotationNotFoundException;

import java.util.List;

/**
 * Interface domain model Data access object Annotations.
 *
 * @see org.apromore.dao.model.Process
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @version 1.0
 */
public interface AnnotationDao {

    /**
     * Find the annotation record by it's native uri id.
     * @return the annotation, a list of them for all the different canoncials.
     */
    public List<Annotation> findByUri(final int nativeUri);

    /**
     * Get the Canonical format. this is just a string but contains the xml Canonical Format.
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param annotationName the name of the annotation to get
     * @return the XML as a string
     * @throws AnnotationNotFoundException if the annotation can not be found
     */
    Annotation getAnnotation(long processId, String version, String annotationName) throws AnnotationNotFoundException;


    /**
     * Save the annotation.
     * @param annotation the annotation to persist
     */
    void save(Annotation annotation);

    /**
     * Update the process.
     * @param annotation the annotation to update
     */
    void update(Annotation annotation);

    /**
     * Remove the annotation.
     * @param annotation the annotation to remove
     */
    void delete(Annotation annotation);

}
