package org.apromore.service;

import java.util.Set;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.AnnotationProcessor;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.exception.PluginNotFoundException;

/**
 * Interface for the Annotation Pre Processor Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface AnnotationService {

    /**
     * Lists all installed Annotation Pre Processors for the given native process formats.
     * @param processType the source and target process format supports
     * @return Set of Annotation Pre Processors that support the native process formats
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    Set<AnnotationProcessor> listBySourceAndTargetProcessType(String processType) throws PluginNotFoundException;

    /**
     * Finds first Annotation Pre Processor with the given native types.
     * @param processType the source and target process format supports
     * @return Annotation Pre Processor that support the native process formats
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    AnnotationProcessor findBySourceAndTargetProcessType(String processType) throws PluginNotFoundException;

    /**
     * Finds first Annotation Pre Processor with given native type with given name and specified version.
     * @param processType the source and target process format supports
     * @param name the name of the processor
     * @param version the version of the processor
     * @return annotation that support the native process format
     * @throws org.apromore.plugin.exception.PluginNotFoundException
     */
    AnnotationProcessor findBySourceAndTargetProcessTypeAndNameAndVersion(String processType, String name, String version)
            throws PluginNotFoundException;

    /**
     * Run the Pre Processing for a canonical format and it's annotations. Updates only the ANF and returns the results.
     * @param targetType the target process format.
     * @param canonicalFormat the canonical process
     * @param annotationFormat the canonical annotations
     * @return AnnotationsType updated with the correct layout info for the target format.
     */
    AnnotationsType preProcess(final String targetType, final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat);

}
