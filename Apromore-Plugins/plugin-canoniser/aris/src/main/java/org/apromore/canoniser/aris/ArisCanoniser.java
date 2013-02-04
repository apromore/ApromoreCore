package org.apromore.canoniser.aris;

// Java 2 Standard packages
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

// Local packages
import generated.*;
import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.*;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.impl.PluginRequestImpl;
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
            // Convert AML fragment into a CPF with the ANF annotations embedded as extension elements
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(
                new StreamSource(getClass().getClassLoader().getResourceAsStream("xsd/aml2cpf.xsl"))
            );
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(arisInput), new StreamResult(baos));

            // Strip out the embedded ANF to obtain clean CPF
            transformer = transformerFactory.newTransformer(
                new StreamSource(getClass().getClassLoader().getResourceAsStream("xsd/cpf2cpf.xsl"))
            );
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(new ByteArrayInputStream(baos.toByteArray())), new StreamResult(baos2));
            final CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(new ByteArrayInputStream(baos2.toByteArray()), true /* is validating */).getValue();
            
            // Extract the embedded ANF to obtain clean ANF
            transformer = transformerFactory.newTransformer(
                new StreamSource(getClass().getClassLoader().getResourceAsStream("xsd/cpf2anf.xsl"))
            );
            baos2.reset();
            transformer.transform(new StreamSource(new ByteArrayInputStream(baos.toByteArray())), new StreamResult(baos2));
            final AnnotationsType anf = ANFSchema.unmarshalAnnotationFormat(new ByteArrayInputStream(baos2.toByteArray()), true /* is validating */).getValue();

            // Package and return the result
            canonicalFormat.add(cpf);
            annotationFormat.add(anf);
            PluginResult result = new PluginResultImpl();
            return result;

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
