package org.apromore.canoniser.aris;

// Java 2 Standard packages
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.impl.PluginResultImpl;
import org.springframework.stereotype.Component;

/**
 * Canoniser for ARIS Markup Language (AML).
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Component("arisCanoniser")
public class ArisCanoniser extends DefaultAbstractCanoniser {

    // Methods implementing Canoniser interface

    /** {@inheritDoc} */
    @Override
    public PluginResult canonise(final InputStream                arisInput,
                                 final List<AnnotationsType>      annotationFormat,
                                 final List<CanonicalProcessType> canonicalFormat,
                                 final PluginRequest request) throws CanoniserException {

        try {
            throw new Exception("Not implemented");
        } catch (Exception e) {
            throw new CanoniserException("Could not canonise to ARIS stream", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public PluginResult createInitialNativeFormat(final OutputStream arisOutput,
                                                  final String processName,
                                                  final String processVersion,
                                                  final String processAuthor,
                                                  final Date processCreated,
                                                  final PluginRequest request) {

        PluginResultImpl result = newPluginResult();
        try {
            throw new Exception("Not implemented");
        } catch (Exception e) {
            result.addPluginMessage("Failed to create empty ARIS model: {0}", e.getMessage());
        }
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public PluginResult deCanonise(final CanonicalProcessType canonicalFormat,
                                   final AnnotationsType      annotationFormat,
                                   final OutputStream         arisOutput,
                                   final PluginRequest        request) throws CanoniserException {

        try {
            throw new Exception("Not implemented");
        } catch (Exception e) {
            throw new CanoniserException("Could not decanonise from ARIS stream", e);
        }
    }

    /**
     * {@inheritDoc}
     * @return a result expressing just the name of the ARIS process, or <code>null</code> if any exception occurs internally
     */
    @Override
    public CanoniserMetadataResult readMetaData(final InputStream bpmnInput, final PluginRequest request) {
        try {
            CanoniserMetadataResult result = new CanoniserMetadataResult();
            return result;
        } catch (Exception e) {
            return null;
        }
    }
}
