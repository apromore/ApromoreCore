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

package org.apromore.plugin.portal.APM;

import java.nio.charset.Charset;
import java.util.*;

import hub.top.petrinet.PetriNet;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.apm.APMService;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
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

    private APMService apmService;


    public APMController(PortalContext portalContext, APMService apmService) {
        this.apmService = apmService;
        this.portalContext = portalContext;
    }

    public void apmVerify(PetriNet[] nets, String prefix) {
        try {
            this.nets = nets;
            this.prefix = prefix;
            String[] differences = apmService.getSpecification(nets, prefix);

            makeResultWindows(differences);
        } catch (Exception e) {
            Messagebox.show("Exception in the call", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
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
