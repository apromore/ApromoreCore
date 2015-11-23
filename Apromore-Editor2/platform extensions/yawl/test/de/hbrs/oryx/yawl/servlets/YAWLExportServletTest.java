package de.hbrs.oryx.yawl.servlets;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import de.hbrs.orxy.yawl.OryxTestData;

public class YAWLExportServletTest {

    @Test
    public void testgetYAWLFromJSON() throws JSONException, IOException {
        YAWLExportServlet exportServlet = new YAWLExportServlet();
        JSONObject orderFulfillment = exportServlet.getYAWLFromJSON(OryxTestData.orderFulfillmentRequestData);
        assertNotNull(orderFulfillment);

        // TODO test JSON

        FileWriter fWriter = new FileWriter(new File("target/export-servletresponse-Orderfulfillment.json"));
        fWriter.write(orderFulfillment.toString(2));
        fWriter.close();
    }

}
