/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.quaestio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import javax.xml.bind.DatatypeConverter;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

class UrlProcessModel implements ProcessModel {

        private URL url;
	//private BpmnDefinitions bpmn;

	/**
         * Constructor.
         *
         * This reads a process model from a URL.
         *
         * @param urlString
         */
	UrlProcessModel(final String urlString) throws Exception {
		url = new URL(urlString);
                //URLConnection connection = url.openConnection();
                //connection.setRequestProperty("Authorization", "Basic " + DatatypeConverter.printBase64Binary("admin:password".getBytes()));
		//bpmn = BpmnDefinitions.newInstance(connection.getInputStream(), true /* validate */);
	}

	public BpmnDefinitions getBpmn() throws Exception {
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " + DatatypeConverter.printBase64Binary("admin:password".getBytes()));
		return BpmnDefinitions.newInstance(connection.getInputStream(), true /* validate */);
	}

	public String getText() {
		return null;
	}

	/**
	 * Update a C-BPMN process model on the Apromore manager service.
         *
         * @param bpmn the updated process model
	 */
	public void update(final BpmnDefinitions bpmn) throws Exception {

		//assert manager != null;

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bpmn.marshal(baos, true);

		// Send to the server
		/*
		double newVersion = version + 0.1d;
		manager.updateProcess(
			0,		// session code
			null,		// user name
			NATIVE_TYPE,	// native type
			processId,	// process ID
			null,		// domain
			null,		// process name
			branch,		// original branch name
			branch,		// new branch name
			newVersion,	// version number
			version,	// original version number
			null,		// pre-version
			new ByteArrayInputStream(baos.toByteArray())
		);
                version = newVersion;
		*/
	}
}

