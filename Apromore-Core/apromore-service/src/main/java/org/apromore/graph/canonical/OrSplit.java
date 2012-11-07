package org.apromore.graph.canonical;

import java.util.UUID;

/**
 * Implementation of the Canonical Split Node.
 *
 * @author Cameron James
 */
public class OrSplit extends Split implements IOrSplit {

    /**
     * Empty constructor.
     */
    public OrSplit() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public OrSplit(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public OrSplit(String label, String desc) {
        super(label, desc);
    }

    @Override
    public OrSplit clone() {
        OrSplit clone = (OrSplit) super.clone();
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
