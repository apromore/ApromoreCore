package org.apromore.canoniser.provider.impl;

import java.util.List;

import org.apromore.canoniser.Canoniser;
import org.springframework.stereotype.Service;

@Service("osgiCanoniserProviderImpl")
public class OSGiCanoniserProvider extends CanoniserProviderImpl {

    public List<Canoniser> getCanoniserList() {
        return getInternalCanoniserList();
    }

    public void setCanoniserList(final List<Canoniser> canoniserList) {
        setInternalCanoniserList(canoniserList);
    }

}
