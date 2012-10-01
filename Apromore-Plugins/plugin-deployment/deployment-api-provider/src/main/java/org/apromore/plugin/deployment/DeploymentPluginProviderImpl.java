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
package org.apromore.plugin.deployment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private List<DeploymentPlugin> internalDeploymentPluginList;

    protected List<DeploymentPlugin> getInternalDeploymentPluginList() {
        return internalDeploymentPluginList;
    }

    protected void setInternalDeploymentPluginList(final List<DeploymentPlugin> internalDeploymentPluginList) {
        this.internalDeploymentPluginList = internalDeploymentPluginList;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#listAll()
     */
    @Override
    public Collection<DeploymentPlugin> listAll() {
        return findAllDeploymentPlugins(null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#findByName(java.lang.String)
     */
    @Override
    public DeploymentPlugin findByName(final String name) throws PluginNotFoundException {
        List<DeploymentPlugin> list = findAllDeploymentPlugins(null, name);
        if (list.isEmpty()) {
            throw new PluginNotFoundException();
        } else {
            return list.get(0);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.plugin.deployment.provider.DeploymentPluginProvider#findByNativeType(java.lang.String)
     */
    @Override
    public DeploymentPlugin findByNativeType(final String nativeType) throws PluginNotFoundException {
        List<DeploymentPlugin> list = findAllDeploymentPlugins(nativeType, null);
        if (list.isEmpty()) {
            throw new PluginNotFoundException();
        } else {
            return list.get(0);
        }
    }

    /**
     * Returns a List of DeploymentPlugin with matching parameters.
     *
     * @param nativeType
     *            can be NULL
     * @param name
     *            can be NULL
     * @return List of DeploymentPlugin or empty List
     */
    private List<DeploymentPlugin> findAllDeploymentPlugins(final String nativeType, final String name) {

        final List<DeploymentPlugin> deploymentList = new ArrayList<DeploymentPlugin>();

        for (final DeploymentPlugin d : getInternalDeploymentPluginList()) {
            if (PluginProviderHelper.compareNullable(nativeType, d.getNativeType()) && PluginProviderHelper.compareNullable(name, d.getName())) {
                deploymentList.add(d);
            }
        }
        return deploymentList;
    }

}
