package com.processconfiguration.quaestio;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.Collections;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apromore.helper.Version;
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

	private int       processId;
	private String    branch;
	private Version   version;
	private Component parent;

	/**
         * Constructor.
         *
         * This reads a process model from an Apromore manager service.
         *
         * @param processId  the ID of the process model
         * @param branch     the branch name of the process model
	 * @param versionString    the version number of the process model
         * @param parent  the UI component, used only for aligning dialog windows
         */
	ApromoreProcessModel(final int processId, String branch, String versionString, final Component parent) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/managerClientContext.xml");
		manager = (ManagerService) context.getAutowireCapableBeanFactory().getBean("managerClient");

		this.processId     = processId;
		this.branch        = branch;
		this.version       = new Version(versionString);
		this.parent        = parent;

		exportFormatResult = manager.exportFormat(
			processId,		// process ID
			null,                   // process name
			branch,                 // branch
			version.toString(),     // version number,
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
         * This presents a {@link SaveDialog} window with which
         * the user can invoke the {@link #commit} method.
         *
         * @param bpmn the updated process model
	 */
	public void update(final BpmnDefinitions bpmn) throws Exception {

                // Generate the default next version and branch
                Version newVersion = new Version(version.getMajor(), version.getMinor() + 1);

                // Present a UI to validate or edit the next version and branch
                JDialog saveDialog = new SaveDialog(bpmn, newVersion, branch);
		saveDialog.setLocationRelativeTo(parent);
                saveDialog.show();
	}

	/**
	 * Update a C-BPMN process model on the Apromore manager service.
         *
         * @param bpmn the updated process model
	 * @param newVersion  the version number of the update
	 * @param newBranch  the branch of the update
	 */
	void commit(final BpmnDefinitions bpmn, final Version newVersion, final String newBranch) {
		try {
			// Serialize
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bpmn.marshal(baos, true);

			// Send to the server
			manager.updateProcess(
				0,			// session code
				null,			// user name
				NATIVE_TYPE,		// native type
				processId,		// process ID
				null,			// domain
				"non-null dummy",	// process name
				branch,			// original branch name
				branch,			// new branch name
				newVersion.toString(),	// version number
				version.toString(),	// original version number
				null,			// pre-version
				new ByteArrayInputStream(baos.toByteArray())
			);

			// Updated process attributes become the current process attributes
			this.version = newVersion;
			this.branch  = newBranch;

		} catch (Exception e) {
			String message = e.getMessage();

			// See if we can give a better explanation
			if (message != null && message.startsWith("More than one result was returned from Query.getSingleResult()")) {
				message = "This version already exists on this branch.\n" +
				          "Select a different version or branch and retry.";
			} else {
				message = "Unable to save model.";
			}

			JOptionPane.showMessageDialog(null, message, "Unable to save model", JOptionPane.ERROR_MESSAGE);
			System.err.println("Cause was " + e.getCause());
			e.printStackTrace();
		}
	}

	/**
         * Save dialog.
         */
	private class SaveDialog extends JDialog {

		private JFormattedTextField versionField;
		private JTextField          branchField;

		SaveDialog(final BpmnDefinitions bpmn, final Version version, final String branch) {

			versionField = new JFormattedTextField(new JFormattedTextField.AbstractFormatterFactory() {
                                        public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
                                                return new JFormattedTextField.AbstractFormatter() {
                                                        public Object stringToValue(String s) throws ParseException {
                                                                return new Version(s);
                                                        }
                                                        public String valueToString(Object o) throws ParseException {
                                                                return (o == null) ? "null" : o.toString();
                                                        }
                                                };
                                        }
                                }, version);

			branchField = new JTextField(branch);

			// Populate the dialog

			JPanel formPanel = new JPanel();
			formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
			formPanel.setLayout(new GridBagLayout());
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx  = 0;
				gbc.gridy  = 0;
				gbc.anchor = GridBagConstraints.WEST;
				formPanel.add(new JLabel("Version Number"), gbc);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 1;
				gbc.gridy = 0;
				gbc.fill  = GridBagConstraints.HORIZONTAL;
				formPanel.add(versionField, gbc);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx  = 0;
				gbc.gridy  = 1;
				gbc.anchor = GridBagConstraints.WEST;
				formPanel.add(new JLabel("Branch Name"), gbc);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 1;
				gbc.gridy = 1;
				gbc.fill  = GridBagConstraints.HORIZONTAL;
				formPanel.add(branchField, gbc);
			}

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.add(new JButton(new AbstractAction("Save") {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Saving");
					ApromoreProcessModel.this.commit(
						bpmn,
						(Version) versionField.getValue(),
						branchField.getText()
					);
					System.out.println("Saved");
				}
			}));
			buttonPanel.add(new JButton(new AbstractAction("Cancel") {
				public void actionPerformed(ActionEvent e) {
					SaveDialog.this.dispose();
				}
			}));

			setLayout(new BorderLayout());
			//setSize(600, 400);
			setTitle("Save");
			add(formPanel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);
			pack();
		}
	}
}

