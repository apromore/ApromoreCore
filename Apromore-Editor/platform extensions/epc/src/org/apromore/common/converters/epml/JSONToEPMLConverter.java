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
package org.apromore.common.converters.epml;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import de.epml.EPMLSchema;
import de.epml.ObjectFactory;
import de.epml.TEpcElement;
import de.epml.TypeArc;
import de.epml.TypeCoordinates;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeFlow;
import de.epml.TypeGraphics;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypePosition;
import de.epml.TypeProcessInterface;
import de.epml.TypeToProcess;
import org.json.JSONException;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicShape;
import org.xml.sax.SAXException;

/**
 * Converts a Signavio JSON stream to an EPML 2.0 stream.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 */
public class JSONToEPMLConverter {

    private static final Logger LOGGER = Logger.getLogger(JSONToEPMLConverter.class.getCanonicalName());

    private final ObjectFactory factory = new ObjectFactory();
    private final IdFactory idFactory = new IdFactory();

    /**
     * Maps identifiers in the source JSON representation to identifiers in the target EPML representation.
     */
    private final Map<String, BigInteger> jsonToEpmlIdMap = new HashMap<String, BigInteger>();

    /**
     * @param jsonStream source stream in Signavio JSON format
     * @param epmlStream destination stream to receive EPML 2.0 format
     */
    public void convert(final InputStream jsonStream, final OutputStream epmlStream) {
        try {
            String json = new Scanner(jsonStream, "UTF-8").useDelimiter("\\A").next();
            BasicDiagram diagram = BasicDiagramBuilder.parseJson(json);
            TypeEPML epml = toEPML(diagram);
            EPMLSchema.marshalEPMLFormat(epmlStream, epml, true /* is validating */);
        } catch (JAXBException | JSONException | SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param diagram a process model as a Signavio JSON object
     * @return the translation of the <var>diagram</var> into EPML
     * @throws RuntimeException if <var>diagram</var> contains an unsupported stencil ID
     */
    public TypeEPML toEPML(final BasicDiagram diagram) {
        final Map<BasicShape, BasicShape> sourceMap = new HashMap<>();  // for each edge, what is its source node?
        final Map<BasicShape, BasicShape> targetMap = new HashMap<>();  // for each edge, what is its target node?

        // First pass, during which the EPC topology is examined (i.e. sourceMap and targetMap are populated)
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
        TypeEPML epml = factory.createTypeEPML();

        TypeCoordinates coordinates = factory.createTypeCoordinates();
        coordinates.setXOrigin("leftToRight");
        coordinates.setYOrigin("topToBottom");
        epml.setCoordinates(coordinates);

        TypeEPC epc = factory.createTypeEPC();
        epc.setEpcId(new BigInteger("1" /*diagram.getResourceId()*/));  // TODO: 1 is a dummy value
        epc.setName("dummy" /* diagram.getProperty("title") */);
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            switch (shape.getStencilId()) {
                case "system":
                    LOGGER.info("eEPC extension found that isn't currently supported!");
                    break;
                case "AndConnector":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCAnd(populateElement(factory.createTypeAND(), shape)));
                    break;
                case "Event":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCEvent(populateElement(factory.createTypeEvent(), shape)));
                    break;
                case "Function":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCFunction(populateElement(factory.createTypeFunction(), shape)));
                    break;
                case "ControlFlow":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCArc(populateArc(factory.createTypeArc(), shape, sourceMap, targetMap)));
                    break;
                case "OrConnector":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCOr(populateElement(factory.createTypeOR(), shape)));
                    break;
                case "ProcessInterface":
                    TypeProcessInterface processInterface = populateElement(factory.createTypeProcessInterface(), shape);

                    TypeToProcess toProcess = factory.createTypeToProcess();
                    toProcess.setLinkToEpcId(new BigInteger("1"));  // TODO: 1 is a dummy value; need to actually populate it
                    processInterface.setToProcess(toProcess);

                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCProcessInterface(processInterface));
                    break;
                case "XorConnector":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCXor(populateElement(factory.createTypeXOR(), shape)));
                    break;
                default:
                    throw new RuntimeException("Unsupported stencil ID: " + shape.getStencilId());
            }
        }

        TypeDirectory directory = factory.createTypeDirectory();
        directory.getEpcOrDirectory().add(epc);
        epml.getDirectory().add(directory);

        return epml;
    }

    /**
     * Name an EPC element within an EPC.
     *
     * @param jsonId the identifier of the element within the source JSON representation
     * @return the identifier within the target EPML representation
     */
    private BigInteger getId(final String jsonId) {
        if (jsonToEpmlIdMap.containsKey(jsonId)) {
            // This JSON identifier has been encountered before, so return its previously-mapped EPML value
            return jsonToEpmlIdMap.get(jsonId);
        } else {
            // This is the first occurrence of this JSON identifier, so map it to a new EPC identifier
            BigInteger epmlId = idFactory.newId(jsonId);
            jsonToEpmlIdMap.put(jsonId, epmlId);
            return epmlId;
        }

    }

    /**
     * @param arc   an EPC arc to initialize
     * @param shape the Signavio JSON object corresponding to the <var>arc</var>
     * @return <var>arc</var>
     */
    private TypeArc populateArc(final TypeArc arc, final BasicShape shape, final Map<BasicShape, BasicShape> sourceMap,
        final Map<BasicShape, BasicShape> targetMap) {
        BasicShape sourceShape = sourceMap.get(shape);
        BasicShape targetShape = targetMap.get(shape);

        arc.setId(getId(shape.getResourceId()));

        TypeFlow flow = factory.createTypeFlow();
        flow.setSource(getId(sourceShape.getResourceId()));
        flow.setTarget(getId(targetShape.getResourceId()));
        arc.setFlow(flow);

        TypeMove move = factory.createTypeMove();
        for (int i = 0; i < shape.getDockersReadOnly().size(); i++) {
            Point point = shape.getDockersReadOnly().get(i);
            Point origin = new Point(0, 0);

            // If this edge is connected to a source node, the first waypoint's coordinates are relative to that node
            // If this edge is connected to a target node, the last waypoint's coordinates are relative to that node
            if (i == 0 && sourceShape != null) {
                origin = sourceShape.getBounds().getUpperLeft();
            } else if (i == shape.getDockersReadOnly().size() - 1 && targetShape != null) {
                origin = targetShape.getBounds().getUpperLeft();
            }

            move.getPosition().add(toMove2(point, origin));
        }
        arc.getGraphics().add(move);
        return arc;
    }

    private TypeMove2 toMove2(final Point point, final Point origin) {
        TypeMove2 move2 = factory.createTypeMove2();
        move2.setX(new BigDecimal(point.getX() + origin.getX()));
        move2.setY(new BigDecimal(point.getY() + origin.getY()));
        return move2;
    }

    /**
     * @param element an EPC element to initialize
     * @param shape   the Signavio JSON object corresponding to the <var>element</var>
     * @return <var>element</var>
     */
    private <T extends TEpcElement> T populateElement(final T element, final BasicShape shape) {
        element.setId(getId(shape.getResourceId()));
        element.setName(shape.getProperty("title"));
        element.setGraphics(toGraphics(shape.getBounds()));
        return element;
    }

    /**
     * @param bounds a Signavio JSON bounds
     * @return an equivalent EPC graphics specification
     */
    private TypeGraphics toGraphics(final Bounds bounds) {
        TypePosition position = factory.createTypePosition();
        position.setX(new BigDecimal(bounds.getUpperLeft().getX()));
        position.setY(new BigDecimal(bounds.getUpperLeft().getY()));
        position.setWidth(new BigDecimal(bounds.getWidth()));
        position.setHeight(new BigDecimal(bounds.getHeight()));

        TypeGraphics graphics = factory.createTypeGraphics();
        graphics.setPosition(position);
        return graphics;
    }

    /**
     * Command line filter converting a Signavio JSON-formatted standard input stream into an EPML-formatted standard output stream.
     *
     * @param args first argument names the input file
     * @throw FileNotFoundException  if <code>args[0]</code> isn't the name of a file
     */
    public static void main(String args[]) throws FileNotFoundException {
        new JSONToEPMLConverter().convert(new FileInputStream(args[0]), System.out);
    }
}
