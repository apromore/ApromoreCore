package de.hbrs.oryx.yawl.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jdom2.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.yawlfoundation.yawl.exceptions.YSyntaxException;

import de.hbrs.orxy.yawl.YAWLTestData;

public class YAWLImportServletTest {

    @Test
    public void testGetJsonFromYAWL() throws YSyntaxException, JSONException, JDOMException, IOException {
        YAWLImportServlet importServlet = new YAWLImportServlet();
        JSONObject orderFulfillment = importServlet.getJsonFromYAWL(YAWLTestData.orderFulfillmentSource, true);
        assertNotNull(orderFulfillment);

        assertTrue(orderFulfillment.has("diagrams"));
        assertTrue(orderFulfillment.has("rootNetName"));
        assertTrue(orderFulfillment.has("hasFailed"));
        assertTrue(orderFulfillment.has("hasWarnings"));
        assertTrue(orderFulfillment.has("warnings"));

        String rootNetName = orderFulfillment.getString("rootNetName");
        assertEquals("Overall", rootNetName);

        JSONArray diagramArray = orderFulfillment.getJSONArray("diagrams");
        assertEquals(9, diagramArray.length());

        FileWriter fWriter = new FileWriter(new File("target/import-servletresponse-Orderfulfillment.json"));
        fWriter.write(orderFulfillment.toString(2));
        fWriter.close();
    }

    @Test
    public void testGetJsonFromYAWLEmbedded() throws YSyntaxException, JSONException, JDOMException, IOException {
        YAWLImportServlet importServlet = new YAWLImportServlet();
        JSONObject orderFulfillment = importServlet.getJsonFromYAWL(YAWLTestData.orderFulfillmentSource, false);
        assertNotNull(orderFulfillment);
    }

    /**
     * This test prepares a request that can be used to test YAWLExportServlet! If you make any changes to the import of a YAWL specification, you
     * probably want to copy the generated file 'orderfulfillment.json' to the '/resources/' directory and overwrite the old test fixture.
     *
     * This is not done automatically!!
     *
     */
    @Test
    public void prepareExportRequestData() throws YSyntaxException, JSONException, JDOMException, IOException {
        JSONObject orderFulfillment = new YAWLImportServlet().getJsonFromYAWL(YAWLTestData.orderFulfillmentSource, true);
        writeRequestData(orderFulfillment, "orderfulfilment.json");

        JSONObject filmProduction = new YAWLImportServlet().getJsonFromYAWL(YAWLTestData.filmProduction, true);
        writeRequestData(filmProduction, "filmproduction.json");
    }

    private void writeRequestData(final JSONObject jsonResult, final String fileName) throws JSONException, IOException {
        JSONObject exportRequestData = new JSONObject();
        exportRequestData.put("subDiagrams", new JSONArray());

        String rootNetName = jsonResult.getString("rootNetName");
        JSONArray diagramArray = jsonResult.getJSONArray("diagrams");

        for (int i = 0; i < diagramArray.length(); i++) {
            JSONObject diagram = diagramArray.getJSONObject(i);

            if (diagram.getString("resourceId").equals(rootNetName)) {
                exportRequestData.put("rootDiagram", diagram);
            } else {
                JSONObject diagramWrapper = new JSONObject();
                diagramWrapper.put("id", diagram.getString("resourceId"));
                diagramWrapper.put("diagram", diagram);
                exportRequestData.getJSONArray("subDiagrams").put(diagramWrapper);
            }
        }

        FileWriter fWriter = new FileWriter(new File("target/" + fileName));
        fWriter.write(exportRequestData.toString(2));
        fWriter.close();
    }

}
