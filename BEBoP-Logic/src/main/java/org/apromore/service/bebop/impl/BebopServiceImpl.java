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

package org.apromore.service.bebop.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import au.edu.qut.bpmn.exporter.impl.BPMNDiagramExporterImpl;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.*;
import java.util.stream.*;
import java.util.Set;
import java.net.URLEncoder;

import org.apromore.service.bebop.impl.*;
import org.apromore.service.ProcessService;
import org.apromore.service.bebop.BebopService;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.helper.Version;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ExportFormatResultType;

import javax.inject.Inject;
import javax.faces.context.FacesContext;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;

import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.NodeTypeEnum;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Created by Fabrizio Fornari on 18/05/2017.
 */
@Service
public class BebopServiceImpl implements BebopService {

    @Override
    public ArrayList <String> checkGuidelines(
            String modelName,
            ExportFormatResultType exportedProcess,
            Canonical cpfDiagram
            ){

            String branch = null;
            String nativeType = null;
            String annName = null;
            String modelString=null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try{
                TransformerFactory.newInstance().newTransformer().transform( new StreamSource(exportedProcess.getNative().getInputStream()), new StreamResult(baos));
                modelString = baos.toString();

            }catch(Exception e1) {
                System.err.println("Unable to Transform BPMN model: " + e1.getMessage());
                e1.printStackTrace();
            }

            ArrayList <String> tableRows = new ArrayList <String>();

            try{

              ArrayList <String> resultGuideline = GetBEBoPfromStringModel(modelString);

              resultGuideline = GetGuidelinesList(resultGuideline , cpfDiagram);


              for(int i=0; i<resultGuideline.size(); i++){
               tableRows.add(resultGuideline.get(i));
              }

            }catch(Exception e2){
              System.err.println("Unable to Receive BEBoP results: " + e2.getMessage());
              StringWriter errors = new StringWriter();
              e2.printStackTrace(new PrintWriter(errors));
              tableRows.add("Unable to Receive BEBoP results: " + e2.getMessage() + " " +errors.toString());
              tableRows.add("modelString: " + modelString);
              e2.printStackTrace();
            }

            return tableRows;

            }


            public static ArrayList<String> GetBEBoPfromStringModel( String modelString ) throws MalformedURLException, IOException {

                //bckp machine 1
                //target = "http://90.147.167.207:8080/verification-component-understandability-plugin-1.0/validatemodel/put/?en";
                //bckp machine 2
                String target = "http://90.147.102.20:8080/verification-component-understandability-plugin-1.0/validatemodel/put/?en";
                //BEBoP official CNR machine
//               String target = "http://understandabilitybpmn.isti.cnr.it:8080/verification-component-understandability-plugin/validatemodel/put/?en";

                //connection to the server and post of the model in String format
                     URL myurl = new URL(target);
                     HttpURLConnection con = (HttpURLConnection)myurl.openConnection();
                     con.setDoOutput(true);
                     con.setDoInput(true);
                     con.setRequestProperty("Content-Type", "text/plain;");
                     con.setRequestProperty("Accept", "text/plain");
                     con.setRequestProperty("Method", "POST");
                     OutputStream os = con.getOutputStream();
                     os.write(modelString.getBytes("UTF-8"));
                     os.close();

                     //Handling the response from the server
                     StringBuilder id = new StringBuilder();
                     int HttpResult =con.getResponseCode();
                     if(HttpResult ==HttpURLConnection.HTTP_OK){
                         BufferedReader br = new BufferedReader(new   InputStreamReader(con.getInputStream(),"utf-8"));

                         String line = null;
                         while ((line = br.readLine()) != null) {id.append(line + "\n");}
                         br.close();
                         System.out.println(""+id.toString());

                     }else{
                         System.out.println(con.getResponseCode());
                         System.out.println(con.getResponseMessage());
                     }

                     //second connection to the server
                     //GET the id of the moel stored on the server
                //bckp machine 1
                     //target = "http://90.147.167.207:8080/verification-component-understandability-plugin-1.0/validatemodel/"+id;
                //bckp machine 2
                     target = "http://90.147.102.20:8080/verification-component-understandability-plugin-1.0/validatemodel/"+id;
                //BEBoP official CNR machine
                     //target = "http://understandabilitybpmn.isti.cnr.it:8080/verification-component-understandability-plugin/validatemodel/"+id;

                     URL obj = new URL(target);
                     HttpURLConnection con2 = (HttpURLConnection) obj.openConnection();

                     // optional default is GET
                     con2.setRequestMethod("GET");

                   	 int responseCode = con2.getResponseCode();
                   	 System.out.println("\nSending 'GET' request to URL : " + target);
                   	 System.out.println("Response Code : " + responseCode);

                     //Handling the response from the server
                     BufferedReader in = new BufferedReader(
                     		          new InputStreamReader(con2.getInputStream()));

                     String inputLine;
                   	 StringBuffer response = new StringBuffer();
                   	 while ((inputLine = in.readLine()) != null) {	response.append(inputLine);}
                     in.close();

                     System.out.println(response.toString());

                     try{

                         JAXBContext jaxbContext = JAXBContext.newInstance(GuidelinesFactory.class);
                         Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                         StringReader reader = new StringReader(response.toString());
                         GuidelinesFactory glres = (GuidelinesFactory) unmarshaller.unmarshal(reader);
                         ArrayList<String> ret =glres.getIDs();
                         //DEBUG
                         //System.out.println("glres.getDefinitionID(): "+glres.getGuidelines().toString());
                         return ret;

                     }catch( Exception e){

                         ArrayList<String> excpt = new ArrayList <String>();
                         StringWriter sw = new StringWriter();
                         PrintWriter pw = new PrintWriter(sw);
                         e.printStackTrace(pw);
                         sw.toString();
                         excpt.add("Exception ");
                         excpt.add(sw.toString());
                         return excpt;

                     }
                   }

                   public static ArrayList<String> GetGuidelinesList( ArrayList<String> guidelinesList , Canonical cpfDiagram) throws IOException {
                     for(int i=0; i<guidelinesList.size(); i++){

                        Set<CPFNode> originalNodes = cpfDiagram.getNodes();

                        for(CPFNode node : cpfDiagram.getNodes()){
                          if( node.getId().equals(guidelinesList.get(i)) ) {
                                guidelinesList.set(i,node.getNodeType().toString()+" with name: "+node.getName()+" and Id: "+node.getId());

                          }

                        }

                     }

                     return guidelinesList;

                   }

                   public static ArrayList<String> GetGuidelinesListBPMN( ArrayList<String> guidelinesList , BPMNDiagram bpmnDiagram) throws IOException {

                     ArrayList<String> guidelinesListResult = new ArrayList<String>();


                       guidelinesListResult=guidelinesList;

                       ArrayList<NodeBPMN> nodesBPMN = new ArrayList<NodeBPMN>();

                       try {
                           for (Swimlane p : bpmnDiagram.getSwimlanes()) {
                               String pId = p.getId().toString();
                               pId = pId.replace(" ", "_");
                               NodeBPMN nodeBPMN = new NodeBPMN(pId, p.getLabel(), p.getSwimlaneType().toString());
                               System.out.println("\n Swimlane: " + pId + " " + p.getLabel() + " " + p.getSwimlaneType().toString());
                               nodesBPMN.add(nodeBPMN);
                           }
                       }catch(Exception e4){
                           StringWriter errors = new StringWriter();
                           e4.printStackTrace(new PrintWriter(errors));
                           System.out.println("\nNo Swimlane");
                           e4.printStackTrace();
                       }

                       System.out.println("\n nodesBPMN.size()"+nodesBPMN.size());


                       BPMNNode duplicate;
                       for( BPMNNode node : bpmnDiagram.getNodes() ) {

                           if (node instanceof Gateway) {

                               try {
                                   String nodeId = node.getId().toString();
                                   nodeId = nodeId.replace(" ", "_");
                                   String gatType = ((Gateway) node).getGatewayType().toString();
                                   if (gatType.equals("DATABASED")) gatType = "Exclusive Databased";
                                   gatType=gatType+" Gateway";
                                       NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), gatType );
                                       nodesBPMN.add(nodeBPMN);
                                   //}
                               }catch(Exception e3){
                               StringWriter errors = new StringWriter();
                               e3.printStackTrace(new PrintWriter(errors));
                               e3.printStackTrace();
                           }

                           } else if (node instanceof Activity) {

                               String nodeId = node.getId().toString();
                               nodeId = nodeId.replace(" ", "_");

                               String activityType="Task";
                               boolean x = ((Activity) node).isBLooped();
                               if(x){
                                    activityType+=" Looping";
                               }
                               boolean y = ((Activity) node).isBAdhoc();
                               if(y){
                                   activityType+=" Ad-Hoc";
                               }
                               boolean z = ((Activity) node).isBCompensation();
                               if(z){
                                   activityType+=" Compensation";
                               }
                               boolean t = ((Activity) node).isBMultiinstance();
                               if(t){
                                   activityType+=" Multiinstance";
                               }
                               boolean v = ((Activity) node).isBCollapsed();
                               if(v){
                                   activityType+=" Collapsed";
                               }

                               NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), activityType);
                               nodesBPMN.add(nodeBPMN);

                           } else if (node instanceof Event) {

                               String nodeId = node.getId().toString();
                               nodeId = nodeId.replace(" ", "_");

                               NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), ((Event) node).getEventType().toString() + " Event");
                               nodesBPMN.add(nodeBPMN);

                           }else  if (node instanceof SubProcess) {

                               String nodeId = node.getId().toString();
                               nodeId = nodeId.replace(" ", "_");

                               String subprocessType="SubProcess";
                               boolean x = ((Activity) node).isBLooped();
                               if(x){
                                   subprocessType+=" Looping";
                               }
                               boolean y = ((Activity) node).isBAdhoc();
                               if(y){
                                   subprocessType+=" Ad-Hoc";
                               }
                               boolean z = ((Activity) node).isBCompensation();
                               if(z){
                                   subprocessType+=" Compensation";
                               }
                               boolean t = ((Activity) node).isBMultiinstance();
                               if(t){
                                   subprocessType+=" Multiinstance";
                               }
                               boolean v = ((Activity) node).isBCollapsed();
                               if(v){
                                   subprocessType+=" Collapsed";
                               }

                               NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), subprocessType);
                               nodesBPMN.add(nodeBPMN);
                         }
                       }

                       System.out.println("\n AfternodesBPMN.size()"+nodesBPMN.size());

                       try {

                       for(int i=0; i<guidelinesListResult.size(); i++) {
                           for (NodeBPMN g : nodesBPMN) {
                               if (g.getId().equals(guidelinesListResult.get(i))) {

                                   System.out.println("\nINFONODOBPMN "+g.getId()+g.getName()+g.getType());

                                       String nodeInfo = "The Element with Id " + g.getId();
                                       if (g.getName()!=null) {
                                           if (!g.getName().isEmpty()) {
                                               nodeInfo += " and Label " + g.getName();
                                           }else nodeInfo += " without Label ";
                                       }else nodeInfo += " without Label ";



                                       if (g.getType()!=null) {
                                           if (!g.getType().isEmpty()){
                                               nodeInfo += " of Type " + g.getType();
                                           }else nodeInfo += "";
                                       }else nodeInfo += "";


                                       nodeInfo += " does not meet the guideline";

                                       System.out.println("\ni: "+i+"nodeInfo: "+nodeInfo);

                                   guidelinesListResult.set(i,nodeInfo);


                           }
                           }
                       }
                       }  catch(Exception e5){
                           System.out.println("String nodeInfo = ");
                           StringWriter errors = new StringWriter();
                           e5.printStackTrace(new PrintWriter(errors));
                           e5.printStackTrace();
                       }

                       return guidelinesListResult;

                   }


                   @Override
                   public ArrayList <String> checkGuidelinesBPMNDiagram(
                             BPMNDiagram bpmnDiagram
                   ){

                     //need to convert BPMNDiagram to string
                     BPMNDiagramExporterImpl exporterService = new BPMNDiagramExporterImpl();

                     String modelString=null;
                     try{
                       modelString  = exporterService.exportBPMNDiagram(bpmnDiagram);

                     }catch(Exception eexportBPMNDiagram){
                       System.err.println("Unable to eexportBPMNDiagram: " + eexportBPMNDiagram.getMessage());
                       StringWriter errors = new StringWriter();
                       eexportBPMNDiagram.printStackTrace(new PrintWriter(errors));
                       eexportBPMNDiagram.printStackTrace();
                       ArrayList <String> resultGuideline = null;
                       resultGuideline.add("Error here: exporterService.exportBPMNDiagram(bpmnDiagram)");
                     }
                    ArrayList <String> resultGuideline = new ArrayList <String>();

                     try{

                       resultGuideline = GetBEBoPfromStringModel(modelString);

                         System.out.println("\nDopo resultGuideline = GetBEBoPfromStringModel(modelString);");

                       resultGuideline=GetGuidelinesListBPMN(resultGuideline , bpmnDiagram);
                         System.out.println("\nDopo resultGuideline=GetGuidelinesListBPMN(resultGuideline , bpmnDiagram);");

                     }catch(Exception e2){
                       System.err.println("Unable to GetGuidelinesListBPMN: " + e2.getMessage());
                       StringWriter errors = new StringWriter();
                       e2.printStackTrace(new PrintWriter(errors));
                         e2.printStackTrace();
                     }

                      return resultGuideline;
                   }


    @Override
    public  HashMap<Integer,JSONObject> checkGuidelinesBPMNDiagramJson(
            BPMNDiagram bpmnDiagram
    ){

        //need to convert BPMNDiagram to string
        BPMNDiagramExporterImpl exporterService = new BPMNDiagramExporterImpl();

        String modelString=null;
        try{
            modelString  = exporterService.exportBPMNDiagram(bpmnDiagram);

        }catch(Exception eexportBPMNDiagram){
            System.err.println("Unable to eexportBPMNDiagram: " + eexportBPMNDiagram.getMessage());
            StringWriter errors = new StringWriter();
            eexportBPMNDiagram.printStackTrace(new PrintWriter(errors));
            eexportBPMNDiagram.printStackTrace();

        }

        System.out.println("QUESTOQUI: "+bpmnDiagram);

        ArrayList <String> resultGuideline = new ArrayList <String>();

        HashMap<Integer,JSONObject> resultGuidelineJson=new HashMap<Integer,JSONObject>();
        try{

            resultGuideline = GetBEBoPfromStringModel(modelString);

            System.out.println("\nDopo resultGuideline = GetBEBoPfromStringModel(modelString);");

            resultGuidelineJson=GetGuidelinesListBPMNJson(resultGuideline , bpmnDiagram);
            System.out.println("\nDopo resultGuideline=GetGuidelinesListBPMN(resultGuideline , bpmnDiagram);");

        }catch(Exception e2){
            System.err.println("Unable to GetGuidelinesListBPMN: " + e2.getMessage());
            StringWriter errors = new StringWriter();
            e2.printStackTrace(new PrintWriter(errors));
            e2.printStackTrace();
        }

        return resultGuidelineJson;
    }


    public static HashMap<Integer,JSONObject> GetGuidelinesListBPMNJson(ArrayList<String> guidelinesList , BPMNDiagram bpmnDiagram) throws IOException {

        ArrayList<String> guidelinesListResult = new ArrayList<String>();

        System.out.println("\nguidelinesList: "+guidelinesList);

        guidelinesListResult=guidelinesList;

        ArrayList<NodeBPMN> nodesBPMN = new ArrayList<NodeBPMN>();

        try {
            for (Swimlane p : bpmnDiagram.getSwimlanes()) {
                String pId = p.getId().toString();
                pId = pId.replace(" ", "_");
                NodeBPMN nodeBPMN = new NodeBPMN(pId, p.getLabel(), p.getSwimlaneType().toString());
                //DEBUG
                //System.out.println("\n Swimlane: " + pId + " " + p.getLabel() + " " + p.getSwimlaneType().toString());
                nodesBPMN.add(nodeBPMN);
            }
        }catch(Exception e4){
            StringWriter errors = new StringWriter();
            e4.printStackTrace(new PrintWriter(errors));
            System.out.println("\nNo Swimlane");
            e4.printStackTrace();
        }

        //DEBUG
        //System.out.println("\n nodesBPMN.size()"+nodesBPMN.size());

        for( BPMNNode node : bpmnDiagram.getNodes() ) {

            if (node instanceof Gateway) {

                try {
                    String nodeId = node.getId().toString();
                    nodeId = nodeId.replace(" ", "_");
                    String gatType = ((Gateway) node).getGatewayType().toString();
                    if (gatType.equals("DATABASED")) gatType = "Exclusive Databased";
                    gatType=gatType+" Gateway";
                    NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), gatType );
                    nodesBPMN.add(nodeBPMN);
                }catch(Exception e3){
                    StringWriter errors = new StringWriter();
                    e3.printStackTrace(new PrintWriter(errors));
                    e3.printStackTrace();
                }

            } else if (node instanceof Activity) {

                String nodeId = node.getId().toString();
                nodeId = nodeId.replace(" ", "_");

                String activityType="Task";
                boolean x = ((Activity) node).isBLooped();
                if(x){
                    activityType+=" Looping";
                }
                boolean y = ((Activity) node).isBAdhoc();
                if(y){
                    activityType+=" Ad-Hoc";
                }
                boolean z = ((Activity) node).isBCompensation();
                if(z){
                    activityType+=" Compensation";
                }
                boolean t = ((Activity) node).isBMultiinstance();
                if(t){
                    activityType+=" Multiinstance";
                }
                boolean v = ((Activity) node).isBCollapsed();
                if(v){
                    activityType+=" Collapsed";
                }

                NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), activityType);
                nodesBPMN.add(nodeBPMN);

            } else if (node instanceof Event) {

                String nodeId = node.getId().toString();
                nodeId = nodeId.replace(" ", "_");

                NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), ((Event) node).getEventType().toString() + " Event");
                nodesBPMN.add(nodeBPMN);

            }else  if (node instanceof SubProcess) {

                String nodeId = node.getId().toString();
                nodeId = nodeId.replace(" ", "_");

                String subprocessType="SubProcess";
                boolean x = ((Activity) node).isBLooped();
                if(x){
                    subprocessType+=" Looping";
                }
                boolean y = ((Activity) node).isBAdhoc();
                if(y){
                    subprocessType+=" Ad-Hoc";
                }
                boolean z = ((Activity) node).isBCompensation();
                if(z){
                    subprocessType+=" Compensation";
                }
                boolean t = ((Activity) node).isBMultiinstance();
                if(t){
                    subprocessType+=" Multiinstance";
                }
                boolean v = ((Activity) node).isBCollapsed();
                if(v){
                    subprocessType+=" Collapsed";
                }

                NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), subprocessType);
                nodesBPMN.add(nodeBPMN);
            }
        }

        //DEBUG
        //System.out.println("\n AfternodesBPMN.size()"+nodesBPMN.size());

        //FINITO DI GENERARE I NODE

        try {

            for(int i=0; i<guidelinesListResult.size(); i++) {
                for (NodeBPMN g : nodesBPMN) {
                    if (g.getId().equals(guidelinesListResult.get(i))) {

                        //DEBUG
                        //System.out.println("\nINFONODOBPMN "+g.getId()+g.getName()+g.getType());

                        String nodeInfo = "The Element with Id: " + g.getId();
                        if (g.getName()!=null) {
                            if (!g.getName().isEmpty()) {
                                nodeInfo += " and Label: " + g.getName();
                            }else nodeInfo += "  and Label: ";
                        }else nodeInfo += "  and Label: ";



                        if (g.getType()!=null) {
                            if (!g.getType().isEmpty()){
                                nodeInfo += " of Type: " + g.getType();
                            }else nodeInfo += " of Type: ";
                        }else nodeInfo += " of Type: ";


                        nodeInfo += " does not meet the guideline";
                        //DEBUG
                        //System.out.println("\ni: "+i+"nodeInfo: "+nodeInfo);

                        guidelinesListResult.set(i,nodeInfo);


                    }
                }
            }
        }  catch(Exception e5){
            System.out.println("String nodeInfo = ");
            StringWriter errors = new StringWriter();
            e5.printStackTrace(new PrintWriter(errors));
            e5.printStackTrace();
        }


        JSONObject appoggio = new JSONObject();

        HashMap<Integer,JSONObject> ja=new HashMap<Integer,JSONObject>();


        try {

            int j=0;
            for (int i = 0; i < guidelinesListResult.size(); i++) {

                if (i==0) {
                    appoggio.put("GuidelineName", guidelinesListResult.get(i));
                    if(guidelinesListResult.get(i).contains("Labeling XOR gateway")){
                        appoggio.put("GuidelineDescription", "Make sure (X)OR split gateways are preceded (not necessarily immediately before) by a decision activity, whose outcomes are then modeled by the outgoing sequence flows of the split." +
                                " Sequence flows coming out of diverging gateways of type exclusive, inclusive and complex should be labeled using their associated conditions stated as outcomes.");
                    }else {
                        appoggio.put("GuidelineDescription", guidelinesListResult.get(i + 1));
                    }
                    i=i+1;
                }else {

                    if (guidelinesListResult.get(i).contains("node")) {

                        JSONObject result = new JSONObject(appoggio, JSONObject.getNames(appoggio));
                        result.put("Node", guidelinesListResult.get(i));
                        //DEBUG
                        //System.out.println("\n Result.PUT: "+ result.toString());
                        // System.out.println("\n Node: "+ guidelinesListResult.get(i));
                        ja.put(j,result);

                        j++;


                    } else {

                        if ((i + 1) < guidelinesListResult.size()) {
                            appoggio = new JSONObject();
                            //ja.put(result);
                            appoggio.put("GuidelineName", guidelinesListResult.get(i));
                            if(guidelinesListResult.get(i).contains("Labeling XOR gateway")){
                                appoggio.put("GuidelineDescription", "Make sure (X)OR split gateways are preceded (not necessarily immediately before) by a decision activity, whose outcomes are then modeled by the outgoing sequence flows of the split." +
                                        " Sequence flows coming out of diverging gateways of type exclusive, inclusive and complex should be labeled using their associated conditions stated as outcomes.");
                            }else {
                                appoggio.put("GuidelineDescription", guidelinesListResult.get(i + 1));
                            }
                            i = i + 1;
                        }
                    }
                }
            }

            JSONObject finalResult = new JSONObject();
            finalResult.put("Guidelines",ja);

            //DEBUG
            //System.out.println("\nfinalResult.toString(): "+finalResult.toString());

        }  catch(Exception e6){
            System.out.println("SException e6 ");
            StringWriter errors = new StringWriter();
            e6.printStackTrace(new PrintWriter(errors));
            e6.printStackTrace();
        }

        return ja;

    }



 //CheckGuidelines for right bpmn model Portal Side
 @Override
 public  ArrayList <String> checkGuidelinesBPMN(
         String model, BPMNDiagram bpmnDiagram
 ){


     ArrayList <String> resultGuideline = new ArrayList <String>();

     try{

         resultGuideline = GetBEBoPfromStringModel(model);

         System.out.println("\nDopo resultGuideline = GetBEBoPfromStringModel(modelString);");

         resultGuideline=GetGuidelinesListRealBPMNString(resultGuideline , bpmnDiagram);
         System.out.println("\nDopo resultGuideline=GetGuidelinesListBPMN(resultGuideline , bpmnDiagram);");

     }catch(Exception e2){
         System.err.println("Unable to GetGuidelinesListBPMN: " + e2.getMessage());
         StringWriter errors = new StringWriter();
         e2.printStackTrace(new PrintWriter(errors));
         e2.printStackTrace();
     }

     return resultGuideline;
 }


    public static ArrayList <String> GetGuidelinesListRealBPMNString(ArrayList<String> guidelinesList , BPMNDiagram bpmnDiagram) throws IOException {

        ArrayList<String> guidelinesListResult = new ArrayList<String>();

        //DEBUG
        //System.out.println("\nguidelinesList: "+guidelinesList);

        guidelinesListResult=guidelinesList;

        ArrayList<NodeBPMN> nodesBPMN = new ArrayList<NodeBPMN>();

        try {
            for (Swimlane p : bpmnDiagram.getSwimlanes()) {
                String pId = p.getId().toString();
                pId = pId.replace(" ", "_");
                NodeBPMN nodeBPMN = new NodeBPMN(pId, p.getLabel(), p.getSwimlaneType().toString());
                System.out.println("\n Swimlane: " + pId + " " + p.getLabel() + " " + p.getSwimlaneType().toString());
                nodesBPMN.add(nodeBPMN);
            }
        }catch(Exception e4){
            StringWriter errors = new StringWriter();
            e4.printStackTrace(new PrintWriter(errors));
            System.out.println("\nNo Swimlane");
            e4.printStackTrace();
        }

        //DEBUG
        //System.out.println("\n nodesBPMN.size()"+nodesBPMN.size());


        for( BPMNNode node : bpmnDiagram.getNodes() ) {

            if (node instanceof Gateway) {

                try {
                    String nodeId = node.getId().toString();
                    nodeId = nodeId.replace(" ", "_");
                    String gatType = ((Gateway) node).getGatewayType().toString();
                    if (gatType.equals("DATABASED")) gatType = "Exclusive Databased";
                    gatType=gatType+" Gateway";
                    NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), gatType );
                    nodesBPMN.add(nodeBPMN);
                }catch(Exception e3){
                    StringWriter errors = new StringWriter();
                    e3.printStackTrace(new PrintWriter(errors));
                    e3.printStackTrace();
                }

            } else if (node instanceof Activity) {

                String nodeId = node.getId().toString();
                nodeId = nodeId.replace(" ", "_");

                String activityType="Task";
                boolean x = ((Activity) node).isBLooped();
                if(x){
                    activityType+=" Looping";
                }
                boolean y = ((Activity) node).isBAdhoc();
                if(y){
                    activityType+=" Ad-Hoc";
                }
                boolean z = ((Activity) node).isBCompensation();
                if(z){
                    activityType+=" Compensation";
                }
                boolean t = ((Activity) node).isBMultiinstance();
                if(t){
                    activityType+=" Multiinstance";
                }
                boolean v = ((Activity) node).isBCollapsed();
                if(v){
                    activityType+=" Collapsed";
                }

                NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), activityType);
                nodesBPMN.add(nodeBPMN);

            } else if (node instanceof Event) {

                String nodeId = node.getId().toString();
                nodeId = nodeId.replace(" ", "_");

                NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), ((Event) node).getEventType().toString() + " Event");
                nodesBPMN.add(nodeBPMN);

            }else  if (node instanceof SubProcess) {

                String nodeId = node.getId().toString();
                nodeId = nodeId.replace(" ", "_");

                String subprocessType="SubProcess";
                boolean x = ((Activity) node).isBLooped();
                if(x){
                    subprocessType+=" Looping";
                }
                boolean y = ((Activity) node).isBAdhoc();
                if(y){
                    subprocessType+=" Ad-Hoc";
                }
                boolean z = ((Activity) node).isBCompensation();
                if(z){
                    subprocessType+=" Compensation";
                }
                boolean t = ((Activity) node).isBMultiinstance();
                if(t){
                    subprocessType+=" Multiinstance";
                }
                boolean v = ((Activity) node).isBCollapsed();
                if(v){
                    subprocessType+=" Collapsed";
                }

                NodeBPMN nodeBPMN = new NodeBPMN(nodeId, node.getLabel(), subprocessType);
                nodesBPMN.add(nodeBPMN);
            }
        }

        System.out.println("\n AfternodesBPMN.size()"+nodesBPMN.size());

        //FINITO DI GENERARE I NODE

        try {

            for(int i=0; i<guidelinesListResult.size(); i++) {
                for (NodeBPMN g : nodesBPMN) {
                    if (g.getId().equals(guidelinesListResult.get(i))) {

                        System.out.println("\nINFONODOBPMN "+g.getId()+g.getName()+g.getType());

                        String nodeInfo = "The Element with Id: " + g.getId();
                        if (g.getName()!=null) {
                            if (!g.getName().isEmpty()) {
                                nodeInfo += " and Label: " + g.getName();
                            }else nodeInfo += "  and Label: ";
                        }else nodeInfo += "  and Label: ";



                        if (g.getType()!=null) {
                            if (!g.getType().isEmpty()){
                                nodeInfo += " of Type: " + g.getType();
                            }else nodeInfo += " of Type: ";
                        }else nodeInfo += " of Type: ";


                        nodeInfo += " does not meet the guideline";

                        System.out.println("\ni: "+i+"nodeInfo: "+nodeInfo);

                        guidelinesListResult.set(i,nodeInfo);


                    }
                }
            }
        }  catch(Exception e5){
            System.out.println("String nodeInfo = ");
            StringWriter errors = new StringWriter();
            e5.printStackTrace(new PrintWriter(errors));
            e5.printStackTrace();
        }

        return guidelinesListResult;

    }


    //CheckGuidelines for right bpmn model - Editor side
    @Override
    public  HashMap<Integer,JSONObject> checkGuidelinesBPMNJson(
            String model
    ){


        ArrayList <String> resultGuideline = new ArrayList <String>();

        HashMap<Integer,JSONObject> resultGuidelineJson=new HashMap<Integer,JSONObject>();
        try{

            resultGuideline = GetBEBoPfromStringModel(model);

            //DEBUG
            //System.out.println("\nDopo resultGuideline = GetBEBoPfromStringModel(modelString);");
            //DEBUG
            //System.out.println("resultGuidelineNow"+resultGuideline);

            resultGuidelineJson=GetGuidelinesListBPMNJsonV2(resultGuideline, model);

            //DEBUG
            //System.out.println("\nDopo resultGuideline=GetGuidelinesListBPMN(resultGuideline , bpmnDiagram);");

        }catch(Exception e2){
            System.err.println("Unable to GetGuidelinesListBPMN: " + e2.getMessage());
            StringWriter errors = new StringWriter();
            e2.printStackTrace(new PrintWriter(errors));
            e2.printStackTrace();
        }

        return resultGuidelineJson;
    }


    public static HashMap<Integer,JSONObject> GetGuidelinesListBPMNJsonV2(ArrayList<String> guidelinesList, String inputStringModel) throws IOException {

        ArrayList<String> guidelinesListResult = new ArrayList<String>();

        //DEBUG
        //System.out.println("\nguidelinesList: "+guidelinesList);
        //System.out.println("\nListbpmnDiagram: "+bpmnDiagram);

        guidelinesListResult=guidelinesList;

        ArrayList<NodeBPMN> nodesBPMN = new ArrayList<NodeBPMN>();

        try {
            //scorro gli elementi dall'xml
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            //DEBUG
            //System.out.println("\nBEFOREdBuilder\n");

            Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(inputStringModel.getBytes("utf-8"))));
            //DEBUG
            //System.out.println("\ndoc.getChildNodes().getLength(): "+doc.getChildNodes().getLength()+"\n");


        NodeList process = doc.getElementsByTagName("ns4:process");

        String EdgeName = "";

        //for() processes
            try {
                for (int i = 0; i < process.getLength(); i++) {
                    System.out.println("process: " + process.item(i).getNodeName());
                    //for() process elements
                    for (int y = 0; y < process.item(i).getChildNodes().getLength(); y++) {
                        //DEBUG
					    //System.out.println("childsnodes: "+process.item(i).getChildNodes().item(y).getNodeName());

                        if(process.item(i).getChildNodes().item(y).getNodeName().equals("ns4:laneSet")){
                            //DEBUG
                            //System.out.println("LANESETFOUND: "+process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("id").getNodeValue());
                            NodeBPMN nodeBPMN = new NodeBPMN(process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("id").getNodeValue(),
                                    "",
                                    "lane");
                            nodesBPMN.add(nodeBPMN);
                            //DEBUG
                            //System.out.println("Add LANE");

                        }

                        if (!process.item(i).getChildNodes().item(y).getNodeName().equals("#text")
                               && (!process.item(i).getChildNodes().item(y).getNodeName().equals("ns4:laneSet"))
                                && (!process.item(i).getChildNodes().item(y).getNodeName().equals("ns4:flowNodeRef"))
                                && (!process.item(i).getChildNodes().item(y).getNodeName().equals("ns4:extensionElements"))) {
                            //                if (process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("id").getNodeValue().equals(nodeToColor)) {

                            //DEBUG
                            //System.out.println("Aggiungo Nodo");
                            //System.out.println("QUIORA: "+process.item(i).getChildNodes().item(y));
                            //System.out.println("Aggiunto Nodo");

                            String name = process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("name").getNodeValue();
                            String type = process.item(i).getChildNodes().item(y).getNodeName();
                            type = type.replace("ns4:", "");
                            String id = process.item(i).getChildNodes().item(y).getAttributes().getNamedItem("id").getNodeValue();

                            //DEBUG
                            //System.out.println("\nName:" + name);
                            //System.out.println("\nType:" + type);
                            //System.out.println("\nId: " + id);

                            NodeBPMN nodeBPMN = new NodeBPMN(id,
                                    name,
                                    type);
                            nodesBPMN.add(nodeBPMN);


                        }
                    }
                }

                System.out.println("\n AfternodesBPMN.size()" + nodesBPMN.size());
            }catch(Exception e5){
                StringWriter errors = new StringWriter();
                e5.printStackTrace(new PrintWriter(errors));
                System.out.println("\nRead XML error");
                e5.printStackTrace();
            }


        }catch(Exception e4){
            StringWriter errors = new StringWriter();
            e4.printStackTrace(new PrintWriter(errors));
            System.out.println("\nDocumentBuilder error");
            e4.printStackTrace();
        }

        //NodeBPMN generation ended

        try {

            for(int i=0; i<guidelinesListResult.size(); i++) {
                for (NodeBPMN g : nodesBPMN) {
                    //DEBUG
                    //System.out.println("\nINFONODOBPMN "+g.getId()+g.getName()+g.getType());

                    if (g.getId().equals(guidelinesListResult.get(i))) {
                        //DEBUG
                        //System.out.println("\nINFONODOBPMN "+g.getId()+g.getName()+g.getType());

                        String nodeInfo = "The Element with Id: " + g.getId();
                        if (g.getName()!=null) {
                            if (!g.getName().isEmpty()) {
                                nodeInfo += " and Label: " + g.getName();
                            }else nodeInfo += "  and Label: ";
                        }else nodeInfo += "  and Label: ";



                        if (g.getType()!=null) {
                            if (!g.getType().isEmpty()){
                                nodeInfo += " of Type: " + g.getType();
                            }else nodeInfo += " of Type: ";
                        }else nodeInfo += " of Type: ";


                        nodeInfo += " does not meet the guideline";

                        //DEBUG
                        //System.out.println("\ni: "+i+"nodeInfo: "+nodeInfo);

                        guidelinesListResult.set(i,nodeInfo);

                    }
                }
            }
        }  catch(Exception e5){
            System.out.println("String nodeInfo = ");
            StringWriter errors = new StringWriter();
            e5.printStackTrace(new PrintWriter(errors));
            e5.printStackTrace();
        }

        HashMap<Integer,JSONObject> ja=new HashMap<Integer,JSONObject>();
        JSONObject flagJson = new JSONObject();

        try {

            //DEBUG
            //System.out.println("\n guidelinesListResultSIZEEEEEE: "+ guidelinesListResult.size());


            int j=0;
            for (int i = 0; i < guidelinesListResult.size(); i++) {
                //DEBUG
                System.out.println("\n guidelinesListResult.get(i): "+ guidelinesListResult.get(i));


                if (i==0) {
                    flagJson.put("GuidelineName", guidelinesListResult.get(i));
                    if(guidelinesListResult.get(i).contains("Labeling XOR gateway")){
                        flagJson.put("GuidelineDescription", "Make sure (X)OR split gateways are preceded (not necessarily immediately before) by a decision activity, whose outcomes are then modeled by the outgoing sequence flows of the split." +
                                " Sequence flows coming out of diverging gateways of type exclusive, inclusive and complex should be labeled using their associated conditions stated as outcomes.");
                    }else {
                        flagJson.put("GuidelineDescription", guidelinesListResult.get(i + 1));
                    }
                    i=i+1;
                }else {

                    if (guidelinesListResult.get(i).contains("node")||guidelinesListResult.get(i).contains("sid")) {
                        //result = flagJson;
                        JSONObject result = new JSONObject(flagJson, JSONObject.getNames(flagJson));

                        result.put("Node", guidelinesListResult.get(i));

                        ja.put(j,result);

                        j++;


                    } else {

                        if ((i + 1) < guidelinesListResult.size()) {
                            flagJson = new JSONObject();
                            flagJson.put("GuidelineName", guidelinesListResult.get(i));
                            System.out.println("GuidelineName"+ guidelinesListResult.get(i));
                            if(guidelinesListResult.get(i).contains("Labeling XOR gateway")){
                                flagJson.put("GuidelineDescription", "Make sure (X)OR split gateways are preceded (not necessarily immediately before) by a decision activity, whose outcomes are then modeled by the outgoing sequence flows of the split." +
                                        " Sequence flows coming out of diverging gateways of type exclusive, inclusive and complex should be labeled using their associated conditions stated as outcomes.");
                            }else {
                                flagJson.put("GuidelineDescription", guidelinesListResult.get(i + 1));
                            }
//                            flagJson.put("GuidelineDescription", guidelinesListResult.get(i + 1));
                            i = i + 1;
                        }
                    }
                }
            }

            //DEBUG
            //for (HashMap.Entry entry : ja.entrySet()) {
            //    System.out.println(entry.getKey() + ", " + entry.getValue());
            //}
            //DEBUG
            //for(int k=0;k<ja.size();k++)System.out.println("\n OGNI OGGETTO SALVATO: "+k+" "+ja.get(k).toString());


            JSONObject finalResult = new JSONObject();
            finalResult.put("Guidelines",ja);
            //DEBUG
            //System.out.println("\nfinalResult.toString(): "+finalResult.toString());


        }  catch(Exception e6){
            System.out.println("SException e6 ");
            StringWriter errors = new StringWriter();
            e6.printStackTrace(new PrintWriter(errors));
            e6.printStackTrace();
        }

        return ja;

    }





}
