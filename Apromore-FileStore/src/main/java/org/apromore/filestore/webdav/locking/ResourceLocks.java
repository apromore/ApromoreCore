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

package org.apromore.filestore.webdav.locking;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * simple locking management for concurrent data access, NOT the webdav locking.
 * ( could that be used instead? )
 * 
 * IT IS ACTUALLY USED FOR DOLOCK
 * 
 * @author re
 */
public class ResourceLocks implements IResourceLocks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLocks.class.getName());

    /**
     * after creating this much LockedObjects, a cleanup deletes unused
     * LockedObjects
     */
    private final int _cleanupLimit = 100000;

    protected int _cleanupCounter = 0;

    /**
     * keys: path value: LockedObject from that path
     * Concurrent access can occur
     */
    protected Hashtable<String, LockedObject> _locks = new Hashtable<>();

    /**
     * keys: id value: LockedObject from that id
     * Concurrent access can occur
     */
    protected Hashtable<String, LockedObject> _locksByID = new Hashtable<>();

    /**
     * keys: path value: Temporary LockedObject from that path
     * Concurrent access can occur
     */
    protected Hashtable<String, LockedObject> _tempLocks = new Hashtable<>();

    /**
     * keys: id value: Temporary LockedObject from that id
     * Concurrent access can occur
     */
    protected Hashtable<String, LockedObject> _tempLocksByID = new Hashtable<>();

    // REMEMBER TO REMOVE UNUSED LOCKS FROM THE HASHTABLE AS WELL

    protected LockedObject _root = null;

    protected LockedObject _tempRoot = null;

    private boolean _temporary = true;

    public ResourceLocks() {
        _root = new LockedObject(this, "/", true);
        _tempRoot = new LockedObject(this, "/", false);
    }

    public synchronized boolean lock(ITransaction transaction, String path, String owner, boolean exclusive, int depth, int timeout,
            boolean temporary) throws LockFailedException {
        LockedObject lo;

        if (temporary) {
            lo = generateTempLockedObjects(transaction, path);
            lo._type = "read";
        } else {
            lo = generateLockedObjects(transaction, path);
            lo._type = "write";
        }

        if (lo.checkLocks(exclusive, depth)) {
            lo._exclusive = exclusive;
            lo._lockDepth = depth;
            lo._expiresAt = System.currentTimeMillis() + (timeout * 1000);
            if (lo._parent != null) {
                lo._parent._expiresAt = lo._expiresAt;
                if (lo._parent.equals(_root)) {
                    LockedObject rootLo = getLockedObjectByPath(transaction, _root.getPath());
                    rootLo._expiresAt = lo._expiresAt;
                } else if (lo._parent.equals(_tempRoot)) {
                    LockedObject tempRootLo = getTempLockedObjectByPath(transaction, _tempRoot.getPath());
                    tempRootLo._expiresAt = lo._expiresAt;
                }
            }
            if (lo.addLockedObjectOwner(owner)) {
                return true;
            } else {
                LOGGER.trace("Couldn't set owner \"" + owner + "\" to resource at '" + path + "'");
                return false;
            }
        } else {
            LOGGER.trace("Lock resource at " + path + " failed because\na parent or child resource is currently locked");
            return false;
        }
    }

    public synchronized boolean unlock(ITransaction transaction, String id, String owner) {
        if (_locksByID.containsKey(id)) {
            String path = _locksByID.get(id).getPath();
            if (_locks.containsKey(path)) {
                LockedObject lo = _locks.get(path);
                lo.removeLockedObjectOwner(owner);

                if (lo._children == null && lo._owner == null) {
                    lo.removeLockedObject();
                }
            } else {
                LOGGER.trace("ResourceLocks.unlock(): no lock for path " + path);
                return false;
            }

            if (_cleanupCounter > _cleanupLimit) {
                _cleanupCounter = 0;
                cleanLockedObjects(transaction, _root, !_temporary);
            }
        }
        checkTimeouts(transaction, !_temporary);

        return true;

    }

    public synchronized void unlockTemporaryLockedObjects(ITransaction transaction, String path, String owner) {
        if (_tempLocks.containsKey(path)) {
            LockedObject lo = _tempLocks.get(path);
            lo.removeLockedObjectOwner(owner);
        } else {
            LOGGER.trace("ResourceLocks.unlock(): no lock for path " + path);
        }

        if (_cleanupCounter > _cleanupLimit) {
            _cleanupCounter = 0;
            cleanLockedObjects(transaction, _tempRoot, _temporary);
        }

        checkTimeouts(transaction, _temporary);

    }

    public void checkTimeouts(ITransaction transaction, boolean temporary) {
        if (!temporary) {
            Enumeration<LockedObject> lockedObjects = _locks.elements();
            while (lockedObjects.hasMoreElements()) {
                LockedObject currentLockedObject = lockedObjects.nextElement();

                if (currentLockedObject._expiresAt < System.currentTimeMillis()) {
                    currentLockedObject.removeLockedObject();
                }
            }
        } else {
            Enumeration<LockedObject> lockedObjects = _tempLocks.elements();
            while (lockedObjects.hasMoreElements()) {
                LockedObject currentLockedObject = lockedObjects.nextElement();

                if (currentLockedObject._expiresAt < System.currentTimeMillis()) {
                    currentLockedObject.removeTempLockedObject();
                }
            }
        }

    }

    public boolean exclusiveLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException {
        return lock(transaction, path, owner, true, depth, timeout, false);
    }

    public boolean sharedLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException {
        return lock(transaction, path, owner, false, depth, timeout, false);
    }

    public LockedObject getLockedObjectByID(ITransaction transaction, String id) {
        if (_locksByID.containsKey(id)) {
            return _locksByID.get(id);
        } else {
            return null;
        }
    }

    public LockedObject getLockedObjectByPath(ITransaction transaction, String path) {
        if (_locks.containsKey(path)) {
            return this._locks.get(path);
        } else {
            return null;
        }
    }

    public LockedObject getTempLockedObjectByID(ITransaction transaction, String id) {
        if (_tempLocksByID.containsKey(id)) {
            return _tempLocksByID.get(id);
        } else {
            return null;
        }
    }

    public LockedObject getTempLockedObjectByPath(ITransaction transaction, String path) {
        if (_tempLocks.containsKey(path)) {
            return this._tempLocks.get(path);
        } else {
            return null;
        }
    }

    /**
     * generates real LockedObjects for the resource at path and its parent
     * folders. does not create new LockedObjects if they already exist
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path path to the (new) LockedObject
     * @return the LockedObject for path.
     */
    private LockedObject generateLockedObjects(ITransaction transaction, String path) {
        if (!_locks.containsKey(path)) {
            LockedObject returnObject = new LockedObject(this, path, !_temporary);
            String parentPath = getParentPath(path);
            if (parentPath != null) {
                LockedObject parentLockedObject = generateLockedObjects(transaction, parentPath);
                parentLockedObject.addChild(returnObject);
                returnObject._parent = parentLockedObject;
            }
            return returnObject;
        } else {
            return this._locks.get(path);
        }

    }

    /**
     * generates temporary LockedObjects for the resource at path and its parent
     * folders. does not create new LockedObjects if they already exist
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path path to the (new) LockedObject
     * @return the LockedObject for path.
     */
    private LockedObject generateTempLockedObjects(ITransaction transaction, String path) {
        if (!_tempLocks.containsKey(path)) {
            LockedObject returnObject = new LockedObject(this, path, _temporary);
            String parentPath = getParentPath(path);
            if (parentPath != null) {
                LockedObject parentLockedObject = generateTempLockedObjects(transaction, parentPath);
                parentLockedObject.addChild(returnObject);
                returnObject._parent = parentLockedObject;
            }
            return returnObject;
        } else {
            return this._tempLocks.get(path);
        }

    }

    /**
     * deletes unused LockedObjects and resets the counter. works recursively
     * starting at the given LockedObject
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param lo LockedObject
     * @param temporary Clean temporary or real locks
     * @return if cleaned
     */
    private boolean cleanLockedObjects(ITransaction transaction, LockedObject lo, boolean temporary) {
        if (lo._children == null) {
            if (lo._owner == null) {
                if (temporary) {
                    lo.removeTempLockedObject();
                } else {
                    lo.removeLockedObject();
                }
                return true;
            } else {
                return false;
            }
        } else {
            boolean canDelete = true;
            int limit = lo._children.length;
            for (int i = 0; i < limit; i++) {
                if (!cleanLockedObjects(transaction, lo._children[i], temporary)) {
                    canDelete = false;
                } else {
                    i--;
                    limit--;
                }
            }
            if (canDelete) {
                if (lo._owner == null) {
                    if (temporary) {
                        lo.removeTempLockedObject();
                    } else {
                        lo.removeLockedObject();
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * creates the parent path from the given path by removing the last '/' and
     * everything after that
     * 
     * @param path the path
     * @return parent path
     */
    private String getParentPath(String path) {
        int slash = path.lastIndexOf('/');
        if (slash == -1) {
            return null;
        } else {
            if (slash == 0) {
                return "/";
            } else {
                return path.substring(0, slash);
            }
        }
    }

}
