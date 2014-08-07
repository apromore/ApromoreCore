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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.annotations;

import java.math.BigInteger;

import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.yawlfoundation.yawlschema.LayoutFactsType.Specification;
import org.yawlfoundation.yawlschema.LayoutFrameType;
import org.yawlfoundation.yawlschema.LayoutNetFactsType;

/**
 * Convert the layout of a CPF NetType to YAWL
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class NetGraphicsTypeHandler extends CanonicalElementHandler<GraphicsType, Specification> {

    private static final int DEFAULT_NET_HEIGHT = 800;
    private static final int DEFAULT_NET_WIDTH = 800;
    private static final int DEFAULT_NET_X = 0;
    private static final int DEFAULT_NET_Y = 0;

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
        final LayoutNetFactsType netLayout = YAWL_FACTORY.createLayoutNetFactsType();
        netLayout.setId(generateUUID(graphic.getCpfId()));

        if (graphic.getFill() != null) {
            netLayout.setBgColor(ConversionUtils.convertColorToBigInteger(graphic.getFill().getColor()));
        }

        // Create viewport, frame and bounds element with the same information as CPF only contains one size
        final LayoutFrameType viewport = convertFrame(graphic);
        netLayout.getBoundsOrFrameOrViewport().add(YAWL_FACTORY.createLayoutNetFactsTypeViewport(viewport));

        final LayoutFrameType frame = convertFrame(graphic);
        netLayout.getBoundsOrFrameOrViewport().add(YAWL_FACTORY.createLayoutNetFactsTypeFrame(frame));

        final LayoutFrameType bounds = convertFrame(graphic);
        netLayout.getBoundsOrFrameOrViewport().add(YAWL_FACTORY.createLayoutNetFactsTypeBounds(bounds));

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
        final LayoutFrameType frame = YAWL_FACTORY.createLayoutFrameType();

        if (graphic.getSize() != null) {
            frame.setH(graphic.getSize().getHeight().toBigInteger());
            frame.setW(graphic.getSize().getWidth().toBigInteger());
        } else {
            // Some default values
            frame.setH(BigInteger.valueOf(DEFAULT_NET_HEIGHT));
            frame.setW(BigInteger.valueOf(DEFAULT_NET_WIDTH));
        }

        if (graphic.getPosition().size() == 1) {
            frame.setX(graphic.getPosition().get(0).getX().toBigInteger());
            frame.setY(graphic.getPosition().get(0).getY().toBigInteger());
        } else {
            // Some default value
            frame.setX(BigInteger.valueOf(DEFAULT_NET_X));
            frame.setY(BigInteger.valueOf(DEFAULT_NET_Y));
        }
        return frame;
    }

}