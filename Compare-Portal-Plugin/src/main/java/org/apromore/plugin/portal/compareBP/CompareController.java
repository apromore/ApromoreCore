/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.compareBP;

import java.io.*;
import java.util.*;

import hub.top.petrinet.PetriNet;
import org.apache.tools.ant.types.resources.selectors.Compare;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.compare.CompareService;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.in.*;
import org.deckfour.xes.model.XLog;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

/**
 * Created by conforti on 10/04/15.
 */
public class CompareController {

    private PortalContext portalContext;
    private Window enterLogWin;
    private Window resultsWin;
    private Button uploadLog;
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
    private List<String> listCandidates;
    private boolean[] selected;
    private Label l;

    private org.zkoss.util.media.Media logFile = null;
    private byte[] logByteArray = null;
    private String logFileName = null;
    private CompareService compareService;
    private PetriNet net;

    public CompareController(PortalContext portalContext, CompareService compareService, PetriNet net1, PetriNet net2, HashSet<String> silent1, HashSet<String> silent2) throws Exception{
        this.compareService = compareService;
        this.portalContext = portalContext;
        Set<String> differences = compareService.discoverModelModel(net1, net2, silent1, silent2);

//                for (String s : differences)
//                    result += s + "\n";

        makeResultWindows(differences);
    }

    public CompareController(PortalContext portalContext, CompareService compareService, PetriNet net) {
        this.compareService = compareService;
        this.net = net;
        this.portalContext = portalContext;

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
        this.enterLogWin.detach();
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
//                String result = "";
                Set<String> differences = compareService.discoverBPMNModel(net, log);

//                for (String s : differences)
//                    result += s + "\n";

                makeResultWindows(differences);
//                Messagebox.show(result, "Differences", Messagebox.OK, Messagebox.INFORMATION);
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

//
//            int i = 0;
//            for (String sentence : results) {
//                System.out.println("Sentence = " + sentence);
//                this.row = new Row();
//                this.row.setId(""+(i++));
//                this.sent = new Label(sentence);
//                this.sent.setParent(this.row);
//                this.row.setParent(this.rows);
//            }

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

//    public void setSelectedPrimaryKeys(Map<Set<String>, Set<String>> group) {
//        mineAndSave(listCandidates, group);
//    }

    public void noEntityException() {
    }

    public void setSelectedPrimaryKeys(Map<Set<String>, Set<String>> group) {
    }
//
//    public void noEntityException() {
//        mineAndSave(new ArrayList<String>(), new HashMap<Set<String>, Set<String>>());
//    }
//
//    private void mineAndSave(List<String> listCandidates, Map<Set<String>, Set<String>> group) {
//        try {
//
//            this.enterLogWin.detach();
//
//            String model = bpmnMinerService.discoverBPMNModel(log, sortLog.getSelectedIndex()==0?true:false, structProcess.getSelectedIndex()==0?true:false, miningAlgorithms.getSelectedIndex(), dependencyAlgorithms.getSelectedIndex()+1,
//                    ((double) interruptingEventTolerance.getCurpos())/100.0, ((double) timerEventPercentage.getCurpos())/100.0, ((double) timerEventTolerance.getCurpos())/100.0,
//                    ((double) multiInstancePercentage.getCurpos())/100.0, ((double) multiInstanceTolerance.getCurpos())/100.0, ((double) noiseThreshold.getCurpos())/100.0,
//                    listCandidates, group);
//
//            String defaultProcessName = this.logFileName.split("\\.")[0];
//            if(!modelName.getValue().isEmpty()) {
//                defaultProcessName = modelName.getValue();
//            }
//
//            String user = portalContext.getCurrentUser().getUsername();
//            Version version = new Version(1, 0);
//            Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
//            String now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString();
//            boolean publicModel = true;
//
//            ProcessModelVersion pmv = processService.importProcess(user,
//                portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
//                defaultProcessName,
//                version,
//                this.nativeType,
//                canoniserService.canonise(this.nativeType, new ByteArrayInputStream(model.getBytes()), canoniserProperties),
//                domainCB.getValue(),
//                "Model generated by the Apromore BPMN process mining service.",
//                now,  // creation timestamp
//                now,  // last update timestamp
//                publicModel);
//
//            this.portalContext.displayNewProcess(userInterfaceHelper.createProcessSummary(pmv.getProcessBranch().getProcess(),
//                pmv.getProcessBranch(),
//                pmv,
//                this.nativeType,
//                domainCB.getValue(),
//                now,  // creation timestamp
//                now,  // last update timestamp
//                user,
//                publicModel));
//
//            this.portalContext.refreshContent();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public class SimpleRenderer implements RowRenderer<String> {

        public void render(Row row, String data, int index) {
            // the data append to each row with simple label
            row.appendChild(new Label(Integer.toString(index)));
            row.appendChild(new Label(data));
            // we create a thumb up/down comment to each row
        }
    }
}
