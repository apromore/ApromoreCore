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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apromore.filestore.webdav.IMimeTyper;
import org.apromore.filestore.webdav.ITransaction;
import org.apromore.filestore.webdav.IWebDavStore;
import org.apromore.filestore.webdav.StoredObject;
import org.apromore.filestore.webdav.WebDavStatus;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoGet extends DoHead {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoCopy.class.getName());

    public DoGet(IWebDavStore store, String dftIndexFile, String insteadOf404, ResourceLocks resourceLocks, IMimeTyper mimeTyper, int contentLengthHeader) {
        super(store, dftIndexFile, insteadOf404, resourceLocks, mimeTyper, contentLengthHeader);

    }

    @Override
    protected void doBody(ITransaction transaction, HttpServletResponse resp, String path) {

        try {
            StoredObject so = _store.getStoredObject(transaction, path);
            if (so.isNullResource()) {
                String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.sendError(WebDavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }
            OutputStream out = resp.getOutputStream();
            InputStream in = _store.getResourceContent(transaction, path);
            try {
                int read = -1;
                byte[] copyBuffer = new byte[BUF_SIZE];

                while ((read = in.read(copyBuffer, 0, copyBuffer.length)) != -1) {
                    out.write(copyBuffer, 0, read);
                }
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                    LOGGER.warn("Closing InputStream causes Exception!\n" + e.toString());
                }
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    LOGGER.warn("Flushing OutputStream causes Exception!\n" + e.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.trace(e.toString());
        }
    }

    @Override
    protected void folderBody(ITransaction transaction, String path, HttpServletResponse resp, HttpServletRequest req) throws IOException {
        StoredObject so = _store.getStoredObject(transaction, path);
        if (so == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, req.getRequestURI());
        } else {
            if (so.isNullResource()) {
                String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
                resp.addHeader("Allow", methodsAllowed);
                resp.sendError(WebDavStatus.SC_METHOD_NOT_ALLOWED);
                return;
            }

            if (so.isFolder()) {
                DateFormat shortDF = getDateTimeFormat(req.getLocale());
                resp.setContentType("text/html");
                resp.setCharacterEncoding("UTF8");
                OutputStream out = resp.getOutputStream();
                String[] children = _store.getChildrenNames(transaction, path);
                children = children == null ? new String[]{} : children;
                StringBuilder childrenTemp = new StringBuilder();
                childrenTemp.append("<html><head><title>Content of folder");
                childrenTemp.append(path);
                childrenTemp.append("</title><style type=\"text/css\">");
                childrenTemp.append(getCSS());
                childrenTemp.append("</style></head>");
                childrenTemp.append("<body>");
                childrenTemp.append(getHeader(transaction, path, resp, req));
                childrenTemp.append("<table>");
                childrenTemp.append("<tr><th>Name</th><th>Size</th><th>Created</th><th>Modified</th></tr>");
                childrenTemp.append("<tr>");
                childrenTemp.append("<td colspan=\"4\"><a href=\"../\">Parent</a></td></tr>");
                boolean isEven = false;
                for (String child : children) {
                    isEven = !isEven;
                    childrenTemp.append("<tr class=\"");
                    childrenTemp.append(isEven ? "even" : "odd");
                    childrenTemp.append("\">");
                    childrenTemp.append("<td>");
                    childrenTemp.append("<a href=\"");
                    childrenTemp.append(child);
                    StoredObject obj = _store.getStoredObject(transaction, path + "/" + child);
                    if (obj.isFolder()) {
                        childrenTemp.append("/");
                    }
                    childrenTemp.append("\">");
                    childrenTemp.append(child);
                    childrenTemp.append("</a></td>");
                    if (obj.isFolder()) {
                        childrenTemp.append("<td>Folder</td>");
                    } else {
                        childrenTemp.append("<td>");
                        childrenTemp.append(obj.getResourceLength());
                        childrenTemp.append(" Bytes</td>");
                    }
                    if (obj.getCreationDate() != null) {
                        childrenTemp.append("<td>");
                        childrenTemp.append(shortDF.format(obj.getCreationDate()));
                        childrenTemp.append("</td>");
                    } else {
                        childrenTemp.append("<td></td>");
                    }
                    if (obj.getLastModified() != null) {
                        childrenTemp.append("<td>");
                        childrenTemp.append(shortDF.format(obj.getLastModified()));
                        childrenTemp.append("</td>");
                    } else {
                        childrenTemp.append("<td></td>");
                    }
                    childrenTemp.append("</tr>");
                }
                childrenTemp.append("</table>");
                childrenTemp.append(getFooter(transaction, path, resp, req));
                childrenTemp.append("</body></html>");
                out.write(childrenTemp.toString().getBytes("UTF-8"));
            }
        }
    }

    /**
     * Return the CSS styles used to display the HTML representation
     * of the webdav content.
     *
     * @return String returning the CSS style sheet used to display result in html format
     */
    protected String getCSS() {
        // The default styles to use
        String retVal = "body {\n" +
                "	font-family: Arial, Helvetica, sans-serif;\n" +
                "}\n" +
                "h1 {\n" +
                "	font-size: 1.5em;\n" +
                "}\n" +
                "th {\n" +
                "	background-color: #9DACBF;\n" +
                "}\n" +
                "table {\n" +
                "	border-top-style: solid;\n" +
                "	border-right-style: solid;\n" +
                "	border-bottom-style: solid;\n" +
                "	border-left-style: solid;\n" +
                "}\n" +
                "td {\n" +
                "	margin: 0px;\n" +
                "	padding-top: 2px;\n" +
                "	padding-right: 5px;\n" +
                "	padding-bottom: 2px;\n" +
                "	padding-left: 5px;\n" +
                "}\n" +
                "tr.even {\n" +
                "	background-color: #CCCCCC;\n" +
                "}\n" +
                "tr.odd {\n" +
                "	background-color: #FFFFFF;\n" +
                "}\n" +
                "";
        try {
            // Try loading one via class loader and use that one instead
            ClassLoader cl = getClass().getClassLoader();
            InputStream iStream = cl.getResourceAsStream("webdav.css");
            if (iStream != null) {
                // Found css via class loader, use that one
                StringBuilder out = new StringBuilder();
                byte[] b = new byte[4096];
                for (int n; (n = iStream.read(b)) != -1; ) {
                    out.append(new String(b, 0, n));
                }
                retVal = out.toString();
            }
        } catch (Exception ex) {
            LOGGER.error("Error in reading webdav.css", ex);
        }

        return retVal;
    }

    /**
     * Return this as the Date/Time format for displaying Creation + Modification dates
     *
     * @param browserLocale
     * @return DateFormat used to display creation and modification dates
     */
    protected DateFormat getDateTimeFormat(Locale browserLocale) {
        return SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.SHORT, SimpleDateFormat.MEDIUM, browserLocale);
    }

    /**
     * Return the header to be displayed in front of the folder content
     *
     * @param transaction
     * @param path
     * @param resp
     * @param req
     * @return String representing the header to be display in front of the folder content
     */
    protected String getHeader(ITransaction transaction, String path,
                               HttpServletResponse resp, HttpServletRequest req) {
        return "<h1>Content of folder " + path + "</h1>";
    }

    /**
     * Return the footer to be displayed after the folder content
     *
     * @param transaction
     * @param path
     * @param resp
     * @param req
     * @return String representing the footer to be displayed after the folder content
     */
    protected String getFooter(ITransaction transaction, String path,
                               HttpServletResponse resp, HttpServletRequest req) {
        return "";
    }
}
