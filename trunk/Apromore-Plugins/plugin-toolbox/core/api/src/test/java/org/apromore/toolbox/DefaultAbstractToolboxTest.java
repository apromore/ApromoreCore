package org.apromore.toolbox;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DefaultAbstractToolboxTest {

    private DefaultAbstractToolbox defaultAbstractToolbox;

    @Before
    public void setUp() {
        defaultAbstractToolbox = new DefaultAbstractToolbox() {
            @Override
            public String getToolName() {
                return "test tool";
            }
        };
    }

    @Test
    public void testGetToolName() {
        assertNotNull(defaultAbstractToolbox);
        assertNotNull(defaultAbstractToolbox.getToolName());
        assertNotNull(defaultAbstractToolbox.getAuthor());
    }

}
