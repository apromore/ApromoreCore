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
