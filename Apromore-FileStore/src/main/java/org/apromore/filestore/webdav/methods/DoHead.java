/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.filestore.webdav.methods;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.IMimeTyper;
import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.AccessDeniedException;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.ObjectAlreadyExistsException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DoHead extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoHead.class.getName());

    protected String _dftIndexFile;
    protected IWebDavStore _store;
    protected String _insteadOf404;
    protected ResourceLocks _resourceLocks;
    protected IMimeTyper _mimeTyper;
    protected int _contentLength;

    public DoHead(IWebDavStore store, String dftIndexFile, String insteadOf404, ResourceLocks resourceLocks, IMimeTyper mimeTyper, int contentLengthHeader) {
        _store = store;
        _dftIndexFile = dftIndexFile;
        _insteadOf404 = insteadOf404;
        _resourceLocks = resourceLocks;
        _mimeTyper = mimeTyper;
        _contentLength = contentLengthHeader;
    }

    public void execute(ITransaction transaction, HttpServletRequest req,
            HttpServletResponse resp) throws IOException, LockFailedException {
        boolean bUriExists = false;

        String path = getRelativePath(req);
        LOGGER.trace("-- " + this.getClass().getName());

        StoredObject so = _store.getStoredObject(transaction, path);
        if (so == null) {
            if (this._insteadOf404 != null && !_insteadOf404.trim().equals("")) {
                path = this._insteadOf404;
                so = _store.getStoredObject(transaction, this._insteadOf404);
            }
        } else {
            bUriExists = true;
        }
        if (so != null) {
            if (so.isFolder()) {
                if (_dftIndexFile != null && !_dftIndexFile.trim().equals("")) {
                    resp.sendRedirect(resp.encodeRedirectURL(req.getRequestURI() + this._dftIndexFile));
                    return;
                }
            } else if (so.isNullResource()) {
                String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.sendError(WebDavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }

            String tempLockOwner = "doGet" + System.currentTimeMillis() + req.toString();

            if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
                try {
                    String eTagMatch = req.getHeader("If-None-Match");
                    if (eTagMatch != null) {
                        if (eTagMatch.equals(getETag(so))) {
                            resp.setStatus(WebDavStatus.SC_NOT_MODIFIED);
                            return;
                        }
                    }

                    if (so.isResource()) {
                        if (path.endsWith("/") || (path.endsWith("\\"))) {
                            resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI());
                        } else {
                            long lastModified = so.getLastModified().getTime();
                            resp.setDateHeader("last-modified", lastModified);

                            String eTag = getETag(so);
                            resp.addHeader("ETag", eTag);

                            long resourceLength = so.getResourceLength();
                            if (_contentLength == 1) {
                                if (resourceLength > 0) {
                                    if (resourceLength <= Integer.MAX_VALUE) {
                                        resp.setContentLength((int) resourceLength);
                                    } else {
                                        resp.setHeader("content-length", "" + resourceLength);
                                    }
                                }
                            }

                            String mimeType = _mimeTyper.getMimeType(path);
                            if (mimeType != null) {
                                resp.setContentType(mimeType);
                            } else {
                                int lastSlash = path.replace('\\', '/') .lastIndexOf('/');
                                int lastDot = path.indexOf(".", lastSlash);
                                if (lastDot == -1) {
                                    resp.setContentType("text/html");
                                }
                            }

                            doBody(transaction, resp, path);
                        }
                    } else {
                        folderBody(transaction, path, resp, req);
                    }
                } catch (AccessDeniedException e) {
                    resp.sendError(WebDavStatus.SC_FORBIDDEN);
                } catch (ObjectAlreadyExistsException e) {
                    resp.sendError(WebDavStatus.SC_NOT_FOUND, req.getRequestURI());
                } catch (WebDavException e) {
                    resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    _resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
                }
            } else {
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            folderBody(transaction, path, resp, req);
        }

        if (!bUriExists) {
            resp.setStatus(WebDavStatus.SC_NOT_FOUND);
        }
    }

    protected void folderBody(ITransaction transaction, String path, HttpServletResponse resp, HttpServletRequest req) throws IOException {
        // no body for HEAD
    }

    protected void doBody(ITransaction transaction, HttpServletResponse resp, String path) throws IOException {
        // no body for HEAD
    }
}
