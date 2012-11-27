package org.apromore.util;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class XMLUtilsManualTest {

    private String testElement;
    private String testElement2;

    @Before
    public void setUp() {
        testElement = "<inputParam:inputParam xmlns:inputParam=\"http://www.yawlfoundation.org/yawlschema\" xmlns=\"http://www.yawlfoundation.org/yawlschema\">"
                +"<index>0</index>"
                +"<name>interval</name>"
                +"<type>string</type>"
                +"<namespace>http://www.w3.org/2001/XMLSchema</namespace>"
            +"</inputParam:inputParam>";

        testElement2 = "<codelet:codelet xmlns:codelet=\"http://www.yawlfoundation.org/yawlschema\" xmlns=\"http://www.yawlfoundation.org/yawlschema\">org.yawlfoundation.yawl.resourcing.codelets.RandomWait</codelet:codelet>";
    }

    @Test
    public void testAnyElementToStringAndBack() {

        Element anyObject = XMLUtils.stringToAnyElement(testElement);
        Element anyObject2 = XMLUtils.stringToAnyElement(testElement2);

        assertNotNull(anyObject);
        assertNotNull(anyObject2);

        String element = XMLUtils.anyElementToString(anyObject);
        String element2 = XMLUtils.anyElementToString(anyObject2);

        assertEquals(testElement, element);
        assertEquals(testElement2, element2);

    }


}
