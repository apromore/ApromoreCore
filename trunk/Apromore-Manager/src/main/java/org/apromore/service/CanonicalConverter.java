package org.apromore.service;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.Canonical;

/**
 * An Adapter class used to convert between two different formats of the same data.
 * The code that uses this can not be changed to use just one.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface CanonicalConverter {

    /**
     * Converts the Data between the two types, from jaxb to graph.
     * @param cpt the JAXB Object.
     * @return the canonical Object.
     */
    Canonical convert(CanonicalProcessType cpt);

    /**
     * Converts the data between the type types, from graph to jaxb.
     * @param canonical the RPST Graph.
     * @return
     */
    CanonicalProcessType convert(Canonical canonical);

}
