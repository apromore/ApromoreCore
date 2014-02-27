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

import org.apromore.pnml.ArcType;
import org.apromore.pnml.ArcTypeType;
import org.apromore.pnml.DimensionType;
import org.apromore.pnml.GraphicsNodeType;
import org.apromore.pnml.NetType;
import org.apromore.pnml.NodeNameType;
import org.apromore.pnml.NodeType;
import org.apromore.pnml.PNMLSchema;
import org.apromore.pnml.PlaceType;
import org.apromore.pnml.PnmlType;
import org.apromore.pnml.PositionType;
import org.apromore.pnml.TransitionType;
import org.json.JSONException;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
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


    /**
     * @param jsonStream source stream in Signavio JSON format
     * @param pnmlStream destination stream to receive PNML format
     */
    public void convert(final InputStream jsonStream, final OutputStream pnmlStream) {
        try {
            String json = new Scanner(jsonStream, "UTF-8").useDelimiter("\\A").next();
            BasicDiagram diagram = BasicDiagramBuilder.parseJson(json);
            PnmlType pnml = toPNML(diagram);
            PNMLSchema.marshalPNMLFormat(pnmlStream, pnml, false /* is validating */);
        } catch (JAXBException | JSONException | SAXException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param diagram a process model as a Signavio JSON object
     * @return the translation of the <var>diagram</var> into PNML
     * @throws RuntimeException if <var>diagram</var> contains an unsupported stencil ID
     */
    public PnmlType toPNML(final BasicDiagram diagram) {
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
        PnmlType pnml = new PnmlType();
        NetType net = new NetType();
        net.setId(diagram.getResourceId());
        net.setType("http://www.yasper.org/specs/epnml-1.1");

        NetType.Name name = new NetType.Name();
        name.setText(diagram.getProperty("title"));
        net.setName(name);
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            switch (shape.getStencilId()) {
                case "Transition":
                    net.getTransition().add(createTransition(shape));
                    break;
                case "VerticalEmptyTransition":
                    net.getTransition().add(createTransition(shape));
                    break;
                case "Place":
                    net.getPlace().add(createPlace(shape));
                    break;
                case "Arc":
                    ArcType arc = new ArcType();
                    arc.setId(shape.getResourceId());
                    arc.setSource(findNode(net, shape, sourceMap));
                    arc.setTarget(findNode(net, shape, targetMap));
                    if (Boolean.TRUE.equals(shape.getPropertyBoolean("reset"))) {
                        ArcTypeType type = new ArcTypeType();
                        type.setText("reset");
                        arc.setType(type);
                    }
                    net.getArc().add(arc);
                    break;
                default:
                    throw new RuntimeException("Unsupported stencil ID: " + shape.getStencilId());
            }
        }

        pnml.getNet().add(net);

        return pnml;
    }

    private NodeType findNode(NetType net, BasicShape shape, Map<BasicShape, BasicShape> nodeMap) {
        BasicShape result = nodeMap.get(shape);

        for (NodeType node : net.getPlace()) {
            if (node.getId().equalsIgnoreCase(result.getResourceId())) {
                return node;
            }
        }
        for (NodeType node : net.getTransition()) {
            if (node.getId().equalsIgnoreCase(result.getResourceId())) {
                return node;
            }
        }

        return null;
    }

    private PlaceType createPlace(BasicShape shape) {
        PlaceType place = new PlaceType();
        place.setId(shape.getResourceId());

        NodeNameType name = new NodeNameType();
        name.setText(shape.getProperty("title"));
        place.setName(name);

        if (shape.hasProperty("numberoftokens") && !"".equals(shape.getProperty("numberoftokens"))) {
            PlaceType.InitialMarking marking = new PlaceType.InitialMarking();
            marking.setText(shape.getProperty("numberoftokens"));
            place.setInitialMarking(marking);
        }

        place.setGraphics(createNodeGraphics(shape));

        return place;
    }

    private TransitionType createTransition(BasicShape shape) {
        TransitionType transition = new TransitionType();
        transition.setId(shape.getResourceId());
        if (shape.getStencilId().equals("Transition")) {
            NodeNameType name = new NodeNameType();
            name.setText(shape.getProperty("title"));
            transition.setName(name);
        }
        transition.setGraphics(createNodeGraphics(shape));
        return transition;
    }


    private GraphicsNodeType createNodeGraphics(BasicShape shape) {
        BigDecimal lowerX = new BigDecimal(shape.getBounds().getLowerLeft().getX());
        BigDecimal lowerY = new BigDecimal(shape.getBounds().getLowerLeft().getY());
        PositionType position = new PositionType();
        position.setX(lowerX);
        position.setY(lowerY);

        lowerX = new BigDecimal(shape.getBounds().getWidth());
        lowerY = new BigDecimal(shape.getBounds().getHeight());
        DimensionType dimension = new DimensionType();
        dimension.setX(lowerX);
        dimension.setY(lowerY);

        GraphicsNodeType graphicsNodeType = new GraphicsNodeType();
        graphicsNodeType.setDimension(dimension);
        graphicsNodeType.setPosition(position);

        return graphicsNodeType;
    }

}
