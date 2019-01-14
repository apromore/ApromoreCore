/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.AccessDeniedException;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.locking.IResourceLocks;
import org.apromore.filestore.webdav.locking.LockedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoPut extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoPut.class.getName());

    private IWebDavStore _store;
    private IResourceLocks _resourceLocks;
    private boolean _readOnly;
    private boolean _lazyFolderCreationOnPut;
    private String _userAgent;


    public DoPut(IWebDavStore store, IResourceLocks resLocks, boolean readOnly, boolean lazyFolderCreationOnPut) {
        _store = store;
        _resourceLocks = resLocks;
        _readOnly = readOnly;
        _lazyFolderCreationOnPut = lazyFolderCreationOnPut;
    }


    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        LOGGER.trace("-- " + this.getClass().getName());

        if (!_readOnly) {
            String path = getRelativePath(req);
            String parentPath = getParentPath(path);

            _userAgent = req.getHeader("User-Agent");
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

            String tempLockOwner = "doPut" + System.currentTimeMillis() + req.toString();
            if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0,  TEMP_TIMEOUT, TEMPORARY)) {
                StoredObject parentSo, so;
                try {
                    parentSo = _store.getStoredObject(transaction, parentPath);
                    if (parentPath != null && parentSo != null && parentSo.isResource()) {
                        resp.sendError(WebDavStatus.SC_FORBIDDEN);
                        return;
                    } else if (parentPath != null && parentSo == null && _lazyFolderCreationOnPut) {
                        _store.createFolder(transaction, parentPath);
                    } else if (parentPath != null && parentSo == null) {
                        errorList.put(parentPath, WebDavStatus.SC_NOT_FOUND);
                        sendReport(req, resp, errorList);
                        return;
                    }

                    so = _store.getStoredObject(transaction, path);
                    if (so == null) {
                        _store.createResource(transaction, path);
                    } else {
                        if (so.isNullResource()) {
                            LockedObject nullResourceLo = _resourceLocks.getLockedObjectByPath(transaction, path);
                            if (nullResourceLo == null) {
                                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                                return;
                            }
                            String nullResourceLockToken = nullResourceLo.getID();
                            String[] lockTokens = getLockIdFromIfHeader(req);
                            String lockToken;
                            if (lockTokens != null) {
                                lockToken = lockTokens[0];
                            } else {
                                resp.sendError(WebDavStatus.SC_BAD_REQUEST);
                                return;
                            }
                            if (lockToken.equals(nullResourceLockToken)) {
                                so.setNullResource(false);
                                so.setFolder(false);

                                String[] nullResourceLockOwners = nullResourceLo.getOwner();
                                String owner = null;
                                if (nullResourceLockOwners != null) {
                                    owner = nullResourceLockOwners[0];
                                }
                                if (!_resourceLocks.unlock(transaction, lockToken, owner)) {
                                    resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                                }
                            } else {
                                errorList.put(path, WebDavStatus.SC_LOCKED);
                                sendReport(req, resp, errorList);
                            }
                        }
                    }
                    doUserAgentWorkaround(resp);
                    long resourceLength = _store.setResourceContent(transaction, path, req.getInputStream(), null, null);
                    so = _store.getStoredObject(transaction, path);
                    if (resourceLength != -1) {
                        so.setResourceLength(resourceLength);
                    }

                } catch (AccessDeniedException e) {
                    resp.sendError(WebDavStatus.SC_FORBIDDEN);
                } catch (WebDavException e) {
                    resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    _resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
                }
            } else {
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
        }
    }

    /**
     * @param resp
     */
    private void doUserAgentWorkaround(HttpServletResponse resp) {
        if (_userAgent != null && _userAgent.indexOf("WebDAVFS") != -1
                && _userAgent.indexOf("Transmit") == -1) {
            LOGGER.trace("DoPut.execute() : do workaround for user agent '"
                    + _userAgent + "'");
            resp.setStatus(WebDavStatus.SC_CREATED);
        } else if (_userAgent != null && _userAgent.indexOf("Transmit") != -1) {
            // Transmit also uses WEBDAVFS 1.x.x but crashes
            // with SC_CREATED response
            LOGGER.trace("DoPut.execute() : do workaround for user agent '"
                    + _userAgent + "'");
            resp.setStatus(WebDavStatus.SC_NO_CONTENT);
        } else {
            resp.setStatus(WebDavStatus.SC_CREATED);
        }
    }
}
