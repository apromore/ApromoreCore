package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.List;

/**
 * CPF 1.0 resource type with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public interface CpfResourceTypeType extends Attributed {

    // Methods already present on CPF ResourceTypeType

    public String getId();

    // Additional convenience methods

    /**
     * @return every other resource type which has this one as a specialization
     */
    public List<CpfResourceTypeType> getGeneralizationRefs();
}
