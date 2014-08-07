/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.filestore.webdav;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;

import org.apromore.filestore.webdav.exceptions.UnauthenticatedException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.apromore.filestore.webdav.fromcatalina.MD5Encoder;
import org.apromore.filestore.webdav.locking.ResourceLocks;
import org.apromore.filestore.webdav.methods.DoCopy;
import org.apromore.filestore.webdav.methods.DoDelete;
import org.apromore.filestore.webdav.methods.DoGet;
import org.apromore.filestore.webdav.methods.DoHead;
import org.apromore.filestore.webdav.methods.DoLock;
import org.apromore.filestore.webdav.methods.DoMkcol;
import org.apromore.filestore.webdav.methods.DoMove;
import org.apromore.filestore.webdav.methods.DoNotImplemented;
import org.apromore.filestore.webdav.methods.DoOptions;
import org.apromore.filestore.webdav.methods.DoPropfind;
import org.apromore.filestore.webdav.methods.DoProppatch;
import org.apromore.filestore.webdav.methods.DoPut;
import org.apromore.filestore.webdav.methods.DoUnlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDavServletBean extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDavServletBean.class.getName());

    protected static MessageDigest MD5_HELPER;
    protected static final MD5Encoder MD5_ENCODER = new MD5Encoder();

    private static final boolean READ_ONLY = false;
    private ResourceLocks _resLocks;
    private IWebDavStore _store;
    private HashMap<String, IMethodExecutor> _methodMap = new HashMap<>();

    public WebDavServletBean() {
        _resLocks = new ResourceLocks();

        try {
            MD5_HELPER = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    public void init(IWebDavStore store, String dftIndexFile, String insteadOf404, int nocontentLenghHeaders,
            boolean lazyFolderCreationOnPut) throws ServletException {
        _store = store;
        IMimeTyper mimeTyper = new IMimeTyper() {
            public String getMimeType(String path) {
                return getServletContext().getMimeType(path);
            }
        };

        register("GET", new DoGet(store, dftIndexFile, insteadOf404, _resLocks, mimeTyper, nocontentLenghHeaders));
        register("HEAD", new DoHead(store, dftIndexFile, insteadOf404, _resLocks, mimeTyper, nocontentLenghHeaders));
        DoDelete doDelete = (DoDelete) register("DELETE", new DoDelete(store, _resLocks, READ_ONLY));
        DoCopy doCopy = (DoCopy) register("COPY", new DoCopy(store, _resLocks, doDelete, READ_ONLY));
        register("LOCK", new DoLock(store, _resLocks, READ_ONLY));
        register("UNLOCK", new DoUnlock(store, _resLocks, READ_ONLY));
        register("MOVE", new DoMove(_resLocks, doDelete, doCopy, READ_ONLY));
        register("MKCOL", new DoMkcol(store, _resLocks, READ_ONLY));
        register("OPTIONS", new DoOptions(store, _resLocks));
        register("PUT", new DoPut(store, _resLocks, READ_ONLY, lazyFolderCreationOnPut));
        register("PROPFIND", new DoPropfind(store, _resLocks, mimeTyper));
        register("PROPPATCH", new DoProppatch(store, _resLocks, READ_ONLY));
        register("*NO*IMPL*", new DoNotImplemented(READ_ONLY));
    }

    private IMethodExecutor register(String methodName, IMethodExecutor method) {
        _methodMap.put(methodName, method);
        return method;
    }

    /**
     * Handles the special WebDAV methods.
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String methodName = req.getMethod();
        ITransaction transaction = null;
        boolean needRollback = false;

        if (LOGGER.isTraceEnabled()) {
            debugRequest(methodName, req);
        }

        try {
            Principal userPrincipal = req.getUserPrincipal();
            transaction = _store.begin(userPrincipal);
            needRollback = true;
            _store.checkAuthentication(transaction);
            resp.setStatus(WebDavStatus.SC_OK);

            try {
                IMethodExecutor methodExecutor = _methodMap.get(methodName);
                if (methodExecutor == null) {
                    methodExecutor = _methodMap.get("*NO*IMPL*");
                }

                methodExecutor.execute(transaction, req, resp);
                _store.commit(transaction);
                needRollback = false;

            } catch (IOException e) {
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                e.printStackTrace(pw);
                LOGGER.error("IOException: " + sw.toString());
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                _store.rollback(transaction);
                throw new ServletException(e);
            }

        } catch (UnauthenticatedException e) {
            resp.sendError(WebDavStatus.SC_FORBIDDEN);
        } catch (WebDavException e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            LOGGER.error("WebDavException: " + sw.toString());
            throw new ServletException(e);
        } catch (Exception e) {
            java.io.StringWriter sw = new java.io.StringWriter();
            java.io.PrintWriter pw = new java.io.PrintWriter(sw);
            e.printStackTrace(pw);
            LOGGER.error("Exception: " + sw.toString());
        } finally {
            if (needRollback)
                _store.rollback(transaction);
        }

    }

    private void debugRequest(String methodName, HttpServletRequest req) {
        LOGGER.trace("-----------");
        LOGGER.trace("WebDavServlet\n request: methodName = " + methodName);
        LOGGER.trace("time: " + System.currentTimeMillis());
        LOGGER.trace("path: " + req.getRequestURI());
        LOGGER.trace("-----------");
        Enumeration<?> e = req.getHeaderNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            LOGGER.trace("header: " + s + " " + req.getHeader(s));
        }
        e = req.getAttributeNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            LOGGER.trace("attribute: " + s + " " + req.getAttribute(s));
        }
        e = req.getParameterNames();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            LOGGER.trace("parameter: " + s + " " + req.getParameter(s));
        }
    }

}
