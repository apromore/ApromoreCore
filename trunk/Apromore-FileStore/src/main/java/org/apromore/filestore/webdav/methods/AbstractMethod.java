package org.apromore.filestore.webdav.methods;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apromore.filestore.webdav.IMethodExecutor;
import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.exceptions.LockFailedException;
import org.apromore.filestore.webdav.fromcatalina.URLEncoder;
import org.apromore.filestore.webdav.fromcatalina.XMLWriter;
import org.apromore.filestore.webdav.locking.IResourceLocks;
import org.apromore.filestore.webdav.locking.LockedObject;

public abstract class AbstractMethod implements IMethodExecutor {

    /**
     * Array containing the safe characters set.
     */
    protected static URLEncoder URL_ENCODER;

    /**
     * Default depth is infite.
     */
    protected static final int INFINITY = 3;

    /**
     * Simple date format for the creation date ISO 8601 representation
     * (partial).
     */
    protected static final SimpleDateFormat CREATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Simple date format for the last modified date. (RFC 822 updated by RFC
     * 1123)
     */
    protected static final SimpleDateFormat LAST_MODIFIED_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    static {
        CREATION_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
        LAST_MODIFIED_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

        URL_ENCODER = new URLEncoder();
        URL_ENCODER.addSafeCharacter('-');
        URL_ENCODER.addSafeCharacter('_');
        URL_ENCODER.addSafeCharacter('.');
        URL_ENCODER.addSafeCharacter('*');
        URL_ENCODER.addSafeCharacter('/');
    }

    /**
     * size of the io-buffer
     */
    protected static int BUF_SIZE = 65536;

    /**
     * Default lock timeout value.
     */
    protected static final int DEFAULT_TIMEOUT = 3600;

    /**
     * Maximum lock timeout.
     */
    protected static final int MAX_TIMEOUT = 604800;

    /**
     * Boolean value to temporary lock resources (for method locks)
     */
    protected static final boolean TEMPORARY = true;

    /**
     * Timeout for temporary locks
     */
    protected static final int TEMP_TIMEOUT = 10;

    /**
     * Return the relative path associated with this servlet.
     * 
     * @param request The servlet request we are processing
     */
    protected String getRelativePath(HttpServletRequest request) {
        if (request.getAttribute("javax.servlet.include.request_uri") != null) {
            String result = (String) request.getAttribute("javax.servlet.include.path_info");
            if ((result == null) || (result.equals(""))) {
                result = "/";
            }
            return (result);
        }

        // No, extract the desired path directly from the request
        String result = request.getPathInfo();
        if ((result == null) || (result.equals(""))) {
            result = "/";
        }
        return (result);

    }

    /**
     * creates the parent path from the given path by removing the last '/' and
     * everything after that
     * 
     * @param path the path
     * @return parent path
     */
    protected String getParentPath(String path) {
        int slash = path.lastIndexOf('/');
        if (slash != -1) {
            return path.substring(0, slash);
        }
        return null;
    }

    /**
     * removes a / at the end of the path string, if present
     * 
     * @param path the path
     * @return the path without trailing /
     */
    protected String getCleanPath(String path) {
        if (path.endsWith("/") && path.length() > 1) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    /**
     * Return JAXP document builder instance.
     */
    protected DocumentBuilder getDocumentBuilder() throws ServletException {
        DocumentBuilder documentBuilder;
        DocumentBuilderFactory documentBuilderFactory;
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServletException("jaxp failed");
        }
        return documentBuilder;
    }

    /**
     * reads the depth header from the request and returns it as a int
     * 
     * @param req the request
     * @return the depth from the depth header
     */
    protected int getDepth(HttpServletRequest req) {
        int depth = INFINITY;
        String depthStr = req.getHeader("Depth");
        if (depthStr != null) {
            if (depthStr.equals("0")) {
                depth = 0;
            } else if (depthStr.equals("1")) {
                depth = 1;
            }
        }
        return depth;
    }

    /**
     * URL rewriter.
     * 
     * @param path  Path which has to be rewiten
     * @return the rewritten path
     */
    protected String rewriteUrl(String path) {
        return URL_ENCODER.encode(path);
    }

    /**
     * Get the ETag associated with a file.
     * 
     * @param so  StoredObject to get resourceLength, lastModified and a hashCode of StoredObject
     * @return the ETag
     */
    protected String getETag(StoredObject so) {
        String resourceLength = "";
        String lastModified = "";

        if (so != null && so.isResource()) {
            resourceLength = Long.toString(so.getResourceLength());
            lastModified = Long.toString(so.getLastModified().getTime());
        }

        return "W/\"" + resourceLength + "-" + lastModified + "\"";

    }

    protected String[] getLockIdFromIfHeader(HttpServletRequest req) {
        String[] ids = new String[2];
        String id = req.getHeader("If");

        if (id != null && !id.equals("")) {
            if (id.indexOf(">)") == id.lastIndexOf(">)")) {
                id = id.substring(id.indexOf("(<"), id.indexOf(">)"));
                if (id.contains("locktoken:")) {
                    id = id.substring(id.indexOf(':') + 1);
                }
                ids[0] = id;
            } else {
                String firstId = id.substring(id.indexOf("(<"), id.indexOf(">)"));
                if (firstId.contains("locktoken:")) {
                    firstId = firstId.substring(firstId.indexOf(':') + 1);
                }
                ids[0] = firstId;

                String secondId = id.substring(id.lastIndexOf("(<"), id.lastIndexOf(">)"));
                if (secondId.contains("locktoken:")) {
                    secondId = secondId.substring(secondId.indexOf(':') + 1);
                }
                ids[1] = secondId;
            }
        } else {
            ids = null;
        }
        return ids;
    }

    protected String getLockIdFromLockTokenHeader(HttpServletRequest req) {
        String id = req.getHeader("Lock-Token");

        if (id != null) {
            id = id.substring(id.indexOf(":") + 1, id.indexOf(">"));

        }

        return id;
    }

    /**
     * Checks if locks on resources at the given path exists and if so checks
     * the If-Header to make sure the If-Header corresponds to the locked
     * resource. Returning true if no lock exists or the If-Header is
     * corresponding to the locked resource
     * 
     * @param req Servlet request
     * @param resp Servlet response
     * @param resourceLocks the resource locks
     * @param path path to the resource
     * @return true if no lock on a resource with the given path exists or if the If-Header corresponds to the locked resource
     * @throws java.io.IOException
     * @throws LockFailedException
     */
    protected boolean checkLocks(ITransaction transaction, HttpServletRequest req, HttpServletResponse resp,
            IResourceLocks resourceLocks, String path) throws IOException, LockFailedException {
        LockedObject loByPath = resourceLocks.getLockedObjectByPath(transaction, path);
        if (loByPath != null) {
            if (loByPath.isShared()) {
                return true;
            }

            String[] lockTokens = getLockIdFromIfHeader(req);
            String lockToken;
            if (lockTokens != null) {
                lockToken = lockTokens[0];
            } else {
                return false;
            }
            if (lockToken != null) {
                LockedObject loByIf = resourceLocks.getLockedObjectByID(transaction, lockToken);
                if (loByIf == null) {
                    return false;
                }
                if (!loByIf.equals(loByPath)) {
                    loByIf = null;
                    return false;
                }
                loByIf = null;
            }

        }
        loByPath = null;
        return true;
    }

    /**
     * Send a multistatus element containing a complete error report to the
     * client.
     * 
     * @param req Servlet request
     * @param resp Servlet response
     * @param errorList List of error to be displayed
     */
    protected void sendReport(HttpServletRequest req, HttpServletResponse resp,
            Hashtable<String, Integer> errorList) throws IOException {
        resp.setStatus(WebDavStatus.SC_MULTI_STATUS);
        String absoluteUri = req.getRequestURI();

        HashMap<String, String> namespaces = new HashMap<>();
        namespaces.put("DAV:", "D");

        XMLWriter generatedXML = new XMLWriter(namespaces);
        generatedXML.writeXMLHeader();
        generatedXML.writeElement("DAV::multistatus", XMLWriter.OPENING);

        Enumeration<String> pathList = errorList.keys();
        while (pathList.hasMoreElements()) {
            String errorPath = pathList.nextElement();
            int errorCode = errorList.get(errorPath);

            generatedXML.writeElement("DAV::response", XMLWriter.OPENING);
            generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
            String toAppend = null;
            if (absoluteUri.endsWith(errorPath)) {
                toAppend = absoluteUri;
            } else if (absoluteUri.contains(errorPath)) {
                int endIndex = absoluteUri.indexOf(errorPath) + errorPath.length();
                toAppend = absoluteUri.substring(0, endIndex);
            }
            assert toAppend != null;
            if (!toAppend.startsWith("/") && !toAppend.startsWith("http:")) {
                toAppend = "/" + toAppend;
            }
            generatedXML.writeText(errorPath);
            generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
            generatedXML.writeText("HTTP/1.1 " + errorCode + " " + WebDavStatus.getStatusText(errorCode));
            generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
            generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);

        }

        generatedXML.writeElement("DAV::multistatus", XMLWriter.CLOSING);

        Writer writer = resp.getWriter();
        writer.write(generatedXML.toString());
        writer.close();
    }

}
