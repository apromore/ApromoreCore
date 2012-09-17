/**
 * Copyright (c) 2011-2012 Felix Mannhardt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */
package org.apromore.common.converters.epml.handler.epml.impl;

import de.epml.TypePosition;
import org.apromore.common.converters.epml.context.EPMLConversionContext;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicShape;

import java.math.BigInteger;
import java.util.Map;

/**
 * Base class for all converters that create a Signavio/Oryx node
 *
 * @author Felix Mannhardt
 */
public abstract class NodeHandler extends EPMLHandlerImpl {

    public NodeHandler(EPMLConversionContext context) {
        super(context);
    }

    /* (non-Javadoc)
     * @see org.apromore.common.converters.epml.EPMLObjectConverter#convert()
     */
    @Override
    public BasicShape convert() {
        BasicShape shape = createShape();
        TypePosition position = getPosition();
        if (position != null) {
            shape.setBounds(convertPosition(position));
            //shape.getLabelSettings().add(new LabelSettings());
        }
        shape.setProperties(convertProperties());
        getContext().addShape(getShapeId(), shape);
        return shape;
    }

    abstract protected Map<String, String> convertProperties();

    abstract protected TypePosition getPosition();

    abstract protected BasicShape createShape();

    abstract protected BigInteger getShapeId();

    protected Bounds convertPosition(TypePosition position) {
        Bounds bounds = new Bounds();
        Point leftUpper = new Point(position.getX(), position.getY());
        Point rightLower = new Point(position.getX().add(position.getWidth()), position.getY().add(position.getHeight()));
        bounds.setCoordinates(leftUpper, rightLower);
        return bounds;
    }

}