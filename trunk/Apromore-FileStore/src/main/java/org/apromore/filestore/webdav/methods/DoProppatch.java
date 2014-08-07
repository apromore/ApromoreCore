/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.filestore.webdav.methods;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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

public class DoProppatch extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoProppatch.class.getName());

    private boolean _readOnly;
    private IWebDavStore _store;
    private ResourceLocks _resourceLocks;


    public DoProppatch(IWebDavStore store, ResourceLocks resLocks, boolean readOnly) {
        _readOnly = readOnly;
        _store = store;
        _resourceLocks = resLocks;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        LOGGER.trace("-- " + this.getClass().getName());

        if (_readOnly) {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
            return;
        }

        String path = getRelativePath(req);
        String parentPath = getParentPath(getCleanPath(path));
        Hashtable<String, Integer> errorList = new Hashtable<>();

        if (!checkLocks(transaction, req, resp, _resourceLocks, parentPath)) {
            errorList.put(parentPath, WebDavStatus.SC_LOCKED);
            sendReport(req, resp, errorList);
            return;
        }

        if (!checkLocks(transaction, req, resp, _resourceLocks, path)) {
            errorList.put(path, WebDavStatus.SC_LOCKED);
            sendReport(req, resp, errorList);
            return;
        }

        String tempLockOwner = "doProppatch" + System.currentTimeMillis() + req.toString();
        if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
            StoredObject so;
            LockedObject lo;
            try {
                so = _store.getStoredObject(transaction, path);
                lo = _resourceLocks.getLockedObjectByPath(transaction,  getCleanPath(path));
                if (so == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                if (so.isNullResource()) {
                    String methodsAllowed = DeterminableMethod .determineMethodsAllowed(so);
                    resp.addHeader("Allow", methodsAllowed);
                    resp.sendError(WebDavStatus.SC_METHOD_NOT_ALLOWED);
                    return;
                }

                if (lo != null && lo.isExclusive()) {
                    errorList = new Hashtable<String, Integer>();
                    errorList.put(path, WebDavStatus.SC_LOCKED);
                    sendReport(req, resp, errorList);
                    return;
                }

                List<String> toset;
                List<String> toremove;
                List<String> tochange = new Vector<>();
                path = getCleanPath(getRelativePath(req));
                Node tosetNode;
                Node toremoveNode;

                if (req.getContentLength() != 0) {
                    DocumentBuilder documentBuilder = getDocumentBuilder();
                    try {
                        Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
                        Element rootElement = document.getDocumentElement();

                        tosetNode = XMLHelper.findSubElement(XMLHelper.findSubElement(rootElement, "set"), "prop");
                        toremoveNode = XMLHelper.findSubElement(XMLHelper.findSubElement(rootElement, "remove"), "prop");
                    } catch (Exception e) {
                        resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                } else {
                    resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                    return;
                }

                HashMap<String, String> namespaces = new HashMap<>();
                namespaces.put("DAV:", "D");

                if (tosetNode != null) {
                    toset = XMLHelper.getPropertiesFromXML(tosetNode);
                    tochange.addAll(toset);
                }
                if (toremoveNode != null) {
                    toremove = XMLHelper.getPropertiesFromXML(toremoveNode);
                    tochange.addAll(toremove);
                }

                resp.setStatus(WebDavStatus.SC_MULTI_STATUS);
                resp.setContentType("text/xml; charset=UTF-8");

                // Create multistatus object
                XMLWriter generatedXML = new XMLWriter(resp.getWriter(), namespaces);
                generatedXML.writeXMLHeader();
                generatedXML.writeElement("DAV::multistatus", XMLWriter.OPENING);
                generatedXML.writeElement("DAV::response", XMLWriter.OPENING);
                String status = "HTTP/1.1 " + WebDavStatus.SC_OK + " " + WebDavStatus.getStatusText(WebDavStatus.SC_OK);

                // Generating href element
                generatedXML.writeElement("DAV::href", XMLWriter.OPENING);

                String href = req.getContextPath();
                if ((href.endsWith("/")) && (path.startsWith("/"))) {
                    href += path.substring(1);
                }else {
                    href += path;
                }
                if ((so.isFolder()) && (!href.endsWith("/"))) {
                    href += "/";
                }

                generatedXML.writeText(rewriteUrl(href));
                generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
                for (String property : tochange) {
                    generatedXML.writeElement("DAV::propstat",  XMLWriter.OPENING);
                    generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
                    generatedXML.writeElement(property, XMLWriter.NO_CONTENT);
                    generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
                    generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
                    generatedXML.writeText(status);
                    generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
                    generatedXML.writeElement("DAV::propstat",  XMLWriter.CLOSING);
                }

                generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);
                generatedXML.writeElement("DAV::multistatus", XMLWriter.CLOSING);
                generatedXML.sendData();
            } catch (AccessDeniedException e) {
                resp.sendError(WebDavStatus.SC_FORBIDDEN);
            } catch (WebDavException e) {
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            } catch (ServletException e) {
                e.printStackTrace();
            } finally {
                _resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
            }
        } else {
            resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
