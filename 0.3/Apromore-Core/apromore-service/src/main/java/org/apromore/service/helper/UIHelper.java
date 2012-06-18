package org.apromore.service.helper;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.ProcessDao;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Used By the Services to generate the data objects used by the UI.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("UIHelper")
@Transactional(propagation = Propagation.REQUIRED)
public class UIHelper {

    @Autowired @Qualifier("AnnotationDao")
    private AnnotationDao annDao;
    @Autowired @Qualifier("ProcessDao")
    private ProcessDao prsDao;
    @Autowired @Qualifier("NativeDao")
    private NativeDao natDao;


    /**
     * Create a Process Summary record for the Front UI display.
     * @param name the process Name
     * @param processId the process Id
     * @param version the version number of this model
     * @param nativeType the native type of this model
     * @param domain The domain of this model
     * @param created the Date create
     * @param lastUpdate the Date Last Updated
     * @param username the user who updated the
     * @return the created Process Summary
     */
    public ProcessSummaryType createProcessSummary(String name, Integer processId, String version, String nativeType,
            String domain, String created, String lastUpdate, String username) {
        ProcessSummaryType proType = new ProcessSummaryType();
        VersionSummaryType verType = new VersionSummaryType();
        AnnotationsType annType = new AnnotationsType();

        proType.setId(processId);
        proType.setName(name);
        proType.setDomain(domain);
        proType.setRanking("");
        proType.setLastVersion(version);
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
     * @param conditions the search conditions
     * @param similarProcesses
     * @return the list of process Summaries
     */
    public ProcessSummariesType buildProcessSummaryList(String conditions, ProcessVersionsType similarProcesses) {
        ProcessSummariesType processSummaries = new ProcessSummariesType();
        ProcessSummaryType processSummary;

        List<Integer> proIds = buildProcessIdList(similarProcesses);
        List<Process> processes = prsDao.getAllProcesses(conditions);
        for (Process pro : processes) {
            if (!proIds.isEmpty() && !proIds.contains(pro.getProcessId())) {
                continue;
            }
            processSummary = new ProcessSummaryType();
            processSummary.setId(pro.getProcessId());
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
            buildNativeSummaryList(processSummary.getId(), versionSummary);

            processSummary.getVersionSummaries().add(versionSummary);
            processSummary.setLastVersion(versionSummary.getName());
        }
    }

    /* Builds the list of Native Summaries for a version summary. */
    private void buildNativeSummaryList(Integer id, VersionSummaryType versionSummary) {
        AnnotationsType annotation;
        List<Native> natives = natDao.findNativeByCanonical(id, versionSummary.getName());

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
        List<Annotation> anns = annDao.findByUri(nat.getUri());
        for (Annotation ann : anns) {
            annotation.getAnnotationName().add(ann.getName());
        }
    }

    /* From a list of ProcessVersionTypes build a list of the id's of each */
    private List<Integer> buildProcessIdList(ProcessVersionsType similarProcesses) {
        List<Integer> proIds = new ArrayList<Integer>(0);
        if (similarProcesses != null) {
            for (ProcessVersionType pvt :similarProcesses.getProcessVersion()) {
                proIds.add(pvt.getProcessId());
            }
        }
        return proIds;
    }




    /**
     * Set the Annotation DAO object for this class. Mainly for spring tests.
     * @param annDAOJpa the Annotation Dao.
     */
    public void setAnnotationDao(AnnotationDao annDAOJpa) {
        annDao = annDAOJpa;
    }

    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     * @param prsDAOJpa the process Dao.
     */
    public void setProcessDao(ProcessDao prsDAOJpa) {
        prsDao = prsDAOJpa;
    }

    /**
     * Set the Native DAO object for this class. Mainly for spring tests.
     * @param natDAOJpa the Native Dao.
     */
    public void setNativeDao(NativeDao natDAOJpa) {
        natDao = natDAOJpa;
    }
}
