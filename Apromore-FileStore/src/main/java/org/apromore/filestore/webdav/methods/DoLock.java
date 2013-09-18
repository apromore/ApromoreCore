package org.apromore.filestore.webdav.methods;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.fromcatalina.XMLWriter;
import org.apromore.filestore.webdav.locking.IResourceLocks;
import org.apromore.filestore.webdav.locking.LockedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DoLock extends AbstractMethod {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoLock.class.getName());

    private IWebDavStore _store;
    private IResourceLocks _resourceLocks;
    private boolean _readOnly;
    private boolean _macLockRequest = false;
    private boolean _exclusive = false;
    private String _type = null;
    private String _lockOwner = null;
    private String _path = null;
    private String _parentPath = null;
    private String _userAgent = null;

    public DoLock(IWebDavStore store, IResourceLocks resourceLocks, boolean readOnly) {
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
            _path = getRelativePath(req);
            _parentPath = getParentPath(getCleanPath(_path));

            Hashtable<String, Integer> errorList = new Hashtable<>();

            if (!checkLocks(transaction, req, resp, _resourceLocks, _path)) {
                errorList.put(_path, WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
                return;
            }

            if (!checkLocks(transaction, req, resp, _resourceLocks, _parentPath)) {
                errorList.put(_parentPath, WebDavStatus.SC_LOCKED);
                sendReport(req, resp, errorList);
                return;
            }

            _userAgent = req.getHeader("User-Agent");
            if (_userAgent != null && _userAgent.contains("Darwin")) {
                _macLockRequest = true;
                String timeString = Long.toString(System.currentTimeMillis());
                _lockOwner = _userAgent.concat(timeString);
            }

            String tempLockOwner = "doLock" + System.currentTimeMillis()+ req.toString();
            if (_resourceLocks.lock(transaction, _path, tempLockOwner, false,  0, TEMP_TIMEOUT, TEMPORARY)) {
                try {
                    if (req.getHeader("If") != null) {
                        doRefreshLock(transaction, req, resp);
                    } else {
                        doLock(transaction, req, resp);
                    }
                } catch (LockFailedException e) {
                    resp.sendError(WebDavStatus.SC_LOCKED);
                    e.printStackTrace();
                } finally {
                    _resourceLocks.unlockTemporaryLockedObjects(transaction, _path, tempLockOwner);
                }
            }
        }
    }

    private void doLock(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        StoredObject so = _store.getStoredObject(transaction, _path);

        if (so != null) {
            doLocking(transaction, req, resp);
        } else {
            doNullResourceLock(transaction, req, resp);
        }

        so = null;
        _exclusive = false;
        _type = null;
        _lockOwner = null;

    }

    private void doLocking(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction, _path);
        if (lo != null) {
            if (lo.isExclusive()) {
                sendLockFailError(transaction, req, resp);
                return;
            }
        }
        try {
            executeLock(transaction, req, resp);
        } catch (ServletException e) {
            resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            LOGGER.trace(e.toString());
        } catch (LockFailedException e) {
            sendLockFailError(transaction, req, resp);
        } finally {
            lo = null;
        }
    }

    private void doNullResourceLock(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StoredObject parentSo, nullSo;
        try {
            parentSo = _store.getStoredObject(transaction, _parentPath);
            if (_parentPath != null && parentSo == null) {
                _store.createFolder(transaction, _parentPath);
            } else if (_parentPath != null && parentSo.isResource()) {
                resp.sendError(WebDavStatus.SC_PRECONDITION_FAILED);
                return;
            }

            nullSo = _store.getStoredObject(transaction, _path);
            if (nullSo == null) {
                _store.createResource(transaction, _path);
                if (_userAgent != null && _userAgent.contains("Transmit")) {
                    LOGGER.trace("DoLock.execute() : do workaround for user agent '" + _userAgent + "'");
                    resp.setStatus(WebDavStatus.SC_NO_CONTENT);
                } else {
                    resp.setStatus(WebDavStatus.SC_CREATED);
                }

            } else {
                sendLockFailError(transaction, req, resp);
                return;
            }
            nullSo = _store.getStoredObject(transaction, _path);
            nullSo.setNullResource(true);
            executeLock(transaction, req, resp);
        } catch (LockFailedException e) {
            sendLockFailError(transaction, req, resp);
        } catch (WebDavException | ServletException e) {
            resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        } finally {
            parentSo = null;
            nullSo = null;
        }
    }

    private void doRefreshLock(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws IOException, LockFailedException {
        String[] lockTokens = getLockIdFromIfHeader(req);
        String lockToken = null;
        if (lockTokens != null)
            lockToken = lockTokens[0];

        if (lockToken != null) {
            LockedObject refreshLo = _resourceLocks.getLockedObjectByID(transaction, lockToken);
            if (refreshLo != null) {
                int timeout = getTimeout(transaction, req);
                refreshLo.refreshTimeout(timeout);
                generateXMLReport(transaction, resp, refreshLo);
                refreshLo = null;
            } else {
                resp.sendError(WebDavStatus.SC_PRECONDITION_FAILED);
            }
        } else {
            resp.sendError(WebDavStatus.SC_PRECONDITION_FAILED);
        }
    }


    /**
     * Executes the LOCK
     */
    private void executeLock(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws LockFailedException, IOException,
            ServletException {
        if (_macLockRequest) {
            LOGGER.trace("DoLock.execute() : do workaround for user agent '" + _userAgent + "'");
            doMacLockRequestWorkaround(transaction, req, resp);
        } else {
            if (getLockInformation(transaction, req, resp)) {
                int depth = getDepth(req);
                int lockDuration = getTimeout(transaction, req);

                boolean lockSuccess;
                if (_exclusive) {
                    lockSuccess = _resourceLocks.exclusiveLock(transaction, _path, _lockOwner, depth, lockDuration);
                } else {
                    lockSuccess = _resourceLocks.sharedLock(transaction, _path, _lockOwner, depth, lockDuration);
                }

                if (lockSuccess) {
                    LockedObject lo = _resourceLocks.getLockedObjectByPath( transaction, _path);
                    if (lo != null) {
                        generateXMLReport(transaction, resp, lo);
                    } else {
                        resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                    }
                } else {
                    sendLockFailError(transaction, req, resp);
                    throw new LockFailedException();
                }
            } else {
                resp.setContentType("text/xml; charset=UTF-8");
                resp.sendError(WebDavStatus.SC_BAD_REQUEST);
            }
        }
    }

    /**
     * Tries to get the LockInformation from LOCK request
     */
    private boolean getLockInformation(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Node lockInfoNode;
        DocumentBuilder documentBuilder;

        documentBuilder = getDocumentBuilder();
        try {
            Document document = documentBuilder.parse(new InputSource(req.getInputStream()));
            lockInfoNode = document.getDocumentElement();
            if (lockInfoNode != null) {
                NodeList childList = lockInfoNode.getChildNodes();
                Node lockScopeNode = null;
                Node lockTypeNode = null;
                Node lockOwnerNode = null;
                Node currentNode;
                String nodeName;

                for (int i = 0; i < childList.getLength(); i++) {
                    currentNode = childList.item(i);
                    if (currentNode.getNodeType() == Node.ELEMENT_NODE|| currentNode.getNodeType() == Node.TEXT_NODE) {
                        nodeName = currentNode.getNodeName();
                        if (nodeName.endsWith("locktype")) {
                            lockTypeNode = currentNode;
                        }
                        if (nodeName.endsWith("lockscope")) {
                            lockScopeNode = currentNode;
                        }
                        if (nodeName.endsWith("owner")) {
                            lockOwnerNode = currentNode;
                        }
                    } else {
                        return false;
                    }
                }

                if (lockScopeNode != null) {
                    String scope = null;
                    childList = lockScopeNode.getChildNodes();
                    for (int i = 0; i < childList.getLength(); i++) {
                        currentNode = childList.item(i);

                        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                            scope = currentNode.getNodeName();

                            if (scope.endsWith("exclusive")) {
                                _exclusive = true;
                            } else if (scope.equals("shared")) {
                                _exclusive = false;
                            }
                        }
                    }
                    if (scope == null) {
                        return false;
                    }

                } else {
                    return false;
                }

                if (lockTypeNode != null) {
                    childList = lockTypeNode.getChildNodes();
                    for (int i = 0; i < childList.getLength(); i++) {
                        currentNode = childList.item(i);

                        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                            _type = currentNode.getNodeName();

                            if (_type.endsWith("write")) {
                                _type = "write";
                            } else if (_type.equals("read")) {
                                _type = "read";
                            }
                        }
                    }
                    if (_type == null) {
                        return false;
                    }
                } else {
                    return false;
                }

                if (lockOwnerNode != null) {
                    childList = lockOwnerNode.getChildNodes();
                    for (int i = 0; i < childList.getLength(); i++) {
                        currentNode = childList.item(i);

                        if (currentNode.getNodeType() == Node.ELEMENT_NODE
                             || currentNode.getNodeType() == Node.TEXT_NODE ) {
                            _lockOwner = currentNode.getTextContent();
                        }
                    }
                }
                if (_lockOwner == null) {
                    return false;
                }
            } else {
                return false;
            }

        } catch (DOMException e) {
            resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Ties to read the timeout from request
     */
    private int getTimeout(ITransaction transaction, HttpServletRequest req) {
        int lockDuration = DEFAULT_TIMEOUT;
        String lockDurationStr = req.getHeader("Timeout");

        if (lockDurationStr == null) {
            lockDuration = DEFAULT_TIMEOUT;
        } else {
            int commaPos = lockDurationStr.indexOf(',');
            // if multiple timeouts, just use the first one
            if (commaPos != -1) {
                lockDurationStr = lockDurationStr.substring(0, commaPos);
            }
            if (lockDurationStr.startsWith("Second-")) {
                lockDuration = new Integer(lockDurationStr.substring(7));
            } else {
                if (lockDurationStr.equalsIgnoreCase("infinity")) {
                    lockDuration = MAX_TIMEOUT;
                } else {
                    try {
                        lockDuration = new Integer(lockDurationStr);
                    } catch (NumberFormatException e) {
                        lockDuration = MAX_TIMEOUT;
                    }
                }
            }
            if (lockDuration <= 0) {
                lockDuration = DEFAULT_TIMEOUT;
            }
            if (lockDuration > MAX_TIMEOUT) {
                lockDuration = MAX_TIMEOUT;
            }
        }
        return lockDuration;
    }

    /**
     * Generates the response XML with all lock information
     */
    private void generateXMLReport(ITransaction transaction,
            HttpServletResponse resp, LockedObject lo) throws IOException {

        HashMap<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("DAV:", "D");

        resp.setStatus(WebDavStatus.SC_OK);
        resp.setContentType("text/xml; charset=UTF-8");

        XMLWriter generatedXML = new XMLWriter(resp.getWriter(), namespaces);
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
        generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.OPENING);
        generatedXML.writeElement("DAV::activelock", XMLWriter.OPENING);

        generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
        generatedXML.writeProperty("DAV::" + _type);
        generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

        generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
        if (_exclusive) {
            generatedXML.writeProperty("DAV::exclusive");
        } else {
            generatedXML.writeProperty("DAV::shared");
        }
        generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

        int depth = lo.getLockDepth();

        generatedXML.writeElement("DAV::depth", XMLWriter.OPENING);
        if (depth == INFINITY) {
            generatedXML.writeText("Infinity");
        } else {
            generatedXML.writeText(String.valueOf(depth));
        }
        generatedXML.writeElement("DAV::depth", XMLWriter.CLOSING);

        generatedXML.writeElement("DAV::owner", XMLWriter.OPENING);
        generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
        generatedXML.writeText(_lockOwner);
        generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
        generatedXML.writeElement("DAV::owner", XMLWriter.CLOSING);

        long timeout = lo.getTimeoutMillis();
        generatedXML.writeElement("DAV::timeout", XMLWriter.OPENING);
        generatedXML.writeText("Second-" + timeout / 1000);
        generatedXML.writeElement("DAV::timeout", XMLWriter.CLOSING);

        String lockToken = lo.getID();
        generatedXML.writeElement("DAV::locktoken", XMLWriter.OPENING);
        generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
        generatedXML.writeText("opaquelocktoken:" + lockToken);
        generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
        generatedXML.writeElement("DAV::locktoken", XMLWriter.CLOSING);

        generatedXML.writeElement("DAV::activelock", XMLWriter.CLOSING);
        generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.CLOSING);
        generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);

        resp.addHeader("Lock-Token", "<opaquelocktoken:" + lockToken + ">");

        generatedXML.sendData();

    }

    /**
     * Executes the lock for a Mac OS Finder client
     */
    private void doMacLockRequestWorkaround(ITransaction transaction,
            HttpServletRequest req, HttpServletResponse resp)
            throws LockFailedException, IOException {
        LockedObject lo;
        int depth = getDepth(req);
        int lockDuration = getTimeout(transaction, req);
        if (lockDuration < 0 || lockDuration > MAX_TIMEOUT)
            lockDuration = DEFAULT_TIMEOUT;

        boolean lockSuccess = false;
        lockSuccess = _resourceLocks.exclusiveLock(transaction, _path,
                _lockOwner, depth, lockDuration);

        if (lockSuccess) {
            // Locks successfully placed - return information about
            lo = _resourceLocks.getLockedObjectByPath(transaction, _path);
            if (lo != null) {
                generateXMLReport(transaction, resp, lo);
            } else {
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            // Locking was not successful
            sendLockFailError(transaction, req, resp);
        }
    }

    /**
     * Sends an error report to the client
     */
    private void sendLockFailError(ITransaction transaction,
            HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Hashtable<String, Integer> errorList = new Hashtable<String, Integer>();
        errorList.put(_path, WebDavStatus.SC_LOCKED);
        sendReport(req, resp, errorList);
    }

}
