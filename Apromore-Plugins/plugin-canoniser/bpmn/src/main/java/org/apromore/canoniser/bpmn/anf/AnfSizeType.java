package org.apromore.canoniser.bpmn.anf;

// Java 2 Standard packages
import java.math.BigDecimal;

// Local packages
import org.apromore.anf.SizeType;
import org.omg.spec.dd._20100524.dc.Bounds;

/**
 * ANF 0.3 size element.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AnfSizeType extends SizeType {

    /** No-arg constructor. */
    public AnfSizeType() {
        super();
    }

    /**
     * Construct a size corresponding to an OMG DC Bounds.
     *
     * @param bounds  an OMG DC Bounds
     */
    public AnfSizeType(final Bounds bounds) {
        setWidth(new BigDecimal(bounds.getWidth()));
        setHeight(new BigDecimal(bounds.getHeight()));
    }
}
