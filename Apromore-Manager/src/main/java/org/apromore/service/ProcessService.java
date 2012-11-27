package org.apromore.service;

import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.NameValuePair;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

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
     * @param canoniserProperties the properties
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
     * @param branch the process branch
     * @param rootFragmentVersionUri the root fragment uri
     * @param versionNumber the version number
     * @param versionName the version name
     * @param numVertices the number of nodes
     * @param numEdges the number of edges
     * @return the found Process Model Version
     * @throws ExceptionDao if the DAO found an issue.
     */
    ProcessModelVersion addProcessModelVersion(ProcessBranch branch, String rootFragmentVersionUri, Double versionNumber, String versionName,
                                               int numVertices, int numEdges) throws ExceptionDao;

    /**
     * Used to import a new model into the Database.
     * @param processName the name of the process.
     * @param versionName the version name of the process
     * @param username the user who imported the model
     * @param CanonicalURI the uri of the Canonical model
     * @param nativeType the native format of the model
     * @param domain the domain for this model
     * @param documentation any documentation for this model
     * @param created the date it was created
     * @param lastUpdated the date it was updated
     * @param pg the actual model in a process graph format
     * @return the new Id of the model
     * @throws ImportException if the import failed ???
     */
    ProcessModelVersion addProcessModel(String processName, String versionName, String username, String CanonicalURI, String nativeType,
                                        String domain, String documentation, String created, String lastUpdated, Canonical pg)
            throws ImportException;

//    /**
//     * Update a process Model in the database.
//     * @param g the process model graph
//     */
//    void updateProcessModel(Canonical g);
//
//    /**
//     * Update a process Model in the database.
//     * @param versionId The version Id of the model
//     * @param branchId the branch id of the model
//     * @param g the process model graph
//     */
//    void updateProcessModel(String versionId, String branchId, Canonical g);


    /**
     * Using the Process Model Verison passed in we can get the Canonical format.
     * Used by a lot of methods in repoService and external.
     * @param pmv the process model version we want the Canonical for.
     * @return the built Canonical
     */
    Canonical getCanonicalFormat(ProcessModelVersion pmv);

    /**
     * Using the Process Model Version passed in we can get the Canonical format.
     * Used by a lot of methods in repoService and external.
     * @param pmv the process model version we want the Canonical for.
     * @param processName
     * @param branchName
     * @param lock
     * @return the built Canonical
     */
    Canonical getCanonicalFormat(ProcessModelVersion pmv, String processName, String branchName, boolean lock);

    /**
     * Gets the Current Process Model. assuming the branchName is the Trunk.
     * @param processName the process name.
     * @param lock do we lock the records or not.
     * @return the found process model graph.
     * @throws org.apromore.exception.LockFailedException if the lock failed.
     */
    Canonical getCurrentProcessModel(String processName, boolean lock) throws LockFailedException;

    /**
     * Gets the Current Process Model. this on can have any branch name.
     * @param processName the process name.
     * @param branchName the branch name.
     * @param lock do we lock the records or not.
     * @return the found process model graph.
     * @throws LockFailedException if the lock failed.
     */
    Canonical getCurrentProcessModel(String processName, String branchName, boolean lock) throws LockFailedException;

    /**
     * Gets the process model and returns the process model graph for that model.
     * @param processName the process name.
     * @param branchName the branch name.
     * @param versionName the version name.
     * @return the process model graph.
     */
    Canonical getProcessModel(String processName, String branchName, String versionName);


    /**
     * Creates new versions for all ascendant fragments of originalFragment by
     * replacing originalFragment with updatedFragment. New versions will be
     * created for all process models which use any of the updated fragments as
     * its root fragment. This method also releases locks of all ascendant
     * fragments.
     * @param originalFragmentId the original Fragment Id
     * @param updatedFragmentId  the updated fragment Id
     */
    void propagateChangesWithLockRelease(String originalFragmentId, String updatedFragmentId, List<String> composingFragmentIds)
            throws ExceptionDao;



    /**
     * Removes a process from the repository.
     * @param process the process to remove
     */
    void deleteProcess(org.apromore.dao.model.Process process);

    /**
     * Deletes the current process model version of the given branch.
     * @param models A map of models that are to be removed.
     */
    void deleteProcessModel(List<NameValuePair> models);

}
