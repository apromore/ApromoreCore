package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.AnnotationDao;
import org.apromore.dao.CanonicalDao;
import org.apromore.dao.NativeDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.jpa.AnnotationDaoJpa;
import org.apromore.dao.jpa.CanonicalDaoJpa;
import org.apromore.dao.jpa.NativeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.exception.AnnotationNotFoundException;
import org.apromore.exception.CanonicalFormatNotFoundException;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.model.AnnotationsType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.service.CanoniserService;
import org.apromore.service.FormatService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.Format;
import org.apromore.service.search.SearchExpressionBuilder;
import org.apromore.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.*;
import java.io.*;
import java.util.List;

/**
 * Implementation of the UserService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    // Services
    @Autowired
    private CanoniserService canSrv;
    @Autowired
    private UserService usrSrv;
    @Autowired
    private FormatService fmtSrv;

    // Dao's
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
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessSummariesType readProcessSummaries(String searchExpression) {
        ProcessSummariesType processSummaries = new ProcessSummariesType();
        
        try {
            // Firstly, do we need to use the searchExpression
            SearchExpressionBuilder seb = new SearchExpressionBuilder();
            String conditions = seb.buildSearchConditions(searchExpression);
            LOGGER.debug("Search Expression Builder output: " + conditions);

            // Now... Build the Object tree from this list of processes.
            buildProcessSummaryList(conditions, processSummaries);
        } catch (UnsupportedEncodingException usee) {
            LOGGER.error("Failed to get Process Sumamries: " + usee.toString());
        }

        return processSummaries;
    }


    /**
     * @see org.apromore.service.ProcessService#exportFormat(long, String, String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DataSource exportFormat(final long processId, final String version, final String format) throws ExportFormatException {
        String read;
        DataSource dataSource;
        try {
            if (Constants.CANONICAL.compareTo(format) == 0) {
                read = canDao.getCanonical(processId, version).getContent();
            } else if (format.startsWith(Constants.ANNOTATIONS)) {
                String type = format.substring(Constants.ANNOTATIONS.length() + 3, format.length());
                read = annDao.getAnnotation(processId, version, type).getContent();
            } else {
                read = natDao.getNative(processId, version, format).getContent();
            }
            dataSource = new ByteArrayDataSource(read, "text/xml");
        } catch (Exception e) {
            throw new ExportFormatException(e.getMessage(), e.getCause());
        }
        return dataSource;
    }

    /**
     * Returns the Canonical format as XML.
     *
     * @see org.apromore.service.ProcessService#getCanonicalAnf(long, String, boolean, String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Format getCanonicalAnf(final long processId, final String version, final boolean withAnn, final String annName)
            throws ExportFormatException {
        Format result = new Format();
        try {

            String canonical = canDao.getCanonical(processId, version).getContent();
            if (withAnn) {
                String annotation = annDao.getAnnotation(processId, version, annName).getContent();
                result.setAnf(new ByteArrayDataSource(annotation, "text/xml"));
            }
            result.setCpf(new ByteArrayDataSource(canonical, "text/xml"));

        } catch (CanonicalFormatNotFoundException cfnfe) {
            throw new ExportFormatException(cfnfe.getMessage(), cfnfe.getCause());
        } catch (AnnotationNotFoundException afnfe) {
            throw new ExportFormatException(afnfe.getMessage(), afnfe.getCause());
        } catch (IOException e) {
            throw new ExportFormatException(e.getMessage(), e.getCause());
        }
        return result;
    }


    /**
     * @see org.apromore.service.ProcessService#importProcess(String, String, String, String, String, DataHandler, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    public ProcessSummaryType importProcess(String username, String processName, String cpfURI, String version, String natType,
            DataHandler cpf, String domain, String documentation, String created, String lastUpdate) throws ImportException {
        LOGGER.info("Executing operation canoniseProcess");

        CanonisedProcess cp;
        ProcessSummaryType pro;
        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();

        try {

            canSrv.canonise(cpfURI, cpf.getInputStream(), natType, anf_xml, cpf_xml);

            cp = new CanonisedProcess();
            cp.setAnf(new ByteArrayInputStream(anf_xml.toByteArray()));
            cp.setCpf(new ByteArrayInputStream(cpf_xml.toByteArray()));

            User user = usrSrv.findUser(username);
            NativeType nativeType = fmtSrv.findNativeType(natType);

            pro = storeNativeAndCpf(processName, version, cpfURI, cpf.getInputStream(), domain, created, lastUpdate, cp, user, nativeType);

//            process = client.storeNativeCpf(username, processName, cpfURI, domain, nativeType, versionName,
//                    "", created, lastUpdate, cpf, cpf_is, anf_is);
        } catch (Exception ex) {
            LOGGER.error("Canonisation Process Failed: " + ex.toString());
            throw new ImportException(ex);
        }

        return pro;
    }


    /**
     * @see org.apromore.service.ProcessService#storeNativeAndCpf(String, String, String, InputStream, String, String, String, CanonisedProcess, User, NativeType)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessSummaryType storeNativeAndCpf(String processName, String version, String cpfURI, InputStream cpf, String domain,
            String created, String lastUpdate, CanonisedProcess cp, User user, NativeType nativeType) throws JAXBException {
        Process process = new Process();
        process.setDomain(domain);
        process.setName(processName);
        process.setUser(user);
        process.setNativeType(nativeType);
        prsDao.save(process);

        String processId = Long.toString(process.getProcessId());
        InputStream sync_npf = StreamUtil.copyParam2NPF(cpf, nativeType.getNatType(), processName, version, user.getUsername(), created, lastUpdate);
        InputStream sync_cpf = StreamUtil.copyParam2CPF(cp.getCpf(), processId, processName, version, user.getUsername(), created, lastUpdate);

        String nativeString = StreamUtil.inputStream2String(sync_npf).trim();
        String cpfString = StreamUtil.inputStream2String(sync_cpf).trim();
        String anfString = StreamUtil.inputStream2String(cp.getAnf()).trim();

        Canonical canonical = new Canonical();
        canonical.setUri(cpfURI);
        canonical.setProcess(process);
        canonical.setVersionName(version);
        canonical.setCreationDate(created);
        canonical.setLastUpdate(lastUpdate);
        canonical.setContent(cpfString);
        canDao.save(canonical);

        Native nat = new Native();
        nat.setNativeType(nativeType);
        nat.setCanonical(canonical);
        nat.setContent(nativeString);
        natDao.save(nat);

        Annotation annotation = new Annotation();
        annotation.setCanonical(canonical);
        annotation.setNatve(nat);
        annotation.setContent(anfString);
        annotation.setName(Constants.INITIAL_ANNOTATION);
        annDao.save(annotation);

        return createProcessSummary(processName, processId, version, nativeType.getNatType(), domain, created, lastUpdate, user.getUsername());
    }


    private ProcessSummaryType createProcessSummary(String name, String processId, String version, String nativeType, String domain,
            String created, String lastUpdate, String username) {
        ProcessSummaryType proType = new ProcessSummaryType();
        VersionSummaryType verType = new VersionSummaryType();
        AnnotationsType annType = new AnnotationsType();

        proType.setDomain(domain);
        proType.setId(Integer.parseInt(processId));
        proType.setLastVersion(version);
        proType.setName(name);
        proType.setOriginalNativeType(nativeType);
        proType.setRanking("");
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

    /*
    * Builds the list of process Summaries and kicks off the versions and annotations.
    */
    private void buildProcessSummaryList(String conditions, ProcessSummariesType processSummaries) {
        Process process;
        ProcessSummaryType processSummary;
        List<Object[]> processes = prsDao.getAllProcesses(conditions);

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
     * @param id the id of the model
     * @param versionSummary the version summary
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
     * @param nat The native type
     * @param annotation the annotations for this Model
     */
    private void buildAnnotationNames(Native nat, AnnotationsType annotation) {
        List<Annotation> anns = annDao.findByUri(nat.getUri());
        for (Annotation ann : anns) {
            annotation.getAnnotationName().add(ann.getName());
        }
    }









    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     *
     * @param prsDAOJpa the process Dao.
     */
    public void setProcessDao(ProcessDaoJpa prsDAOJpa) {
        prsDao = prsDAOJpa;
    }

    /**
     * Set the Canonical DAO object for this class. Mainly for spring tests.
     *
     * @param canDAOJpa the Canonical Dao.
     */
    public void setCanonicalDao(CanonicalDaoJpa canDAOJpa) {
        canDao = canDAOJpa;
    }

    /**
     * Set the Native DAO object for this class. Mainly for spring tests.
     *
     * @param natDAOJpa the Native Dao.
     */
    public void setNativeDao(NativeDaoJpa natDAOJpa) {
        natDao = natDAOJpa;
    }

    /**
     * Set the Annotation DAO object for this class. Mainly for spring tests.
     *
     * @param annDAOJpa the Annotation Dao.
     */
    public void setAnnotationDao(AnnotationDaoJpa annDAOJpa) {
        annDao = annDAOJpa;
    }

    /**
     * Set the Canoniser Service for this class. Mainly for spring tests.
     *
     * @param canSrv the service
     */
    public void setCanoniserService(CanoniserService canSrv) {
        this.canSrv = canSrv;
    }

    /**
     * Set the User Service for this class. Mainly for spring tests.
     *
     * @param usrSrv the service
     */
    public void setUserService(UserService usrSrv) {
        this.usrSrv = usrSrv;
    }

    /**
     * Set the Format Service for this class. Mainly for spring tests.
     *
     * @param fmtSrv the service
     */
    public void setFormatService(FormatService fmtSrv) {
        this.fmtSrv = fmtSrv;
    }

}
