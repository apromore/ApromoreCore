//package org.apromore.service.helper;
//
//import javax.inject.Inject;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apromore.common.Constants;
//import org.apromore.dao.AnnotationRepository;
//import org.apromore.dao.NativeRepository;
//import org.apromore.dao.NativeTypeRepository;
//import org.apromore.dao.ProcessBranchRepository;
//import org.apromore.dao.ProcessModelVersionRepository;
//import org.apromore.dao.ProcessRepository;
//import org.apromore.dao.UserRepository;
//import org.apromore.dao.dataObject.AnnotationDO;
//import org.apromore.dao.dataObject.NativeDO;
//import org.apromore.dao.dataObject.ProcessBranchDO;
//import org.apromore.dao.dataObject.ProcessDO;
//import org.apromore.dao.dataObject.ProcessUserDO;
//import org.apromore.model.AnnotationsType;
//import org.apromore.model.ProcessSummariesType;
//import org.apromore.model.ProcessSummaryType;
//import org.apromore.model.ProcessVersionType;
//import org.apromore.model.ProcessVersionsType;
//import org.apromore.model.VersionSummaryType;
//import org.apromore.service.WorkspaceService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
///**
// * Used By the Services to generate the data objects used by the UI.
// *
// * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
// */
//@Service
//@Transactional
//public class UIHelper implements UserInterfaceHelper {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(UIHelper.class);
//
//    private AnnotationRepository aRepository;
//    private ProcessRepository pRepository;
//    private ProcessBranchRepository pbRepository;
//    private ProcessModelVersionRepository pmvRepository;
//    private NativeRepository nRepository;
//    private NativeTypeRepository ntRepository;
//    private UserRepository uRepository;
//
//    private WorkspaceService workspaceService;
//
//
//    /**
//     * Default Constructor allowing Spring to Autowire for testing and normal use.
//     * @param annotationRepository Annotations Repository.
//     * @param processRepository process Repository.
//     * @param processModelVersionRepository process model version repository.
//     * @param nativeRepository Native Repository.
//     * @param workspaceService Workspace Services.
//     */
//    @Inject
//    public UIHelper(final AnnotationRepository annotationRepository, final ProcessRepository processRepository, final ProcessBranchRepository processBranchRepository,
//            final ProcessModelVersionRepository processModelVersionRepository, final NativeRepository nativeRepository,
//            final NativeTypeRepository nativeTypeRepository, final UserRepository userRepository, final WorkspaceService workspaceService) {
//        this.aRepository = annotationRepository;
//        this.pRepository = processRepository;
//        this.pbRepository = processBranchRepository;
//        this.pmvRepository = processModelVersionRepository;
//        this.nRepository = nativeRepository;
//        this.ntRepository = nativeTypeRepository;
//        this.uRepository = userRepository;
//        this.workspaceService = workspaceService;
//    }
//
//
//
//    /**
//     * @see UserInterfaceHelper#createProcessSummary(String, Integer, String, Double, String, String, String, String, String)
//     */
//    public ProcessSummaryType createProcessSummary(String name, Integer processId, String branchName, Double versionNumber, String nativeType,
//            String domain, String created, String lastUpdate, String username) {
//        ProcessSummaryType proType = new ProcessSummaryType();
//        VersionSummaryType verType = new VersionSummaryType();
//        AnnotationsType annType = new AnnotationsType();
//
//        proType.setId(processId);
//        proType.setName(name);
//        proType.setDomain(domain);
//        proType.setRanking("");
//        proType.setLastVersion(versionNumber.toString());
//        proType.setOriginalNativeType(nativeType);
//        proType.setOwner(username);
//
//        verType.setName(branchName);
//        verType.setCreationDate(created);
//        verType.setLastUpdate(lastUpdate);
//        verType.setVersionNumber(versionNumber);
//        verType.setRanking("");
//
//        annType.setNativeType(nativeType);
//        annType.getAnnotationName().add(Constants.INITIAL_ANNOTATION);
//
//        verType.getAnnotations().add(annType);
//        proType.getVersionSummaries().clear();
//        proType.getVersionSummaries().add(verType);
//
//        return proType;
//    }
//
//
//    /**
//     * @see UserInterfaceHelper#buildProcessSummaryList(String, org.apromore.model.ProcessVersionsType)
//     * {@inheritDoc}
//     */
//    public ProcessSummariesType buildProcessSummaryList(String conditions, ProcessVersionsType similarProcesses) {
//        ProcessSummaryType processSummaryType;
//        ProcessSummariesType processSummaries = new ProcessSummariesType();
//
//        List<Integer> proIds = buildProcessIdList(similarProcesses);
//        List<ProcessDO> processes = pRepository.findAllProcessesJDBC(conditions);
//
//        for (ProcessDO process : processes) {
//            processSummaryType = buildProcessList(proIds, similarProcesses, process);
//            if (processSummaryType != null) {
//                processSummaries.getProcessSummary().add(processSummaryType);
//            }
//        }
//
//        return processSummaries;
//    }
//
//
//    /**
//     * @see UserInterfaceHelper#buildProcessSummaryList(String, Integer, org.apromore.model.ProcessVersionsType)
//     * {@inheritDoc}
//     */
//    public ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, ProcessVersionsType similarProcesses) {
//        ProcessSummaryType processSummaryType;
//        ProcessSummariesType processSummaries = new ProcessSummariesType();
//
//        List<Integer> proIds = buildProcessIdList(similarProcesses);
//        List<ProcessUserDO> processes = workspaceService.getUserProcesses(userId, folderId);
//
//        for (ProcessUserDO processUser : processes) {
//            processSummaryType = buildProcessList(proIds, similarProcesses, pRepository.findProcessById(processUser.getProcessId()));
//            if (processSummaryType != null) {
//                processSummaryType.setHasRead(processUser.isHasRead());
//                processSummaryType.setHasWrite(processUser.isHasWrite());
//                processSummaryType.setHasOwnership(processUser.isHasOwnership());
//                processSummaries.getProcessSummary().add(processSummaryType);
//            }
//        }
//
//        return processSummaries;
//    }
//
//
//    /* Builds the list from a process record. */
//    private ProcessSummaryType buildProcessList(List<Integer> proIds, ProcessVersionsType similarProcesses, ProcessDO process) {
//        ProcessSummaryType processSummary = null;
//        if (proIds != null && (proIds.isEmpty() || !proIds.contains(process.getId()))) {
//            return processSummary;
//        }
//        processSummary = new ProcessSummaryType();
//        processSummary.setId(process.getId());
//        processSummary.setName(process.getName());
//        processSummary.setDomain(process.getDomain());
//        processSummary.setRanking("");
//
//        if (process.getNativeTypeId() != null) {
//            processSummary.setOriginalNativeType(ntRepository.findNativeTypeById(process.getNativeTypeId()).getNatType());
//        }
//        if (process.getUserId() != null) {
//            processSummary.setOwner(uRepository.findUserById(process.getUserId()).getUsername());
//        }
//        buildVersionSummaryTypeList(processSummary, similarProcesses, process);
//
//        return processSummary;
//    }
//
//
//    /* Builds the list of version Summaries for a process. */
//    private void buildVersionSummaryTypeList(ProcessSummaryType processSummary, ProcessVersionsType similarProcesses, ProcessDO pro) {
//        Double maxVersion;
//        VersionSummaryType versionSummary;
//        ProcessVersionType processVersionType = null;
//
//        if (similarProcesses != null) {
//            for (ProcessVersionType pvt : similarProcesses.getProcessVersion()) {
//                if (pvt.getProcessId().equals(pro.getId())) {
//                    processVersionType = pvt;
//                }
//            }
//        }
//
//        List<ProcessBranchDO> branches = pbRepository.findBranchByProcessId(pro.getId());
//        for (ProcessBranchDO branch : branches) {
//            maxVersion = pmvRepository.findMaxVersionByProcessIdAndBranchName(pro.getId(), branch.getName());
//
//            versionSummary = new VersionSummaryType();
//            versionSummary.setName(branch.getName());
//            versionSummary.setCreationDate(branch.getCreationDate());
//            versionSummary.setLastUpdate(branch.getLastUpdateDate());
//            versionSummary.setRanking(branch.getRanking());
//            versionSummary.setVersionNumber(maxVersion);
//            if (branch.getCurrentProcessModelVersionId() != null) {
//                processSummary.setLastVersion(pmvRepository.findVersionNumberById(branch.getCurrentProcessModelVersionId()).toString());
//            }
//            if (processVersionType != null) {
//                versionSummary.setScore(processVersionType.getScore());
//            }
//            buildNativeSummaryList(processSummary, versionSummary, maxVersion);
//
//            processSummary.getVersionSummaries().add(versionSummary);
//        }
//    }
//
//    /* Builds the list of Native Summaries for a version summary. */
//    private void buildNativeSummaryList(ProcessSummaryType processSummary, VersionSummaryType versionSummary, Double maxVersion) {
//        AnnotationsType annotation;
//        List<NativeDO> natives = nRepository.findNativeByProcessAndVersionNumber(processSummary.getId(), maxVersion);
//
//        for (NativeDO nat : natives) {
//            annotation = new AnnotationsType();
//
//            if (nat.getNatTypeId() != null) {
//                annotation.setNativeType(ntRepository.findNativeTypeById(nat.getNatTypeId()).getNatType());
//            }
//            buildAnnotationNames(nat, annotation);
//
//            versionSummary.getAnnotations().add(annotation);
//        }
//    }
//
//    /* Populate the Annotation names. */
//    private void buildAnnotationNames(NativeDO nat, AnnotationsType annotation) {
//        List<AnnotationDO> anns = aRepository.findByNativeId(nat.getId());
//        for (AnnotationDO ann : anns) {
//            annotation.getAnnotationName().add(ann.getName());
//        }
//    }
//
//    /* From a list of ProcessVersionTypes build a list of the id's of each */
//    private List<Integer> buildProcessIdList(ProcessVersionsType similarProcesses) {
//        if (similarProcesses == null) {
//            return null;
//        }
//
//        List<Integer> proIds = new ArrayList<Integer>(0);
//        for (ProcessVersionType pvt : similarProcesses.getProcessVersion()) {
//            proIds.add(pvt.getProcessId());
//        }
//        return proIds;
//    }
//
//}
package org.apromore.service.helper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.ProcessUser;
import org.apromore.model.AnnotationsType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* Used By the Services to generate the data objects used by the UI.
*
* @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
*/
@Service
@Transactional
public class UIHelper implements UserInterfaceHelper {

    private AnnotationRepository aRepository;
    private ProcessRepository pRepository;

    private WorkspaceService workspaceService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotations Repository.
     * @param processRepository process Repository.
     * @param workspaceService Workspace Services.
     */
    @Inject
    public UIHelper(final AnnotationRepository annotationRepository, final ProcessRepository processRepository,
                    final WorkspaceService workspaceService) {
        this.aRepository = annotationRepository;
        this.pRepository = processRepository;
        this.workspaceService = workspaceService;
    }



    /**
     * @see UserInterfaceHelper#createProcessSummary(org.apromore.dao.model.Process, org.apromore.dao.model.ProcessBranch, org.apromore.dao.model.ProcessModelVersion, String, String, String, String, String)
     */
    public ProcessSummaryType createProcessSummary(Process process, ProcessBranch branch, ProcessModelVersion pmv,  String nativeType,
            String domain, String created, String lastUpdate, String username) {
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

        verType.setName(branch.getBranchName());
        verType.setCreationDate(created);
        verType.setLastUpdate(lastUpdate);
        verType.setVersionNumber(pmv.getVersionNumber());
        verType.setRanking("");

        annType.setNativeType(nativeType);
        annType.getAnnotationName().add(Constants.INITIAL_ANNOTATION);

        verType.getAnnotations().add(annType);
        proType.getVersionSummaries().clear();
        proType.getVersionSummaries().add(verType);

        return proType;
    }


    /**
     * @see UserInterfaceHelper#buildProcessSummaryList(String, org.apromore.model.ProcessVersionsType)
     * {@inheritDoc}
     */
    public ProcessSummariesType buildProcessSummaryList(String conditions, ProcessVersionsType similarProcesses) {
        ProcessSummaryType processSummaryType;
        ProcessSummariesType processSummaries = new ProcessSummariesType();

        processSummaries.setTotalProcessCount(pRepository.count());

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<Process> processes = pRepository.findAllProcesses(conditions);

        for (Process process : processes) {
            processSummaryType = buildProcessList(proIds, similarProcesses, process);
            if (processSummaryType != null) {
                processSummaries.getProcessSummary().add(processSummaryType);
            }
        }

        return processSummaries;
    }


    /**
     * @see UserInterfaceHelper#buildProcessSummaryList(String, Integer, org.apromore.model.ProcessVersionsType)
     * {@inheritDoc}
     */
    public ProcessSummariesType buildProcessSummaryList(String userId, Integer folderId, ProcessVersionsType similarProcesses) {
        ProcessSummaryType processSummaryType;
        ProcessSummariesType processSummaries = new ProcessSummariesType();

        processSummaries.setTotalProcessCount(pRepository.count());

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<ProcessUser> processes = workspaceService.getUserProcessesOrig(userId, folderId);

        for (ProcessUser processUser : processes) {
            processSummaryType = buildProcessList(proIds, similarProcesses, processUser.getProcess());
            if (processSummaryType != null) {
                processSummaryType.setHasRead(processUser.isHasRead());
                processSummaryType.setHasWrite(processUser.isHasWrite());
                processSummaryType.setHasOwnership(processUser.isHasOwnership());
                processSummaries.getProcessSummary().add(processSummaryType);
            }
        }

        return processSummaries;
    }


    /* Builds the list from a process record. */
    private ProcessSummaryType buildProcessList(List<Integer> proIds, ProcessVersionsType similarProcesses, Process process) {
        ProcessSummaryType processSummary = null;
        if (proIds != null && (proIds.isEmpty() || !proIds.contains(process.getId()))) {
            return processSummary;
        }
        processSummary = new ProcessSummaryType();
        processSummary.setId(process.getId());
        processSummary.setName(process.getName());
        processSummary.setDomain(process.getDomain());
        processSummary.setRanking(process.getRanking());
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
                buildNativeSummaryList(processSummary, versionSummary, processModelVersion.getVersionNumber());

                processSummary.getVersionSummaries().add(versionSummary);

                if (processSummary.getLastVersion() == null || processSummary.getLastVersion() < processModelVersion.getVersionNumber()) {
                    processSummary.setLastVersion(processModelVersion.getVersionNumber());
                }
            }
        }
    }

    /* Builds the list of Native Summaries for a version summary. */
    private void buildNativeSummaryList(ProcessSummaryType processSummary, VersionSummaryType versionSummary, Double maxVersion) {
        AnnotationsType annotation;
        List<Annotation> annotations = aRepository.findAnnotationByCanonical(processSummary.getId(), maxVersion);

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
