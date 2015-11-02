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

package org.apromore.filestore.webdav;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.apromore.filestore.ConfigBean;
import org.apromore.filestore.webdav.exceptions.UnauthenticatedException;
import org.apromore.filestore.webdav.exceptions.WebDavException;
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
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * A Spring defined Request Handler. This "Servlet" is handles by Spring.
 *
 * @author Cameron James
 */
@Component
public class WebDavSpringServlet extends HttpServletBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDavSpringServlet.class.getName());
    private static final boolean READ_ONLY = false;

    protected static MessageDigest MD5_HELPER;
    private ResourceLocks resLocks;
    private IWebDavStore store;
    private HashMap<String, IMethodExecutor> methodMap = new HashMap<>();

    private String rootPath;
    private String servletPath;
    private String resourceHandlerImplementation;
    private boolean lazyFolderCreationOnPut;
    private String defaultIndexFile;
    private String insteadOf404;
    private int noContentLengthHeaders;

    private ConfigBean filestoreConfig;

    /**
     * Default Constructor for this Servlet.
     */
    public WebDavSpringServlet() {
        resLocks = new ResourceLocks();

        try {
            MD5_HELPER = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException();
        }
    }

    /**
     * @see org.springframework.web.servlet.HttpServletBean#initServletBean()
     * {@inheritDoc}
     */
    @Override
    public void initServletBean() throws ServletException {
        ConfigBean filestoreConfig = (ConfigBean) WebApplicationContextUtils.getWebApplicationContext(getServletContext())
                                                                            .getAutowireCapableBeanFactory()
                                                                            .getBean("filestoreConfig");

        resourceHandlerImplementation = getInitParameter("ResourceHandlerImplementation");
        if (resourceHandlerImplementation == null || resourceHandlerImplementation.equals("")) {
            resourceHandlerImplementation = LocalFileSystemStore.class.getName();
        }

        File root = getFileRoot();
        servletPath = getInitParameter("servletPath").replace("${site.filestore}", filestoreConfig.getSiteFilestore());
        store = constructStore(resourceHandlerImplementation, root);

        lazyFolderCreationOnPut = getInitParameter("lazyFolderCreationOnPut") != null && getInitParameter("lazyFolderCreationOnPut").equals("1");
        defaultIndexFile = getInitParameter("default-index-file");
        insteadOf404 = getInitParameter("instead-of-404");
        noContentLengthHeaders = getIntInitParameter("no-content-length-headers");

        extraInit();
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
            transaction = store.begin(userPrincipal);
            needRollback = true;
            store.checkAuthentication(transaction);
            resp.setStatus(WebDavStatus.SC_OK);

            try {
                IMethodExecutor methodExecutor = methodMap.get(methodName);
                if (methodExecutor == null) {
                    methodExecutor = methodMap.get("*NO*IMPL*");
                }

                methodExecutor.execute(transaction, req, resp);
                store.commit(transaction);
                needRollback = false;

            } catch (IOException e) {
                java.io.StringWriter sw = new java.io.StringWriter();
                java.io.PrintWriter pw = new java.io.PrintWriter(sw);
                e.printStackTrace(pw);
                LOGGER.error("IOException: " + sw.toString());
                resp.sendError(WebDavStatus.SC_INTERNAL_SERVER_ERROR);
                store.rollback(transaction);
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
            if (needRollback) {
                store.rollback(transaction);
            }
        }

    }


    /* Setup the Init Params that are Integers. */
    private int getIntInitParameter(String key) {
        return getInitParameter(key) == null ? -1 : Integer.parseInt(getInitParameter(key));
    }

    /* Construct the FileStore used by the webdav system. */
    private IWebDavStore constructStore(String clazzName, File root) {
        IWebDavStore webDavStore;
        try {
            Class<?> clazz = WebDavServlet.class.getClassLoader().loadClass(clazzName);
            Constructor<?> ctor = clazz.getConstructor(new Class[]{File.class});
            webDavStore = (IWebDavStore) ctor.newInstance(root);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("some problem making store component", e);
        }
        return webDavStore;
    }

    /* Used to get a handle on where the file system root can be found. */
    private File getFileRoot() {
        rootPath = getInitParameter("rootpath");
        if (rootPath == null) {
            throw new WebDavException("The root path hasn't been initialized");
        }
        if (rootPath.equals("*WAR-FILE-ROOT*")) {
            String file = LocalFileSystemStore.class.getProtectionDomain().getCodeSource().getLocation().getFile().replace('\\', '/');
            if (file.charAt(0) == '/' && System.getProperty("os.name").contains("Windows")) {
                file = file.substring(1, file.length());
            }

            int ix = file.indexOf("/WEB-INF/");
            if (ix != -1) {
                rootPath = file.substring(0, ix).replace('/', File.separatorChar);
            } else {
                throw new WebDavException("Could not determine root of war file. Can't extract from path '" + file + "' for this webdav container");
            }
        } else if (rootPath.equals("*APROMORE-CONFIG*")) {
            try {
                rootPath = filestoreConfig.getFilestoreDir();
            } catch (Exception e) {
                throw new WebDavException("Unable to initialize rootpath using *APROMORE-CONFIG*", e);
            }
        }
        return new File(rootPath);
    }


    /* Extra initialization for the servlet.  */
    private void extraInit() throws ServletException {
        IMimeTyper mimeTyper = new IMimeTyper() {
            public String getMimeType(String path) {
                return getServletContext().getMimeType(path);
            }
        };

        register("GET", new DoGet(store, defaultIndexFile, insteadOf404, resLocks, mimeTyper, noContentLengthHeaders));
        register("HEAD", new DoHead(store, defaultIndexFile, insteadOf404, resLocks, mimeTyper, noContentLengthHeaders));
        DoDelete doDelete = (DoDelete) register("DELETE", new DoDelete(store, resLocks, READ_ONLY));
        DoCopy doCopy = (DoCopy) register("COPY", new DoCopy(store, resLocks, doDelete, READ_ONLY));
        register("LOCK", new DoLock(store, resLocks, READ_ONLY));
        register("UNLOCK", new DoUnlock(store, resLocks, READ_ONLY));
        register("MOVE", new DoMove(resLocks, doDelete, doCopy, READ_ONLY));
        register("MKCOL", new DoMkcol(store, resLocks, READ_ONLY));
        register("OPTIONS", new DoOptions(store, resLocks));
        register("PUT", new DoPut(store, resLocks, READ_ONLY, lazyFolderCreationOnPut));
        register("PROPFIND", new DoPropfind(store, resLocks, mimeTyper));
        register("PROPPATCH", new DoProppatch(store, resLocks, READ_ONLY));
        register("*NO*IMPL*", new DoNotImplemented(READ_ONLY));
    }

    /* Used to register Methods that might be passed in with the request. */
    private IMethodExecutor register(String methodName, IMethodExecutor method) {
        methodMap.put(methodName, method);
        return method;
    }

    /* Writes to the console the debug information for the request. */
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



    public IWebDavStore getStore() {
        return store;
    }
}
