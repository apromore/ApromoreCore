/*
 * Copyright © 2009-2015 The Apromore Initiative.
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
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.AccessDeniedException;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.ObjectAlreadyExistsException;
import org.apromore.filestore.webdav.exceptions.ObjectNotFoundException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoDelete extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoDelete.class.getName());

    private IWebDavStore _store;
    private ResourceLocks _resourceLocks;
    private boolean _readOnly;

    public DoDelete(IWebDavStore store, ResourceLocks resourceLocks, boolean readOnly) {
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
                errorList.put(parentPath, WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
                return; 
            }

            if (!checkLocks(transaction, req, resp, _resourceLocks, path)) {
                errorList.put(path, WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
                return; 
            }

            String tempLockOwner = "doDelete" + System.currentTimeMillis() + req.toString();
            if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0,  TEMP_TIMEOUT, TEMPORARY)) {
                try {
                    errorList = new Hashtable<>();
                    deleteResource(transaction, path, errorList, req, resp);
                    if (!errorList.isEmpty()) {
                        sendReport(req, resp, errorList);
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
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
        }

    }

    /**
     * deletes the recources at "path"
     * 
     * @param transaction
     *      indicates that the method is within the scope of a WebDAV
     *      transaction
     * @param path
     *      the folder to be deleted
     * @param errorList
     *      all errors that ocurred
     * @param req
     *      HttpServletRequest
     * @param resp
     *      HttpServletResponse
     * @throws WebDavException
     *      if an error in the underlying store occurs
     * @throws java.io.IOException
     *      when an error occurs while sending the response
     */
    public void deleteResource(ITransaction transaction, String path,
            Hashtable<String, Integer> errorList, HttpServletRequest req,
            HttpServletResponse resp) throws IOException, WebDavException {
        resp.setStatus(WebDavStatus.SC_NO_CONTENT);

        if (!_readOnly) {
            StoredObject so = _store.getStoredObject(transaction, path);
            if (so != null) {

                if (so.isResource()) {
                    _store.removeObject(transaction, path);
                } else {
                    if (so.isFolder()) {
                        deleteFolder(transaction, path, errorList, req, resp);
                        _store.removeObject(transaction, path);
                    } else {
                        resp.sendError(WebDavStatus.SC_NOT_FOUND);
                    }
                }
            } else {
                resp.sendError(WebDavStatus.SC_NOT_FOUND);
            }
            so = null;

        } else {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
        }
    }

    /**
     * 
     * helper method of deleteResource() deletes the folder and all of its
     * contents
     * 
     * @param transaction
     *      indicates that the method is within the scope of a WebDAV
     *      transaction
     * @param path
     *      the folder to be deleted
     * @param errorList
     *      all errors that ocurred
     * @param req
     *      HttpServletRequest
     * @param resp
     *      HttpServletResponse
     * @throws WebDavException
     *      if an error in the underlying store occurs
     */
    private void deleteFolder(ITransaction transaction, String path,
            Hashtable<String, Integer> errorList, HttpServletRequest req,
            HttpServletResponse resp) throws WebDavException {

        String[] children = _store.getChildrenNames(transaction, path);
        children = children == null ? new String[] {} : children;
        StoredObject so = null;
        for (int i = children.length - 1; i >= 0; i--) {
            children[i] = "/" + children[i];
            try {
                so = _store.getStoredObject(transaction, path + children[i]);
                if (so.isResource()) {
                    _store.removeObject(transaction, path + children[i]);

                } else {
                    deleteFolder(transaction, path + children[i], errorList,
                            req, resp);

                    _store.removeObject(transaction, path + children[i]);

                }
            } catch (AccessDeniedException e) {
                errorList.put(path + children[i], WebDavStatus.SC_FORBIDDEN);
            } catch (ObjectNotFoundException e) {
                errorList.put(path + children[i], WebDavStatus.SC_NOT_FOUND);
            } catch (WebDavException e) {
                errorList.put(path + children[i], WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            }
        }
        so = null;

    }

}
