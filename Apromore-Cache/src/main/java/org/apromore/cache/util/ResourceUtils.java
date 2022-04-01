/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.cache.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceUtils {

    /**
     * Resource path prefix that specifies to load from a classpath location, value is <b>{@code classpath:}</b>
     */
    public static final String CLASSPATH_PREFIX = "classpath:";
    /**
     * Resource path prefix that specifies to load from a url location, value is <b>{@code url:}</b>
     */
    public static final String URL_PREFIX = "url:";
    /**
     * Resource path prefix that specifies to load from a file location, value is <b>{@code file:}</b>
     */
    public static final String FILE_PREFIX = "file:";

    /**
     * Private internal log instance.
     */
    private static final Logger log = LoggerFactory.getLogger(ResourceUtils.class);



    /**
     * Prevent instantiation.
     */
    private ResourceUtils() {
    }

    /**
     * Returns {@code true} if the resource path is not null and starts with one of the recognized
     * resource prefixes ({@link #CLASSPATH_PREFIX CLASSPATH_PREFIX},
     * {@link #URL_PREFIX URL_PREFIX}, or {@link #FILE_PREFIX FILE_PREFIX}), {@code false} otherwise.
     *
     * @param resourcePath the resource path to check
     * @return {@code true} if the resource path is not null and starts with one of the recognized
     *         resource prefixes, {@code false} otherwise.
     * @since 7.10
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public static boolean hasResourcePrefix(String resourcePath) {
        return resourcePath != null &&
                (resourcePath.startsWith(CLASSPATH_PREFIX) ||
                        resourcePath.startsWith(URL_PREFIX) ||
                        resourcePath.startsWith(FILE_PREFIX));
    }

    /**
     * Returns {@code true} if the resource at the specified path exists, {@code false} otherwise.  This
     * method supports scheme prefixes on the path as defined in {@link #getInputStreamForPath(String)}.
     *
     * @param resourcePath the path of the resource to check.
     * @return {@code true} if the resource at the specified path exists, {@code false} otherwise.
     * @since 7.10
     */
    public static boolean resourceExists(String resourcePath) {
        InputStream stream = null;
        boolean exists = false;

        try {
            stream = getInputStreamForPath(resourcePath);
            exists = true;
        } catch (IOException e) {
            stream = null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ignored) {
                }
            }
        }

        return exists;
    }


    /**
     * Returns the InputStream for the resource represented by the specified path, supporting scheme
     * prefixes that direct how to acquire the input stream
     * ({@link #CLASSPATH_PREFIX CLASSPATH_PREFIX},
     * {@link #URL_PREFIX URL_PREFIX}, or {@link #FILE_PREFIX FILE_PREFIX}).  If the path is not prefixed by one
     * of these schemes, the path is assumed to be a file-based path that can be loaded with a
     * {@link FileInputStream FileInputStream}.
     *
     * @param resourcePath the String path representing the resource to obtain.
     * @return the InputStream for the specified resource.
     * @throws IOException if there is a problem acquiring the resource at the specified path.
     */
    public static InputStream getInputStreamForPath(String resourcePath) throws IOException {

        InputStream is;
        if (resourcePath.startsWith(CLASSPATH_PREFIX)) {
            is = loadFromClassPath(stripPrefix(resourcePath));

        } else if (resourcePath.startsWith(URL_PREFIX)) {
            is = loadFromUrl(stripPrefix(resourcePath));

        } else if (resourcePath.startsWith(FILE_PREFIX)) {
            is = loadFromFile(stripPrefix(resourcePath));

        } else {
            is = loadFromFile(resourcePath);
        }

        if (is == null) {
            throw new IOException("Resource [" + resourcePath + "] could not be found.");
        }

        return is;
    }

    private static InputStream loadFromFile(String path) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Opening file [" + path + "]...");
        }
        return new FileInputStream(path);
    }

    private static InputStream loadFromUrl(String urlPath) throws IOException {
        log.debug("Opening url {}", urlPath);
        URL url = new URL(urlPath);
        return url.openStream();
    }

    private static InputStream loadFromClassPath(String path) {
        log.debug("Opening resource from class path [{}]", path);
        return ClassUtils.getResourceAsStream(path);
    }

    private static String stripPrefix(String resourcePath) {
        return resourcePath.substring(resourcePath.indexOf(":") + 1);
    }

    /**
     * Convenience method that closes the specified {@link InputStream InputStream}, logging any
     * {@link IOException IOException} that might occur. If the {@code InputStream}
     * argument is {@code null}, this method does nothing.  It returns quietly in all cases.
     *
     * @param is the {@code InputStream} to close, logging any {@code IOException} that might occur.
     */
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                log.warn("Error closing input stream.", e);
            }
        }
    }
}
