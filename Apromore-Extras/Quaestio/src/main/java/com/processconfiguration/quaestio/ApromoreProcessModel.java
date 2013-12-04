package com.processconfiguration.quaestio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

class ApromoreProcessModel implements ProcessModel {

	private static final String NATIVE_TYPE = "BPMN 2.0";

        /** Handle for interacting with the Apromore server. */
	private ManagerService manager;

	private ExportFormatResultType exportFormatResult;

	private int    processId;
	private String branch;
	private double version;

	/**
         * Constructor.
         *
         * This reads a process model from an Apromore manager service.
         *
         * @param processId  the ID of the process model
         * @param branch     the branch name of the process model
	 * @param version    the version number of the process model
         */
	ApromoreProcessModel(final int processId, String branch, double version) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/managerClientContext.xml");
		manager = (ManagerService) context.getAutowireCapableBeanFactory().getBean("managerClient");

		this.processId = processId;
		this.branch    = branch;
		this.version   = version;

		exportFormatResult = manager.exportFormat(
			processId,		// process ID
			null,                   // process name
			branch,                 // branch
			version,                // version number,
			NATIVE_TYPE,            // nativeType,
			null,                   // annotation name,
			false,                  // with annotations?
			null,			// owner
			Collections.EMPTY_SET   // canoniser properties
		);
		//bpmn = BpmnDefinitions.newInstance(result.getNative().getInputStream(), true /* validate */);

		// Serialize created model for debugging inspection
		//bpmn.marshal(System.out, true);
	}

	public BpmnDefinitions getBpmn() throws Exception {
		return BpmnDefinitions.newInstance(exportFormatResult.getNative().getInputStream(), true /* validate */);
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

		assert manager != null;

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bpmn.marshal(baos, true);

		// Send to the server
		double newVersion = version + 0.1d;
		manager.updateProcess(
			0,			// session code
			null,			// user name
			NATIVE_TYPE,		// native type
			processId,		// process ID
			null,			// domain
			"non-null dummy",	// process name
			branch,			// original branch name
			branch,			// new branch name
			newVersion,		// version number
			version,		// original version number
			null,			// pre-version
			new ByteArrayInputStream(baos.toByteArray())
		);
                version = newVersion;
	}
}

