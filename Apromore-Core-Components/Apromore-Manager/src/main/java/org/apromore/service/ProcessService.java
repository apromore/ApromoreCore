/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2015 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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
import org.apromore.graph.canonical.Canonical;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.SummariesType;
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
     * @param folderId the folder we are currently in.
     * @param searchExpression the search expression to limit the search.
     * @return The ProcessSummariesType used for Webservices.
     */
    SummariesType readProcessSummaries(final Integer folderId, final String userRowGuid, final String searchExpression);

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
     * @param publicModel   is this a public model?
     * @return the processSummaryType
     * @throws ImportException if the import process failed for any reason.
     *
     */
    ProcessModelVersion importProcess(String username, Integer folderId, String processName, Version versionNumber, String nativeType,
            CanonisedProcess cpf, String domain, String documentation, String created, String lastUpdate, boolean publicModel)
            throws ImportException;

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
    ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Version version,
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
     * @param isPublic is this model public.
     */
    void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username,
        final Version preVersion, final Version newVersion, final String ranking, final boolean isPublic) throws UpdateProcessException;

    boolean isPublicProcess(Integer processId);

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
    ProcessModelVersion addProcessModelVersion(ProcessBranch branch, FragmentVersion rootFragmentVersion, Version versionNumber,
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
    ProcessModelVersion updateProcess(Integer processId, String processName, String originalBranchName, String newBranchName,
            Version versionNumber, Version originalVersionNumber, User user, String lockStatus, NativeType nativeType, CanonisedProcess cpf)
            throws ImportException, RepositoryException;


    /**
     * Using the Process Model Verison passed in we can get the Canonical format.
     * Used by a lot of methods in repoService and external.
     * @param pmv the process model version we want the Canonical for.
     * @return the built Canonical
     */
    CanonicalProcessType getCanonicalFormat(ProcessModelVersion pmv);

    /**
     * Using the ProcessID, its branchName and its versionNumber passed in we can get the Canonical Object.
     * Used by a lot of methods in repoService and external.
     * @param processId is the ID of the process to retrieve from the database
     * @param branchName the branch name
     * @param versionNumber the versione of the process
     * @return the built Canonical
     */
    Canonical getCanonicalFormat(Integer processId, String branchName, String versionNumber);

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
    CanonicalProcessType getProcessModelVersion(Integer processId, String processName, String branchName, final Version version,
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
        Set<FragmentVersion> composingFragments, Version newVersionNumber) throws RepositoryException;


    /**
     * Deletes the current process model version of the given branch.
     * @param models A map of models that are to be removed.
     * @param user with write permission
     * @throws UpdateProcessException if the user doesn't have write permission on any of the models
     */
    void deleteProcessModel(List<ProcessData> models, User user) throws UpdateProcessException;;


    /**
     * Gives back a BMP Model represented in BPMN 2.0
     *
     * @param name       the process model name
     * @param processId  the processId
     * @param branch     the branch name
     * @param version    the version of the process model.
     * @return the XML as a String
     * @throws RepositoryException if for some reason the process model can not be found.
     */
    String getBPMNRepresentation(final String name, final Integer processId, final String branch, final Version version) throws RepositoryException;

}
