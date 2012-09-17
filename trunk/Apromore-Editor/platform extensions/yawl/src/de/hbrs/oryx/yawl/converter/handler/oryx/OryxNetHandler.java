/**
 * Copyright (c) 2011-2012 Felix Mannhardt, felix.mannhardt@smail.wir.h-brs.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See: http://www.gnu.org/licenses/lgpl-3.0
 *
 */
package de.hbrs.oryx.yawl.converter.handler.oryx;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.layout.OryxLayoutConverter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YExternalNetElement;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.elements.data.YVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Converts a (sub)-net
 *
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 */
public class OryxNetHandler extends OryxDecompositionHandler {

    public OryxNetHandler(OryxConversionContext context, BasicShape shape) {
        super(context, shape);
    }

    /*
      * (non-Javadoc)
      *
      * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxHandler#convert()
      */
    @Override
    public void convert() {
        String yawlId = convertYawlId(getShape());

        YNet net = new YNet(yawlId, getContext().getSpecification());

        convertLayout(net);

        try {
            convertProperties(net);
        } catch (JSONException e) {
            getContext().addConversionWarnings("Error converting Net properties", e);
        } catch (ConversionException e) {
            getContext().addConversionWarnings("Error converting Net properties", e);
        }

        getContext().addNet(getShape(), net);

        convertChildShapes();
        convertFlows();
        convertCancellationSets(net);

        if (new Boolean(getShape().getProperty("isrootnet"))) {
            getContext().getSpecification().setRootNet(net);
        } else {
            getContext().getSpecification().setDecomposition(net);
        }
    }

    private void convertLayout(YNet net) {
        new OryxLayoutConverter(getContext(), net, getShape()).convertLayout();
    }

    private void convertProperties(YNet net) throws JSONException, ConversionException {
        JSONObject variables = getShape().getPropertyJsonObject("decompositionvariables");
        if (variables != null) {
            JSONArray varArray = variables.getJSONArray("items");
            for (int index = 0; index < varArray.length(); index++) {
                JSONObject varObject = varArray.getJSONObject(index);
                String usage = varObject.getString("usage");

                if (usage.equals("local")) {

                    if (varObject.has("name")) {
                        YVariable convertVariable = convertVariable(net, varObject);
                        convertVariable.setOrdering(index);
                        net.getLocalVariables().put(varObject.getString("name"), convertVariable);
                    }

                } else {
                    // TODO REFACTOR: is the same as in atomictask
                    YParameter convertParameter = convertParameter(net, varObject);
                    convertParameter.setOrdering(index);

                    if (usage.equals("input")) {
                        net.addInputParameter(convertParameter);
                    } else if (usage.equals("output")) {
                        net.addOutputParameter(convertParameter);

                        // Add local variable with index 0 as YAWL seems to do
                        // this. Seems to be useless!
                        YVariable convertVariable = convertVariable(net, varObject);
                        convertVariable.setOrdering(0);
                        net.getLocalVariables().put(varObject.getString("name"), convertVariable);
                    } else {
                        // Is Both
                        varObject.put("usage", "input");
                        YParameter inputParameter = convertParameter(net, varObject);
                        inputParameter.setOrdering(index);
                        net.addInputParameter(inputParameter);

                        // Add both to Input and Output Parameters
                        varObject.put("usage", "output");
                        YParameter outputParameter = convertParameter(net, varObject);
                        outputParameter.setOrdering(index);
                        net.addOutputParameter(outputParameter);
                    }
                }

            }
        }
    }

    private void convertChildShapes() {
        for (BasicShape shape : getShape().getChildShapesReadOnly()) {
            OryxHandler handler = getContext().getHandlerFactory().createOryxConverter(shape);
            handler.convert();
        }
    }

    private void convertFlows() {
        Set<BasicEdge> flowSet = getContext().getFlowSet(getShape());

        for (BasicEdge flowShape : flowSet) {
            OryxHandler handler = getContext().getHandlerFactory().createOryxConverter(flowShape, getShape());
            handler.convert();
        }

    }

    private void convertCancellationSets(YNet net) {
        for (Entry<YTask, List<String>> cancellationSet : getContext().getCancellationSets(net)) {
            YTask task = cancellationSet.getKey();
            List<String> cancelledIds = cancellationSet.getValue();
            List<YExternalNetElement> removeSet = new ArrayList<YExternalNetElement>();
            for (String id : cancelledIds) {
                removeSet.add(net.getNetElement(id));
            }
            task.addRemovesTokensFrom(removeSet);
        }
    }

}
