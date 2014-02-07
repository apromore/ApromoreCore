package org.apromore.common.converters.pnml;

import de.hpi.diagram.SignavioUUID;
import org.apromore.common.converters.pnml.context.PNMLConversionContext;
import org.apromore.common.converters.pnml.handler.PNMLHandler;
import org.apromore.common.converters.pnml.handler.PNMLHandlerFactory;
import org.jbpt.petri.Flow;
import org.jbpt.petri.Marking;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Takes the pnml data and creates a PNML object and converts it into JSON for the editor to visualise.
 *
 * @author Cameron James
 */
public class PNMLToJSONConverter {

    /**
     * Does the conversion from pnml to json.
     * @param pnmlData   the pnml data
     * @param encoding   the string encoding format
     * @param jsonStream the output stream.
     */
    public void convert(String pnmlData, String encoding, ServletOutputStream jsonStream) {
        try {
            PNMLSerializer pnmlSerializer = new PNMLSerializer();
            NetSystem pnmlNet = pnmlSerializer.parse(pnmlData.getBytes(encoding));
            convertPNML(pnmlNet, jsonStream);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /* Convert the PNML to JSON. */
    private void convertPNML(NetSystem pnmlNet, ServletOutputStream jsonStream) throws JSONException, IOException {
        PNMLConversionContext context = new PNMLConversionContext();
        PNMLHandlerFactory converterFactory = new PNMLHandlerFactory(context);
        context.setConverterFactory(converterFactory);

        String stencilSetNs = "http://b3mn.org/stencilset/petrinet#";
        BasicDiagram diagram = new BasicDiagram(SignavioUUID.generate(), pnmlNet.getName(), new StencilSetReference(stencilSetNs));

        Bounds bounds = new Bounds();
        bounds.setCoordinates(0, 0, 600, 600);
        diagram.setBounds(bounds);
        diagram.setProperty("title", pnmlNet.getName());

        context.addDiagram(diagram);

        Marking marking = pnmlNet.getMarking();
        for (Transition transition : pnmlNet.getTransitions()) {
            PNMLHandler converter = converterFactory.createNodeConverter(transition, marking);
            if (converter != null) {
                diagram.addChildShape(converter.convert());
            }
        }
        for (Place place : pnmlNet.getPlaces()) {
            PNMLHandler converter = converterFactory.createNodeConverter(place, marking);
            if (converter != null) {
                diagram.addChildShape(converter.convert());
            }
        }
        for (Flow flow : pnmlNet.getFlow()) {
            PNMLHandler converter = converterFactory.createEdgeConverter(flow);
            if (converter != null) {
                diagram.addChildShape(converter.convert());
            }
        }

        writeJson(context, jsonStream);
    }

    /* Convert the basic diagram to json in an output stream. */
    private void writeJson(final PNMLConversionContext context, final OutputStream jsonStream) throws JSONException, IOException {
        BasicDiagram diagram = context.getDiagram(0);
        assert diagram != null;
        JSONObject jsonDiagram = GenericJSONBuilder.parseModel(diagram);
        OutputStreamWriter outWriter = new OutputStreamWriter(jsonStream, "UTF-8");
        jsonDiagram.write(outWriter);
        outWriter.flush();
    }

}
