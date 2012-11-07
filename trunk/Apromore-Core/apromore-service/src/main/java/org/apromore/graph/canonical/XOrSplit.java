package org.apromore.graph.canonical;

import java.util.UUID;

/**
 * Implementation of the Canonical Split Node.
 *
 * @author Cameron James
 */
public class XOrSplit extends Split implements IXOrSplit {

    /**
     * Empty constructor.
     */
    public XOrSplit() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public XOrSplit(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public XOrSplit(String label, String desc) {
        super(label, desc);
    }


    @Override
    public XOrSplit clone() {
        XOrSplit clone = (XOrSplit) super.clone();
        clone.setId(UUID.randomUUID().toString());

        if (this.getName() != null) {
            clone.setName(this.getName());
        }
        if (this.getDescription() != null) {
            clone.setDescription(this.getDescription());
        }

        return clone;
    }

}
