package de.hpi.bpmn2_0.factory;

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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.generic.GenericShape;
import org.oryxeditor.server.diagram.label.LabelSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.xml.bind.StringInputStream;

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.configuration.Configuration;
import de.hpi.bpmn2_0.factory.edge.AssociationFactory;
import de.hpi.bpmn2_0.factory.edge.ConversationLinkFactory;
import de.hpi.bpmn2_0.factory.edge.MessageFlowFactory;
import de.hpi.bpmn2_0.factory.edge.SequenceFlowFactory;
import de.hpi.bpmn2_0.factory.node.ChoreographyActivityFactory;
import de.hpi.bpmn2_0.factory.node.ChoreographyParticipantFactory;
import de.hpi.bpmn2_0.factory.node.ConversationFactory;
import de.hpi.bpmn2_0.factory.node.ConversationParticipantFactory;
import de.hpi.bpmn2_0.factory.node.DataObjectFactory;
import de.hpi.bpmn2_0.factory.node.DataStoreFactory;
import de.hpi.bpmn2_0.factory.node.EndEventFactory;
import de.hpi.bpmn2_0.factory.node.GatewayFactory;
import de.hpi.bpmn2_0.factory.node.GroupFactory;
import de.hpi.bpmn2_0.factory.node.ITSystemFactory;
import de.hpi.bpmn2_0.factory.node.IntermediateCatchEventFactory;
import de.hpi.bpmn2_0.factory.node.IntermediateThrowEventFactory;
import de.hpi.bpmn2_0.factory.node.LaneFactory;
import de.hpi.bpmn2_0.factory.node.MessageFactory;
import de.hpi.bpmn2_0.factory.node.ParticipantFactory;
import de.hpi.bpmn2_0.factory.node.ProcessParticipantFactory;
import de.hpi.bpmn2_0.factory.node.StartEventFactory;
import de.hpi.bpmn2_0.factory.node.SubprocessFactory;
import de.hpi.bpmn2_0.factory.node.TaskFactory;
import de.hpi.bpmn2_0.factory.node.TextannotationFactory;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Documentation;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.extension.ExtensionElements;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioLabel;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.model.misc.Auditing;
import de.hpi.bpmn2_0.model.misc.Monitoring;
import de.hpi.bpmn2_0.transformation.Constants;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

/**
 * This is the abstract factory that offers methods to create a process element
 * and a related diagram element from a {@link GenericShape}.
 */
public abstract class AbstractBpmnFactory {

	private static List<Class<? extends AbstractBpmnFactory>> factoryClasses = new ArrayList<Class<? extends AbstractBpmnFactory>>();
	
	/**
	 * Manual initialization of factory classes list. Is there a pattern for automatic initialization
	 * except reading the jar file?
	 */
	static {
		
		/* Standard BPMN 2.0 */
		
		factoryClasses.add(AbstractActivityFactory.class);
		factoryClasses.add(SubprocessFactory.class);
		factoryClasses.add(TaskFactory.class);
		factoryClasses.add(AbstractEdgesFactory.class);
		factoryClasses.add(ConversationLinkFactory.class);
		factoryClasses.add(MessageFlowFactory.class);
		factoryClasses.add(SequenceFlowFactory.class);
		factoryClasses.add(AssociationFactory.class);
		factoryClasses.add(ChoreographyActivityFactory.class);
		factoryClasses.add(ChoreographyParticipantFactory.class);
		factoryClasses.add(ConversationFactory.class);
		factoryClasses.add(ConversationParticipantFactory.class);
		factoryClasses.add(DataObjectFactory.class);
		factoryClasses.add(DataStoreFactory.class);
		factoryClasses.add(EndEventFactory.class);
		factoryClasses.add(GatewayFactory.class);
		factoryClasses.add(GroupFactory.class);
		factoryClasses.add(IntermediateCatchEventFactory.class);
		factoryClasses.add(IntermediateThrowEventFactory.class);
		factoryClasses.add(ITSystemFactory.class);
		factoryClasses.add(LaneFactory.class);
		factoryClasses.add(MessageFactory.class);
		factoryClasses.add(ParticipantFactory.class);
		factoryClasses.add(ProcessParticipantFactory.class);
		factoryClasses.add(StartEventFactory.class);
		factoryClasses.add(TextannotationFactory.class);
	}
	
	public static List<Class<? extends AbstractBpmnFactory>> getFactoryClasses() {
		List<Class<? extends AbstractBpmnFactory>> factories = new ArrayList<Class<? extends AbstractBpmnFactory>>(factoryClasses);
		
		Constants c = Diagram2BpmnConverter.getConstants();
		if(c == null) {
			return factories;
		}
		
		factories.addAll(c.getAdditionalFactoryClasses());
		
		
		return factories;
	}
	
	/**
	 * Creates a process element based on a {@link GenericShape}.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The constructed process element.
	 */
	protected abstract BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException;

	/**
	 * Creates a diagram element based on a {@link GenericShape}.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The constructed diagram element.
	 */
	protected abstract DiagramElement createDiagramElement(GenericShape shape);

	/**
	 * Creates BPMNElement that contains DiagramElement and ProcessElement
	 * 
	 * @param shape
	 *            The resource shape.
	 * @return The constructed BPMN element.
	 */
	public abstract BPMNElement createBpmnElement(GenericShape shape,
			BPMNElement parent) throws BpmnConverterException;

	/**
	 * Sets attributes of a {@link BaseElement} that are common for all
	 * elements.
	 * 
	 * @param element
	 *            The BPMN 2.0 element
	 * @param shape
	 *            The resource shape
	 */
	protected void setCommonAttributes(BaseElement element, GenericShape shape) {
		element.setId(shape.getResourceId());
		
		/* Documentation */
		String documentation = shape.getProperty("documentation");
		if (documentation != null && !(documentation.length() == 0) && element.getDocumentation().size() == 0)
			element.getDocumentation().add(new Documentation(documentation));
		
		/* Common FlowElement attributes */
		if(element instanceof FlowElement) {
			
			/* Auditing */
			String auditing = shape.getProperty("auditing");
			if (auditing != null && !(auditing.length() == 0))
				((FlowElement) element).setAuditing(new Auditing(auditing));
			
			/* Monitoring */
			String monitoring = shape.getProperty("monitoring");
			if (monitoring != null && !(monitoring.length() == 0))
				((FlowElement) element).setMonitoring(new Monitoring(monitoring));
			
			/* Name */
			String name = shape.getProperty("name");
			if(name != null && !(name.length() == 0)) {
				((FlowElement) element).setName(name);
			}
		}
	}

	/**
	 * Sets common fields for the visual representation.
	 * 
	 * @param diaElement
	 *            The BPMN 2.0 diagram element
	 * @param shape
	 *            The resource shape
	 */
	protected void setVisualAttributes(DiagramElement diaElement, GenericShape shape) {
		diaElement.setId(shape.getResourceId() + "_gui");
	}

	protected BaseElement invokeCreatorMethod(GenericShape shape)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, BpmnConverterException {

		/* Retrieve the method to create the process element */
		for (Method method : Arrays
				.asList(this.getClass().getMethods())) {
			StencilId stencilIdA = method.getAnnotation(StencilId.class);
			if (stencilIdA != null
					&& Arrays.asList(stencilIdA.value()).contains(
							shape.getStencilId())) {
				/* Create element with appropriate method */
				BaseElement createdElement = (BaseElement) method.invoke(this,
						shape);
				/* Invoke generalized method to set common element attributes */
				this.setCommonAttributes(createdElement, shape);
				
				return createdElement;
			}
		}

		throw new BpmnConverterException("Creator method for shape with id "
				+ shape.getStencilId() + " not found");
	}

	protected BaseElement invokeCreatorMethodAfterProperty(GenericShape shape)
			throws BpmnConverterException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {

		for (Method method : Arrays
				.asList(this.getClass().getMethods())) {
			Property property = method.getAnnotation(Property.class);

			if (property != null
					&& Arrays.asList(property.value()).contains(
							shape.getProperty(property.name()))) {
				
				/* Create element */
				BaseElement createdElement = (BaseElement) method.invoke(this,
						shape);
				/* Invoke generalized method to set common element attributes */
				this.setCommonAttributes(createdElement, shape);
				
				return createdElement;
			}
		}

		throw new BpmnConverterException("Creator method for shape with id "
				+ shape.getStencilId() + " not found");
	}
	
	
	public BPMNElement createBpmnElement(GenericShape shape, Configuration configuration) throws BpmnConverterException {
		BPMNElement bpmnElement = createBpmnElement(shape, new BPMNElement(null, null, null));
		
		if(bpmnElement != null && bpmnElement.getNode() != null) {
			bpmnElement.getNode()._diagramElement = bpmnElement.getShape();
			
			setCustomAttributes(shape, bpmnElement.getNode(), configuration.getMetaData());
			
			// handle external extension elements like from Activiti
//			try {
//				reinsertExternalExtensionElements(shape, bpmnElement);
//			} catch (Exception e) {
//				
//			}
			
			// Apply processid from shape used for round tripping if existing
			if(bpmnElement.getNode() instanceof FlowElement) {
				((FlowElement) bpmnElement.getNode()).setProcessid(shape.getProperty("processid"));
			}
		}
		
		return bpmnElement;
	}
	
	private void setCustomAttributes(GenericShape shape, BaseElement node, Map<String, Set<String>> metaData) {
		if(shape == null || node == null || metaData == null) 
			return;
		
		Set<String> attributeNames = metaData.get(shape.getStencilId());
		if(attributeNames == null) {
			return;
		}
		
		ExtensionElements extElements = node.getOrCreateExtensionElements();
		
		Iterator<String> iterator = attributeNames.iterator();
		while(iterator.hasNext()) {
			String attributeKey = iterator.next();
			String attributeValue = shape.getProperty(attributeKey);
			
			/* Avoid undefined Signavio meta attributes */
			if(attributeValue == null) {
				continue;
			}
			
			SignavioMetaData sigMetaData = new SignavioMetaData(attributeKey, attributeValue);
			
			extElements.getAny().add(sigMetaData);
		}
	}
	
	/**
	 * Checks if the shapes has content in the externalextensionelements 
	 * property and writes those XML Elements back to the extension elements
	 * part of each element.
	 * 
	 * @param shape
	 * @param el
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	protected void reinsertExternalExtensionElements(GenericShape shape, BPMNElement el) throws ParserConfigurationException, SAXException, IOException {
		reinsertOtherAttributes(shape, el);
		
		String exElXml = shape.getProperty("externalextensionelements");
		if(exElXml == null || exElXml.length() == 0) 
			return;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		StringInputStream sis = new StringInputStream(exElXml);
		Document exDoc = builder.parse(sis);
		if(!exDoc.getFirstChild().getNodeName().equals("external")) {
			return;
		}
		
		Node n = exDoc.getFirstChild().getFirstChild();
		while(n != null) {
			if(n instanceof Element) {
				el.getNode().getOrCreateExtensionElements().getAnyExternal().add((Element) n);
				findNamespaceURIs((Element) n, el);
			}
			n = n.getNextSibling();
		}
		
	}
	
	/**
	 * Parse attributes being intended for export under other namespace.
	 * 
	 * @param shape
	 * @param el
	 */
	private void reinsertOtherAttributes(GenericShape shape, BPMNElement el) {
		String otherAttrStr = shape.getProperty("otherattributes");
		if(otherAttrStr == null || otherAttrStr.length() == 0) {
			return;
		}
		
		// process as json array containing json objects
		try {
			JSONArray a = new JSONArray(otherAttrStr);
			for(int i = 0; i < a.length(); i++) {
				JSONObject o = a.getJSONObject(i);
				String localpart = o.optString("localpart");
				String ns = o.optString("ns");
				String prefix = o.optString("prefix");
				String value = o.optString("value");
				
				if((localpart != null || ns != null || prefix != null)
						&& value != null) {
					el.getNode().getOtherAttributes().put(new QName((ns != null ? ns : ""), (localpart != null ? localpart : ""), (prefix != null ? prefix : "")), value);
				}
			}
			
		} catch (JSONException e) {
		}
	}
	
	private void findNamespaceURIs(Element element, BPMNElement el) {
//		Map<String,String> nsMapping = new HashMap<String, String>();
		
		if(element.getPrefix() != null && element.getPrefix().length() > 0 
				&& element.getNamespaceURI() != null 
				&& element.getNamespaceURI().length() > 0) {
			
			el.getExternalNamespaceDefinitions().put(element.getNamespaceURI(), element.getPrefix());
			
			// remove local ns definition
			element.removeAttribute("xmlns:" + element.getPrefix());
			element.getAttribute("xmlns:" + element.getPrefix());
			
		}
		
//		return nsMapping;
	}
	
	protected void setLabelPositionInfo(GenericShape<?,?> shape, BaseElement node) {
		if(shape == null || node == null || shape.getLabelSettings().isEmpty()) {
			return;
		}
		
		ExtensionElements extElements = node.getOrCreateExtensionElements();
		
		for(LabelSettings settings : shape.getLabelSettings()) {
			SignavioLabel label = new SignavioLabel(settings.getSettingsMap());
			extElements.getAny().add(label);
		}
	}
	
}
