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
package org.apromore.toolbox.provider.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Resource;

import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.apromore.toolbox.Toolbox;
import org.apromore.toolbox.provider.ToolboxProvider;
import org.springframework.stereotype.Service;

/**
 * Providing the default ToolboxProvider implementation
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
public class ToolboxProviderImpl implements ToolboxProvider {

    @Resource
    private Set<Toolbox> toolboxSet;

    public Set<Toolbox> getToolboxSet() {
        return toolboxSet;
    }

    public void setToolboxSet(final Set<Toolbox> toolboxSet) {
        this.toolboxSet = toolboxSet;
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.toolbox.provider.ToolboxProvider#listAll()
     */
    @Override
    public final Set<Toolbox> listAll() {
        return Collections.unmodifiableSet(getToolboxSet());
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.toolbox.provider.ToolboxProvider#listByToolName(java.lang.String)
     */
    @Override
    public final Set<Toolbox> listByToolName(final String nativeType) {
        return Collections.unmodifiableSet(findAllToolbox(nativeType, null, null));
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.toolbox.provider.ToolboxProvider#listByToolNameAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Set<Toolbox> listByToolNameAndName(final String nativeType, final String name) {
        return Collections.unmodifiableSet(findAllToolbox(nativeType, name, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.toolbox.provider.ToolboxProvider#findByToolName(java.lang.String)
     */
    @Override
    public final Toolbox findByToolName(final String nativeType) throws PluginNotFoundException {
        return findByToolNameAndNameAndVersion(nativeType, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.toolbox.provider.ToolboxProvider#findByToolNameAndName(java.lang.String, java.lang.String)
     */
    @Override
    public final Toolbox findByToolNameAndName(final String nativeType, final String name) throws PluginNotFoundException {
        return findByToolNameAndNameAndVersion(nativeType, name, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.provider.ToolboxProvider#findByNativeTypeAndNameAndVersion(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public final Toolbox findByToolNameAndNameAndVersion(final String nativeType, final String name, final String version)
            throws PluginNotFoundException {
        final Set<Toolbox> resultList = findAllToolbox(nativeType, name, version);
        Iterator<Toolbox> iter = resultList.iterator();
        if (iter.hasNext()) {
            // Just return the first one
            return iter.next();
        }
        throw new PluginNotFoundException("Could not find tool with name: " + ((name != null) ? name : "null") + " version: "
                + ((version != null) ? version : "null") + " nativeType: " + ((nativeType != null) ? nativeType : "null"));
    }

    /**
     * Returns a List of Canonisers with matching parameters.
     *
     * @param nativeType can be NULL
     * @param name can be NULL
     * @param version can be NULL
     * @return List of Canonisers or empty List
     */
    private Set<Toolbox> findAllToolbox(final String nativeType, final String name, final String version) {
        final Set<Toolbox> cList = new HashSet<Toolbox>();

        for (final Toolbox c : getToolboxSet()) {
            if (PluginProviderHelper.compareNullable(nativeType, c.getToolName()) && PluginProviderHelper.compareNullable(name, c.getName()) && PluginProviderHelper.compareNullable(version, c.getVersion())) {
                cList.add(c);
            }
        }
        return cList;
    }

}
