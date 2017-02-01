/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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

package org.apromore.filestore.webdav.methods;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apromore.filestore.webdav.IMimeTyper;
import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.AccessDeniedException;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.fromcatalina.XMLHelper;
import org.apromore.filestore.webdav.fromcatalina.XMLWriter;
import org.apromore.filestore.webdav.locking.LockedObject;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class DoPropfind extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoPropfind.class.getName());

    /**
     * PROPFIND - Specify a property mask.
     */
    private static final int FIND_BY_PROPERTY = 0;

    /**
     * PROPFIND - Display all properties.
     */
    private static final int FIND_ALL_PROP = 1;

    /**
     * PROPFIND - Return property names.
     */
    private static final int FIND_PROPERTY_NAMES = 2;

    private IWebDavStore _store;
    private ResourceLocks _resourceLocks;
    private IMimeTyper _mimeTyper;
    private int _depth;

    public DoPropfind(IWebDavStore store, ResourceLocks resLocks, IMimeTyper mimeTyper) {
        _store = store;
        _resourceLocks = resLocks;
        _mimeTyper = mimeTyper;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        LOGGER.trace("-- " + this.getClass().getName());

        // Retrieve the resources
        String path = getCleanPath(getRelativePath(req));
        String tempLockOwner = "doPropfind" + System.currentTimeMillis() + req.toString();
        _depth = getDepth(req);

        if (_resourceLocks.lock(transaction, path, tempLockOwner, false, _depth, TEMP_TIMEOUT, TEMPORARY)) {
            StoredObject so;
            try {
                so = _store.getStoredObject(transaction, path);
                if (so == null) {
                    resp.setContentType("text/xml; charset=UTF-8");
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI());
                    return;
                }

                List<String> properties = null;
                path = getCleanPath(getRelativePath(req));
                int propertyFindType = FIND_ALL_PROP;
                Node propNode = null;

                if (req.getContentLength() > 0) {
                    DocumentBuilder documentBuilder = getDocumentBuilder();
                    try {
                        Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
                        Element rootElement = document.getDocumentElement();
                        propNode = XMLHelper.findSubElement(rootElement, "prop");
                        if (propNode != null) {
                            propertyFindType = FIND_BY_PROPERTY;
                        } else if (XMLHelper.findSubElement(rootElement, "propname") != null) {
                            propertyFindType = FIND_PROPERTY_NAMES;
                        } else if (XMLHelper.findSubElement(rootElement, "allprop") != null) {
                            propertyFindType = FIND_ALL_PROP;
                        }
                    } catch (Exception e) {
                        resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                } else {
                    propertyFindType = FIND_ALL_PROP;
                }

                HashMap<String, String> namespaces = new HashMap<>();
                namespaces.put("DAV:", "D");

                if (propertyFindType == FIND_BY_PROPERTY) {
                    propertyFindType = 0;
                    properties = XMLHelper.getPropertiesFromXML(propNode);
                }

                resp.setStatus(WebDavStatus.SC_MULTI_STATUS);
                resp.setContentType("text/xml; charset=UTF-8");

                XMLWriter generatedXML = new XMLWriter(resp.getWriter(), namespaces);
                generatedXML.writeXMLHeader();
                generatedXML.writeElement("DAV::multistatus", XMLWriter.OPENING);
                if (_depth == 0) {
                    parseProperties(transaction, req, generatedXML, path, propertyFindType, properties, _mimeTyper.getMimeType(path));
                } else {
                    recursiveParseProperties(transaction, path, req, generatedXML, propertyFindType, properties, _depth, _mimeTyper.getMimeType(path));
                }
                generatedXML .writeElement("DAV::multistatus", XMLWriter.CLOSING);
                generatedXML.sendData();
            } catch (AccessDeniedException e) {
                resp.sendError(WebDavStatus.SC_FORBIDDEN);
            } catch (WebDavException e) {
                LOGGER.warn("Sending internal error!");
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            } catch (ServletException e) {
                e.printStackTrace();
            } finally {
                _resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
            }
        } else {
            Hashtable<String, Integer> errorList = new Hashtable<>();
            errorList.put(path, WebDavStatus.SC_LOCKED);
            sendReport(req, resp, errorList);
        }
    }


    /**
     * goes recursive through all folders. used by propfind
     * 
     * @param currentPath the current path
     * @param req HttpServletRequest
     * @param generatedXML
     * @param propertyFindType
     * @param properties
     * @param depth depth of the propfind
     * @throws WebDavException if an error in the underlying store occurs
     */
    private void recursiveParseProperties(ITransaction transaction, String currentPath, HttpServletRequest req, XMLWriter generatedXML,
            int propertyFindType, List<String> properties, int depth, String mimeType) throws WebDavException {
        parseProperties(transaction, req, generatedXML, currentPath, propertyFindType, properties, mimeType);
        if (depth > 0) {
            String[] names = _store.getChildrenNames(transaction, currentPath);
            names = names == null ? new String[] {} : names;
            String newPath;

            for (String name : names) {
                newPath = currentPath;
                if (!(newPath.endsWith("/"))) {
                    newPath += "/";
                }
                newPath += name;
                recursiveParseProperties(transaction, newPath, req, generatedXML, propertyFindType, properties, depth - 1, mimeType);
            }
        }
    }

    /**
     * Propfind helper method.
     * 
     * @param req The servlet request
     * @param generatedXML  XML response to the Propfind request
     * @param path  Path of the current resource
     * @param type Propfind type
     * @param propertiesVector If the propfind type is find properties by name, then this Vector contains those properties
     */
    private void parseProperties(ITransaction transaction, HttpServletRequest req, XMLWriter generatedXML, String path,
            int type, List<String> propertiesVector, String mimeType) throws WebDavException {
        StoredObject so = _store.getStoredObject(transaction, path);

        boolean isFolder = so.isFolder();
        String creationdate = CREATION_DATE_FORMAT.format(so.getCreationDate());
        String lastModified = LAST_MODIFIED_DATE_FORMAT.format(so.getLastModified());
        String resourceLength = String.valueOf(so.getResourceLength());
        generatedXML.writeElement("DAV::response", XMLWriter.OPENING);
        String status = "HTTP/1.1 " + WebDavStatus.SC_OK + " " + WebDavStatus.getStatusText(WebDavStatus.SC_OK);

        generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
        generatedXML.writeText(rewriteUrl(getUri(req, path, isFolder)));
        generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);

        String resourceName = path;
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash != -1) {
            resourceName = resourceName.substring(lastSlash + 1);
        }

        switch (type) {
        case FIND_ALL_PROP:
            generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
            generatedXML.writeProperty("DAV::creationdate", creationdate);
            generatedXML.writeElement("DAV::displayname", XMLWriter.OPENING);
            generatedXML.writeData(resourceName);
            generatedXML.writeElement("DAV::displayname", XMLWriter.CLOSING);
            if (!isFolder) {
                generatedXML.writeProperty("DAV::getlastmodified", lastModified);
                generatedXML.writeProperty("DAV::getcontentlength", resourceLength);
                if (mimeType != null) {
                    generatedXML.writeProperty("DAV::getcontenttype",  mimeType);
                }
                generatedXML.writeProperty("DAV::getetag", getETag(so));
                generatedXML.writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
            } else {
                generatedXML.writeElement("DAV::resourcetype", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::collection", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::resourcetype",  XMLWriter.CLOSING);
            }

            writeSupportedLockElements(transaction, generatedXML, path);
            writeLockDiscoveryElements(transaction, generatedXML, path);

            generatedXML.writeProperty("DAV::source", "");
            generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText(status);
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);
            break;

        case FIND_PROPERTY_NAMES:
            generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::creationdate", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::displayname", XMLWriter.NO_CONTENT);
            if (!isFolder) {
                generatedXML.writeElement("DAV::getcontentlanguage", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getcontentlength", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getcontenttype", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getetag", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::getlastmodified", XMLWriter.NO_CONTENT);
            }
            generatedXML .writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::supportedlock",  XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::source", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText(status);
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);
            break;

        case FIND_BY_PROPERTY:
            List<String> propertiesNotFound = new ArrayList<>();

            generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

            for (String property : propertiesVector) {
                switch (property) {
                    case "DAV::creationdate":
                        generatedXML.writeProperty("DAV::creationdate", creationdate);
                        break;
                    case "DAV::displayname":
                        generatedXML.writeElement("DAV::displayname", XMLWriter.OPENING);
                        generatedXML.writeData(resourceName);
                        generatedXML.writeElement("DAV::displayname", XMLWriter.CLOSING);
                        break;
                    case "DAV::getcontentlanguage":
                        if (isFolder) {
                            propertiesNotFound.add(property);
                        } else {
                            generatedXML.writeElement("DAV::getcontentlanguage",  XMLWriter.NO_CONTENT);
                        }
                        break;
                    case "DAV::getcontentlength":
                        if (isFolder) {
                            propertiesNotFound.add(property);
                        } else {
                            generatedXML.writeProperty("DAV::getcontentlength", resourceLength);
                        }
                        break;
                    case "DAV::getcontenttype":
                        if (isFolder) {
                            propertiesNotFound.add(property);
                        } else {
                            generatedXML.writeProperty("DAV::getcontenttype", mimeType);
                        }
                        break;
                    case "DAV::getetag":
                        if (isFolder || so.isNullResource()) {
                            propertiesNotFound.add(property);
                        } else {
                            generatedXML.writeProperty("DAV::getetag", getETag(so));
                        }
                        break;
                    case "DAV::getlastmodified":
                        if (isFolder) {
                            propertiesNotFound.add(property);
                        } else {
                            generatedXML.writeProperty("DAV::getlastmodified", lastModified);
                        }
                        break;
                    case "DAV::resourcetype":
                        if (isFolder) {
                            generatedXML.writeElement("DAV::resourcetype", XMLWriter.OPENING);
                            generatedXML.writeElement("DAV::collection", XMLWriter.NO_CONTENT);
                            generatedXML.writeElement("DAV::resourcetype", XMLWriter.CLOSING);
                        } else {
                            generatedXML.writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
                        }
                        break;
                    case "DAV::source":
                        generatedXML.writeProperty("DAV::source", "");
                        break;
                    case "DAV::supportedlock":
                        writeSupportedLockElements(transaction, generatedXML, path);
                        break;
                    case "DAV::lockdiscovery":
                        writeLockDiscoveryElements(transaction, generatedXML, path);
                        break;
                    default:
                        propertiesNotFound.add(property);
                        break;
                }
            }

            generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText(status);
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

            Iterator<String> propertiesNotFoundList = propertiesNotFound.iterator();

            if (propertiesNotFoundList.hasNext()) {
                status = "HTTP/1.1 " + WebDavStatus.SC_NOT_FOUND + " " + WebDavStatus.getStatusText(WebDavStatus.SC_NOT_FOUND);
                generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

                while (propertiesNotFoundList.hasNext()) {
                    generatedXML.writeElement(propertiesNotFoundList.next(), XMLWriter.NO_CONTENT);
                }

                generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
                generatedXML.writeText(status);
                generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);
            }

            break;
        }

        generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);
        so = null;
    }

    private String getUri(HttpServletRequest req, String path, boolean folder) {
        String href = req.getContextPath();
        String servletPath = req.getServletPath();
        if (servletPath != null) {
            if ((href.endsWith("/")) && (servletPath.startsWith("/"))) {
                href += servletPath.substring(1);
            } else {
                href += servletPath;
            }
        }
        if ((href.endsWith("/")) && (path.startsWith("/"))) {
            href += path.substring(1);
        } else {
            href += path;
        }
        if ((folder) && (!href.endsWith("/"))) {
            href += "/";
        }
        return href;
    }

    private void writeSupportedLockElements(ITransaction transaction, XMLWriter generatedXML, String path) {
        LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction, path);
        generatedXML.writeElement("DAV::supportedlock", XMLWriter.OPENING);
        if (lo == null) {
            generatedXML.writeElement("DAV::lockentry", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::exclusive", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::write", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::lockentry", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::lockentry", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::shared", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::write", XMLWriter.NO_CONTENT);
            generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::lockentry", XMLWriter.CLOSING);
        } else {
            if (lo.isShared()) {
                generatedXML.writeElement("DAV::lockentry", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::shared", XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::" + lo.getType(), XMLWriter.NO_CONTENT);
                generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::lockentry", XMLWriter.CLOSING);
            }
        }

        generatedXML.writeElement("DAV::supportedlock", XMLWriter.CLOSING);
        lo = null;
    }

    private void writeLockDiscoveryElements(ITransaction transaction, XMLWriter generatedXML, String path) {
        LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction, path);

        if (lo != null && !lo.hasExpired()) {
            generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::activelock", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
            generatedXML.writeProperty("DAV::" + lo.getType());
            generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
            if (lo.isExclusive()) {
                generatedXML.writeProperty("DAV::exclusive");
            } else {
                generatedXML.writeProperty("DAV::shared");
            }
            generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::depth", XMLWriter.OPENING);
            if (_depth == INFINITY) {
                generatedXML.writeText("Infinity");
            } else {
                generatedXML.writeText(String.valueOf(_depth));
            }
            generatedXML.writeElement("DAV::depth", XMLWriter.CLOSING);

            String[] owners = lo.getOwner();
            if (owners != null) {
                for (String owner : owners) {
                    generatedXML.writeElement("DAV::owner", XMLWriter.OPENING);
                    generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
                    generatedXML.writeText(owner);
                    generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
                    generatedXML.writeElement("DAV::owner", XMLWriter.CLOSING);
                }
            } else {
                generatedXML.writeElement("DAV::owner", XMLWriter.NO_CONTENT);
            }

            int timeout = (int) (lo.getTimeoutMillis() / 1000);
            String timeoutStr = Integer.toString(timeout);
            generatedXML.writeElement("DAV::timeout", XMLWriter.OPENING);
            generatedXML.writeText("Second-" + timeoutStr);
            generatedXML.writeElement("DAV::timeout", XMLWriter.CLOSING);

            String lockToken = lo.getID();

            generatedXML.writeElement("DAV::locktoken", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
            generatedXML.writeText("opaquelocktoken:" + lockToken);
            generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::locktoken", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::activelock", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.CLOSING);

        } else {
            generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.NO_CONTENT);
        }

        lo = null;
    }

}
