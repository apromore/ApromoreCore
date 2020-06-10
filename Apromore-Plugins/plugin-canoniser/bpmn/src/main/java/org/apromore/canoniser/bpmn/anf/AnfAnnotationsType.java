/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2013 Felix Mannhardt.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.canoniser.bpmn.anf;

// Java 2 Standard packages
import java.io.InputStream;
import java.io.OutputStream;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

// Local packages
import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.anf.ObjectFactory;
import org.apromore.canoniser.bpmn.IdFactory;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.omg.spec.bpmn._20100524.di.BPMNDiagram;
import org.omg.spec.bpmn._20100524.di.BPMNEdge;
import org.omg.spec.bpmn._20100524.di.BPMNShape;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;
import org.omg.spec.dd._20100524.di.DiagramElement;


/**
 * ANF 0.3 top-level document element.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class AnfAnnotationsType extends AnnotationsType {

    /** Qualified name of the root element <code>anf:Annotations</code>. */
    private static final QName ANF_ROOT = new QName("http://www.apromore.org/ANF", "Annotations");

    /** XML schema for ANF 0.3. */
    private static final Schema ANF_SCHEMA;

    static {
        //ClassLoader loader = AnfAnnotationsType.class.getClassLoader();
        try {
            //ANF_SCHEMA  = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI).newSchema(
            //    new StreamSource(loader.getResourceAsStream("xsd/anf_0.3.xsd"))
            //);
			ANF_SCHEMA = ANFSchema.getANFSchema();
        } catch (SAXException e) {
            throw new RuntimeException("Unable to parse ANF schema", e);
        }
    }

    /** No-arg constructor. */
    public AnfAnnotationsType() { }

    /**
     * Construct an ANF document corresponding to a BPMNDI Diagram element.
     *
     * The resulting document must have its <code>uri</code> element set in order to be schema-legal.
     *
     * @param definitions  a BPMN document, from which documentation annotations will be taken; may be <code>null</code>
     * @param diagram  a BPMNDI Diagram element from <code>definitions</code>, from which graphics annotations will be taken; may be <code>null</code>
     */
    public AnfAnnotationsType(final BpmnDefinitions definitions, final BPMNDiagram diagram) {

        // Generator for identifiers scoped to this ANF document
        final IdFactory anfIdFactory = new IdFactory();

        if (definitions != null) {
            // TODO - extract DocumentationTypes from the BPMN semantics
        }

        if (diagram != null) {
            for (JAXBElement<? extends DiagramElement> element : diagram.getBPMNPlane().getDiagramElement()) {
                element.getValue().accept(new BaseVisitor() {
                    @Override
                    public void visit(final BPMNEdge edge) {
                        getAnnotation().add(new AnfGraphicsType(edge, anfIdFactory));
                    }

                    @Override
                    public void visit(final BPMNShape shape) {
                        getAnnotation().add(new AnfGraphicsType(shape, anfIdFactory));
                    }
                });
            }
        }
    }

    /**
     * Construct an instance from an input stream.
     *
     * @param in  an ANF-formatted stream
     * @param validate  whether to perform schema validation during parsing
     * @throws JAXBException if the stream can't be successfully parsed
     * @return the parsed instance
     */
    public static AnnotationsType newInstance(final InputStream in, final Boolean validate) throws JAXBException {
        Unmarshaller unmarshaller = JAXBContext.newInstance(ANFSchema.ANF_CONTEXT, ObjectFactory.class.getClassLoader()).createUnmarshaller();
        if (validate) {
            unmarshaller.setSchema(ANF_SCHEMA);
        }
        return unmarshaller.unmarshal(new StreamSource(in), AnnotationsType.class)
                           .getValue();
    }

   /**
     * Write this instance to a stream.
     *
     * @param out  the destination stream
     * @param validate  whether to perform schema validation during serialization
     * @throws JAXBException if serialization fails
     */
    public void marshal(final OutputStream out, final Boolean validate) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(ANFSchema.ANF_CONTEXT, ObjectFactory.class.getClassLoader()).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        if (validate) {
            marshaller.setSchema(ANF_SCHEMA);
        }
        marshaller.marshal(new JAXBElement<AnnotationsType>(ANF_ROOT, AnnotationsType.class, this), out);
    }

}
