package com.processconfiguration.quaestio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.bind.JAXBException;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

class FileProcessModel implements ProcessModel {

        /** A file in C-BPMN format. */
	private File file;

	/**
         * Read a process model from the filesystem. 
	 */
	FileProcessModel(final File file) {
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

