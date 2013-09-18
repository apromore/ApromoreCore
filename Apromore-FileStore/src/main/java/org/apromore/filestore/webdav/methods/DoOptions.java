/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apromore.filestore.webdav.methods;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.AccessDeniedException;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoOptions extends DeterminableMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoOptions.class.getName());

    private IWebDavStore _store;
    private ResourceLocks _resourceLocks;


    public DoOptions(IWebDavStore store, ResourceLocks resLocks) {
        _store = store;
        _resourceLocks = resLocks;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        LOGGER.trace("-- " + this.getClass().getName());

        String tempLockOwner = "doOptions" + System.currentTimeMillis() + req.toString();
        String path = getRelativePath(req);
        if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
            StoredObject so;
            try {
                resp.addHeader("DAV", "1, 2");
                so = _store.getStoredObject(transaction, path);
                String methodsAllowed = determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.addHeader("MS-Author-Via", "DAV");
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
    }
}
