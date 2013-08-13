package de.hpi.bpmn2_0.factory.node;

import java.util.Map;

import org.oryxeditor.server.diagram.generic.GenericShape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractShapeFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.model.BaseElement;

/**
 * Factory to create configuration annotation artifacts.
 *
 * Because these don't actually occur in BPMN, this factory simply returns <code>null</code>.
 *
 * @author Simon Raboczi
 */
@StencilId("ConfigurationAnnotation")
public class ConfigurationAnnotationFactory extends AbstractShapeFactory {

	/**
	 * @return <code>null</code> always, since configuration annotations don't actually exist in BPMN
	 */
	@Override public BPMNElement createBpmnElement(GenericShape shape, BPMNElement parent, State state) {
		return null;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override protected BaseElement createProcessElement(GenericShape shape) throws BpmnConverterException {
		throw new BpmnConverterException("Configuration annotation elements don't exist in BPMN");
	}
}
