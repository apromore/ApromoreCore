package com.processconfiguration.quaestio;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

/**
 * Abstracts away where exactly we're reading our process models from.
 */
public interface ProcessModel {

	/**
         * @return the C-BPMN process model
	 */
	BpmnDefinitions getBpmn() throws Exception;

	/**
         * @return the filename of the process model, <code>null</code> if the model has no file
         */
	String getText();

	/**
	 * Durably record any changes to the configurable process model.
         *
         * @param bpmn  the new value of the process model
	 */
	void update(final BpmnDefinitions bpmn) throws Exception;
}

