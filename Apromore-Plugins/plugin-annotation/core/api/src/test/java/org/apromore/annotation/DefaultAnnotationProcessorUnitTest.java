package org.apromore.annotation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apromore.anf.AnnotationsType;
import org.apromore.annotation.exception.AnnotationProcessorException;
import org.apromore.annotation.result.AnnotationPluginResult;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Before;
import org.junit.Test;

public class DefaultAnnotationProcessorUnitTest {

    private DefaultAbstractAnnotationProcessor defaultAbstractAnnotationProcessor;

    @Before
    public void setUp() {
        defaultAbstractAnnotationProcessor = new DefaultAbstractAnnotationProcessor() {
            @Override
            public AnnotationPluginResult processAnnotation(CanonicalProcessType canonisedFormat, AnnotationsType annotationFormat)
                    throws AnnotationProcessorException {
                throw new AnnotationProcessorException("not implemented");
            }
        };
    }

    @Test
    public void testGetSourceProcessType() {
        assertNotNull(defaultAbstractAnnotationProcessor);
        assertNull(defaultAbstractAnnotationProcessor.getProcessFormatProcessor());
    }

}
