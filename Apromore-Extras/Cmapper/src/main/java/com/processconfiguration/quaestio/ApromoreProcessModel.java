/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.Collections;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apromore.helper.Version;
import org.apromore.manager.client.ManagerService;
import org.apromore.manager.client.ManagerServiceClient;
import org.apromore.model.ExportFormatResultType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

public class ApromoreProcessModel implements ProcessModel {

	private static final String NATIVE_TYPE = "BPMN 2.0";

        /** Handle for interacting with the Apromore server. */
	private ManagerService manager;

	private ExportFormatResultType exportFormatResult;

	private int        processId;
	private String     branch;
	private Version    version;
	private Component  parent;
	private String     user;
	private SaveDialog saveDialog;

	/**
         * Constructor.
         *
         * This reads a process model from an Apromore manager service.
         *
         * @param managerEndpointURI  the externally reachable SOAP endpoint of the Apromore manager, e.g. <code>http://example.com:80/manager/services</code>
         * @param processId           the ID of the process model
         * @param branch              the branch name of the process model
	 * @param versionString       the version number of the process model
         * @param parent              the UI component, used only for aligning dialog windows
         * @param user                the user principal in whose name updates are made
         */
	public ApromoreProcessModel(final URI managerEndpointURI, final int processId, String branch, String versionString, final Component parent, final String user) throws Exception {

                this.manager       = new ManagerServiceClient(managerEndpointURI);
		this.processId     = processId;
		this.branch        = branch;
		this.version       = new Version(versionString);
		this.parent        = parent;
		this.user          = user;

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

		// Determine the default next version and branch
		Version newVersion = new Version(version.getMajor(), version.getMinor() + 1);
		String  newBranch  = branch;

                // Present a UI to validate or edit the next version and branch
		if (saveDialog == null) {
			Container ancestor = SwingUtilities.getAncestorOfClass(Frame.class, parent);
			System.err.println("Quaestio save dialog modal parent is " + ancestor);
			saveDialog = new SaveDialog((Frame) ancestor);
			saveDialog.pack();
		}
		saveDialog.setModel(bpmn);
		saveDialog.setVersion(new Version(version.getMajor(), version.getMinor() + 1));
		saveDialog.setBranch(branch);
		saveDialog.setLocationRelativeTo(parent);
                saveDialog.setVisible(true);
	}

	/**
	 * Update a C-BPMN process model on the Apromore manager service.
         *
         * @param bpmn the updated process model
	 * @param newVersion  the version number of the update
	 * @param newBranch  the branch of the update
	 * @return whether the update was successful
	 */
	boolean commit(final BpmnDefinitions bpmn, final Version newVersion, final String newBranch) {
		try {
			// Serialize
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bpmn.marshal(baos, true);

			// Send to the server
			manager.updateProcess(
				0,			// session code
				user,			// user name
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

			return true;

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

			return false;
		}
	}

	/**
         * Save dialog.
         */
	private class SaveDialog extends JDialog {

		private BpmnDefinitions     model;
		private JFormattedTextField versionField;
		private JTextField          branchField;

		/**
                 * User interface for editing the version and branch before saving the model.
                 *
                 * @param parent  currently ignored, but provided in case we ever want to make the dialog modal
                 */
		SaveDialog(Frame parent) {
			super(parent, false);

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
                                }, null);

			branchField = new JTextField("BRANCH");

			setLayout(new GridBagLayout());
			//setSize(600, 400);
			setTitle("Save");

			// Populate the dialog

			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx   = 0;
				gbc.gridy   = 0;
				gbc.anchor  = GridBagConstraints.WEST;
				gbc.fill    = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 0.1;
				gbc.weighty = 1.0;
				gbc.insets  = new Insets(20, 20, 5, 5); 
				add(new JLabel("Version Number"), gbc);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx   = 1;
				gbc.gridy   = 0;
				gbc.fill    = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1.0;
				gbc.insets  = new Insets(20, 5, 5, 20); 
				add(versionField, gbc);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx  = 0;
				gbc.gridy  = 1;
				gbc.anchor = GridBagConstraints.WEST;
				gbc.fill  = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 0.1;
				gbc.weighty = 1.0;
				gbc.insets  = new Insets(5, 20, 20, 5); 
				add(new JLabel("Branch Name"), gbc);
			}
			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 1;
				gbc.gridy = 1;
				gbc.fill  = GridBagConstraints.HORIZONTAL;
				gbc.weightx = 1.0;
				gbc.insets  = new Insets(5, 5, 20, 20); 
				add(branchField, gbc);
			}

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.add(new JButton(new AbstractAction("Save") {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Saving");
					boolean success = ApromoreProcessModel.this.commit(
						model,
						(Version) versionField.getValue(),
						branchField.getText()
					);
					if (success) {
						SaveDialog.this.setVisible(false);
					}
					System.out.println("Saved");
				}
			}));
			buttonPanel.add(new JButton(new AbstractAction("Cancel") {
				public void actionPerformed(ActionEvent e) {
					SaveDialog.this.setVisible(false);
				}
			}));

			{
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx     = 0;
				gbc.gridy     = 2;
				gbc.gridwidth = 2;
				gbc.anchor    = GridBagConstraints.SOUTHEAST;
				add(buttonPanel, gbc);
			}
		}

		void setModel(final BpmnDefinitions newModel) {
			model = newModel;
		}

		void setVersion(final Version newVersion) {
			versionField.setValue(newVersion);
		}

		void setBranch(final String newBranch) {
			branchField.setText(newBranch);
		}
	}
}

