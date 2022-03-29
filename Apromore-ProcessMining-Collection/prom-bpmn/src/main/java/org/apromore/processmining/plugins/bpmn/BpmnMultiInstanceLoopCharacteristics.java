/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.processmining.plugins.bpmn;

import org.xmlpull.v1.XmlPullParser;

public class BpmnMultiInstanceLoopCharacteristics extends BpmnId {

	/*<xsd:attribute name="isSequential" type="xsd:boolean" default="false"/>
	<xsd:attribute name="behavior" type="tMultiInstanceFlowCondition" default="All"/>
	<xsd:attribute name="oneBehaviorEventRef" type="xsd:QName" use="optional"/>
	<xsd:attribute name="noneBehaviorEventRef" type="xsd:QName" use="optional"/>*/
	
	private String isSequential;
	private String behavior;
	private String oneBehaviorEventRef;
	private String noneBehaviorEventRef;
	
	public BpmnMultiInstanceLoopCharacteristics(String tag) {
		super(tag);
		
		isSequential = null;
		behavior = null;
		oneBehaviorEventRef = null;
		noneBehaviorEventRef = null;
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "isSequential");
		if (value != null) {
			isSequential = value;
		}
		value = xpp.getAttributeValue(null, "behavior");
		if (value != null) {
			behavior = value;
		}
		value = xpp.getAttributeValue(null, "oneBehaviorEventRef");
		if (value != null) {
			oneBehaviorEventRef = value;
		}
		value = xpp.getAttributeValue(null, "noneBehaviorEventRef");
		if (value != null) {
			noneBehaviorEventRef = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (isSequential != null) {
			s += exportAttribute("isSequential", isSequential);
		}
		if (behavior != null) {
			s += exportAttribute("behavior", behavior);
		}
		if (oneBehaviorEventRef != null) {
			s += exportAttribute("oneBehaviorEventRef", oneBehaviorEventRef);
		}
		if (noneBehaviorEventRef != null) {
			s += exportAttribute("noneBehaviorEventRef", noneBehaviorEventRef);
		}
		return s;
	}
	
	@Override
	protected void checkValidity(Bpmn bpmn) {
		// do not require id
	}
}
