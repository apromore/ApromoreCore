package org.apromore.service.helper;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

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
    private ProcessModelVersionRepository pmvRepository;
    private NativeRepository nRepository;

    private WorkspaceService workspaceService;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotations Repository.
     * @param processRepository process Repository.
     * @param processModelVersionRepository process model version repository.
     * @param nativeRepository Native Repository.
     * @param workspaceService Workspace Services.
     */
    @Inject
    public UIHelper(final AnnotationRepository annotationRepository, final ProcessRepository processRepository,
            final ProcessModelVersionRepository processModelVersionRepository, final NativeRepository nativeRepository,
            final WorkspaceService workspaceService) {
        this.aRepository = annotationRepository;
        this.pRepository = processRepository;
        this.pmvRepository = processModelVersionRepository;
        this.nRepository = nativeRepository;
        this.workspaceService = workspaceService;
    }



    /**
     * @see UserInterfaceHelper#createProcessSummary(String, Integer, String, Double, String, String, String, String, String)
     */
    public ProcessSummaryType createProcessSummary(String name, Integer processId, String branchName, Double versionNumber, String nativeType,
            String domain, String created, String lastUpdate, String username) {
        ProcessSummaryType proType = new ProcessSummaryType();
        VersionSummaryType verType = new VersionSummaryType();
        AnnotationsType annType = new AnnotationsType();

        proType.setId(processId);
        proType.setName(name);
        proType.setDomain(domain);
        proType.setRanking("");
        proType.setLastVersion(versionNumber.toString());
        proType.setOriginalNativeType(nativeType);
        proType.setOwner(username);

        verType.setName(branchName);
        verType.setCreationDate(created);
        verType.setLastUpdate(lastUpdate);
        verType.setVersionNumber(versionNumber);
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

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<Process> processes = pRepository.findAllProcesses(conditions);

        for (Process process : processes) {
            processSummaryType = buildProcessList(proIds, process);
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

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<ProcessUser> processes = workspaceService.getUserProcesses(userId, folderId);

        for (ProcessUser processUser : processes) {
            processSummaryType = buildProcessList(proIds, processUser.getProcess());
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
    private ProcessSummaryType buildProcessList(List<Integer> proIds, Process process) {
        ProcessSummaryType processSummary = null;
        if (!proIds.isEmpty() && !proIds.contains(process.getId())) {
            return processSummary;
        }
        processSummary = new ProcessSummaryType();
        processSummary.setId(process.getId());
        processSummary.setName(process.getName());
        processSummary.setDomain(process.getDomain());
        processSummary.setRanking("");
        if (process.getNativeType() != null) {
            processSummary.setOriginalNativeType(process.getNativeType().getNatType());
        }
        if (process.getUser() != null) {
            processSummary.setOwner(process.getUser().getUsername());
        }
        buildVersionSummaryTypeList(processSummary, process);

        return processSummary;
    }


    /* Builds the list of version Summaries for a process. */
    private void buildVersionSummaryTypeList(ProcessSummaryType processSummary, Process pro) {
        Double maxVersion;
        VersionSummaryType versionSummary;

        // Find the branches for a RootFragment.
        Set<ProcessBranch> branches = pro.getProcessBranches();
        for (ProcessBranch branch : branches) {
            maxVersion = pmvRepository.findMaxVersionNumberByProcessIdAndBranchName(pro.getId(), branch.getBranchName());

            versionSummary = new VersionSummaryType();
            versionSummary.setName(branch.getBranchName());
            versionSummary.setCreationDate(branch.getCreationDate());
            versionSummary.setLastUpdate(branch.getLastUpdate());
            versionSummary.setRanking(branch.getRanking());
            versionSummary.setVersionNumber(maxVersion);
            buildNativeSummaryList(processSummary, versionSummary, maxVersion);

            processSummary.getVersionSummaries().add(versionSummary);

            processSummary.setLastVersion(branch.getCurrentProcessModelVersion().getVersionNumber().toString());
        }
    }

    /* Builds the list of Native Summaries for a version summary. */
    private void buildNativeSummaryList(ProcessSummaryType processSummary, VersionSummaryType versionSummary, Double maxVersion) {
        AnnotationsType annotation;
        List<Native> natives = nRepository.findNativeByCanonical(processSummary.getId(), maxVersion);

        for (Native nat : natives) {
            annotation = new AnnotationsType();

            if (nat.getNativeType() != null) {
                annotation.setNativeType(nat.getNativeType().getNatType());
            }
            buildAnnotationNames(nat, annotation);

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
        List<Integer> proIds = new ArrayList<Integer>(0);
        if (similarProcesses != null) {
            for (ProcessVersionType pvt : similarProcesses.getProcessVersion()) {
                proIds.add(pvt.getProcessId());
            }
        }
        return proIds;
    }

}
