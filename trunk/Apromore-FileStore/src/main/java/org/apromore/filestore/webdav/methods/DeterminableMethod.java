/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.filestore.webdav.methods;

import org.apromore.filestore.webdav.StoredObject;

public abstract class DeterminableMethod extends AbstractMethod {

    private static final String NULL_RESOURCE_METHODS_ALLOWED = "OPTIONS, MKCOL, PUT, PROPFIND, LOCK, UNLOCK";
    private static final String RESOURCE_METHODS_ALLOWED = "OPTIONS, GET, HEAD, POST, DELETE, TRACE" + ", PROPPATCH, COPY, MOVE, LOCK, UNLOCK, PROPFIND";
    private static final String FOLDER_METHOD_ALLOWED = ", PUT";
    private static final String LESS_ALLOWED_METHODS = "OPTIONS, MKCOL, PUT";

    /**
     * Determines the methods normally allowed for the resource.
     * 
     * @param so StoredObject representing the resource
     * @return all allowed methods, separated by commas
     */
    protected static String determineMethodsAllowed(StoredObject so) {
        try {
            if (so != null) {
                if (so.isNullResource()) {
                    return NULL_RESOURCE_METHODS_ALLOWED;
                } else if (so.isFolder()) {
                    return RESOURCE_METHODS_ALLOWED + FOLDER_METHOD_ALLOWED;
                }
                return RESOURCE_METHODS_ALLOWED;
            }
        } catch (Exception e) {
        }

        return LESS_ALLOWED_METHODS;
    }

}
