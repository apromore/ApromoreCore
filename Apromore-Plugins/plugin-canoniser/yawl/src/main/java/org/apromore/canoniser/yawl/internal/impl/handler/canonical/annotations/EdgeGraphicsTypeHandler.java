/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import java.math.BigInteger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext.ElementInfo;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.EdgeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.LayoutAttributesFactsType;
import org.yawlfoundation.yawlschema.LayoutFlowFactsType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.LayoutPointType;
import org.yawlfoundation.yawlschema.LayoutPointsType;
import org.yawlfoundation.yawlschema.LayoutPortsType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;
import org.yawlfoundation.yawlschema.ObjectFactory;

/**
 * Convert the layout for a CPF edge (YAWL Flow).
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class EdgeGraphicsTypeHandler extends ElementGraphicsTypeHandler {

    private static final int DEFAULT_LINE_SIZE = 11;
    private static final int DEFAULT_YAWL_PORT = 14;

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeGraphicsTypeHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final LayoutNetFactsType netLayout = findNetLayout(getObject().getCpfId(), getConvertedParent());

        final EdgeType edge = getContext().getEdgeById(getObject().getCpfId());

        final ExternalNetElementType sourceElement = getContext().getElementInfo(edge.getSourceId()).element;
        final ExternalNetElementType targetElement = getContext().getElementInfo(edge.getTargetId()).element;

        if (sourceElement != null && targetElement != null) {

            final JAXBElement<LayoutFlowFactsType> existingFlowLayout = findLayoutFlowAnnotation();
            if (existingFlowLayout != null) {
                LOGGER.debug("Added layout for flow using YAWL extension element. Source {} -> Target {}", sourceElement.getId(),
                        targetElement.getId());
                netLayout.getBoundsOrFrameOrViewport().add(convertFlowLayout(existingFlowLayout, sourceElement, targetElement));
            } else {
                LOGGER.debug("Added layout for flow using default settings. Source {} -> Target {}", sourceElement.getId(), targetElement.getId());
                netLayout.getBoundsOrFrameOrViewport().add(createDefaultFlowLayout(sourceElement, targetElement, edge));
            }

        } else {
            LOGGER.warn("Can not find Edge in YAWL specification, maybe it was merged! {}", edge.getId());
        }
    }

    private JAXBElement<LayoutFlowFactsType> findLayoutFlowAnnotation() {
        // Search for YAWL annotation
        for (final Object obj : getObject().getAny()) {
            try {
                if (ConversionUtils.isValidFragment(obj, ConversionUtils.YAWLSCHEMA_URL, "flow")) {
                    final LayoutFlowFactsType flowLayout = ConversionUtils.unmarshalYAWLFragment(obj, LayoutFlowFactsType.class);
                    final ObjectFactory oF = getContext().getYawlObjectFactory();
                    return oF.createLayoutNetFactsTypeFlow(flowLayout);
                }
            } catch (final JAXBException e) {
                LOGGER.warn("Error unmarshalling extension elements. This should not happen, but the conversion will still work.", e);
            }
        }
        return null;
    }

    private JAXBElement<LayoutFlowFactsType> convertFlowLayout(final JAXBElement<LayoutFlowFactsType> flowLayoutElement,
            final ExternalNetElementType sourceElement, final ExternalNetElementType targetElement) {
        final LayoutFlowFactsType flowLayout = flowLayoutElement.getValue();
        // Update Target/Source IDs as they may have been changed
        flowLayout.setSource(sourceElement.getId());
        flowLayout.setTarget(targetElement.getId());
        return flowLayoutElement;
    }

    private JAXBElement<LayoutFlowFactsType> createDefaultFlowLayout(final ExternalNetElementType sourceElement,
            final ExternalNetElementType targetElement, final EdgeType edge) {

        final ObjectFactory oF = getContext().getYawlObjectFactory();

        final LayoutFlowFactsType flowLayout = oF.createLayoutFlowFactsType();

        // Set source and target
        flowLayout.setSource(sourceElement.getId());
        flowLayout.setTarget(targetElement.getId());

        // Convert Ports
        final LayoutPortsType ports = oF.createLayoutPortsType();
        // Set default port as we don't know better
        ports.setIn(BigInteger.valueOf(DEFAULT_YAWL_PORT));
        ports.setOut(BigInteger.valueOf(DEFAULT_YAWL_PORT));
        flowLayout.setPorts(ports);

        // Add default line style
        final LayoutAttributesFactsType attributes = oF.createLayoutAttributesFactsType();
        attributes.getAutosizeOrBackgroundColorOrBendable().add(oF.createLayoutAttributesFactsTypeLineStyle(BigInteger.valueOf(DEFAULT_LINE_SIZE)));
        flowLayout.setAttributes(attributes);

        // Set default sizes
        final LayoutPointsType points = oF.createLayoutPointsType();
        points.getValue().add(createPoint(edge.getSourceId()));
        points.getValue().add(createPoint(edge.getTargetId()));

        attributes.getAutosizeOrBackgroundColorOrBendable().add(oF.createLayoutAttributesFactsTypePoints(points));

        return oF.createLayoutNetFactsTypeFlow(flowLayout);
    }

    private LayoutPointType createPoint(final String nodeId) {
        final LayoutPointType point = getContext().getYawlObjectFactory().createLayoutPointType();

        final ElementInfo elementInfo = getContext().getElementInfo(nodeId);
        final LayoutRectangleType elementSize = elementInfo.elementSize;

        point.setX(elementSize.getX());
        point.setY(elementSize.getY());

        return point;
    }

}
