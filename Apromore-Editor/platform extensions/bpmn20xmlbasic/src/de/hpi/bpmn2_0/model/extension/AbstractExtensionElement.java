package de.hpi.bpmn2_0.model.extension;

import de.hpi.bpmn2_0.model.extension.signavio.SignavioLabel;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMessageName;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioMetaData;
import de.hpi.bpmn2_0.model.extension.signavio.SignavioType;
import de.hpi.bpmn2_0.model.extension.synergia.Configurable;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationAnnotation;
import de.hpi.bpmn2_0.model.extension.synergia.ConfigurationMapping;
import de.hpi.bpmn2_0.model.extension.synergia.Variants;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * Abstract BPMN 2.0 extension element
 *
 * @author Sven Wagner-Boysen
 */
@XmlSeeAlso({
	Configurable.class,
        ConfigurationAnnotation.class,
        ConfigurationMapping.class,
        SignavioMetaData.class,
        SignavioType.class,
        SignavioLabel.class,
        SignavioMessageName.class,
	Variants.class
})
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractExtensionElement {

}
