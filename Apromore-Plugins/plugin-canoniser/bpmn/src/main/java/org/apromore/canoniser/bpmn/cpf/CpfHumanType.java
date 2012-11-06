package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.Collections;
import java.util.List;

// Local packages
import org.apromore.cpf.HumanType;

/**
 * CPF 1.0 human resource type with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfHumanType extends HumanType implements CpfResourceTypeType {

    /** No-arg constructor. */
    public CpfHumanType() { }

    /**
     * @return every other resource type which has this one as a specialization
     */
    public List<CpfResourceTypeType> getGeneralizationRefs() {
        return Collections.emptyList();
    }
}
