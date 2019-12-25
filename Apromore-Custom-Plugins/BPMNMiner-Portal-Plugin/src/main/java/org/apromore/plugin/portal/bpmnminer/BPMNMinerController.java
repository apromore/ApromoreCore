/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.bpmnminer;

import com.raffaeleconforti.bpmnminer.preprocessing.functionaldependencies.DiscoverERmodel;
import com.raffaeleconforti.bpmnminer.preprocessing.functionaldependencies.DiscoverERmodel.PrimaryKeyData;
import com.raffaeleconforti.bpmnminer.subprocessminer.selection.SelectMinerResult;
import com.raffaeleconforti.foreignkeydiscovery.functionaldependencies.Data;
import com.raffaeleconforti.foreignkeydiscovery.functionaldependencies.NoEntityException;
import com.raffaeleconforti.wrappers.settings.MiningSettings;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.helper.Version;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.bimp_annotation.BIMPAnnotationService;
import org.apromore.service.bpmnminer.BPMNMinerService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.logfilter.behaviour.InfrequentBehaviourFilterService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


/**
 * Created by conforti on 10/04/15.
 */
public class BPMNMinerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNMinerController.class.getCanonicalName());

    PortalContext portalContext;
    private Window bpmnMinerW;
    private Button uploadLog;
    private Button cancelButton;
    private Button okButton;

    private SelectDynamicListController domainCB;

    private String nativeType = "BPMN 2.0";

    private String[] arrayMiningAlgorithms = new String[] {
            SelectMinerResult.SM,
            SelectMinerResult.IM,
            SelectMinerResult.HM6,
            SelectMinerResult.HM5,
            SelectMinerResult.ALPHA,
            SelectMinerResult.ILP
    };
    private String[] arrayDependencyAlgorithms = new String[] {
            "Normal",
            "Noise Tolerant"
    };

    private Textbox modelName;
    private Selectbox miningAlgorithms;
    private Radiogroup flatModel;
    private Radiogroup dependencyAlgorithms;
    private Radiogroup filterLog;
    private Radiogroup sortLog;
    private Radiogroup structProcess;
    private Radiogroup bimpAnnotated;
    private Slider interruptingEventTolerance;
    private Slider multiInstancePercentage;
    private Slider multiInstanceTolerance;
    private Slider timerEventPercentage;
    private Slider timerEventTolerance;
    private Slider noiseThreshold;

    private MiningSettings params;

    private XLog log;
    private DiscoverERmodel erModel;
    private List<String> listCandidates;
    private boolean[] selected;
    private Label l;
    private Map<String, Data> data;

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    String logFileName = null;

    private final BIMPAnnotationService bimpAnnotationService;
    private final BPMNMinerService bpmnMinerService;
    private final CanoniserService canoniserService;
    private final DomainService domainService;
    private final ProcessService processService;
    private final EventLogService eventLogService;
    private final InfrequentBehaviourFilterService infrequentBehaviourFilterService;
    private final UserInterfaceHelper userInterfaceHelper;
    
    private static final String EVENT_QUEUE = BPMNMinerController.class.getCanonicalName(); //"EVENT_QUEUE";  
    private static final String CHANGE_DESCRIPTION = "CHANGE_DESCRIPTION";
    private static final String CHANGE_FRACTION_COMPLETE = "CHANGE_FRACTION_COMPLETE";
    private static final String MINING_COMPLETE = "MINING_COMPLETE";
    private static final String MINING_EXCEPTION = "MINING_EXCEPTION";
    private static final String ANNOTATION_EXCEPTION = "ANNOTATION_EXCEPTION";
    private EventQueue<Event> eventQueue = EventQueues.lookup(EVENT_QUEUE, EventQueues.SESSION, true);
    private EventListener<Event> miningEventListener = null;

    public BPMNMinerController(PortalContext portalContext,
                               BIMPAnnotationService bimpAnnotationService,
                               BPMNMinerService bpmnMinerService,
                               CanoniserService canoniserService,
                               DomainService domainService,
                               ProcessService processService,
                               EventLogService eventLogService,
                               InfrequentBehaviourFilterService infrequentBehaviourFilterService,
                               UserInterfaceHelper userInterfaceHelper) {

        this.portalContext       = portalContext;
        this.bimpAnnotationService = bimpAnnotationService;
        this.bpmnMinerService    = bpmnMinerService;
        this.canoniserService    = canoniserService;
        this.domainService       = domainService;
        this.processService      = processService;
        this.eventLogService     = eventLogService;
        this.userInterfaceHelper = userInterfaceHelper;
        this.infrequentBehaviourFilterService = infrequentBehaviourFilterService;

        try {
            List<String> domains = domainService.findAllDomains();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
            Set<LogSummaryType> selectedLogSummaryType = new HashSet<>();
            for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
                if(entry.getKey() instanceof LogSummaryType) {
                    selectedLogSummaryType.add((LogSummaryType) entry.getKey());
                }
            }

            // At least 2 process versions must be selected. Not necessarily of different processes
            if (selectedLogSummaryType.size() == 0 || selectedLogSummaryType.size() > 1) {
//                this.bpmnMinerW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/bpmnMinerInput.zul", null, null);
//                this.l = (Label) this.bpmnMinerW.getFellow("fileName");
//                this.uploadLog = (Button) this.bpmnMinerW.getFellow("bpmnMinerUpload");
//                this.uploadLog.addEventListener("onUpload", new EventListener<Event>() {
//                    public void onEvent(Event event) throws Exception {
//                        uploadFile((UploadEvent) event);
//                    }
//                });
                Messagebox.show("Select one log for process discovery.", "Apromore", Messagebox.OK, Messagebox.NONE);
                return;
            }else {
                log = eventLogService.getXLog(selectedLogSummaryType.iterator().next().getId());
                this.bpmnMinerW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/bpmnMiner.zul", null, null);
            }

            this.modelName = (Textbox) this.bpmnMinerW.getFellow("bpmnMinerModelName");
            this.modelName.setText(selectedLogSummaryType.iterator().next().getName()+"_model");
            this.miningAlgorithms = (Selectbox) this.bpmnMinerW.getFellow("bpmnMinerMiningAlgorithm");
            ListModelArray listModelArray = new ListModelArray<Object>(arrayMiningAlgorithms);
            listModelArray.addToSelection(arrayMiningAlgorithms[0]);
            this.miningAlgorithms.setModel(listModelArray);

            this.flatModel = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerFlat");
//            this.flatModel = new Radiogroup();
//            flatModel.appendChild(this.bpmnMinerW.getFellow("flat"));
//            flatModel.appendChild(this.bpmnMinerW.getFellow("hierarchical"));

            this.dependencyAlgorithms = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerDependencyAlgorithm");
//            this.dependencyAlgorithms = new Radiogroup();
//            dependencyAlgorithms.appendChild(this.bpmnMinerW.getFellow("normal"));
//            dependencyAlgorithms.appendChild(this.bpmnMinerW.getFellow("noiseTolerant"));

            this.filterLog = (Radiogroup) this.bpmnMinerW.getFellow("noiseFilter");
//            this.filterLog = new Radiogroup();
//            this.filterLog.appendChild(this.bpmnMinerW.getFellow("filtered"));
//            this.filterLog.appendChild(this.bpmnMinerW.getFellow("notFiltered"));

            this.sortLog = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerSort");
//            this.sortLog = new Radiogroup();
//            this.sortLog.appendChild(this.bpmnMinerW.getFellow("sort"));
//            this.sortLog.appendChild(this.bpmnMinerW.getFellow("notSort"));

            this.structProcess = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerStructProcess");
//            this.structProcess = new Radiogroup();
//            this.structProcess.appendChild(this.bpmnMinerW.getFellow("structured"));
//            this.structProcess.appendChild(this.bpmnMinerW.getFellow("notStructured"));

            this.bimpAnnotated = (Radiogroup) this.bpmnMinerW.getFellow("bpmnMinerBimpAnnotated");

            this.interruptingEventTolerance = (Slider) this.bpmnMinerW.getFellow("bpmnMinerInterruptingEventTolerance");
            this.multiInstancePercentage = (Slider) this.bpmnMinerW.getFellow("bpmnMinerMultiInstancePercentage");
            this.multiInstanceTolerance = (Slider) this.bpmnMinerW.getFellow("bpmnMinerMultiInstanceTolerance");
            this.timerEventPercentage = (Slider) this.bpmnMinerW.getFellow("bpmnMinerTimerEventPercentage");
            this.timerEventTolerance = (Slider) this.bpmnMinerW.getFellow("bpmnMinerTimerEventTolerance");
            this.noiseThreshold = (Slider) this.bpmnMinerW.getFellow("bpmnMinerNoiseThreshold");

            this.cancelButton = (Button) this.bpmnMinerW.getFellow("bpmnMinerCancelButton");
            this.okButton = (Button) this.bpmnMinerW.getFellow("bpmnMinerOKButton");

            this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    modelName.setText(modelName.getText().trim());
                    if (modelName.getText().equals("")) {
                        Messagebox.show("Please enter a model name");
                        return;
                    }
                    setupMiningAlgorithm();
                }
            });
            this.bpmnMinerW.doModal();
        }catch (IOException e) {
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

    protected void setupMiningAlgorithm() {
        bpmnMinerW.detach();
        new MiningSettingsController(this, getSelectedAlgorithm());
    }

    public void setMiningSettings(MiningSettings params) {
        this.params = params;
    }

    protected void createCanditatesEntity() {
        try {
            if(log == null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(logByteArray, 0, logByteArray.length);
                InputStream zipEntryIS = new ByteArrayInputStream(bos.toByteArray());
                log = importFromStream(new XFactoryNaiveImpl(), zipEntryIS, logFileName);
            }

            if(log == null) {
                Messagebox.show("Please select a log.");
            }else {
                if(flatModel.getSelectedIndex() == 0) {
                    noEntityException();
                }else {
                    erModel = new DiscoverERmodel();
                    listCandidates = erModel.generateAllAttributes(log);
                    Collections.sort(listCandidates);

                    new CandidatesEntitiesController(this, listCandidates);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setSelectedCandidatesEntities(List<String> listCandidates, boolean[] selected) throws IOException, NoEntityException {
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
        mineAndSave(listCandidates, group);
    }

    public void noEntityException() {
        mineAndSave(new ArrayList<String>(), new HashMap<Set<String>, Set<String>>());
    }

    private void mineAndSave(List<String> listCandidates, Map<Set<String>, Set<String>> group) {

        Window window;
        Label descriptionLabel;
        Progressmeter fractionCompleteProgressmeter;

        try {
            window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/mineAndSave.zul", null, null);
        } catch (IOException e) {
            throw new Error("Unable to create embedded ZUL", e);
        }

        ((Button) window.getFellow("cancel")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                window.detach();
            }
        });

        descriptionLabel = (Label) window.getFellow("description");
        assert descriptionLabel != null;

        fractionCompleteProgressmeter = (Progressmeter) window.getFellow("fractionComplete");
        assert fractionCompleteProgressmeter != null;

        window.doModal();

        miningEventListener = new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                switch (event.getName()) {
                case CHANGE_DESCRIPTION:
                    descriptionLabel.setValue((String) event.getData());
                    break;

                case CHANGE_FRACTION_COMPLETE:
                    fractionCompleteProgressmeter.setValue((int) Math.round(100.0 * (Double) event.getData()));
                    break;

                case MINING_COMPLETE:
                    fractionCompleteProgressmeter.setValue(100);
                    descriptionLabel.setValue("Saving BPMN model");
                    try {
                        save();

                    } catch (Exception e) {
                        e.printStackTrace();
                        eventQueue.unsubscribe(miningEventListener); 
                        Messagebox.show("Process mining failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);

                    }
                    window.detach();
                    BPMNMinerController.this.portalContext.refreshContent();

                    break;

                case MINING_EXCEPTION:
                    Exception e = (Exception) event.getData();
                    Messagebox.show("Process mining failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
                    break;

                case ANNOTATION_EXCEPTION:
                    Exception e2 = (Exception) event.getData();
                    Messagebox.show("Unable to annotate BPMN model for BIMP simulation (" + e2.getMessage() + ")\n\nModel will be created without annotations.", "Attention", Messagebox.OK, Messagebox.EXCLAMATION);
                    break;
                }
                
                eventQueue.unsubscribe(miningEventListener); 
            }
        };
        eventQueue.subscribe(miningEventListener);

        new Thread() {
            public void run() {
                try {
                    mine(listCandidates, group);
                    eventQueue.publish(new Event(MINING_COMPLETE, null, null));

                } catch (Exception e) {
                    e.printStackTrace();
                    eventQueue.publish(new Event(MINING_EXCEPTION, null, e));
                }
                // Do not do this: call unsubscribe in another thread will not be successful
                // because Executions.getCurrent() will be null
                //eventQueue.unsubscribe(eventListener); 
            }
        }.start();
    }

    private String model;

    private void mine(List<String> listCandidates, Map<Set<String>, Set<String>> group) throws Exception {
        if(filterLog.getSelectedIndex() == 0) {
            log = infrequentBehaviourFilterService.filterLog(log);
        }

        model = bpmnMinerService.discoverBPMNModel(log, sortLog.getSelectedIndex()==0?true:false, structProcess.getSelectedIndex()==0?true:false, getSelectedAlgorithm(), params, dependencyAlgorithms.getSelectedIndex()+1,
                ((double) interruptingEventTolerance.getCurpos())/100.0, ((double) timerEventPercentage.getCurpos())/100.0, ((double) timerEventTolerance.getCurpos())/100.0,
                ((double) multiInstancePercentage.getCurpos())/100.0, ((double) multiInstanceTolerance.getCurpos())/100.0, ((double) noiseThreshold.getCurpos())/100.0,
                listCandidates, group);

        if (bimpAnnotated.getSelectedIndex() == 0) {
            try {
                model = bimpAnnotationService.annotateBPMNModelForBIMP(model, log, new BIMPAnnotationService.Context() {
                    public void setDescription(String description) {
                        eventQueue.publish(new Event(CHANGE_DESCRIPTION, null, description));
                    }

                    public void setFractionComplete(Double fractionComplete) {
                        eventQueue.publish(new Event(CHANGE_FRACTION_COMPLETE, null, fractionComplete));
                    }
                });

            } catch (Exception e) {
                LOGGER.warn("Unable to annotate BPMN model for BIMP simulation", e);
                eventQueue.publish(new Event(ANNOTATION_EXCEPTION, null, e));
            }
        }
    }

    private void save() throws Exception {
        String defaultProcessName = null;
        if(this.logFileName != null) {
            defaultProcessName = this.logFileName.split("\\.")[0];
        }
        if (!modelName.getValue().isEmpty()) {
            defaultProcessName = modelName.getValue();
        }

        String user = portalContext.getCurrentUser().getUsername();
        Version version = new Version(1, 0);
        Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
        String now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString();
        boolean publicModel = false;
            
        ProcessModelVersion pmv = processService.importProcess(user,
            portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
            defaultProcessName,
            version,
            this.nativeType,
            canoniserService.canonise(this.nativeType, new ByteArrayInputStream(model.getBytes()), canoniserProperties),
            domainCB.getValue(),
            "Model generated by the Apromore BPMN process mining service.",
            now,  // creation timestamp
            now,  // last update timestamp
            publicModel);

        this.portalContext.displayNewProcess(userInterfaceHelper.createProcessSummary(pmv.getProcessBranch().getProcess(),
            pmv.getProcessBranch(),
            pmv,
            this.nativeType,
            domainCB.getValue(),
            now,  // creation timestamp
            now,  // last update timestamp
            user,
            publicModel));
    }

    public int getSelectedAlgorithm() {
        int selected = miningAlgorithms.getSelectedIndex();
        String name = "";

        switch (selected) {
            case 0 : name = SelectMinerResult.SM; break;
            case 1 : name = SelectMinerResult.IM; break;
            case 2 : name = SelectMinerResult.HM6; break;
            case 3 : name = SelectMinerResult.HM5; break;
            case 4 : name = SelectMinerResult.ALPHA; break;
            case 5 : name = SelectMinerResult.ILP;
        }

        if(name.equals(SelectMinerResult.ALPHA)) return SelectMinerResult.ALPHAPOS;
        else if(name.equals(SelectMinerResult.HM6)) return SelectMinerResult.HMPOS6;
        else if(name.equals(SelectMinerResult.HM5)) return SelectMinerResult.HMPOS5;
        else if(name.equals(SelectMinerResult.SM)) return SelectMinerResult.SMPOS;
        else if(name.equals(SelectMinerResult.ILP)) return SelectMinerResult.ILPPOS;
        else if(name.equals(SelectMinerResult.IM)) return SelectMinerResult.IMPOS;

        return SelectMinerResult.SMPOS;
    }
}
