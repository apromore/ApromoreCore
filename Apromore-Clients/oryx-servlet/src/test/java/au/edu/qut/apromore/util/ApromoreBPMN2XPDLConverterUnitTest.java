package au.edu.qut.apromore.util;

import au.edu.qut.apromore.TestData;
import de.hpi.bpmn2xpdl.XPDLPackage;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test the BPMN 2 XPDL Converter.
 */
public class ApromoreBPMN2XPDLConverterUnitTest {
    
    @Test
    public void testGetXPDLModel() {
        ApromoreBPMN2XPDLConverter con = new ApromoreBPMN2XPDLConverter();
        
        XPDLPackage xpdl = con.getXPDLModel(TestData.XPDL);

        assertThat(xpdl.getRedefinableHeader(), notNullValue());
    }
    
}
