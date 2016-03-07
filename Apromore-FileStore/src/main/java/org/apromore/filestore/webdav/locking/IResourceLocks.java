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

package org.apromore.filestore.webdav.locking;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.exceptions.LockFailedException;

public interface IResourceLocks {

    /**
     * Tries to lock the resource at "path".
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path what resource to lock
     * @param owner the owner of the lock
     * @param exclusive if the lock should be exclusive (or shared)
     * @param depth depth
     * @param timeout Lock Duration in seconds.
     * @return true if the resource at path was successfully locked, false if an existing lock prevented this
     * @throws LockFailedException
     */
    boolean lock(ITransaction transaction, String path, String owner, boolean exclusive, int depth, int timeout, boolean temporary)
            throws LockFailedException;

    /**
     * Unlocks all resources at "path" (and all subfolders if existing)<p/> that
     * have the same owner.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param id id to the resource to unlock
     * @param owner who wants to unlock
     */
    boolean unlock(ITransaction transaction, String id, String owner);

    /**
     * Unlocks all resources at "path" (and all subfolders if existing)<p/> that
     * have the same owner.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path what resource to unlock
     * @param owner who wants to unlock
     */
    void unlockTemporaryLockedObjects(ITransaction transaction, String path, String owner);

    /**
     * Deletes LockedObjects, where timeout has reached.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param temporary Check timeout on temporary or real locks
     */
    void checkTimeouts(ITransaction transaction, boolean temporary);

    /**
     * Tries to lock the resource at "path" exclusively.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path what resource to lock
     * @param owner the owner of the lock
     * @param depth depth
     * @param timeout Lock Duration in seconds.
     * @return true if the resource at path was successfully locked, false if an
     *  existing lock prevented this
     * @throws LockFailedException
     */
    boolean exclusiveLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException;

    /**
     * Tries to lock the resource at "path" shared.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path what resource to lock
     * @param owner the owner of the lock
     * @param depth depth
     * @param timeout Lock Duration in seconds.
     * @return true if the resource at path was successfully locked, false if an existing lock prevented this
     * @throws LockFailedException
     */
    boolean sharedLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException;

    /**
     * Gets the LockedObject corresponding to specified id.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param id LockToken to requested resource
     * @return LockedObject or null if no LockedObject on specified path exists
     */
    LockedObject getLockedObjectByID(ITransaction transaction, String id);

    /**
     * Gets the LockedObject on specified path.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path Path to requested resource
     * @return LockedObject or null if no LockedObject on specified path exists
     */
    LockedObject getLockedObjectByPath(ITransaction transaction, String path);

    /**
     * Gets the LockedObject corresponding to specified id (locktoken).
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param id LockToken to requested resource
     * @return LockedObject or null if no LockedObject on specified path exists
     */
    LockedObject getTempLockedObjectByID(ITransaction transaction, String id);

    /**
     * Gets the LockedObject on specified path.
     * 
     * @param transaction indicates that the method is within the scope of a WebDAV transaction
     * @param path Path to requested resource
     * @return LockedObject or null if no LockedObject on specified path exists
     */
    LockedObject getTempLockedObjectByPath(ITransaction transaction, String path);

}
