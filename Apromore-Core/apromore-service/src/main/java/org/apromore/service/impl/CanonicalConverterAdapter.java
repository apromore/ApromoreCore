package org.apromore.service.impl;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CanonicalToGraph;
import org.apromore.graph.canonical.GraphToCanonical;
import org.apromore.service.CanonicalConverter;
import org.springframework.stereotype.Service;

/**
 * Implementation of the CanonicalConverter. Allows the conversion of the different formats between each other.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("CanonicalConverter")
public class CanonicalConverterAdapter implements CanonicalConverter {

    /**
     * @see CanonicalConverter#convert(org.apromore.cpf.CanonicalProcessType)
     * {@inheritDoc}
     */
    @Override
    public Canonical convert(CanonicalProcessType cpt) {
        return CanonicalToGraph.convert(cpt);
    }

    /**
     * @see CanonicalConverter#convert(org.apromore.graph.canonical.Canonical)
     * {@inheritDoc}
     */
    @Override
    public CanonicalProcessType convert(Canonical canonical) {
        return GraphToCanonical.convert(canonical);
    }
}
