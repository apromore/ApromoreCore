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
import org.apromore.service.bpmnminer.BPMNMinerService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.logfilter.behaviour.InfrequentBehaviourFilterService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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

    private final BPMNMinerService bpmnMinerService;
    private final CanoniserService canoniserService;
    private final DomainService domainService;
    private final ProcessService processService;
    private final EventLogService eventLogService;
    private final InfrequentBehaviourFilterService infrequentBehaviourFilterService;
    private final UserInterfaceHelper userInterfaceHelper;

    public BPMNMinerController(PortalContext portalContext,
                               BPMNMinerService bpmnMinerService,
                               CanoniserService canoniserService,
                               DomainService domainService,
                               ProcessService processService,
                               EventLogService eventLogService,
                               InfrequentBehaviourFilterService infrequentBehaviourFilterService,
                               UserInterfaceHelper userInterfaceHelper) {

        this.portalContext       = portalContext;
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
            if (elements.size() == 0) {
                this.bpmnMinerW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/bpmnMinerInput.zul", null, null);
                this.l = (Label) this.bpmnMinerW.getFellow("fileName");
                this.uploadLog = (Button) this.bpmnMinerW.getFellow("bpmnMinerUpload");
                this.uploadLog.addEventListener("onUpload", new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        uploadFile((UploadEvent) event);
                    }
                });
            }else if (selectedLogSummaryType.size() == 1) {
                log = eventLogService.getXLog(selectedLogSummaryType.iterator().next().getId());
                this.bpmnMinerW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/bpmnMiner.zul", null, null);
            }else {
                portalContext.getMessageHandler().displayInfo("Select one log for process discovery.");
                return;
            }

            this.modelName = (Textbox) this.bpmnMinerW.getFellow("bpmnMinerModelName");
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
        try {

            this.bpmnMinerW.detach();

            if(filterLog.getSelectedIndex() == 0) {
                log = infrequentBehaviourFilterService.filterLog(log);
            }

            String model = bpmnMinerService.discoverBPMNModel(log, sortLog.getSelectedIndex()==0?true:false, structProcess.getSelectedIndex()==0?true:false, getSelectedAlgorithm(), params, dependencyAlgorithms.getSelectedIndex()+1,
                    ((double) interruptingEventTolerance.getCurpos())/100.0, ((double) timerEventPercentage.getCurpos())/100.0, ((double) timerEventTolerance.getCurpos())/100.0,
                    ((double) multiInstancePercentage.getCurpos())/100.0, ((double) multiInstanceTolerance.getCurpos())/100.0, ((double) noiseThreshold.getCurpos())/100.0,
                    listCandidates, group);

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
            boolean publicModel = true;
            
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

            this.portalContext.refreshContent();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
