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
import de.epml.TypeAttribute;
import de.epml.TypeAttrType;
import de.epml.TypeAttrTypes;
import de.epml.TypeCoordinates;
import de.epml.TypeDirectory;
import de.epml.TypeEPC;
import de.epml.TypeEPML;
import de.epml.TypeFlow;
import de.epml.TypeGraphics;
import de.epml.TypeMove;
import de.epml.TypeMove2;
import de.epml.TypeObject;
import de.epml.TypePosition;
import de.epml.TypeProcessInterface;
import de.epml.TypeRelation;
import de.epml.TypeRole;
import de.epml.TypeToProcess;
import org.json.JSONException;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicDiagramBuilder;
import org.oryxeditor.server.diagram.basic.BasicEdge;
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

        boolean  isRolePresent = false;  // are any role EPC elements present?
        TypeEPML epml = factory.createTypeEPML();

        TypeCoordinates coordinates = factory.createTypeCoordinates();
        coordinates.setXOrigin("leftToRight");
        coordinates.setYOrigin("topToBottom");
        epml.setCoordinates(coordinates);

        TypeEPC epc = factory.createTypeEPC();
        try {
            epc.setEpcId(new BigInteger(diagram.getResourceId()));
        } catch (NumberFormatException e) {
            LOGGER.warning("JSON for EPML model had diagram id \"" + diagram.getResourceId() + "\"; substituting 1 instead");
            epc.setEpcId(BigInteger.ONE);
        }
        epc.setName(diagram.getProperty("title"));
        for (BasicShape shape : diagram.getAllShapesReadOnly()) {
            switch (shape.getStencilId()) {
                case "AndConnector":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCAnd(populateElement(factory.createTypeAND(), shape)));
                    break;

                case "ControlFlow":
                case "Relation":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCArc(populateArc(factory.createTypeArc(), (BasicEdge) shape)));
                    break;

                case "Data":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCObject(populateElement(factory.createTypeObject(), shape)));
                    break;

                case "Event":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCEvent(populateElement(factory.createTypeEvent(), shape)));
                    break;

                case "Function":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCFunction(populateElement(factory.createTypeFunction(), shape)));
                    break;

                case "OrConnector":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCOr(populateElement(factory.createTypeOR(), shape)));
                    break;

                case "Position":
                case "Organization":
                case "System":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCRole(populateElement(factory.createTypeRole(), shape)));
                    isRolePresent = true;
                    break;

                case "ProcessInterface":
                    epc.getEventAndFunctionAndRole().add(factory.createTypeEPCProcessInterface(populateElement(factory.createTypeProcessInterface(), shape)));
                    break;

                case "TextNote":
                    LOGGER.warning("EPML Text Note with id " + shape.getResourceId() + " is unsupported and will be ignored.");
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

        // If any roles are present, need to include the attribute definition for "roletype"
        if (isRolePresent) {
            TypeAttrTypes attrTypes = factory.createTypeAttrTypes();
            TypeAttrType attrType = factory.createTypeAttrType();
            attrType.setDescription("Apromore understands \"IT system\" and \"Organizational Unit\", and treats all others as Position");
            attrType.setTypeId("roletype");
            attrTypes.getAttributeType().add(attrType);
            epml.getAttributeTypes().add(attrTypes);
        }

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
    private TypeArc populateArc(final TypeArc arc,
                                final BasicEdge edge) {

        // Find the edge's source
        BasicShape sourceShape = null;
        for (BasicShape incoming: edge.getIncomingsReadOnly()) {
            sourceShape = incoming;
        }
        assert sourceShape != null: "Shape with id " + edge.getResourceId() + " not present in source map";

        // Find the edge's target
        BasicShape targetShape = null;
        for (BasicShape outgoing: edge.getOutgoingsReadOnly()) {
            targetShape = outgoing;
        }
        assert targetShape != null: "Shape with id " + edge.getResourceId() + " not present in target map";

        // Set an arc/@id
        arc.setId(getId(edge.getResourceId()));

        // Set either an arc/flow or arc/relation
        switch (edge.getStencilId()) {
        case "ControlFlow":
            TypeFlow flow = factory.createTypeFlow();
            flow.setSource(getId(sourceShape.getResourceId()));
            flow.setTarget(getId(targetShape.getResourceId()));
            arc.setFlow(flow);
            break;

        case "Relation":
            TypeRelation relation = factory.createTypeRelation();
            relation.setSource(getId(sourceShape.getResourceId()));
            relation.setTarget(getId(targetShape.getResourceId()));
            relation.setType(findRelationType(edge, sourceShape, targetShape));
            arc.setRelation(relation);
            break;

        default:
            throw new RuntimeException("Unsupported arc stencil ID: " + edge.getStencilId());
        }

        // Set an arc/graphics
        TypeMove move = factory.createTypeMove();
        for (int i = 0; i < edge.getDockersReadOnly().size(); i++) {
            Point point = edge.getDockersReadOnly().get(i);
            Point origin = new Point(0, 0);

            // If this edge is connected to a source node, the first waypoint's coordinates are relative to that node
            // If this edge is connected to a target node, the last waypoint's coordinates are relative to that node
            if (i == 0 && sourceShape != null) {
                origin = sourceShape.getBounds().getUpperLeft();
            } else if (i == edge.getDockersReadOnly().size() - 1 && targetShape != null) {
                origin = targetShape.getBounds().getUpperLeft();
            }

            move.getPosition().add(toMove2(point, origin));
        }
        arc.getGraphics().add(move);

        return arc;
    }

    /**
     * @param edge  a JSON edge representing an EPML Relation, never <code>null</code>
     * @param sourceShape  the <var>edge</var>'s source shape, <code>null</code> if not connected
     * @param targetShape  the <var>edge</var>'s target shape, <code>null</code> if not connected
     * @return the relation's type, one of "any", "input", "output" or "role"
     */
    String findRelationType(final BasicEdge edge, final BasicShape sourceShape, final BasicShape targetShape) {

        if (sourceShape != null) {
            switch (sourceShape.getStencilId()) {
            case "Data":
                return "input";
            case "Position":
            case "Organization":
            case "System":
                return "role";
            }
        }

        if (targetShape != null) {
            switch (targetShape.getStencilId()) {
            case "Data":
                return "output";
            case "Position":
            case "Organization":
            case "System":
                return "role";
            }
        }

        return "Yes".equals(edge.getProperty("informationflow")) ? "any" : "role";
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
        element.setDescription(shape.getProperty("description"));
        element.setGraphics(toGraphics(shape.getBounds()));
        // TODO: element.setDefRef(???)

        switch (shape.getStencilId()) {
        case "Data":
            populateObject((TypeObject) element, shape);
            break;

        case "Organization":
            populateRole((TypeRole) element, "Organizational Unit");
            break;

        case "Position":
            // default role when we don't know a roletype, so don't add an attribute element
            break;

        case "ProcessInterface":
            TypeToProcess toProcess = factory.createTypeToProcess();
            toProcess.setLinkToEpcId(new BigInteger("1"));  // TODO: 1 is a dummy value; need to actually populate it
            ((TypeProcessInterface) element).setToProcess(toProcess);
            break;

        case "System":
            populateRole((TypeRole) element, "IT system");
            break;
        }

        return element;
    }

    /**
     * @param object  the element to populate
     * @param shape  the JSON shape corresponding to the <var>object</var>
     */
    private void populateObject(final TypeObject object, final BasicShape shape) {

            object.setType(findObjectType(shape));

            if (shape.hasProperty("isOptional")) {
                object.setOptional(shape.getPropertyBoolean("isOptional"));
            }

            if (shape.hasProperty("isConsumed")) {
                object.setConsumed(shape.getPropertyBoolean("isConsumed"));
            }

            if (shape.hasProperty("isInitial")) {
                object.setInitial(shape.getPropertyBoolean("isInitial"));
            }

            if (shape.hasProperty("isFinal")) {
                object.setFinal(shape.getPropertyBoolean("isFinal"));
            }
    }

    /**
     * @param shape  a JSON shape representing an EPML Object
     * @return either "input" or "output"
     */
    private String findObjectType(final BasicShape shape) {

            // Is this object the target of any relation?
            boolean hasIncomingRelations = false;
            for (BasicShape incoming: shape.getIncomingsReadOnly()) {
                if ("Relation".equals(incoming.getStencilId()) && "Yes".equals(incoming.getProperty("informationflow"))) {
                    hasIncomingRelations = true;
                }
            }

            // Is this object the source of any relation?
            boolean hasOutgoingRelations = false;
            for (BasicShape outgoing: shape.getOutgoingsReadOnly()) {
                if ("Relation".equals(outgoing.getStencilId()) && "Yes".equals(outgoing.getProperty("informationflow"))) {
                    hasOutgoingRelations = true;
                }
            }

            // Determine this object's type based on the relations connected to it, or as a fallback the "type" JSON property
            String type         = null;
            String declaredType = shape.getProperty("type");

            if (hasOutgoingRelations) {
                type = "input";
                if (declaredType != null && !type.equals(declaredType)) {
                    LOGGER.warning("EPML data object with id " + shape.getResourceId() + " changed type from " + declaredType + " to " + type);
                }
                if (hasIncomingRelations) {
                    LOGGER.warning("EPML data object with id " + shape.getResourceId() + " has both incoming and outgoing relations");
                }
            }
            else if (hasIncomingRelations) {
                type = "output";
                if (declaredType != null && !type.equals(declaredType)) {
                    LOGGER.warning("EPML data object with id " + shape.getResourceId() + " changed type from " + declaredType + " to " + type);
                }
                assert !hasOutgoingRelations;
            }
            else if ("input".equals(declaredType) || "output".equals(declaredType)) {
                type = declaredType;
            }
            else {
                type = "input";
                LOGGER.warning("EPML data object with id " + shape.getResourceId() + " had no type; guessing \"input\".");
            }
            assert type != null;

            return type;
    }

    /**
     * Add an <code>&lt;attribute&gt;</code> element to an EPML role; called by {@link populateElement} which
     * sets all the other attributes, so you probably don't want to call this on its own.
     *
     * @param role  the element to populate
     * @param roletypeAttributeValue  the value for the <code>role/attribute/@value</code> XML attribute
     */
    private void populateRole(final TypeRole role, final String roletypeAttributeValue) {
        TypeAttribute attribute = factory.createTypeAttribute();
        attribute.setTypeRef("roletype");
        attribute.setValue(roletypeAttributeValue);
        role.getAttribute().add(attribute);
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
