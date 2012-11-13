package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.List;

// Local classes
import org.apromore.cpf.CancellationRefType;

/**
 * CPF 1.0 work with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface CpfWorkType extends CpfNodeType {

    // Methods specified by WorkType

    /** @see {@link WorkType#getCancelEdgeId} */
    List<CancellationRefType> getCancelEdgeId();

    /** @see {@link WorkType#getCancelEdgeId} */
    List<CancellationRefType> getCancelNodeId();
}
