package org.apromore.canoniser.bpmn.anf;

// Java 2 Standard packages
import java.math.BigDecimal;

// Local packages
import org.apromore.anf.PositionType;
import org.omg.spec.dd._20100524.dc.Bounds;
import org.omg.spec.dd._20100524.dc.Point;

/**
 * ANF 0.3 position element.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AnfPositionType extends PositionType {

    /** No-arg constructor. */
    public AnfPositionType() { }

    /**
     * Construct a position corresponding to the upper left corner of an OMG DC Bounds.
     *
     * @param bounds  an OMG DC Bounds
     */
    public AnfPositionType(final Bounds bounds) {
        setX(new BigDecimal(bounds.getX()));
        setY(new BigDecimal(bounds.getY()));
    }

    /**
     * Construct a position corresponding to an OMG DC Point.
     *
     * @param point  an OMG DC Point
     */
    public AnfPositionType(final Point point) {
        setX(new BigDecimal(point.getX()));
        setY(new BigDecimal(point.getY()));
    }
}
