/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

package org.apromore.plugin.portal.ltl.conformance;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import au.ltl.domain.Action;
import au.ltl.domain.Actions;
import au.ltl.main.RuleVisualization;
import au.ltl.utils.ModelAbstractions;
import au.qut.org.processmining.framework.util.Pair;
import com.google.gson.Gson;
import hub.top.petrinet.PetriNet;
import org.apromore.helper.Version;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.SelectDynamicListController;
import org.apromore.portal.dialogController.dto.SignavioSession;
import org.apromore.service.conf.ltl.LTLConfCheckService;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.Messagebox;

import au.ltl.domain.Constraint;

public class LTLConfController {

    private Window resultsWin;
    private Button closeButton;
    private Rows rows;
    private Grid grid;
    private String nativeType = "BPMN 2.0";

    private PetriNet net;

    private LTLConfCheckService ltlConfService;

    private PortalContext portalContext;
    private Window enterLogWin;
    private Button uploadLog;
    private Button cancelButton;
    private Button okButton;
    private SelectDynamicListController domainCB;

    private Textbox inputText;
    private Textbox inputAddText;
    private Textbox inputDeleteText;

    private Label l;

    private String specificationString = null;
    private String specificationFileName = null;

    private org.zkoss.util.media.Media specificationFile = null;

    private ModelAbstractions model;
    private ProcessSummaryType processSummaryType;
    private VersionSummaryType versionSummaryType;

    public LTLConfController(PortalContext portalContext, LTLConfCheckService ltlConfService) {
        this.ltlConfService = ltlConfService;
        this.portalContext = portalContext;
    }

    public void apmVerify(PetriNet net) {
        this.net = net;
        popupXML();
    }

    public void makeResultWindows(HashMap<String, List<RuleVisualization>> results){
        try {
            Set<RequestParameterType<?>> requestParameters = new HashSet<>();
            requestParameters.add(new RequestParameterType<String>("m1_differences_json", new Gson().toJson(results)));

            displayMisconformances(processSummaryType, versionSummaryType, "BPMN 2.0", null, null, requestParameters);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void displayMisconformances(final ProcessSummaryType process1, final VersionSummaryType version1,
                                  final String nativeType, final String annotation,
                                  final String readOnly, Set<RequestParameterType<?>> requestParameterTypes) {
        String instruction = "";

        String username = this.portalContext.getCurrentUser().getUsername();
        EditSessionType editSession1 = createEditSession(username,process1, version1, nativeType, annotation);

        try {
            String id = UUID.randomUUID().toString();

            SignavioSession session = new SignavioSession(editSession1, null, null, process1, version1, null, null, requestParameterTypes);
            UserSessionManager.setEditSession(id, session);

            String url = "../ltlconf/displayMisconformanceInSignavio.zul?id=" + id;
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

    public void verify(){
        String text = this.inputText.getValue();
        StringTokenizer token = new StringTokenizer(text, "\n");

        LinkedList<Constraint> specifications = new LinkedList<>();
        int i = 0;
        while(token.hasMoreTokens()) {
            String tok= token.nextToken();
            specifications.add(new Constraint(tok, "Rule"+i));
            System.out.println(tok);
        }

        System.out.println(" ----- " + specifications.size());
        System.out.println(Arrays.asList(specifications).toString());

        try {
            makeResultWindows(ltlConfService.checkConformanceLTL(this.model, new ByteArrayInputStream(this.specificationFile.getByteData()), specifications, Integer.parseInt(inputAddText.getValue()), Integer.parseInt(inputDeleteText.getValue())));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void popupXML() {
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
            this.uploadLog = (Button) this.enterLogWin.getFellow("specificationUpload");
            this.inputText = (Textbox) this.enterLogWin.getFellow("specificationTB");
            this.inputAddText = (Textbox) this.enterLogWin.getFellow("addCost");
            this.inputDeleteText = (Textbox) this.enterLogWin.getFellow("deleteCost");
            this.cancelButton = (Button) this.enterLogWin.getFellow("cancelButton");
            this.okButton = (Button) this.enterLogWin.getFellow("oKButton");

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
                    verify();
                    cancel();
                }
            });
            this.enterLogWin.doModal();
        }catch (IOException e) {
            Messagebox.show("Import failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    protected void cancel() {
        this.enterLogWin.detach();
    }

    protected void closeResults(){
        this.resultsWin.detach();
    }

//    protected void downloadResults(String xmlString){
//        Filedownload.save(xmlString.getBytes(Charset.forName("UTF-8")), "text/plain",
//                "feedback.txt");
//    }

    private void uploadFile(UploadEvent event) {
        specificationFile = event.getMedia();
        l.setStyle("color: blue");
        l.setValue(specificationFile.getName());
        byte[] byteStr = specificationFile.getByteData();
        try {
            specificationString = new String(byteStr, "UTF-8");
        }catch(Exception e){
            e.printStackTrace();
        }

        specificationFileName = specificationFile.getName();
    }

    public void checkConformance(ModelAbstractions model, ProcessSummaryType processSummaryType, VersionSummaryType versionSummaryType) {
        this.model = model;
        this.processSummaryType = processSummaryType;
        this.versionSummaryType = versionSummaryType;
        popupXML();
    }

    public class SimpleRenderer implements RowRenderer<String> {

        public void render(Row row, String data, int index) {
            // the data append to each row with simple label
            row.appendChild(new Label(Integer.toString(index)));
            row.appendChild(new Label(data));

            if(data.contains("is always") || data.contains("exists a path") || data.contains("never occur") || data.contains("are concurrent"))
                row.setStyle("background:#C4E0B2;!important;Border: #E3E3E3");
            else
                row.setStyle("background:#f0997c;!important;Border: #E3E3E3");

            // we create a thumb up/down comment to each row
        }
    }
}
