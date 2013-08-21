package org.apromore.property;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test the FileUtil class.
 *
 * @author Cameron James
 */
public class FileUtilTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private FileUtil fileUtils;

    @Before
    public void setUp() {
        fileUtils = new FileUtil();
    }

    @Test
    public void shouldDetermineIfFileExists() throws Exception {
        File file = folder.newFile("tempfile");
        assertTrue(fileUtils.fileNotExists("non existant"));
        assertTrue(fileUtils.fileNotExists(null));
        assertTrue(fileUtils.fileNotExists(""));
        assertFalse(fileUtils.fileNotExists(file.getAbsolutePath()));
    }


    @Test
    public void shouldReturnTrueWhenAbsolutePathFilename() {
        assertFalse(fileUtils.isAbsolutePath("target/somedir/somepath"));
        assertTrue(fileUtils.isAbsolutePath(new File("target/somefile").getAbsolutePath()));
    }
}
