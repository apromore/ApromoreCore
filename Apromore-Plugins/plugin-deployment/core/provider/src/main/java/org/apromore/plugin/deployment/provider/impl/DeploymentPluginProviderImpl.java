/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */
package org.apromore.plugin.deployment.provider.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.Resource;

import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.provider.DeploymentPluginProvider;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.springframework.stereotype.Component;

/**
 * Providing the default Provider implementation
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
@Component
public class DeploymentPluginProviderImpl implements DeploymentPluginProvider {

    @Resource
    private Set<DeploymentPlugin> deploymentPluginSet;

    public Set<DeploymentPlugin> getDeploymentPluginSet() {
        return deploymentPluginSet;
    }

    public void setDeploymentPluginSet(final Set<DeploymentPlugin> deploymentPluginSet) {
        this.deploymentPluginSet = deploymentPluginSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#listAll()
     */
    @Override
    public Set<DeploymentPlugin> listAll() {
        return Collections.unmodifiableSet(findAllDeploymentPlugins(null, null, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#listByNativeType(java.lang.String)
     */
    @Override
    public Set<DeploymentPlugin> listByNativeType(final String nativeType) {
        return Collections.unmodifiableSet(findAllDeploymentPlugins(nativeType, null, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#findByNameAndVersion(java.lang.String, java.lang.String)
     */
    @Override
    public DeploymentPlugin findByNativeTypeAndNameAndVersion(final String nativeType, final String name, final String version)
            throws PluginNotFoundException {
        Set<DeploymentPlugin> set = findAllDeploymentPlugins(nativeType, name, version);
        Iterator<DeploymentPlugin> iterator = set.iterator();
        if (!iterator.hasNext()) {
            throw new PluginNotFoundException();
        } else {
            return iterator.next();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#findByNativeType(java.lang.String)
     */
    @Override
    public DeploymentPlugin findByNativeType(final String nativeType) throws PluginNotFoundException {
        Set<DeploymentPlugin> list = findAllDeploymentPlugins(nativeType, null, null);
        Iterator<DeploymentPlugin> iterator = list.iterator();
        if (!iterator.hasNext()) {
            throw new PluginNotFoundException();
        } else {
            return iterator.next();
        }
    }

    /**
     * Returns a List of DeploymentPlugin with matching parameters.
     *
     * @param nativeType can be NULL
     * @param name       can be NULL
     * @param version    can be NULL
     * @return Set of DeploymentPlugin or empty Set
     */
    private Set<DeploymentPlugin> findAllDeploymentPlugins(final String nativeType, final String name, final String version) {

        final Set<DeploymentPlugin> deploymentSet = new HashSet<DeploymentPlugin>();

        for (final DeploymentPlugin d : getDeploymentPluginSet()) {
            if (PluginProviderHelper.compareNullable(nativeType, d.getNativeType()) && PluginProviderHelper.compareNullable(name, d.getName())
                    && PluginProviderHelper.compareNullable(version, d.getVersion())) {
                deploymentSet.add(d);
            }
        }
        return deploymentSet;
    }

}
