package org.apromore.helper;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apromore.canoniser.result.CanoniserMetadataResult;
import org.apromore.model.NativeMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CanoniserHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserHelper.class);

    private CanoniserHelper() {
    }

    public static NativeMetaData convertFromCanoniserMetaData(final CanoniserMetadataResult metaData) {
        NativeMetaData xmlMetaData = new NativeMetaData();
        xmlMetaData.setProcessAuthor(metaData.getProcessAuthor());
        if (metaData.getProcessCreated() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(metaData.getProcessCreated());
            try {
                xmlMetaData.setProcessCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error("", e);
            }
        }
        if (metaData.getProcessLastUpdate() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(metaData.getProcessLastUpdate());
            try {
                xmlMetaData.setProcessLastUpdate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
            } catch (DatatypeConfigurationException e) {
                LOGGER.error("", e);
            }
        }
        xmlMetaData.setProcessName(metaData.getProcessName());
        xmlMetaData.setProcessDocumentation(metaData.getProcessDocumentation());
        xmlMetaData.setProcessVersion(metaData.getProcessVersion());
        return xmlMetaData;
    }

}
