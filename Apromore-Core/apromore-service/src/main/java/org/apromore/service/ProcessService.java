package org.apromore.service;

import java.io.InputStream;
import java.util.Set;

import javax.activation.DataSource;

import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.model.CanonisedProcess;

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
     * Import a Process.
     *
     * @param username      The user doing the importing.
     * @param processName   the name of the process being imported.
     * @param cpfURI        the Canonical URI
     * @param versionName   the version of the Process
     * @param nativeType    the native process format type
     * @param cpf           the canonised process
     * @param nativeXml     the original native process XML
     * @param domain        the domain of the model
     * @param documentation any documentation that is required
     * @param created       the time created
     * @param lastUpdate    the time last updated
     * @return the processSummaryType
     * @throws ImportException if the import process failed for any reason.
     *
     */
    ProcessSummaryType importProcess(String username, String processName, String cpfURI, String versionName, String nativeType,
            CanonisedProcess cpf, InputStream nativeXml, String domain, String documentation, String created, String lastUpdate) throws ImportException;

    /**
     * Export a BMP Model but in a particular format.
     *
     * @param name       the process model name
     * @param processId  the processId
     * @param version    the version of the process model
     * @param nativeType the format of the model
     * @param annName    the annotation format
     * @param withAnn    do we export annotations as well.
     * @param canoniserProperties 
     * @return the XML but as a dataSource object
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    ExportFormatResultType exportProcess(final String name, final Integer processId, final String version, final String nativeType,
            final String annName, boolean withAnn, Set<RequestParameterType<?>> canoniserProperties) throws ExportFormatException;

    /**
     * Updates a processes meta data, this is the Name, Version, domain, rating and then updated the Native xml with these details.
     * @param processId the process id.
     * @param processName the process name.
     * @param domain the domain of the process.
     * @param username the user who is updating the data.
     * @param preVersion the before version.
     * @param newVersion the old version.
     * @param ranking the ranking of this model.
     */
    void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username, final String preVersion,
            final String newVersion, final String ranking) throws UpdateProcessException;

    /**
     * Add a new ProcessModelVersion record into the DB.
     * @param branch
     * @param rootFragmentVersionUri
     * @param versionNumber
     * @param versionName
     * @param numVertices
     * @param numEdges
     * @return
     * @throws ExceptionDao
     */
    ProcessModelVersion addProcessModelVersion(ProcessBranch branch, String rootFragmentVersionUri, int versionNumber, String versionName,
            int numVertices, int numEdges) throws ExceptionDao;


}
