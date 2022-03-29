/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.portal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apromore.plugin.portal.WebContentService;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.SimpleWebApp;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.sys.RequestInfo;
import org.zkoss.zk.ui.sys.UiFactory;
import org.zkoss.zk.ui.util.Configuration;

/**
 * Customizes ZK's default web application to also load ZUML pages from the classpath of OSGi bundles.
 */
public class ApromoreWebApp extends SimpleWebApp {

    @Override
    public void setUiFactory(UiFactory uiFactory) {
        super.setUiFactory(new WrappedUiFactory(uiFactory) {
            @Override
            public PageDefinition getPageDefinition(RequestInfo ri, String path) {

                // Use the stock (wrapped) UiFactory to fetch the ZUML document from the WAR's webapp directory
                PageDefinition pageDefinition = super.getPageDefinition(ri, path);

                // If the WAR didn't have a resource at the specified path, check if the OSGi services can provide it instead
                if (pageDefinition == null) {
                    HttpServletRequest request = (HttpServletRequest) ri.getNativeRequest();
                    for (WebContentService webContentService: getWebContentServices()) {
                        
                        if (webContentService.hasResource(path)) {
//                          log("Got page definition for " + path + " from " + webContentService);
                            try (InputStream in = webContentService.getResourceAsStream(path)) {
                                if (in != null) {
                                    pageDefinition = PageDefinitions.getPageDefinitionDirectly(
                                        ri.getWebApp(),
                                        PageDefinitions.getLocator(ri.getWebApp(), path),
                                        new InputStreamReader(in, "utf-8"),
                                        null);
                                }
                            } catch (IOException e) {
                                log("Unable to read page definition " + path, e);
                            }

                            break;
//                      } else {
//                          log("Unable to get page definition for " + path + " from " + webContentService);
                        }
                    }
//              } else {
//                  log("Got page definition for " + path + " from WAR");
                }

                if (pageDefinition == null) {
                    log("Unable to get page definition for " + path);
                }

                return pageDefinition;
            }
        });
    }

    /**
     * @return all the {@link WebContentServices} registered in the OSGi context
     */
    private List<WebContentService> getWebContentServices() {
//        BundleContext bundleContext = (BundleContext) getServletContext().getAttribute("osgi-bundlecontext");
//        /* This single-expression stream-based code is incompatible with Virgo's classpath scanner
//        List<WebContentService> webContentServices = (List<WebContentService>) bundleContext.getServiceReferences(WebContentService.class, null)
//            .stream()
//            .map(serviceReference -> ((WebContentService) bundleContext.getService((ServiceReference) serviceReference)))
//            .collect(Collectors.toList());
//        */
//        List<WebContentService> webContentServices = new java.util.ArrayList<>();
//        try {
//            java.util.Collection<ServiceReference> references = (java.util.Collection<ServiceReference>) bundleContext.getServiceReferences(WebContentService.class, null);
//            if (references != null) {
//                for (ServiceReference serviceReference: references) {
//                    webContentServices.add((WebContentService) bundleContext.getService((ServiceReference) serviceReference));
//                }
//            }
//            return webContentServices;
//
//        } catch (InvalidSyntaxException e) {
//            throw new Error("Bad hardcoded filter in BundleContext.getServiceReferences call", e);
//        }
    	return null;
    }
}
