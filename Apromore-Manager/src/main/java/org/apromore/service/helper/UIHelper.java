/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.helper.Version;
import org.apromore.model.AnnotationsType;
import org.apromore.model.IndexStatus;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.PQLService;
import org.apromore.service.WorkspaceService;

/**
* Used By the Services to generate the data objects used by the UI.
*
* @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
*/
@Service
@Transactional
public class UIHelper implements UserInterfaceHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserInterfaceHelper.class);

    private AnnotationRepository aRepository;
    private ProcessRepository pRepository;
    private GroupProcessRepository gpRepository;
    private ProcessModelVersionRepository pmvRepository;
    private FolderRepository fRepository;
    private WorkspaceService workspaceService;
    private PQLService pqlService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotations Repository.
     * @param processRepository process Repository.
     * @param groupProcessRepository process access control group Repository
     * @param processModelVersionRepository process model version Repository.
     * @param folderRepository folder repository.
     * @param workspaceService Workspace Services.
     * @param pqlService Process Query Lanuage services
     */
    @Inject
    public UIHelper(final AnnotationRepository annotationRepository,
                    final ProcessRepository processRepository,
                    final GroupProcessRepository groupProcessRepository,
                    final ProcessModelVersionRepository processModelVersionRepository,
                    final FolderRepository folderRepository,
                    final WorkspaceService workspaceService) {

        this.aRepository = annotationRepository;
        this.pRepository = processRepository;
        this.gpRepository = groupProcessRepository;
        this.pmvRepository = processModelVersionRepository;
        this.fRepository = folderRepository;
        this.workspaceService = workspaceService;
    }

    // KLUDGE to work around circular dependency
    public void setPQLService(PQLService pqlService) {
        this.pqlService = pqlService;
    }



    /**
     * @see UserInterfaceHelper#createProcessSummary(org.apromore.dao.model.Process, org.apromore.dao.model.ProcessBranch, org.apromore.dao.model.ProcessModelVersion, String, String, String, String, String, boolean)
     */
    public ProcessSummaryType createProcessSummary(Process process, ProcessBranch branch, ProcessModelVersion pmv, String nativeType,
            String domain, String created, String lastUpdate, String username, boolean isPublic) {
        ProcessSummaryType proType = new ProcessSummaryType();
        VersionSummaryType verType = new VersionSummaryType();
        AnnotationsType annType = new AnnotationsType();

        proType.setId(process.getId());
        proType.setName(process.getName());
        proType.setDomain(domain);
        proType.setRanking("");
        proType.setLastVersion(pmv.getVersionNumber());
        proType.setOriginalNativeType(nativeType);
        proType.setOwner(username);
        proType.setMakePublic(isPublic);

        verType.setName(branch.getBranchName());
        verType.setCreationDate(created);
        verType.setLastUpdate(lastUpdate);
        verType.setVersionNumber(pmv.getVersionNumber());
        verType.setRanking("");
        if (pmv.getNumEdges() == 0 && pmv.getNumVertices() == 0) {
            verType.setEmpty(Boolean.TRUE);
        } else {
            verType.setEmpty(Boolean.FALSE);
        }

        if (nativeType != null && !nativeType.equals("")) {
            annType.setNativeType(nativeType);
            annType.getAnnotationName().add(Constants.INITIAL_ANNOTATION);
            verType.getAnnotations().add(annType);
        }

        proType.getVersionSummaries().clear();
        proType.getVersionSummaries().add(verType);

        return proType;
    }


    /**
     * Used for the Search on the Main Screen Page
     * @see UserInterfaceHelper#buildProcessSummaryList(Integer, String, org.apromore.model.ProcessVersionsType)
     * {@inheritDoc}
     */
    public ProcessSummariesType buildProcessSummaryList(Integer folderId, String conditions, ProcessVersionsType similarProcesses) {
        ProcessSummaryType processSummaryType;
        ProcessSummariesType processSummaries = new ProcessSummariesType();

        processSummaries.setTotalProcessCount(pRepository.count());

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<Process> processes = pRepository.findAllProcessesByFolder(folderId, conditions);

        for (Process process : processes) {
            processSummaryType = buildProcessList(proIds, similarProcesses, process);
            if (processSummaryType != null) {
                processSummaries.getProcessSummary().add(processSummaryType);
            }
        }

        return processSummaries;
    }

    /**
     * Used by get the list of processes for  user (main page).
     * @see UserInterfaceHelper#buildProcessSummaryList(String, Integer, org.apromore.model.ProcessVersionsType)
     * {@inheritDoc}
     */
    public ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, ProcessVersionsType similarProcesses) {
        ProcessSummariesType processSummaries = new ProcessSummariesType();

        processSummaries.setTotalProcessCount(pRepository.count()); //(long) fRepository.getProcessByFolderUserRecursive(folderId, userId).size()

        List<Integer> proIds = buildProcessIdList(similarProcesses);

        Map<Integer, ProcessSummaryType> map = new HashMap<>();

        for (GroupProcess groupProcess: workspaceService.getGroupProcesses(userId, folderId)) {
            if (proIds != null && (proIds.isEmpty() || !proIds.contains(groupProcess.getProcess().getId()))) {
                 continue;
            }

            ProcessSummaryType processSummaryType = map.get(groupProcess.getProcess().getId());
            if (processSummaryType == null) {
                // New process, so create a new process summary entry
                processSummaryType = buildProcessList(proIds, similarProcesses, groupProcess.getProcess());
                processSummaryType.setHasRead(groupProcess.getHasRead());
                processSummaryType.setHasWrite(groupProcess.getHasWrite());
                processSummaryType.setHasOwnership(groupProcess.getHasOwnership());
                processSummaries.getProcessSummary().add(processSummaryType);
                map.put(groupProcess.getProcess().getId(), processSummaryType);
            } else {
                // Existing process for a different group, so merge permissions in existing process summary entry
                processSummaryType.setHasRead(processSummaryType.isHasRead()           || groupProcess.getHasRead());
                processSummaryType.setHasWrite(processSummaryType.isHasWrite()         || groupProcess.getHasWrite());
                processSummaryType.setHasOwnership(processSummaryType.isHasOwnership() || groupProcess.getHasOwnership());
            }
        }

        return processSummaries;
    }

    /**
     * Get the list of processes for a user.
     *
     * This is used to populate the main page's ProcessListbox area.
     * @see UserInterfaceHelper#buildProcessSummaryList(String, Integer, Integer, Integer)
     * {@inheritDoc}
     */
    public ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, Integer pageIndex, Integer pageSize) {
        assert pageSize != null;
        assert pageIndex != null;

        Page<Process> processes = workspaceService.getProcesses(userId, folderId, new PageRequest(pageIndex, pageSize));

        ProcessSummariesType processSummaries = new ProcessSummariesType();
        processSummaries.setTotalProcessCount(pRepository.count());
        processSummaries.setOffset(new Long(pageIndex * pageSize));
        processSummaries.setProcessCount(new Long(processes.getTotalElements()));
        for (Process process: processes.getContent()) {
            processSummaries.getProcessSummary().add(buildProcessSummary(process));
        }

        return processSummaries;
    }

    /**
     * Populate a process summary.
     *
     * @param process
     * @return the populated process summary
     */
    private ProcessSummaryType buildProcessSummary(final Process process) {
        ProcessSummaryType processSummary = new ProcessSummaryType();
        processSummary.setId(process.getId());
        processSummary.setName(process.getName());
        processSummary.setDomain(process.getDomain());
        processSummary.setRanking(process.getRanking());
        processSummary.setMakePublic(process.getPublicModel());

        ProcessModelVersion latestVersion = pmvRepository.getLatestProcessModelVersion(process.getId(), "MAIN");
        if (latestVersion != null) {
            processSummary.setLastVersion(latestVersion.getVersionNumber());
        }

        if (process.getNativeType() != null) {
            processSummary.setOriginalNativeType(process.getNativeType().getNatType());
        }

        if (process.getUser() != null) {
            processSummary.setOwner(process.getUser().getUsername());
        }

        buildVersionSummaryTypeList(processSummary, null, process);

        List<GroupProcess> groupProcesses = gpRepository.findByProcessId(process.getId());
        boolean hasRead = false, hasWrite = false, hasOwnership = false;
        for (GroupProcess groupProcess: groupProcesses) {
            hasRead = hasRead || groupProcess.getHasRead();
            hasWrite = hasWrite || groupProcess.getHasWrite();
            hasOwnership = hasOwnership || groupProcess.getHasOwnership();
        }
        processSummary.setHasRead(hasRead);
        processSummary.setHasWrite(hasWrite);
        processSummary.setHasOwnership(hasOwnership);

	return processSummary;
    }


    /* Builds the list from a process record. */
    private ProcessSummaryType buildProcessList(List<Integer> proIds, ProcessVersionsType similarProcesses, Process process) {
        ProcessSummaryType processSummary;
        if (proIds != null && (proIds.isEmpty() || !proIds.contains(process.getId()))) {
            return null;
        }
        processSummary = new ProcessSummaryType();
        processSummary.setId(process.getId());
        processSummary.setName(process.getName());
        processSummary.setDomain(process.getDomain());
        processSummary.setRanking(process.getRanking());
        processSummary.setMakePublic(process.getPublicModel());
        if (process.getNativeType() != null) {
            processSummary.setOriginalNativeType(process.getNativeType().getNatType());
        }
        if (process.getUser() != null) {
            processSummary.setOwner(process.getUser().getUsername());
        }
        buildVersionSummaryTypeList(processSummary, similarProcesses, process);

        return processSummary;
    }


    /* Builds the list of version Summaries for a process. */
    private void buildVersionSummaryTypeList(ProcessSummaryType processSummary, ProcessVersionsType similarProcesses, Process pro) {
        VersionSummaryType versionSummary;
        ProcessVersionType processVersionType = null;

        if (similarProcesses != null) {
            for (ProcessVersionType pvt : similarProcesses.getProcessVersion()) {
                if (pvt.getProcessId().equals(pro.getId())) {
                    processVersionType = pvt;
                }
            }
        }

        // Find the branches for a RootFragment.
        Version pmVersion;
        Version lastVersion;
        String externalId = null;
        for (ProcessBranch branch : pro.getProcessBranches()) {
            for (ProcessModelVersion processModelVersion : branch.getProcessModelVersions()) {
                versionSummary = new VersionSummaryType();
                versionSummary.setName(branch.getBranchName());
                versionSummary.setCreationDate(processModelVersion.getCreateDate());
                versionSummary.setLastUpdate(processModelVersion.getLastUpdateDate());
                versionSummary.setVersionNumber(processModelVersion.getVersionNumber());
                if (processVersionType != null) {
                    versionSummary.setScore(processVersionType.getScore());
                }
                if (processModelVersion.getNumEdges() == 0 && processModelVersion.getNumVertices() == 0) {
                    versionSummary.setEmpty(Boolean.TRUE);
                } else {
                    versionSummary.setEmpty(Boolean.FALSE);
                }

                pmVersion = new Version(processModelVersion.getVersionNumber());
                buildNativeSummaryList(processSummary, versionSummary, branch.getBranchName(), pmVersion);

                processSummary.getVersionSummaries().add(versionSummary);

                if (processSummary.getLastVersion() == null) {
                    processSummary.setLastVersion(processModelVersion.getVersionNumber());
                } else {
                    lastVersion = new Version(processSummary.getLastVersion());
                    if (lastVersion.compareTo(pmVersion) < 0) {
                        processSummary.setLastVersion(processModelVersion.getVersionNumber());
                    }
                }

                externalId = pro.getId() + "/" + pmVersion + "/" + branch.getBranchName();
            }
        }
        assert externalId != null;

        if (pqlService.isIndexingEnabled()) {
            // Find the status of the process in the PQL index
            try {
                org.pql.index.IndexStatus pqlStatus = pqlService.getIndexStatus(externalId);
                IndexStatus status = pqlStatus == null ? null : Enum.valueOf(IndexStatus.class, pqlStatus.name());
                LOGGER.debug("Index status of process with id " + pro.getId() + " is " + status);
                processSummary.setPqlIndexerStatus(status);
            } catch (SQLException e) {
                LOGGER.warn("Unable to get index status for process with id " + pro.getId(), e);
                processSummary.setPqlIndexerStatus(null);  // Rendered as an error icon
            }
        }
    }

    /* Builds the list of Native Summaries for a version summary. */
    private void buildNativeSummaryList(ProcessSummaryType processSummary, VersionSummaryType versionSummary, String branchName,
            Version maxVersion) {
        AnnotationsType annotation;
        List<Annotation> annotations = aRepository.findAnnotationByCanonical(processSummary.getId(), branchName, maxVersion.toString());

        for (Annotation ann : annotations) {
            annotation = new AnnotationsType();

            if (ann.getNatve() != null) {
                annotation.setNativeType(ann.getNatve().getNativeType().getNatType());
            }
            buildAnnotationNames(ann.getNatve(), annotation);

            versionSummary.getAnnotations().add(annotation);
        }
    }

    /* Populate the Annotation names. */
    private void buildAnnotationNames(Native nat, AnnotationsType annotation) {
        List<Annotation> anns = aRepository.findByUri(nat.getId());
        for (Annotation ann : anns) {
            annotation.getAnnotationName().add(ann.getName());
        }
    }

    /* From a list of ProcessVersionTypes build a list of the id's of each */
    private List<Integer> buildProcessIdList(ProcessVersionsType similarProcesses) {
        if (similarProcesses == null) {
            return null;
        }

        List<Integer> proIds = new ArrayList<>();
        for (ProcessVersionType pvt : similarProcesses.getProcessVersion()) {
            proIds.add(pvt.getProcessId());
        }
        return proIds;
    }

}
