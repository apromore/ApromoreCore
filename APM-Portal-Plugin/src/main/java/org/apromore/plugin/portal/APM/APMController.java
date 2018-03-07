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

package org.apromore.plugin.portal.APM;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

import hub.top.petrinet.PetriNet;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.dialogController.SelectDynamicListController;
import org.apromore.service.apm.APMService;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.*;

/**
 * Created by conforti on 10/04/15.
 */
public class APMController {

    private PortalContext portalContext;
    private Window resultsWin;
    private Button closeButton;
    private Button downloadButton;
    private Rows rows;
    private Grid grid;
    private String nativeType = "BPMN 2.0";

    private PetriNet[] nets;
    private String prefix;
    private Checkbox viresp;
    private Checkbox viprec;
    private Checkbox veiresp;
    private Checkbox veresp;
    private Checkbox vconf;
    private Checkbox vpar;

    private Window enterLogWin;
    private Button cancelButton;
    private Button okButton;
    private SelectDynamicListController domainCB;

    private APMService apmService;


    public APMController(PortalContext portalContext, APMService apmService) {
        this.apmService = apmService;
        this.portalContext = portalContext;
    }

    public void apmVerify(PetriNet[] nets, String prefix) {
        try {
            this.nets = nets;
            this.prefix = prefix;

            popopWindowCheck();
        } catch (Exception e) {
            Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void getSpecification(){
        try {
            String[] differences = apmService.getSpecification(nets, prefix, viresp.isChecked(), viprec.isChecked(), veiresp.isChecked(), veresp.isChecked(), vconf.isChecked(), vpar.isChecked());
            makeResultWindows(differences);
        } catch (Exception e) {
            Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    public void popopWindowCheck() {
        try {
            List<String> domains = new ListModelList<>();
            this.domainCB = new SelectDynamicListController(domains);
            this.domainCB.setReference(domains);
            this.domainCB.setAutodrop(true);
            this.domainCB.setWidth("85%");
            this.domainCB.setHeight("100%");
            this.domainCB.setAttribute("hflex", "1");

            this.enterLogWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/checklist.zul", null, null);

            this.viresp = (Checkbox) this.enterLogWin.getFellow("viresp");
            this.viprec = (Checkbox) this.enterLogWin.getFellow("viprec");
            this.veiresp = (Checkbox) this.enterLogWin.getFellow("veiresp");
            this.veresp = (Checkbox) this.enterLogWin.getFellow("veresp");
            this.vconf = (Checkbox) this.enterLogWin.getFellow("vconf");
            this.vpar = (Checkbox) this.enterLogWin.getFellow("vpar");

            this.viresp.setChecked(true);
            this.viprec.setChecked(true);
            this.veiresp.setChecked(true);
            this.veresp.setChecked(true);
            this.vconf.setChecked(true);
            this.vpar.setChecked(true);

            this.cancelButton = (Button) this.enterLogWin.getFellow("cancelBtn");
            this.okButton = (Button) this.enterLogWin.getFellow("acceptBtn");

            this.cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    cancel();
                }
            });
            this.okButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    getSpecification();
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

    public void makeResultWindows(String[] results){
        try {
            this.resultsWin = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/results.zul", null, null);
            this.closeButton = (Button) this.resultsWin.getFellow("closeBtn");
            this.downloadButton = (Button) this.resultsWin.getFellow("downloadBtn");

            this.closeButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    closeResults();
                }
            });

            this.downloadButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    downloadResults(results[0]);
                }
            });

            this.grid = (Grid) this.resultsWin.getFellow("gridMD");
            this.rows = this.grid.getRows();

            ArrayList<String> gridData = new ArrayList<String>();

            StringTokenizer token = new StringTokenizer(results[1], "\n");
            while(token.hasMoreTokens())
                gridData.add(token.nextToken());

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

    protected void downloadResults(String xmlString){
        Filedownload.save(xmlString.getBytes(Charset.forName("UTF-8")), "application/xml",
                "verification.xml");
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
