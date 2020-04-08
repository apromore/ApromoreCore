package org.apromore.processmining.plugins.bpmn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @modifier Bruce Nguyen
 *  - 6 April 2020: Remove XLog: it is used for logging purpose but this is not the right use of XLog.
 *
 */
public class Bpmn extends BpmnDefinitions {

	//private XLog log;
	//private XTrace trace;
	//private XFactory factory;
	//private XExtension conceptExtension;
	//private XExtension organizationalExtension;

	boolean hasErrors;
	boolean hasInfos;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Bpmn.class);

	public Bpmn() {
		super("definitions");

		//initializeLog();
	}

	/**
	 * Creates and initializes a log to throw to the framework when importing
	 * the XPDL file fails.
	 */
//	private void initializeLog() {
//		factory = XFactoryRegistry.instance().currentDefault();
//		conceptExtension = XConceptExtension.instance();
//		organizationalExtension = XOrganizationalExtension.instance();
//		log = factory.createLog();
//		log.getExtensions().add(conceptExtension);
//		log.getExtensions().add(organizationalExtension);
//
//		log("<preamble>");
//
//		hasErrors = false;
//	}

//	public XLog getLog() {
//		return log;
//	}

	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current PNML tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Error message.
	 */
	public void log(String context, int lineNumber, String message) {
		LOGGER.error("BPMN Import error. Tag: " + context + 
		                ". Line number: " + lineNumber + 
		                ". Error: " + message);
		hasErrors = true;
	}
	
//    public void log(String context, int lineNumber, String message) {
//        XAttributeMap attributeMap = new XAttributeMapImpl();
//        attributeMap.put(XConceptExtension.KEY_NAME, factory.createAttributeLiteral(XConceptExtension.KEY_NAME,
//                message, conceptExtension));
//        attributeMap.put(XConceptExtension.KEY_INSTANCE, factory.createAttributeLiteral(XConceptExtension.KEY_INSTANCE,
//                context, conceptExtension));
//        attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
//                XOrganizationalExtension.KEY_RESOURCE, "Line " + lineNumber, organizationalExtension));
//        XEvent event = factory.createEvent(attributeMap);
//        trace.add(event);
//        hasErrors = true;
//        System.out.println(message);
//    }	

	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current PNML tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Error message.
	 */
	public void logInfo(String context, int lineNumber, String message) {
	    LOGGER.info("BPMN Import info. Tag: " + context + 
                ". Line number: " + lineNumber + 
                ". Error: " + message);
		hasInfos = true;
	}
	
//    public void logInfo(String context, int lineNumber, String message) {
//        XAttributeMap attributeMap = new XAttributeMapImpl();
//        attributeMap.put(XConceptExtension.KEY_NAME, factory.createAttributeLiteral(XConceptExtension.KEY_NAME,
//                message, conceptExtension));
//        attributeMap.put(XConceptExtension.KEY_INSTANCE, factory.createAttributeLiteral(XConceptExtension.KEY_INSTANCE,
//                context, conceptExtension));
//        attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
//                XOrganizationalExtension.KEY_RESOURCE, "Line " + lineNumber, organizationalExtension));
//        XEvent event = factory.createEvent(attributeMap);
//        trace.add(event);
//        hasInfos = true;
//    }	

	/**
	 * Adds a new trace with the given name to the log. This trace is now
	 * current.
	 * 
	 * @param name
	 *            The give name.
	 */
//	public void log(String name) {
//		trace = factory.createTrace();
//		log.add(trace);
//		trace.getAttributes().put(XConceptExtension.KEY_NAME,
//				factory.createAttributeLiteral(XConceptExtension.KEY_NAME, name, conceptExtension));
//	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public boolean hasInfos() {
		return hasInfos;
	}
}
