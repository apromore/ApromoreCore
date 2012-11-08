package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.List;

// Local packages
import org.apromore.cpf.NonhumanType;

/**
 * CPF 1.0 nonhuman resource type with convenience methods.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class CpfNonhumanType extends NonhumanType implements CpfResourceTypeType {

    /** Secondary superclass. */
    private final CpfResourceTypeType super2;

    /** No-arg constructor. */
    public CpfNonhumanType() {
        super2 = new CpfResourceTypeTypeImpl();
    }

    /** {@inheritDoc} */
    public List<CpfResourceTypeType> getGeneralizationRefs() {
        return super2.getGeneralizationRefs();
    }
}
