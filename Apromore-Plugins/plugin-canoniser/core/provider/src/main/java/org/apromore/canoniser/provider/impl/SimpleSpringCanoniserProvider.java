package org.apromore.canoniser.provider.impl;

import java.util.Set;

import org.apromore.canoniser.Canoniser;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.springframework.stereotype.Service;

/**
 * CanoniserProvider using Spring/Reflection to find installed Canonisers
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
@Service
public class SimpleSpringCanoniserProvider extends CanoniserProviderImpl {

    /**
     * Searchs for all installed Canonisers using Spring and Reflection
     */
    public SimpleSpringCanoniserProvider() {
        super();
        Set<Canoniser> canoniserList = PluginProviderHelper.findPluginsByClass(Canoniser.class, "org.apromore.canoniser");
        setInternalCanoniserSet(canoniserList);
    }

}
