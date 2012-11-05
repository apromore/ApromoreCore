package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;

// Local packages
import org.apromore.canoniser.bpmn.bpmn.ProcessWrapper;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.omg.spec.bpmn._20100524.model.TCallActivity;
import org.omg.spec.bpmn._20100524.model.TSubProcess;
import org.omg.spec.bpmn._20100524.model.TTask;

/**
 * CPF 1.0 task with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
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

    // Constructors

    /** No-arg constructor. */
    public CpfTaskType() { }

    /**
     * Construct a CPF Task corresponding to a BPMN Call Activity.
     *
     * @param callActivity  a BPMN Call Activity
     * @param initializer  global construction state
     * @throws CanoniserException if the task can't be constructed
     */
    public CpfTaskType(final TCallActivity callActivity, final Initializer initializer) throws CanoniserException {
        initializer.populateActivity(this, callActivity);
        if (false) {
            // The called element is a process or global task within this same BPMN document
            setSubnetId(callActivity.getId());  // TODO - process through CpfIdFactory
        } else {
            // The called element is NOT a process or global task within this same BPMN document
            setCalledElement(new QName("dummy"));
        }
    }

    /**
     * Construct a CPF Task corresponding to a BPMN SubProcess.
     *
     * @param subProcess  a BPMN SubProcess
     * @param initializer  global construction state
     * @param net  parent net
     * @throws CanoniserException if the task's subnet can't be constructed
     */
    public CpfTaskType(final TSubProcess subProcess,
                       final Initializer initializer,
                       final NetType     net) throws CanoniserException {

        // Add the CPF child net
        NetType subnet = new CpfNetType(new ProcessWrapper(subProcess, initializer.newId("subprocess")),
                                        net,
                                        initializer);
        assert subnet != null;

        // Add the CPF Task to the parent Net
        initializer.populateActivity(this, subProcess);
        setSubnetId(subnet.getId());
    }

    /**
     * Construct a CPF Task corresponding to a BPMN Task.
     *
     * @param task  a BPMN Task
     * @param initializer  global construction state
     * @throws CanoniserException if the task can't be constructed
     */
    public CpfTaskType(final TTask task, final Initializer initializer) throws CanoniserException {
        initializer.populateActivity(this, task);
    }

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

    /** @return the identifier of the called element of a BPMN Call Activity */
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

    /** @param id  The identifier of the called element, or <code>null</code> to clear the property */
    public void setCalledElement(final QName id) {

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

    /** @param value  whether this CPF task corresponds to a BPMN event-triggered subprocess */
    public void setTriggeredByEvent(final Boolean value) {

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
