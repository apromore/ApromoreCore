/**
 * Copyright (c) 2013 Simon Raboczi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * See: http://www.opensource.org/licenses/mit-license.php
 *
 */
package org.apromore.common.converters.pnml;

import de.epml.ObjectFactory;
import org.apromore.common.converters.epml.IdFactory;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.Node;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.petri.io.PNMLSerializer;
import org.jbpt.throwable.SerializationException;
import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicShape;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Converts a Signavio JSON stream to an PNML 1.3.2 stream.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class JSONToPNMLConverter {

    private static final Logger LOGGER = Logger.getLogger(JSONToPNMLConverter.class.getCanonicalName());

    public static final String PNML_CONTEXT = "org.apromore.pnml";

    /**
     * @param jsonStream source stream in Signavio JSON format
     * @param pnmlStream destination stream to receive PNML format
     */
    public void convert(final InputStream jsonStream, final OutputStream pnmlStream) {
        try {
            String json = new Scanner(jsonStream, "UTF-8").useDelimiter("\\A").next();
            BasicDiagram diagram = BasicDiagramBuilder.parseJson(json);
            NetSystem pnml = toPNML(diagram);

            String serializePetriNet = PNMLSerializer.serializePetriNet(pnml);
            //marshalPNMLFormat(pnmlStream, pnml, false);
            //EPMLSchema.marshalEPMLFormat(pnmlStream, pnml, false);
        } catch (JSONException | SerializationException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param diagram a process model as a Signavio JSON object
     * @return the translation of the <var>diagram</var> into PNML
     * @throws RuntimeException if <var>diagram</var> contains an unsupported stencil ID
     */
    public NetSystem toPNML(final BasicDiagram diagram) {
        final Map<BasicShape, BasicShape> sourceMap = new HashMap<>();
        final Map<BasicShape, BasicShape> targetMap = new HashMap<>();

        // First pass, during which the PNML topology is examined (i.e. sourceMap and targetMap are populated)
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            if (shape.isNode()) {
                for (BasicShape incoming : shape.getIncomingsReadOnly()) {
                    targetMap.put(incoming, shape);
                }
                for (BasicShape outgoing : shape.getOutgoingsReadOnly()) {
                    sourceMap.put(outgoing, shape);
                }
            }
        }

        // Second pass, during which the EPML model is created and populated
        NetSystem pnml = new NetSystem();
        pnml.setId(diagram.getResourceId());
        pnml.setName(diagram.getProperty("title"));
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            switch (shape.getStencilId()) {
                case "Transition":
                    pnml.addTransition(createTransition(shape));
                    break;
                case "VerticalEmptyTransition":
                    pnml.addTransition(createTransition(shape));
                    break;
                case "Place":
                    pnml.addPlace(createPlace(shape));
                    break;
                case "Arc":
                    Node source = findNode(pnml, shape, sourceMap);
                    Node target = findNode(pnml, shape, targetMap);
                    pnml.addFlow(source, target);
                    break;
                default:
                    throw new RuntimeException("Unsupported stencil ID: " + shape.getStencilId());
            }
        }

        return pnml;
    }

    private Node findNode(NetSystem pnml, BasicShape shape, Map<BasicShape, BasicShape> nodeMap) {
        Node resultNode = null;
        BasicShape result = nodeMap.get(shape);

        for (Node node : pnml.getNodes()) {
            if (node.getId().equalsIgnoreCase(result.getResourceId())) {
                resultNode = node;
            }
        }

        return resultNode;
    }

    private Place createPlace(BasicShape shape) {
        Place place = new Place();
        place.setId(shape.getResourceId());
        place.setLabel(shape.getProperty("title"));
        return place;
    }

    private Transition createTransition(BasicShape shape) {
        Transition transition = new Transition();
        transition.setId(shape.getResourceId());
        if (shape.getStencilId().equals("Transition")) {
            transition.setLabel(shape.getProperty("title"));
        }
        return transition;
    }

}
