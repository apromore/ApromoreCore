package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;

// Local packages
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.plugin.property.PropertyType;

/**
 * Canoniser for Business Process Model and Notation (BPMN) 2.0.
 *
 * @see <a href="http://www.bpmn.org">Object Management Group BPMN site</a>
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class BPMN20Canoniser implements Canoniser {

    // Methods implementing Canoniser

    /** {@inheritDoc} */
    @Override
    public String getNativeType() {
        return "BPMN 2.0";
    }

    /** {@inheritDoc} */
    @Override
    public void canonise(final InputStream                bpmnInput,
                         final List<AnnotationsType>      annotationFormat,
                         final List<CanonicalProcessType> canonicalFormat) throws CanoniserException {

        try {
            CanoniserResult result = JAXBContext.newInstance(BpmnObjectFactory.class,
                                                             org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                             org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                             org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                             org.omg.spec.dd._20100524.di.ObjectFactory.class)
                                                .createUnmarshaller()
                                                .unmarshal(new StreamSource(bpmnInput), CanoniserDefinitions.class)
                                                .getValue()  // discard the JAXBElement wrapper
                                                .canonise();
            for (int i = 0; i < result.size(); i++) {
                annotationFormat.add(result.getAnf(i));
                canonicalFormat.add(result.getCpf(i));
            }
        } catch (Exception e) {
            throw new CanoniserException("Could not canonise to BPMN stream", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deCanonise(final CanonicalProcessType canonicalFormat,
                           final AnnotationsType      annotationFormat,
                           final OutputStream         bpmnOutput) throws CanoniserException {

        try {
            Marshaller marshaller = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                            org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                            org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                            org.omg.spec.dd._20100524.di.ObjectFactory.class)
                                               .createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(new CanoniserDefinitions(canonicalFormat, annotationFormat), bpmnOutput);
        } catch (Exception e) {
            throw new CanoniserException("Could not decanonise from BPMN stream", e);
        }
    }

    // Methods implementing PropertyAwarePlugin (superinterface of Canoniser)

    /** {@inheritDoc} */
    @Override
    public Set<PropertyType> getAvailableProperties() {
        return Collections.emptySet();
    }

    /** {@inheritDoc} */
    @Override
    public Set<PropertyType> getMandatoryProperties() {
        return Collections.emptySet();
    }

    // Methods implementing Plugin (superinterface of PropertyAwarePlugin, superinterface of Canoniser)

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return BPMN20Canoniser.class.getCanonicalName();
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return "1.0";
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return Canoniser.class.getCanonicalName();
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return "Implements only the descriptive subclass of BPMN 2.0.";
    }
}
