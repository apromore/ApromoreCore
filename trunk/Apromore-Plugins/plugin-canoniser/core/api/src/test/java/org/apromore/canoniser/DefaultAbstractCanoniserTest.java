package org.apromore.canoniser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.junit.Before;
import org.junit.Test;

public class DefaultAbstractCanoniserTest {

    private DefaultAbstractCanoniser defaultAbstractCanoniser;

    @Before
    public void setUp() {
        defaultAbstractCanoniser = new DefaultAbstractCanoniser() {

            @Override
            public PluginResult canonise(final InputStream nativeInput, final List<AnnotationsType> annotationFormat, final List<CanonicalProcessType> canonicalFormat,
                    final PluginRequest request) throws CanoniserException {
                throw new CanoniserException("not implemented");
            }

            @Override
            public PluginResult deCanonise(final CanonicalProcessType canonicalFormat, final AnnotationsType annotationFormat, final OutputStream nativeOutput,
                    final PluginRequest request) throws CanoniserException {
                throw new CanoniserException("not implemented");
            }

            @Override
            public PluginResult createInitialNativeFormat(OutputStream nativeOutput, String processName, String processVersion, String processAuthor,
                    Date processCreated, PluginRequest request) {
                return null;
            }

            @Override
            public CanoniserMetadataResult readMetaData(InputStream nativeInput, PluginRequest request) {
                return null;
            }
        };
    }

    @Test
    public void testGetNativeType() {
        assertNotNull(defaultAbstractCanoniser);
        assertNull(defaultAbstractCanoniser.getNativeType());
    }

}
