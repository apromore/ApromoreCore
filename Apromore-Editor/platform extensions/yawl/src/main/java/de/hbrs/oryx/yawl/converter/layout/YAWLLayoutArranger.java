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

package de.hbrs.oryx.yawl.converter.layout;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * Created by Pasquale Napoli on 1/08/14.
 */
public class YAWLLayoutArranger {

    private StringBuilder sb = new StringBuilder();

    public Element arrangeLayout(Element root) throws JDOMException,
            IOException {
        sb=new StringBuilder();
        sb.append("<layout>");
        sb.append("<specification id=\"Initial\">");
        sb.append("<size w=\"1000\" h=\"1000\"/>");

        List<Element> specifications=root.getChildren("specification", root.getNamespace());
        for (Element specification : specifications) {
            List<Element> decompositions = specification.getChildren("decomposition", root.getNamespace());
            for (Element yawlDecomposition : decompositions) {
                    createNet(yawlDecomposition);
            }
        }
        sb.append("<labelFontSize>12</labelFontSize>");
        sb.append("</specification>");
        sb.append("</layout>");

        String XMLLayoutContent=sb.toString();

        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(XMLLayoutContent));
        return document.getRootElement();
    }

    private void createNet(Element yawlDecomposition) {
        String isRootNet = yawlDecomposition.getAttributeValue("isRootNet");
        if (Boolean.parseBoolean(isRootNet)) {
            String id = yawlDecomposition.getAttributeValue("id");
            sb.append("<net id=\"" + id + "\">");
            createFakeXYWH();
            Element yawlProcessControlElements=yawlDecomposition.getChild("processControlElements",yawlDecomposition.getNamespace());
            if(yawlProcessControlElements!=null){
                createVertexesAndFlows(yawlProcessControlElements);
            }
            sb.append("</net>");
        }

    }



    private void createVertexesAndFlows(Element yawlProcessControlElements) {
        List<Element> vertexes=new LinkedList<Element>();
        vertexes.addAll(yawlProcessControlElements.getChildren());
        List<String> flowOpenTags=new LinkedList<String>();
        for (Element element : vertexes) {
            sb.append("<vertex id=\""+element.getAttributeValue("id")+"\">");
            createFakeAttributes();
            sb.append("</vertex>");
            flowOpenTags.addAll(searchFlows(element));
        }
        for (String flowOpenTag : flowOpenTags) {
            createFlow(flowOpenTag);
        }

    }


    private void createFlow(String flowOpenTag) {
        sb.append(flowOpenTag);
        createFlowFakeContent();
        sb.append("</flow>");

    }

    private void createFlowFakeContent() {
        sb.append("<ports in=\"14\" out=\"14\"/>");
        sb.append("<attributes>");
        sb.append("<lineStyle>11</lineStyle>");
        sb.append("<points>");
        sb.append("<value x=\"0\" y=\"0\"/>");
        sb.append("<value x=\"0\" y=\"0\"/>");
        sb.append("</points>");
        sb.append("</attributes>");
    }

    private List<String> searchFlows( Element element) {
        List<String> res=new LinkedList<String>();
        String sourceId=element.getAttributeValue("id"),targetId;
        List<Element> flowsIntos=element.getChildren("flowsInto",element.getNamespace());
        for (Element flowInto : flowsIntos) {
            List<Element> nextElementRefs=flowInto.getChildren("nextElementRef",element.getNamespace());
            for (Element nextElementRef : nextElementRefs) {
                targetId=nextElementRef.getAttributeValue("id");
                res.add("<flow source=\""+sourceId+"\" target=\""+targetId+"\">");
            }
        }
        return res;
    }

    private void createFakeAttributes() {
        sb.append("<attributes>");
        sb.append("<bounds x=\"0\" y=\"0\" w=\"32\" h=\"32\"/>");
        sb.append("</attributes>");
    }

    private void createFakeXYWH() {
        sb.append("<bounds x=\"0\" y=\"0\" w=\"1000\" h=\"1000\" />"
                + "<frame x=\"0\" y=\"0\" w=\"1000\" h=\"1000\" />"
                + "<viewport x=\"0\" y=\"0\" w=\"1000\" h=\"1000\" />");
    }

}
