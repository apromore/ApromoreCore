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

