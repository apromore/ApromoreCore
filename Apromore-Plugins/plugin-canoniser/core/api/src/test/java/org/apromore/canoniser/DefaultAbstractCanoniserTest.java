package org.apromore.canoniser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Before;
import org.junit.Test;

public class DefaultAbstractCanoniserTest {

    private DefaultAbstractCanoniser defaultAbstractCanoniser;

    @Before
    public void setUp() {
        defaultAbstractCanoniser = new DefaultAbstractCanoniser() {

            @Override
            public void deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput)
                    throws CanoniserException {
                throw new CanoniserException("not implemented");
            }

            @Override
            public void canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat)
                    throws CanoniserException {
                throw new CanoniserException("not implemented");
            }
        };
    }


    @Test
    public void testGetNativeType() {
        assertNotNull(defaultAbstractCanoniser);
        assertNull(defaultAbstractCanoniser.getNativeType());
    }

}
