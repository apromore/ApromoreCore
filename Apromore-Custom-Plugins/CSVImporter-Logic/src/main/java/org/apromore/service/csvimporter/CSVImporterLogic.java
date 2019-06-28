package org.apromore.service.csvimporter;

import org.zkoss.util.media.Media;
import org.zkoss.zul.Div;
import org.zkoss.zul.ListModel;

import java.util.HashMap;
import java.util.List;

/**
 * Sample service API.
 */
public interface CSVImporterLogic {

    /**
     * Sample service API method.
     *
     * @param n  parameter the service requires
     * @return some result
     * @throws SampleException if something goes wrong
     */
    String method(int n);

    /**
     * Something that might go wrong.
     */
    public static class SampleException extends Exception {
    }
}
