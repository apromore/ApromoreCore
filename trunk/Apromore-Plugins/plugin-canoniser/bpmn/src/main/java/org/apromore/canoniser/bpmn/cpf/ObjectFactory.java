package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.TaskType;

/**
 * Element factory for a CPF 0.6 object model with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
@XmlRegistry
public class ObjectFactory extends org.apromore.cpf.ObjectFactory {

    @Override
    public EdgeType createEdgeType() {
        return new CpfEdgeType();
    }

    @Override
    public EventType createEventType() {
        return new CpfEventType();
    }

    @Override
    public TaskType createTaskType() {
        return new CpfTaskType();
    }
}
