/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package de.hbrs.oryx.yawl.converter.layout;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.yawlfoundation.yawl.util.JDOMUtil;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.layout.NetElementLayout.DecoratorType;
import de.hbrs.oryx.yawl.util.YAWLMapping;

/**
 * Converts the layout information stored in a YAWL XML file.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class YAWLLayoutConverter {

    private static final Bounds DEFAULT_FLOW_BOUNDS = new Bounds(new Point(0.0, 0.0), new Point(0.0, 0.0));

    /**
     * Namespaces of YAWL specification
     */
    private Namespace yawlNamespace;

    /**
     * Locale of YAWL XML file
     */
    private Locale yawlLocale;

    /**
     * Correct number formatter for YAWL locale
     */
    private NumberFormat numberFormatter;

    /**
     * YAWL specification to be converted
     */
    private final String specificationString;

    /**
     * JDOM Element of the YAWL layout
     */
    private Element layout;

    /**
     * Conversion context valid for one conversion
     */
    private final YAWLConversionContext context;

    /**
     * Provide a default layout when missing
     */
    private final YAWLLayoutArranger yawlLayoutArranger;

    /**
     * Creates the layout converter for the given specification.
     * 
     * @param specificationString
     *            from YAWL XML
     * @param context
     *            in which the layout information will be stored
     * @throws JDOMException
     * @throws IOException
     */
    public YAWLLayoutConverter(final String specificationString, final YAWLConversionContext context) throws JDOMException, IOException {
        super();
        this.specificationString = specificationString;
        this.context = context;
        yawlLayoutArranger=new YAWLLayoutArranger();
        initXMLReader();
    }

    private void initXMLReader() throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new StringReader(specificationString));
        Element root = document.getRootElement();
        layout = root.getChild("layout", root.getNamespace());
        if (layout == null)
            layout = yawlLayoutArranger.arrangeLayout(root);
        yawlNamespace = layout.getNamespace();
        setYAWLLocale(layout);
    }

    private void setYAWLLocale(final Element e) {
        Element eLocale = e.getChild("locale", yawlNamespace);
        if (eLocale != null) {
            String language = eLocale.getAttributeValue("language");
            String country = eLocale.getAttributeValue("country");
            yawlLocale = new Locale(language, country);
        } else {
            yawlLocale = Locale.getDefault();
        }

        numberFormatter = NumberFormat.getInstance(yawlLocale);
    }

    private double parseDouble(final String d) {
        try {
            return numberFormatter.parse(d).doubleValue();
        } catch (Exception pe) {
            context.addConversionWarnings("Could not convert to Double, returning 1 instead. " + d, pe);
            return 1;
        }
    }

    /**
     * Parse and convert information about the layout of the YAWL nets. These information are stored separate from the decompositions in a YAWL
     * specification.
     * 
     * @throws JDOMException
     * @throws IOException
     */
    public void convertLayout() throws JDOMException, IOException {

        Element spec = layout.getChild("specification", yawlNamespace);

        if (spec != null) {
            @SuppressWarnings("rawtypes")
            // JDOM uses Raw Lists, but we check with "instanceof"
            List nets = spec.getChildren("net", yawlNamespace);
            for (Object netObject : nets) {
                // YAWL Nets
                if (netObject instanceof Element) {
                    Element yawlNet = (Element) netObject;
                    NetLayout netLayout = convertNetLayout(yawlNet);

                    // Vertexes are Conditions or Tasks of a YAWL Net without a
                    // Label or Decorator
                    for (Object vertexObject : yawlNet.getChildren("vertex", yawlNamespace)) {
                        convertVertexLayout(netLayout, (Element) vertexObject);
                    }

                    // Containers may be Conditions or Tasks of a YAWL Net
                    for (Object containterObject : yawlNet.getChildren("container", yawlNamespace)) {
                        convertContainerLayout(netLayout, (Element) containterObject);

                    }

                    // Flows for a YAWL Net, should be read as last because it
                    // uses the Join/Split decorator information of the task
                    // containers
                    for (Object flowObject : yawlNet.getChildren("flow", yawlNamespace)) {
                        convertFlowLayout(netLayout, (Element) flowObject);
                    }

                }
            }
        }
    }

    /**
     * Converts the YAWL net layout bounds
     * 
     * <pre>
     * <net id="">
     *   	<bounds x="" y="" w="" h="" />
     *   	<frame x="" y="" w="" h="" />
     *   	<viewport x="" y="0" w="" h="" />
     *   	<vertex> .. </vertex>
     *   	...
     *  </net>
     * </pre>
     * 
     * @param yawlNet
     * @return
     */
    private NetLayout convertNetLayout(final Element yawlNet) {
        String yawlId = yawlNet.getAttributeValue("id");
        Element yawlBounds = yawlNet.getChild("bounds", yawlNamespace);
        Bounds bounds = convertToOryxBounds(fixNetBoundsElement(yawlBounds), 0.0, 0.0);
        NetLayout netLayout = new NetLayout(bounds);
        context.putNetLayout(yawlId, netLayout);
        return netLayout;
    }

    /**
     * If the viewport inside YAWL Editor is adjusted, then YAWL stores negative values in "bounds" element. For Oryx this has to be adjusted, simply
     * by setting each negative value to zero!
     * 
     * @param boundsElement
     *            original "bounds" element of YAWL Net
     * @return Element containing only positive dimensions
     */
    private Element fixNetBoundsElement(final Element boundsElement) {
        Element clonedElement = boundsElement.clone();
        clonedElement.setAttribute("x", fixBoundsAttribute(boundsElement.getAttributeValue("x")));
        clonedElement.setAttribute("y", fixBoundsAttribute(boundsElement.getAttributeValue("y")));
        clonedElement.setAttribute("h", fixBoundsAttribute(boundsElement.getAttributeValue("h")));
        clonedElement.setAttribute("w", fixBoundsAttribute(boundsElement.getAttributeValue("w")));
        return clonedElement;
    }

    private String fixBoundsAttribute(final String attr) {
        return numberFormatter.format((Math.max(0, parseDouble(attr))));
    }

    /**
     * Converts a YAWL Vertex, which may be a condition or a task.
     * 
     * <pre>
     * <vertex id="XY">
     *     <attributes>
     *     </attributes>
     * </vertex>
     * </pre>
     * 
     * @param netLayout
     * @param yawlVertex
     */
    private void convertVertexLayout(final NetLayout netLayout, final Element yawlVertex) {
        if (isCondition(yawlVertex.getAttributeValue("id"))) {
            convertConditionLayout(netLayout, null, yawlVertex);
        } else {
            convertTaskLayout(netLayout, null, yawlVertex);
        }
    }

    /**
     * Converts a YAWL Container, which may be a condition or a task.
     * 
     * <pre>
     * <container id="XY">
     *   <vertex>
     *   </vertex>
     *   <decorator type="XYJoin">
     *   </decorator>
     *   <decorator type="XYSplit">
     *   </decorator>
     * </container>
     * </pre>
     * 
     * @param netLayout
     * @param yawlContainer
     */
    private void convertContainerLayout(final NetLayout netLayout, final Element yawlContainer) {

        Element yawlVertex = yawlContainer.getChild("vertex", yawlNamespace);
        if (yawlVertex != null) {
            if (isCondition(yawlContainer.getAttributeValue("id"))) {
                convertConditionLayout(netLayout, yawlContainer, yawlVertex);
            } else {
                convertTaskLayout(netLayout, yawlContainer, yawlVertex);
            }
        } else {
            // Should not happen. TODO: Log warning
        }
    }

    /**
     * Converts the layout of a YAWL condition from a vertex or container element
     * 
     * @param netLayout
     * @param yawlContainer
     *            may be NULL if the condition has no label
     * @param yawlVertex
     *            containing the bounds of the condition
     */
    private void convertConditionLayout(final NetLayout netLayout, final Element yawlContainer, final Element yawlVertex) {
        Element yawlVertexBounds = yawlVertex.getChild("attributes", yawlNamespace).getChild("bounds", yawlNamespace);
        NetElementLayout layoutInformation = new NetElementLayout(true);
        layoutInformation.setBounds(convertToOryxBounds(yawlVertexBounds, 0.0, 0.0));
        if (yawlContainer != null) {
            netLayout.putVertexLayout(yawlContainer.getAttributeValue("id"), layoutInformation);
        } else {
            netLayout.putVertexLayout(yawlVertex.getAttributeValue("id"), layoutInformation);
        }

    }

    /**
     * Converts the layout of a YAWL task from container element
     * 
     * @param netLayout
     * @param yawlContainer
     *            may be NULL if the task has no labels and decorators
     * @param yawlVertex
     *            bounds of the vertex element inside the container
     */
    private void convertTaskLayout(final NetLayout netLayout, final Element yawlContainer, final Element yawlVertex) {
        Element yawlVertexBounds = yawlVertex.getChild("attributes", yawlNamespace).getChild("bounds", yawlNamespace);
        NetElementLayout layoutInformation = new NetElementLayout(false);
        // Task has to be adjusted to Oryx coordinates,
        // as the JOIN/SPLIT decorator is part of the BasicShape in Oryx
        layoutInformation.setBounds(convertToOryxBounds(yawlVertexBounds, 24.0, 24.0));
        layoutInformation.setIconPath(convertIconPath(yawlVertex));
        if (yawlContainer != null) {
            convertDecorator(yawlContainer, layoutInformation);
            netLayout.putVertexLayout(yawlContainer.getAttributeValue("id"), layoutInformation);
        } else {
            netLayout.putVertexLayout(yawlVertex.getAttributeValue("id"), layoutInformation);
        }
    }

    /**
     * Extracts the information about the iconPath.
     * 
     * @param yawlVertex
     * @return IconPath as String or Empty String
     */
    private String convertIconPath(final Element yawlVertex) {
        if (yawlVertex.getChild("iconpath", yawlNamespace) != null) {
            return yawlVertex.getChildText("iconpath", yawlNamespace);
        }
        return "";
    }

    /**
     * Converts the all decorators of a YAWL task. There may be two decorators, each with alignment TOP, LEFT, RIGHT, BOTTOM.
     * 
     * @param yawlContainer
     *            the container element of the YAWL task
     * @param layoutInformation
     *            already converted layout of the YAWL task
     */
    private void convertDecorator(final Element yawlContainer, final NetElementLayout layoutInformation) {
        @SuppressWarnings("rawtypes")
        List yawlDecoratorList = yawlContainer.getChildren("decorator", yawlNamespace);
        if (yawlDecoratorList != null) {
            for (Object o : yawlDecoratorList) {
                Element yawlDecorator = (Element) o;
                NetElementLayout.DecoratorType decoratorType = convertDecoratorType(yawlDecorator);
                if (yawlDecorator.getAttributeValue("type").contains("join")) {
                    layoutInformation.setJoinDecorator(decoratorType);
                }
                if (yawlDecorator.getAttributeValue("type").contains("split")) {
                    layoutInformation.setSplitDecorator(decoratorType);
                }
            }
        }
    }

    private DecoratorType convertDecoratorType(final Element yawlDecorator) {
        Element decoratorPosition = yawlDecorator.getChild("position", yawlNamespace);
        if (decoratorPosition != null) {
            return YAWLMapping.DECORATOR_TYPE_MAP.get(Integer.parseInt(decoratorPosition.getText()));
        }
        return DecoratorType.NONE;
    }

    /**
     * Searches the whole specification if the element if a condition.
     * 
     * @param id
     *            of the element
     * @return true if there is a condition with specified id
     */
    private boolean isCondition(final String id) {
        return specificationString.contains("<condition id=\"" + id + "\"") || specificationString.contains("<inputCondition id=\"" + id + "\"")
                || specificationString.contains("<outputCondition id=\"" + id + "\"");
    }

    /**
     * @param netLayout
     * @param yawlFlow
     */
    private void convertFlowLayout(final NetLayout netLayout, final Element yawlFlow) {
        FlowLayout layoutInformation = new FlowLayout();
        // Flow apparently doesn't need correct Bounds, but it
        // needs NOT NULL Bounds
        layoutInformation.setBounds(DEFAULT_FLOW_BOUNDS);
        // Flow does need corrects dockers (ports in YAWL)
        layoutInformation.setDockers(convertFlowDockers(netLayout, yawlFlow));
        layoutInformation.setLabel(convertFlowLabel(yawlFlow));
        layoutInformation.setLineStyle(convertFlowLineStyle(yawlFlow));
        netLayout.putFlowLayout(yawlFlow.getAttributeValue("source") + "|" + yawlFlow.getAttributeValue("target"), layoutInformation);
    }

    private int convertFlowLineStyle(final Element yawlFlow) {
        if (yawlFlow.getChild("attributes", yawlNamespace) != null) {
            Element attributes = yawlFlow.getChild("attributes", yawlNamespace);
            if (attributes.getChild("lineStyle", yawlNamespace) != null) {
                return Integer.parseInt(attributes.getChild("lineStyle", yawlNamespace).getValue());
            }
        }
        return 11;
    }

    private String convertFlowLabel(final Element yawlFlow) {
        if (yawlFlow.getChild("label", yawlNamespace) != null) {
            Element labelElement = yawlFlow.getChild("label", yawlNamespace);
            // TODO why is the label text urlencoded in YAWL?
            try {
                return URLDecoder.decode(JDOMUtil.decodeEscapes(labelElement.getText()), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
        return "";
    }

    /**
     * @param netLayout
     * @param yawlFlow
     * @return
     */
    private ArrayList<Point> convertFlowDockers(final NetLayout netLayout, final Element yawlFlow) {

        Element flowPorts = yawlFlow.getChild("ports", yawlNamespace);
        Integer inPort = Integer.valueOf(flowPorts.getAttributeValue("in"));
        Integer outPort = Integer.valueOf(flowPorts.getAttributeValue("out"));

        Point inDocker;
        Point outDocker;

        String targetId = yawlFlow.getAttributeValue("target");
        String sourceId = yawlFlow.getAttributeValue("source");

        // Source may have a SPLIT decorator or may be a condition
        if (netLayout.getVertexLayout(sourceId).isCondition()) {
            inDocker = YAWLMapping.CONDITION_PORT_MAP.get(inPort);
        } else {
            inDocker = convertTaskDocker(netLayout.getVertexLayout(sourceId).getSplitDecorator(), inPort);
        }

        // Target may have a JOIN decorator or may be a condition
        if (netLayout.getVertexLayout(targetId).isCondition()) {
            outDocker = YAWLMapping.CONDITION_PORT_MAP.get(outPort);
        } else {
            outDocker = convertTaskDocker(netLayout.getVertexLayout(targetId).getJoinDecorator(), outPort);
        }
        
        // Fallback in case the values in XML are wrong
        if (outDocker == null) {
            outDocker = YAWLMapping.TASK_PORT_MAP.get(14);
        }
        
        if (inDocker == null) {
            inDocker = YAWLMapping.TASK_PORT_MAP.get(14);
        }        

        ArrayList<Point> dockers = new ArrayList<Point>();

        // Important to add as first Docker with source BasicShape coordinates
        dockers.add(inDocker);

        // Add bends from YAWL with coordinates of Diagram
        Element pointElement = yawlFlow.getChild("attributes", yawlNamespace).getChild("points", yawlNamespace);
        // points may be omitted in YAWL
        if (pointElement != null) {
            @SuppressWarnings("rawtypes")
            List pointList = pointElement.getChildren();
            if (pointList.size() > 2) {
                // Skip the first and last element,
                // as those will be added according to the port information
                for (int i = 1; i < pointList.size() - 1; i++) {
                    if (pointList.get(i) instanceof Element) {
                        Element point = (Element) pointList.get(i);
                        String x = point.getAttributeValue("x");
                        String y = point.getAttributeValue("y");
                        dockers.add(new Point(parseDouble(x), parseDouble(y)));
                    }
                }
            }
        }

        // Important to add as last Docker with target BasicShape coordinates
        dockers.add(outDocker);
        return dockers;
    }

    /**
     * Return the correct Oryx coordinates for each Decorator-Type and Port combination.
     * 
     * @param decoratorType
     *            LEFT, RIGHT, TOP, BOTTOM, NONE
     * @param port
     *            integer from 1-14
     * @return
     */
    private Point convertTaskDocker(final DecoratorType decoratorType, final Integer port) {
        switch (decoratorType) {
        case NONE:
            if (YAWLMapping.TASK_PORT_MAP.containsKey(port)) {
                return YAWLMapping.TASK_PORT_MAP.get(port);
            } else {
                // Default Oryx Mapping
                return YAWLMapping.TASK_PORT_MAP.get(14);
            }
        case TOP:
            return YAWLMapping.TOP_DECORATOR_PORT_MAP.get(port);
        case BOTTOM:
            return YAWLMapping.BOTTOM_DECORATOR_PORT_MAP.get(port);
        case LEFT:
            return YAWLMapping.LEFT_DECORATOR_PORT_MAP.get(port);
        case RIGHT:
            return YAWLMapping.RIGHT_DECORATOR_PORT_MAP.get(port);
        default:
            return YAWLMapping.TASK_PORT_MAP.get(14);
        }
    }

    /**
     * Converts the x,y,w,h attributes to an Oryx Bounds object.
     * 
     * @param yawlBounds
     * @param heightAdjusment
     *            enlarge the height, without affecting the position
     * @param widthAdjustment
     *            enlarge the width, without affecting the position
     * @return
     */
    private Bounds convertToOryxBounds(final Element yawlBounds, final double heightAdjusment, final double widthAdjustment) {
        double x = parseDouble(yawlBounds.getAttributeValue("x")) - (widthAdjustment / 2);
        double y = parseDouble(yawlBounds.getAttributeValue("y")) - (heightAdjusment / 2);
        double w = parseDouble(yawlBounds.getAttributeValue("w")) + widthAdjustment;
        double h = parseDouble(yawlBounds.getAttributeValue("h")) + heightAdjusment;
        return new Bounds(new Point(x + w, y + h), new Point(x, y));
    }

}
