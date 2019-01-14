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

import de.epml.TypeArc;
import de.epml.TypeFlow;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypeRelation;
import org.apromore.common.converters.epml.context.EPMLConversionContext;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicEdge;
import org.oryxeditor.server.diagram.basic.BasicShape;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class TypeArcHandler extends EPMLHandlerImpl {

    private final TypeArc arc;

    public TypeArcHandler(EPMLConversionContext context, TypeArc arc) {
        super(context);
        this.arc = arc;
    }

    @Override
    public BasicEdge convert() {
        BasicEdge edge;

        if (arc.getFlow() != null) {
            edge = createControlFlowShape(arc.getFlow());
        } else {
            edge = createRelationShape(arc.getRelation());
        }

        edge.setDockers(convertPosition(arc.getGraphics()));
        edge.setBounds(convertBounds(arc.getGraphics()));

        return edge;
    }

    private Bounds convertBounds(List<TypeMove> graphics) {
        if (graphics.size() == 1) {
            TypeMove typeMove = graphics.get(0);
            if (typeMove.getPosition().size() >= 2) {
                TypeMove2 firstPos = typeMove.getPosition().get(0);
                TypeMove2 lastPos = typeMove.getPosition().get(typeMove.getPosition().size() - 1);

                Point upperLeft = new Point(firstPos.getX(), firstPos.getY());
                Point lowerRight = new Point(lastPos.getX(), lastPos.getY());

                return new Bounds(upperLeft, lowerRight);
            } else {
                Bounds artificalBounds = new Bounds();
                artificalBounds.setCoordinates(10, 10, 20, 20);
                return artificalBounds;
            }
        } else {
            Bounds artificalBounds = new Bounds();
            artificalBounds.setCoordinates(10, 10, 20, 20);
            return artificalBounds;
        }
    }

    private List<Point> convertPosition(List<TypeMove> graphics) {
        List<Point> points = new ArrayList<Point>();

        for (TypeMove move : graphics) {
            final int positionCount = move.getPosition().size();
            if (positionCount > 0) {
                for (int i = 0; i < positionCount; i++) {
                    TypeMove2 position = move.getPosition().get(i);
                    BigDecimal x = position.getX();
                    BigDecimal y = position.getY();

                    // Locate the source of this arc
                    BigInteger sourceId = null;
                    if (arc.getFlow() != null) {
                         sourceId = arc.getFlow().getSource();
                    } else if (arc.getRelation() != null) {
                         sourceId = arc.getRelation().getSource();
                    }

                    // If the arc has a source, the first waypoint generates a docker attached to the source shape's coordinate system
                    if (i == 0 && sourceId != null && getContext().getShape(sourceId) != null) {
                        if (getContext().getShape(sourceId) == null) {
                            java.util.logging.Logger.getAnonymousLogger().info("Source shape " + sourceId + " of arc " + arc.getId() + " missing");
                        } else {
                            Bounds bounds = getContext().getShape(sourceId).getBounds();
                            x = x.subtract(new BigDecimal(bounds.getUpperLeft().getX()));
                            y = y.subtract(new BigDecimal(bounds.getUpperLeft().getY()));
                        }
                    }

                    // Locate the target of this arc
                    BigInteger targetId = null;
                    if (arc.getFlow() != null) {
                         targetId = arc.getFlow().getTarget();
                    } else if (arc.getRelation() != null) {
                         targetId = arc.getRelation().getTarget();
                    }

                    // If the arc has a target, the last waypoint generates a docker attached to the target shape's coordinate system
                    if (i == positionCount - 1 && targetId != null && getContext().getShape(targetId) != null) {
                        if (getContext().getShape(targetId) == null) {
                            java.util.logging.Logger.getAnonymousLogger().info("Target shape " + targetId + " of arc " + arc.getId() + " missing");
                        } else {
                            Bounds bounds = getContext().getShape(targetId).getBounds();
                            x = x.subtract(new BigDecimal(bounds.getUpperLeft().getX()));
                            y = y.subtract(new BigDecimal(bounds.getUpperLeft().getY()));
                        }
                    }

                    points.add(new Point(x, y));
                }
            }
        }

        return points;
    }

    private BasicEdge createRelationShape(TypeRelation typeRelation) {
        BasicEdge basicEdge = new BasicEdge(arc.getId().toString(), "Relation");
        connectEdge(basicEdge, typeRelation.getSource(), typeRelation.getTarget());
        if (!"role".equals(typeRelation.getType())) {
            basicEdge.setProperty("informationflow", "True");
        }
        return basicEdge;
    }

    private BasicEdge createControlFlowShape(TypeFlow typeFlow) {
        BasicEdge basicEdge = new BasicEdge(arc.getId().toString(), "ControlFlow");
        connectEdge(basicEdge, typeFlow.getSource(), typeFlow.getTarget());
        return basicEdge;
    }

    private void connectEdge(BasicEdge basicEdge, BigInteger source, BigInteger target) {
        List<BasicShape> outgoings = new ArrayList<BasicShape>();
        BasicShape outgoingShape = getContext().getShape(target);
        if (outgoingShape != null) {
            outgoings.add(outgoingShape);
        }
        basicEdge.setOutgoingsAndUpdateTheirIncomings(outgoings);

        List<BasicShape> incomings = new ArrayList<BasicShape>();
        BasicShape incomingShape = getContext().getShape(source);
        if (incomingShape != null) {
            incomings.add(incomingShape);
        }
        basicEdge.setIncomingsAndUpdateTheirOutgoings(incomings);
    }

}
