package org.apromore.portal.dialogController;

import ee.ut.eventstr.test.AlphaBasedPosetReaderTest;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.bpmnminer.CandidatesEntitiesController;
import org.apromore.portal.dialogController.bpmnminer.PrimaryKeyController;
import org.apromore.portal.exception.ExceptionDomains;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.processmining.models.graphbased.directed.conceptualmodels.ConceptualModel;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.Data;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.DiscoverERmodel;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.DiscoverERmodel.PrimaryKeyData;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.DiscoverERmodel.ForeignKeyData;
import org.processmining.plugins.bpmn.miner.preprocessing.functionaldependencies.NoEntityException;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Created by conforti on 10/04/15.
 */
public class BPMNMinerController extends BaseController {

    private MainController mainC;
    private Window bpmnMinerW;
    private Button uploadLog;
    private Button cancelButton;
    private Button okButton;

    private SelectDynamicListController domainCB;

    private String nativeType = "BPMN 2.0";

    private String[] arrayMiningAlgorithms = new String[] {
            "Heusistics Miner ProM 5.2 without unused relationships",
            "Inductive Miner",
            "Heusistics Miner ProM 6",
            "Fodina Miner",
            "Alpha Algorithm",
            "ILP Miner",
            "Heusistics Miner ProM 5.2 with unused relationships"
    };
    private String[] arrayDependencyAlgorithms = new String[] {
            "Normal",
            "Noise Tolerant"
    };

    private Textbox modelName;
    private Selectbox miningAlgorithms;
    private Radiogroup dependencyAlgorithms;
    private Radiogroup sortLog;
    private Slider interruptingEventTolerance;
    private Slider multiInstancePercentage;
    private Slider multiInstanceTolerance;
    private Slider timerEventPercentage;
    private Slider timerEventTolerance;
    private Slider noiseThreshold;

    private XLog log;
    private DiscoverERmodel erModel;
    private List<String> listCandidates;
    private boolean[] selected;
    private Label l;
    private HashMap<String, Data> data;

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    String logFileName = null;

    public BPMNMinerController(MainController mainC) {
        try {
            this.mainC = mainC;

            List<String> domains = this.mainC.getDomains();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            this.bpmnMinerW = (Window) Executions.createComponents("macros/bpmnMiner.zul", null, null);

            this.l = (Label) this.bpmnMinerW.getFellow("fileName");
            this.modelName = (Textbox) this.bpmnMinerW.getFellow("bpmnMinerModelName");
            this.miningAlgorithms = (Selectbox) this.bpmnMinerW.getFellow("bpmnMinerMiningAlgorithm");
            this.miningAlgorithms.setModel(new ListModelArray<Object>(arrayMiningAlgorithms));

            this.dependencyAlgorithms = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerDependencyAlgorithm");
            this.sortLog = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerSort");

            this.interruptingEventTolerance = (Slider) this.bpmnMinerW.getFellow("bpmnMinerInterruptingEventTolerance");
            this.multiInstancePercentage = (Slider) this.bpmnMinerW.getFellow("bpmnMinerMultiInstancePercentage");
            this.multiInstanceTolerance = (Slider) this.bpmnMinerW.getFellow("bpmnMinerMultiInstanceTolerance");
            this.timerEventPercentage = (Slider) this.bpmnMinerW.getFellow("bpmnMinerTimerEventPercentage");
            this.timerEventTolerance = (Slider) this.bpmnMinerW.getFellow("bpmnMinerTimerEventTolerance");
            this.noiseThreshold = (Slider) this.bpmnMinerW.getFellow("bpmnMinerNoiseThreshold");

            this.uploadLog = (Button) this.bpmnMinerW.getFellow("bpmnMinerUpload");
            this.cancelButton = (Button) this.bpmnMinerW.getFellow("bpmnMinerCancelButton");
            this.okButton = (Button) this.bpmnMinerW.getFellow("bpmnMinerOKButton");

            this.uploadLog.addEventListener("onUpload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    uploadFile((UploadEvent) event);
                }
            });

            this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    createCanditatesEntity();
                }
            });
            this.bpmnMinerW.doModal();
        }catch (ExceptionDomains e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Upload file: an archive or an xml file
     * @param event the event to process.
     * @throws InterruptedException
     */
    private void uploadFile(UploadEvent event) {
            logFile = event.getMedia();
            l.setStyle("color: blue");
            l.setValue(logFile.getName());
            logByteArray = logFile.getByteData();
            logFileName = logFile.getName();
    }

    public static XLog importFromStream(XFactory factory, InputStream is, String name) throws Exception {
        XParser parser = null;
        if(name.endsWith("mxml")) {
            parser = new XMxmlParser(factory);
        }else if(name.endsWith("mxml.gz")) {
            parser = new XMxmlGZIPParser(factory);
        }else if(name.endsWith("xes")) {
            parser = new XesXmlParser(factory);
        }else if(name.endsWith("xes.gz")) {
            parser = new XesXmlGZIPParser(factory);
        }

        Collection<XLog> logs;
        try {
            logs = parser.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
            logs = null;
        }
        if (logs == null) {
            // try any other parser
            for (XParser p : XParserRegistry.instance().getAvailable()) {
                if (p == parser) {
                    continue;
                }
                try {
                    logs = p.parse(is);
                    if (logs.size() > 0) {
                        break;
                    }
                } catch (Exception e1) {
                    // ignore and move on.
                    logs = null;
                }
            }
        }

        // log sanity checks;
        // notify user if the log is awkward / does miss crucial information
        if (logs == null || logs.size() == 0) {
            throw new Exception("No processes contained in log!");
        }

        XLog log = logs.iterator().next();
        if (XConceptExtension.instance().extractName(log) == null) {
            XConceptExtension.instance().assignName(log, "Anonymous log imported from ");
        }

        if (log.isEmpty()) {
            throw new Exception("No process instances contained in log!");
        }

        return log;

    }

    protected void cancel() {
        this.bpmnMinerW.detach();
    }

    protected void createCanditatesEntity() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(logByteArray, 0, logByteArray.length);
            InputStream zipEntryIS = new ByteArrayInputStream(bos.toByteArray());
            log = importFromStream(new XFactoryNaiveImpl(), zipEntryIS, logFileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(log == null) {
            Messagebox.show("Please select a log.");
        }else if(miningAlgorithms.getSelectedIndex() < 0) {
            Messagebox.show("Please select mining algorithm.");
        }else {
            erModel = new DiscoverERmodel();
            listCandidates = erModel.generateAllAttributes(log);

            new CandidatesEntitiesController(this, listCandidates);
        }
    }

    public void setSelectedCandidatesEntities(List<String> listCandidates, boolean[] selected) throws NoEntityException {
        this.listCandidates = listCandidates;
        this.selected = selected;

        this.data = erModel.generateData(log, getIgnoreAttributes());
        List<PrimaryKeyData> pKeyData = PrimaryKeyData.getData(data);

        new PrimaryKeyController(this, pKeyData);
    }

    public List<String> getIgnoreAttributes() {
        List<String> ignored = new ArrayList<String>();
        for (int i = 0; i < selected.length; i++) {
            if (!selected[i]) ignored.add(listCandidates.get(i));
        }
        return ignored;
    }

    public void setSelectedPrimaryKeys(Map<Set<String>, Set<String>> group) {
        try {

            this.bpmnMinerW.detach();
            String model = getService().discoverBPMNModel(log, sortLog.getSelectedIndex()==0?true:false, miningAlgorithms.getSelectedIndex(), dependencyAlgorithms.getSelectedIndex()+1,
                    ((double) interruptingEventTolerance.getCurpos())/100.0, ((double) timerEventPercentage.getCurpos())/100.0, ((double) timerEventTolerance.getCurpos())/100.0,
                    ((double) multiInstancePercentage.getCurpos())/100.0, ((double) multiInstanceTolerance.getCurpos())/100.0, ((double) noiseThreshold.getCurpos())/100.0,
                    listCandidates, group);

            ImportListProcessesController importListProcessesController = new ImportListProcessesController(mainC);
            importListProcessesController.cancelAll();

            String defaultProcessName = this.logFileName.split("\\.")[0];
            if(!modelName.getValue().isEmpty()) {
                defaultProcessName = modelName.getValue();
            }

            InputStream inputStream = new ByteArrayInputStream(model.getBytes());

            ImportOneProcessController oneImport = new ImportOneProcessController(mainC, importListProcessesController, inputStream, defaultProcessName, this.nativeType, this.logFileName);
            oneImport.importProcess(domainCB.getValue(), UserSessionManager.getCurrentUser().getUsername());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void noEntityException() {
        try {
            this.bpmnMinerW.detach();
            String model = getService().discoverBPMNModel(log, sortLog.getSelectedIndex()==0?true:false, miningAlgorithms.getSelectedIndex(), dependencyAlgorithms.getSelectedIndex()+1,
                    ((double) interruptingEventTolerance.getCurpos())/100.0, ((double) timerEventPercentage.getCurpos())/100.0, ((double) timerEventTolerance.getCurpos())/100.0,
                    ((double) multiInstancePercentage.getCurpos())/100.0, ((double) multiInstanceTolerance.getCurpos())/100.0, ((double) noiseThreshold.getCurpos())/100.0,
                    new ArrayList<String>(), new HashMap<Set<String>, Set<String>>());

            ImportListProcessesController importListProcessesController = new ImportListProcessesController(mainC);
            importListProcessesController.cancelAll();

            String defaultProcessName = this.logFileName.split("\\.")[0];
            if(!modelName.getValue().isEmpty()) {
                defaultProcessName = modelName.getValue();
            }

            InputStream inputStream = new ByteArrayInputStream(model.getBytes());

            ImportOneProcessController oneImport = new ImportOneProcessController(mainC, importListProcessesController, inputStream, defaultProcessName, this.nativeType, this.logFileName);
            oneImport.importProcess(domainCB.getValue(), UserSessionManager.getCurrentUser().getUsername());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
