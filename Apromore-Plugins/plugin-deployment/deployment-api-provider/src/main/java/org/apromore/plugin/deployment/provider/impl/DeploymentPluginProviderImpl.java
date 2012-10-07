/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.plugin.deployment.provider.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apromore.plugin.deployment.DeploymentPlugin;
import org.apromore.plugin.deployment.provider.DeploymentPluginProvider;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProviderHelper;

/**
 * Providing the default Provider implementation
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 *
 */
public abstract class DeploymentPluginProviderImpl implements DeploymentPluginProvider {

    private Set<DeploymentPlugin> internalDeploymentPluginSet;

    protected Set<DeploymentPlugin> getInternalDeploymentPluginSet() {
        return internalDeploymentPluginSet;
    }

    protected void setInternalDeploymentPluginSet(final Set<DeploymentPlugin> internalDeploymentPluginSet) {
        this.internalDeploymentPluginSet = internalDeploymentPluginSet;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#listAll()
     */
    @Override
    public Set<DeploymentPlugin> listAll() {
        return findAllDeploymentPlugins(null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#findByName(java.lang.String)
     */
    @Override
    public DeploymentPlugin findByName(final String name) throws PluginNotFoundException {
        Set<DeploymentPlugin> set = findAllDeploymentPlugins(null, name);
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
        Set<DeploymentPlugin> list = findAllDeploymentPlugins(nativeType, null);
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
     * @param nativeType
     *            can be NULL
     * @param name
     *            can be NULL
     * @return Set of DeploymentPlugin or empty Set
     */
    private Set<DeploymentPlugin> findAllDeploymentPlugins(final String nativeType, final String name) {

        final Set<DeploymentPlugin> deploymentSet = new HashSet<DeploymentPlugin>();

        for (final DeploymentPlugin d : getInternalDeploymentPluginSet()) {
            if (PluginProviderHelper.compareNullable(nativeType, d.getNativeType()) && PluginProviderHelper.compareNullable(name, d.getName())) {
                deploymentSet.add(d);
            }
        }
        return deploymentSet;
    }

}
