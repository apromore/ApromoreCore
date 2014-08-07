/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.quaestio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

public class FileProcessModel implements ProcessModel {

        /** A file in C-BPMN format. */
	private File file;

	/**
         * Read a process model from the filesystem. 
	 */
	public FileProcessModel(final File file) {
		/*
		if (file.toString().endsWith(".bpmn")) {
                        schemaValidation.validate(getClass().getResource("/xsd/BPMN20.xsd"), file);
                } else if (fInModel.toString().endsWith(".epml")) {
                        schemaValidation.validate(getClass().getResource("/schema/EPML_2.0.xsd"), file);
                } else if (fInModel.toString().endsWith(".yawl")) {
                        schemaValidation.validate(getClass().getResource("/schema/YAWL_Schema2.2.xsd"), file);
                }
		*/

		this.file = file;
	}

        public BpmnDefinitions getBpmn() throws FileNotFoundException, JAXBException {
		return BpmnDefinitions.newInstance(new FileInputStream(file), true);
	}

	public String getText() {
		return file.getAbsolutePath();
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(final BpmnDefinitions bpmn) throws Exception {
		throw new Exception("Save to file not implemented");
	}
}

