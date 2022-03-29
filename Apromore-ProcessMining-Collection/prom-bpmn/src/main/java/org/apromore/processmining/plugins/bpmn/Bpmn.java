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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Bpmn</b> is a {@link BpmnDefinitions} with added error logging capabilities.
 * @author Anna Kalenkova
 * @author Bruce Nguyen
 *  - 6 April 2020: Remove XLog: it is used for logging purpose but this is not the right use of XLog.
 *	- 19 Oct 2021: add class comments
 */
public class Bpmn extends BpmnDefinitions {
	private final List<String> errorMessages = new ArrayList<>();
	private final List<String> infoMessages = new ArrayList<>();
	private final static int MAX_ERROR_MESSAGES = 20;
	private final static int MAX_INFO_MESSAGES = 20;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Bpmn.class);

	public Bpmn() {
		super("definitions");
	}

	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current BPMN tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Error message.
	 */
	public void log(String context, int lineNumber, String message) {
		String errorMessage = "Tag: " + context +
				". Line number: " + lineNumber +
				". Error: " + message;
		if (errorMessages.size() < MAX_ERROR_MESSAGES) errorMessages.add(errorMessage);
		LOGGER.error("BPMN Import error. " + errorMessage);
	}
	

	/**
	 * Adds a log event to the current trace in the log.
	 * 
	 * @param context
	 *            Context of the message, typically the current BPMN tag.
	 * @param lineNumber
	 *            Current line number.
	 * @param message
	 *            Info message.
	 */
	public void logInfo(String context, int lineNumber, String message) {
		String infoMessage = "Tag: " + context +
				". Line number: " + lineNumber +
				". Info: " + message;
		if (infoMessages.size() < MAX_INFO_MESSAGES) infoMessages.add(infoMessage);
	    LOGGER.info("BPMN Import info. " + infoMessage);
	}
	

	public boolean hasErrors() {
		return !errorMessages.isEmpty();
	}

	public boolean hasInfos() {
		return !infoMessages.isEmpty();
	}

	public String getErrorMessages() {
		return errorMessages.toString()
				.replace(", \"", "\n\"") // end of a message
				.replace("[", "")
				.replace("]", "");
	}

	public String getInfoMessages() {
		return infoMessages.toString()
				.replace(", \"", "\n\"") // end of a message
				.replace("[", "")
				.replace("]", "");
	}
}
