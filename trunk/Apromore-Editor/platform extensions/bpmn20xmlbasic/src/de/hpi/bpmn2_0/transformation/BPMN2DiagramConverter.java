/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
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
 */

package de.hpi.bpmn2_0.transformation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.sun.xml.bind.IDResolver;
import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;
import org.oryxeditor.server.diagram.basic.BasicDiagram;
import org.oryxeditor.server.diagram.basic.BasicShape;

import com.processconfiguration.DefinitionsIDResolver;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.bpmndi.BPMNDiagram;
import de.hpi.bpmn2_0.model.bpmndi.BPMNEdge;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.gateway.Gateway;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationAssociation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotationShape;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;
import org.oryxeditor.server.diagram.generic.GenericJSONBuilder;

/**
 * Converter that transforms BPMN {@link Definitions} to a native {@link BasicDiagram}
 * 
 * @author Sven Wagner-Boysen
 * @author Simon Raboczi (port from Oryx to Signavio)
 */
public class BPMN2DiagramConverter {

	private static final Logger logger = Logger.getLogger(BPMN2DiagramConverter.class.getCanonicalName());
	
	private String rootDir;
	
	public BPMN2DiagramConverter(String rootDir) {
		this.rootDir = rootDir;
	}
	
	public List<BasicDiagram> getDiagramFromBpmn20(Definitions definitions) {

		// Reverse mapping for the bpmnElement attribute
		final Map<BaseElement,DiagramElement> bpmndiMap = new HashMap<BaseElement,DiagramElement>();

		// ResourceIDs of all Messages which are decorators on a MessageFlow rather than residing on the canvas
		final Set<String> messageRefSet = new HashSet<String>();

		// Populate bpmndiMap and messageRefSet
		logger.fine("Populating id map");
		for (BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
			for (DiagramElement element : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
				element.acceptVisitor(new AbstractVisitor() {
					DiagramElement bpmndiElement = null;

					// Populate bpmndiMap
					@Override public void visitBaseElement(BaseElement that) {
						that._diagramElement = bpmndiElement;
						logger.finer(that.getId() + " -> " + bpmndiElement.getId());
						bpmndiMap.put(that, bpmndiElement);
					}

					@Override public void visitMessageFlow(MessageFlow that) {
						if (that.getMessageRef() != null) {
							messageRefSet.add(that.getMessageRef().getId());
						}
					}

					// The next two methods traverse via bpmnElement attributes from the BPMNDI part to the BPMN part of the document
					// Note that BPMNPlane also has a bpmnElement attribute; traversal of this is not implemented

					@Override public void visitBpmnEdge(BPMNEdge that) {
						super.visitBpmnEdge(that);
						bpmndiElement = that;
						assert that.getBpmnElement() != null : that.getId() + " has no bpmnElement attribute";
						that.getBpmnElement().acceptVisitor(this);
					}

					@Override public void visitBpmnShape(BPMNShape that) {
						super.visitBpmnShape(that);
						bpmndiElement = that;
						assert that.getBpmnElement() != null : that.getId() + " has no bpmnElement attribute";
						that.getBpmnElement().acceptVisitor(this);
					}
				});
			}
		}

		// Populate the transient JAXB fields
		logger.fine("Populating transient JAXB fields");
		for (BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
			for (DiagramElement element : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
				logger.finer("Scanning " + element.getId());
				element.acceptVisitor(new AbstractVisitor() {
					BPMNEdge precedingEdge = null;

					@Override public void visitBpmnEdge(BPMNEdge that) {
						precedingEdge = that;
						that.getBpmnElement().acceptVisitor(this);
					}

					@Override public void visitBpmnShape(BPMNShape that) {
						that.getBpmnElement().acceptVisitor(this);
					}

					@Override public void visitEdge(Edge that) {
						logger.finer(that.getId() + ": " + that.getSourceRef() + " -> " + that.getTargetRef());

						// In a valid BPMN document, edges must be connected to both a source and a target.
						// Half-edited JSON diagrams break this constraint, so we warn about them.

						if (that.getSourceRef() == null) {
							logger.warning(that.getId() + " has no sourceRef attribute");
						}
						else {
							that.getSourceRef().getOutgoing().add(that);
							precedingEdge.setSourceElement(bpmndiMap.get(that.getSourceRef()));
						}

						if (that.getTargetRef() == null) {
							logger.warning(that.getId() + " has no targetRef attribute");
						}
						else {
							that.getTargetRef().getIncoming().add(that);
							precedingEdge.setTargetElement(bpmndiMap.get(that.getTargetRef()));
						}
					}

					@Override public void visitBoundaryEvent(BoundaryEvent that) {
						if (that.getAttachedToRef() == null) {
							throw new IllegalArgumentException(that.getId() + " has no attachedToRef attribute");
						}
						logger.info(that.getId() + " attachedTo " + that.getAttachedToRef().getId());
						logger.info(that.getId() + " boundary event refs: " + that.getAttachedToRef().getBoundaryEventRefs());
						logger.info(that.getId() + " attached boundary event refs: " + that.getAttachedToRef().getAttachedBoundaryEvents());
						that.getAttachedToRef().getAttachedBoundaryEvents().addAll(that.getAttachedToRef().getBoundaryEventRefs());
						logger.info(that.getId() + " new attached boundary event refs: " + that.getAttachedToRef().getAttachedBoundaryEvents());
						logger.info(that.getAttachedToRef().getId() + " outgoing " + that.getOutgoing());
					}
				});
			}
		}

		// Create a set of IDs for sequence flows which are configured to be absent
		final Set<SequenceFlow> absentInConfiguration = new HashSet<SequenceFlow>();

		for (BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
			for (DiagramElement element : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
				logger.finer("Re-scanning " + element.getId());
				element.acceptVisitor(new AbstractVisitor() {
					BPMNEdge precedingEdge = null;

					@Override public void visitBpmnEdge(BPMNEdge that) {
						that.getBpmnElement().acceptVisitor(this);
					}

					@Override public void visitBpmnShape(BPMNShape that) {
						that.getBpmnElement().acceptVisitor(this);
					}

					@Override public void visitGateway(Gateway that) {
						ExtensionElements extensionElements = that.getExtensionElements();
						if (extensionElements == null) { return; }
						Configurable configurable = extensionElements.getFirstExtensionElementOfType(Configurable.class);
						if (configurable == null || configurable.getConfiguration() == null) { return; }

						// Source references
						switch (that.getGatewayDirection()) {
						case CONVERGING:
						case MIXED:
							List absentInflows = new ArrayList(that.getIncomingSequenceFlows());
							absentInflows.removeAll(configurable.getConfiguration().getSourceRefs());
							absentInConfiguration.addAll(absentInflows);
							break;
						}

						// Target references
						switch (that.getGatewayDirection()) {
						case DIVERGING:
						case MIXED:
							List absentOutflows = new ArrayList(that.getOutgoingSequenceFlows());
							absentOutflows.removeAll(configurable.getConfiguration().getTargetRefs());
							absentInConfiguration.addAll(absentOutflows);
							break;
						}
					}
				});
			}
		}

		// This will be our return value
		List<BasicDiagram> diagrams = new ArrayList<BasicDiagram>();
		
		logger.fine("Generating JSON diagram from BPMN JAXB");
		for (BPMNDiagram bpmnDiagram : definitions.getDiagram()) {
			logger.fine("BPMN diagram=" + bpmnDiagram + " class=" + bpmnDiagram.getClass());

			BasicDiagram diagram = new BasicDiagram(
				"canvas",						// id
				"BPMNDiagram",						// type
				new StencilSetReference(
					"http://b3mn.org/stencilset/bpmn2.0#",		// stencilSet.ns
					rootDir + "stencilsets//bpmn2.0/bpmn2.0.json"	// stencilSet.url
				)
			);

			// Additional properties
			diagram.addSsextension("http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#");
			diagram.setBounds(new Bounds(new Point(0, 0), new Point(2400, 2000)));
			diagram.setProperty("documentation",      bpmnDiagram.getDocumentation());
			diagram.setProperty("expressionlanguage", "http://www.w3.org/1999/XPath");
			diagram.setProperty("name",               bpmnDiagram.getName());
			diagram.setProperty("orientation",        bpmnDiagram.getOrientation());
			diagram.setProperty("targetnamespace",    "http://www.signavio.com/bpmn20");
			diagram.setProperty("typelanguage",       "http://www.w3.org/2001/XMLSchema");

			/*
			// Handle extension elements
			if (bpmnDiagram.getBPMNPlane().getBpmnElement().getExtensionElements() != null) {
                        	for (AbstractExtensionElement extensionElement : bpmnDiagram.getBPMNPlane().getBpmnElement().getExtensionElements().getAny()) {
					if (extensionElement instanceof Variants) {
						for (Variants.Variant variant : ((Variants) extensionElement).getVariant()) {
							logger.fine("Variant " + variant.getId() + " name=" + variant.getName());
						}
					}
				}
			}
			*/

			// Child elements
			for (DiagramElement element : bpmnDiagram.getBPMNPlane().getDiagramElement()) {
				BPMN2DiagramConverterVisitor visitor = new BPMN2DiagramConverterVisitor(diagram, bpmndiMap, absentInConfiguration);
				element.acceptVisitor(visitor);
				if (!messageRefSet.contains(visitor.getShape().getResourceId())) {
					diagram.addChildShape(visitor.getShape());
				}
			}

			diagrams.add(diagram);
		}

		return diagrams;
	}
    
    public void getBPMN(String bpmnString, String encoding, OutputStream jsonStream) {

        // Parse BPMN from XML to JAXB
        Unmarshaller unmarshaller = null;
        try {
            StreamSource source = new StreamSource(new StringReader(bpmnString));
            unmarshaller = JAXBContext.newInstance(Definitions.class,
                    ConfigurationAnnotationAssociation.class,
                    ConfigurationAnnotationShape.class)
                    .createUnmarshaller();
            unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
            Definitions definitions = unmarshaller.unmarshal(source, Definitions.class).getValue();
            
            //return definitions;



        logger.fine("Parsed BPMN");

        // Convert BPMN to JSON
        BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/signaviocore/editor/");
        List<BasicDiagram> diagrams = converter.getDiagramFromBpmn20(definitions);

        logger.fine("Diagrams=" + diagrams);
        String data = "";
        for(BasicDiagram diagram : diagrams) {
                data = diagram.getString();
                writeJson(data, jsonStream);
                break;
            }
            }catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }  catch (JAXBException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        
    }

	/**
         * Take a BPMN XML file as input and generate an equivalent Signavio JSON file as output.
         *
         * Synergia extensions for configurable BPMN are additionally supported in the input file.
         *
         * @param args  first argument is the path of a BPMN XML file
         */
        public static void main(String[] args) throws JAXBException, JSONException {
            try {
                logger.info("Starting test for " + args[0]);

                // Parse BPMN from XML to JAXB
                Unmarshaller unmarshaller = JAXBContext.newInstance(Definitions.class,
                                                                    ConfigurationAnnotationAssociation.class,
                                                                    ConfigurationAnnotationShape.class)
                                                       .createUnmarshaller();
                unmarshaller.setProperty(IDResolver.class.getName(), new DefinitionsIDResolver());
                Definitions definitions = unmarshaller.unmarshal(new StreamSource(new File(args[0])), Definitions.class)
                                                      .getValue();

                logger.fine("Parsed BPMN");

                // Convert BPMN to JSON
                BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/signaviocore/editor/");
                List<BasicDiagram> diagrams = converter.getDiagramFromBpmn20(definitions);

                logger.fine("Diagrams=" + diagrams);
                for(BasicDiagram diagram : diagrams) System.out.println(diagram.getString());

                logger.info("Completed test for " + args[0]);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    private void writeJson(String json, OutputStream jsonStream) throws JSONException, IOException {
        //BasicDiagram diagram = context.getDiagram(0);
        //JSONObject jsonDiagram = GenericJSONBuilder.parseModel(diagram);
        OutputStreamWriter outWriter = new OutputStreamWriter(jsonStream, "UTF-8");
        outWriter.write(json);
        outWriter.flush();
    }
}
