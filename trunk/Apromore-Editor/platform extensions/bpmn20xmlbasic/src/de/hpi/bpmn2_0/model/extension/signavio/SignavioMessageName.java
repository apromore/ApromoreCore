package de.hpi.bpmn2_0.model.extension.signavio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import de.hpi.bpmn2_0.model.extension.AbstractExtensionElement;

/**
 * The BPMN 2.0 Spec Version 2010-06-04 does not offer to store the name of 
 * a message visible on a choreography participant. Therefore the name property
 * is stored as an signavio extension element.
 * 
 * @author Sven Wagner-Boysen
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SignavioMessageName extends AbstractExtensionElement {
	@XmlAttribute
	private String name;
	
	public SignavioMessageName() {
		super();
	}
	
	public SignavioMessageName(String name) {
		super();
		this.name = name;
	}
	
	/* Getter & Setter */

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
