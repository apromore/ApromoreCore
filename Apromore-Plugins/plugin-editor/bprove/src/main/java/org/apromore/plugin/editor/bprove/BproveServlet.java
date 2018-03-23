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

package org.apromore.plugin.editor.bprove;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.nio.charset.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.validation.SchemaFactory;

import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;

// Java 2 Standard
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

// Java 2 Enterprise
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

// Third party
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.springframework.web.context.support.WebApplicationContextUtils;

// Apromore
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.bprove.BproveService;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;

//added for IDS
import org.oryxeditor.server.diagram.generic.GenericDiagram;
import org.oryxeditor.server.diagram.generic.GenericEdge;
import org.oryxeditor.server.diagram.generic.GenericShape;
import org.apromore.plugin.editor.bprove.*;

import org.apromore.service.ProcessService;
//grafics import
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.script.*;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;
//import plugin.bpmn.to.maude.handlers.MaudeOperationEditor;

//import plugin.bpmn.to.maude.handlers.MaudeOperation;

public class BproveServlet extends HttpServlet {

    @Inject
    private ProcessService processService;
    private BPMNDiagramImporter importerService;

    private static final Logger LOGGER = Logger.getLogger(BproveServlet.class);

    private BproveService bproveService;

    private String originalModelhigh=null;
    private String parsedModelhigh=null;
     private String parsedModelToBeUsed=null;
    
    private String counterexample=null;
    private String traceResult="tutto ok";
    private String stoppedservice=null;
    private String propertyToVerify=null;
    private JFrame f;

    private String param = null;
    private String poolName1 = null;
    private String poolName2 = null;
    private String taskName1 = null;
    private String taskName2 = null;
    private String msgName = null;

    public void setMsgName (String string){
        msgName=string;}

        public String getMsgName (){
            return msgName;}            


    public void setParsedModelToBeUsed (String string){
        parsedModelToBeUsed=string;}

        public String getParsedModelToBeUsed (){
            return parsedModelToBeUsed;}


    public void setParameterized (String string){
        param=string;}

        public String getParameterized (){
            return param;}

    public void setPoolName1 (String string){
        poolName1=string;}

    public String getPoolName1 (){
        return poolName1;}

    public void setPoolName2 (String string){
        poolName2=string;}
    
    public String getPoolName2 (){
        return poolName2;}

    public void setTaskName1 (String string){
        taskName1=string;}
            
    public String getTaskName1 (){
        return taskName1;}

    public void setTaskName2 (String string){
        taskName2=string;}
                    
    public String getTaskName2 (){
        return taskName2;}            





    public void setStoppedService (String string){
        stoppedservice=string;}

    public void setPropertyToVerify (String string){
        propertyToVerify=string;}

    public String getPropertyToVerify (){
        return propertyToVerify;}

    public String getStoppedService (){
        return stoppedservice;}

    public void settraceResult (String string){
        traceResult=string;}

    public String gettraceResult (){
        return traceResult;}

    public void setoriginalModelhigh (String string){
        originalModelhigh=string;}

    public String getoriginalModelhigh (){
        return originalModelhigh;}

    public void setparsedModelhigh (String string){
        parsedModelhigh=string;}

    public String getparsedModelhigh (){
        return parsedModelhigh;}

    public void setcounterexample (String string){
        counterexample=string;}

    public String getcounterexample (){
        return counterexample;}

    public void init(ServletConfig config) throws ServletException {
        importerService = (BPMNDiagramImporter)
            WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
                                      .getAutowireCapableBeanFactory()
                                      .getBean("importerService");
        bproveService = (BproveService)
            WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
                                      .getAutowireCapableBeanFactory()
                                      .getBean("bproveService");
    }



    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //String jsonData = request.getParameter("data");

        //System.out.println("\nHandling Post\n");
        String jsonData = request.getParameter("data");
        String propertyToVerify = request.getParameter("property");
        //String propertyToVerify = "Can a Process End?";
        setPropertyToVerify(propertyToVerify);

        //System.out.println("\nparam\n");
        String param = request.getParameter("param");
        //System.out.println(param);
        setParameterized(param);
        //String poolName1 = request.getParameter("poolName1");
        
        try{
            String parsedModelToBeUsed = request.getParameter("parsedModel");
            setParsedModelToBeUsed(parsedModelToBeUsed);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        //if((poolName1!=null)&&(poolName1!="")&&(poolName1!=" ")){
            if(param.equals("true")){
                
                //vedo quali paramentri ci sono
                try{
                    String poolName1 = request.getParameter("poolName1");
                    setPoolName1(poolName1);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                try{    
                    String poolName2 = request.getParameter("poolName2");
                    setPoolName2(poolName2);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                try{    
                    String taskName1 = request.getParameter("taskName1");
                    setTaskName1(taskName1);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
                try{    
                    String taskName2 = request.getParameter("taskName2");
                    setTaskName2(taskName2);
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
                try{    
                    String msgName = request.getParameter("msgName");
                    setMsgName(msgName);
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
                //System.out.println("\nparam true dentro if param = true\n");
            }

                try {
                    if (jsonData == null || jsonData.isEmpty()) {
                        response.setStatus(500);
                        response.setContentType("text/plain; charset=UTF-8");
                        response.getWriter().write("Empty or missing parameter 'data'!");
                    } else {
                        String bprove = parseModel(jsonData,request);
                        //JOptionPane.showMessageDialog(null, "\nbprove\n"+bprove);
                        //DEBUG
                        //System.out.println("\nbprove\n"+bprove);
                        response.setContentType("application/json");
                        response.setStatus(200);
                        response.getWriter().write(bprove);}
                    }catch (Exception e) {
                        try {
                            LOGGER.error("parseModel(jsonData,request); failed: " + e.toString(), e);
                            JSONObject json = new JSONObject();
                            json.put("errors", e.toString());
                            response.setStatus(500);
                            response.setContentType("text/plain; charset=UTF-8");
                        } catch (Exception e1) {
                            LOGGER.error("Unable to report servlet error to JSON: " + e1.toString(), e1);
                        }
                }
     
    }

    
   private String parseModel(String jsonData, HttpServletRequest request) throws Exception {

    //System.out.println("\nInside parseModel \n");
    String result=null;

    

    if(getPropertyToVerify().equals("getParsedModel")){

        BasicDiagram basicDiagram = BasicDiagramBuilder.parseJson(jsonData);
        if (basicDiagram == null) {
            return null;
        }
        assert basicDiagram != null;

        // Signavio Diagram -> BPMN DOM
        Diagram2BpmnConverter diagram2BpmnConverter = new Diagram2BpmnConverter(basicDiagram, AbstractBpmnFactory.getFactoryClasses());
        Definitions bpmn = diagram2BpmnConverter.getDefinitionsFromDiagram();

        // BPMN DOM -> BPMN-formatted String
        JAXBContext jaxbContext = JAXBContext.newInstance(Definitions.class, Configurable.class,
                ConfigurationAnnotationAssociation.class,
                ConfigurationAnnotationShape.class, Variants.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jaxbContext.createMarshaller().marshal(bpmn, baos);
        String process = baos.toString("utf-8");

        
        //JOptionPane.showMessageDialog(null, "\nprocess\n"+process);
        // File file = new File("/Users/user/Desktop/BPMNMODELFROMFACADE"+".txt");
        // FileWriter fw = null;
        // try {
        //   fw = new FileWriter(file);
        //   fw.write(process);	
        //   fw.flush();
        // } catch (IOException e2) {
        //     e2.printStackTrace();
        // }



        //System.out.println("\nprocess \n"+process);
        setoriginalModelhigh(process);
        
        String parsedModel = bproveService.getParsedModelBprove(process);
        setparsedModelhigh(parsedModel);

        if (parsedModel.contains("The model presents the following ineligible BPMN elements")){
            result=result+"$$$ParsedModel$$$"+parsedModel;
            return result;
        }

        //System.out.println("\nBefore MaudeOperationEditor \n");
        //per ritornare il risultato
        result = bproveService.getMaudeOperation(getoriginalModelhigh(),getparsedModelhigh(),getPropertyToVerify(),getParameterized(), getPoolName1(),getPoolName2(),getTaskName1(),getTaskName2(),getMsgName());

        result=result+"$$$ParsedModel$$$"+parsedModel;
        
    }else{

        result = bproveService.getMaudeOperation(getoriginalModelhigh(),getParsedModelToBeUsed(),getPropertyToVerify(),getParameterized(), getPoolName1(),getPoolName2(),getTaskName1(),getTaskName2(),getMsgName());
        // JOptionPane.showMessageDialog(null, "\ngetPropertyToVerify()\n"+getPropertyToVerify());
        // JOptionPane.showMessageDialog(null, "\ngetParameterized()\n"+getParameterized());
        // JOptionPane.showMessageDialog(null, "\ngetPoolName1()\n"+getPoolName1());
        // JOptionPane.showMessageDialog(null, "\ngetPoolName2()\n"+getPoolName2());
        // JOptionPane.showMessageDialog(null, "\ngetTaskName1()\n"+getTaskName1());
        // JOptionPane.showMessageDialog(null, "\ngetTaskName2()\n"+getTaskName2());
        // JOptionPane.showMessageDialog(null, "\ngetMsgName()\n"+getMsgName());


    }

    return result;



  }

}
