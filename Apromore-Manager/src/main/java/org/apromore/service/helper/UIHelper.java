package org.apromore.service.helper;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.model.AnnotationsType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.ProcessVersionsType;
import org.apromore.model.VersionSummaryType;
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

    private AnnotationRepository annotationRepository;
    private ProcessRepository processRepository;
    private NativeRepository nativeRepository;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepository Annotations Repository
     * @param processRepository process Repository
     * @param nativeRepository Native Repository
     */
    @Inject
    public UIHelper(final AnnotationRepository annotationRepository, final ProcessRepository processRepository,
            final NativeRepository nativeRepository) {
        this.annotationRepository = annotationRepository;
        this.processRepository = processRepository;
        this.nativeRepository = nativeRepository;
    }



    /**
     * Create a Process Summary record for the Front UI display.
     *
     * @param name       the process Name
     * @param processId  the process Id
     * @param version    the version number of this model
     * @param nativeType the native type of this model
     * @param domain     The domain of this model
     * @param created    the Date create
     * @param lastUpdate the Date Last Updated
     * @param username   the user who updated the
     * @return the created Process Summary
     */
    public ProcessSummaryType createProcessSummary(String name, Integer processId, String version, String versionName, String nativeType,
            String domain, String created, String lastUpdate, String username) {
        ProcessSummaryType proType = new ProcessSummaryType();
        VersionSummaryType verType = new VersionSummaryType();
        AnnotationsType annType = new AnnotationsType();

        proType.setId(processId);
        proType.setName(name);
        proType.setDomain(domain);
        proType.setRanking("");
        proType.setLastVersion(versionName);
        proType.setOriginalNativeType(nativeType);
        proType.setOwner(username);

        verType.setName(version);
        verType.setCreationDate(created);
        verType.setLastUpdate(lastUpdate);
        verType.setRanking("");

        annType.setNativeType(nativeType);
        annType.getAnnotationName().add(Constants.INITIAL_ANNOTATION);

        verType.getAnnotations().add(annType);
        proType.getVersionSummaries().clear();
        proType.getVersionSummaries().add(verType);

        return proType;
    }


    /**
     * Builds the list of process Summaries and kicks off the versions and annotations.
     *
     * @param conditions       the search conditions
     * @param similarProcesses
     * @return the list of process Summaries
     */
    public ProcessSummariesType buildProcessSummaryList(String conditions, ProcessVersionsType similarProcesses) {
        ProcessSummariesType processSummaries = new ProcessSummariesType();
        ProcessSummaryType processSummary;

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<Process> processes = processRepository.findAllProcesses(conditions);
        for (Process pro : processes) {
            if (!proIds.isEmpty() && !proIds.contains(pro.getId())) {
                continue;
            }
            processSummary = new ProcessSummaryType();
            processSummary.setId(pro.getId());
            processSummary.setName(pro.getName());
            processSummary.setDomain(pro.getDomain());
            processSummary.setRanking("");
            if (pro.getNativeType() != null) {
                processSummary.setOriginalNativeType(pro.getNativeType().getNatType());
            }
            if (pro.getUser() != null) {
                processSummary.setOwner(pro.getUser().getUsername());
            }
            buildVersionSummaryTypeList(processSummary, pro);

            processSummaries.getProcessSummary().add(processSummary);
        }
        return processSummaries;
    }


    /* Builds the list of version Summaries for a process. */
    private void buildVersionSummaryTypeList(ProcessSummaryType processSummary, Process pro) {
        VersionSummaryType versionSummary;

        // Find the branches for a RootFragment.
        Set<ProcessBranch> branches = pro.getProcessBranches();
        for (ProcessBranch branch : branches) {
            versionSummary = new VersionSummaryType();
            versionSummary.setName(branch.getBranchName());
            versionSummary.setCreationDate(branch.getCreationDate());
            versionSummary.setLastUpdate(branch.getLastUpdate());
            versionSummary.setRanking(branch.getRanking());
            buildNativeSummaryList(processSummary, versionSummary);

            processSummary.getVersionSummaries().add(versionSummary);

            processSummary.setLastVersion(branch.getCurrentProcessModelVersion().getVersionNumber().toString());
        }
    }

    /* Builds the list of Native Summaries for a version summary. */
    private void buildNativeSummaryList(ProcessSummaryType processSummary, VersionSummaryType versionSummary) {
        AnnotationsType annotation;
        List<Native> natives = nativeRepository.findNativeByCanonical(processSummary.getId(), versionSummary.getName());

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
        List<Annotation> anns = annotationRepository.findByUri(nat.getId());
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
