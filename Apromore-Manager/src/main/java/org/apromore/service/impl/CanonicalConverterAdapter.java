package org.apromore.service.impl;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.converter.CanonicalToGraph;
import org.apromore.graph.canonical.converter.GraphToCanonical;
import org.apromore.service.CanonicalConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Implementation of the CanonicalConverter. Allows the conversion of the different formats between each other.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
public class CanonicalConverterAdapter implements CanonicalConverter {

    @Inject
    private CanonicalToGraph canonicalToGraph;
    @Inject
    private GraphToCanonical graphToCanonical;


    /**
     * @see CanonicalConverter#convert(org.apromore.cpf.CanonicalProcessType)
     * {@inheritDoc}
     */
    @Override
    public Canonical convert(CanonicalProcessType cpt) {
        return canonicalToGraph.convert(cpt);
    }

    /**
     * @see CanonicalConverter#convert(org.apromore.graph.canonical.Canonical)
     * {@inheritDoc}
     */
    @Override
    public CanonicalProcessType convert(Canonical canonical) {
        return graphToCanonical.convert(canonical);
    }
}
