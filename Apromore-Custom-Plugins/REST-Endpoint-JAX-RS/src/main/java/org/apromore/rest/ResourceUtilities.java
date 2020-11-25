/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.rest;

import javax.servlet.ServletContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

abstract class ResourceUtilities {

    /**
     * Access a unique OSGi service.
     *
     * This obtains the bundle context from the servlet context of the web application.
     *
     * @param clazz  the type of the service; there must be exactly one registered service of this type
     * @param context  the servlet context
     * @return the service instance
     */
    static <T> T getOSGiService(final Class<T> clazz, final ServletContext context) {
        BundleContext bundleContext = (BundleContext) context.getAttribute("osgi-bundlecontext");
        ServiceReference serviceReference = bundleContext.getServiceReference(clazz);
        T service = (T) bundleContext.getService(serviceReference);

        return service;
    }
}
