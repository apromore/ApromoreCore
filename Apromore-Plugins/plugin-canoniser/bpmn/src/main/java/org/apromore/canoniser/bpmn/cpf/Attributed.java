package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.List;

// Local classes
import org.apromore.cpf.TypeAttribute;

/**
 * Any CPF element with a {@link TypeAttribute} extension list.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public interface Attributed {

    /** @return the list of extension attributes */
    List<TypeAttribute> getAttribute();
}
