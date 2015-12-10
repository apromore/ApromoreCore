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

package org.apromore.filestore.webdav;

import java.util.Date;

public class StoredObject {

    private boolean isFolder;
    private Date lastModified;
    private Date creationDate;
    private long contentLength;

    private boolean isNullRessource;

    /**
     * Determines whether the StoredObject is a folder or a resource
     * 
     * @return true if the StoredObject is a collection
     */
    public boolean isFolder() {
        return (isFolder);
    }

    /**
     * Determines whether the StoredObject is a folder or a resource
     * 
     * @return true if the StoredObject is a resource
     */
    public boolean isResource() {
        return (!isFolder);
    }

    /**
     * Sets a new StoredObject as a collection or resource
     * 
     * @param f true - collection ; false - resource
     */
    public void setFolder(boolean f) {
        this.isFolder = f;
    }

    /**
     * Gets the date of the last modification
     * 
     * @return last modification Date
     */
    public Date getLastModified() {
        return (lastModified);
    }

    /**
     * Sets the date of the last modification
     * 
     * @param d date of the last modification
     */
    public void setLastModified(Date d) {
        this.lastModified = d;
    }

    /**
     * Gets the date of the creation
     * 
     * @return creation Date
     */
    public Date getCreationDate() {
        return (creationDate);
    }

    /**
     * Sets the date of the creation
     * 
     * @param d date of the creation
     */
    public void setCreationDate(Date d) {
        this.creationDate = d;
    }

    /**
     * Gets the length of the resource content
     * 
     * @return length of the resource content
     */
    public long getResourceLength() {
        return (contentLength);
    }

    /**
     * Sets the length of the resource content
     * 
     * @param l  the length of the resource content
     */
    public void setResourceLength(long l) {
        this.contentLength = l;
    }

    /**
     * Gets the state of the resource
     * 
     * @return true if the resource is in lock-null state
     */
    public boolean isNullResource() {
        return isNullRessource;
    }

    /**
     * Sets a StoredObject as a lock-null resource
     * 
     * @param f true to set the resource as lock-null resource
     */
    public void setNullResource(boolean f) {
        this.isNullRessource = f;
        this.isFolder = false;
        this.creationDate = null;
        this.lastModified = null;
        this.contentLength = 0;
    }

}
