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

package org.apromore.plugin.portal.bebop;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.*;
import java.util.stream.*;
import java.io.PrintWriter;
import java.io.StringWriter;

// Java 2 Enterprise Edition packages
import javax.inject.Inject;
import java.util.Map;

// Third party packages
import org.apromore.model.SummaryType;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

// Local packages
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.helper.Version;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.portal.custom.gui.tab.impl.TabRowValue;
import org.apromore.service.ProcessService;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.bebop.BebopService;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;

import org.apromore.graph.canonical.Canonical;
import au.edu.qut.bpmn.exporter.impl.BPMNDiagramExporterImpl;
/**
 * Bebop service. Created by Fabrizio Fornari 18/05/2017
 */
@Component("plugin")
public class BebopPlugin extends PluginCustomGui {
    private static final Logger LOGGER = LoggerFactory.getLogger(BebopPlugin.class);

    private static final String NATIVE_TYPE = "BPMN 2.0";

    @Inject private BebopService bebopService;
    @Inject private ProcessService processService;
    @Inject private BPMNDiagramImporter importerService;

    @Override
    public String getLabel(Locale locale) {
        return "Check model guidelines with BEBoP";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Analyze";
    }

    @Override
    public void execute(final PortalContext context) {

        BebopController controller = new BebopController(context, bebopService);

      //try to see if this is the right model in bpmn
      ArrayList <String> tableRows = getGuidelines(context);

        // System.out.println("\nIN PORTAL DOPO  doSomething(context);");

        //controller.bebopShowResult(tableRows);
      String modelString = null;
      String modelName = null;
      String branch = null;
      String nativeType = null;
      String annName = null;
      List<TabRowValue> rows = new ArrayList<>();

        //DEBUG
        // System.out.println("\nIN PORTAL before tableRows;");
                int j=0;
               for(int i=0;i<tableRows.size();i++){
                   if(!tableRows.get(i).contains("node")&&!tableRows.get(i).contains("sid")) {
                       TabRowValue trv = createTabRowValue(tableRows.get(i));
                       rows.add(trv);
                       if (j % 2 != 0) rows.add(createTabRowValue(""));
                       j++;
                   }
               }
                //DEBUG
                // System.out.println("\nIN PORTAL after tableRows;");

               List<Listheader> listheaders = new ArrayList<>();
               listheaders.add(new Listheader("Understandability Guidelines Check Result"));

               addTab("Check model guidelines with BEBoP", "", rows, listheaders, null, context);

               context.getMessageHandler().displayInfo("Executed example plug-in!");

               //DEBUG
               // System.out.println("\nIN PORTAL after context.getMessageHandler().displayInfo(\"Executed example plug-in!\");  ;");

  }//execute


  public ArrayList <String> doSomething(PortalContext portalContext){

    ArrayList <String> guidelinesResult = null;
    Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        final Map<ProcessSummaryType, List<VersionSummaryType>> processVersions = new HashMap<ProcessSummaryType, List<VersionSummaryType>>();


    for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof ProcessSummaryType) {
                processVersions.put((ProcessSummaryType) entry.getKey(), entry.getValue());
            }
        }

        if( processVersions.size() != 1 ) {
            Messagebox.show("Please, select exactly one process.", "Wrong Process Selection", Messagebox.OK, Messagebox.INFORMATION);
        }


    try {

          for (ProcessSummaryType process : processVersions.keySet()) {
              for (VersionSummaryType vst : processVersions.get(process)) {
                  int procID = process.getId();
                  String procName = process.getName();
                  String branch = vst.getName();
                  Version version = new Version(vst.getVersionNumber());

                  String model = processService.getBPMNRepresentation(procName, procID, branch, version);
                  BPMNDiagram bpmnDiagram = importerService.importBPMNDiagram(model);
                  String modelString=null;

                  try{
                     BPMNDiagramExporterImpl exporterService = new BPMNDiagramExporterImpl();
                     modelString  = exporterService.exportBPMNDiagram(bpmnDiagram);

                }catch(Exception e2){
                              System.err.println("Unable to parse model to string: " + e2.getMessage());
                              StringWriter errors = new StringWriter();
                              e2.printStackTrace(new PrintWriter(errors));
                              Messagebox.show("Unable exportBPMNDiagram to string: "+errors.toString(), "Attention", Messagebox.OK, Messagebox.ERROR);
                              e2.printStackTrace();
                  }

                  try{
                    guidelinesResult = bebopService.checkGuidelinesBPMNDiagram(bpmnDiagram);

                      HashMap<Integer,JSONObject> testMap = bebopService.checkGuidelinesBPMNDiagramJson(bpmnDiagram);
                      //DEBUG
                      // System.out.println("\nJSONArray testMap = "+testMap.toString());

                      //for (HashMap.Entry entry : testMap.entrySet()) {
                      //   System.out.println(entry.getKey() + ", " + entry.getValue());
                      //}
                      //DEBUG
                      // System.out.println("\nIN PORTAL after guidelinesResult = bebopService.checkGuidelinesBPMNDiagram(bpmnDiagram);");

                  }catch(Exception e4){
                              StringWriter errors = new StringWriter();
                              e4.printStackTrace(new PrintWriter(errors));
                              Messagebox.show("Unable to checkGuidelinesBPMNDiagram: "+errors.toString(), "Attention", Messagebox.OK, Messagebox.ERROR);
                              e4.printStackTrace();
                }
        }
      }




    }catch(Exception e2){
                  System.err.println("Unable to Receive BEBoP results: " + e2.getMessage());
                  StringWriter errors = new StringWriter();
                  e2.printStackTrace(new PrintWriter(errors));
                  Messagebox.show("Unable to export BPMN model; erroc: "+errors.toString(), "Attention", Messagebox.OK, Messagebox.ERROR);
                  e2.printStackTrace();
    }
    //DEBUG
    //System.out.println("\nIN PORTAL before return guidelinesResult");



      return guidelinesResult;
  }//close doSomething()




    public ArrayList <String> getGuidelines(PortalContext portalContext){

        ArrayList <String> guidelinesResult = null;
        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        final Map<ProcessSummaryType, List<VersionSummaryType>> processVersions = new HashMap<ProcessSummaryType, List<VersionSummaryType>>();


        for(Map.Entry<SummaryType, List<VersionSummaryType>> entry : elements.entrySet()) {
            if(entry.getKey() instanceof ProcessSummaryType) {
                processVersions.put((ProcessSummaryType) entry.getKey(), entry.getValue());
            }
        }

        if( processVersions.size() != 1 ) {
            Messagebox.show("Please, select exactly one process.", "Wrong Process Selection", Messagebox.OK, Messagebox.INFORMATION);
        }


        try {

            for (ProcessSummaryType process : processVersions.keySet()) {
                for (VersionSummaryType vst : processVersions.get(process)) {
                    int procID = process.getId();
                    String procName = process.getName();
                    String branch = vst.getName();
                    Version version = new Version(vst.getVersionNumber());

                    String model = processService.getBPMNRepresentation(procName, procID, branch, version);

                    //DEBUG
                    //System.out.println("THE RIGHT MODEL: "+model);

                    BPMNDiagram bpmnDiagram = importerService.importBPMNDiagram(model);

                    guidelinesResult = bebopService.checkGuidelinesBPMN(model,bpmnDiagram);

                }
            }




        }catch(Exception e2){
            System.err.println("Unable to Receive BEBoP results: " + e2.getMessage());
            StringWriter errors = new StringWriter();
            e2.printStackTrace(new PrintWriter(errors));
            Messagebox.show("Unable to export BPMN model; erroc: "+errors.toString(), "Attention", Messagebox.OK, Messagebox.ERROR);
            e2.printStackTrace();
        }


        return guidelinesResult;
    }//close doSomething()




}
