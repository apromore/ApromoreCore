package org.apromore.service.impl;

import org.apache.commons.io.IOUtils;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.helper.Version;
import org.apromore.model.Detail;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.*;
import org.apromore.service.helper.UserInterfaceHelper;
import org.jbpt.petri.Flow;
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Marking;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;
import org.pql.api.IPQLAPI;
import org.pql.api.PQLQueryResult;
import org.pql.core.PQLTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by corno on 22/07/2014.
 */
@Service
public class PQLServiceImpl implements PQLService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PQLServiceImpl.class);

    private final String admin = "admin";
    private String PNMLCanoniser;
    @Inject
    private WorkspaceService workspaceService;
    // @Inject  kludge so that the constructor for ProcessServiceImpl can explicitly inject it; workaround for circular dependency
    private ProcessService processService;
    @Inject
    private PluginService pluginService;
    @Inject
    private CanoniserService canoniserService;
    @Inject
    private UserService userService;
    @Inject
    private UserInterfaceHelper helperService;
    @Inject
    @Qualifier("canoniserProvider")
    private CanoniserProvider canoniserProviderService;
    @Inject
    private ProcessModelVersionRepository processModelVersionRepository;

    private String parameterCategory = Canoniser.DECANONISE_PARAMETER;

    private final Set<Double> indexedLabelSimilarities = new HashSet<Double>();

    private LolaDirBean lolaDir;
    private MySqlBeanImpl mySqlBean;
    private PGBeanImpl pgBean;

    private Map<PQLTask,PQLTask> map = new HashMap<>();
    private PqlBeanImpl pqlBean;
    private int numberOfCore = Runtime.getRuntime().availableProcessors();
    private Semaphore sem= new Semaphore(numberOfCore-1);

    @Inject
    public PQLServiceImpl(LolaDirImpl lolaDir, MySqlBeanImpl mySqlBean, PGBeanImpl pgBean, PqlBeanImpl pqlBean) {
        this.lolaDir = lolaDir;
        this.mySqlBean=mySqlBean;
        this.pgBean=pgBean;
        this.pqlBean=pqlBean;
        indexedLabelSimilarities.add(new Double(0.5));
        indexedLabelSimilarities.add(new Double(0.75));
        indexedLabelSimilarities.add(new Double(1.0));
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public void indexAllModels() {
        LinkedList<FolderTreeNode> root = null;
        LinkedList<GroupProcess> processes = new LinkedList<>();

        try {
            String userID = userService.findUserByLogin(admin).getRowGuid();
            root = new LinkedList<>(workspaceService.getWorkspaceFolderTree(userID));

            FolderTreeNode head;
            Integer folderId = 0;

            processes.addAll(workspaceService.getGroupProcesses(userID, folderId));
            indexModels(processes, folderId, userID);
            processes.clear();

            while (!root.isEmpty()) {
                head = root.removeFirst();
                folderId = head.getId();
                processes.addAll(workspaceService.getGroupProcesses(userID, folderId));
                indexModels(processes, folderId, userID);
                root.addAll(head.getSubFolders());
                processes.clear();
            }

        } catch (Exception e) {
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE1: " + e.getMessage() + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
    }

    @Override
    public void indexOneModel(ProcessModelVersion pmv) {

        Process process=pmv.getProcessBranch().getProcess();
        Version version2=new Version(pmv.getVersionNumber());
        Set<RequestParameterType<?>> canoniserProperties=readPluginProperties(parameterCategory);

        try {
            LOGGER.info("indexOneModel name=" + process.getName() + " id=" + process.getId() + " branch=" + pmv.getProcessBranch().getBranchName() + " version=" + version2 + " canoniser=" + getPNMLCanoniser() + " canoniserProperties=" + canoniserProperties);
            ExportFormatResultType exportResult = this.processService.exportProcess(process.getName(), process.getId(), pmv.getProcessBranch().getBranchName(), version2, getPNMLCanoniser(), null, false, canoniserProperties);
            storeModel(exportResult, process.getId(), version2, pmv.getProcessBranch().getBranchName());
        } catch(Exception e){
            LOGGER.error("Unable to index model " + process + " " + version2, e);
        }
    }

    @Override
    public void deleteModel(ProcessModelVersion pmv) {
        Version version=new Version(pmv.getVersionNumber());
        Process process = pmv.getProcessBranch().getProcess();
        try {
            PqlBean pqlBean= new PqlBeanImpl((LolaDirImpl)lolaDir,mySqlBean,pgBean,this.pqlBean.isIndexingEnabled(), this.pqlBean.getLabelSimilaritySearch());
            IPQLAPI api=pqlBean.getApi();
            LOGGER.info("-----------DELETE: " + pmv.getProcessBranch().getProcess().getId()+"/"+version.toString()+"/"+pmv.getProcessBranch().getBranchName());

            String externalId = pmv.getProcessBranch().getProcess().getId() + "/" + version.toString() + "/" + pmv.getProcessBranch().getBranchName();
            int internalId = api.getInternalID(externalId);
            if (internalId == 0) {
                throw new Exception("No model with PQL external id " + externalId + " found in PQL database");
            }
            if (!api.deleteIndex(internalId)) {
                throw new Exception("Failed to delete model with PQL external id " + externalId + " and internal id " + internalId);
            }
            LOGGER.info("Deleted model with PQL external id " + externalId + " and internal id " + internalId);

            /*
            try {
                LOGGER.info("Performing post-deletion cleanup");
                api.cleanupIndex();
                LOGGER.info("Performed post-deletion cleanup");
            } catch (SQLException e) {
                LOGGER.error("Unable to perform post-deletion cleanup", e);
            }
            */
        } catch(Exception e) {
            LOGGER.error("Unable to delete model " + process.getId() + " " + version, e);
        }

    }

    @Override
    public void notifyUpdate(final ProcessModelVersion pmv) {
        if (pqlBean.isIndexingEnabled()) {
            indexOneModel(pmv);
        }
    }

    @Override
    public void notifyDelete(final ProcessModelVersion pmv) {
        if (pqlBean.isIndexingEnabled()) {
            deleteModel(pmv);
        }
    }

    private String getPNMLCanoniser() {
        if (PNMLCanoniser != null) {
            LOGGER.info("RECALLED " + PNMLCanoniser);
            return PNMLCanoniser;
        }

        for(Canoniser canoniser : canoniserProviderService.listAll()){
            if(canoniser.getNativeType().startsWith("PNML")) {
                PNMLCanoniser=canoniser.getNativeType();
                LOGGER.info("INITIALIZED " + PNMLCanoniser);
                return PNMLCanoniser;
            }
        }

        throw new RuntimeException("Unable to find a canoniser for PNML");
    }

    private Set<RequestParameterType<?>> readPluginProperties(String parameterCategory) {
        Set<RequestParameterType<?>> requestProperties = new HashSet<>();
        requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTransition",true));
        requestProperties.add(new RequestParameterType<>("isCpfTaskPnmlTrans",false));
        try {
        } catch (Exception e) {
            LOGGER.error("-----------ERRORRE PluginProperties: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE4: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }

        return requestProperties;
    }

    private void indexModels(LinkedList<GroupProcess> processes, Integer folderId, String userID) {

        try {
            for (GroupProcess process : processes) {
                Process currentProc = process.getProcess();
                String procName = currentProc.getName();
                Integer procId = currentProc.getId();
                Version version = null;
                String nativeType = currentProc.getNativeType().getNatType();
                String annotationName = null;
                boolean withAnnotation = false;
                Set<RequestParameterType<?>> canoniserProperties = readPluginProperties(parameterCategory);

                for (ProcessSummaryType pst : helperService.buildProcessSummaryList(userID, folderId, null).getProcessSummary()) {
                    if (pst.getName().equals(procName)) {
                    for (VersionSummaryType vst : pst.getVersionSummaries()) {

                            version = new Version(vst.getVersionNumber());
                            if (version != null && canoniserProperties != null) {
                                LOGGER.info("PROCESS: " + procName + " " + procId + " " + vst.getName() + " " + version.toString() + " " + nativeType);
                                ExportFormatResultType exportResult = this.processService.exportProcess(procName, procId, vst.getName(), version, getPNMLCanoniser(), annotationName, withAnnotation, canoniserProperties);
				storeModel(exportResult, procId, version, vst.getName());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to index models in folder " + folderId, e);
        }
    }

    /**
     * Store the text of a PNML model into the PQL database.
     *
     * This is a relatively quick, synchronous operation.  The model is not indexed immediately, but
     * becomes available for eventual (asynchronous) indexing by the PQL "bot" process.
     *
     * @param exportResult  a PNML model obtained from the {@link ProcessService#exportProcess} method
     * @param procId
     * @param version
     * @param branchName
     * @throws IOException  if the native PNML serialization couldn't be obtained from the <var>exportResult</var>
     * @throws SQLException  if the model couldn't be store into the PQL database
     */
    private void storeModel(ExportFormatResultType exportResult, Integer procId, Version version, String branchName) throws IOException, SQLException {

        byte[] bytes = IOUtils.toByteArray(exportResult.getNative().getInputStream());

        PNMLSerializer pnmlSerializer = new PNMLSerializer();

        // External ids are strings, e.g. "17/1.0/MAIN"
        String externalId = procId.toString() + "/" + version.toString() + "/" + branchName;

        IPQLAPI api = pqlBean.getApi();
        int internalId = api.storeNetSystem(bytes, externalId);

        LOGGER.info("SOUNDNESS: " + api.checkNetSystem(internalId));
    }

    @Override
    public List<String> runAPQLQuery(String queryPQL, List<String> IDs, String userID) {
        Set<String> idNets=new HashSet<>();
        List<String> results = Collections.emptyList();
        IPQLAPI api=pqlBean.getApi();
        LOGGER.error("-----------PQLAPI: " + api);
        LOGGER.error("----------- query: " + queryPQL);
        LOGGER.error("-----------   IDs: " + IDs);
        LOGGER.error("-----------  user: " + userID);
        try {
            PQLQueryResult pqlQueryResult = api.query(queryPQL /*, new HashSet<>(IDs) */);
            if (pqlQueryResult.getNumberOfParseErrors() != 0) {
                results = pqlQueryResult.getParseErrorMessages();
            } else {//risultati
                LOGGER.error("-----------IDS PQLServiceImpl" + IDs);
/*
                map=pqlQueryResult.getTaskMap();
                LinkedList<PQLTask> tasks=new LinkedList<>(map.values());
                
                idNets=new HashSet<>(IDs);
                idNets=api.checkLastQuery(idNets);
                results.addAll(idNets);
*/
                results = new LinkedList<>(pqlQueryResult.getSearchResults());
                LOGGER.error("-----------QUERYAPQL ESATTA "+results);
            }
        } catch (Exception e) {
            LOGGER.error("-----------ERRORRE: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE6: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
        return results;
    }

    @Override
    public List<Detail> getDetails(){
        List<Detail> details = new LinkedList<>();
        Detail detail;
        for(PQLTask task : map.keySet()){
            PQLTask taskTwo = map.get(task);

            detail= new Detail();
            detail.setLabelOne(task.getLabel());
            detail.setSimilarityLabelOne(""+task.getSimilarity());
            if (taskTwo.getSimilarLabels() != null) {
                detail.getDetail().addAll(taskTwo.getSimilarLabels());
            }
            details.add(detail);
        }
        return details;
    }

}
