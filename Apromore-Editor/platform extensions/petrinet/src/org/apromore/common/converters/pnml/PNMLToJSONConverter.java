package org.apromore.common.converters.pnml;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.serialization.PTNetRDFImporter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter.Tool;
import org.apromore.common.converters.pnml.servlet.StencilSetUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.ServletOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Takes the pnml data and creates a PNML object and converts it into JSON for the editor to visualise.
 *
 * @author Cameron James
 */
public class PNMLToJSONConverter {

    private Tool tool;

    /**
     * Does the conversion from pnml to json.
     *
     * @param pnmlData   the pnml data
     * @param encoding   the string encoding format
     * @param jsonStream the output stream.
     */
    public void convert(String pnmlData, String encoding, ServletOutputStream jsonStream)
            throws IOException, SAXException, ParserConfigurationException {
        convert(new ByteArrayInputStream(pnmlData.getBytes(encoding)), jsonStream);
    }

    /**
     * @param pnmlStream source stream in Signavio JSON format
     * @param jsonStream destination stream to receive EPML 2.0 format
     */
    public void convert(final InputStream pnmlStream, final OutputStream jsonStream)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(pnmlStream);
        Document pnmlDoc = builder.newDocument();

        processDocument(document, pnmlDoc);

        String jsonRepresentation = RdfJsonTransformation.toJson(pnmlDoc, "").toString();

        jsonStream.write(jsonRepresentation.getBytes());
    }


    protected void processDocument(Document document, Document pnmlDoc) throws IOException {
        String type = new StencilSetUtil().getStencilSet(document);
        //if (type.equals("petrinet.json")) {
            processPetriNet(document, pnmlDoc);
        //} else {
        //    throw new IOException("Sorry, not Implemented!");
        //}
    }


    protected void processPetriNet(Document document, Document pnmlDoc) {
        PTNetRDFImporter importer = new PTNetRDFImporter(document);
        PTNet net = importer.loadPTNet();

        PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
        convertNetToDocumentUsing(pnmlDoc, net, exp);
    }

    private void convertNetToDocumentUsing(Document pnmlDoc, PetriNet net, PetriNetPNMLExporter exp) {
        exp.setTargetTool(getTool());
        if (net.getInitialMarking().getNumTokens() == 0) {
            net.getInitialMarking().setNumTokens(net.getInitialPlace(), 1);
        }
        exp.savePetriNet(pnmlDoc, net);
    }


    public Tool getTool() {
        return this.tool;
    }

    public void setTool(Tool tool) {
        this.tool = tool;
    }
}
