/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2015 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import java.io.InputStream;
import java.util.List;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.service.model.ProcessData;

/**
 * Interface for the Process Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface ProcessService {
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
            InputStream nativeStream, String domain, String documentation, String created, String lastUpdate, boolean publicModel)
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
     * @return the XML but as a dataSource object
     * @throws ExportFormatException if for some reason the process model can not be found.
     */
    ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Version version,
            final String nativeType)
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
     * Create a process Model version in the database.
     * @param processId of this update.
     * @param processName of this update.
     * @param branchName of this update.
     * @param versionNumber of this update.
     * @param originalVersionNumber of this update.
     * @param user User who updated the process model.
     * @param lockStatus is this model now going to be locked?
     * @param nativeType the native format.
     * @param nativeStream native content.
     */
    ProcessModelVersion createProcessModelVersion(Integer processId, final String branchName, Version versionNumber, 
            Version originalVersionNumber, User user, String lockStatus, NativeType nativeType, InputStream nativeStream)
            throws ImportException, RepositoryException;

    /**
     * Update a process Model version in the database.
     * @param processId of this update.
     * @param branchName of this update.
     * @param version Version of this update.
     * @param user User who updated the process model.
     * @param lockStatus is this model now going to be locked?
     * @param nativeType the native format.
     * @param nativeStream native content.
     */
    ProcessModelVersion updateProcessModelVersion(final Integer processId, final String branchName, final Version version, 
            final User user, final String lockStatus,
            final NativeType nativeType, final InputStream nativeStream) throws ImportException, RepositoryException;
    
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

    /**
     * Get folder that the associated Process is saved in
     * @param pmv processSummaryType
     * @return Folder
     */
    Folder getFolderByPmv(ProcessModelVersion pmv);
}
