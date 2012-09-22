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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apromore.anf.GraphicsType;
import org.apromore.anf.PositionType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalNetElementType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;
import org.yawlfoundation.yawlschema.LayoutAttributesFactsType;
import org.yawlfoundation.yawlschema.LayoutContainerFactsType;
import org.yawlfoundation.yawlschema.LayoutDecoratorFactsType;
import org.yawlfoundation.yawlschema.LayoutDimensionType;
import org.yawlfoundation.yawlschema.LayoutLabelFactsType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.LayoutRectangleType;
import org.yawlfoundation.yawlschema.LayoutVertexFactsType;
import org.yawlfoundation.yawlschema.ObjectFactory;

/**
 * Convert the layout of an CPF node to YAWL
 *
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class NodeGraphicsTypeHandler extends ElementGraphicsTypeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeGraphicsTypeHandler.class.getName());

    private static final String JOIN_ROUTING_TYPE = "join";
    private static final String SPLIT_ROUTING_TYPE = "split";

    private ExternalNetElementType yawlElement;
    private LayoutRectangleType elementBounds;
    private YAWLAutoLayouter autoLayoutInfo;

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        if (getContext().getElementInfo(getObject().getCpfId()).element != null) {
            this.yawlElement = getContext().getElementInfo(getObject().getCpfId()).element;
        } else {
            LOGGER.warn("Could not find converted YAWL element for CPF-ID {} while trying to convert GraphicsType annotation with ID {}.",
                    getObject().getCpfId(), getObject().getId());
            return;
        }

        this.autoLayoutInfo = getContext().getAutoLayoutInfo();

        final JAXBElement<LayoutContainerFactsType> elementLayout = createElementLayout(getObject());
        LOGGER.debug("Added Layout for element {} ", yawlElement.getId());
        final LayoutNetFactsType netLayout = findNetLayout(getObject().getCpfId(), getConvertedParent());
        if (netLayout != null) {
            netLayout.getBoundsOrFrameOrViewport().add(elementLayout);
        } else {
            throw new CanoniserException("Could not find LayoutNetFactsType for element with ID " + getObject().getCpfId());
        }

        getContext().getAutoLayoutInfo().setLastElementBounds(elementBounds);
    }

    private ObjectFactory getObjectFactory() {
        return getContext().getYawlObjectFactory();
    }

    private JAXBElement<LayoutContainerFactsType> createElementLayout(final GraphicsType graphic) throws CanoniserException {
        final LayoutContainerFactsType layoutContainer = getObjectFactory().createLayoutContainerFactsType();
        layoutContainer.setId(generateUUID(graphic.getCpfId()));
        // First create Vertex as it will be used as reference for other conversion
        final LayoutVertexFactsType elementVertex = createVertex(graphic);
        layoutContainer.getVertexOrLabelOrDecorator().add(elementVertex);
        layoutContainer.getVertexOrLabelOrDecorator().add(createLabel(graphic));
        if (hasRoutingElement()) {
            final List<LayoutDecoratorFactsType> decorators = createDecorator(graphic, (ExternalTaskFactsType) yawlElement);
            layoutContainer.getVertexOrLabelOrDecorator().addAll(decorators);
        }
        return getObjectFactory().createLayoutNetFactsTypeContainer(layoutContainer);
    }

    private LayoutVertexFactsType createVertex(final GraphicsType graphic) throws CanoniserException {
        final LayoutVertexFactsType vertex = getObjectFactory().createLayoutVertexFactsType();
        final LayoutAttributesFactsType attrs = getObjectFactory().createLayoutAttributesFactsType();

        attrs.getAutosizeOrBackgroundColorOrBendable().add(convertVertexBounds(graphic));

        if (graphic.getFill() != null) {
            if (graphic.getFill().getImage() != null && graphic.getFill().getImage().startsWith("/org/yawlfoundation/yawl/")) {
                // It was an YAWL icon
                LOGGER.debug("Setting icon {}", graphic.getFill().getImage());
                vertex.setIconpath(graphic.getFill().getImage());
            }
            if (graphic.getFill().getColor() != null) {
                final BigInteger bgColor = ConversionUtils.convertColorToBigInteger(graphic.getFill().getColor());
                LOGGER.debug("Setting bgcolor {}", bgColor);
                attrs.getAutosizeOrBackgroundColorOrBendable().add(getObjectFactory().createLayoutAttributesFactsTypeBackgroundColor(bgColor));
            }
        }

        vertex.setAttributes(attrs);
        return vertex;
    }

    private JAXBElement<LayoutRectangleType> convertVertexBounds(final GraphicsType graphic) throws CanoniserException {
        elementBounds = getObjectFactory().createLayoutRectangleType();
        final NumberFormat nf = getContext().getYawlNumberFormat();

        if (graphic.getSize() != null) {
            elementBounds.setH(nf.format(graphic.getSize().getHeight()));
            elementBounds.setW(nf.format(graphic.getSize().getWidth()));
        } else {
            elementBounds.setH(nf.format(this.autoLayoutInfo.getElementHeight(yawlElement)));
            elementBounds.setW(nf.format(this.autoLayoutInfo.getElementWidth(yawlElement)));
        }

        if (graphic.getPosition().size() == 1) {
            final PositionType pos = graphic.getPosition().get(0);
            elementBounds.setX(nf.format(pos.getX()));
            elementBounds.setY(nf.format(pos.getY()));
        } else {
            elementBounds.setX(nf.format(this.autoLayoutInfo.getElementX()));
            elementBounds.setY(nf.format(this.autoLayoutInfo.getElementY()));
        }

        LOGGER.debug("Setting size h: {}, w: {} and position x: {}, y: {}",
                new String[] { elementBounds.getH(), elementBounds.getW(), elementBounds.getX(), elementBounds.getY() });
        getContext().setElementBounds(getObject().getCpfId(), elementBounds);
        final JAXBElement<LayoutRectangleType> boundsAttribute = getObjectFactory().createLayoutAttributesFactsTypeBounds(elementBounds);
        return boundsAttribute;
    }

    private LayoutLabelFactsType createLabel(final GraphicsType graphic) throws CanoniserException {
        final LayoutLabelFactsType yawlGraphicsExtension = getYAWLGraphicsExtension(graphic);
        if (yawlGraphicsExtension != null) {
            // Use YAWL Annotation
            LOGGER.debug("Adding label layout using YAWL extension");
            return yawlGraphicsExtension;
        } else {
            // Otherwise guess reasonable defaults
            LOGGER.debug("Adding label layout using default settings");
            return createDefaultLabel();
        }
    }

    private LayoutLabelFactsType getYAWLGraphicsExtension(final GraphicsType graphic) {
        for (final Object extensionObj : graphic.getAny()) {
            try {
                if (ConversionUtils.isValidFragment(extensionObj, ConversionUtils.YAWLSCHEMA_URL, "label")) {
                    return ConversionUtils.unmarshalYAWLFragment(extensionObj, LayoutLabelFactsType.class);
                }
            } catch (final JAXBException e) {
                LOGGER.warn("Error unmarshalling extension elements. This should not happen, but the conversion will still work.", e);
            }
        }
        return null;
    }

    private LayoutLabelFactsType createDefaultLabel() throws CanoniserException {
        final LayoutLabelFactsType label = getObjectFactory().createLayoutLabelFactsType();
        final LayoutAttributesFactsType labelAttr = getObjectFactory().createLayoutAttributesFactsType();
        final NumberFormat nf = getContext().getYawlNumberFormat();

        // Guess reasonable defaults from GraphicsType
        final LayoutRectangleType labelRect = getObjectFactory().createLayoutRectangleType();
        labelRect.setH(nf.format(this.autoLayoutInfo.getLabelHeight(yawlElement)));
        labelRect.setW(nf.format(this.autoLayoutInfo.getLabelWidth(yawlElement)));
        final boolean hasJoinRouting = getContext().hasJoinRouting(yawlElement.getId());
        final boolean hasSplitRouting = getContext().hasSplitRouting(yawlElement.getId());
        labelRect.setX(nf.format(this.autoLayoutInfo.getLabelX(yawlElement, elementBounds, hasJoinRouting, hasSplitRouting)));
        labelRect.setY(nf.format(this.autoLayoutInfo.getLabelY(yawlElement, elementBounds, hasJoinRouting, hasSplitRouting)));

        labelAttr.getAutosizeOrBackgroundColorOrBendable().add(getObjectFactory().createLayoutAttributesFactsTypeBounds(labelRect));
        label.setAttributes(labelAttr);
        return label;
    }

    private boolean hasRoutingElement() {
        if (yawlElement instanceof ExternalTaskFactsType) {
            final ExternalTaskFactsType task = (ExternalTaskFactsType) yawlElement;
            return getContext().hasJoinRouting(task.getId()) || getContext().hasSplitRouting(task.getId());
        }
        return false;
    }

    private List<LayoutDecoratorFactsType> createDecorator(final GraphicsType graphic, final ExternalTaskFactsType yawlElement)
            throws CanoniserException {
        // First check for extension
        final List<LayoutDecoratorFactsType> decoratorList = new ArrayList<LayoutDecoratorFactsType>();
        for (final Object extensionObj : graphic.getAny()) {
            try {
                if (ConversionUtils.isValidFragment(extensionObj, ConversionUtils.YAWLSCHEMA_URL, "decorator")) {
                    final LayoutDecoratorFactsType decorator = ConversionUtils.unmarshalYAWLFragment(extensionObj, LayoutDecoratorFactsType.class);
                    decoratorList.add(decorator);
                    LOGGER.debug("Adding decorator layout using YAWL extension, type: {}", decorator.getType());
                }
            } catch (final JAXBException e) {
                LOGGER.warn("Error unmarshalling extension elements. This should not happen, but the conversion will still work.", e);
            }
        }
        if (!decoratorList.isEmpty()) {
            return decoratorList;
        }

        return createDefaultDecorator(graphic, yawlElement);
    }

    private List<LayoutDecoratorFactsType> createDefaultDecorator(final GraphicsType graphic, final ExternalTaskFactsType yawlElement)
            throws CanoniserException {
        final List<LayoutDecoratorFactsType> decoratorList = new ArrayList<LayoutDecoratorFactsType>();

        if (getContext().hasJoinRouting(yawlElement.getId())) {
            final LayoutDecoratorFactsType decorator = getObjectFactory().createLayoutDecoratorFactsType();
            decorator.setType(convertJoinRouting(yawlElement));
            decorator.setPosition(BigInteger.valueOf(12));
            final LayoutAttributesFactsType attributes = getContext().getYawlObjectFactory().createLayoutAttributesFactsType();
            attributes.getAutosizeOrBackgroundColorOrBendable().add(createDefaultDecoratorBounds(graphic, JOIN_ROUTING_TYPE));
            attributes.getAutosizeOrBackgroundColorOrBendable().add(createDefaultDecoratorSize());
            decorator.setAttributes(attributes);
            LOGGER.debug("Adding decorator layout using default settings, type: {}", decorator.getType());
            decoratorList.add(decorator);
        }

        if (getContext().hasSplitRouting(yawlElement.getId())) {
            final LayoutDecoratorFactsType decorator = getObjectFactory().createLayoutDecoratorFactsType();
            decorator.setType(convertSplitRouting(yawlElement));
            decorator.setPosition(BigInteger.valueOf(13));
            final LayoutAttributesFactsType attributes = getContext().getYawlObjectFactory().createLayoutAttributesFactsType();
            attributes.getAutosizeOrBackgroundColorOrBendable().add(createDefaultDecoratorBounds(graphic, SPLIT_ROUTING_TYPE));
            attributes.getAutosizeOrBackgroundColorOrBendable().add(createDefaultDecoratorSize());
            decorator.setAttributes(attributes);
            LOGGER.debug("Adding decorator layout using default settings, type: {}", decorator.getType());
            decoratorList.add(decorator);
        }

        return decoratorList;
    }

    private JAXBElement<?> createDefaultDecoratorBounds(final GraphicsType graphic, final String routingType) throws CanoniserException {
        final LayoutRectangleType rect = getContext().getYawlObjectFactory().createLayoutRectangleType();
        final NumberFormat nf = getContext().getYawlNumberFormat();
        rect.setH(nf.format(autoLayoutInfo.getDecoratorHeight()));
        rect.setW(nf.format(autoLayoutInfo.getDecoratorWidth()));
        rect.setX(nf.format(autoLayoutInfo.getDecoratorX(routingType, yawlElement, elementBounds)));
        rect.setY(nf.format(autoLayoutInfo.getDecoratorY(routingType, yawlElement, elementBounds)));
        return getContext().getYawlObjectFactory().createLayoutAttributesFactsTypeBounds(rect);
    }

    private JAXBElement<?> createDefaultDecoratorSize() {
        final LayoutDimensionType dimension = getObjectFactory().createLayoutDimensionType();
        dimension.setH(BigInteger.valueOf(autoLayoutInfo.getDecoratorHeight()));
        dimension.setW(BigInteger.valueOf(autoLayoutInfo.getDecoratorWidth()));
        return getContext().getYawlObjectFactory().createLayoutAttributesFactsTypeSize(dimension);
    }

    private String convertSplitRouting(final ExternalTaskFactsType yawlTask) {
        switch (yawlTask.getSplit().getCode()) {
        case AND:
            return "AND_split";
        case OR:
            return "OR_split";
        case XOR:
            return "XOR_split";

        default:
            return "";
        }
    }

    private String convertJoinRouting(final ExternalTaskFactsType yawlTask) {
        switch (yawlTask.getJoin().getCode()) {
        case AND:
            return "AND_join";
        case OR:
            return "OR_join";
        case XOR:
            return "XOR_join";

        default:
            return "";
        }
    }

}