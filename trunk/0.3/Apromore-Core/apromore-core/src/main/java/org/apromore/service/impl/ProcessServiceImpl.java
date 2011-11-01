package org.apromore.service.impl;

import org.apromore.dao.AnnotationDao;
import org.apromore.dao.CanonicalDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.jpa.AnnotationDaoJpa;
import org.apromore.dao.jpa.CanonicalDaoJpa;
import org.apromore.dao.jpa.NativeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.Process;
import org.apromore.model.AnnotationsType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    private ProcessDao prsDao;
    @Autowired
    private CanonicalDao canDao;
    @Autowired
    private NativeDao natDao;
    @Autowired
    private AnnotationDao annDao;


    /**
     * @see org.apromore.service.ProcessService#readProcessSummaries(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessSummariesType readProcessSummaries(String searchExpression) {
        ProcessSummariesType processSummaries = new ProcessSummariesType();

        // Firstly, do we need to use the searchExpression

        // Now... Build the Object tree from this list of processes.
        buildProcessSummaryList(processSummaries);

        return processSummaries;
    }


    /*
     * Builds the list of process Summaries and kicks off the versions and annotations.
     */
    private void buildProcessSummaryList(ProcessSummariesType processSummaries) {
        Process process;
        ProcessSummaryType processSummary;
        List<Object[]> processes = prsDao.getAllProcesses();

        for (Object[] proc : processes) {
            process = (Process) proc[0];
            processSummary = new ProcessSummaryType();

            processSummary.setId(Long.valueOf(process.getProcessId()).intValue());
            processSummary.setName(process.getName());
            processSummary.setDomain(process.getDomain());
            processSummary.setRanking(proc[1].toString());
            if (process.getNativeType() != null) {
                processSummary.setOriginalNativeType(process.getNativeType().getNatType());
            }
            if (process.getUser() != null) {
                processSummary.setOwner(process.getUser().getUsername());
            }
            buildVersionSummaryTypeList(processSummary);

            processSummaries.getProcessSummary().add(processSummary);
        }
    }

    /*
     * Builds the list of version Summaries for a process.
     */
    private void buildVersionSummaryTypeList(ProcessSummaryType processSummary) {
        VersionSummaryType versionSummary;
        List<Canonical> canonicals = canDao.findByProcessId((long) processSummary.getId());

        for (Canonical canonical : canonicals) {
            versionSummary = new VersionSummaryType();

            versionSummary.setName(canonical.getVersionName());
            versionSummary.setCreationDate(canonical.getCreationDate());
            versionSummary.setLastUpdate(canonical.getLastUpdate());
            versionSummary.setRanking(canonical.getRanking());
            buildNativeSummaryList((long) processSummary.getId(), versionSummary);

            processSummary.getVersionSummaries().add(versionSummary);
            processSummary.setLastVersion(versionSummary.getName());
        }
    }

    /**
     * Builds the list of Native Summaries for a version summary.
     */
    private void buildNativeSummaryList(long id, VersionSummaryType versionSummary) {
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

    /**
     * Populate the Annotation names.
     */
    private void buildAnnotationNames(Native nat, AnnotationsType annotation) {
        List<Annotation> anns = annDao.findByUri(nat.getUri());
        for (Annotation ann : anns) {
            annotation.getAnnotationName().add(ann.getName());
        }
    }




    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     * @param prsDAOJpa the process Dao.
     */
    public void setProcessDao(ProcessDaoJpa prsDAOJpa) {
        prsDao = prsDAOJpa;
    }

    /**
     * Set the Canonical DAO object for this class. Mainly for spring tests.
     * @param canDAOJpa the Canonical Dao.
     */
    public void setCanonicalDao(CanonicalDaoJpa canDAOJpa) {
        canDao = canDAOJpa;
    }

    /**
     * Set the Native DAO object for this class. Mainly for spring tests.
     * @param natDAOJpa the Native Dao.
     */
    public void setNativeDao(NativeDaoJpa natDAOJpa) {
        natDao = natDAOJpa;
    }

    /**
     * Set the Annotation DAO object for this class. Mainly for spring tests.
     * @param annDAOJpa the Annotation Dao.
     */
    public void setAnnotationDao(AnnotationDaoJpa annDAOJpa) {
        annDao = annDAOJpa;
    }
}
