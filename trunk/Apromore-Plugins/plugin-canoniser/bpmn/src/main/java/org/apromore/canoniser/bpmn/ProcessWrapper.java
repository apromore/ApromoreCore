package org.apromore.canoniser.bpmn;

// Java 2 Standard packages
import java.util.List;
import javax.xml.bind.JAXBElement;

// Local packages
import org.omg.spec.bpmn._20100524.model.TArtifact;
import org.omg.spec.bpmn._20100524.model.TFlowElement;
import org.omg.spec.bpmn._20100524.model.TLaneSet;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TSubProcess;

/**
 * Wrapper to provide a common interface to both {@link TProcess} and {@link TSubProcess}.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class ProcessWrapper {

    private final String id;
    private final List<JAXBElement<? extends TArtifact>> artifact;
    private final List<JAXBElement<? extends TFlowElement>> flowElement;
    private final List<TLaneSet> laneSet;

    // Constructors

    /**
     * Wrap a {@link TProcess}.
     *
     * @param process  wrapped instance
     */
    public ProcessWrapper(final TProcess process) {
        id          = process.getId();
        artifact    = process.getArtifact();
        flowElement = process.getFlowElement();
        laneSet     = process.getLaneSet();
    }

    /**
     * Wrap a {@link TSubProcess}.
     *
     * @param subprocess  wrapped instance
     * @param processId  identifier to be used for the implicit process within the subprocess
     */
    public ProcessWrapper(final TSubProcess subprocess, final String processId) {
        id          = processId;
        artifact    = subprocess.getArtifact();
        flowElement = subprocess.getFlowElement();
        laneSet     = subprocess.getLaneSet();
    }

    // Accessor methods

    /** @return <code>id</code> property */
    public String getId() { return id; }

    /** @return <code>artifact</code> property */
    public List<JAXBElement<? extends TArtifact>> getArtifact() { return artifact; }

    /** @return <code>flowElement</code> property */
    public List<JAXBElement<? extends TFlowElement>> getFlowElement() { return flowElement; }

    /** @return <code>laneSet</code> property */
    public List<TLaneSet> getLaneSet() { return laneSet; }
}
