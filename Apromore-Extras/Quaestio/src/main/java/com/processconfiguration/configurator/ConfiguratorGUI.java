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
package com.processconfiguration.configurator;

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

import com.processconfiguration.utils.CEPCFilter;
import com.processconfiguration.utils.CMappingFilter;
import com.processconfiguration.utils.CYAWLFilter;
import com.processconfiguration.utils.DCLFilter;
import com.processconfiguration.utils.schemaValidation;

public class ConfiguratorGUI extends JFrame {// @jve:decl-index=0:visual-constraint="3,12"

	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu helpMenu = null;
	private JMenuItem aboutMenuItem = null;
	private JDialog aboutDialog = null; // @jve:decl-index=0:visual-constraint="543,11"
	private JPanel aboutContentPane = null;
	private JTextArea aboutVersionArea = null;
	private JPanel jPanel_commit = null;
	private JPanel jPanel_map = null;
	private JButton jButton_map = null;
	private JTextField jTextField_map = null;
	private JButton jButton_conf = null;
	private JTextField jTextField_conf = null;
	private JButton jButton_commit = null;
	private JButton jButton_exit = null;
	private JPanel jPanel_model = null;
	private JButton jButton_model = null;
	private JTextField jTextField_model = null;
	File fInModel = null;
	File fInMap = null;
	File fInConf = null;
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
			gridBagConstraints11.insets = new Insets(2, 2, 2, 110);
			gridBagConstraints11.ipadx = 0;
			gridBagConstraints11.gridy = 0;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.fill = GridBagConstraints.NONE;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.weighty = 1.0;
			gridBagConstraints12.insets = new Insets(2, 110, 2, 2);
			gridBagConstraints12.ipadx = 0;
			gridBagConstraints12.gridy = 0;
			jPanel_commit = new JPanel();
			jPanel_commit.setLayout(new GridBagLayout());
			jPanel_commit.add(getJButton_commit(), gridBagConstraints11);
			jPanel_commit.add(getJButton_exit(), gridBagConstraints12);
		}
		return jPanel_commit;
	}

	/**
	 * This method initializes jPanel_map
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_map() {
		if (jPanel_map == null) {
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.insets = new Insets(2, 4, 2, 2);
			gridBagConstraints8.weighty = 1.0;
			gridBagConstraints8.ipady = 0;
			gridBagConstraints8.anchor = GridBagConstraints.CENTER;
			gridBagConstraints8.gridx = 1;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.fill = GridBagConstraints.NONE;
			gridBagConstraints7.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.ipadx = 0;
			gridBagConstraints7.ipady = 0;
			gridBagConstraints7.weightx = 0.0;
			gridBagConstraints7.anchor = GridBagConstraints.CENTER;
			gridBagConstraints7.gridy = 0;
			jPanel_map = new JPanel();
			jPanel_map.setLayout(new GridBagLayout());
			jPanel_map.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEtchedBorder(EtchedBorder.RAISED),
							"C-Mapping", TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 11), new Color(51, 94, 168)),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));
			jPanel_map.add(getJButton_map(), gridBagConstraints7);
			jPanel_map.add(getJTextField_map(), gridBagConstraints8);
		}
		return jPanel_map;
	}

	/**
	 * This method initializes jPanel_conf
	 * 
	 * @return javax.swing.JPanel
	 *//*
	private JPanel getJPanel_conf() {
		if (jPanel_conf == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.insets = new Insets(2, 4, 2, 2);
			gridBagConstraints10.weighty = 1.0;
			gridBagConstraints10.anchor = GridBagConstraints.CENTER;
			gridBagConstraints10.gridx = 1;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.fill = GridBagConstraints.NONE;
			gridBagConstraints9.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints9.weighty = 1.0;
			gridBagConstraints9.weightx = 0.0;
			gridBagConstraints9.anchor = GridBagConstraints.CENTER;
			gridBagConstraints9.gridy = 0;
			jPanel_conf = new JPanel();
			jPanel_conf.setLayout(new GridBagLayout());
			jPanel_conf.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEtchedBorder(EtchedBorder.RAISED),
							"Domain Configuration",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 11), new Color(51, 94, 168)),
					BorderFactory.createEmptyBorder(1, 1, 1, 1)));
			jPanel_conf.add(getJButton_conf(), gridBagConstraints9);
			jPanel_conf.add(getJTextField_conf(), gridBagConstraints10);
		}
		return jPanel_conf;
	}*/

	/**
	 * This method initializes jButton_map
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_map() {
		if (jButton_map == null) {
			jButton_map = new JButton();
			jButton_map.setIcon(new ImageIcon(getClass().getResource(
					"/icons/open.png")));
			jButton_map.setMaximumSize(new Dimension(32, 25));
			jButton_map.setMinimumSize(new Dimension(32, 25));
			jButton_map.setMnemonic(KeyEvent.VK_UNDEFINED);
			jButton_map.setPreferredSize(new Dimension(32, 25));
			jButton_map.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fileChooser = new JFileChooser(".");
					fileChooser.setFont(new java.awt.Font("Dialog",
							java.awt.Font.PLAIN, 11));
					fileChooser.setLocation(100, 100);
					fileChooser.addChoosableFileFilter(new CMappingFilter());
					int returnVal = fileChooser
							.showOpenDialog(ConfiguratorGUI.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						fInMap = fileChooser.getSelectedFile();
						try {
							schemaValidation.validate(
									getClass().getResource("/schema/CMAP.xsd"),
									fInMap);
							getJTextField_map().setText(
									fInMap.getAbsolutePath());
						} catch (Exception ev) {
							getJTextField_map().setText(
									"Format not valid! Please check.");
							fInMap = null;
						}
					}
					enableCommitButton();
				}
			});
		}
		return jButton_map;
	}

	protected void enableCommitButton() {
		//if ((fInModel != null) && (fInMap != null) && (fInConf != null))
		if ((fInModel != null) && (fInMap != null))
			getJButton_commit().setEnabled(true);
	}

	/**
	 * This method initializes jTextField_map
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_map() {
		if (jTextField_map == null) {
			jTextField_map = new JTextField();
			jTextField_map.setMinimumSize(new Dimension(80, 25));
			jTextField_map.setMaximumSize(new Dimension(2147483647, 25));
			jTextField_map.setEditable(false);
			jTextField_map.setPreferredSize(new Dimension(80, 25));
		}
		return jTextField_map;
	}

	/**
	 * This method initializes jButton_conf
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_conf() {
		if (jButton_conf == null) {
			jButton_conf = new JButton();
			jButton_conf.setIcon(new ImageIcon(getClass().getResource(
					"/icons/open.png")));
			jButton_conf.setMaximumSize(new Dimension(32, 25));
			jButton_conf.setPreferredSize(new Dimension(32, 25));
			jButton_conf.setMinimumSize(new Dimension(32, 25));
			jButton_conf.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					fileChooser = new JFileChooser(".");
					fileChooser.setFont(new java.awt.Font("Dialog",
							java.awt.Font.PLAIN, 11));
					fileChooser.setLocation(100, 100);
					fileChooser.addChoosableFileFilter(new DCLFilter());
					int returnVal = fileChooser
							.showOpenDialog(ConfiguratorGUI.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						fInConf = fileChooser.getSelectedFile();
						try {
							schemaValidation.validate(
									getClass().getResource("/schema/DCL.xsd"),
									fInConf);
							getJTextField_conf().setText(
									fInConf.getAbsolutePath());
						} catch (Exception ev) {
							getJTextField_conf().setText(
									"Format not valid! Please check.");
							fInConf = null;
						}

					}
					enableCommitButton();
				}
			});
		}
		return jButton_conf;
	}

	/**
	 * This method initializes jTextField_conf
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_conf() {
		if (jTextField_conf == null) {
			jTextField_conf = new JTextField();
			jTextField_conf.setPreferredSize(new Dimension(80, 25));
			jTextField_conf.setMaximumSize(new Dimension(2147483647, 25));
			jTextField_conf.setEditable(false);
			jTextField_conf.setMinimumSize(new Dimension(80, 25));
		}
		return jTextField_conf;
	}

	/**
	 * This method initializes jButton_exit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_exit() {
		if (jButton_exit == null) {
			jButton_exit = new JButton();
			jButton_exit.setText("Exit");
			jButton_exit.setMaximumSize(new Dimension(100, 25));
			jButton_exit.setMinimumSize(new Dimension(100, 25));
			jButton_exit.setPreferredSize(new Dimension(100, 25));
			jButton_exit.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_exit.setEnabled(true);
			jButton_exit
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							ConfiguratorGUI.this.setVisible(false);
						}
					});
		}
		return jButton_exit;
	}
	
	/**
	 * This method initializes jButton_commit
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_commit() {
		if (jButton_commit == null) {
			jButton_commit = new JButton();
			jButton_commit.setText("OK");
			jButton_commit.setMaximumSize(new Dimension(100, 25));
			jButton_commit.setMinimumSize(new Dimension(100, 25));
			jButton_commit.setPreferredSize(new Dimension(100, 25));
			jButton_commit.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_commit.setEnabled(false);
			jButton_commit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jLabel_status.setText("Process Model and Mapping are linked");
					//fInModel;
					//fInMap;
				}
/*					Configurator mp = new Configurator(fInModel,fInMap, fInConf);
						if (mp.commit() == 0) {
							jLabel_status.setText("Configuration succesfully committed. Output saved in: "+ mp.fOutName);
						} else
							jLabel_status.setText("Commitment failed!");
						}*/
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
							"Process Model",
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
									.addChoosableFileFilter(new CEPCFilter());
							fileChooser
									.addChoosableFileFilter(new CYAWLFilter());
							int returnVal = fileChooser
									.showOpenDialog(ConfiguratorGUI.this);

							if (returnVal == JFileChooser.APPROVE_OPTION) {
								fInModel = fileChooser.getSelectedFile();
								try {
									if (fInModel.toString().endsWith(".epml")) {
										schemaValidation
												.validate(
														getClass()
																.getResource(
																		"/schema/EPML_2.0.xsd"),
														fInModel);
									} else if (fInModel.toString().endsWith(
											".yawl")) {
										schemaValidation
												.validate(
														getClass()
																.getResource(
																		"/schema/YAWL_Schema2.2.xsd"),
														fInModel);
									}
									getJTextField_model().setText(
											fInModel.getAbsolutePath());
								} catch (Exception ev) {
									getJTextField_model().setText(
											"Format not valid! Please check.");
									fInModel = null;
								}
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
			gridBagConstraints13.gridx = 0;
			jLabel_status = new JLabel();
			jLabel_status.setFont(new Font("Dialog", Font.PLAIN, 11));
			jLabel_status.setMinimumSize(new Dimension(0, 23));
			jLabel_status.setMaximumSize(new Dimension(0, 23));
			jLabel_status.setText("  Output message:");
			jLabel_status.setPreferredSize(new Dimension(0, 23));
			jPanel_status = new JPanel();
			jPanel_status.setLayout(new GridBagLayout());
			jPanel_status.setBorder(BorderFactory.createLineBorder(new Color(
					212, 208, 200), 1));
			jPanel_status.setPreferredSize(new Dimension(10, 25));
			jPanel_status.setMinimumSize(new Dimension(10, 25));
			jPanel_status.setMaximumSize(new Dimension(2147483647, 25));
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
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ConfiguratorGUI application = new ConfiguratorGUI();
				application.setVisible(true);
			}
		});
	}*/

	public ConfiguratorGUI() {
		super();
		initialize();
	}

	private void initialize() {
		this.setLocation(new Point(100, 100));
		this.setResizable(true);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setJMenuBar(getJJMenuBar());
		this.setMinimumSize(new java.awt.Dimension(500, 310));
		this.setPreferredSize(new java.awt.Dimension(500, 310));
		this.setSize(500, 310);
		this.setContentPane(getJContentPane());
		this.setTitle("Synergia - Link to Process Model");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/icons/CP.gif")));

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
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
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
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.ipadx = 80;
			gridBagConstraints4.ipady = 0;
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.weighty = 0.3;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new Insets(2, 4, 4, 4);
			gridBagConstraints4.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.ipadx = 80;
			gridBagConstraints3.ipady = 0;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.weighty = 0.3;
			gridBagConstraints3.insets = new Insets(2, 4, 2, 4);
			gridBagConstraints3.anchor = GridBagConstraints.CENTER;
			gridBagConstraints3.gridy = 1;
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
			jContentPane.add(getJPanel_map(), gridBagConstraints3);
			//jContentPane.add(getJPanel_conf(), gridBagConstraints4);
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
					Point loc = ConfiguratorGUI.this.getLocation();
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
			aboutDialog.setSize(new Dimension(247, 114));
			aboutDialog.setMinimumSize(new Dimension(247, 114));
			aboutDialog.setPreferredSize(new Dimension(247, 114));
			aboutDialog.setMaximumSize(new Dimension(247, 114));
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
					.setText("\n               Process Configurator v. 0.5\n\n    Copyright 2008-2011, Marcello La Rosa");
		}
		return aboutVersionArea;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
