package org.apromore.common.converters.bpstruct;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.serialize.JSON2Process;
import de.hpi.bpt.process.serialize.SerializationException;
import org.apache.commons.io.FileUtils;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;


/**
 * Converts Editor JSon to BPStruct JSon.
 *
 * @author Cameron James
 */
public class BPStructConverter {

    private static final String BPSTRUCT_SERVER = "http://localhost:8080/bpstruct/rest/v1/structure/max";

    /**
     * Does the conversion.
     *
     * @param editorJson source in Signavio JSON format.
     * @return the editor JSon format but structured.
     */
    public String convert(final String editorJson) throws Exception {
        String json;
        Process process;
        BasicDiagram newDiagram = null;

        BasicDiagram diagram = BasicDiagramBuilder.parseJson(editorJson);
        if (diagram != null) {
            process = toProcessModel(diagram);
            json = getJsonFromProcess(process);

            Process newProcess = getProcessFromJson(processWithBPStruct(json));
            newDiagram = toBasicDiagram(newProcess);
        }

        if (newDiagram != null) {
            return newDiagram.getJSON().toString();
        } else {
            return "";
        }
    }

    private String processWithBPStruct(String json) throws Exception {
        if (json != null && !json.equals("")) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(BPSTRUCT_SERVER);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(json.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                System.out.println(response.toString());
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        return null;
    }


    /* Converts the Process to JSon. */
    private String getJsonFromProcess(Process process) {
        String json = null;
        try {
            json = Process2JSON.convert(process);
        } catch (SerializationException e) {
            System.err.println(e.getMessage());
        }
        System.out.println(json);
        return json;
    }

    /* Converts the JSon to Process. */
    private Process getProcessFromJson(String json) {
        Process process = null;
        try {
            process = JSON2Process.convert(json);
        } catch (SerializationException e) {
            System.err.println(e.getMessage());
        }
        return process;
    }


    /* Convert a basicDiagram into the Format required for BPStruct. */
    private Process toProcessModel(BasicDiagram diagram) throws SerializationException {
        BasicDiagramToProcessModel bd2pm = new BasicDiagramToProcessModel();
        return bd2pm.convert(diagram);
    }

    /* Convert a basicDiagram into the Format required for BPStruct. */
    private BasicDiagram toBasicDiagram(Process process) throws SerializationException {
        ProcessModelToBasicDiagram pm2bd = new ProcessModelToBasicDiagram();
        return pm2bd.convert(process);
    }



    /* So we can run it as a test in isolation. */
    public static void main(String[] args) throws Exception {
        File file = new File("test/CyclicStructureSimple.json");
        new BPStructConverter().convert(FileUtils.readFileToString(file));
    }

}
