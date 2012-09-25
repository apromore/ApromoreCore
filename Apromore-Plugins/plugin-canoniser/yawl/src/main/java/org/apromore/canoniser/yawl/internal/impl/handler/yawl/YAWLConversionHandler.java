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
package org.apromore.canoniser.yawl.internal.impl.handler.yawl;

import org.apromore.anf.AnnotationType;
import org.apromore.anf.DocumentationType;
import org.apromore.anf.GraphicsType;
import org.apromore.canoniser.yawl.internal.impl.context.YAWLConversionContext;
import org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandlerImpl;
import org.w3c.dom.Element;

/**
 * Abstract base class for a YAWL -> CPF handler
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 * @param <T>
 *            type of Element to be converted
 * @param <E>
 *            type of the already converted Parent
 */
public abstract class YAWLConversionHandler<T, E> extends ConversionHandlerImpl<T, E> {

    protected static final String NET_ID_PREFIX = "N";

    protected static final String CONTROLFLOW_ID_PREFIX = "C";

    protected static final String RESOURCE_ID_PREFIX = "R";

    protected static final String DATA_ID_PREFIX = "D";

    /**
     * @return the YAWL conversion context
     */
    protected YAWLConversionContext getContext() {
        return (YAWLConversionContext) context;
    }

    /**
     * Add the extension Element (XML) to the ANF as xs:any
     * 
     * @param element
     *            any XML element
     * @param nodeId
     *            of the CPF element
     */
    protected void addToAnnotations(final Element element, final String nodeId) {
        AnnotationType extension = findAnnotation(nodeId);
        if (extension == null) {
            // Create new Annotation
            extension = ANF_FACTORY.createAnnotationType();
            extension.setCpfId(nodeId);
            extension.setId(generateUUID());
            extension.getAny().add(element);
            getContext().getAnnotationResult().getAnnotation().add(extension);
        }
    }

    /**
     * Add the extension Element (XML) to the ANF as xs:any for the whole process model
     * 
     * @param element
     *            any XML element
     */
    protected void addToAnnotations(final Element element) {
        // Create new Annotation
        final AnnotationType extension = ANF_FACTORY.createAnnotationType();
        extension.setId(generateUUID());
        extension.getAny().add(element);
        getContext().getAnnotationResult().getAnnotation().add(extension);
    }

    private AnnotationType findAnnotation(final String nodeId) {
        for (final AnnotationType annotation : getContext().getAnnotationResult().getAnnotation()) {
            if (!(annotation instanceof DocumentationType || annotation instanceof GraphicsType)) {
                // We just want to add an plain 'AnnotationType'
                if (nodeId.equals(annotation.getCpfId())) {
                    return annotation;
                }
            }
        }
        return null;
    }

}