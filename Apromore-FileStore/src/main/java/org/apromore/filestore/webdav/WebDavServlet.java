package org.apromore.filestore.webdav;

import javax.servlet.ServletException;
import java.io.File;
import java.lang.reflect.Constructor;

import org.apromore.filestore.webdav.exceptions.WebDavException;

/**
 * Servlet which provides support for WebDAV level 2.
 * 
 * the original class is org.apache.catalina.servlets.WebdavServlet by Remy
 * Maucherat, which was heavily changed
 * 
 * @author Remy Maucherat
 */
public class WebDavServlet extends WebDavServletBean {

    private static final String ROOTPATH_PARAMETER = "rootpath";

    @Override
    public void init() throws ServletException {
        // Parameters from webdav.xml
        String clazzName = getServletConfig().getInitParameter("ResourceHandlerImplementation");
        if (clazzName == null || clazzName.equals("")) {
            clazzName = LocalFileSystemStore.class.getName();
        }

        File root = getFileRoot();
        IWebDavStore webdavStore = constructStore(clazzName, root);

        boolean lazyFolderCreationOnPut = getInitParameter("lazyFolderCreationOnPut") != null && getInitParameter("lazyFolderCreationOnPut").equals("1");

        String dftIndexFile = getInitParameter("default-index-file");
        String insteadOf404 = getInitParameter("instead-of-404");

        int noContentLengthHeader = getIntInitParameter("no-content-length-headers");

        super.init(webdavStore, dftIndexFile, insteadOf404, noContentLengthHeader, lazyFolderCreationOnPut);
    }

    private int getIntInitParameter(String key) {
        return getInitParameter(key) == null ? -1 : Integer.parseInt(getInitParameter(key));
    }

    protected IWebDavStore constructStore(String clazzName, File root) {
        IWebDavStore webdavStore;
        try {
            Class<?> clazz = WebDavServlet.class.getClassLoader().loadClass(clazzName);
            Constructor<?> ctor = clazz.getConstructor(new Class[]{File.class});
            webdavStore = (IWebDavStore) ctor.newInstance(root);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("some problem making store component", e);
        }
        return webdavStore;
    }

    private File getFileRoot() {
        String rootPath = getInitParameter(ROOTPATH_PARAMETER);
        if (rootPath == null) {
            throw new WebDavException("missing parameter: " + ROOTPATH_PARAMETER);
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
                throw new WebDavException(
                        "Could not determine root of war file. Can't extract from path '" + file + "' for this webdav container");
            }
        }
        return new File(rootPath);
    }

}
