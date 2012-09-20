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

import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutFrameType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;
import org.yawlfoundation.yawlschema.ObjectFactory;

/**
 * Convert the layout of a CPF NetType to YAWL
 * 
 * @author Felix Mannhardt (Bonn-Rhein-Sieg University oAS)
 * 
 */
public class NetGraphicsTypeHandler extends CanonicalElementHandler<GraphicsType, Specification> {

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final LayoutNetFactsType netLayout = createNetLayout(getObject());
        getConvertedParent().getNet().add(netLayout);

    }

    private LayoutNetFactsType createNetLayout(final GraphicsType graphic) {
        final ObjectFactory oF = getContext().getYawlObjectFactory();
        final LayoutNetFactsType netLayout = oF.createLayoutNetFactsType();
        netLayout.setId(generateUUID(graphic.getCpfId()));

        if (graphic.getFill() != null) {
            netLayout.setBgColor(ConversionUtils.convertColorToBigInteger(graphic.getFill().getColor()));
        }

        // Create viewport, frame and bounds element with the same information as CPF only contains one size
        final LayoutFrameType viewport = convertFrame(graphic);
        netLayout.getBoundsOrFrameOrViewport().add(oF.createLayoutNetFactsTypeViewport(viewport));

        final LayoutFrameType frame = convertFrame(graphic);
        netLayout.getBoundsOrFrameOrViewport().add(oF.createLayoutNetFactsTypeFrame(frame));

        final LayoutFrameType bounds = convertFrame(graphic);
        netLayout.getBoundsOrFrameOrViewport().add(oF.createLayoutNetFactsTypeBounds(bounds));

        return netLayout;
    }

    /**
     * Converts the ANF GraphicsType to an YAWL frame omitting the position information.
     * 
     * @param graphic
     *            of ANF
     * @return YAWL frame
     */
    private LayoutFrameType convertFrame(final GraphicsType graphic) {
        final LayoutFrameType frame = getContext().getYawlObjectFactory().createLayoutFrameType();

        if (graphic.getSize() != null) {
            frame.setH(graphic.getSize().getHeight().toBigInteger());
            frame.setW(graphic.getSize().getWidth().toBigInteger());
        } else {
            // Some default values
            frame.setH(BigInteger.valueOf(800));
            frame.setW(BigInteger.valueOf(800));
        }

        if (graphic.getPosition().size() == 1) {
            frame.setX(graphic.getPosition().get(0).getX().toBigInteger());
            frame.setY(graphic.getPosition().get(0).getY().toBigInteger());
        } else {
            // Some default value
            frame.setX(BigInteger.valueOf(0));
            frame.setY(BigInteger.valueOf(0));
        }
        return frame;
    }

}