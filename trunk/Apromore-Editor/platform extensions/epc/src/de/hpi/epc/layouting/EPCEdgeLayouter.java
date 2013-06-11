/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig, Matthias Weidlich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.hpi.epc.layouting;

import java.util.Iterator;
import java.util.Random;

import de.hpi.epc.layouting.model.EPCType;
import de.hpi.layouting.grid.Grid;
import de.hpi.layouting.grid.Grid.Cell;
import de.hpi.layouting.model.LayoutingBounds;
import de.hpi.layouting.model.LayoutingBoundsImpl;
import de.hpi.layouting.model.LayoutingDockers;
import de.hpi.layouting.model.LayoutingElement;

/**
 * Simple layouting of edges based on the edge layouter of team Royal Fawn.
 *
 * @author matthias.weidlich
 */
public class EPCEdgeLayouter {

    private Grid<LayoutingElement> grid;
    private LayoutingElement edge;

    private LayoutingElement source;
    private LayoutingElement target;
    private LayoutingBounds sourceGeometry;
    private LayoutingBounds targetGeometry;

    // Relative coordinates
    private double sourceRelativCenterX;
    private double sourceRelativCenterY;
    private double targetRelativCenterX;
    private double targetRelativCenterY;

    // absolute coordinates
    private double sourceAbsoluteCenterX;
    private double sourceAbsoluteCenterY;
    private double sourceAbsoluteY;
    private double sourceAbsoluteX2;
    private double sourceAbsoluteY2;
    private double targetAbsoluteCenterX;
    private double targetAbsoluteCenterY;
    private double targetAbsoluteX;
    private double targetAbsoluteY;
    private double targetAbsoluteY2;

    // layout hints
    private boolean sourceJoin;
    private boolean sourceSplit;
    private boolean targetJoin;
    private boolean backwards;

    public EPCEdgeLayouter(Grid<LayoutingElement> grid, LayoutingElement edge) {
        this.edge = edge;
        this.grid = grid;

        displayEdge(edge);
        displayGrid(grid);

        calculateGlobals();
        pickLayoutForEdge();
    }

    private void displayEdge(LayoutingElement edge) {
        System.out.println("Incoming: " + edge.getIncomingLinks().size() + " - Outgoing: " + edge.getOutgoingLinks().size());
    }

    private void displayGrid(Grid<LayoutingElement> grid) {
        Cell<LayoutingElement> cell;
        Iterator<Cell<LayoutingElement>> cells;

        for (Grid.Row<LayoutingElement> row : grid) {
            cells = row.iterator();
            while (cells.hasNext()) {
                cell = cells.next();

                if (cell != null && cell.getValue() != null) {
                    if (cell != null) {
                        System.out.println("CELL: " + cell.getValue().getId() + " - " + cell.getValue().getType());
                    } else {
                        System.out.println("Cell empty");
                    }
                }
            }
        }
    }

    private void calculateGlobals() {
        // should both be only one !
        if (!edge.getIncomingLinks().isEmpty()) {
            source = edge.getIncomingLinks().get(0);
        } else {
            throw new RuntimeException("edge has no source : " + edge.getId());
        }
        if (!edge.getOutgoingLinks().isEmpty()) {
            target = edge.getOutgoingLinks().get(0);
        } else {
            throw new RuntimeException("edge has no target : " + edge.getId());
        }

        sourceGeometry = source.getGeometry();
        targetGeometry = target.getGeometry();

        // get relative centers of elements
        sourceRelativCenterX = sourceGeometry.getWidth() / 2;
        sourceRelativCenterY = sourceGeometry.getHeight() / 2;
        targetRelativCenterX = targetGeometry.getWidth() / 2;
        targetRelativCenterY = targetGeometry.getHeight() / 2;

        // get parent adjustments
        double sourceParentAdjustmentX = 0;
        double sourceParentAdjustmentY = 0;
        LayoutingElement parent = source.getParent();
        while (parent != null) {
            sourceParentAdjustmentX += parent.getGeometry().getX();
            sourceParentAdjustmentY += parent.getGeometry().getY();
            parent = parent.getParent();
        }

        double targetParentAdjustmentX = 0;
        double targetParentAdjustmentY = 0;
        parent = target.getParent();
        while (parent != null) {
            targetParentAdjustmentX += parent.getGeometry().getX();
            targetParentAdjustmentY += parent.getGeometry().getY();
            parent = parent.getParent();
        }

        // get absolute coordinates
        double sourceAbsoluteX = sourceGeometry.getX() + sourceParentAdjustmentX;
        sourceAbsoluteY = sourceGeometry.getY() + sourceParentAdjustmentY;
        sourceAbsoluteX2 = sourceGeometry.getX2() + sourceParentAdjustmentX;
        sourceAbsoluteY2 = sourceGeometry.getY2() + sourceParentAdjustmentY;

        targetAbsoluteX = targetGeometry.getX() + targetParentAdjustmentX;
        targetAbsoluteY = targetGeometry.getY() + targetParentAdjustmentY;
        targetAbsoluteY2 = targetGeometry.getY2() + targetParentAdjustmentY;

        sourceAbsoluteCenterX = sourceAbsoluteX + sourceRelativCenterX;
        sourceAbsoluteCenterY = sourceAbsoluteY + sourceRelativCenterY;
        targetAbsoluteCenterX = targetAbsoluteX + targetRelativCenterX;
        targetAbsoluteCenterY = targetAbsoluteY + targetRelativCenterY;

        // layout hints
        sourceJoin = source.isJoin();
        sourceSplit = source.isSplit();
        targetJoin = target.isJoin();
        target.isSplit();
        backwards = sourceAbsoluteCenterX > targetAbsoluteCenterX;
    }

    private void pickLayoutForEdge() {
        if (EPCType.ControlFlow.equals(edge.getType())) {
            pickLayoutForSequenceFlow();
        } else {
            pickLayoutForOtherConnection();
        }
    }

    private void pickLayoutForOtherConnection() {
        setEdgeDirectCenter();
    }

    private void pickLayoutForSequenceFlow() {
        // if on the same x or y and nothing between -> make direct connection
        // something between -> up corner
        if (sourceAbsoluteCenterX == targetAbsoluteCenterX) {
            setEdgeDirectCenter();
            return;
        } else if (sourceAbsoluteCenterY == targetAbsoluteCenterY) {
            if (areCellsHorizontalFree()) {
                setEdgeDirectCenter();
            } else {
                setEdgeAroundTheCorner(true);
            }
            return;
        }

        if (sourceAbsoluteCenterX <= targetAbsoluteCenterX && sourceAbsoluteCenterY <= targetAbsoluteCenterY) {
            if (sourceJoin && sourceSplit) {
                setEdgeStepRight();
                return;
            } else if (sourceSplit) {
                setEdge90DegreeRightUnderClockwise();
                return;
            } else if (targetJoin) {
                setEdge90DegreeRightUnderAntiClockwise();
                return;
            }
        } else if (sourceAbsoluteCenterX > targetAbsoluteCenterX && sourceAbsoluteCenterY <= targetAbsoluteCenterY) {
            // target is left under
            if (sourceJoin && sourceSplit) {
                setEdgeStepRight();
                return;
            } else if (sourceSplit) {
                setEdge90DegreeRightAboveAntiClockwise();
                return;
            } else if (targetJoin) {
                setEdge90DegreeRightAboveClockwise();
                return;
            }
        }

        if (sourceJoin && sourceSplit && (!backwards)) {
            setEdgeStepRight();
            return;
        }

        if (sourceSplit && targetJoin) {
            setEdgeAroundTheCorner(true);
            return;
        }

        setEdgeDirectCenter();
    }

    private boolean areCellsHorizontalFree() {
        if (grid == null || source.getParent() != target.getParent()) {
            return (Math.abs(sourceAbsoluteCenterX - targetAbsoluteCenterX) < 210);
        }

        Cell<LayoutingElement> fromCell;
        Cell<LayoutingElement> toCell;

        if (sourceAbsoluteCenterX < targetAbsoluteCenterX) {
            fromCell = grid.getCellOfItem(source);
            toCell = grid.getCellOfItem(target);
        } else {
            fromCell = grid.getCellOfItem(target);
            toCell = grid.getCellOfItem(source);
        }

        fromCell = fromCell.getNextCell();
        while (fromCell != toCell) {
            if (fromCell == null || fromCell.isFilled()) {
                return false;
            }
            fromCell = fromCell.getNextCell();
        }

        return true;
    }

    private void setEdgeDirectCenter() {
        double boundsMinX = Math.min(sourceAbsoluteCenterX, targetAbsoluteCenterX);
        double boundsMinY = Math.min(sourceAbsoluteCenterY, targetAbsoluteCenterY);
        double boundsMaxX = Math.max(sourceAbsoluteCenterX, targetAbsoluteCenterX);
        double boundsMaxY = Math.max(sourceAbsoluteCenterY, targetAbsoluteCenterY);

        // set bounds
        edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY));

        // set dockers - direct connection
        LayoutingDockers dockers = edge.getDockers();

        if (source.getType().equals(EPCType.TextNote)) {
            // TextAnnotation has its docker at the left
            dockers.setPoints(0, sourceRelativCenterY);
        } else {
            dockers.setPoints(sourceRelativCenterX, sourceRelativCenterY);
        }

        if (target.getType().equals(EPCType.TextNote)) {
            // TextAnnotation has its docker at the left
            dockers.addPoint(0, targetRelativCenterY);
        } else {
            dockers.addPoint(targetRelativCenterX, targetRelativCenterY);
        }

    }

    private void setEdge90DegreeRightAboveAntiClockwise() {
        double boundsMinX = sourceAbsoluteX2;
        double boundsMinY = targetAbsoluteY2;
        double boundsMaxX = targetAbsoluteCenterX;
        double boundsMaxY = sourceAbsoluteCenterY;
        set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY, boundsMaxX, boundsMaxY);
    }

    private void setEdge90DegreeRightAboveClockwise() {
        double boundsMinX = sourceAbsoluteCenterX;
        double boundsMinY = targetAbsoluteCenterY;
        double boundsMaxX = targetAbsoluteX;
        double boundsMaxY = sourceAbsoluteY;
        set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY, boundsMinX, boundsMinY);
    }

    private void setEdge90DegreeRightUnderAntiClockwise() {
        double boundsMinX = sourceAbsoluteCenterX;
        double boundsMinY = sourceAbsoluteY2;
        double boundsMaxX = targetAbsoluteX;
        double boundsMaxY = targetAbsoluteCenterY;
        set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY, boundsMinX, boundsMaxY);
    }

    private void setEdge90DegreeRightUnderClockwise() {
        double boundsMinX = sourceAbsoluteX2;
        double boundsMinY = sourceAbsoluteCenterY;
        double boundsMaxX = targetAbsoluteCenterX;
        double boundsMaxY = targetAbsoluteY;
        set90DegreeEdgeGeometry(boundsMinX, boundsMinY, boundsMaxX, boundsMaxY, boundsMaxX, boundsMinY);
    }

    private void set90DegreeEdgeGeometry(double boundsMinX, double boundsMinY, double boundsMaxX, double boundsMaxY, double cornerDockerX,
                                         double cornerDockerY) {
        // set bounds
        edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY));

        // set dockers
        edge.getDockers().setPoints(sourceRelativCenterX, sourceRelativCenterY, cornerDockerX, cornerDockerY, targetRelativCenterX,
                targetRelativCenterY);
    }

    private void setEdgeAroundTheCorner(boolean down) {
        int angleDistance = 15;
        double height = Math.max(sourceGeometry.getHeight() / 2, targetGeometry.getHeight() / 2) + 20;
        height += new Random().nextInt(5) * 3;

        // make bounding box
        double boundsMinX = Math.min(sourceAbsoluteCenterX, targetAbsoluteCenterX);
        double boundsMinY = Math.min(sourceAbsoluteCenterY, targetAbsoluteCenterY);
        double boundsMaxX = Math.max(sourceAbsoluteCenterX, targetAbsoluteCenterX);
        double boundsMaxY = Math.max(sourceAbsoluteCenterY, targetAbsoluteCenterY);

        if (down) {
            boundsMaxY += height;
        } else {
            boundsMinY -= height;
        }

        // set bounds
        edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY));

        // set dockers
        double docker1X = sourceAbsoluteCenterX;
        double docker2X = targetAbsoluteCenterX;
        if (backwards) {
            docker1X += angleDistance;
            docker2X -= angleDistance;
        } else {
            docker1X += angleDistance;
            docker2X -= angleDistance;
        }

        double docker1Y;
        double docker2Y;
        if (down) {
            docker1Y = boundsMaxY;
            docker2Y = boundsMaxY;
        } else {
            docker1Y = boundsMinY;
            docker2Y = boundsMinY;
        }

        edge.getDockers().setPoints(sourceRelativCenterX, sourceRelativCenterY, docker1X, docker1Y, docker2X, docker2Y, targetRelativCenterX,
                targetRelativCenterY);
    }

    private void setEdgeStepRight() {
        // make bounding box
        double boundsMinX = Math.min(sourceAbsoluteCenterX, targetAbsoluteCenterX);
        double boundsMinY = Math.min(sourceAbsoluteCenterY, targetAbsoluteCenterY);
        double boundsMaxX = Math.max(sourceAbsoluteCenterX, targetAbsoluteCenterX);
        double boundsMaxY = Math.max(sourceAbsoluteCenterY, targetAbsoluteCenterY);

        // set bounds
        edge.setGeometry(new LayoutingBoundsImpl(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY));

        edge.getDockers().setPoints(sourceRelativCenterX, sourceRelativCenterY, targetRelativCenterX, targetRelativCenterY);
    }
}
