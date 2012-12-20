package org.apromore.service.impl;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Net;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.Object;
import org.apromore.dao.model.ObjectAttribute;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelAttribute;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.ProcessUser;
import org.apromore.dao.model.Resource;
import org.apromore.dao.model.ResourceAttribute;
import org.apromore.dao.model.User;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.IAttribute;
import org.apromore.graph.canonical.ICPFObject;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ObjectTypeEnum;
import org.apromore.graph.canonical.ResourceTypeEnum;
import org.apromore.manager.client.helper.PluginHelper;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ComposerService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.service.model.NameValuePair;
import org.apromore.service.search.SearchExpressionBuilder;
import org.apromore.util.StreamUtil;
import org.apromore.util.VersionNameUtil;
import org.apromore.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.wfmc._2008.xpdl2.PackageType;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;


/**
 * Implementation of the UserService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private AnnotationRepository annotationRepo;
    private ContentRepository contentRepo;
    private NativeRepository nativeRepo;
    private ProcessBranchRepository processBranchRepo;
    private ProcessRepository processRepo;
    private FragmentVersionRepository fragmentVersionRepo;
    private FragmentVersionDagRepository fragmentVersionDagRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private CanonicalConverter converter;
    private CanoniserService canoniserSrv;
    private FragmentService fragmentSrv;
    private LockService lService;
    private UserService userSrv;
    private FormatService formatSrv;
    private ComposerService composerSrv;
    private DecomposerService decomposerSrv;
    private UserInterfaceHelper ui;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepo Annotations repository.
     * @param contentRepo Content Repository
     * @param nativeRepo Native Repository.
     * @param processBranchRepo Process Branch Map Repository.
     * @param processRepo Process Repository
     * @param fragmentVersionRepo Fragment Version Repository.
     * @param fragmentVersionDagRepo Fragment Version Dag Repository.
     * @param processModelVersionRepo Process Model Version Repository.
     * @param converter Canonical Format Converter.
     * @param canoniserSrv Canoniser Service.
     * @param fragmentSrv Fragment Service.
     * @param lService Lock Service.
     * @param userSrv User Service
     * @param formatSrv Format Service.
     * @param composerSrv composer Service.
     * @param decomposerSrv decomposer Service.
     * @param ui User Interface Helper.
     */
    @Inject
    public ProcessServiceImpl(final AnnotationRepository annotationRepo, final ContentRepository contentRepo, final NativeRepository nativeRepo, final ProcessBranchRepository processBranchRepo, ProcessRepository processRepo, final FragmentVersionRepository fragmentVersionRepo, final FragmentVersionDagRepository fragmentVersionDagRepo, final ProcessModelVersionRepository processModelVersionRepo, final CanonicalConverter converter, final CanoniserService canoniserSrv, final FragmentService fragmentSrv, final LockService lService, final UserService userSrv, final FormatService formatSrv, final ComposerService composerSrv, final DecomposerService decomposerSrv, final UserInterfaceHelper ui) {
        this.annotationRepo = annotationRepo;
        this.contentRepo = contentRepo;
        this.nativeRepo = nativeRepo;
        this.processBranchRepo = processBranchRepo;
        this.processRepo = processRepo;
        this.fragmentVersionRepo = fragmentVersionRepo;
        this.fragmentVersionDagRepo = fragmentVersionDagRepo;
        this.processModelVersionRepo = processModelVersionRepo;
        this.converter = converter;
        this.canoniserSrv = canoniserSrv;
        this.fragmentSrv = fragmentSrv;
        this.lService = lService;
        this.userSrv = userSrv;
        this.formatSrv = formatSrv;
        this.composerSrv = composerSrv;
        this.decomposerSrv = decomposerSrv;
        this.ui = ui;
    }

    /**
     * @see org.apromore.service.ProcessService#readProcessSummaries(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessSummariesType readProcessSummaries(final String searchExpression) {
        ProcessSummariesType processSummaries = null;

        try {
            // Firstly, do we need to use the searchExpression
            SearchExpressionBuilder seb = new SearchExpressionBuilder();
            String conditions = seb.buildSearchConditions(searchExpression);
            LOGGER.debug("Search Expression Builder output: " + conditions);

            // Now... Build the Object tree from this list of processes.
            processSummaries = ui.buildProcessSummaryList(conditions, null);
        } catch (UnsupportedEncodingException usee) {
            LOGGER.error("Failed to get Process Summaries: " + usee.toString());
        }

        return processSummaries;
    }


    /**
     * @see org.apromore.service.ProcessService#importProcess(String, String, String, String, String, org.apromore.service.model.CanonisedProcess, java.io.InputStream, String, String, String, String)
     *      * {@inheritDoc}
     */
    @Override
    public ProcessModelVersion importProcess(final String username, final String processName, final String cpfURI, final String version, final String natType, final CanonisedProcess cpf, final InputStream nativeXml, final String domain, final String documentation, final String created, final String lastUpdate) throws ImportException {
        LOGGER.info("Executing operation canoniseProcess");
        ProcessModelVersion pmv;

        try {
            User user = userSrv.findUserByLogin(username);
            NativeType nativeType = formatSrv.findNativeType(natType);

            pmv = addProcess(processName, version, user, nativeType, domain, documentation, created, lastUpdate, cpf);
            formatSrv.storeNative(processName, version, pmv, nativeXml, created, lastUpdate, user, nativeType, cpf);
        } catch (Exception e) {
            LOGGER.error("Failed to import process {} with native type {}", processName, natType);
            LOGGER.error("Original exception was: ", e);
            throw new ImportException(e);
        }

        return pmv;
    }

    /**
     * @see org.apromore.service.ProcessService#exportProcess(String, Integer, String, String, String, boolean, java.util.Set)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ExportFormatResultType exportProcess(final String name, final Integer processId, final String version, final String format, final String annName, final boolean withAnn, Set<RequestParameterType<?>> canoniserProperties) throws ExportFormatException {
        try {
            CanonicalProcessType cpt = getCurrentProcessModel(name, version, false);

            // TODO XML model of web service should not already be used here, but in ManagerEndpoint
            ExportFormatResultType exportResult = new ExportFormatResultType();
            if ((withAnn && format.startsWith(Constants.INITIAL_ANNOTATION)) || format.startsWith(Constants.ANNOTATIONS)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(nativeRepo.getNative(processId, version, format).getContent(), "text/xml")));
            } else if (format.equals(Constants.CANONICAL)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(canoniserSrv.CPFtoString(cpt), "text/xml")));
            } else {
                DecanonisedProcess dp;
                if (withAnn) {
                    String annotation = annotationRepo.getAnnotation(processId, version, annName).getContent();
                    AnnotationsType anf = ANFSchema.unmarshalAnnotationFormat(new ByteArrayDataSource(annotation, "text/xml").getInputStream(), false).getValue();
                    dp = canoniserSrv.deCanonise(processId, version, format, cpt, anf, canoniserProperties);
                } else {
                    dp = canoniserSrv.deCanonise(processId, version, format, cpt, null, canoniserProperties);
                }
                exportResult.setMessage(PluginHelper.convertFromPluginMessages(dp.getMessages()));
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(dp.getNativeFormat(), "text/xml")));
            }
            return exportResult;
        } catch (Exception e) {
            LOGGER.error("Failed to export process model {} to format {}", name, format);
            LOGGER.error("Original exception was: ", e);
            throw new ExportFormatException(e);
        }
    }


    /**
     * @see org.apromore.service.ProcessService#updateProcessMetaData(Integer, String, String, String, String, String, String)
     *      {@inheritDoc}
     */
    @Override
    public void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username, final String preVersion, final String newVersion, final String ranking) throws UpdateProcessException {
        LOGGER.info("Executing operation update process meta data.");
        try {
            Process process = processRepo.findOne(processId);
            process.setDomain(domain);
            process.setName(processName);
            process.setUser(userSrv.findUserByLogin(username));

            ProcessModelVersion processModelVersion = processModelVersionRepo.getCurrentProcessModelVersion(processId, preVersion);
            processModelVersion.setVersionName(newVersion);

            ProcessBranch branch = processModelVersion.getProcessBranch();
            branch.setRanking(ranking);

            updateNativeRecords(processModelVersion.getNatives(), processName, username, newVersion);

            processRepo.save(process);
            processModelVersionRepo.save(processModelVersion);
            processBranchRepo.save(branch);
        } catch (Exception e) {
            throw new UpdateProcessException(e.getMessage(), e.getCause());
        }
    }


    /**
     * @see ProcessService#addProcessModelVersion(ProcessBranch, String, Double, String, int, int)
     *      {@inheritDoc}
     */
    @Override
    public ProcessModelVersion addProcessModelVersion(final ProcessBranch branch, final String rootFragmentVersionUri, final Double versionNumber, final String versionName, final int numVertices, final int numEdges) throws ExceptionDao {
        ProcessModelVersion pmv = new ProcessModelVersion();

        pmv.setProcessBranch(branch);
        pmv.setRootFragmentVersion(fragmentVersionRepo.findFragmentVersionByUri(rootFragmentVersionUri));
        pmv.setVersionNumber(versionNumber);
        pmv.setVersionName(versionName);
        pmv.setNumVertices(numVertices);
        pmv.setNumEdges(numEdges);

        return processModelVersionRepo.save(pmv);
    }


    /**
     * @see ProcessService#addProcess(String, String, org.apromore.dao.model.User, org.apromore.dao.model.NativeType, String, String, String, String, org.apromore.service.model.CanonisedProcess)
     *      {@inheritDoc}
     */
    @Override
    public ProcessModelVersion addProcess(final String processName, final String versionNumber, final User user,
            final NativeType nativeType, final String domain, final String documentation, final String created, final String lastUpdated,
            final CanonisedProcess cpf) throws ImportException {
        if (cpf == null) {
            LOGGER.error("Process " + processName + " Failed to import correctly.");
            throw new ImportException("Process " + processName + " Failed to import correctly.");
        } else if (processRepo.getProcessByName(processName) != null) {
            LOGGER.error("Process " + processName + " was found to already exist in the Repository.");
            throw new ImportException("Process " + processName + " was found to already exist in the Repository.");
        }

        // TODO: Is this a process or ProcessModelVersion we return
        Process process;
        ProcessModelVersion pmv = null;
        Map<String, ProcessModelVersion> models = new HashMap<String, ProcessModelVersion>(0);
        try {
            process = insertProcess(processName, user, nativeType, domain);
            ProcessBranch branch = insertProcessBranch(process, created, lastUpdated, Constants.TRUNK_NAME);

            for (NetType net : cpf.getCpt().getNet()) {
                if (net.getNode() != null) {
                    Canonical can = converter.convert(createNet(cpf, net));
                    LOGGER.info("Starting to process Net: " + net.getId() + " - " + net.getName());

                    pmv = createProcessModelVersion(process, branch, can, cpf.getCpt().getRootIds(), net.getId(),
                            versionNumber, Constants.TRUNK_NAME);
                    OperationContext netRoot = decomposerSrv.decompose(can, pmv);
                    if (netRoot != null) {
                        pmv.setRootFragmentVersion(netRoot.getCurrentFragment());
                        models.put(net.getId(), pmv);
                    }
                }
            }
            createSubProcessReferences(cpf, models);
            createRootProcessReferences(process, cpf, models);
        } catch (Exception re) {
            LOGGER.error("Failed to add the process model " + processName, re);
        }
        return pmv;
    }



    /**
     * @see ProcessService#getCanonicalFormat(org.apromore.dao.model.ProcessModelVersion)
     *      {@inheritDoc}
     */
    @Override
    public CanonicalProcessType getCanonicalFormat(final ProcessModelVersion pmv) {
        String processName = pmv.getProcessBranch().getProcess().getName();
        String branchName = pmv.getProcessBranch().getBranchName();

        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>(1);
        pmvs.add(pmv);

        return getCanonicalFormat(pmvs, processName, branchName, false);
    }


    /**
     * @see ProcessService#getCanonicalFormat(java.util.List, String, String, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CanonicalProcessType getCanonicalFormat(final List<ProcessModelVersion> pmvs, final String processName, final String branchName, final boolean lock) {
        Canonical canonical;
        CanonicalProcessType tmp;
        CanonicalProcessType result = new CanonicalProcessType();
        try {
            for (ProcessModelVersion pmv : pmvs) {
                canonical = composerSrv.compose(pmv.getRootFragmentVersion());
                canonical.setProperty(Constants.PROCESS_NAME, processName);
                canonical.setProperty(Constants.BRANCH_NAME, branchName);
                canonical.setProperty(Constants.BRANCH_ID, pmv.getProcessBranch().getId().toString());
                canonical.setProperty(Constants.VERSION_NUMBER, Double.toString(pmv.getVersionNumber()));
                canonical.setProperty(Constants.PROCESS_MODEL_VERSION_ID, pmv.getId().toString());
                canonical.setProperty(Constants.ROOT_FRAGMENT_ID, pmv.getRootFragmentVersion().getId().toString());
                if (lock) {
                    canonical.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
                }

                tmp = converter.convert(canonical);

                result.getNet().addAll(tmp.getNet());
                result.getResourceType().addAll(tmp.getResourceType());
                result.getAttribute().addAll(tmp.getAttribute());
                if (pmv.getNet() != null) {
                    result.setName(processName);
                    result.setUri(pmv.getProcessBranch().getProcess().getId().toString());
                    result.setAuthor(pmv.getProcessBranch().getProcess().getUser().getUsername());
                    result.setCreationDate(pmv.getProcessBranch().getCreationDate());
                    result.setModificationDate(pmv.getProcessBranch().getLastUpdate());
                    result.setVersion(Double.toString(pmv.getVersionNumber()));
                    result.getRootIds().add(pmv.getNet().getNetId());
                }
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the current version of the process model " + processName + " - " + branchName;
            LOGGER.error(msg, e);
        }
        return result;
    }


    /**
     * @see ProcessService#getCurrentProcessModel(String, String, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CanonicalProcessType getCurrentProcessModel(final String processName, final String branchName, final boolean lock) throws LockFailedException {
        List<ProcessModelVersion> pmvs = processModelVersionRepo.getCurrentProcessModelVersion(processName, branchName);

        if (pmvs == null) {
            return null;
        }
        if (lock) {
            for (ProcessModelVersion pmv : pmvs) {
                boolean locked = lService.lockFragment(pmv.getRootFragmentVersion().getId());
                if (!locked) {
                    throw new LockFailedException();
                }
            }
        }

        return getCanonicalFormat(pmvs, processName, branchName, lock);
    }


    /**
     * @see ProcessService#propagateChangesWithLockRelease(String, String, java.util.List)
     *      {@inheritDoc}
     */
    @Override
    public void propagateChangesWithLockRelease(String originalFragmentId, String updatedFragmentId, List<String> composingFragmentIds) throws ExceptionDao {
        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersionsByURI(originalFragmentId);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, updatedFragmentId, composingFragmentIds);
        }

        LOGGER.debug("Unlocking the original fragment: " + originalFragmentId);
        lService.unlockFragmentByURI(originalFragmentId);
        lService.unlockDescendantFragmentsByURI(originalFragmentId);

        LOGGER.debug("Propagating to parent fragments of fragment: " + originalFragmentId);
        List<FragmentVersion> lockedParents = fragmentVersionRepo.getLockedParentFragmentIdsByUri(originalFragmentId);
        for (FragmentVersion parent : lockedParents) {
            propagateToParentsWithLockRelease(parent.getUri(), originalFragmentId, updatedFragmentId, composingFragmentIds);
        }
    }


    /**
     * @see ProcessService#deleteProcess(org.apromore.dao.model.Process)
     *      {@inheritDoc}
     */
    @Override
    public void deleteProcess(final Process process) {
        processRepo.delete(process);
    }

    /**
     * @see ProcessService#deleteProcessModel(java.util.List)
     *      {@inheritDoc}
     */
    @Override
    public void deleteProcessModel(final List<NameValuePair> models) {
        List<ProcessModelVersion> pvids;
        for (NameValuePair entry : models) {
            try {
                LOGGER.debug("Retrieved the pvid of the current version of " + entry.getName() + " - " + entry.getValue() + " to be deleted.");
                pvids = processModelVersionRepo.getCurrentProcessModelVersion(entry.getName(), entry.getValue());
                for (ProcessModelVersion pmv : pvids) {
                    deleteProcessModel(pmv);
                }

                updateProcessTree(entry.getName(), entry.getValue());
            } catch (Exception e) {
                String msg = "Failed to delete the current version of the branch " + entry.getValue() + " of the process model " + entry.getValue();
                LOGGER.error(msg, e);
            }
        }
    }


    /* Update a list of native process models with this new meta data, */
    private void updateNativeRecords(final Set<Native> natives, final String processName, final String username, final String version) throws CanoniserException, JAXBException {
        for (Native n : natives) {
            String natType = n.getNativeType().getNatType();
            InputStream inStr = new ByteArrayInputStream(n.getContent().getBytes());
            CanonisedProcess cp = canoniserSrv.canonise(natType, inStr, new HashSet<RequestParameterType<?>>(0));

            //TODO why is this done here? apromore should not know about native format outside of canonisers
            if (natType.compareTo("XPDL 2.1") == 0) {
                PackageType pakType = StreamUtil.unmarshallXPDL(inStr);
                StreamUtil.copyParam2XPDL(pakType, processName, version, username, null, null);
                n.setContent(StreamUtil.marshallXPDL(pakType));
            }
        }
    }

    /* Inserts a new process into the DB. */
    private Process insertProcess(final String processName, final User user, final NativeType nativeType, final String domain)
            throws ImportException {
        LOGGER.info("Executing operation Insert Process");
        Process process = new Process();

        try {
            process.setName(processName);
            process.setUser(user);
            process.setDomain(domain);
            process.setNativeType(nativeType);

            ProcessUser processUser = new ProcessUser();
            processUser.setProcess(process);
            processUser.setUser(user);
            processUser.setHasRead(true);
            processUser.setHasWrite(true);
            processUser.setHasOwnership(true);

            user.getProcessUsers().add(processUser);
            process.getProcessUsers().add(processUser);

            return processRepo.save(process);
        } catch (Exception ex) {
            LOGGER.error("Importing a Process Failed: " + ex.toString());
            throw new ImportException(ex);
        }
    }

    /* inserts a new branch into the DB. */
    private ProcessBranch insertProcessBranch(final Process process, final String created, final String lastUpdated, final String name) throws ImportException {
        LOGGER.info("Executing operation Insert Branch");
        ProcessBranch branch = new ProcessBranch();

        try {
            branch.setProcess(process);
            branch.setBranchName(name);
            branch.setCreationDate(created);
            branch.setLastUpdate(lastUpdated);

            process.getProcessBranches().add(branch);

            return processBranchRepo.save(branch);
        } catch (Exception ex) {
            LOGGER.error("Importing a Branch Failed: " + ex.toString());
            throw new ImportException(ex);
        }
    }

    private ProcessModelVersion createProcessModelVersion(final Process process, final ProcessBranch branch, final Canonical proModGrap,
            List<String> rootIds, final String netId, final String versionNumber, final String versionName) {
        ProcessModelVersion processModel = new ProcessModelVersion();

        processModel.setProcessBranch(branch);
        processModel.setOriginalId(netId);
        processModel.setVersionName(versionName);
        processModel.setVersionNumber(Double.valueOf(versionNumber));
        processModel.setNumEdges(proModGrap.countEdges());
        processModel.setNumVertices(proModGrap.countVertices());
        processModel.setLockStatus(Constants.NO_LOCK);

        branch.setCurrentProcessModelVersion(processModel);

        addAttributesToProcessModel(proModGrap, processModel);
        addObjectsToProcessModel(proModGrap, processModel);
        addResourcesToProcessModel(proModGrap, processModel);
        updateResourcesOnProcessModel(proModGrap.getResources(), processModel);
        //addNetRecords(processModel, process, rootIds, netId);

        return processModelVersionRepo.save(processModel);
    }


    /* Insert the Attributes to the ProcessModel */
    private void addAttributesToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
        ProcessModelAttribute pmvAtt;
        for (CPFNode node : proModGrap.getNodes()) {
            for (Map.Entry<String, IAttribute> obj : node.getAttributes().entrySet()) {
                pmvAtt = new ProcessModelAttribute();
                pmvAtt.setName(obj.getKey());
                pmvAtt.setValue(obj.getValue().getValue());
                if (obj.getValue().getAny() instanceof Element) {
                    pmvAtt.setValue(XMLUtils.anyElementToString((Element) obj.getValue().getAny()));
                }
                pmvAtt.setProcessModelVersion(process);
                process.getProcessModelAttributes().add(pmvAtt);
            }
        }
    }

    /* Insert the Objects to the ProcessModel TODO: Attributes */
    private void addObjectsToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
        Object objTyp;
        if (proModGrap.getObjects() != null) {
            for (ICPFObject cpfObj : proModGrap.getObjects()) {
                objTyp = new org.apromore.dao.model.Object();
                objTyp.setUri(cpfObj.getId());
                objTyp.setName(cpfObj.getName());
                objTyp.setNetId(cpfObj.getNetId());
                objTyp.setConfigurable(cpfObj.isConfigurable());
                objTyp.setProcessModelVersion(process);
                if (cpfObj.getObjectType().equals(ObjectTypeEnum.HARD)) {
                    objTyp.setType(ObjectTypeEnum.HARD);
                } else {
                    objTyp.setType(ObjectTypeEnum.SOFT);
                    objTyp.setSoftType(cpfObj.getSoftType());
                }

                addObjectAttributes(objTyp, cpfObj);

                process.getObjects().add(objTyp);
            }
        }
    }

    /* Add Attributes to the Object Reference. */
    private void addObjectAttributes(final org.apromore.dao.model.Object object, final ICPFObject cpfObject) {
        ObjectAttribute objAtt;
        for (Map.Entry<String, IAttribute> e : cpfObject.getAttributes().entrySet()) {
            objAtt = new ObjectAttribute();
            objAtt.setName(e.getKey());
            objAtt.setValue(e.getValue().getValue());
            objAtt.setObject(object);

            object.getObjectAttributes().add(objAtt);
        }
    }

    /* Insert the Resources to the ProcessModel TODO: Attributes */
    private void addResourcesToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
        Resource resTyp;
        if (proModGrap.getResources() != null) {
            for (ICPFResource cpfRes : proModGrap.getResources()) {
                resTyp = new Resource();
                resTyp.setUri(cpfRes.getId());
                resTyp.setName(cpfRes.getName());
                resTyp.setOriginalId(cpfRes.getOriginalId());
                resTyp.setConfigurable(cpfRes.isConfigurable());
                if (cpfRes.getResourceType() != null) {
                    if (cpfRes.getResourceType().equals(ResourceTypeEnum.HUMAN)) {
                        resTyp.setType(ResourceTypeEnum.HUMAN);
                        if (cpfRes.getHumanType() != null) {
                            resTyp.setTypeName(cpfRes.getHumanType().value());
                        }
                    } else {
                        resTyp.setType(ResourceTypeEnum.NONHUMAN);
                        if (cpfRes.getNonHumanType() != null) {
                            resTyp.setTypeName(cpfRes.getNonHumanType().value());
                        }
                    }
                }
                resTyp.setProcessModelVersion(process);

                addResourceAttributes(resTyp, cpfRes);

                process.getResources().add(resTyp);
            }
        }
    }

    /* Add Attributes to the Object Reference. */
    private void addResourceAttributes(final Resource resource, final ICPFResource cpfResource) {
        ResourceAttribute resAtt;
        for (Map.Entry<String, IAttribute> e : cpfResource.getAttributes().entrySet()) {
            resAtt = new ResourceAttribute();
            resAtt.setName(e.getKey());
            resAtt.setValue(e.getValue().getValue());
            resAtt.setResource(resource);

            resource.getResourceAttributes().add(resAtt);
        }
    }

    /* Update to Process Models Resource information, specifically the Specialisation Id's. This can't be done at
       time of original entry as we might not have all the Resources in memory. */
    private void updateResourcesOnProcessModel(Set<ICPFResource> resources, ProcessModelVersion processModel) {
        if (resources != null) {
            for (ICPFResource cpfRes : resources) {
                addSpecialisations(processModel, cpfRes, findResource(processModel.getResources(), cpfRes.getId()));
            }
        }
    }

    /* Update the Resource with it's specialisations Ids. */
    private void addSpecialisations(ProcessModelVersion processModel, ICPFResource cpfRes, Resource resource) {
        if (cpfRes.getSpecializationIds() != null) {
            for (String resourceId : cpfRes.getSpecializationIds()) {
                resource.getSpecialisations().add(findResource(processModel.getResources(), resourceId));
            }
        }
    }


    /* Update the branch links or remove if no processModelVersion is attached. */
    private void updateProcessTree(final String processName, final String branchName) {
        Process process = processRepo.getProcessByName(processName);
        ProcessBranch pBranch = processBranchRepo.getProcessBranchByProcessBranchName(process.getId(), branchName);

        if (process.getProcessBranches() != null && process.getProcessBranches().size() == 1 && pBranch.getProcessModelVersions().size() == 0) {
            deleteProcess(process);
        } else {
            ProcessModelVersion pmv = processModelVersionRepo.getCurrentProcessModelVersion(pBranch.getId());
            pBranch.setCurrentProcessModelVersion(pmv);
            processBranchRepo.save(pBranch);
        }
    }

    private void createNewProcessModelVersion(ProcessModelVersion pmv, String rootFragmentUri, List<String> composingFragmentIds) throws ExceptionDao {
        Double versionNumber = pmv.getVersionNumber() + 1;
        String versionName = VersionNameUtil.getNextVersionName(pmv.getVersionName());
        ProcessModelVersion pv = addProcessModelVersion(pmv.getProcessBranch(), rootFragmentUri, versionNumber, versionName, 0, 0);
        //fragmentSrv.addProcessFragmentMappings(pv.getId(), composingFragmentIds);
    }

    private void propagateToParentsWithLockRelease(String parentUri, String originalFragmentId, String updatedFragmentId, List<String> composingFragmentIds) throws ExceptionDao {
        LOGGER.debug("Propagating - fragment: " + originalFragmentId + ", parent: " + parentUri);
        String newParentUri = createNewFragmentVersionByReplacingChild(parentUri, originalFragmentId, updatedFragmentId);
        composingFragmentIds.add(newParentUri);
        fillUnchangedDescendantIds(newParentUri, updatedFragmentId, composingFragmentIds);

        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersionsByURI(parentUri);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, newParentUri, composingFragmentIds);
            lService.unlockProcessModelVersion(pmv.getId());
        }
        lService.unlockFragmentByURI(parentUri);

        List<FragmentVersion> nextLockedParents = fragmentVersionRepo.getLockedParentFragmentIdsByUri(parentUri);
        for (FragmentVersion nextParent : nextLockedParents) {
            propagateToParentsWithLockRelease(nextParent.getUri(), parentUri, newParentUri, composingFragmentIds);
        }
        LOGGER.debug("Completed propagation - fragment: " + originalFragmentId + ", parent: " + parentUri);
    }

    private void fillUnchangedDescendantIds(String parentUri, String updatedChildId, List<String> composingFragmentIds) throws ExceptionDao {
        List<FragmentVersionDag> allChild = fragmentVersionDagRepo.getChildMappingsByURI(parentUri);
        for (FragmentVersionDag child : allChild) {
            if (!child.getChildFragmentVersion().getUri().equals(updatedChildId)) {
                composingFragmentIds.add(child.getChildFragmentVersion().getUri());
                fillDescendantIds(child.getChildFragmentVersion().getId(), composingFragmentIds);
            }
        }
    }

    private void fillDescendantIds(Integer fragmentId, List<String> composingFragmentIds) throws ExceptionDao {
        List<FragmentVersionDag> allChild = fragmentVersionDagRepo.getChildMappings(fragmentId);
        for (FragmentVersionDag child : allChild) {
            composingFragmentIds.add(child.getChildFragmentVersion().getUri());
            fillDescendantIds(child.getChildFragmentVersion().getId(), composingFragmentIds);
        }
    }

    private String createNewFragmentVersionByReplacingChild(String fragmentUri, String oldChildId, String newChildId) throws ExceptionDao {
        FragmentVersion fv = fragmentVersionRepo.findFragmentVersionByUri(fragmentUri);
        int lockType = 0;
        int lockCount = 0;
        if (fv.getLockStatus() == 1) {
            if (fv.getLockCount() > 1) {
                lockType = 1;
                lockCount = fv.getLockCount() - 1;
            }
        }

        Map<String, String> childMappings = createChildMap(fragmentVersionDagRepo.getChildMappingsByURI(fragmentUri));
        Set<String> pockets = childMappings.keySet();
        for (String pocketId : pockets) {
            String childId = childMappings.get(pocketId);
            if (childId.equals(oldChildId)) {
                childMappings.put(pocketId, newChildId);
            }
        }

        ProcessModelVersion pmv = fv.getProcessModelVersions().iterator().next();
        // TODO size of the new fragment has to calculated correctly by considering the sizes of the old child and new child
        return fragmentSrv.addFragmentVersion(pmv, fv.getContent(), childMappings, fragmentUri, lockType, lockCount, fv.getFragmentSize(), fv.getFragmentType()).getUri();
    }


    private Map<String, String> createChildMap(List<FragmentVersionDag> fvds) {
        Map<String, String> childMappings = new HashMap<String, String>();
        for (FragmentVersionDag fvd : fvds) {
            childMappings.put(fvd.getPocketId(), fvd.getChildFragmentVersion().getUri());
        }
        return childMappings;
    }


    private void deleteProcessModel(final ProcessModelVersion pvid) throws ExceptionDao {
        try {
            Integer rootFragmentVersion = processModelVersionRepo.getRootFragmentVersionId(pvid.getId());
            processModelVersionRepo.delete(pvid);
            deleteFragmentVersion(rootFragmentVersion);
        } catch (Exception e) {
            String msg = "Failed to delete the process model version " + pvid;
            LOGGER.error(msg, e);
            throw new ExceptionDao(msg, e);
        }
    }

    private void deleteFragmentVersion(final Integer fvid) throws ExceptionDao {
        List<FragmentVersionDag> childFragments = fragmentVersionDagRepo.getChildMappings(fvid);
        Integer contentId = fragmentVersionRepo.findOne(fvid).getContent().getId();
        fragmentSrv.deleteChildRelationships(fvid);
        contentRepo.delete(contentId);

        for (FragmentVersionDag childFV : childFragments) {
            // TODO: Change this to not use the Integer Id
            deleteFragmentVersion(childFV.getChildFragmentVersion().getId());
        }
    }


    /* Finds the Resource using the resource Id supplied. */
    private Resource findResource(Set<Resource> resources, String resourceId) {
        Resource found = null;
        for (Resource resource : resources) {
            if (resource.getUri().equals(resourceId)) {
                found = resource;
                break;
            }
        }
        if (found == null) {
            LOGGER.warn("Could not find Resource with Id: " + resourceId);
        }
        return found;
    }


    /* Create a Net used for the Creation of the Process Model. */
    private CanonicalProcessType createNet(CanonisedProcess cpf, NetType net) {
        CanonicalProcessType cpt = new CanonicalProcessType();

        cpt.setUri(net.getId());
        cpt.setName(cpf.getCpt().getName());
        cpt.setAuthor(cpf.getCpt().getAuthor());
        cpt.setDataTypes(cpf.getCpt().getDataTypes());
        cpt.setCreationDate(cpf.getCpt().getCreationDate());
        cpt.setModificationDate(cpf.getCpt().getModificationDate());

        cpt.getNet().add(net);
        cpt.getRootIds().addAll(cpf.getCpt().getRootIds());
        cpt.getAttribute().addAll(cpf.getCpt().getAttribute());
        cpt.getResourceType().addAll(cpf.getCpt().getResourceType());

        return cpt;
    }

    /* Updates the Node Sub processes if they have any. */
    private void createSubProcessReferences(CanonisedProcess cpf, Map<String, ProcessModelVersion> models) {
        for (NetType net : cpf.getCpt().getNet()) {
            for (NodeType nodeType : net.getNode()) {
                if ((nodeType instanceof TaskType) && ((TaskType) nodeType).getSubnetId() != null) {
                    Node node = findNode(nodeType.getId(), models);
                    node.setSubProcess(models.get(((TaskType) nodeType).getSubnetId()));
                }
            }
        }
    }

    /* Create the Net objects and link them to the correct Process and ProcessModelVersion. */
    private void addNetRecords(ProcessModelVersion pmv, Process process, List<String> rootIds, String netId) {
        if (rootIds != null && netId != null) {
            for (String rootId : rootIds) {
                if (rootId.equals(netId)) {
                    Net net = new Net();
                    net.setNetId(netId);
                    net.setProcess(process);
                    net.setProcessModelVersion(pmv);

                    pmv.setNet(net);
                    process.getNets().add(net);
                }
            }
        }
    }

    private void createRootProcessReferences(Process process, CanonisedProcess cpf, Map<String, ProcessModelVersion> models) {
        ProcessModelVersion pmv;
        for (NetType netType : cpf.getCpt().getNet()) {
            if (cpf.getCpt().getRootIds() != null) {
                for (String rootId : cpf.getCpt().getRootIds()) {
                    if (rootId.equals(netType.getId())) {
                        pmv = models.get(netType.getId());

                        if (pmv != null) {
                            Net net = new Net();
                            net.setNetId(netType.getId());
                            net.setProcess(process);
                            net.setProcessModelVersion(pmv);

                            pmv.setNet(net);
                            process.getNets().add(net);

                            processModelVersionRepo.save(pmv);
                        }
                    }
                }
            }
        }
    }

    /* Finds a node in the maybe multiple process model versions by it's URI. */
    private Node findNode(final String nodeUri, final Map<String, ProcessModelVersion> models) {
        Node found = null;
        for (ProcessModelVersion pmv : models.values()) {
            for (FragmentVersion fv : pmv.getFragmentVersions()) {
                for (Node node : fv.getContent().getNodes()) {
                    if (node.getUri().equals(nodeUri)) {
                        found = node;
                        break;
                    }
                }
            }
        }
        return found;
    }


}
