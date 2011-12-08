package de.hpi.bpmn2_0.transformation;

import java.util.List;
import java.util.Map;

import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.extension.PropertyListItem;

public interface Constants {
	public List<Class<? extends AbstractBpmnFactory>> getAdditionalFactoryClasses();
	public List<Class<? extends PropertyListItem>> getAdditionalPropertyItemClasses();
	public Map<String, String> getCustomNamespacePrefixMappings();
}
