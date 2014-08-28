/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.aris;

// Java 2 Standard packages

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.DefaultAbstractCanoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.PluginRequest;
import org.apromore.plugin.PluginResult;
import org.apromore.plugin.PluginResultImpl;
import org.springframework.stereotype.Component;

// Local packages

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
