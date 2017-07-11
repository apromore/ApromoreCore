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

package org.apromore.plugin.editor.bebop;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

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
import org.apromore.service.bebop.BebopService;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;


//added for IDS
import org.oryxeditor.server.diagram.generic.GenericDiagram;
import org.oryxeditor.server.diagram.generic.GenericEdge;
import org.oryxeditor.server.diagram.generic.GenericShape;
import org.apromore.plugin.editor.bebop.*;

import org.apromore.service.ProcessService;

public class BebopServlet extends HttpServlet {

    @Inject
    private ProcessService processService;
    private BPMNDiagramImporter importerService;

    private static final Logger LOGGER = Logger.getLogger(BebopServlet.class);

    private BebopService bebopService;

    public void init(ServletConfig config) throws ServletException {
        importerService = (BPMNDiagramImporter)
            WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
                                      .getAutowireCapableBeanFactory()
                                      .getBean("importerService");
        bebopService = (BebopService)
            WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
                                      .getAutowireCapableBeanFactory()
                                      .getBean("bebopService");
    }



    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String jsonData = request.getParameter("data");


        /* Transform and return */
        try {
            if (jsonData == null || jsonData.isEmpty()) {
                response.setStatus(500);
                response.setContentType("text/plain; charset=UTF-8");
                response.getWriter().write("Empty or missing parameter 'data'!");
            } else {
                String bebop = convert2(jsonData,request);
                //DEBUG
                //System.out.println("convert2(jsonData)"+convert2(jsonData));
                response.setContentType("application/json");
                response.setStatus(200);
                response.getWriter().write(bebop);
            }
        } catch (Exception e) {
            try {
                LOGGER.error("Measurement failed: " + e.toString(), e);
                JSONObject json = new JSONObject();
                json.put("errors", e.toString());
                response.setStatus(500);
                response.setContentType("text/plain; charset=UTF-8");
            } catch (Exception e1) {
                LOGGER.error("Unable to report servlet error to JSON: " + e1.toString(), e1);
            }
        }
    }


   private String convert2(String jsonData, HttpServletRequest request) throws Exception {


       BasicDiagram basicDiagram = BasicDiagramBuilder.parseJson(jsonData);
       if (basicDiagram == null) {
           return null;
       }
       assert basicDiagram != null;

       // Signavio Diagram -> BPMN DOM
       Diagram2BpmnConverter diagram2BpmnConverter = new Diagram2BpmnConverter(basicDiagram, AbstractBpmnFactory.getFactoryClasses());
       Definitions bpmn = diagram2BpmnConverter.getDefinitionsFromDiagram();

       // BPMN DOM -> BPMN-formatted String
       JAXBContext jaxbContext = JAXBContext.newInstance(  Definitions.class, Configurable.class,
               ConfigurationAnnotationAssociation.class,
               ConfigurationAnnotationShape.class, Variants.class );

       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       jaxbContext.createMarshaller().marshal(bpmn, baos);
       String process = baos.toString("utf-8");
       //DEBUG
       //System.out.println("\nPROCESS: "+process);


        /* result already in json format */
        HashMap <Integer,JSONObject> bebop = bebopService.checkGuidelinesBPMNJson(process);

        HashMap <Integer,JSONObject> bebop2 = new HashMap <Integer ,JSONObject>();

        int key=0;
        for (HashMap.Entry entry : bebop.entrySet()) {
            JSONObject jsnObjFlag = (JSONObject) entry.getValue();

            String node=null, id=null, type=null, label=null, gName=null, gDesc=null;
//
            gName=jsnObjFlag.getString("GuidelineName");
            //DEBUG
            //A way to possibly skip some guidelines
            //if(gName.equals("Exclusive Gateway Marking"))continue;
            //if(gName.equals("Labeling Converging Gateways"))continue;
            gDesc=jsnObjFlag.getString("GuidelineDescription");

            JSONObject result = new JSONObject();
            result.put("GuidelineName",gName);
            result.put("GuidelineDescription",gDesc);

            //DEBUG
            //System.out.println("\n jsnObjFlag.getString: "+jsnObjFlag.getString("Node"));

            node=jsnObjFlag.getString("Node");
            //DEBUG
            //System.out.println("\n NODE: "+node.toString());

            if(node.contains(":")) {
                String[] nodeInfo = node.split(": ");
                for (int i = 0; i < nodeInfo.length; i++)
                    System.out.println("\n nodeInfo: i" + i + "nodeInfo: " + nodeInfo[i]);

                //DEBUG
                //System.out.println("\n nodeInfo[1]: "+nodeInfo[1]);
                //System.out.println("\n nodeInfo[1]: "+nodeInfo[2]);
                //System.out.println("\n nodeInfo[1]: "+nodeInfo[3]);

                id = nodeInfo[1];

                //DEBUG
                //System.out.println("\n id=nodeInfo[1]: "+id);
                String[] idS = id.split("and Label");
                //DEBUG
                //for(int i=0;i<idS.length;i++) System.out.println("\n ID: i"+i+"valore: "+idS[i]);
                id = idS[0];
                id = id.replaceAll("\\s+", "");

                label = nodeInfo[2];
                //DEBUG
                //System.out.println("\n label=nodeInfo[2]: "+label);
                String[] labelS = label.split("of Type");
                label = labelS[0];
                //label=label.replaceAll("\\s+","") ;

                //DEBUG
                //for(int i=0;i<labelS.length;i++) System.out.println("\n LABEL: i"+i+"valore: "+labelS[i]);

                type = nodeInfo[3];
                String[] typeS = type.split("does not meet the guideline");
                //DEBUG
                //for(int i=0;i<typeS.length;i++) System.out.println("\n TYPEf: i"+i+"valore: "+typeS[i]);
                type = typeS[0];
                type = type.replaceAll("\\s+", "");
                //DEBUG
                //System.out.println("\n STRINGSPLIT: "+id+label+type);
                //System.out.println("\n STRINGSPLITID: "+id);
                //System.out.println("\n STRINGSPLITLABEL: "+label);
                result.put("NodeID", id);
                result.put("NodeLabel", label);
                result.put("NodeType", type);
            }else{
                if(!node.isEmpty()){
                    result.put("NodeID", node);
                    result.put("NodeLabel", "");
                    result.put("NodeType", "swimlane");
                }
            }

            bebop2.put(key,result);
            key++;

        }


        //OK json format: GName , GDescr, Node
        JSONArray jaGuid = new JSONArray();
        for (HashMap.Entry entry : bebop2.entrySet()) {
            JSONObject jsnObjFlag = (JSONObject) entry.getValue();
            jaGuid.put(jsnObjFlag);
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }

        JSONObject finalResult = new JSONObject(jaGuid);
        finalResult.put("Guidelines",jaGuid);

       //DEBUG
        //System.out.println("\nfinalResult.toString(): "+finalResult.toString());
        return finalResult.toString();


    }

}
