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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl;

import java.math.BigDecimal;

import javax.xml.bind.JAXBElement;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.FillType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.SizeType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.ExternalNetElementFactsType;
import org.yawlfoundation.yawlschema.InputParameterFactsType;
import org.yawlfoundation.yawlschema.LayoutFrameType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.NetFactsType.ProcessControlElements;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;
import org.yawlfoundation.yawlschema.OutputParameterFactsType;
import org.yawlfoundation.yawlschema.VariableBaseType;

/**
 * Converting a YAWL (Sub)-Net
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class NetHandler extends YAWLConversionHandler<NetFactsType, CanonicalProcessType> {

    private static final String VIEWPORT = "viewport";

    @Override
    public void convert() throws CanoniserException {
        final CanonicalProcessType cpf = getContext().getCanonicalResult();
        final AnnotationsType anf = getContext().getAnnotationResult();

        final NetType canonicalNet = createNet();
        cpf.getNet().add(canonicalNet);

        final LayoutNetFactsType netLayout = getContext().getLayoutForNet(getObject().getId());
        if (netLayout != null) {
            anf.getAnnotation().add(convertGraphics(netLayout));
        }
        if (getObject().getDocumentation() != null) {
            anf.getAnnotation().add(convertDocumentation(canonicalNet, getObject().getDocumentation()));
        }

        // Set rootId of parent if this is the RootNet
        if (getObject().isIsRootNet() != null && getObject().isIsRootNet()) {
            getConvertedParent().getRootIds().add(canonicalNet.getId());
        }

        // First convert data as it is referenced by Tasks
        convertNetData(canonicalNet, getObject());

        final ProcessControlElements processControlElements = getObject().getProcessControlElements();

        // Convert Input Condition
        final ExternalConditionFactsType inputCondition = processControlElements.getInputCondition();
        getContext().createHandler(inputCondition, canonicalNet, getObject()).convert();

        // Convert Net Elements
        for (final ExternalNetElementFactsType element : processControlElements.getTaskOrCondition()) {
            getContext().createHandler(element, canonicalNet, getObject()).convert();
        }

        // Convert Output Condition
        final OutputConditionFactsType outputCondition = processControlElements.getOutputCondition();
        getContext().createHandler(outputCondition, canonicalNet, getObject()).convert();
    }

    private void convertNetData(final NetType canonicalNet, final NetFactsType netDecomposition) throws CanoniserException {
        for (final VariableBaseType var : netDecomposition.getLocalVariable()) {
            getContext().createHandler(var, canonicalNet, netDecomposition).convert();
        }
        for (final InputParameterFactsType param : netDecomposition.getInputParam()) {
            getContext().createHandler(param, canonicalNet, getObject()).convert();
        }
        for (final OutputParameterFactsType param : netDecomposition.getOutputParam()) {
            getContext().createHandler(param, canonicalNet, getObject()).convert();
        }
    }

    private DocumentationType convertDocumentation(final NetType canonicalNet, final String documentation) throws CanoniserException {
        final DocumentationType d = ANF_FACTORY.createDocumentationType();
        d.setCpfId(generateUUID(NET_ID_PREFIX, canonicalNet.getId()));
        d.setId(generateUUID());
        d.getAny().add(ExtensionUtils.marshalYAWLFragment(ExtensionUtils.DOCUMENTATION, documentation, String.class));
        return d;
    }

    private GraphicsType convertGraphics(final LayoutNetFactsType netLayout) {
        final GraphicsType g = ANF_FACTORY.createGraphicsType();
        g.setCpfId(generateUUID(NET_ID_PREFIX, getObject().getId()));
        g.setId(generateUUID());

        if (netLayout.getBgColor() != null) {
            final FillType fill = ANF_FACTORY.createFillType();
            final String color = ConversionUtils.convertColorToString(netLayout.getBgColor().intValue());
            fill.setColor(color);
            g.setFill(fill);
        }

        // Use size of viewport only, as CPF only supports one type of size
        final SizeType size = ANF_FACTORY.createSizeType();
        for (final JAXBElement<?> element : netLayout.getBoundsOrFrameOrViewport()) {
            if (element.getValue() instanceof LayoutFrameType && element.getName().getLocalPart().equals(VIEWPORT)) {
                final LayoutFrameType frame = (LayoutFrameType) element.getValue();
                size.setHeight(new BigDecimal(frame.getH()));
                size.setWidth(new BigDecimal(frame.getW()));
            }
        }
        g.setSize(size);

//        PositionType position = ANF_FACTORY.createPositionType();
//        position.setX(BigDecimal.ZERO);
//        position.setY(BigDecimal.ZERO);
//        g.getPosition().add(position);

        return g;
    }

    private NetType createNet() {
        final NetType net = CPF_FACTORY.createNetType();
        net.setId(generateUUID(NET_ID_PREFIX, getObject().getId()));
        net.setOriginalID(getObject().getId());
        return net;
    }

}
