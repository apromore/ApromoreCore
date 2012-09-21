package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

// Local packages
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.WorkType;

/**
 * CPF 0.6 event with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfEventType extends EventType implements CpfNodeType {

    /** Incoming edges. */
    Set<EdgeType> incomingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    Set<EdgeType> outgoingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /**
     * @return every edge which has this node as its target
     */
    public Set<EdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * @return every edge which has this node as its source
     */
    public Set<EdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }
}
