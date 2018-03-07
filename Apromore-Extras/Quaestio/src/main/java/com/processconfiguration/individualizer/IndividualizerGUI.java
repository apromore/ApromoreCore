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
package com.processconfiguration.individualizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.processconfiguration.utils.EPMLFilter;
import com.processconfiguration.utils.Utils;
import com.processconfiguration.utils.schemaValidation;

public class IndividualizerGUI extends JFrame {// @jve:decl-index=0:visual-constraint="3,12"

	/**
	 * 
	 */
	private static final long serialVersionUID = -7439227300328195597L;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu helpMenu = null;
	private JMenuItem aboutMenuItem = null;
	private JDialog aboutDialog = null; // @jve:decl-index=0:visual-constraint="543,12"
	private JPanel aboutContentPane = null;
	private JTextArea aboutVersionArea = null;
	private JPanel jPanel_commit = null;
	private JButton jButton_commit = null;
	private JPanel jPanel_model = null;
	private JButton jButton_model = null;
	private JTextField jTextField_model = null;
	private String extension = "epml";
	File fInModel = null;
	JFileChooser fileChooser = null;
	private JPanel jPanel_status = null;
	private JLabel jLabel_status = null;

	/**
	 * This method initializes jPanel_commit
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_commit() {
		if (jPanel_commit == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.NONE;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints11.ipadx = 0;
			gridBagConstraints11.gridy = 0;
			jPanel_commit = new JPanel();
			jPanel_commit.setLayout(new GridBagLayout());
			jPanel_commit.add(getJButton_commit(), gridBagConstraints11);
		}
		return jPanel_commit;
	}

	protected void enableCommitButton() {
		if ((fInModel != null)) {
			if (extension.equals("yawl"))
				jButton_commit.setText("Generate YAWL net");
			if (extension.equals("epml"))
				jButton_commit.setText("Generate EPC");
			getJButton_commit().setEnabled(true);
		}
	}

	/**
	 * This method initializes jButton_commit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_commit() {
		if (jButton_commit == null) {
			jButton_commit = new JButton();
			jButton_commit.setText("Generate model");
			jButton_commit.setIcon(new ImageIcon(getClass().getResource(
					"/icons/save_as.png")));
			jButton_commit.setMaximumSize(new Dimension(170, 25));
			jButton_commit.setMinimumSize(new Dimension(170, 25));
			jButton_commit.setPreferredSize(new Dimension(170, 25));
			jButton_commit.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_commit.setEnabled(false);
			jButton_commit
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								if (extension.equals("yawl")) {
									schemaValidation.validate(getClass().getResource("/schema/YAWL_Schema2.2.xsd"),fInModel);
									IndividualizerYAWL pp = new IndividualizerYAWL(fInModel);
									//String fOutName = fInModel.getPath().substring(0,fInModel.getPath().length() - 5) + "_individualized.yawl"; NOTE: not shown as user can set path for individualized file
									if (pp.commit() == null)//if (pp.commit() != null)
										//jLabel_status.setText("YAWL net succesflly generated.\n Output saved in: " + fOutName);
									//else
										jLabel_status.setText("Generation failed!");
								}
								if (extension.equals("epml")) {
									schemaValidation.validate(getClass().getResource("/schema/EPML_2.0.xsd"),fInModel);
									IndividualizerEPC pp = new IndividualizerEPC(fInModel);
									//String fOutName = fInModel.getPath().substring(0,fInModel.getPath().length() - 5) + "_individualized.epml"; NOTE: not shown as user can set path for individualized file
									if (pp.commit() == null)//if (pp.commit() != null)
										//jLabel_status.setText("EPC succesflly generated.\n Output saved in: " + fOutName);
									//else
										jLabel_status.setText("Generation failed!");
								}
							} catch (Exception e1) {
								jTextField_model
										.setText("Format not valid! Please check.");
								System.out.println(e1.getMessage());
							}
						}

					});
		}
		return jButton_commit;
	}

	/**
	 * This method initializes jPanel_model
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_model() {
		if (jPanel_model == null) {
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new Insets(2, 4, 2, 2);
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.anchor = GridBagConstraints.CENTER;
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.NONE;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.weightx = 0.0;
			gridBagConstraints1.anchor = GridBagConstraints.CENTER;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			jPanel_model = new JPanel();
			jPanel_model.setLayout(new GridBagLayout());
			jPanel_model.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEtchedBorder(EtchedBorder.RAISED),
							"Configured Model",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 11), new Color(51, 94, 168)),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));
			jPanel_model.add(getJButton_model(), gridBagConstraints1);
			jPanel_model.add(getJTextField_model(), gridBagConstraints5);
		}
		return jPanel_model;
	}

	/**
	 * This method initializes jButton_model
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_model() {
		if (jButton_model == null) {
			jButton_model = new JButton();
			jButton_model.setIcon(new ImageIcon(getClass().getResource(
					"/icons/open.png")));
			jButton_model.setMinimumSize(new Dimension(32, 25));
			jButton_model.setMaximumSize(new Dimension(32, 25));
			jButton_model.setMnemonic(KeyEvent.VK_UNDEFINED);
			jButton_model.setPreferredSize(new Dimension(32, 25));
			jButton_model
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							fileChooser = new JFileChooser(".");
							fileChooser.setFont(new java.awt.Font("Dialog",
									java.awt.Font.PLAIN, 11));
							fileChooser.setLocation(100, 100);
							fileChooser
									.addChoosableFileFilter(new EPMLFilter());
							int returnVal = fileChooser
									.showOpenDialog(IndividualizerGUI.this);

							if (returnVal == JFileChooser.APPROVE_OPTION) {
								fInModel = fileChooser.getSelectedFile();
								getJTextField_model().setText(
										fInModel.getAbsolutePath());
								extension = Utils.getExtension(fInModel);
							}
							enableCommitButton();
						}
					});
		}
		return jButton_model;
	}

	/**
	 * This method initializes jTextField_model
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_model() {
		if (jTextField_model == null) {
			jTextField_model = new JTextField();
			jTextField_model.setMinimumSize(new Dimension(80, 25));
			jTextField_model.setMaximumSize(new Dimension(2147483647, 25));
			jTextField_model.setEditable(false);
			jTextField_model.setPreferredSize(new Dimension(80, 25));
		}
		return jTextField_model;
	}

	/**
	 * This method initializes jPanel_status
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_status() {
		if (jPanel_status == null) {
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.insets = new Insets(2, 4, 2, 4);
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.fill = GridBagConstraints.BOTH;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.weighty = 1.0;
			gridBagConstraints13.gridheight = 2;
			gridBagConstraints13.anchor = GridBagConstraints.NORTH;
			gridBagConstraints13.gridx = 0;
			jLabel_status = new JLabel();
			jLabel_status.setText("  Output message: ");
			jLabel_status.setFont(new Font("Dialog", Font.PLAIN, 11));
			jLabel_status.setMinimumSize(new Dimension(0, 43));
			jLabel_status.setMaximumSize(new Dimension(0, 43));
			jLabel_status.setPreferredSize(new Dimension(0, 43));
			jPanel_status = new JPanel();
			jPanel_status.setLayout(new GridBagLayout());
			jPanel_status.setBorder(BorderFactory.createLineBorder(new Color(
					212, 208, 200), 1));
			// jPanel_status.setPreferredSize(new Dimension(10, 25));
			// jPanel_status.setMinimumSize(new Dimension(10, 25));
			// jPanel_status.setMaximumSize(new Dimension(2147483647, 25));
			jPanel_status.setAlignmentX(0.0F);
			jPanel_status.setAlignmentY(0.0F);
			jPanel_status.add(jLabel_status, gridBagConstraints13);
		}
		return jPanel_status;
	}

/*	*//**
	 * @param args
	 *//*
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				IndividualizerGUI application = new IndividualizerGUI();
				application.setVisible(true);
			}
		});
	}*/

	public IndividualizerGUI() {
		super();
		initialize();
	}

	private void initialize() {
		this.setLocation(new Point(100, 100));
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setMinimumSize(new java.awt.Dimension(500, 190));
		this.setPreferredSize(new java.awt.Dimension(500, 190));
		this.setSize(500, 186);
		this.setContentPane(getJContentPane());
		this.setTitle("Synergia - Process Individualizer 0.5");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/icons/PP_16.gif")));

	}

	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	// private JFrame getJFrame() {
	// if (jFrame == null) {
	// jFrame = new JFrame();
	// jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// jFrame.setJMenuBar(getJJMenuBar());
	// jFrame.setSize(503, 282);
	// jFrame.setContentPane(getJContentPane());
	// jFrame.setTitle("Model2Questionnaire Tool");
	// }
	// return jFrame;
	// }

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.weighty = 1.0;
			gridBagConstraints21.ipady = 0;
			gridBagConstraints21.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints21.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints21.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.gridy = 6;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 0.3;
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.insets = new Insets(2, 4, 4, 4);
			gridBagConstraints6.anchor = GridBagConstraints.NORTH;
			gridBagConstraints6.ipadx = 0;
			gridBagConstraints6.ipady = 0;
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.ipady = 0;
			gridBagConstraints2.ipadx = 38;
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.gridheight = 3;
			gridBagConstraints2.insets = new Insets(4, 4, 8, 4);
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.weighty = 0.1;
			gridBagConstraints2.gridy = 3;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridheight = -1;
			gridBagConstraints.gridy = 1;
			gridBagConstraints.ipadx = 417;
			gridBagConstraints.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJPanel_commit(), gridBagConstraints2);
			jContentPane.add(getJPanel_model(), gridBagConstraints6);
			jContentPane.add(getJPanel_status(), gridBagConstraints21);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.setFont(new Font("Dialog", Font.PLAIN, 12));
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setFont(new Font("Dialog", Font.PLAIN, 11));
			helpMenu.setEnabled(true);
			helpMenu.setMnemonic(KeyEvent.VK_UNDEFINED);
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About");
			aboutMenuItem.setFont(new Font("Dialog", Font.PLAIN, 11));
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JDialog aboutDialog = getAboutDialog();
					aboutDialog.pack();
					Point loc = IndividualizerGUI.this.getLocation();
					loc.translate(20, 20);
					aboutDialog.setLocation(loc);
					aboutDialog.setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	/**
	 * This method initializes aboutDialog
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new JDialog(this, true);
			aboutDialog.setTitle("About");
			aboutDialog.setLocation(new Point(this.getLocation().x + 250, this
					.getLocation().y + 155));
			aboutDialog.setBackground(new Color(235, 233, 237));
			aboutDialog.setSize(new Dimension(489, 94));
			aboutDialog.setMinimumSize(new Dimension(489, 94));
			aboutDialog.setPreferredSize(new Dimension(489, 94));
			aboutDialog.setMaximumSize(new Dimension(489, 94));
			aboutDialog.setResizable(false);
			aboutDialog.setContentPane(getAboutContentPane());
		}
		return aboutDialog;
	}

	/**
	 * This method initializes aboutContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAboutContentPane() {
		if (aboutContentPane == null) {
			aboutContentPane = new JPanel();
			aboutContentPane.setLayout(new BorderLayout());
			aboutContentPane.add(getAboutVersionArea(), BorderLayout.CENTER);
		}
		return aboutContentPane;
	}

	/**
	 * This method initializes aboutVersionArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getAboutVersionArea() {
		if (aboutVersionArea == null) {
			aboutVersionArea = new JTextArea();
			aboutVersionArea.setEditable(false);
			aboutVersionArea.setBackground(new Color(235, 233, 237));
			aboutVersionArea.setWrapStyleWord(true);
			aboutVersionArea.setLineWrap(true);
			aboutVersionArea
					.setText("\n                                             Synergia - Process Individualizer v. 0.5\n"
							+ "                    Copyright \u24B8 2006-2011, Marcello La Rosa, Florian Gottschalk\n\n");// +
			// "           This program and the accompanying materials are made available under the\n"
			// +
			// "           terms of the Eclipse Public License v1.0 which accompanies this distribution,\n"
			// +
			// "           and is available at http://www.eclipse.org/legal/epl-v10.html.");
		}
		return aboutVersionArea;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
