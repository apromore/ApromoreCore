/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.canoniser.provider.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;

import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.provider.CanoniserProvider;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.springframework.stereotype.Service;

/**
 * Providing the default CanoniserProvider implementation
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 */
@Service
public class CanoniserProviderImpl implements CanoniserProvider {

    @Resource
    private Set<Canoniser> canoniserSet;

    public Set<Canoniser> getCanoniserSet() {
        return canoniserSet;
    }

    public void setCanoniserSet(final Set<Canoniser> canoniserSet) {
        this.canoniserSet = canoniserSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#listAll()
     */
    @Override
    public final Set<Canoniser> listAll() {
        return Collections.unmodifiableSet(getCanoniserSet());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#listByNativeType(java.lang.String)
     */
    @Override
    public final Set<Canoniser> listByNativeType(final String nativeType) {
        return Collections.unmodifiableSet(findAllCanoniser(nativeType, null, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#listByNativeTypeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Set<Canoniser> listByNativeTypeAndName(final String nativeType, final String name) {
        return Collections.unmodifiableSet(findAllCanoniser(nativeType, name, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeType(java.lang.String)
     */
    @Override
    public final Canoniser findByNativeType(final String nativeType) throws PluginNotFoundException {
        return findByNativeTypeAndNameAndVersion(nativeType, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeTypeAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Canoniser findByNativeTypeAndName(final String nativeType, final String name) throws PluginNotFoundException {
        return findByNativeTypeAndNameAndVersion(nativeType, name, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.CanoniserProvider#findByNativeTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public final Canoniser findByNativeTypeAndNameAndVersion(final String nativeType, final String name, final String version)
            throws PluginNotFoundException {
        final Set<Canoniser> resultList = findAllCanoniser(nativeType, name, version);
        Iterator<Canoniser> iter = resultList.iterator();
        if (iter.hasNext()) {
            // Just return the first one
            return iter.next();
        }
        throw new PluginNotFoundException("Could not find canoniser with name: " + ((name != null) ? name : "null") + " version: "
                + ((version != null) ? version : "null") + " nativeType: " + ((nativeType != null) ? nativeType : "null"));
    }

    /**
     * Returns a List of Canonisers with matching parameters.
     *
     * @param nativeType can be NULL
     * @param name       can be NULL
     * @param version    can be NULL
     * @return List of Canonisers or empty List
     */
    private Set<Canoniser> findAllCanoniser(final String nativeType, final String name, final String version) {
        final Set<Canoniser> cList = new HashSet<>();

        for (final Canoniser c : getCanoniserSet()) {
            if (PluginProviderHelper.compareNullable(nativeType, c.getNativeType()) && PluginProviderHelper.compareNullable(name, c.getName()) && PluginProviderHelper.compareNullable(version, c.getVersion())) {
                cList.add(c);
            }
        }
        return cList;
    }

}
