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

package org.apromore.portal.servlet;

import javax.servlet.http.HttpServlet;
import org.apromore.plugin.portal.WebContentService;

/**
 * The portal's default servlet.
 *
 * Serves static resources by checking the available {@link WebContentService}s and POST requests by
 * checking {@link HttpServlet} OSGi services.
 *
 * Only GET requests are supported for static content. For ZUML templates, use the
 * <code>zkLoader</code> servlet instead.
 *
 * For POST requests, the service must have a <code>org.apromore.portal.servlet.pattern</code>
 * service property, which must match the servlet path exactly (regex patterns unsupported). This is
 * similar to OSGi HTTP Whiteboard's <code>osgi.http.whiteboard.servlet.pattern</code>.
 */
public class ResourceServlet extends HttpServlet {

  // private Map<String, String> contentTypeMap = new HashMap<>();
  // private static final String PORTAL_SERVLET_BUNDLE_KEY = "org.apromore.portal.servlet.pattern";
  // private static final Logger LOGGER = LoggerFactory.getLogger(ResourceServlet.class);
  //
  // @Override
  // public void init() throws ServletException {
  // super.init();
  //
  // contentTypeMap.put("png", "image/png");
  // contentTypeMap.put("svg", "image/svg+xml");
  // contentTypeMap.put("css", "text/css"); // for customised icon-fonts
  // contentTypeMap.put("eot", "application/vnd.ms-fontobject"); // for customised icon-fonts
  // contentTypeMap.put("svg", "image/svg+xml"); // for customised icon-fonts
  // contentTypeMap.put("ttf", "application/font-sfnt"); // for customised icon-fonts
  // contentTypeMap.put("woff", "application/font-woff"); // for customised icon-fonts
  // contentTypeMap.put("js", "text/javascript");
  // }
  //
  // @Override
  // public void destroy() {
  // contentTypeMap.clear();
  // super.destroy();
  // }
  //
  // @Override
  // public void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws
  // ServletException, IOException {
  // try {
  // boolean serviceDone = serviceByServletBundle(req, resp);
  // if (!serviceDone) serviceDone = serviceByWebContentBundle(req, resp);
  // if (!serviceDone) getServletContext().getNamedDispatcher("default").forward(req, resp);
  // }
  // catch (InvalidSyntaxException e) {
  // throw new ServletException(e);
  // }
  // }
  //
  // @Override
  // public void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws
  // ServletException, IOException {
  // try {
  // boolean serviceDone = serviceByServletBundle(req, resp);
  // if (!serviceDone) resp.sendError(HttpServletResponse.SC_NOT_FOUND);
  // }
  // catch (InvalidSyntaxException e) {
  // throw new ServletException(e);
  // }
  // }
  //
  // /**
  // * Service the request by a servlet bundle
  // * @param req
  // * @param resp
  // * @return: true if the request is serviced, false otherwise
  // * @throws IOException
  // * @throws InvalidSyntaxException
  // */
  // private boolean serviceByServletBundle(final HttpServletRequest req, final HttpServletResponse
  // resp) throws InvalidSyntaxException, IOException {
  // BundleContext bundleContext = (BundleContext)
  // getServletContext().getAttribute("osgi-bundlecontext");
  // for (ServiceReference serviceReference: (Collection<ServiceReference>)
  // bundleContext.getServiceReferences(HttpServlet.class, null)) {
  // HttpServlet servlet = (HttpServlet) bundleContext.getService(serviceReference);
  //
  // String servletKey = (String)serviceReference.getProperty(PORTAL_SERVLET_BUNDLE_KEY);
  // if (servletKey == null) {
  // LOGGER.error("Wrong or missing property key '" + PORTAL_SERVLET_BUNDLE_KEY +
  // "' in servlet bundle: " + servlet.getClass());
  // }
  //
  // if (pathMatchesPattern(req.getServletPath(), servletKey)) {
  // try {
  // servlet.init(getServletConfig()); // TODO: create a new servlet config based on service
  // parameters
  // servlet.service(req, resp);
  // }
  // catch (Exception ex) {
  // LOGGER.error("Errors occurred in " + servlet.getClass() +
  // " servlet bundle when servicing the request for " + req.getServletPath(), ex);
  // resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
  // " Error in " + servlet.getClass() +
  // " while servicing request. Please contact your administrator.");
  // }
  // return true;
  // }
  // }
  //
  // return false; // no servlet bundle found
  // }
  //
  // /**
  // * Service the request by a web content bundle
  // * @param req
  // * @param resp
  // * @return: true if the request is serviced, false otherwise
  // */
  // private boolean serviceByWebContentBundle(final HttpServletRequest req, final
  // HttpServletResponse resp) throws InvalidSyntaxException {
  // BundleContext bundleContext = (BundleContext)
  // getServletContext().getAttribute("osgi-bundlecontext");
  // List<WebContentService> webContentServices = new ArrayList<>();
  // for (ServiceReference serviceReference: (Collection<ServiceReference>)
  // bundleContext.getServiceReferences(WebContentService.class, null)) {
  // webContentServices.add((WebContentService) bundleContext.getService(serviceReference));
  // }
  //
  // for (WebContentService webContentService: webContentServices) {
  // String path = req.getServletPath();
  // try {
  // if (webContentService.hasResource(path)) {
  // try (InputStream in = webContentService.getResourceAsStream(path)) {
  // if (in == null) {
  // resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, webContentService + " did not
  // produce content for " + path);
  // } else {
  // resp.setContentType(contentType(path));
  // resp.setStatus(HttpServletResponse.SC_OK);
  // try (OutputStream out = resp.getOutputStream()) {
  // ByteStreams.copy(in, out);
  // }
  // }
  // }
  // catch (Exception ex) {
  // LOGGER.error("Errors occurred in web content service " + webContentService.getClass() +
  // " when servicing the request for " + path, ex);
  // resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
  // " Error in " + webContentService.getClass() +
  // " while servicing request. Please contact your administrator.");
  // }
  // return true;
  // }
  // }
  // // Log exception and keep going without being stopped by misbehaved bundles
  // catch (Exception ex) {
  // LOGGER.error("Errors occurred in web content service " + webContentService.getClass() +
  // " when servicing the request for " + path, ex);
  // }
  // }
  //
  // return false; // no web content bundle found
  // }
  //
  // private String contentType(String path) {
  // String extension = path.substring(path.lastIndexOf(".") + 1);
  // return contentTypeMap.containsKey(extension) ? contentTypeMap.get(extension) : "";
  // }
  //
  // /**
  // * @param path a URL path
  // * @param pattern a servlet pattern
  // * @return whether the <var>path</var> matches the <var>pattern</var>
  // */
  // private boolean pathMatchesPattern(final String path, final String pattern) {
  // if (path == null || pattern == null) return false;
  //
  // // e.g. "/img/*"
  // if (pattern.endsWith("*")) {
  // return path.startsWith(pattern.substring(0, pattern.length() - 1));
  // }
  //
  // // e.g. "*.jpg"
  // if (pattern.startsWith("*")) {
  // return path.endsWith(pattern.substring(1));
  // }
  //
  // return path.equals(pattern);
  // }

}
