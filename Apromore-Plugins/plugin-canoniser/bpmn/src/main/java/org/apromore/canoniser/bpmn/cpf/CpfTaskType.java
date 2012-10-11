package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;

/**
 * CPF 0.6 task with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfTaskType extends TaskType implements CpfNodeType {

    // Extension attribute names

    /** {@link TypeAttribute#name} indicating a BPMN CallActivity's called element. */
    public static final String CALLED_ELEMENT = "calledElement";

    /** {@link TypeAttribute#name} indicating a BPMN event-triggered subprocess. */
    public static final String TRIGGERED_BY_EVENT = "bpmnTriggeredByEvent";

    // Internal state

    /** Incoming edges. */
    private Set<EdgeType> incomingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    /** Outgoing edges. */
    private Set<EdgeType> outgoingEdges = new HashSet<EdgeType>();  // TODO - diamond operator

    // Accessor methods

    /** @return every edge which has this node as its target */
    public Set<EdgeType> getIncomingEdges() {
        return incomingEdges;
    }

    /** @return every edge which has this node as its source */
    public Set<EdgeType> getOutgoingEdges() {
        return outgoingEdges;
    }

    // Accessors for CPF extension attributes

    public QName getCalledElement() {

        // Check for an existing attribute with the right name
        for (TypeAttribute attribute : getAttribute()) {
            if (CALLED_ELEMENT.equals(attribute.getName())) {
                return QName.valueOf(attribute.getValue());
            }
        }

        // Didn't find a called element
        return null;
    }

    public void setCalledElement(QName id) {

        // Remove any existing attribute
        Iterator<TypeAttribute> i = getAttribute().iterator();
        while (i.hasNext()) {
            if (CALLED_ELEMENT.equals(i.next().getName())) {
                i.remove();
            }
        }

        if (id != null) {
            // Create a new attribute for the specified called element
            TypeAttribute attribute = new ObjectFactory().createTypeAttribute();
            attribute.setName(CALLED_ELEMENT);
            attribute.setValue(id.toString());
            getAttribute().add(attribute);

            assert id.equals(getCalledElement());
        } else {
            assert getCalledElement() == null;
        }
    }

    /** @return whether this task has any attribute named {@link #TRIGGERED_BY_EVENT}. */
    public boolean isTriggeredByEvent() {
        for (TypeAttribute attribute : getAttribute()) {
            if (TRIGGERED_BY_EVENT.equals(attribute.getName())) {
                return true;
            }
        }
        return false;
    }

    public void setTriggeredByEvent(Boolean value) {

        if (value) {
            // Check whether there's already an existing flag
            for (TypeAttribute attribute : getAttribute()) {
                if (TRIGGERED_BY_EVENT.equals(attribute.getName())) {
                    return;  // already flagged, so nothing needs to be changed
                }
            }

            // Didn't find an existing flag, so create and add one
            TypeAttribute attribute = new ObjectFactory().createTypeAttribute();
            attribute.setName(TRIGGERED_BY_EVENT);
            getAttribute().add(attribute);

            assert isTriggeredByEvent();

        } else {
            // Remove any existing flags
            Iterator<TypeAttribute> i = getAttribute().iterator();
            while (i.hasNext()) {
                if (TRIGGERED_BY_EVENT.equals(i.next().getName())) {
                    i.remove();
                }
            }

            assert !isTriggeredByEvent();
        }
    }
}
