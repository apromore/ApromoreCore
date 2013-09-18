package org.apromore.filestore.webdav.methods;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.locking.IResourceLocks;
import org.apromore.filestore.webdav.locking.LockedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoUnlock extends DeterminableMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoUnlock.class.getName());

    private IWebDavStore _store;
    private IResourceLocks _resourceLocks;
    private boolean _readOnly;


    public DoUnlock(IWebDavStore store, IResourceLocks resourceLocks, boolean readOnly) {
        _store = store;
        _resourceLocks = resourceLocks;
        _readOnly = readOnly;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        LOGGER.trace("-- " + this.getClass().getName());

        if (_readOnly) {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
            return;
        } else {
            String path = getRelativePath(req);
            String tempLockOwner = "doUnlock" + System.currentTimeMillis()
                    + req.toString();
            try {
                if (_resourceLocks.lock(transaction, path, tempLockOwner,false, 0, TEMP_TIMEOUT, TEMPORARY)) {
                    String lockId = getLockIdFromLockTokenHeader(req);
                    LockedObject lo;
                    if (lockId != null && ((lo = _resourceLocks.getLockedObjectByID(transaction, lockId)) != null)) {

                        String[] owners = lo.getOwner();
                        String owner = null;
                        if (lo.isShared()) {
                            if (owners != null) {
                                for (String owner1 : owners) {
                                    lo.removeLockedObjectOwner(owner1);
                                }
                            }
                        } else {
                            if (owners != null) {
                                owner = owners[0];
                            } else {
                                owner = null;
                            }
                        }

                        if (_resourceLocks.unlock(transaction, lockId, owner)) {
                            StoredObject so = _store.getStoredObject(transaction, path);
                            if (so.isNullResource()) {
                                _store.removeObject(transaction, path);
                            }

                            resp.setStatus(WebDavStatus.SC_NO_CONTENT);
                        } else {
                            LOGGER.trace("DoUnlock failure at " + lo.getPath());
                            resp.sendError(WebDavStatus.SC_METHOD_FAILURE);
                        }

                    } else {
                        resp.sendError(WebDavStatus.SC_BAD_REQUEST);
                    }
                }
            } catch (LockFailedException e) {
                e.printStackTrace();
            } finally {
                _resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
            }
        }
    }

}
