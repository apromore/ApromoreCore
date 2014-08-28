/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.xml.bind.JAXBElement;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalToYAWLElementInfo;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
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

        final ExternalNetElementType sourceElement = getContext().getControlFlowContext().getElementInfo(edge.getSourceId()).getElement();
        final ExternalNetElementType targetElement = getContext().getControlFlowContext().getElementInfo(edge.getTargetId()).getElement();

        if (sourceElement != null && targetElement != null) {

            final LayoutFlowFactsType existingFlowLayout = findLayoutFlowAnnotation();
            if (existingFlowLayout != null) {
                LOGGER.debug("Added layout for flow using YAWL extension element. Source {} -> Target {}", sourceElement.getId(),
                        targetElement.getId());
                netLayout.getBoundsOrFrameOrViewport().add(convertFlowLayout(YAWL_FACTORY.createLayoutNetFactsTypeFlow(existingFlowLayout), sourceElement, targetElement));
            } else {
                LOGGER.debug("Added layout for flow using default settings. Source {} -> Target {}", sourceElement.getId(), targetElement.getId());
                if ((getContext().getControlFlowContext().getElementInfo(edge.getSourceId()).getElementSize() != null)
                        && (getContext().getControlFlowContext().getElementInfo(edge.getTargetId()).getElementSize() != null)) {
                    netLayout.getBoundsOrFrameOrViewport().add(createDefaultFlowLayout(sourceElement, targetElement, edge));
                }
            }

        } else {
            LOGGER.warn("Can not find Edge in YAWL specification, maybe it was merged! {}", edge.getId());
        }
    }

    private LayoutFlowFactsType findLayoutFlowAnnotation() {
        // Search for YAWL annotation
        for (final Object obj : getObject().getAny()) {
            try {
                if (ExtensionUtils.isValidFragment(obj, ExtensionUtils.YAWLSCHEMA_URL, ExtensionUtils.FLOW)) {
                    return ExtensionUtils.unmarshalYAWLFragment(obj, LayoutFlowFactsType.class);
                }
            } catch (final CanoniserException e) {
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
            final ExternalNetElementType targetElement, final EdgeType edge) throws CanoniserException {

        final LayoutFlowFactsType flowLayout = YAWL_FACTORY.createLayoutFlowFactsType();

        // Set source and target
        flowLayout.setSource(sourceElement.getId());
        flowLayout.setTarget(targetElement.getId());

        // Convert Ports
        final LayoutPortsType ports = YAWL_FACTORY.createLayoutPortsType();
        // Set default port as we don't know better
        ports.setIn(BigInteger.valueOf(DEFAULT_YAWL_PORT));
        ports.setOut(BigInteger.valueOf(DEFAULT_YAWL_PORT));
        flowLayout.setPorts(ports);

        // Add default line style
        final LayoutAttributesFactsType attributes = YAWL_FACTORY.createLayoutAttributesFactsType();
        attributes.getAutosizeOrBackgroundColorOrBendable().add(
                YAWL_FACTORY.createLayoutAttributesFactsTypeLineStyle(BigInteger.valueOf(DEFAULT_LINE_SIZE)));
        flowLayout.setAttributes(attributes);

        // Set default sizes
        final LayoutPointsType points = YAWL_FACTORY.createLayoutPointsType();
        try {
            points.getValue().add(createPoint(edge.getSourceId()));
            points.getValue().add(createPoint(edge.getTargetId()));
        } catch (ParseException e) {
            throw new CanoniserException("Could not convert edge location", e);
        }

        attributes.getAutosizeOrBackgroundColorOrBendable().add(YAWL_FACTORY.createLayoutAttributesFactsTypePoints(points));

        return YAWL_FACTORY.createLayoutNetFactsTypeFlow(flowLayout);
    }

    private LayoutPointType createPoint(final String nodeId) throws ParseException {
        final LayoutPointType point = YAWL_FACTORY.createLayoutPointType();

        final CanonicalToYAWLElementInfo elementInfo = getContext().getControlFlowContext().getElementInfo(nodeId);
        final LayoutRectangleType elementSize = elementInfo.getElementSize();

        NumberFormat nf = getContext().getYawlNumberFormat();
        int width = nf.parse(elementSize.getW()).intValue();
        int height = nf.parse(elementSize.getH()).intValue();
        double x = nf.parse(elementSize.getX()).doubleValue();
        double y = nf.parse(elementSize.getY()).doubleValue();

        point.setX(nf.format(x + (width /2 )));
        point.setY(nf.format(y + (height / 2)));

        return point;
    }

}
