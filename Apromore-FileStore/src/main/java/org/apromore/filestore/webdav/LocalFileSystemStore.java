/*
 * Copyright © 2009-2016 The Apromore Initiative.
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apromore.filestore.webdav.exceptions.WebDavException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reference Implementation of WebDavStore
 * 
 * @author joa
 * @author re
 */
public class LocalFileSystemStore implements IWebDavStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDavServletBean.class.getName());
    private static final int BUF_SIZE = 65536;

    private File _root = null;


    public LocalFileSystemStore(File root) {
        _root = root;
    }


    public ITransaction begin(Principal principal) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.begin()");
        if (!_root.exists()) {
            if (!_root.mkdirs()) {
                throw new WebDavException("root path: " + _root.getAbsolutePath() + " does not exist and could not be created");
            }
        }
        return null;
    }

    public void checkAuthentication(ITransaction transaction)
            throws SecurityException {
        LOGGER.trace("LocalFileSystemStore.checkAuthentication()");
    }

    public void commit(ITransaction transaction) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.commit()");
    }

    public void rollback(ITransaction transaction) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.rollback()");
    }

    public void createFolder(ITransaction transaction, String uri) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.createFolder(" + uri + ")");
        File file = new File(_root, uri);
        if (!file.mkdir())
            throw new WebDavException("cannot create folder: " + uri);
    }

    public void createResource(ITransaction transaction, String uri)
            throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.createResource(" + uri + ")");
        File file = new File(_root, uri);
        try {
            if (!file.createNewFile()) {
                throw new WebDavException("cannot create file: " + uri);
            }
        } catch (IOException e) {
            LOGGER.error("LocalFileSystemStore.createResource(" + uri + ") failed");
            throw new WebDavException(e);
        }
    }

    public long setResourceContent(ITransaction transaction, String uri, InputStream is, String contentType, String characterEncoding)
            throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.setResourceContent(" + uri + ")");
        File file = new File(_root, uri);
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file), BUF_SIZE);
            try {
                int read;
                byte[] copyBuffer = new byte[BUF_SIZE];

                while ((read = is.read(copyBuffer, 0, copyBuffer.length)) != -1) {
                    os.write(copyBuffer, 0, read);
                }
            } finally {
                try {
                    is.close();
                } finally {
                    os.close();
                }
            }
        } catch (IOException e) {
            LOGGER.error("LocalFileSystemStore.setResourceContent(" + uri + ") failed");
            throw new WebDavException(e);
        }
        long length = -1;

        try {
            length = file.length();
        } catch (SecurityException e) {
            LOGGER.error("LocalFileSystemStore.setResourceContent(" + uri + ") failed" + "\nCan't get file.length");
        }

        return length;
    }

    public String[] getChildrenNames(ITransaction transaction, String uri) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.getChildrenNames(" + uri + ")");
        File file = new File(_root, uri);
        String[] childrenNames = null;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            List<String> childList = new ArrayList<>();
            String name;
            assert children != null;
            for (int i = 0; i < children.length; i++) {
                name = children[i].getName();
                childList.add(name);
                LOGGER.trace("Child " + i + ": " + name);
            }
            childrenNames = new String[childList.size()];
            childrenNames = childList.toArray(childrenNames);
        }
        return childrenNames;
    }

    public void removeObject(ITransaction transaction, String uri) throws WebDavException {
        File file = new File(_root, uri);
        boolean success = file.delete();
        LOGGER.trace("LocalFileSystemStore.removeObject(" + uri + ")=" + success);
        if (!success) {
            throw new WebDavException("cannot delete object: " + uri);
        }

    }

    public InputStream getResourceContent(ITransaction transaction, String uri) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.getResourceContent(" + uri + ")");
        File file = new File(_root, uri);

        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
            LOGGER.error("LocalFileSystemStore.getResourceContent(" + uri + ") failed");
            throw new WebDavException(e);
        }
        return in;
    }

    public long getResourceLength(ITransaction transaction, String uri) throws WebDavException {
        LOGGER.trace("LocalFileSystemStore.getResourceLength(" + uri + ")");
        File file = new File(_root, uri);
        return file.length();
    }

    public StoredObject getStoredObject(ITransaction transaction, String uri) {
        StoredObject so = null;

        File file = new File(_root, uri);
        if (file.exists()) {
            so = new StoredObject();
            so.setFolder(file.isDirectory());
            so.setLastModified(new Date(file.lastModified()));
            so.setCreationDate(new Date(file.lastModified()));
            so.setResourceLength(getResourceLength(transaction, uri));
        }

        return so;
    }

}
