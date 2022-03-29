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

public class BpmnStandardLoopCharacteristics extends BpmnId {

		/*	
			<xsd:attribute name="testBefore" type="xsd:boolean" default="false"/>
			<xsd:attribute name="loopMaximum" type="xsd:integer" use="optional"/>*/
		
		private String testBefore;
		private String loopMaximum;
		
		public BpmnStandardLoopCharacteristics(String tag) {
			super(tag);
			
			testBefore = null;
			loopMaximum = null;
		}

		protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
			super.importAttributes(xpp, bpmn);
			String value = xpp.getAttributeValue(null, "testBefore");
			if (value != null) {
				testBefore = value;
			}
			value = xpp.getAttributeValue(null, "loopMaximum");
			if (value != null) {
				loopMaximum = value;
			}
		}

		/**
		 * Exports all attributes.
		 */
		protected String exportAttributes() {
			String s = super.exportAttributes();
			if (testBefore != null) {
				s += exportAttribute("testBefore", testBefore);
			} else {
				s += exportAttribute("testBefore", "false");
			}
			if (loopMaximum != null) {
				s += exportAttribute("loopMaximum", loopMaximum);
			}
			return s;
		}
		
		@Override
		protected void checkValidity(Bpmn bpmn) {
			// do not require id
		}
	}

