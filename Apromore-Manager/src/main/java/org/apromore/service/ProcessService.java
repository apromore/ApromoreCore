package org.apromore.service;

import java.util.List;
import java.util.Set;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.ProcessData;

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
     * @param folderId      The folder we are saving the process in.
     * @param processName   the name of the process being imported.
     * @param versionNumber the process version number.
     * @param nativeType    the native process format type
     * @param cpf           the canonised process
     * @param domain        the domain of the model
     * @param documentation any documentation that is required
     * @param created       the time created
     * @param lastUpdate    the time last updated
     * @return the processSummaryType
     * @throws ImportException if the import process failed for any reason.
     *
     */
    ProcessModelVersion importProcess(String username, Integer folderId, String processName, Double versionNumber, String nativeType,
            CanonisedProcess cpf, String domain, String documentation, String created, String lastUpdate) throws ImportException;

    /**
     * Export a BMP Model but in a particular format.
     *
     * @param name       the process model name
     * @param processId  the processId
     * @param branch     the branch name
     * @param version    the version of the process model.
     * @param nativeType the format of the model
     * @param annName    the annotation format
     * @param withAnn    do we export annotations as well.
     * @param canoniserProperties the properties
     * @return the XML but as a dataSource object
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Double version,
            final String nativeType, final String annName, boolean withAnn, Set<RequestParameterType<?>> canoniserProperties)
            throws ExportFormatException;

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
    void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username,
        final Double preVersion, final Double newVersion, final String ranking) throws UpdateProcessException;

    /**
     * Add a new ProcessModelVersion record into the DB.
     * @param branch the process branch
     * @param rootFragmentVersion the root fragment uri
     * @param versionNumber the version number
     * @param numVertices the number of nodes
     * @param numEdges the number of edges
     * @return the found Process Model Version
     * @throws ExceptionDao if the DAO found an issue.
     */
    ProcessModelVersion addProcessModelVersion(ProcessBranch branch, FragmentVersion rootFragmentVersion, Double versionNumber,
            int numVertices, int numEdges) throws ExceptionDao;

    /**
     * Update a process Model in the database.
     * @param processId of this update.
     * @param processName of this update.
     * @param originalBranchName of this update.
     * @param newBranchName of this update.
     * @param versionNumber of this update.
     * @param originalVersionNumber of this update.
     * @param user User who updated the process model.
     * @param lockStatus is this model now going to be locked?
     * @param nativeType the native format.
     * @param cpf the process model graph.
     */
    ProcessModelVersion updateProcess(Integer processId, String processName, String originalBranchName, String newBranchName, Double versionNumber,
            Double originalVersionNumber, User user, String lockStatus, NativeType nativeType, CanonisedProcess cpf)
            throws ImportException, RepositoryException;


    /**
     * Using the Process Model Verison passed in we can get the Canonical format.
     * Used by a lot of methods in repoService and external.
     * @param pmv the process model version we want the Canonical for.
     * @return the built Canonical
     */
    CanonicalProcessType getCanonicalFormat(ProcessModelVersion pmv);

    /**
     * Using the Process Model Version passed in we can get the Canonical format.
     * Used by a lot of methods in repoService and external.
     * @param pmvs the process model version we want the Canonical for.
     * @param processName the process name
     * @param branchName the branch name
     * @param lock is it locked?
     * @return the built Canonical
     */
    CanonicalProcessType getCanonicalFormat(ProcessModelVersion pmvs, String processName, String branchName, boolean lock);


    /**
     * Gets the Current Process Model. this on can have any branch name.
     * @param processName the process name.
     * @param branchName the branch name.
     * @param lock do we lock the records or not.
     * @return the found process model graph.
     * @throws LockFailedException if the lock failed.
     */
    CanonicalProcessType getCurrentProcessModel(String processName, String branchName, boolean lock) throws LockFailedException;

    /**
     * Gets the Current Process Model. this on can have any branch name.
     * @param processId the process id
     * @param processName the process name.
     * @param branchName the branch name.
     * @param lock do we lock the records or not.
     * @return the found process model graph.
     * @throws LockFailedException if the lock failed.
     */
    CanonicalProcessType getProcessModelVersion(Integer processId, String processName, String branchName, final Double version,
        boolean lock) throws LockFailedException;


    /**
     * Creates new versions for all ascendant fragments of originalFragment by
     * replacing originalFragment with updatedFragment. New versions will be
     * created for all process models which use any of the updated fragments as
     * its root fragment. This method also releases locks of all ascendant
     * fragments.
     * @param originalFragment the original fragment id
     * @param updatedFragment the updated fragment id
     * @param composingFragments the composing fragment
     * @param newVersionNumber the new version number of the process model version.
     */
    void propagateChangesWithLockRelease(FragmentVersion originalFragment, FragmentVersion updatedFragment,
        Set<FragmentVersion> composingFragments, Double newVersionNumber) throws RepositoryException;


    /**
     * Deletes the current process model version of the given branch.
     * @param models A map of models that are to be removed.
     */
    void deleteProcessModel(List<ProcessData> models);

}
