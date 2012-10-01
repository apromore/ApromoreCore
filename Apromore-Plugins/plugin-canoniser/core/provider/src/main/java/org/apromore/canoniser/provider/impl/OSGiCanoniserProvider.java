package org.apromore.canoniser.provider.impl;

import java.util.List;

import org.apromore.canoniser.Canoniser;
import org.springframework.stereotype.Service;

/**
 * CanoniserProvider using OSGi to find installed Canonisers
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
@Service("osgiCanoniserProviderImpl")
public class OSGiCanoniserProvider extends CanoniserProviderImpl {

    public List<Canoniser> getCanoniserList() {
        return getInternalCanoniserList();
    }

    public void setCanoniserList(final List<Canoniser> canoniserList) {
        setInternalCanoniserList(canoniserList);
    }

}
