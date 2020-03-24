package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.idname.XpdlPackageType;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;

public class Xpdl extends XpdlPackageType {

	private XLog log;
	private XTrace trace;
	private XFactory factory;
	private XExtension conceptExtension;
	private XExtension organizationalExtension;

	boolean hasErrors;
	boolean hasInfos;

	public Xpdl() {
		super("Package");

		initializeLog();
	}

	/**
	 * Creates and initializes a log to throw to the framework when importing
	 * the XPDL file fails.
	 */
	private void initializeLog() {
		factory = XFactoryRegistry.instance().currentDefault();
		conceptExtension = XConceptExtension.instance();
		organizationalExtension = XOrganizationalExtension.instance();
		log = factory.createLog();
		log.getExtensions().add(conceptExtension);
		log.getExtensions().add(organizationalExtension);

		log("<preamble>");

		hasErrors = false;
	}

	public XLog getLog() {
		return log;
	}

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
		XAttributeMap attributeMap = new XAttributeMapImpl();
		attributeMap.put(XConceptExtension.KEY_NAME, factory.createAttributeLiteral(XConceptExtension.KEY_NAME,
				message, conceptExtension));
		attributeMap.put(XConceptExtension.KEY_INSTANCE, factory.createAttributeLiteral(XConceptExtension.KEY_INSTANCE,
				context, conceptExtension));
		attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
				XOrganizationalExtension.KEY_RESOURCE, "Line " + lineNumber, organizationalExtension));
		XEvent event = factory.createEvent(attributeMap);
		trace.add(event);
		hasErrors = true;
	}

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
		XAttributeMap attributeMap = new XAttributeMapImpl();
		attributeMap.put(XConceptExtension.KEY_NAME, factory.createAttributeLiteral(XConceptExtension.KEY_NAME,
				message, conceptExtension));
		attributeMap.put(XConceptExtension.KEY_INSTANCE, factory.createAttributeLiteral(XConceptExtension.KEY_INSTANCE,
				context, conceptExtension));
		attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
				XOrganizationalExtension.KEY_RESOURCE, "Line " + lineNumber, organizationalExtension));
		XEvent event = factory.createEvent(attributeMap);
		trace.add(event);
		hasInfos = true;
	}

	/**
	 * Adds a new trace with the given name to the log. This trace is now
	 * current.
	 * 
	 * @param name
	 *            The give name.
	 */
	public void log(String name) {
		trace = factory.createTrace();
		log.add(trace);
		trace.getAttributes().put(XConceptExtension.KEY_NAME,
				factory.createAttributeLiteral(XConceptExtension.KEY_NAME, name, conceptExtension));
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public boolean hasInfos() {
		return hasInfos;
	}
}
