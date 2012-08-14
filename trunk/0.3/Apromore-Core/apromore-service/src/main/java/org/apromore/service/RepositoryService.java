package org.apromore.service;

import java.util.List;

import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ImportException;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.NonEditableVersionException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.model.NameValuePair;

/**
 * Repository Service, This service is used to access the repository.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface RepositoryService {

    /**
     * Used to import a new model into the Database.
     * @param processName the name of the process.
     * @param versionName the version name of the process
     * @param username the user who imported the model
     * @param cpfURI the uri of the CPF model
     * @param nativeType the native format of the model
     * @param domain the domain for this model
     * @param documentation any documentation for this model
     * @param created the date it was created
     * @param lastUpdated the date it was updated
     * @param pg the actual model in a process graph format
     * @return the new Id of the model
     * @throws ImportException if the import failed ???
     */
    ProcessModelVersion addProcessModel(String processName, String versionName, String username, String cpfURI, String nativeType,
                                        String domain, String documentation, String created, String lastUpdated, CPF pg) //ProcessModelGraph processModelGraph)
            throws ImportException;

    /**
     * Update a process Model in the database.
     * @param g the process model graph
     */
    void updateProcessModel(CPF g);

    /**
     * Update a process Model in the database.
     * @param versionId The version Id of the model
     * @param branchId the branch id of the model
     * @param g the process model graph
     */
    void updateProcessModel(String versionId, String branchId, CPF g);


    /**
     * Using the Process Model Verison passed in we can get the CPF format.
     * Used by a lot of methods in repoService and external.
     * @param pmv the process model version we want the CPF for.
     * @return the built CPF
     */
    CPF getCanonicalFormat(ProcessModelVersion pmv);

    /**
     * Using the Process Model Verison passed in we can get the CPF format.
     * Used by a lot of methods in repoService and external.
     * @param pmv the process model version we want the CPF for.
     * @param processName
     * @param branchName
     * @param lock
     * @return the built CPF
     */
    CPF getCanonicalFormat(ProcessModelVersion pmv, String processName, String branchName, boolean lock);

    /**
     * Gets the Current Process Model. assuming the branchName is the Trunk.
     * @param processName the process name.
     * @param lock do we lock the records or not.
     * @return the found process model graph.
     * @throws LockFailedException if the lock failed.
     */
    CPF getCurrentProcessModel(String processName, boolean lock) throws LockFailedException;

    /**
     * Gets the Current Process Model. this on can have any branch name.
     * @param processName the process name.
     * @param branchName the branch name.
     * @param lock do we lock the records or not.
     * @return the found process model graph.
     * @throws LockFailedException if the lock failed.
     */
    CPF getCurrentProcessModel(String processName, String branchName, boolean lock) throws LockFailedException;

    /**
     * Gets the process model and returns the process model graph for that model.
     * @param processName the process name.
     * @param branchName the branch name.
     * @param versionName the version name.
     * @return the process model graph.
     */
    CPF getProcessModel(String processName, String branchName, String versionName);

    /**
     * Gets a fragment of a process model.
     * @param g the process graph
     * @param nodes the nodes of the fragment
     * @param lock do we lock the tables or not
     * @return the found fragment or null.
     * @throws LockFailedException if the lockking fails
     * @throws NonEditableVersionException if we are unable to edit the fragment version.
     */
    CPF getFragment(CPF g, List<String> nodes, boolean lock) throws LockFailedException, NonEditableVersionException;

    /**
     * Get a Fragment.
     * @param fragmentId the id of the fragment to get.
     * @param lock do we lock or not.
     * @return the process Model Graph
     * @throws LockFailedException if the lock failed.
     */
    CPF getFragment(String fragmentId, boolean lock) throws LockFailedException;

    /**
     * Updates the fragment if it doesn't conflict with concurrent modifications
     * to the same fragment. Change will be propagated to all process models
     * with instant change propagation policy.
     * @param fg the process Model Graph to Update
     * @return the updated Fragment Id
     */
    String updateFragment(CPF fg);

    /**
     * Removes a process from the repository.
     * @param process the process to remove
     */
    void deleteProcess(Process process);

    /**
     * Deletes the current process model version of the given branch.
     * @param models A map of models that are to be removed.
     */
    void deleteProcessModel(List<NameValuePair> models);
}

