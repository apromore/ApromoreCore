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

package org.apromore.filestore.client;

// Java 2 Standard Edition classes
import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

// Third party classes
import com.github.sardine.DavResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test suite for {@link DavFileSystemView}.
 *
 * @author <a href=mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
@Ignore
public class DavFileSystemViewUnitTest {

    private static Logger LOGGER = Logger.getLogger(DavFileSystemViewUnitTest.class.getCanonicalName());

    /**
     * The test object.
     */
    private DavFileSystemView fsView;

    static final Map<QName,String> customProps = new HashMap<>();
    static class TestDavResource extends DavResource {
        public TestDavResource() throws URISyntaxException {
            super(
                "http://localhost:9000/filestore/dav",
                new Date(1000000),  // creation
                new Date(2000000),  // modified
                DavResource.HTTPD_UNIX_DIRECTORY_CONTENT_TYPE,
                0L,  // content length
                "etag",  // etag
                "foo",  // display name
                "en",  // content language
                customProps
            );
        }
    }

    /**
     * Create the test object.
     */
    @Before
    public void createTestObject() throws Exception {

        // Obtain the proxy for the WebDAV repository
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/filestoreClientContext.xml");
        FileStoreService fileStore = (FileStoreService) applicationContext.getAutowireCapableBeanFactory().getBean("fileStoreClientExternal");

        // Create the test object
        fsView = new DavFileSystemView(fileStore);
    }

    /**
     * Test {@link DavFileSystemView#createFileObject(File,String)} method.
     */
    @Test
    public void testCreateFileObject1() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#createFileObject(String)} method.
     */
    @Test
    public void testCreateFileObject2() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#createFileSystemRoot} method.
     */
    @Test
    public void testCreateFileSystemRoot() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#createNewFolder} method.
     */
    @Test
    public void testCreateNewFolder() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#getDefaultDirectory} method.
     */
    @Test
    public void testGetDefaultDirectory() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#getFiles} method.
     */
    @Test
    public void testGetFiles() throws Exception {
        DavFile dir = new DavFile(new TestDavResource());
        File[] files = fsView.getFiles(dir, false);
        assertEquals(39, files.length);
        for (int i = 0; i < 39; i++) {
            LOGGER.info("File #" + i + " =" + files[i]);
        }
    }

    /**
     * Test {@link DavFileSystemView#getHomeDirectory} method.
     */
    @Test
    public void testGetHomeDirectory() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#getParentDirectory} method.
     */
    @Test
    public void testGetParentDirectory() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#getRoots} method.
     */
    @Test
    public void testGetRoots() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#isFileSystem} method.
     */
    @Test
    public void testIsFileSystem() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#isFloppyDrive} method.
     */
    @Test
    public void testIsFloppyDrive() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#isRoot} method.
     */
    @Test
    public void testIsRoot() {
        // not yet implemented
    }

    /**
     * Test {@link DavFileSystemView#isTraversable} method.
     */
    @Test
    public void testIsTraversable() {
        // not yet implemented
    }
}
