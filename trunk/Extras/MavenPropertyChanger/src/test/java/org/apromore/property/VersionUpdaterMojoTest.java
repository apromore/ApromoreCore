package org.apromore.property;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test the VersionUpdaterMojo class.
 *
 * @author Cameron James
 */
public class VersionUpdaterMojoTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private FileUtil fileUtils;
    private Log log;

    private VersionUpdaterMojo mojo;

    @Before
    public void setUp() throws Exception {
        fileUtils = new FileUtil();
        log = createMock(Log.class);
        mojo = new VersionUpdaterMojo(fileUtils) {
            @Override
            public Log getLog() {
                return log;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        FileOutputStream resetter = new FileOutputStream("src/test/resources/empty.properties");
        resetter.write("# Test File 1".getBytes());
        resetter.close();

        resetter = new FileOutputStream("src/test/resources/populated.properties");
        resetter.write("# Test File 2\n".getBytes());
        resetter.write("version.number=1.0.0-1\n".getBytes());
        resetter.write("version.builddate=26-05-2013\n".getBytes());
        resetter.close();
    }


    @Test
    public void shouldSkip() throws Exception {
        mojo.setSkip(true);

        log.info("Skipping");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
    }

    @Test
    public void shouldFailFileNull() throws Exception {
        mojo.setSkip(false);

        log.error("<propertyFile> can not be empty!");
        expectLastCall().anyTimes();
        replay(log);

        exception.expect(MojoExecutionException.class);
        mojo.execute();

        verify(log);
    }

    @Test
    public void shouldFailFileNotExist() throws Exception {
        mojo.setBasedir(".");
        mojo.setSkip(false);
        mojo.setPropertyFile("someFileThatDoesntExist.properties");

        log.info("Ignoring missing file");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
    }

    @Test
    public void shouldFailFileNotExist2() throws Exception {
        mojo.setBasedir("./somePlace");
        mojo.setSkip(false);
        mojo.setPropertyFile("someFileThatDoesntExist.properties");

        log.info("Ignoring missing file");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
    }

    @Test
    public void shouldFailFileNotExist3() throws Exception {
        mojo.setBasedir("");
        mojo.setSkip(false);
        mojo.setPropertyFile("someFileThatDoesntExist.properties");

        log.info("Ignoring missing file");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
    }

    @Test
    public void shouldCreateEntriesEmptyFile() throws Exception {
        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/empty.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(1);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");

        log.info("Properties updates successfully");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
        verifyFilePopulated("src/test/resources/empty.properties");
    }

    @Test
    public void shouldCreateEntriesEmptyFileQuietMode() throws Exception {
        mojo.setSkip(false);
        mojo.setQuiet(true);
        mojo.setPropertyFile("src/test/resources/empty.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(1);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");

        replay(log);

        mojo.execute();

        verify(log);
        verifyFilePopulated("src/test/resources/empty.properties");
    }


    @Test
    public void shouldUpdateVersionNumber() throws Exception {
        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/populated.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(4);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");

        log.info("Properties updates successfully");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
        verifyUpdatedFieldValue("src/test/resources/populated.properties", "version.number", "1.0.0-5");
    }

    @Test
    public void shouldFailUpdateVersionNumber() throws Exception {
        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/populated.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(-4);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");

        log.error("<increment> must be a positive integer value!");
        expectLastCall().anyTimes();
        replay(log);

        exception.expect(MojoExecutionException.class);
        mojo.execute();

        verify(log);
        verifyUpdatedFieldValue("src/test/resources/populated.properties", "version.number", "1.1");
    }

    @Test
    public void shouldUpdateBuildDate() throws Exception {
        String dateFormat = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/populated.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(4);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");

        log.info("Properties updates successfully");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
        verifyUpdatedFieldValue("src/test/resources/populated.properties", "version.builddate", dateFormat);
    }

    @Test
    public void shouldUpdateBuildDateWithTime() throws Exception {
        String dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a").format(new Date());

        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/populated.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(4);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");
        mojo.setAddTime(true);
        mojo.setTimeFormat("HH:mm a");

        log.info("Properties updates successfully");
        expectLastCall().anyTimes();
        replay(log);

        mojo.execute();

        verify(log);
        verifyUpdatedFieldValue("src/test/resources/populated.properties", "version.builddate", dateFormat);
    }

    @Test
    public void shouldFailBuildDateBadDateFormat() throws Exception {
        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/populated.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(4);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat(null);

        log.error("<dateFormat> must be populated with a correct date format!");
        expectLastCall().anyTimes();
        replay(log);

        exception.expect(MojoExecutionException.class);
        mojo.execute();

        verify(log);
    }

    @Test
    public void shouldFailBuildDateBadTimeFormat() throws Exception {
        mojo.setSkip(false);
        mojo.setPropertyFile("src/test/resources/populated.properties");
        mojo.setVersionName("version.number");
        mojo.setIncrement(4);
        mojo.setBuildDateName("version.builddate");
        mojo.setDateFormat("dd-MM-yyyy");
        mojo.setAddTime(true);
        mojo.setTimeFormat(null);

        log.error("<timeFormat> must be populated with a correct time format!");
        expectLastCall().anyTimes();
        replay(log);

        exception.expect(MojoExecutionException.class);
        mojo.execute();

        verify(log);
    }


    /* verify that the empty file has been populated. */
    private void verifyFilePopulated(String fileName) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(fileName));

        assertTrue(prop.getProperty("version.number") != null);
        assertTrue(prop.getProperty("version.builddate") != null);
    }

    /* verify that the field has been updated. */
    private void verifyUpdatedFieldValue(String fileName, String field, String value) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(fileName));

        assertEquals(value, prop.getProperty(field));
    }
}
