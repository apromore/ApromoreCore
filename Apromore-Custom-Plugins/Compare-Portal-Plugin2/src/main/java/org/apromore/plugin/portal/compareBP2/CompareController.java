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

package org.apromore.plugin.portal.compareBP2;

import ee.ut.eventstr.comparison.differences.Differences;
import ee.ut.eventstr.comparison.differences.DifferencesML;
import ee.ut.eventstr.comparison.differences.ModelAbstractions;
import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.service.compare.CompareService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by conforti on 10/04/15.
 */
public class CompareController {

    private PortalContext portalContext;
    private Window enterLogWin;
    private Window resultsWin;
    private Button uploadLog;
    private Button uploadLog2;
    private Button cancelButton;
    private Button closeButton;
    private Button okButton;
    private Grid grid;
    private Rows rows;
    private Row row;
    private Label sent;
    private SelectDynamicListController domainCB;
    private String nativeType = "BPMN 2.0";
    
    private Textbox modelName;
    private Selectbox miningAlgorithms;
    private Radiogroup dependencyAlgorithms;
    private Radiogroup sortLog;
    private Radiogroup structProcess;
    private Slider interruptingEventTolerance;
    private Slider multiInstancePercentage;
    private Slider multiInstanceTolerance;
    private Slider timerEventPercentage;
    private Slider timerEventTolerance;
    private Slider noiseThreshold;

    private XLog log;
    private XLog log2;
    private List<String> listCandidates;
    private boolean[] selected;
    private Label l;
    private Label l2;

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    private String logFileName = null;
    
    private org.zkoss.util.media.Media logFile2 = null;
    private byte[] logByteArray2 = null;
    private String logFileName2 = null;

    private CompareService compareService;
    private ModelAbstractions model;
    VersionSummaryType version;
    ProcessSummaryType process;

    private HashSet<String> obs;


    public CompareController(PortalContext portalContext, CompareService compareService) {
        this.compareService = compareService;
        this.portalContext = portalContext;
    }

    public void compareLLPopup(){

        try {
            List<String> domains = new ListModelList<>();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            this.enterLogWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/compareLL.zul", null, null);

            this.l = (Label) this.enterLogWin.getFellow("fileName");
            this.uploadLog = (Button) this.enterLogWin.getFellow("bpmnMinerUpload");

            this.l2 = (Label) this.enterLogWin.getFellow("fileName2");
            this.uploadLog2 = (Button) this.enterLogWin.getFellow("bpmnMinerUpload2");

            this.cancelButton = (Button) this.enterLogWin.getFellow("bpmnMinerCancelButton");
            this.okButton = (Button) this.enterLogWin.getFellow("bpmnMinerOKButton");

            this.uploadLog.addEventListener("onUpload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    uploadFile((UploadEvent) event);
                }
            });

            this.uploadLog2.addEventListener("onUpload", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    uploadFile2((UploadEvent) event);
                }
            });

            this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    compareLog2Log();
                    cancel();
                }
            });
            this.enterLogWin.doModal();

        }catch (IOException e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void compareLL(XLog log1, XLog log2) {
        try {
            this.log = log1;
            this.log2 = log2;
            Set<String> differences = compareService.discoverLogLog(log1, log2);
            makeResultWindows(differences);
        } catch (Exception e) {
            Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void compareMM(ModelAbstractions model1, ModelAbstractions model2, HashSet<String> obs1, HashSet<String> obs2, ProcessSummaryType process1, VersionSummaryType version1, ProcessSummaryType process2, VersionSummaryType version2) throws Exception {
        Differences differences = compareService.discoverModelModelAbs(model1, model2, obs1, obs2);

        Set<RequestParameterType<?>> requestParameters = new HashSet<>();
        requestParameters.add(new RequestParameterType<Integer>("m1_pes_size", model1.getPES().getLabels().size()));
        requestParameters.add(new RequestParameterType<Integer>("m2_pes_size", model2.getPES().getLabels().size()));
        requestParameters.add(new RequestParameterType<String>("m1_differences_json", Differences.toJSON(differences)));

        compareProcesses(process1, version1, process2, version2, "BPMN 2.0", null, null, requestParameters);
    }

    public void compareML(ModelAbstractions model, ProcessSummaryType process, VersionSummaryType version, XLog log, String logName, HashSet<String> obs1) {
        this.model = model;
        this.process = process;
        this.version = version;
        this.obs = obs1;
        this.log = log;

        try {
            DifferencesML differences = compareService.discoverBPMNModel(model, log, obs);

            Set<RequestParameterType<?>> requestParameters = new HashSet<>();
            requestParameters.add(new RequestParameterType<String>("m1_differences_json", DifferencesML.toJSON(differences)));
            requestParameters.add(new RequestParameterType<String>("log_name", logName));

            compareProcessLog(process, version, "BPMN 2.0", null, null, requestParameters, log);

        } catch (Exception e) {
            Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void compareMLPopup(ModelAbstractions model, HashSet<String> obs1, ProcessSummaryType process, VersionSummaryType version) {
        this.model = model;
        this.process = process;
        this.version = version;
        this.obs = obs1;

        try {
            List<String> domains = new ListModelList<>();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            this.enterLogWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/compare.zul", null, null);

            this.l = (Label) this.enterLogWin.getFellow("fileName");
            this.uploadLog = (Button) this.enterLogWin.getFellow("bpmnMinerUpload");
            this.cancelButton = (Button) this.enterLogWin.getFellow("bpmnMinerCancelButton");
            this.okButton = (Button) this.enterLogWin.getFellow("bpmnMinerOKButton");

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
                    cancel();
                }
            });
            this.enterLogWin.doModal();
        }catch (IOException e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    /**
     * Display two process versions and allow their differences to be highlighted.
     *
     * @param process1 the process summary
     * @param version1 the version of the process
     * @param process2 the process summary
     * @param version2 the version of the process
     * @param nativeType the native type of the process
     * @param annotation the annotation of that process
     * @param readOnly is this model readonly or not
     * @param requestParameterTypes request parameters types.
     * @throws InterruptedException
     */
    public void compareProcesses(final ProcessSummaryType process1, final VersionSummaryType version1,
                                 final ProcessSummaryType process2, final VersionSummaryType version2,
                                 final String nativeType, final String annotation,
                                 final String readOnly, Set<RequestParameterType<?>> requestParameterTypes) throws InterruptedException {
        String instruction = "";

        String username = this.portalContext.getCurrentUser().getUsername();
        EditSessionType editSession1 = createEditSession(username,process1, version1, nativeType, annotation);
        EditSessionType editSession2 = createEditSession(username,process2, version2, nativeType, annotation);

        try {
            String id = UUID.randomUUID().toString();

            SignavioSession session = new SignavioSession(editSession1, editSession2, null, process1, version1, process2, version2, requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "../compare2/compareModels.zul?id=" + id;
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot compare " + process1.getName() + " and " + process2.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void compareProcessLog(final ProcessSummaryType process1, final VersionSummaryType version1,
                                 final String nativeType, final String annotation,
                                 final String readOnly, Set<RequestParameterType<?>> requestParameterTypes, XLog log) {
        String instruction = "";

        String username = this.portalContext.getCurrentUser().getUsername();
        EditSessionType editSession1 = createEditSession(username,process1, version1, nativeType, annotation);

        try {
            String id = UUID.randomUUID().toString();

            SignavioSession session = new SignavioSession(editSession1, null, null, process1, version1, null, null, requestParameterTypes);
            session.setLog(log);
            UserSessionManager.setEditSession(id, session);

            String url = "../compare2/compareModelToLog.zul?id=" + id;
            instruction += "window.open('" + url + "');";

            Clients.evalJavaScript(instruction);
        } catch (Exception e) {
            Messagebox.show("Cannot compare " + process1.getName() + " (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private static EditSessionType createEditSession(final String username, final ProcessSummaryType process, final VersionSummaryType version, final String nativeType, final String annotation) {

        EditSessionType editSession = new EditSessionType();

        editSession.setDomain(process.getDomain());
        editSession.setNativeType(nativeType.equals("XPDL 2.2")?"BPMN 2.0":nativeType);
        editSession.setProcessId(process.getId());
        editSession.setProcessName(process.getName());
        editSession.setUsername(username);
        editSession.setPublicModel(process.isMakePublic());
        editSession.setOriginalBranchName(version.getName());
        editSession.setOriginalVersionNumber(version.getVersionNumber());
        editSession.setCurrentVersionNumber(version.getVersionNumber());
        editSession.setMaxVersionNumber(findMaxVersion(process));

        editSession.setCreationDate(version.getCreationDate());
        editSession.setLastUpdate(version.getLastUpdate());
        if (annotation == null) {
            editSession.setWithAnnotation(false);
        } else {
            editSession.setWithAnnotation(true);
            editSession.setAnnotation(annotation);
        }

        return editSession;
    }

    /* From a list of version summary types find the max version number. */
    private static String findMaxVersion(ProcessSummaryType process) {
        Version versionNum;
        Version max = new Version(0, 0);
        for (VersionSummaryType version : process.getVersionSummaries()) {
            versionNum = new Version(version.getVersionNumber());
            if (versionNum.compareTo(max) > 0) {
                max = versionNum;
            }
        }
        return max.toString();
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
    
    private void uploadFile2(UploadEvent event) {
        logFile2 = event.getMedia();
        l2.setStyle("color: blue");
        l2.setValue(logFile2.getName());
        logByteArray2 = logFile2.getByteData();
        logFileName2 = logFile2.getName();
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
        this.enterLogWin.detach();
    }
    
    protected void compareLog2Log() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(logByteArray, 0, logByteArray.length);
            InputStream zipEntryIS = new ByteArrayInputStream(bos.toByteArray());
            log = importFromStream(new XFactoryNaiveImpl(), zipEntryIS, logFileName);
            
            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            bos2.write(logByteArray2, 0, logByteArray2.length);
            InputStream zipEntryIS2 = new ByteArrayInputStream(bos2.toByteArray());
            log2 = importFromStream(new XFactoryNaiveImpl(), zipEntryIS2, logFileName2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(log == null) {
            Messagebox.show("Please select a log.");
        }else {
            try {
                Set<String> differences = compareService.discoverLogLog(log2, log);
                makeResultWindows(differences);
            } catch (Exception e) {
                Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        }
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
        }else {
            try {
                compareML(model, process, version, log, logFileName, obs);
            } catch (Exception e) {
                Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        }
    }

    public void makeResultWindows(Set<String> results){
        try {
            this.resultsWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/results.zul", null, null);
            this.closeButton = (Button) this.resultsWin.getFellow("closeBtn");

            this.closeButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    closeResults();
                }
            });

            this.grid = (Grid) this.resultsWin.getFellow("gridMD");
            this.rows = this.grid.getRows();

            ArrayList<String> gridData = new ArrayList<String>();

            for (String sentence : results)
                gridData.add(sentence);

            ListModelList listModel = new ListModelList(gridData);
            RowRenderer rowRenderer = new SimpleRenderer();

            grid.setRowRenderer(rowRenderer);
            grid.setModel(listModel);

            this.resultsWin.doModal();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void closeResults(){
        this.resultsWin.detach();
    }

    public List<String> getIgnoreAttributes() {
        List<String> ignored = new ArrayList<String>();
        for (int i = 0; i < selected.length; i++) {
            if (!selected[i]) ignored.add(listCandidates.get(i));
        }
        return ignored;
    }

    public void noEntityException() {
    }

    public void setSelectedPrimaryKeys(Map<Set<String>, Set<String>> group) {
    }

    public class SimpleRenderer implements RowRenderer<String> {

        public void render(Row row, String data, int index) {
            // the data append to each row with simple label
            row.appendChild(new Label(Integer.toString(index)));
            row.appendChild(new Label(data));
            // we create a thumb up/down comment to each row
        }
    }
}
