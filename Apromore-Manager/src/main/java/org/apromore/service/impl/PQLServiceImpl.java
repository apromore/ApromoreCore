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
import org.jbpt.petri.INetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.pql.api.IPQLAPI;
import org.pql.core.PQLTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.inject.Inject;
import java.io.InputStream;
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
    @Inject
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
        PqlBean pqlBean= new PqlBeanImpl((LolaDirImpl)lolaDir,mySqlBean,pgBean,this.pqlBean.isIndexingEnabled(),this.pqlBean.getLabelSimilaritySearch());
        IPQLAPI api=pqlBean.getApi();

        try {
//            LOGGER.error("PQLSERVICE: Dati proc "+processname+" "+processId+" "+branch+" "+version+" "+PNMLCanoniser+" nulla false"+canoniserProperties);
            LOGGER.info("indexOneModel name=" + process.getName() + " id=" + process.getId() + " branch=" + pmv.getProcessBranch().getBranchName() + " version=" + version2 + " canoniser=" + getPNMLCanoniser() + " canoniserProperties=" + canoniserProperties);
            ExportFormatResultType exportResult = this.processService.exportProcess(process.getName(), process.getId(), pmv.getProcessBranch().getBranchName(), version2, getPNMLCanoniser(), null, false, canoniserProperties);

            InputStream input = exportResult.getNative().getInputStream();
//            InputStream input2= exportResult.getNative().getInputStream();
//            Scanner sc=new Scanner(input2);
//            PrintWriter pw=new PrintWriter(new FileWriter("C:/Users/corno/net/"+pmv.getProcessBranch().getProcess().getName()+".txt"));
//            while(sc.hasNextLine()){
//                pw.println(sc.nextLine());
//            }
//            pw.close();
            byte[] bytes = IOUtils.toByteArray(input);

            INetSystem netSystem = api.bytes2NetSystem(bytes);

            int pi,ti;
            pi = ti = 1;
            for (Place p : (Set<Place>)netSystem.getPlaces())
                p.setName("p"+pi++);
            for (Transition t : (Set<Transition>)netSystem.getTransitions()) {
                t.setName("t" + ti++);
                t.setLabel(t.getLabel().replaceAll("[\n]"," "));
            }

            netSystem.loadNaturalMarking();


            if (api.checkNetSystem(netSystem)) {
//                LOGGER.info("SOUNDNESS: " + true);
                api.indexNetSystem(netSystem, process.getId() + "/" + version2.toString() + "/" + pmv.getProcessBranch().getBranchName(), indexedLabelSimilarities);
//                LOGGER.error("--------------------------------------------------------INDEXED: "+process.getName());
            }
//            else{
//                LOGGER.info("SOUNDNESS: " + false);
//            }
        }catch(Exception e){
            LOGGER.error("-----------ERRORRE: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE2: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
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

            api.deleteNetSystem(pmv.getProcessBranch().getProcess().getId() + "/" + version.toString() + "/" + pmv.getProcessBranch().getBranchName());
        }catch(Exception e){
            LOGGER.error("-----------ERRORRE: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE3: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
    }

    @Override
    public void update(User user, NativeType nativeType,final ProcessModelVersion pmv, final boolean delete) {

            // Don't do anything if PQL indexing has been disabled
            if (!pqlBean.isIndexingEnabled()) {
                return;
            }

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    String name = pmv.getProcessBranch().getProcess().getName();
		    LOGGER.info("Updating thread started for " + name);
                    try {
                        PQLServiceImpl.this.sem.acquire();
                        try {
                            if (delete) {
                                LOGGER.info("Deleting " + name);
                                deleteModel(pmv);
                                LOGGER.info("Deleted " + name);
                            } else {
                                LOGGER.info("Indexing " + name);
                                indexOneModel(pmv);
                                LOGGER.info("Indexed " + name);
                            }

		          LOGGER.info("Updating thread completed for " + name);
                        } catch (Throwable e) {
                            LOGGER.error("Updating thread failed for " + name, e);
                        } finally {
                            PQLServiceImpl.this.sem.release();
                        }
                    } catch(InterruptedException ie) {
		        LOGGER.error("Updating thread interrupted for " + pmv.getProcessBranch().getProcess().getName(), ie);
                    }
		    LOGGER.info("Updating thread terminated for " + pmv.getProcessBranch().getProcess().getName());
                }
            };
            //Thread thread = new Thread(run);
            //thread.start();
            run.run();
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
        Process currentProc;
        String procName;
        Integer procId;
        Version version = null;
        String nativeType;
        String annotationName = null;
        boolean withAnnotation = false;

        IPQLAPI api=pqlBean.getApi();
//        LOGGER.error("-----------PQLAPI: " + api);

        Set<RequestParameterType<?>> canoniserProperties = null;
        try {
            for (GroupProcess process : processes) {
                currentProc = process.getProcess();
                procName = currentProc.getName();
                procId = currentProc.getId();
                nativeType = currentProc.getNativeType().getNatType();
                canoniserProperties = readPluginProperties(parameterCategory);

                for (ProcessSummaryType pst : helperService.buildProcessSummaryList(userID, folderId, null).getProcessSummary()) {
                    if (pst.getName().equals(procName)) {
                    for (VersionSummaryType vst : pst.getVersionSummaries()) {

                            version = new Version(vst.getVersionNumber());
                            if (version != null && canoniserProperties != null) {
                                LOGGER.info("PROCESS: " + procName + " " + procId + " " + vst.getName() + " " + version.toString() + " " + nativeType);
                                ExportFormatResultType exportResult = this.processService.exportProcess(procName, procId, vst.getName(), version, getPNMLCanoniser(), annotationName, withAnnotation, canoniserProperties);
                                DataHandler data = exportResult.getNative();

                                InputStream input = data.getInputStream();
                                byte[] bytes = IOUtils.toByteArray(input);

                                INetSystem netSystem = api.bytes2NetSystem(bytes);

                                int pi,ti;
                                pi = ti = 1;
                                for (Place p : (Set<Place>)netSystem.getPlaces())
                                    p.setName("p"+pi++);
                                for (Transition t : (Set<Transition>)netSystem.getTransitions())
                                    t.setName("t"+ti++);

                                netSystem.loadNaturalMarking();

//                                LOGGER.info("SOUNDNESS: " + api.checkNetSystem(netSystem));
                                if (api.checkNetSystem(netSystem)) {
//                                    LOGGER.error("INDEX: " + );
                                    api.indexNetSystem(netSystem, procId.toString() + "/" + vst.getVersionNumber() + "/" + vst.getName(), indexedLabelSimilarities);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("-----------ERRORRE: " + e.toString());
            for (StackTraceElement ste : e.getStackTrace())
                LOGGER.info("ERRORE5: " + ste.getClassName() + " " + ste.getMethodName() + " " + ste.getLineNumber() + " " + ste.getFileName());
        }
    }

    @Override
    public List<String> runAPQLQuery(String queryPQL, List<String> IDs, String userID) {
        Set<String> idNets=new HashSet<>();
        List<String> results=new LinkedList<>();
        IPQLAPI api=pqlBean.getApi();
        LOGGER.error("-----------PQLAPI: " + api);
        try {
            api.prepareQuery(queryPQL);
            if (api.getLastNumberOfParseErrors() != 0) {
                results = api.getLastParseErrorMessages();
            } else {//risultati
                LOGGER.error("-----------IDS PQLServiceImpl" + IDs);
                map=api.getLastQuery().getTaskMap();
                LinkedList<PQLTask> tasks=new LinkedList<>(map.values());

                idNets=new HashSet<>(IDs);
                idNets=api.checkLastQuery(idNets);
                results.addAll(idNets);
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
