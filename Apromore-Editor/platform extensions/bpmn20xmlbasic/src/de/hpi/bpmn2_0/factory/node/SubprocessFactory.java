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

package de.hpi.bpmn2_0.factory.node;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.Property;
import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractActivityFactory;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.factory.configuration.Configuration;
import de.hpi.bpmn2_0.factory.configuration.LinkedModel;
import de.hpi.bpmn2_0.model.AdHocOrdering;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FormalExpression;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.AdHocSubProcess;
import de.hpi.bpmn2_0.model.activity.CallActivity;
import de.hpi.bpmn2_0.model.activity.SubProcess;
import de.hpi.bpmn2_0.model.activity.Transaction;
import de.hpi.bpmn2_0.model.activity.TransactionMethod;
import de.hpi.bpmn2_0.model.artifacts.Artifact;
import de.hpi.bpmn2_0.model.bpmndi.BPMNShape;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;

/**
 * Factory to handle all types subprocesses in a process diagram
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"CollapsedSubprocess",
	"Subprocess",
	"CollapsedEventSubprocess",
	"EventSubprocess"
})
public class SubprocessFactory extends AbstractActivityFactory {
//	@Override
	public BPMNElement createBpmnElement(GenericShape shape, Configuration configuration) throws BpmnConverterException {
		BPMNElement bpmnElement = super.createBpmnElement(shape, configuration);
		
		if(bpmnElement != null && bpmnElement.getNode() != null) {
			handleLinkedDiagrams(bpmnElement.getNode(), shape, configuration);
		}
		
		return bpmnElement;
	}

	// @Override
	protected BPMNShape createDiagramElement(GenericShape shape) {
		
		BPMNShape bpmnShape = super.createDiagramElement(shape);
		
		/* Mark as collapsed or expanded */
		if(shape.getStencilId().matches(".*Collapsed.*")) {
			bpmnShape.setIsExpanded(false);
		} else {
			bpmnShape.setIsExpanded(true);
		}
		
		return bpmnShape;
	}
	
	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	// @Override
	protected BaseElement createProcessElement(GenericShape shape)
			throws BpmnConverterException {
		Activity subprocess = null;
		try {
			subprocess = (Activity) this.invokeCreatorMethodAfterProperty(shape);
			this.createLoopCharacteristics(subprocess, shape);
		} catch (Exception e) {
//			throw new BpmnConverterException("Error creating subprocess elements.", e);
		} 
		
		if(subprocess == null) 
			subprocess = new SubProcess();
		
		this.setStandardAttributes(subprocess, shape);
		
		/* Mark as event subprocess */
		if(!(subprocess instanceof CallActivity)) {
			if(shape.getStencilId().matches(".*EventSubprocess.*")) {
				((SubProcess) subprocess).setTriggeredByEvent(true);
			} else {
				((SubProcess) subprocess).setTriggeredByEvent(false);
			}
		}
		
		subprocess.setName(shape.getProperty("name"));
		
		return subprocess;
	}
	
	@Property(name = "callacitivity", value = "true")
	public CallActivity createCallActivity(GenericShape shape) {
		CallActivity callAct = new CallActivity();
		this.setStandardAttributes(callAct, shape);
		return callAct;
	}
	
	@Property(name = "isatransaction", value = "true")
	public Transaction createTransaction(GenericShape shape) {
		Transaction transaction = new Transaction();
		transaction.setMethod(TransactionMethod.fromValue(shape.getProperty("transactionMethod")));
		return transaction;
	}
	
	@Property(name = "isadhoc", value = "true")
	public AdHocSubProcess createAdhocSubprocess(GenericShape shape) {
		AdHocSubProcess adhocSub = new AdHocSubProcess();
		/* Mapping of properties */
		String condition = shape.getProperty("adhoccompletioncondition");
		if(condition != null && !(condition.length() == 0)) 
			adhocSub.setCompletionCondition(new FormalExpression(condition));
		
		String ordering = shape.getProperty("adhocordering");
		if(ordering != null) {
			adhocSub.setOrdering(AdHocOrdering.fromValue(shape.getProperty("adhocordering")));
		}
		
		String cancelRemIns = shape.getProperty("adhoccancelremaininginstances");
		if(cancelRemIns != null)
			adhocSub.setCancelRemainingInstances(!cancelRemIns.equalsIgnoreCase("false"));
		
		return adhocSub;
	}
	
	/**
	 * Transforms linked diagrams of collapsed subprocess and event subprocess.
	 * 
	 * @param baseElement
	 * @param shape
	 * @param config
	 */
	private void handleLinkedDiagrams(BaseElement baseElement, GenericShape shape, Configuration config) {
		if(baseElement == null 
				|| !((baseElement instanceof SubProcess) || (baseElement instanceof CallActivity)) 
				|| !shape.getStencilId().matches(".*Collapsed.*")) {
			return;
		}
		
		/*
		 * Diagram Link
		 */
		String entry = shape.getProperty("entry");
		if(entry == null || entry.length() == 0) {
			return;
		}
		
		SignavioMetaData metaData = new SignavioMetaData("entry", entry);
		baseElement.getOrCreateExtensionElements().add(metaData);
		
		Definitions linkedDiagram = retrieveDefinitionsOfLinkedDiagram(entry, config);
		
		if(linkedDiagram == null || linkedDiagram.getRootElement().size() == 0) {
			return;
		}
		
		/*
		 * Handle call activity
		 */
		if(baseElement instanceof CallActivity) {
			CallActivity ca = (CallActivity) baseElement;
			
			/*
			 * Assign called process as called element
			 */
			for(BaseElement rootEl : linkedDiagram.getRootElement()) {
				if(rootEl instanceof Process) {
					Process p = (Process) rootEl;
					ca.setCalledElement(p);
					break;
				}
			}
			
			if(linkedDiagram.getDiagram().size() > 0) {
				ca._diagramElement = linkedDiagram.getDiagram().get(0);
			}
			
			return;
		}
		
		/*
		 * Handle sub process
		 */
		
		SubProcess subProcess = (SubProcess) baseElement;
		for(BaseElement rootEl : linkedDiagram.getRootElement()) {
			if(rootEl instanceof Process) {
				Process p = (Process) rootEl;
				subProcess.getFlowElement().addAll(p.getFlowElement());
				subProcess.getArtifact().addAll(p.getArtifact());
			}
		}
		
		/* Retrieve diagram elements */
		for(FlowElement element : subProcess.getFlowElement()) {
			subProcess._diagramElements.add(element._diagramElement);
			
//			if(element instanceof SubProcess) {
//				subProcess._diagramElements.addAll(((SubProcess) element)._diagramElements);
//			}
		}
		
		for(Artifact artifact : subProcess.getArtifact()) {
			subProcess._diagramElements.add(artifact._diagramElement);
		}
		
		
	}
	
	/**
	 * Calculation the BPMN 2.0 Definitions element of the linked diagram.
	 * 
	 * @return
	 */
	protected static Definitions retrieveDefinitionsOfLinkedDiagram(String url, Configuration configuration) {
		int lastSlashIndex = url.lastIndexOf("/");
		LinkedModel linkedModel = configuration.getLinkedModels().get(url.substring(lastSlashIndex + 1));
		if(linkedModel == null || linkedModel.getDiagram() == null || !linkedModel.getDiagram().getStencilsetRef().getNamespace().matches("http://b3mn\\.org/stencilset/bpmn2\\.0.*")) {
			return null;
		}
		
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(linkedModel.getLinkedModels(), linkedModel.getDiagram(), AbstractBpmnFactory.getFactoryClasses());
		
		try {
			return converter.getDefinitionsFromDiagram();
		} catch (BpmnConverterException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
