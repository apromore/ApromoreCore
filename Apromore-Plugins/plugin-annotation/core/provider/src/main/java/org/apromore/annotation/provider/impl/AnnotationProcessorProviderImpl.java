/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.annotation.provider.impl;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apromore.annotation.AnnotationProcessor;
import org.apromore.annotation.provider.AnnotationProcessorProvider;
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.provider.PluginProviderHelper;
import org.springframework.stereotype.Component;

/**
 * Providing the default Annotation Processor Provider implementation
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Component
public class AnnotationProcessorProviderImpl implements AnnotationProcessorProvider {

    @Resource
    private Set<AnnotationProcessor> annotationProcessorSet;

    public Set<AnnotationProcessor> getAnnotationProcessorSet() {
        return annotationProcessorSet;
    }

    public void setAnnotationProcessorSet(final Set<AnnotationProcessor> annotationProcessorSet) {
        this.annotationProcessorSet = annotationProcessorSet;
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.provider.AnnotationProcessorProvider#listAll()
     */
    @Override
    public final Set<AnnotationProcessor> listAll() {
        return Collections.unmodifiableSet(getAnnotationProcessorSet());
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.provider.AnnotationProcessorProvider#
     */
    @Override
    public final Set<AnnotationProcessor> listBySourceAndTargetProcessType(String processType) throws PluginNotFoundException {
        return Collections.unmodifiableSet(findAllAnnotationProcessors(processType, null, null));
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.provider.AnnotationProcessorProvider#
     */
    @Override
    public final Set<AnnotationProcessor> listBySourceAndTargetProcessTypeAndName(String processType, String name) throws PluginNotFoundException {
        return Collections.unmodifiableSet(findAllAnnotationProcessors(processType, name, null));
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.provider.AnnotationProcessorProvider#
     */
    @Override
    public final AnnotationProcessor findBySourceAndTargetProcessType(String processType) throws PluginNotFoundException {
        return findBySourceAndTargetProcessTypeAndNameAndVersion(processType, null, null);
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.annotation.provider.AnnotationProcessorProvider#
     */
    @Override
    public final AnnotationProcessor findBySourceAndTargetProcessTypeAndNameAndVersion(String processType, String name, String version)
            throws PluginNotFoundException {
        final Set<AnnotationProcessor> resultList = findAllAnnotationProcessors(processType, name, version);
        Iterator<AnnotationProcessor> iter = resultList.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        throw new PluginNotFoundException("Could not find annotation post processor with name: " + ((name != null) ? name : "null") +
                " version: " + ((version != null) ? version : "null") +
                " processType: " + ((processType != null) ? processType : "null"));
    }

    /**
     * Returns a List of Canonisers with matching parameters.
     * @param processType can be NULL
     * @param name       can be NULL
     * @param version    can be NULL
     * @return List of Canonisers or empty List
     */
    private Set<AnnotationProcessor> findAllAnnotationProcessors(String processType, final String name, final String version)
            throws PluginNotFoundException {
        final Set<AnnotationProcessor> cList = new HashSet<>();

        for (final AnnotationProcessor c : getAnnotationProcessorSet()) {
            if (PluginProviderHelper.compareNullable(processType, c.getProcessFormatProcessor()) &&
                    PluginProviderHelper.compareNullable(name, c.getName()) &&
                    PluginProviderHelper.compareNullable(version, c.getVersion())) {
                cList.add(c);
            }
        }
        if (cList.isEmpty()) {
            throw new PluginNotFoundException("Could not find plugin with name: " + ((name != null) ? name : "null") + " version: "
                    + ((version != null) ? version : "null") + " processType: " + ((processType != null) ? processType : "null"));
        }
        return cList;
    }

}
