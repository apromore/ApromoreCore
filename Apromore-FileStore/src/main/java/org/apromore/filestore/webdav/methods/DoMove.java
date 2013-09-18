package org.apromore.filestore.webdav.methods;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.AccessDeniedException;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.ObjectAlreadyExistsException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoMove extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoMove.class.getName());

    private ResourceLocks _resourceLocks;
    private DoDelete _doDelete;
    private DoCopy _doCopy;
    private boolean _readOnly;

    
    public DoMove(ResourceLocks resourceLocks, DoDelete doDelete, DoCopy doCopy, boolean readOnly) {
        _resourceLocks = resourceLocks;
        _doDelete = doDelete;
        _doCopy = doCopy;
        _readOnly = readOnly;
    }

    public void execute(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        if (!_readOnly) {
            LOGGER.trace("-- " + this.getClass().getName());

            String sourcePath = getRelativePath(req);
            Hashtable<String, Integer> errorList = new Hashtable<>();
            if (!checkLocks(transaction, req, resp, _resourceLocks, sourcePath)) {
                errorList.put(sourcePath, WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
                return;
            }

            String destinationPath = req.getHeader("Destination");
            if (destinationPath == null) {
                resp.sendError(WebDavStatus.SC_BAD_REQUEST);
                return;
            }

            if (!checkLocks(transaction, req, resp, _resourceLocks, destinationPath)) {
                errorList.put(destinationPath, WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
                return;
            }

            String tempLockOwner = "doMove" + System.currentTimeMillis() + req.toString();

            if (_resourceLocks.lock(transaction, sourcePath, tempLockOwner,
                    false, 0, TEMP_TIMEOUT, TEMPORARY)) {
                try {
                    if (_doCopy.copyResource(transaction, req, resp)) {
                        errorList = new Hashtable<>();
                        _doDelete.deleteResource(transaction, sourcePath, errorList, req, resp);
                        if (!errorList.isEmpty()) {
                            sendReport(req, resp, errorList);
                        }
                    }
                } catch (AccessDeniedException e) {
                    resp.sendError(WebDavStatus.SC_FORBIDDEN);
                } catch (ObjectAlreadyExistsException e) {
                    resp.sendError(WebDavStatus.SC_NOT_FOUND, req.getRequestURI());
                } catch (WebDavException e) {
                    resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                } finally {
                    _resourceLocks.unlockTemporaryLockedObjects(transaction, sourcePath, tempLockOwner);
                }
            } else {
                errorList.put(req.getHeader("Destination"), WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
            }
        } else {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
        }
    }

}
