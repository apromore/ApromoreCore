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

public class DoMkcol extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoCopy.class.getName());

    private IWebDavStore _store;
    private IResourceLocks _resourceLocks;
    private boolean _readOnly;

    public DoMkcol(IWebDavStore store, IResourceLocks resourceLocks, boolean readOnly) {
        _store = store;
        _resourceLocks = resourceLocks;
        _readOnly = readOnly;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        LOGGER.trace("-- " + this.getClass().getName());

        if (!_readOnly) {
            String path = getRelativePath(req);
            String parentPath = getParentPath(getCleanPath(path));

            Hashtable<String, Integer> errorList = new Hashtable<>();
            if (!checkLocks(transaction, req, resp, _resourceLocks, parentPath)) {
                LOGGER.trace("MkCol on locked resource (parentPath) not executable!\n Sending SC_FORBIDDEN (403) error response!");

                resp.sendError(WebDavStatus.SC_FORBIDDEN);
                return;
            }

            String tempLockOwner = "doMkcol" + System.currentTimeMillis() + req.toString();
            if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
                StoredObject parentSo, so;
                try {
                    parentSo = _store.getStoredObject(transaction, parentPath);
                    if (parentSo == null) {
                        resp.sendError(WebDavStatus.SC_CONFLICT);
                        return;
                    }
                    if (parentPath != null && parentSo.isFolder()) {
                        so = _store.getStoredObject(transaction, path);
                        if (so == null) {
                            _store.createFolder(transaction, path);
                            resp.setStatus(WebDavStatus.SC_CREATED);
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
                                    so.setFolder(true);

                                    String[] nullResourceLockOwners = nullResourceLo .getOwner();
                                    String owner = null;
                                    if (nullResourceLockOwners != null) {
                                        owner = nullResourceLockOwners[0];
                                    }
                                    if (_resourceLocks.unlock(transaction, lockToken, owner)) {
                                        resp.setStatus(WebDavStatus.SC_CREATED);
                                    } else {
                                        resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                                    }

                                } else {
                                    LOGGER.trace("MkCol on lock-null-resource with wrong lock-token!\n Sending multistatus error report!");
                                    errorList.put(path, WebDavStatus.SC_LOCKED);
                                    sendReport(req, resp, errorList);
                                }

                            } else {
                                String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
                                resp.addHeader("Allow", methodsAllowed);
                                resp.sendError(WebDavStatus.SC_METHOD_NOT_ALLOWED);
                            }
                        }

                    } else if (parentPath != null && parentSo.isResource()) {
                        LOGGER.trace("MkCol on resource is not executable\n Sending SC_METHOD_NOT_ALLOWED (405) error response!");
                        String methodsAllowed = DeterminableMethod.determineMethodsAllowed(parentSo);
                        resp.addHeader("Allow", methodsAllowed);
                        resp.sendError(WebDavStatus.SC_METHOD_NOT_ALLOWED);
                    } else {
                        resp.sendError(WebDavStatus.SC_FORBIDDEN);
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

}
