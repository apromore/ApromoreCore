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
package de.hbrs.oryx.yawl.converter.handler.oryx;

import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.yawlfoundation.yawl.elements.YCompositeTask;
import org.yawlfoundation.yawl.elements.YDecomposition;
import org.yawlfoundation.yawl.elements.YNet;
import org.yawlfoundation.yawl.elements.YTask;

import de.hbrs.oryx.yawl.converter.context.OryxConversionContext;
import de.hbrs.oryx.yawl.converter.exceptions.ConversionException;
import de.hbrs.oryx.yawl.converter.exceptions.NoSubnetFoundException;

/**
 * Converts a composite task
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University of Applied Sciences)
 * 
 */
public class OryxCompositeTaskHandler extends OryxTaskHandler {

    public OryxCompositeTaskHandler(final OryxConversionContext context, final BasicShape shape) {
        super(context, shape);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxTaskHandler#convertDecomposition (org.oryxeditor.server.diagram.basic.BasicShape)
     */
    @Override
    protected YDecomposition createDecomposition(final YDecomposition existingDecomposition) throws JSONException, ConversionException {

        try {
            BasicDiagram subnetDiagram = retrieveSubnetDiagram(getShape());
            // Just find the contained subnet and ignore all Diagram properties
            BasicShape subnet = findSubnetNet(subnetDiagram);
            // Convert the Subnet with all its childShapes
            getContext().getHandlerFactory().createOryxConverter(subnet).convert();
            // Return to just created subnet, that should be already part of the
            // specification decompositions
            return super.createDecomposition(getContext().getNet(subnet));
        } catch (NoSubnetFoundException e) {
            getContext().addConversionWarnings("Could not find decomposition of composite task " + getShape().getProperty("yawlid"), e);
            return null;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hbrs.oryx.yawl.converter.handler.oryx.OryxTaskHandler#createTask(java .lang.String, org.yawlfoundation.yawl.elements.YNet,
     * org.oryxeditor.server.diagram.basic.BasicShape)
     */
    @Override
    protected YTask createTask(final String taskId, final YNet parentNet) throws JSONException, ConversionException {
        int joinType = convertConnectorType(getShape().getProperty("join"), YTask._XOR);
        int splitType = convertConnectorType(getShape().getProperty("split"), YTask._AND);

        YCompositeTask task = new YCompositeTask(taskId, joinType, splitType, parentNet);

        if (hasDecomposition()) {
            YDecomposition decomposition = createDecomposition(new YNet(getDecompositionId(), getContext().getSpecification()));
            if (decomposition != null) {
                task.setDecompositionPrototype(decomposition);
            }
        }

        return task;
    }

    private BasicShape findSubnetNet(final BasicDiagram diagramShape) throws NoSubnetFoundException {
        return diagramShape;
    }

    /**
     * Fetches the Subnet
     * 
     * @param BasicShape
     * @return
     * @throws NoSubnetFoundException
     */
    private BasicDiagram retrieveSubnetDiagram(final BasicShape shape) throws NoSubnetFoundException {
        BasicDiagram subnetDiagram = getContext().getSubnetDiagram(getDecompositionId());
        if (subnetDiagram != null) {
            return subnetDiagram;
        } else {
            throw new NoSubnetFoundException("Could not find a Diagram for Subnet with ID: " + shape.getProperty("decomposesto"));
        }
    }

}
