package org.apromore.service;

import org.apromore.service.model.Format;
import org.apromore.exception.ExportFormatException;
import org.apromore.model.ProcessSummariesType;

import javax.activation.DataSource;

/**
 * Interface for the Process Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ProcessService {

    /**
     * Loads all the process Summaries. It will either get all or use the keywords parameter
     * to load a subset of the processes.
     * @param searchExpression the search expression to limit the search.
     * @return The ProcessSummariesType used for Webservices.
     */
    ProcessSummariesType readProcessSummaries(final String searchExpression);

    /**
     * Export a BMP Model but in a particular format.
     * @param processId the process model id
     * @param version the version of the process model
     * @param nativeType the format of the model
     * @return the XML but as a datasource object
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    DataSource exportFormat(final long processId, final String version, final String nativeType) throws ExportFormatException;

    /**
     * Reads the Canonical (ANF).
     * @param processId the processId of the Canonical format.
     * @param version the version of the canonical format
     * @param withAnn Do we return the Canonical with annotations or not.
     * @param annName if we do return Annotations, what annotation to return
     * @return the format VO containing the canonical and annotation if requested
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    Format getCanonicalAnf(final long processId, final String version, final boolean withAnn, final String annName)
            throws ExportFormatException;
}
