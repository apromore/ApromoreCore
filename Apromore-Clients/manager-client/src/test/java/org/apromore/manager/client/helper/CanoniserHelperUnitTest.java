package org.apromore.manager.client.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.model.NativeMetaData;
import org.junit.Test;

public class CanoniserHelperUnitTest {

    @Test
    public void testConvertFromCanoniserMetaData() {
        CanoniserMetadataResult metaData = new CanoniserMetadataResult();
        metaData.setProcessAuthor("test");
        metaData.setProcessCreated(new Date());
        metaData.setProcessLastUpdate(null);
        metaData.setProcessName("a name");
        metaData.setProcessVersion("1.0");
        NativeMetaData xmlMetaData = CanoniserHelper.convertFromCanoniserMetaData(metaData);
        assertNotNull(xmlMetaData);
        assertEquals("test", xmlMetaData.getProcessAuthor());
        assertEquals("a name", xmlMetaData.getProcessName());
        assertEquals("1.0", xmlMetaData.getProcessVersion());
        assertNull(xmlMetaData.getProcessLastUpdate());
        assertNotNull(xmlMetaData.getProcessCreated());
    }

}
