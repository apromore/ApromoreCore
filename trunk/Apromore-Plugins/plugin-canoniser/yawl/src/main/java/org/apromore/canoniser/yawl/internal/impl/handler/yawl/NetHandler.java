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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl;

import java.math.BigDecimal;

import javax.xml.bind.JAXBElement;

import org.apromore.anf.AnnotationsType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.FillType;
import org.apromore.anf.GraphicsType;
import org.apromore.anf.SizeType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.factory.ConversionFactory;
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

    @Override
    public void convert() throws CanoniserException {
        final CanonicalProcessType cpf = getContext().getCanonicalResult();
        final AnnotationsType anf = getContext().getAnnotationResult();

        final NetType canoncialNet = createNet();
        cpf.getNet().add(canoncialNet);

        final LayoutNetFactsType netLayout = getContext().getLayoutForNet(getObject().getId());
        anf.getAnnotation().add(convertGraphics(netLayout));
        if (getObject().getDocumentation() != null) {
            anf.getAnnotation().add(convertDocumentation(canoncialNet, getObject().getDocumentation()));
        }

        // Set rootId of parent if this is the RootNet
        if (getObject().isIsRootNet() != null && getObject().isIsRootNet()) {
            getConvertedParent().getRootIds().add(canoncialNet.getId());
        }

        // First convert data as it is referenced by Tasks
        convertNetData(canoncialNet, getObject());

        final ConversionFactory handlerFactory = getContext().getHandlerFactory();
        final ProcessControlElements processControlElements = getObject().getProcessControlElements();

        // Convert Input Condition
        final ExternalConditionFactsType inputCondition = processControlElements.getInputCondition();
        handlerFactory.createHandler(inputCondition, canoncialNet, getObject()).convert();

        // Convert Net Elements
        for (final ExternalNetElementFactsType element : processControlElements.getTaskOrCondition()) {
            handlerFactory.createHandler(element, canoncialNet, getObject()).convert();
        }

        // Convert Output Condition
        final OutputConditionFactsType outputCondition = processControlElements.getOutputCondition();
        handlerFactory.createHandler(outputCondition, canoncialNet, getObject()).convert();
    }

    private void convertNetData(final NetType canoncialNet, final NetFactsType netDecomposition) throws CanoniserException {
        for (final VariableBaseType var : netDecomposition.getLocalVariable()) {
            getContext().getHandlerFactory().createHandler(var, canoncialNet, netDecomposition).convert();
        }
        for (final InputParameterFactsType param : netDecomposition.getInputParam()) {
            getContext().getHandlerFactory().createHandler(param, canoncialNet, getObject()).convert();
        }
        for (final OutputParameterFactsType param : netDecomposition.getOutputParam()) {
            getContext().getHandlerFactory().createHandler(param, canoncialNet, getObject()).convert();
        }
    }

    private DocumentationType convertDocumentation(final NetType canoncialNet, final String documentation) throws CanoniserException {
        final DocumentationType d = ANF_FACTORY.createDocumentationType();
        d.setCpfId(generateUUID(NET_ID_PREFIX, canoncialNet.getId()));
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
            if (element.getValue() instanceof LayoutFrameType) {
                if (element.getName().getLocalPart().equals(ExtensionUtils.VIEWPORT)) {
                    final LayoutFrameType frame = (LayoutFrameType) element.getValue();
                    size.setHeight(new BigDecimal(frame.getH()));
                    size.setWidth(new BigDecimal(frame.getW()));
                }
            }
        }
        g.setSize(size);

        return g;
    }

    private NetType createNet() {
        final NetType net = CPF_FACTORY.createNetType();
        net.setId(generateUUID(NET_ID_PREFIX, getObject().getId()));
        net.setOriginalID(getObject().getId());
        return net;
    }

}
