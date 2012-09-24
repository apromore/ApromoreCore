package org.apromore.canoniser.bpmn;

// Java 2 Standard classes
import java.util.ArrayList;
import java.util.List;

// Local classes
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;

/**
 * Compound result type returned by the BPMN to CPF/ANF {@link CanoniserDefinitions#canonise} method.
 *
 * This is a collection of CPF/ANF pairs, each pair corresponding to a different process view occurring within the BPMN document.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */

public class CanoniserResult {

    /**
     * Processes in the order they appeared within the original BPMN document.
     */
    private List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>();

    /**
     * Each annotation applies to the process in the same position within the {@link #cpfList}.
     */
    private List<AnnotationsType> anfList = new ArrayList<AnnotationsType>();

    /**
     * Add an associated pair of CPF and ANF structures to the result.
     *
     * @param cpf  CPF structure to add
     * @param anf  ANF structure to add
     */
    public void put(final CanonicalProcessType cpf, final AnnotationsType anf) {
        cpfList.add(cpf);
        anfList.add(anf);
    }

    /**
     * @param i  index position
     * @return the annotations for the i<sup>th</sup> process
     */
    public CanonicalProcessType getCpf(final int i) { return cpfList.get(i); }

    /**
     * @param i  index position
     * @return the canonical form for the i<sup>th</sup> process
     */
    public AnnotationsType getAnf(final int i) { return anfList.get(i); }

    /**
     * @return the number of CPF/ANF pairs in this result
     */
    public int size() {
        assert anfList.size() == cpfList.size();
        return cpfList.size();
    }
}
