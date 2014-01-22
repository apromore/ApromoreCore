package org.apromore.dto;

import org.apromore.dao.dataObject.Version;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Version Number Code for Apromore.
 */
public class VersionUnitTest {

    @Test
    public void testOneNumberVersionFromString() {
        Version version = new Version("1");
        Assert.assertEquals("Numbers don't match", version.toString(), "1");
    }

    @Test
    public void testTwoNumberVersionFromString() {
        Version version = new Version("1.0");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0");
    }

    @Test
    public void testTwoNumberVersionFromNumber() {
        Version version = new Version(1,0);
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0");
    }

    @Test
    public void testThreeNumberVersionFromString() {
        Version version = new Version("1.0.0");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.0");
    }

    @Test
    public void testThreeNumberVersionFromNumber() {
        Version version = new Version(1,0,3);
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.3");
    }

    @Test
    public void testVersionWithQualifierFromString() {
        Version version = new Version("1.0.0.test");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.0.test");
    }

    @Test
    public void testVersionWithQualifierFromConstruct() {
        Version version = new Version(1,0,3,"test");
        Assert.assertEquals("Numbers don't match", version.toString(), "1.0.3.test");
    }
}
