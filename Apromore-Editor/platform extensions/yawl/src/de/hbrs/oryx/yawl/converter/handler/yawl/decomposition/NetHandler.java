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
package de.hbrs.oryx.yawl.converter.handler.yawl.decomposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YFlow;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.data.YVariable;

import de.hbrs.oryx.yawl.converter.context.YAWLConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler;

/**
 * Converts a YAWL net to an Oryx diagram and calling the converters of all child elements of the YAWL net.
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class NetHandler extends DecompositionHandler {

    public NetHandler(final YAWLConversionContext context, final YDecomposition decomposition) {
        super(context, decomposition);
    }

    protected YNet getNet() {
        return (YNet) getDecomposition();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.YAWLHandler#convert(java.lang .String)
     */
    @Override
    public void convert(final String parentId) {

        final String netId = getNet().getID();

        String stencilSetNs = "http://b3mn.org/stencilset/yawl2.2#";
        StencilSetReference stencilSetRef = new StencilSetReference(stencilSetNs);

        final BasicDiagram netShape = new BasicDiagram(netId, "Diagram", stencilSetRef);

        netShape.setProperties(convertProperties());
        netShape.setBounds(getNetLayout(getNet()));
        getContext().addNet(netId, netShape);

        // Convert all children (NetElements) of root net
        for (Entry<String, YExternalNetElement> netElementEntry : getNet().getNetElements().entrySet()) {
            YExternalNetElement yElement = netElementEntry.getValue();

            YAWLHandler netElementHandler = getContext().getHandlerFactory().createYAWLConverter(yElement);
            netElementHandler.convert(netId);

        }

        // Convert all flows between the NetElements
        for (YFlow yFlow : getContext().getNetLayout(netId).getFlowSet()) {
            YAWLHandler flowHandler = getContext().getHandlerFactory().createYAWLConverter(yFlow);
            flowHandler.convert(netId);
        }

    }

    /**
     * Converting the properties of a net.<br/>
     * CONTRACT: Sub-Classes have to call this method first, adding their properties afterwards.
     * 
     * @return Oryx Property HashMap
     */
    protected HashMap<String, String> convertProperties() {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("name", getNet().getName() != null ? getNet().getName() : "");
        properties.put("yawlid", getNet().getID());
        // Removed as every net could be a root net
        // properties.put("isrootnet", "false");
        properties.put("externaldatagateway", getNet().getExternalDataGateway());
        properties.putAll(convertDecompositionProperties());
        return properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.yawl.decomposition.DecompositionHandler #convertDecompositionProperties()
     */
    @Override
    protected HashMap<String, String> convertDecompositionProperties() {
        HashMap<String, String> properties = super.convertDecompositionProperties();
        try {
            properties.put("decompositionvariables", convertDecompositionVariables());
        } catch (ConversionException e) {
            getContext().addConversionWarnings("Could not convert decomposition variables.", e);
        }

        return properties;
    }

    private String convertDecompositionVariables() throws ConversionException {

        JSONObject variables = new JSONObject();
        JSONArray items = new JSONArray();

        final Map<String, YParameter> inputParameters = getDecomposition().getInputParameters();
        List<YParameter> inputParameterList = new ArrayList<YParameter>(inputParameters.values());
        Collections.sort(inputParameterList);

        final Map<String, YParameter> outputParameters = getDecomposition().getOutputParameters();
        List<YParameter> outputParameterList = new ArrayList<YParameter>(outputParameters.values());
        Collections.sort(outputParameterList);

        List<YVariable> variablesList = new ArrayList<YVariable>(getNet().getLocalVariables().values());
        Collections.sort(variablesList);

        try {
            for (YParameter inputParam : inputParameterList) {
                if (outputParameters.containsKey(inputParam.getName())) {
                    items.put(convertParameter(inputParam, "inputandoutput"));
                } else {
                    items.put(convertParameter(inputParam, "input"));
                }
            }

            for (YParameter outputParam : outputParameterList) {
                // Only if not already added as inputandoutput paramter
                if (!inputParameters.containsKey(outputParam.getName())) {
                    items.put(convertParameter(outputParam, "output"));
                }
            }

            for (YVariable localVariable : variablesList) {
                // Only if not already added above
                if (!inputParameters.containsKey(localVariable.getName()) && !outputParameters.containsKey(localVariable.getName())) {
                    items.put(convertParameter(localVariable, "local"));
                }
            }
            variables.put("items", items);
        } catch (JSONException e) {
            throw new ConversionException(e);
        }

        return variables.toString();
    }

    private Bounds getNetLayout(final YNet net) {
        return getContext().getNetLayout(net.getID()).getBounds();
    }

}