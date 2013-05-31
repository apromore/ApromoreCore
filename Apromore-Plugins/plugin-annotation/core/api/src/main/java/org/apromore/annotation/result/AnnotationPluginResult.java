package org.apromore.annotation.result;

import org.apromore.anf.AnnotationsType;
import org.apromore.plugin.PluginResultImpl;

public class AnnotationPluginResult extends PluginResultImpl {

    private AnnotationsType annotationType;

    /**
     * Returns the annotations type (ANF).
     * @return the anf that was processed.
     */
    public AnnotationsType getAnnotationsType() {
        return annotationType;
    }

    /**
     * sets a new annotations type (ANF).
     * @param anf the new annotations type to set.
     */
    public void setAnnotationsType(AnnotationsType anf) {
        annotationType = anf;
    }

}
