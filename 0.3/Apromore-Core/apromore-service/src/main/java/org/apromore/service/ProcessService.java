package org.apromore.service;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;

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
     *
     * @param searchExpression the search expression to limit the search.
     * @return The ProcessSummariesType used for Webservices.
     */
    ProcessSummariesType readProcessSummaries(final String searchExpression);

    /**
     * Export a BMP Model but in a particular format.
     *
     * @param name       the process model name
     * @param processId  the processId
     * @param version    the version of the process model
     * @param nativeType the format of the model
     * @param annName    the annotation format
     * @param withAnn    do we export annotations as well.
     * @return the XML but as a dataSource object
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    DataSource exportFormat(final String name, final Integer processId, final String version, final String nativeType,
                            final String annName, boolean withAnn) throws ExportFormatException;


    /**
     * Import a Process.
     *
     * @param username      The user doing the importing.
     * @param processName   the name of the process being imported.
     * @param cpfURI        the Canonical URI
     * @param versionName   the version of the Process
     * @param nativeType    the native process format type
     * @param cpf           the dataHandler of the Process to import (the actual process model)
     * @param domain        the domain of the model
     * @param documentation any documentation that is required
     * @param created       the time created
     * @param lastUpdate    the time last updated
     * @return the processSummaryType
     * @throws ImportException if the import process failed for any reason.
     *                         <p/>
     *                         Deprecated - Use the insertProcess Instead.
     */
    ProcessSummaryType importProcess(String username, String processName, String cpfURI, String versionName, String nativeType,
                                     DataHandler cpf, String domain, String documentation, String created, String lastUpdate) throws ImportException;


    /**
     * Add a new ProcessModelVersion record into the DB.
     *
     * @param branch
     * @param rootFragmentVersionId
     * @param versionNumber
     * @param versionName
     * @param numVertices
     * @param numEdges
     * @return
     * @throws ExceptionDao
     */
    ProcessModelVersion addProcessModelVersion(ProcessBranch branch, String rootFragmentVersionId, int versionNumber, String versionName,
                                               int numVertices, int numEdges) throws ExceptionDao;


}
