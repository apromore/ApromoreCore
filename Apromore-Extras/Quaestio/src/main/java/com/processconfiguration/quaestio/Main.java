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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.filestore.client.DavFileSystemView;
import org.apromore.filestore.client.FileStoreService;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.processconfiguration.bddc.BDDService;
import com.processconfiguration.bddc.ExecBDDC;
import com.processconfiguration.bddc.JavaBDDService;
import com.processconfiguration.cmap.CEpcType;
import com.processconfiguration.cmap.CMAP;
import com.processconfiguration.cmap.CYawlType;
import com.processconfiguration.configurator.Configurator;
import com.processconfiguration.utils.schemaValidation;
import com.processconfiguration.dcl.DCL;
import com.processconfiguration.qml.FactType;
import com.processconfiguration.qml.QMLType;
import com.processconfiguration.qml.QuestionType;
import com.processconfiguration.utils.CEPCFilter;
import com.processconfiguration.utils.CMappingFilter;
import com.processconfiguration.utils.CYAWLFilter;
import com.processconfiguration.utils.DCLFilter;
import com.processconfiguration.utils.EPMLFilter;
import com.processconfiguration.utils.QMLFilter;
import com.processconfiguration.utils.TXTFilter;
import com.processconfiguration.utils.YAWLFilter;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;

public class Main extends JPanel implements ListSelectionListener,
		ItemListener, MouseListener, ActionListener {

	public static final String TRUE = "true"; // @jve:decl-index=0:
	public static final String FALSE = "false"; // @jve:decl-index=0:
	public static final String UNSET = "unset"; // @jve:decl-index=0:
	private File fIn;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu fileMenu = null;
	private JMenu optionsMenu = null;
	private JMenu helpMenu = null;
	private JMenuItem exitMenuItem = null;
	private JMenuItem aboutMenuItem = null;
	private JMenuItem logMenuItem = null;
	private JMenuItem saveModelMenuItem = null;
	private JMenuItem saveCMenuItem = null;
	private JMenuItem LinkCMenuItem = null;
	private JMenuItem openCMMenuItem = null;
	private JPanel jPanel_A = null;
	private JPanel jPanel_info = null;
	private JFileChooser fileChooser = null; // @jve:decl-index=0:visual-constraint="478,824"
	private QMLType qml = null;
	private CMAP cMAP = null;
	private CEpcType cEpmlMap = null;
	private CYawlType cYawlMap = null;
	private JFrame jDialog_log = null; // @jve:decl-index=0:visual-constraint="1356,9"
	private JPanel jContentPane_log = null;
	private JPanel jPanel_logButton = null;
	private JButton jButton_SaveLog = null;
	private JTextArea jArea_log = null;
	private JLabel jLabel_info = null;
	private JLabel jLabel_info2 = null;
	private JLabel jLabel_info3 = null;
	private JPanel jPanel_Q2 = null;
	private JButton jButton_FInspector = null;
	private JButton jButton_QTree = null;
	private JPanel jPanel_A2 = null;
	private JButton jButton_answerPerform = null;
	private JButton jButton_answerUndo = null;
	private JButton jButton_answerSave = null;
	private JButton jButton_answerSaveDCL = null;
	private JScrollPane jScrollPane_enabledQ = null;
	private JList jList_enabledQ = null;
	private JPanel jPanel_Q = null;
	private JScrollPane jScrollPane_answeredQ = null;
	private JList jList_answeredQ = null;
	private QuestionTypeListModel validQ = null; // @jve:decl-index=0:visual-constraint="1387,609"
	private QuestionTypeListModel answeredQ = null; // @jve:decl-index=0:visual-constraint="1436,609"
	private JLabel jLabel_A = null;
	private JScrollPane jScrollPane_A = null;
	private JPanel jPanel_AF = null;
	private JScrollPane jScrollPane_AG = null;
	private JTextArea jTextArea_AG = null;
	private JCheckBoxMenuItem showDef_MenuItem = null;
	private JCheckBoxMenuItem showMan_MenuItem = null;
	private JCheckBoxMenuItem showSQ_MenuItem = null;
	protected QuestionType selectedQ;// the selected question which can be
										// answered // @jve:decl-index=0:
	private JMenuItem showFI_MenuItem = null;
	private JFrame jDialog_FI = null; // @jve:decl-index=0:visual-constraint="874,7"
	private JPanel jContentPane_FI = null;
	private JLabel jLabel_AQID = null;
	private JPanel jPanel_AQD = null;
	private JLabel jLabel_FImpact = null;
	private JScrollPane jScrollPane_FG = null;
	private JTextArea jTextArea_FG = null;
	private JScrollPane jScrollPane_FDep = null;
	private JTextArea jTextArea_FDep = null;
	private JLabel jLabel_FDef = null;
	private JLabel jLabel_FMan = null;
	private JPanel jPanel_F = null;
	private JLabel jLabel_FDescription = null;
	private JLabel jLabel_FID = null;
	private List<State> states = null; // @jve:decl-index=0:
	private State currentS = null; // @jve:decl-index=0:
	private JTextField jTextField_FDescription = null;
	private JTextField jTextField_FID = null;
	private JTextField jTextField_FI = null;
	private JTextField jTextField_FMan = null;
	private JTextField jTextField_FDef = null;
	private Map<String, JToggleButton> buttonsList = null; // for each fID its
															// associated button
															// (not per question
															// but globally) //
															// @jve:decl-index=0:
	private JScrollPane jScrollPane_FC = null;
	private JTextArea jTextArea_FC = null;
	private JLabel jLabel_FinQ = null;
	private JTextField jTextField_FinQ = null;
	private BDDService bddc = null;
	private boolean first;
	private State tempS = null;// temporary state used for testing if an answer
								// is allowed (progresses with the selection of
								// facts) // @jve:decl-index=0:
	protected boolean showDef;
	protected boolean showMan;
	private JButton jButton_answerDefault = null;
	protected JDialog jDialog_About = null; // @jve:decl-index=0:visual-constraint="1360,442"
	private JScrollPane jScrollPane_About = null;
	private JTextArea jTextArea_About = null;
	private HashSet<String> mandatoryF = null; // @jve:decl-index=0:
	private JDialog jDialog_AskToContinue = null; // @jve:decl-index=0:visual-constraint="47,631"
	private JPanel jContentPane_askToContinue = null;
	private JPanel jPanel_ask = null;
	private JPanel jPanel_askTxt = null;
	private JPanel jPanel_ask0 = null;
	private JPanel jPanel_ask1 = null;
	private JPanel jPanel_ask2 = null;
	private JButton jButton_Continue = null;
	private JButton jButton_Stop = null;
	private JLabel jLabel_ask = null;
	protected boolean continueC;
	private JScrollPane jScrollPane_log = null;
	private QuestionTypeListModel tempAQ = null;
	private JTextArea jTextArea_ask = null;
	private JButton jButton_legendC = null;
	private JDialog jDialog_LegendC = null; // @jve:decl-index=0:visual-constraint="47,823"
	private JTextArea jTextArea_LegendC = null; // @jve:decl-index=0:
	private JScrollPane jScrollPane_LegendC = null;
	private JDialog jDialog_AskToSave = null; // @jve:decl-index=0:visual-constraint="447,631"
	private JPanel jContentPane_askToSave = null;
	private JLabel jLabel_save = null;
	private JPanel jPanel_save = null;
	private JPanel jPanel_save0 = null;
	private JPanel jPanel_save1 = null;
	protected JPanel jPanel_save2 = null;
	private JPanel jPanel_saveTxt = null;
	private JButton jButton_Export = null;
	private JButton jButton_Discard = null;
	private JButton jButton_Individualize = null;
	private JTextArea jTextArea_save = null;
	private JMenuItem exportMenuItem = null;
	private boolean XORQuestion;
	private boolean showSkippableQuestions;
	private String currentSelection = null;
	private HashSet<String> XORquestions = null;// contains the qID of the XOR
												// questions
	private HashSet<String> skippedQuestions = null;
	private JScrollPane jScrollPane_QDep = null;
	private JTextArea jTextArea_QDep = null;

	boolean cFlag = true;

	private BufferedReader reader = null;

	// sets
	Map<String, FactType> FactsMap;
	Map<String, QuestionType> QuestionsMap;

	Cmap         fInMap;
	ProcessModel fInModel;
	File         fInConf;

	/**
         * The URL of the Apromore-Editor web application which should be used to display the configured BPMN.
         *
         * If this is <code>null</code>, {@link #showModel} does nothing.
	 */
	private URL editorURL = null;

	/**
	 * This method initializes jScrollPane_A
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_A() {
		if (jScrollPane_A == null) {
			jScrollPane_A = new JScrollPane();
			jScrollPane_A
					.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
			jScrollPane_A.setPreferredSize(new java.awt.Dimension(29, 50));
			jScrollPane_A.setMinimumSize(new java.awt.Dimension(27, 50));
			jScrollPane_A.setViewportView(getJPanel_AF());
			// jScrollPane_A.setMaximumSize(new Dimension(431, 60));
		}
		return jScrollPane_A;
	}

	/**
	 * This method initializes jPanel_AF
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_AF() {
		if (jPanel_AF == null) {
			jPanel_AF = new JPanel();
			jPanel_AF
					.setLayout(new BoxLayout(getJPanel_AF(), BoxLayout.Y_AXIS));
			jPanel_AF.setBorder(javax.swing.BorderFactory.createCompoundBorder(
					javax.swing.BorderFactory.createLineBorder(
							java.awt.Color.lightGray, 1),
					javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			jPanel_AF.setBackground(new Color(235, 233, 237));
			jPanel_AF.setOpaque(false);
			// jPanel_AF.setMaximumSize(new Dimension(431, 60));
			// jPanel_AF.setSize(new Dimension(431, 60));
			// jPanel_AF.setMinimumSize(new Dimension(431, 60));
			// jPanel_AF.setPreferredSize(new Dimension(431, 60));
		}
		return jPanel_AF;
	}

	/**
	 * This method initializes jScrollPane_AG
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_AG() {
		if (jScrollPane_AG == null) {
			JScrollBar jScrollBar = new JScrollBar();
			jScrollBar.setValue(0);
			jScrollBar.setUnitIncrement(10);
			jScrollBar.setMaximum(100);
			jScrollPane_AG = new JScrollPane();
			jScrollPane_AG.setMinimumSize(new Dimension(29, 46));
			jScrollPane_AG.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEmptyBorder(0, 0, 0, 0), "Guidelines",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 13), new Color(51, 94, 168)),
					BorderFactory.createLineBorder(Color.lightGray, 1)));
			jScrollPane_AG
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane_AG
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			jScrollPane_AG.setVerticalScrollBar(jScrollBar);
			jScrollPane_AG.setViewportView(getJTextArea_AG());
		}
		return jScrollPane_AG;
	}

	/**
	 * This method initializes jTextArea_AG
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_AG() {
		if (jTextArea_AG == null) {
			jTextArea_AG = new JTextArea();
			jTextArea_AG.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
			jTextArea_AG.setLineWrap(true);
			jTextArea_AG.setWrapStyleWord(true);
			jTextArea_AG.setEditable(false);
			// jTextArea_AG.setPreferredSize(new Dimension(431, 60));
			jTextArea_AG.setMinimumSize(new Dimension(431, 60));
			jTextArea_AG.setSize(new Dimension(431, 60));
			jTextArea_AG.setFont(new Font("Dialog", Font.PLAIN, 13));
			jTextArea_AG.setMaximumSize(new Dimension(431, 60));
		}
		return jTextArea_AG;
	}

	/**
	 * This method initializes showDef_MenuItem
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getShowDef_MenuItem() {
		if (showDef_MenuItem == null) {
			showDef_MenuItem = new JCheckBoxMenuItem("Show Default Values");
			showDef_MenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			showDef_MenuItem.setSelected(true);
			showDef_MenuItem.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED)
						showDef = false;
					else
						showDef = true;
				}
			});
		}
		return showDef_MenuItem;
	}

	/**
	 * This method initializes showMan_MenuItem
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getShowMan_MenuItem() {
		if (showMan_MenuItem == null) {
			showMan_MenuItem = new JCheckBoxMenuItem("Show Mandatory Facts");
			showMan_MenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			showMan_MenuItem.setSelected(true);
			showMan_MenuItem.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED)
						showMan = false;
					else
						showMan = true;
				}
			});
		}
		return showMan_MenuItem;
	}

	/**
	 * This method initializes showSQ_MenuItem
	 * 
	 * @return javax.swing.JCheckBoxMenuItem
	 */
	private JCheckBoxMenuItem getShowSQ_MenuItem() {
		if (showSQ_MenuItem == null) {
			showSQ_MenuItem = new JCheckBoxMenuItem("Show Skipped Questions");
			showSQ_MenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			showSQ_MenuItem.setSelected(true);
			showSQ_MenuItem.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.DESELECTED)
						showSkippableQuestions = false;
					else
						showSkippableQuestions = true;
					if (!first)// if no model has been read so far, then the
								// control is skipped
						updateSkippableQuestions(showSkippableQuestions);
				}
			});
		}
		return showSQ_MenuItem;
	}

	protected void updateSkippableQuestions(boolean showSkippableQuestions) {
		// TODO: when a question is rolled back if another question being
		// skippable is rolled back as well, this is still shown in gray in the
		// Valid Questions list. Why???
		QuestionType currentQ;
		for (String qID : skippedQuestions) {
			currentQ = QuestionsMap.get(qID);
			if (showSkippableQuestions) {
				currentQ.setSkippable(true);
				answeredQ.addElement(currentQ);
			} else
				answeredQ.removeElement(currentQ);
		}
		if (!showSkippableQuestions)// this is used to avoid showing an
									// inconstistent Question Inspector if the
									// item selected in the Answered Questions
									// List was a skipped question now being
									// removed
			getJList_answeredQ().setSelectedIndex(
					getJList_answeredQ().getLastVisibleIndex());
	}

	/**
	 * This method initializes showFI_MenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getShowFI_MenuItem() {
		if (showFI_MenuItem == null) {
			showFI_MenuItem = new JMenuItem();
			showFI_MenuItem.setText("Show Fact Inspector");
			showFI_MenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			showFI_MenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getJDialog_FI().setVisible(true);
						}
					});
		}
		return showFI_MenuItem;
	}

	/**
	 * This method initializes jDialog_FI: the Fact Inspector showing fact
	 * properties
	 * 
	 * @return javax.swing.JDialog
	 */
	private JFrame getJDialog_FI() {
		if (jDialog_FI == null) {
			jDialog_FI = new JFrame();
			jDialog_FI.setSize(new Dimension(351, 600));
			jDialog_FI.setIconImage(Toolkit.getDefaultToolkit().getImage(
					getClass().getResource("/icons/FI2.gif")));
			jDialog_FI.setTitle("Fact Inspector");
			jDialog_FI.setLocation(new Point(1000, 100));
			jDialog_FI.setContentPane(getJContentPane_FI());
		}
		return jDialog_FI;
	}

	/**
	 * This method initializes jContentPane_FI
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane_FI() {
		if (jContentPane_FI == null) {
			jContentPane_FI = new JPanel();
			jContentPane_FI.setLayout(new GridBagLayout());
			jContentPane_FI.setBorder(javax.swing.BorderFactory
					.createEmptyBorder(8, 8, 8, 8));

			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.gridx = 0;
			gridBagConstraints24.gridy = 0;
			gridBagConstraints24.gridwidth = 1;
			gridBagConstraints24.gridheight = 1;
			//gridBagConstraints24.weightx = 0.0;
			//gridBagConstraints24.anchor = java.awt.GridBagConstraints.CENTER;
			//gridBagConstraints24.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints24.fill = java.awt.GridBagConstraints.BOTH;
			//gridBagConstraints24.insets = new Insets(0, 0, 4, 0);
			//gridBagConstraints24.ipadx = 0;
			jContentPane_FI.add(getJPanel_F(), gridBagConstraints24);

			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.gridy = 1;
			gridBagConstraints19.weightx = 1.0;
			gridBagConstraints19.weighty = 2.7;
			gridBagConstraints19.fill = GridBagConstraints.BOTH;
			jContentPane_FI.add(getJScrollPane_FG(), gridBagConstraints19);

			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.gridy = 2;
			gridBagConstraints26.gridheight = 1;
			gridBagConstraints26.weightx = 1.0;
			gridBagConstraints26.weighty = 2.7;
			gridBagConstraints26.fill = GridBagConstraints.BOTH;
			jContentPane_FI.add(getJScrollPane_FC(), gridBagConstraints26);

			GridBagConstraints gridBagConstraints38 = new GridBagConstraints();
			gridBagConstraints38.gridx = 0;
			gridBagConstraints38.gridy = 3;
			gridBagConstraints38.anchor = GridBagConstraints.EAST;
			gridBagConstraints38.insets = new Insets(0, 0, 0, 4);
			jContentPane_FI.add(getJButton_legendC(), gridBagConstraints38);

			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.gridx = 0;
			gridBagConstraints21.gridy = 4;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.weighty = 1.5;
			gridBagConstraints21.fill = GridBagConstraints.BOTH;
			jContentPane_FI.add(getJScrollPane_FDep(), gridBagConstraints21);
		}
		return jContentPane_FI;
	}

	/**
	 * This method initializes jPanel_AQD
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_AQD() {
		if (jPanel_AQD == null) {
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.insets = new java.awt.Insets(5, 0, 5, 0);
			gridBagConstraints12.gridy = 0;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.EAST;
			gridBagConstraints12.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints12.gridx = 1;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.insets = new java.awt.Insets(5, 0, 5, 0);
			gridBagConstraints10.gridy = 0;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.gridwidth = 1;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints10.weightx = 1.0;
			gridBagConstraints10.gridx = 0;
			jPanel_AQD = new JPanel();
			jPanel_AQD.setLayout(new GridBagLayout());
			jPanel_AQD.setPreferredSize(new java.awt.Dimension(70, 24));
			jLabel_A = new JLabel();
			jLabel_A.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,
					2, 4, 2));
			jLabel_A.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			jLabel_A.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
			jLabel_A.setMinimumSize(new java.awt.Dimension(300, 14));
			jLabel_A.setPreferredSize(new java.awt.Dimension(300, 14));
			jLabel_AQID = new JLabel();
			// OLD: jLabel_AQID.setText("ID: ");
			jLabel_AQID.setPreferredSize(new java.awt.Dimension(45, 14));
			jLabel_AQID.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			jLabel_AQID.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					0, 0, 0, 5));
			jLabel_AQID.setForeground(Color.GRAY);// OLD:
													// jLabel_AQID.setForeground(new
													// java.awt.Color(167,166,170));
			jLabel_AQID.setMinimumSize(new java.awt.Dimension(30, 14));
			jPanel_AQD.add(jLabel_A, gridBagConstraints10);
			jPanel_AQD.add(jLabel_AQID, gridBagConstraints12);
		}
		return jPanel_AQD;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_FG() {
		if (jScrollPane_FG == null) {
			jScrollPane_FG = new JScrollPane();
			jScrollPane_FG.setMinimumSize(new Dimension(29, 46));
			jScrollPane_FG.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEmptyBorder(0, 0, 0, 0), "Guidelines",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 12), new Color(51, 94, 168)),
					BorderFactory.createLineBorder(Color.lightGray, 1)));
			jScrollPane_FG
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane_FG.setViewportView(getJTextArea_FG());
		}
		return jScrollPane_FG;
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_FG() {
		if (jTextArea_FG == null) {
			jTextArea_FG = new JTextArea();
			jTextArea_FG.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
			jTextArea_FG.setLineWrap(true);
			jTextArea_FG.setWrapStyleWord(true);
			jTextArea_FG.setFont(new Font("Dialog", Font.PLAIN, 13));
			jTextArea_FG.setEditable(false);
		}
		return jTextArea_FG;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_FDep() {
		if (jScrollPane_FDep == null) {
			jScrollPane_FDep = new JScrollPane();
			jScrollPane_FDep.setMinimumSize(new Dimension(29, 46));
			jScrollPane_FDep.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEmptyBorder(0, 0, 0, 0), "Dependencies",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 12), new Color(51, 94, 168)),
					BorderFactory.createLineBorder(Color.lightGray, 1)));
			// jScrollPane_FDep.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane_FDep.setViewportView(getJTextArea_FDep());
		}
		return jScrollPane_FDep;
	}

	/**
	 * This method initializes jTextArea1
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_FDep() {
		if (jTextArea_FDep == null) {
			jTextArea_FDep = new JTextArea();
			jTextArea_FDep.setBorder(BorderFactory
					.createEmptyBorder(2, 4, 2, 4));
			// jTextArea_FDep.setLineWrap(true);
			// jTextArea_FDep.setWrapStyleWord(true);
			jTextArea_FDep.setEditable(false);
			jTextArea_FDep
					.setToolTipText("The facts this fact depends on, and the questions they appear in.");
			jTextArea_FDep.setFont(new Font("Dialog", Font.PLAIN, 13));
			jTextArea_FDep.setText("");
		}
		return jTextArea_FDep;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_F() {
		if (jPanel_F == null) {
			jPanel_F = new JPanel();
			jPanel_F.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			jPanel_F.setLayout(new GridBagLayout());
			//jPanel_F.setPreferredSize(new java.awt.Dimension(370, 150));
			//jPanel_F.setMinimumSize(new java.awt.Dimension(370, 150));

			// Description:
			jLabel_FDescription = new JLabel();
			jLabel_FDescription.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			jLabel_FDescription.setHorizontalAlignment(SwingConstants.LEFT);
			jLabel_FDescription.setText("Description:");
			jLabel_FDescription.setForeground(java.awt.Color.black);
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.anchor = GridBagConstraints.WEST;
			jPanel_F.add(jLabel_FDescription, gridBagConstraints13);

			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.gridy = 0;
			gridBagConstraints17.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.weightx = 2.0;
			gridBagConstraints17.gridwidth = 2;
			jPanel_F.add(getJTextField_FDescription(), gridBagConstraints17);

			// ID:
			jLabel_FID = new JLabel();
			jLabel_FID.setEnabled(true);
			jLabel_FID.setText("ID: ");
			jLabel_FID.setForeground(java.awt.Color.black);
			jLabel_FID.setFont(new Font("Dialog", Font.PLAIN, 12));
			jLabel_FID.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.gridx = 3;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.weightx = 0.0;
			gridBagConstraints16.fill = GridBagConstraints.NONE;
			gridBagConstraints16.anchor = GridBagConstraints.EAST;
			jPanel_F.add(jLabel_FID, gridBagConstraints16);

			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 4;
			gridBagConstraints14.gridy = 0;
			gridBagConstraints14.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints14.weightx = 1.0;
			jPanel_F.add(getJTextField_FID(), gridBagConstraints14);

			// Impact:
			jLabel_FImpact = new JLabel();
			jLabel_FImpact.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			jLabel_FImpact.setHorizontalAlignment(SwingConstants.LEFT);
			jLabel_FImpact.setText("Impact Level:");
			jLabel_FImpact.setForeground(java.awt.Color.black);
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.gridy = 1;
			gridBagConstraints15.anchor = GridBagConstraints.WEST;
			jPanel_F.add(jLabel_FImpact, gridBagConstraints15);

			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 1;
			gridBagConstraints22.gridy = 1;
			gridBagConstraints22.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.weightx = 1.0;
			gridBagConstraints22.gridwidth = 4;
			jPanel_F.add(getJTextField_FI(), gridBagConstraints22);

			// Default value:
			jLabel_FDef = new JLabel();
			jLabel_FDef.setText("Default value: ");
			jLabel_FDef.setEnabled(true);
			jLabel_FDef.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			jLabel_FDef.setForeground(java.awt.Color.black);
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 0;
			gridBagConstraints18.gridy = 2;
			gridBagConstraints18.anchor = GridBagConstraints.WEST;
			jPanel_F.add(jLabel_FDef, gridBagConstraints18);

			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 1;
			gridBagConstraints25.gridy = 2;
			gridBagConstraints25.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints25.weightx = 1.0;
			jPanel_F.add(getJTextField_FDef(), gridBagConstraints25);

			// Mandatory:
			jLabel_FMan = new JLabel();
			jLabel_FMan.setText("Mandatory: ");
			jLabel_FMan.setEnabled(true);
			jLabel_FMan.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			jLabel_FMan.setForeground(java.awt.Color.black);
			jLabel_FMan.setHorizontalAlignment(SwingConstants.RIGHT);
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.gridx = 2;
			gridBagConstraints20.gridy = 2;
			gridBagConstraints20.anchor = GridBagConstraints.EAST;
			gridBagConstraints20.gridwidth = 2;
			jPanel_F.add(jLabel_FMan, gridBagConstraints20);

			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.gridx = 4;
			gridBagConstraints23.gridy = 2;
			gridBagConstraints23.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints23.weightx = 1.0;
			jPanel_F.add(getJTextField_FMan(), gridBagConstraints23);

			// Questions:
			jLabel_FinQ = new JLabel();
			jLabel_FinQ.setEnabled(true);
			jLabel_FinQ.setForeground(Color.black);
			jLabel_FinQ.setText("Questions:");
			jLabel_FinQ.setFont(new Font("Dialog", Font.PLAIN, 12));
			GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
			gridBagConstraints27.gridx = 0;
			gridBagConstraints27.gridy = 3;
			gridBagConstraints27.anchor = GridBagConstraints.WEST;
			jPanel_F.add(jLabel_FinQ, gridBagConstraints27);

			GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
			gridBagConstraints28.gridx = 1;
			gridBagConstraints28.gridy = 3;
			gridBagConstraints28.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints28.weightx = 1.0;
			gridBagConstraints28.gridwidth = 4;
			jPanel_F.add(getJTextField_FinQ(), gridBagConstraints28);
		}
		return jPanel_F;
	}

	/**
	 * This method initializes jTextField_FDescription
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_FDescription() {
		if (jTextField_FDescription == null) {
			jTextField_FDescription = new JTextField();
			jTextField_FDescription.setEditable(false);
		}
		return jTextField_FDescription;
	}

	/**
	 * This method initializes jTextField_FID
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_FID() {
		if (jTextField_FID == null) {
			jTextField_FID = new JTextField();
			jTextField_FID.setEditable(false);
		}
		return jTextField_FID;
	}

	/**
	 * This method initializes jTextField_FI
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_FI() {
		if (jTextField_FI == null) {
			jTextField_FI = new JTextField();
			jTextField_FI.setEditable(false);
		}
		return jTextField_FI;
	}

	/**
	 * This method initializes jTextField_FMan
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_FMan() {
		if (jTextField_FMan == null) {
			jTextField_FMan = new JTextField();
			jTextField_FMan.setEditable(false);
		}
		return jTextField_FMan;
	}

	/**
	 * This method initializes jTextField_FDef
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_FDef() {
		if (jTextField_FDef == null) {
			jTextField_FDef = new JTextField();
			jTextField_FDef.setEditable(false);
		}
		return jTextField_FDef;
	}

	/**
	 * This method initializes jScrollPane_FC
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_FC() {
		if (jScrollPane_FC == null) {
			jScrollPane_FC = new JScrollPane();
			jScrollPane_FC.setMinimumSize(new Dimension(29, 46));
			jScrollPane_FC.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEmptyBorder(0, 0, 0, 0), "Constraints",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 12), new Color(51, 94, 168)),
					BorderFactory.createLineBorder(Color.lightGray, 1)));
			jScrollPane_FC
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane_FC.setPreferredSize(new Dimension(400, 46));
			jScrollPane_FC.setViewportView(getJTextArea_FC());
		}
		return jScrollPane_FC;
	}

	/**
	 * This method initializes jTextArea_FC
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_FC() {
		if (jTextArea_FC == null) {
			jTextArea_FC = new JTextArea();
			jTextArea_FC.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
			jTextArea_FC.setEditable(false);
			jTextArea_FC.setLineWrap(true);
			jTextArea_FC.setWrapStyleWord(true);
			jTextArea_FC.setToolTipText("The constraints involving this fact.");
			jTextArea_FC.setFont(new Font("Dialog", Font.PLAIN, 13));
			jTextArea_FC.setText("");
		}
		return jTextArea_FC;
	}

	/**
	 * This method initializes jTextField_FinQ
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField_FinQ() {
		if (jTextField_FinQ == null) {
			jTextField_FinQ = new JTextField();
			jTextField_FinQ.setEditable(false);
			jTextField_FinQ
					.setToolTipText("The questions this facts appears in.");
		}
		return jTextField_FinQ;
	}

	/**
	 * This method initializes jButton_answerDefault
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_answerDefault() {
		if (jButton_answerDefault == null) {
			jButton_answerDefault = new JButton();
			jButton_answerDefault.setText("Default Answer");
			jButton_answerDefault.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_answerDefault.addActionListener(this);
			jButton_answerDefault.setEnabled(false);
		}
		return jButton_answerDefault;
	}

	/**
	 * This method initializes jScrollPane_About
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_About() {
		if (jScrollPane_About == null) {
			jScrollPane_About = new JScrollPane();
			jScrollPane_About.setViewportView(getJTextArea_About());
		}
		return jScrollPane_About;
	}

	/**
	 * This method initializes jScrollPane_LegendC
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_LegendC() {
		if (jScrollPane_LegendC == null) {
			jScrollPane_LegendC = new JScrollPane();
			jScrollPane_LegendC.setViewportView(getJTextArea_LegendC());
		}
		return jScrollPane_LegendC;
	}

	/**
	 * This method initializes jTextArea_About
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_About() {
		if (jTextArea_About == null) {
			jTextArea_About = new JTextArea();
			jTextArea_About.setEditable(false);
			jTextArea_About.setBackground(new Color(235, 233, 237));
			jTextArea_About.setWrapStyleWord(true);
			jTextArea_About.setLineWrap(true);
			jTextArea_About
					.setText("\n   Synergia - Quaestio v. 1.0. Copyright Process Configuration\u24B8 2006-2014\n\n\n"
							+ "           Contributors:\n"
							+ "                        Marcello La Rosa\n"
							+ "                        Possakorn Pitayarojanakul\n"
							+ "                        Simon Raboczi\n\n"
							+ "   This program and the accompanying materials are made available under\n"
							+ "   the terms of the Eclipse Public License v1.0 which accompanies this\n"
							+ "   distribution, and is available at http://www.eclipse.org/legal/epl-v10.html.");
		}
		return jTextArea_About;
	}

	/**
	 * This method initializes jTextArea_LegendC
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_LegendC() {
		if (jTextArea_LegendC == null) {
			jTextArea_LegendC = new JTextArea();
			jTextArea_LegendC.setEditable(false);
			jTextArea_LegendC.setWrapStyleWord(true);
			jTextArea_LegendC.setLineWrap(true);
			jTextArea_LegendC.setBorder(BorderFactory.createEmptyBorder(8, 8,
					8, 8));
			jTextArea_LegendC.setSize(50, 200);
			//jTextArea_LegendC.setMaximumSize(new Dimension(50, 200));
			//jTextArea_LegendC.setPreferredSize(new Dimension(50, 200));
			jTextArea_LegendC
					.setText("Constraints are expressed in propositional logic, using any\n"
							+ "combination of the logical connectives: OR (+), AND (.), NOT (-).\n"
							+ "\nConstraints can also feature macros, such as:\n"
							+ "- XOR: xor(f1,f2) = (f1 . -f2) + (-f1 . f2),\n"
							+ "  used when just a fact over a set of facts can be set to true.\n"
							+ "- NOR: nor(f1,f2) = -(f1 + f2),\n"
							+ "  used when no fact in a set of facts can be set to true.\n"
							+ "- IMPLICATION: f1 => f2 =  -f1 + f2,\n"
							+ "  used to express a condition where if f1 is set to true, also\n"
							+ "  f2 needs to be true, otherwise f2 can take any value.\n"
							+ "- DOUBLE IMPLICATION: f1 = f2, if (f1 => f2) . (f2 => f1),\n"
							+ "  used to express a condition where f1 is true if and only if f2 is true.");
		}
		return jTextArea_LegendC;
	}

	/**
	 * This method initializes jDialog_AskToContinue
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getJDialog_AskToContinue() {
		if (jDialog_AskToContinue == null) {
			jDialog_AskToContinue = new JDialog(/*this, "Warning", true*/);
			jDialog_AskToContinue.setTitle("Warning");
			jDialog_AskToContinue.setMinimumSize(new Dimension(388, 160));
			jDialog_AskToContinue.setSize(new Dimension(388, 168));
			jDialog_AskToContinue.setLocation(new Point(400, 350));
			jDialog_AskToContinue.setMaximumSize(new Dimension(388, 160));
			jDialog_AskToContinue.setResizable(false);
			jDialog_AskToContinue.setPreferredSize(new Dimension(388, 160));
			jDialog_AskToContinue.setContentPane(getJContentPane_askToContinue());
			jDialog_AskToContinue.addWindowListener(new java.awt.event.WindowAdapter() {
                        	/** if closed then the configuration can continue */
				public void windowClosing(java.awt.event.WindowEvent e) {
					getJDialog_AskToContinue().setVisible(false);
					continueC = true;
					updateValidQ();
				}
			});
		}
		return jDialog_AskToContinue;
	}

	/**
	 * This method initializes jDialog_AskToSave
	 * 
	 * @return javax.swing.JDialog
	 */
	private JDialog getJDialog_AskToSave() {
		if (jDialog_AskToSave == null) {
			jDialog_AskToSave = new JDialog(/*this, "Configuration completed", true*/);
                        jDialog_AskToSave.setTitle("Configuration completed");
			jDialog_AskToSave.setMinimumSize(new Dimension(388, 160));
			jDialog_AskToSave.setSize(new Dimension(388, 168));
			jDialog_AskToSave.setLocation(new Point(400, 350));
			jDialog_AskToSave.setMaximumSize(new Dimension(388, 160));
			jDialog_AskToSave.setResizable(false);
			jDialog_AskToSave.setPreferredSize(new Dimension(388, 160));
			jDialog_AskToSave.setContentPane(getJContentPane_askToSave());
			jDialog_AskToSave.addWindowListener(new java.awt.event.WindowAdapter() {
                                /** if closed then the configuration can continue */
				public void windowClosing(java.awt.event.WindowEvent e) {
					getJDialog_AskToSave().setVisible(false);
					getExportMenuItem().setEnabled(true);
				}
			});
		}
		return jDialog_AskToSave;
	}

	/**
	 * This method initializes jContentPane_askToContinue
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane_askToContinue() {
		if (jContentPane_askToContinue == null) {
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.ipadx = 0;
			gridBagConstraints33.ipady = 0;
			gridBagConstraints33.fill = GridBagConstraints.BOTH;
			gridBagConstraints33.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints33.gridwidth = 1;
			gridBagConstraints33.weightx = 1.0;
			gridBagConstraints33.weighty = 0.0;
			gridBagConstraints33.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints33.gridy = 3;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.fill = GridBagConstraints.BOTH;
			gridBagConstraints32.ipadx = 20;
			gridBagConstraints32.ipady = 20;
			gridBagConstraints32.gridwidth = 3;
			gridBagConstraints32.weightx = 1.0;
			gridBagConstraints32.weighty = 1.0;
			gridBagConstraints32.insets = new Insets(2, 0, 2, 0);
			gridBagConstraints32.gridy = 2;
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.gridy = 1;
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints29.gridwidth = 1;
			gridBagConstraints29.gridy = 0;
			jContentPane_askToContinue = new JPanel();
			jContentPane_askToContinue.setLayout(new GridBagLayout());
			jContentPane_askToContinue.setBorder(BorderFactory
					.createEmptyBorder(2, 2, 2, 2));
			jContentPane_askToContinue.add(getJPanel_ask(),
					gridBagConstraints29);
			jContentPane_askToContinue.add(getJPanel_ask0(),
					gridBagConstraints30);
			jContentPane_askToContinue.add(getJPanel_ask1(),
					gridBagConstraints32);
			jContentPane_askToContinue.add(getJPanel_ask2(),
					gridBagConstraints33);
		}
		return jContentPane_askToContinue;
	}

	/**
	 * This method initializes jContentPane_askToSave
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane_askToSave() {
		if (jContentPane_askToSave == null) {
			GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
			gridBagConstraints33.gridx = 0;
			gridBagConstraints33.ipadx = 0;
			gridBagConstraints33.ipady = 0;
			gridBagConstraints33.fill = GridBagConstraints.BOTH;
			gridBagConstraints33.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints33.gridwidth = 1;
			gridBagConstraints33.weightx = 1.0;
			gridBagConstraints33.weighty = 0.0;
			gridBagConstraints33.insets = new Insets(0, 0, 0, 0);
			gridBagConstraints33.gridy = 3;
			GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
			gridBagConstraints32.gridx = 0;
			gridBagConstraints32.fill = GridBagConstraints.BOTH;
			gridBagConstraints32.ipadx = 20;
			gridBagConstraints32.ipady = 20;
			gridBagConstraints32.gridwidth = 3;
			gridBagConstraints32.weightx = 1.0;
			gridBagConstraints32.weighty = 1.0;
			gridBagConstraints32.insets = new Insets(2, 0, 2, 0);
			gridBagConstraints32.gridy = 2;
			GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
			gridBagConstraints30.gridx = 0;
			gridBagConstraints30.gridy = 1;
			GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
			gridBagConstraints29.gridx = 0;
			gridBagConstraints29.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints29.gridwidth = 1;
			gridBagConstraints29.gridy = 0;
			jContentPane_askToSave = new JPanel();
			jContentPane_askToSave.setLayout(new GridBagLayout());
			jContentPane_askToSave.setBorder(BorderFactory.createEmptyBorder(2,
					2, 2, 2));
			jContentPane_askToSave.add(getJPanel_save(), gridBagConstraints29);
			jContentPane_askToSave.add(getJPanel_save0(), gridBagConstraints30);
			jContentPane_askToSave.add(getJPanel_save1(), gridBagConstraints32);
			jContentPane_askToSave.add(getJPanel_save2(), gridBagConstraints33);
		}
		return jContentPane_askToSave;
	}

	/**
	 * This method initializes jPanel_ask
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ask() {
		if (jPanel_ask == null) {
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.ipadx = 42;
			gridBagConstraints31.gridy = 0;
			jPanel_ask = new JPanel();
			jPanel_ask.setLayout(new GridBagLayout());
			jPanel_ask.add(getJPanel_askTxt(), gridBagConstraints31);
		}
		return jPanel_ask;
	}

	/**
	 * This method initializes jPanel_askTxt
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_askTxt() {
		if (jPanel_askTxt == null) {
			jPanel_askTxt = new JPanel();
			jPanel_askTxt.setLayout(new GridBagLayout());
		}
		return jPanel_askTxt;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ask0() {
		if (jPanel_ask0 == null) {
			jPanel_ask0 = new JPanel();
			jPanel_ask0.setLayout(new GridBagLayout());
		}
		return jPanel_ask0;
	}

	/**
	 * This method initializes jPanel_ask1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ask1() {
		if (jPanel_ask1 == null) {
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.fill = GridBagConstraints.BOTH;
			gridBagConstraints37.gridy = 2;
			gridBagConstraints37.weightx = 1.0;
			gridBagConstraints37.weighty = 1.0;
			gridBagConstraints37.gridwidth = 1;
			gridBagConstraints37.anchor = GridBagConstraints.WEST;
			gridBagConstraints37.insets = new Insets(4, 16, 4, 16);
			gridBagConstraints37.gridx = 1;
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints36.gridy = 0;
			gridBagConstraints36.fill = GridBagConstraints.NONE;
			gridBagConstraints36.weightx = 0.0;
			gridBagConstraints36.weighty = 0.0;
			gridBagConstraints36.ipadx = 0;
			gridBagConstraints36.ipady = 0;
			gridBagConstraints36.anchor = GridBagConstraints.CENTER;
			gridBagConstraints36.gridheight = 3;
			gridBagConstraints36.gridx = 0;
			jLabel_ask = new JLabel();
			jLabel_ask.setText("");// All the mandatory facts have been
									// answered.\nDo you want to continue
									// configuring the system? \n Note that
									// facts still unanswered will keep\n their
									// default values, if the configuration is
									// stopped.
			jLabel_ask.setFont(new Font("Dialog", Font.PLAIN, 12));
			jLabel_ask.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabel_ask.setIcon(new ImageIcon(getClass().getResource(
					"/icons/question.gif")));
			jPanel_ask1 = new JPanel();
			jPanel_ask1.setLayout(new GridBagLayout());
			jPanel_ask1.add(jLabel_ask, gridBagConstraints36);
			jPanel_ask1.add(getJTextArea_ask(), gridBagConstraints37);
		}
		return jPanel_ask1;
	}

	/**
	 * This method initializes jPanel_ask2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ask2() {
		if (jPanel_ask2 == null) {
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 1;
			gridBagConstraints35.insets = new Insets(4, 16, 6, 4);
			gridBagConstraints35.gridy = 0;
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.gridx = 0;
			gridBagConstraints34.insets = new Insets(4, 4, 6, 16);
			gridBagConstraints34.gridy = 0;
			jPanel_ask2 = new JPanel();
			jPanel_ask2.setLayout(new GridBagLayout());
			jPanel_ask2.add(getJButton_Continue(), gridBagConstraints34);
			jPanel_ask2.add(getJButton_Stop(), gridBagConstraints35);
		}
		return jPanel_ask2;
	}

	/**
	 * This method initializes jPanel_save
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_save() {
		if (jPanel_save == null) {
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints31.ipadx = 42;
			gridBagConstraints31.gridy = 0;
			jPanel_save = new JPanel();
			jPanel_save.setLayout(new GridBagLayout());
			jPanel_save.add(getJPanel_saveTxt(), gridBagConstraints31);
		}
		return jPanel_save;
	}

	/**
	 * This method initializes jPanel_askTxt
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_saveTxt() {
		if (jPanel_saveTxt == null) {
			jPanel_saveTxt = new JPanel();
			jPanel_saveTxt.setLayout(new GridBagLayout());
		}
		return jPanel_saveTxt;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_save0() {
		if (jPanel_save0 == null) {
			jPanel_save0 = new JPanel();
			jPanel_save0.setLayout(new GridBagLayout());
		}
		return jPanel_save0;
	}

	/**
	 * This method initializes jPanel_ask1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_save1() {
		if (jPanel_save1 == null) {
			GridBagConstraints gridBagConstraints37 = new GridBagConstraints();
			gridBagConstraints37.fill = GridBagConstraints.BOTH;
			gridBagConstraints37.gridy = 2;
			gridBagConstraints37.weightx = 1.0;
			gridBagConstraints37.weighty = 1.0;
			gridBagConstraints37.gridwidth = 1;
			gridBagConstraints37.anchor = GridBagConstraints.WEST;
			gridBagConstraints37.insets = new Insets(4, 16, 4, 16);
			gridBagConstraints37.gridx = 1;
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.insets = new Insets(0, 10, 0, 0);
			gridBagConstraints36.gridy = 0;
			gridBagConstraints36.fill = GridBagConstraints.NONE;
			gridBagConstraints36.weightx = 0.0;
			gridBagConstraints36.weighty = 0.0;
			gridBagConstraints36.ipadx = 0;
			gridBagConstraints36.ipady = 0;
			gridBagConstraints36.anchor = GridBagConstraints.CENTER;
			gridBagConstraints36.gridheight = 3;
			gridBagConstraints36.gridx = 0;
			jLabel_save = new JLabel();
			jLabel_save.setText("");// All the mandatory facts have been
									// answered.\nDo you want to continue
									// configuring the system? \n Note that
									// facts still unanswered will keep\n their
									// default values, if the configuration is
									// stopped.
			jLabel_save.setFont(new Font("Dialog", Font.PLAIN, 12));
			jLabel_save.setHorizontalTextPosition(SwingConstants.TRAILING);
			jLabel_save.setIcon(new ImageIcon(getClass().getResource(
					"/icons/question.gif")));
			jPanel_save1 = new JPanel();
			jPanel_save1.setLayout(new GridBagLayout());
			jPanel_save1.add(jLabel_save, gridBagConstraints36);
			jPanel_save1.add(getJTextArea_save(), gridBagConstraints37);
		}
		return jPanel_save1;
	}

	/**
	 * This method initializes jPanel_save2
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getJPanel_save2() {
		if (jPanel_save2 == null) {
			GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
			gridBagConstraints34.gridx = 0;
			gridBagConstraints34.insets = new Insets(4, 4, 6, 20);
			gridBagConstraints34.gridy = 0;
			GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
			gridBagConstraints35.gridx = 1;
			gridBagConstraints35.insets = new Insets(4, 16, 6, 4);
			gridBagConstraints35.gridy = 0;
			GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
			gridBagConstraints36.gridx = 2;
			gridBagConstraints36.insets = new Insets(4, 40, 6, 4);
			gridBagConstraints36.gridy = 0;
			jPanel_save2 = new JPanel();
			jPanel_save2.setLayout(new GridBagLayout());
			jPanel_save2.add(getJButton_Export(), gridBagConstraints34);
			jPanel_save2.add(getJButton_Individualize(), gridBagConstraints35);
			jPanel_save2.add(getJButton_Discard(), gridBagConstraints36);
		}
		return jPanel_save2;
	}

	/**
	 * This method initializes jButton_Continue
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Continue() {
		if (jButton_Continue == null) {
			jButton_Continue = new JButton();
			jButton_Continue.setMaximumSize(new Dimension(86, 25));
			jButton_Continue.setMinimumSize(new Dimension(86, 25));
			jButton_Continue.setPreferredSize(new Dimension(86, 25));
			jButton_Continue.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_Continue.setText("Manual");
			jButton_Continue.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJDialog_AskToContinue().setVisible(false);
					continueC = true;
					updateValidQ();
				}
			});
		}
		return jButton_Continue;
	}

	/**
	 * This method initializes jButton_Stop
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Stop() {
		if (jButton_Stop == null) {
			jButton_Stop = new JButton();
			jButton_Stop.setMaximumSize(new Dimension(86, 25));
			jButton_Stop.setMinimumSize(new Dimension(86, 25));
			jButton_Stop.setPreferredSize(new Dimension(86, 25));
			jButton_Stop.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_Stop.setText("Automatic");
			jButton_Stop.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					// update tempS again (this needs to be reset because
					// "dirtied" by method checkApplicabilityDef)
					tempS = new State(currentS);

					for (QuestionType currentQ : QuestionsMap.values()) {
						if (!answeredQ.contains(currentQ)) {// TODO: possible
															// simplification:
															// we can check just
															// the set of facts
															// still unset
							giveDefAnswer(currentQ, true);// invoke the
															// giveDefAnswer
															// method just to
															// set and not to
															// check, because
															// this has been
															// done before by
															// checkAppicabilityDef

							currentS = tempS;
							currentS.qs.add(currentQ.getId());// register the
																// question just
																// answered
							log("s" + states.size() + ".qs: " + currentS.qs.toString());// updates
																				// log
																				// (done
																				// before
																				// updating
																				// list
																				// states,
																				// so
																				// as
																				// to
																				// get
																				// the
																				// correct
																				// state
																				// number
																				// starting
																				// from
																				// 0=s_init)
							states.add(new State(currentS));// creates a new
															// state with the
															// info of currentS
															// and stores it in
															// list states
							answeredQ.addElement(currentQ);// Now it is answered
						}
					}
					validQ.removeAllElements();
					// currentS=tempS;//could be simpliefied, anyway: this is
					// used for showing answered questions correctly
					jDialog_AskToContinue.setVisible(false);
					log("Configuration process completed with default values.");
					exportConfigurationTemporarily();
					getJDialog_AskToSave().setVisible(true);// prompt to export
															// the results
				}
			});
		}
		return jButton_Stop;
	}

	/**
	 * This method initializes jButton_Save
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_Export() {
		if (jButton_Export == null) {
			jButton_Export = new JButton();
			jButton_Export.setMaximumSize(new Dimension(100, 25));
			jButton_Export.setMinimumSize(new Dimension(100, 25));
			jButton_Export.setPreferredSize(new Dimension(100, 25));
			jButton_Export.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_Export.setText("Export DCL");
			jButton_Export.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exportConfiguration();
					getJDialog_AskToSave().setVisible(false);
				}
			});
		}
		return jButton_Export;
	}

	/**
	 * @return a configuration
	 */
	private DCL getDCL() {
		DCL dcl = new DCL();
		dcl.setAuthor(qml.getAuthor());
		dcl.setName(qml.getName());
		dcl.setReference(qml.getReference());
		for (Entry<String, String> fact : currentS.vs.entrySet()) {
			FactType factQML = FactsMap.get(fact.getKey());
			com.processconfiguration.dcl.FactType factDCL = new com.processconfiguration.dcl.FactType();
			factDCL.setDescription(factQML.getDescription());
			factDCL.setId(factQML.getId());
			factDCL.setValue(Boolean.parseBoolean(fact.getValue()));
			factDCL.setDeviates(factQML.isDefault() != Boolean.parseBoolean(fact.getValue()));
			dcl.getFact().add(factDCL);
		}

		return dcl;
	}

	/**
         * Export the current configuration to {@link #fInConf}, creating it as a temporary file if necessary.
         */
	protected void exportConfigurationTemporarily() {
		try {
			if (fInConf == null) {  // create temporary file
				fInConf = File.createTempFile("temp", ".dcl");
			}
			JAXBContext.newInstance("com.processconfiguration.dcl")
			           .createMarshaller()
			           .marshal(getDCL(), fInConf);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Export the current configuration to the filesystem using a file chooser dialog.
	 */
	protected void exportConfiguration() {
		File fExport = null;
		FactType factQML;
		String filePath;
		fileChooser = new JFileChooser(".");
		fileChooser.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 11));
		fileChooser.setLocation(100, 100);
		fileChooser.setFileFilter(new DCLFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnVal = fileChooser.showSaveDialog(Main.this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {
				filePath = fileChooser.getSelectedFile().getCanonicalPath();
				if (!filePath.endsWith(".dcl"))
					filePath = filePath + ".dcl";
				fExport = new File(filePath);
				log("Configuration exported to: " + fExport.getName() + ".");

				// marshal
				JAXBContext.newInstance("com.processconfiguration.dcl")
				           .createMarshaller()
				           .marshal(getDCL(), fExport);

				fInConf = fExport;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method initializes jButton_individualize
	 * 
	 * @return javax.swing.JButton
	 */
	boolean flag = true;
	boolean flg = true;

	private JButton getJButton_Individualize() {
		if (jButton_Individualize == null) {
			jButton_Individualize = new JButton();
			jButton_Individualize.setMaximumSize(new Dimension(100, 25));
			jButton_Individualize.setMinimumSize(new Dimension(100, 25));
			jButton_Individualize.setPreferredSize(new Dimension(100, 25));
			jButton_Individualize.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_Individualize.setText("Individualize");
			jButton_Individualize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// check fInModel and fInMap availability
					flag = false;
					getJDialog_AskToSave().setVisible(false);
					getExportMenuItem().setEnabled(true);
					exportConfigurationTemporarily();
					if (fInMap != null && fInModel != null) {
						individualize();
					} else {
						getJDialog_CG().setVisible(true);
						getJButton_exit().setEnabled(true);
						getJButton_Individualize().setEnabled(false);						
						//getJButton_ok().setText("Individualize");
						flg = false;

					}
				}
			});
		}
		return jButton_Individualize;
	}

	/**
	 * This method initializes jButton_Discard
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getJButton_Discard() {
		if (jButton_Discard == null) {
			jButton_Discard = new JButton();
			jButton_Discard.setMaximumSize(new Dimension(86, 25));
			jButton_Discard.setMinimumSize(new Dimension(86, 25));
			jButton_Discard.setPreferredSize(new Dimension(86, 25));
			jButton_Discard.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_Discard.setText("Continue");
			jButton_Discard.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJDialog_AskToSave().setVisible(false);
					getExportMenuItem().setEnabled(true);
				}
			});
		}
		return jButton_Discard;
	}

	/**
	 * This method initializes jTextArea_save
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_save() {
		if (jTextArea_save == null) {
			jTextArea_save = new JTextArea();
			jTextArea_save.setBackground(new Color(235, 233, 237));
			jTextArea_save.setOpaque(false);
			jTextArea_save.setEditable(false);
			jTextArea_save.setLineWrap(true);
			jTextArea_save.setWrapStyleWord(true);
			jTextArea_save.setText(getSaveText());
		}
		return jTextArea_save;
	}

        /**
         * @return explanatory text presented within {@link #jTextArea_save}.
         */
        protected String getSaveText() {
            return "All the facts have been set correctly.\n"
		+ "\nClick Export DCL to export this configuration.\n"
		+ "Click Individualize to link this configuration to a\n"
		+ "process model and individualize the latter.";
        }

	// this method tries to give the default answer to an input question and
	// returns true if the default answer can be given, otherwise it returns
	// false
	// NOTE: this method could be replaced by compl(s) and done just for the
	// unset facts so far, without looking at the questions
	protected boolean giveDefAnswer(QuestionType currentQ, boolean skipCheckConf) {

		for (String fID : currentQ.getMapQFL()) {
			if (retrieveFact(fID).isDefault()) {// if the other facts have not
												// been set yet
				if ((tempS.vs.get(fID)).equals(UNSET)) {// just for facts that
														// appear in the
														// question for the
														// first time
					tempS.vs.put(fID, TRUE);// update vs and t
					tempS.t.add(fID);
				}
				// else if (tempS.vs.get(fID).equals(FALSE))
				// return false;//it deviates, the default setting cannot be
				// applied
			} else {// is false by default
				if ((tempS.vs.get(fID)).equals(UNSET)) {// just for facts that
														// appear in the
														// question for the
														// first time
					tempS.vs.put(fID, FALSE);// update vs and t
					tempS.f.add(fID);
				}
				// else if (tempS.vs.get(fID).equals(TRUE))
				// return false;//it deviates, the default setting cannot be
				// applied
			}
		}
		if (!skipCheckConf) {// not needed the second time, i.e. when users have
								// chosen to stop the configuation process,
								// because the checking has been done previously
								// by checkApplicabilityDef
			if (!bddc.isViolated(tempS.vs)) {// removed buttonsList.keySet() as
												// paramenter
				log(currentQ.getId() + " can be answered with default values.");
				tempAQ.addElement(currentQ);
				return true;
			} else {// if all the facts have been set with a value which
					// deviates from default, then the default answer can't be
					// applied
				log(currentQ.getId() + " cannot be answered with default values.");
				return false;
			}
		} else
			return true;
	}

	/**
	 * This method initializes jScrollPane_log
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_log() {
		if (jScrollPane_log == null) {
			jScrollPane_log = new JScrollPane();
			jScrollPane_log.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(4, 4, 4, 4),
					BorderFactory.createLineBorder(Color.gray, 1)));
			jScrollPane_log.setViewportView(getJText_log());
		}
		return jScrollPane_log;
	}

	/**
	 * This method initializes jTextArea_ask
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_ask() {
		if (jTextArea_ask == null) {
			jTextArea_ask = new JTextArea();
			jTextArea_ask.setBackground(new Color(235, 233, 237));
			jTextArea_ask.setOpaque(false);
			jTextArea_ask.setEditable(false);
			jTextArea_ask.setLineWrap(true);
			jTextArea_ask.setWrapStyleWord(true);
			jTextArea_ask
					.setText("The system can be configured automatically using default values, since there are no mandatory facts.\n"
							+ "\nDo you want to configure it manually or automatically?");
		}
		return jTextArea_ask;
	}

	/**
	 * This method initializes jButton_legendC
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_legendC() {
		if (jButton_legendC == null) {
			jButton_legendC = new JButton();
			jButton_legendC.setText("Legend");
			jButton_legendC.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_legendC.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJDialog_LegendC().setVisible(true);
				}
			});
		}
		return jButton_legendC;
	}

	/**
	 * This method initializes jScrollPane_QDep
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_QDep() {
		if (jScrollPane_QDep == null) {
			jScrollPane_QDep = new JScrollPane();
			jScrollPane_QDep.setMinimumSize(new Dimension(29, 46));
			jScrollPane_QDep.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder(BorderFactory
							.createEmptyBorder(0, 0, 0, 0), "Dependencies",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.PLAIN, 13), new Color(51, 94, 168)),
					BorderFactory.createLineBorder(Color.lightGray, 1)));
			jScrollPane_QDep.setViewportView(getJTextArea_QDep());
		}
		return jScrollPane_QDep;
	}

	/**
	 * This method initializes jTextArea_QDep
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea_QDep() {
		if (jTextArea_QDep == null) {
			jTextArea_QDep = new JTextArea();
			jTextArea_QDep.setBorder(BorderFactory
					.createEmptyBorder(2, 4, 2, 4));
			jTextArea_QDep.setText("");
			jTextArea_QDep.setEditable(false);
			jTextArea_QDep.setFont(new Font("Dialog", Font.PLAIN, 13));
			jTextArea_QDep
					.setToolTipText("The questions this question depends on.");
		}
		return jTextArea_QDep;
	}

	private void openConfiguration(final File file) {
		fInConf = file;

		/// Hack for AotF demo
		if (fInConf != null) {
			exportConfigurationTemporarily();
		}
		/// End hack for AotF demo
	}

	/**
         * Application entry point.
         *
         * If command line arguments are passed, the first is considered to be the QML file.
         *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Main application = new Main();

                JFrame frame = new JFrame();
                frame.add(application);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
                                Main.class.getResource("/icons/Q4.gif")));
                frame.setLocation(new java.awt.Point(100, 100));
                frame.setMinimumSize(new java.awt.Dimension(900, 600));
                frame.setSize(900, 600);
                frame.setJMenuBar(application.getJJMenuBar());
                frame.setContentPane(application.getJContentPane());
                frame.setTitle("Synergia - Quaestio v. 1.0");
                application.setVisible(true);
                frame.pack();
		frame.setVisible(true);

		// Parse command line arguments
                int j = -1;  // the index within args in which "-apromore_model" occurs
                String user = null;
               	for (int i=0; i< args.length; i++) {
			switch (args[i]) {
			case "-apromore_model":
				if (i+3 >= args.length) {
					throw new IllegalArgumentException("-apromore_model without id/branch/version");
				}
                                j = i;
				i += 3;
                                break;
			case "-cmap_url":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-cmap_url without URL");
				}
				application.setLinkedCmap(new UrlCmap(args[i]));
                                break;
			case "-model_url":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-model_url without URL");
				}
				application.setLinkedProcessModel(new UrlProcessModel(args[i]));
                                break;
			case "-qml_url":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-qml_url without URL");
				}
				application.openUrlQuestionnaireModel(args[i]);
                                break;
			case "-editor_url":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-editor_url without URL");
				}
				application.setEditorURL(new URL(args[i]));
                                break;
			case "-cmap":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-cmap without filename");
				}
				application.setLinkedCmap(new FileCmap(new File(args[i])));
				break;
			case "-dcl":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-dcl without filename");
				}
				application.openConfiguration(new File(args[i]));
				break;
			case "-model":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-model without filename");
				}
				application.setLinkedProcessModel(
					new FileProcessModel(new File(args[i]))
				);
				break;
			case "-qml":
				if (++i >= args.length) {
					throw new IllegalArgumentException("-qml without filename");
				}
				application.openQuestionnaireModel(new File(args[i]));
				break;
			case "-user":
                                if (++i >= args.length) {
					throw new IllegalArgumentException("-user without user name");
                                }
                                user = args[i];
				break;
			default:
				throw new IllegalArgumentException("Unknown parameter: " + args[i]);
			}

                        // -apromore_model and -user can occur in either order, so we've deferred processing them until here
			if (j != -1) {
				application.setLinkedProcessModel(new ApromoreProcessModel(
                                        new URI("http://localhost/manager/services"),  // manager SOAP endpoint
					Integer.valueOf(args[j+1]),   // process ID
					args[j+2],                    // branch
					args[j+3],                    // version number
					application,                  // Swing parent component
					user));                       // user name
			}
		}
	}

        /**
         * Read a C-BPMN process model from the Apromore manager service.
         *
         * @param processName  the name of a process on the Apromore server
         */
	private void openApromoreProcess(final int processId, String branch, String version) throws Exception {

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/managerClientContext.xml");
		ManagerService manager = (ManagerService) context.getAutowireCapableBeanFactory().getBean("managerClient");
		ExportFormatResultType result = manager.exportFormat(
			processId,		// process ID
			null,                   // process name
			branch,                 // branch
			version,                // version number,
			"BPMN 2.0",             // nativeType,
			null,                   // annotation name,
			false,                  // with annotations?
			null,			// owner
			Collections.EMPTY_SET   // canoniser properties
		);
		BpmnDefinitions bpmn = BpmnDefinitions.newInstance(result.getNative().getInputStream(), true /* validate */);
		bpmn.marshal(System.out, true);
	}

	/**
	 * Update a C-BPMN process model on the Apromore manager service.
         *
         * @param bpmn the updated process model
	 */
	private void updateApromoreProcess(final BpmnDefinitions bpmn) throws Exception {

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bpmn.marshal(baos, true);

		// Send to the server
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/managerClientContext.xml");
		ManagerService manager = (ManagerService) context.getAutowireCapableBeanFactory().getBean("managerClient");
		manager.updateProcess(
			0,		// session code
			null,		// user name
			"BPMN 2.0",	// native type
			0,		// process ID
			null,		// domain
			null,		// process name
			null,		// original branch name
			null,		// new branch name
			"0.0",		// version number
            "0.0",		// original version number
			null,		// pre-version
			new ByteArrayInputStream(baos.toByteArray())
		);
	}

	/**
	 * This is the default constructor
	 */
	public Main() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		// initiates the ListModels
		validQ = new QuestionTypeListModel();
		answeredQ = new QuestionTypeListModel();
		states = new ArrayList<State>();
		buttonsList = new HashMap<String, JToggleButton>();
		mandatoryF = new HashSet<String>();
		XORquestions = new HashSet<String>();
		skippedQuestions = new HashSet<String>();
		first = true;
		showDef = true;
		showMan = true;
		continueC = false;
		showSkippableQuestions = true;
	}

	/**
	 * This method initializes jPanel_questions0
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_Q() {
		if (jPanel_Q == null) {
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.fill = GridBagConstraints.BOTH;
			gridBagConstraints41.gridx = 0;
			gridBagConstraints41.gridy = 1;
			gridBagConstraints41.ipadx = 109;
			gridBagConstraints41.ipady = 45;
			gridBagConstraints41.weightx = 1.0;
			gridBagConstraints41.weighty = 0.5;
			gridBagConstraints41.insets = new Insets(0, 0, 1, 0);
			GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
			gridBagConstraints40.fill = GridBagConstraints.BOTH;
			gridBagConstraints40.gridy = 0;
			gridBagConstraints40.ipadx = 109;
			gridBagConstraints40.ipady = 45;
			gridBagConstraints40.weightx = 1.0;
			gridBagConstraints40.weighty = 0.5;
			gridBagConstraints40.gridx = 0;
			jPanel_Q = new JPanel();
			jPanel_Q.setLayout(new GridBagLayout());
			jPanel_Q.setMinimumSize(new java.awt.Dimension(360, 320));
			jPanel_Q.setPreferredSize(new java.awt.Dimension(360, 320));
			jPanel_Q.add(getJScrollPane_questions(), gridBagConstraints40);
			jPanel_Q.add(getJScrollPane_answeredQ(), gridBagConstraints41);
		}
		return jPanel_Q;
	}

	/**
	 * This method initializes jScrollPane_answeredQ
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_answeredQ() {
		if (jScrollPane_answeredQ == null) {
			jScrollPane_answeredQ = new JScrollPane();
			jScrollPane_answeredQ
					.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(BorderFactory
									.createEtchedBorder(EtchedBorder.RAISED),
									"Answered Questions",
									TitledBorder.DEFAULT_JUSTIFICATION,
									TitledBorder.DEFAULT_POSITION, new Font(
											"Dialog", Font.BOLD, 13),
									new Color(51, 94, 168)),
							BorderFactory.createCompoundBorder(BorderFactory
									.createEmptyBorder(3, 4, 5, 4),
									BorderFactory.createLineBorder(
											Color.lightGray, 1))));
			// jScrollPane_answeredQ.setMinimumSize(new Dimension(278, 166));
			// jScrollPane_answeredQ.setPreferredSize(new Dimension(278, 183));
			jScrollPane_answeredQ.setViewportView(getJList_answeredQ());
		}
		return jScrollPane_answeredQ;
	}

	/**
	 * This method initializes jList_answeredQ
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJList_answeredQ() {
		if (jList_answeredQ == null) {
			jList_answeredQ = new JList(answeredQ);
			jList_answeredQ.setCellRenderer(new QuestionRender());
			jList_answeredQ
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jList_answeredQ.setFont(new Font("Dialog", Font.PLAIN, 13));
			// jList_answeredQ.setPreferredSize(new Dimension(315, 150));
			// jList_answeredQ.setSize(new Dimension(315, 150));
			// jList_answeredQ.setMinimumSize(new Dimension(315, 150));
			// jList_answeredQ.setMaximumSize(new Dimension(2147483647,
			// 2147483647));
			jList_answeredQ.setBorder(BorderFactory.createEmptyBorder(4, 4, 4,
					4));
			jList_answeredQ.setLayoutOrientation(JList.VERTICAL);
			jList_answeredQ.addListSelectionListener(this);// when an answered
															// question is
															// selected, it can
															// be viewed in the
															// right panel, and
															// rollback can be
															// done on that
															// question
		}
		return jList_answeredQ;
	}

	/**
	 * This method initializes jPanel_questions
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_A() {
		if (jPanel_A == null) {
			GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
			gridBagConstraints39.fill = GridBagConstraints.BOTH;
			gridBagConstraints39.gridy = 4;
			gridBagConstraints39.weightx = 1.0;
			gridBagConstraints39.weighty = 1.0;
			gridBagConstraints39.ipady = 2;
			gridBagConstraints39.gridx = 0;
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 0;
			gridBagConstraints9.fill = GridBagConstraints.HORIZONTAL;
			gridBagConstraints9.insets = new Insets(0, 4, 0, 4);
			gridBagConstraints9.weighty = 1.0;
			gridBagConstraints9.weightx = 1.0;
			gridBagConstraints9.ipady = 2;
			gridBagConstraints9.gridy = 1;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints8.ipady = 10;
			gridBagConstraints8.gridwidth = 0;
			gridBagConstraints8.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints8.gridheight = 0;
			gridBagConstraints8.gridy = 2;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = GridBagConstraints.BOTH;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 3;
			gridBagConstraints7.ipadx = 0;
			gridBagConstraints7.ipady = 80;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.weighty = 1.0;
			gridBagConstraints7.anchor = GridBagConstraints.SOUTH;
			gridBagConstraints7.insets = new Insets(0, 0, 0, 0);
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.gridy = 2;
			gridBagConstraints6.ipadx = 0;
			gridBagConstraints6.ipady = 120;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.anchor = GridBagConstraints.NORTH;
			gridBagConstraints6.insets = new Insets(0, 0, 2, 0);
			gridBagConstraints6.gridx = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.insets = new java.awt.Insets(0, 4, 0, 4);
			gridBagConstraints5.gridy = 1;
			gridBagConstraints5.ipadx = 0;
			gridBagConstraints5.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints5.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints5.ipady = 10;
			gridBagConstraints5.weightx = 0.5;
			gridBagConstraints5.gridx = 1;
			jPanel_A = new JPanel();
			jPanel_A.setLayout(new GridBagLayout());
			jPanel_A.setBorder(BorderFactory.createCompoundBorder(BorderFactory
					.createTitledBorder(BorderFactory
							.createEtchedBorder(EtchedBorder.RAISED),
							"Question Inspector",
							TitledBorder.DEFAULT_JUSTIFICATION,
							TitledBorder.DEFAULT_POSITION, new Font("Dialog",
									Font.BOLD, 13), new Color(51, 94, 168)),
					BorderFactory.createEmptyBorder(2, 2, 2, 2)));
			jPanel_A.setMinimumSize(new java.awt.Dimension(360, 320));
			jPanel_A.setPreferredSize(new java.awt.Dimension(360, 320));
			jPanel_A.add(getJScrollPane_A(), gridBagConstraints6);
			jPanel_A.add(getJScrollPane_AG(), gridBagConstraints7);
			jPanel_A.add(getJPanel_AQD(), gridBagConstraints9);
			jPanel_A.add(getJScrollPane_QDep(), gridBagConstraints39);
		}
		return jPanel_A;
	}

	private void updateValidQ() {// set the validQ for the current state
		QuestionType precedingQ;
		// The file has been already open. Note that validQ has already been
		// initialized as an empty DefaultListModel in initiate()
		boolean isValid;
		for (QuestionType currentQ : QuestionsMap.values()) {
			isValid = false;
			if (!currentS.qs.contains(currentQ.getId())) {// currentQ not
															// answered - we
															// need also to
															// check the
															// questions already
															// valid (WHY???)
				// NOTE: same as answeredQ.contains(currentQ)
				// PERFORMANCES: we can avoid checking among the valid
				// questions: add "!validQ.contains(currentQ)" now
				for (List<String> currentPreQ : currentQ.getPreQL()) {// at
																		// least
																		// one
																		// PreQElement
																		// must
																		// exist
					if (currentPreQ.size() == 0) {// preQElement doesn't contain
													// any QRef -> no
													// preconditions -> currentQ
													// is a valid question
						isValid = true;
						break;
					}
					for (String currentQRef : currentPreQ) {
						precedingQ = QuestionsMap.get(currentQRef);
						if (currentS.qs.contains(precedingQ.getId()))
							// NOTE: same as answeredQ.contains(currentQ)
							isValid = true;
						else {
							isValid = false;
							break;
						}
					}
					if (isValid)
						break;// if it is valid no need to check the other preQ
								// (also if there's just one)

				}// end for currentPreQ
				if (isValid) {
					if (!skippable(currentQ)) {// a question is added to the
												// Valid Questions List if it
												// hasn't been added yet and is
												// not skippable
						currentQ.setSkippable(false);// may be too much, but
														// should work
						skippedQuestions.remove(currentQ.getId());// tries to
																	// remove
																	// the
																	// question
																	// in case
																	// it was
																	// skippable
																	// before
						if (!validQ.contains(currentQ)) {
							validQ.addElement(currentQ);
						}
					} else {// if valid but skippable, don't show and put in the
							// answered questions
						currentQ.setSkippable(true);
						skippedQuestions.add(currentQ.getId());
						log(currentQ.getId() + " is skippable");
						if (validQ.contains(currentQ))// if the question was
														// already shown in the
														// Valid Questions list
														// and now has turned
														// out to be skippable
							validQ.removeElement(currentQ);
						currentS.qs.add(currentQ.getId());// register the
															// question just
															// answered. Note:
															// this will be
															// always considered
															// answered,
															// although skipped
															// questions are set
															// not to be
															// visible.
						log("s" + states.size() + ".qs: " + currentS.qs.toString());// updates
																			// log
																			// (done
																			// before
																			// updating
																			// list
																			// states,
																			// so
																			// as
																			// to
																			// get
																			// the
																			// correct
																			// state
																			// number
																			// starting
																			// from
																			// 0=s_init)
						states.add(new State(currentS));// creates a new state
														// with the info of
														// currentS and stores
														// it in the states list
						if (showSkippableQuestions)
							answeredQ.addElement(currentQ);
					}
				} else if (skippable(currentQ)) {// even if it is not valid but
													// skippable, add to the
													// skipped questions, so
													// that it can be
													// automatically answered
													// when the question becomes
													// valid
					currentQ.setSkippable(true);
					skippedQuestions.add(currentQ.getId());// add to
															// skippedQuestions
															// anyway
				}
				isValid = false;
			}
		}// end for currentQ
	}

	// this method checks if currentQ is skippable. If yes, it returns true,
	// else false.
	private boolean skippable(QuestionType currentQ) {
		FactType currentF;
		int forceable;
		boolean skippable = true;

		for (String currentFID : currentQ.getMapQFL()) {// for each fact in the
														// question
			currentF = retrieveFact(currentFID);
			String currentFValue = currentS.vs.get(currentF.getId());// the fact
																		// is
																		// always
																		// contained
			// TODO: verify why XOR facts are left UNSET

			// for each unset fact, the method checks whether it can be forced
			// to a value, given the constraints and the answers given so far
			if (currentFValue.equals(UNSET)) {// the fact is still unset. CAN
												// THIS BIAS THE SKIPPABILITY OF
												// A QUESTION?

				if ((forceable = bddc.isForceable(currentF.getId())) == 1) {// forceable
																			// to
																			// true:
																			// vs
																			// and
																			// t
																			// need
																			// to
																			// be
																			// updated
					currentS.vs.put(currentF.getId(), TRUE);// update vs and t
					currentS.t.add(currentF.getId());
					bddc.setFact(currentF.getId(), "1");
				} else if (forceable == -1) {// forceable to false
					currentS.vs.put(currentF.getId(), FALSE);// update vs and f:
																// vs and t
																// needs to be
																// updated
					currentS.f.add(currentF.getId());
					bddc.setFact(currentF.getId(), "0");
				} else
					// if at least a fact cannot be forced, then the question is
					// not skippable
					skippable = false;
			}
		}
		return skippable;
	}

	/**
	 * This method initializes jPanel_info2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_info() {
		if (jPanel_info == null) {
			GridLayout gridLayout2 = new GridLayout();
			gridLayout2.setRows(1);
			gridLayout2.setVgap(0);
			gridLayout2.setHgap(0);
			jLabel_info3 = new JLabel();
			jLabel_info3.setText("Reference: ");
			jLabel_info3.setFont(new Font("Dialog", Font.PLAIN, 13));
			jLabel_info3.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					0, 5, 0, 0));
			jLabel_info2 = new JLabel();
			jLabel_info2.setText("Author(s): ");
			jLabel_info2.setFont(new Font("Dialog", Font.PLAIN, 13));
			jLabel_info2.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					0, 5, 0, 0));
			jLabel_info = new JLabel();
			jLabel_info.setText("Name: ");
			jLabel_info.setFont(new Font("Dialog", Font.PLAIN, 13));
			jLabel_info.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					0, 5, 0, 0));
			jPanel_info = new JPanel();
			jPanel_info.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
					"Questionnaire Model Information", TitledBorder.LEFT,
					TitledBorder.DEFAULT_POSITION, new Font("Dialog",
							Font.BOLD, 13), new Color(51, 94, 168)));
			jPanel_info.setLayout(gridLayout2);
			jPanel_info.add(jLabel_info, null);
			jPanel_info.add(jLabel_info2, null);
			jPanel_info.add(jLabel_info3, null);
		}
		return jPanel_info;
	}

	/**
	 * This method initializes jDialog_log
	 * 
	 * @return javax.swing.JDialog
	 */
	private JFrame getJDialog_log() {
		if (jDialog_log == null) {
			jDialog_log = new JFrame();
			jDialog_log.setSize(new Dimension(243, 397));
			jDialog_log.setIconImage(Toolkit.getDefaultToolkit().getImage(
					getClass().getResource("/icons/log.png")));
			jDialog_log.setTitle("Log File");
			jDialog_log.setMinimumSize(new java.awt.Dimension(70, 100));
			jDialog_log.setLocation(new Point(950, 100));
			jDialog_log.setContentPane(getJContentPane_log());
		}
		return jDialog_log;
	}

	/**
	 * This method initializes jContentPane_log
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane_log() {
		if (jContentPane_log == null) {
			jContentPane_log = new JPanel();
			jContentPane_log.setLayout(new BorderLayout());
			jContentPane_log.add(getJPanel_logButton(),
					java.awt.BorderLayout.SOUTH);
			jContentPane_log.add(getJScrollPane_log(), BorderLayout.CENTER);
		}
		return jContentPane_log;
	}

	/**
	 * This method initializes jPanel_logButton
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_logButton() {
		if (jPanel_logButton == null) {
			jPanel_logButton = new JPanel();
			jPanel_logButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2,
					2));
			jPanel_logButton.add(getJButton_SaveLog(), null);
		}
		return jPanel_logButton;
	}

	/**
	 * This method initializes jButton_SaveLog
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_SaveLog() {
		if (jButton_SaveLog == null) {
			jButton_SaveLog = new JButton();
			jButton_SaveLog.setText("Save Log");
			jButton_SaveLog.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 11));
			jButton_SaveLog.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					File fLog = null;
					String filePath;
					fileChooser = new JFileChooser(".");
					fileChooser.setFont(new java.awt.Font("Dialog",
							java.awt.Font.PLAIN, 11));
					fileChooser.setLocation(100, 100);
					fileChooser.setFileFilter(new TXTFilter());
					fileChooser.setAcceptAllFileFilterUsed(false);
					int returnVal = fileChooser.showSaveDialog(Main.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						try {
							filePath = fileChooser.getSelectedFile()
									.getCanonicalPath();
							if (!filePath.endsWith(".txt"))
								filePath = filePath + ".txt";
							fLog = new File(filePath);
							PrintWriter pwLog = new PrintWriter(
									new FileOutputStream(fLog));
							pwLog.print(jArea_log.getText());
							pwLog.close();
							log("Log exported to: " + fLog.getName());
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			});
		}
		return jButton_SaveLog;
	}

	/**
	 * This method initializes jList_log
	 * 
	 * @return javax.swing.JList
	 */
	JTextArea getJText_log() {
		if (jArea_log == null) {
			jArea_log = new JTextArea();
			jArea_log.setMargin(new Insets(5, 5, 5, 5));
			jArea_log.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			jArea_log.setEditable(false);
		}
		return jArea_log;
	}

	/**
	 * This method initializes jPanel_tree2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_questions2() {
		if (jPanel_Q2 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.CENTER);
			jPanel_Q2 = new JPanel();
			jPanel_Q2.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
			jPanel_Q2.setLayout(flowLayout);
			jPanel_Q2.add(getJButton_FInspector(), null);
			jPanel_Q2.add(getJButton_QTree(), null);
		}
		return jPanel_Q2;
	}

	/**
	 * This method initializes jButton_treeCurrent
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_FInspector() {
		if (jButton_FInspector == null) {
			jButton_FInspector = new JButton();
			jButton_FInspector.setText("Fact Inspector");
			jButton_FInspector.setEnabled(true);
			jButton_FInspector.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_FInspector.setIcon(new ImageIcon(getClass().getResource(
					"/icons/FI.gif")));
			jButton_FInspector.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJDialog_FI().setVisible(true);
				}
			});
		}
		return jButton_FInspector;
	}

	/**
	 * This method initializes jButton_treeList
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_QTree() {
		if (jButton_QTree == null) {
			jButton_QTree = new JButton();
			jButton_QTree.setText("Questions Tree");
			jButton_QTree.setEnabled(false);
			jButton_QTree.setFont(new Font("Dialog", Font.PLAIN, 12));
		}
		return jButton_QTree;
	}

	/**
	 * This method initializes jPanel_AnswerButtons
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_answer2() {
		if (jPanel_A2 == null) {
			jPanel_A2 = new JPanel();
			jPanel_A2.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
			jPanel_A2.add(getJButton_answerPerform(), null);
			jPanel_A2.add(getJButton_answerDefault(), null);
			jPanel_A2.add(getJButton_answerUndo(), null);
			jPanel_A2.add(getJButton_answerSave(), null);
			jPanel_A2.add(getJButton_answerSaveDCL(), null);
		}
		return jPanel_A2;
	}

	/**
	 * This method initializes jButton_answerPerform
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_answerPerform() {
		if (jButton_answerPerform == null) {
			jButton_answerPerform = new JButton();
			jButton_answerPerform.setText("Answer");
			jButton_answerPerform.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_answerPerform.addActionListener(this);
			jButton_answerPerform.setEnabled(false);
		}
		return jButton_answerPerform;
	}

	/**
	 * This method initializes jButton_answerUndo
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_answerUndo() {
		if (jButton_answerUndo == null) {
			jButton_answerUndo = new JButton();
			jButton_answerUndo.setText("Rollback");
			jButton_answerUndo.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_answerUndo.addActionListener(this);
			jButton_answerUndo.setEnabled(false);
		}
		return jButton_answerUndo;
	}

	/**
	 * This method initializes jButton_answerSave
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_answerSave() {
		if (jButton_answerSave == null) {
			jButton_answerSave = new JButton();
			jButton_answerSave.setText("Save model");
			jButton_answerSave.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_answerSave.addActionListener(this);
			jButton_answerSave.setEnabled(false);
		}
		return jButton_answerSave;
	}

	/**
	 * This method initializes jButton_answerSaveDCL
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton_answerSaveDCL() {
		if (jButton_answerSaveDCL == null) {
			jButton_answerSaveDCL = new JButton();
			jButton_answerSaveDCL.setText("Save DCL");
			jButton_answerSaveDCL.setFont(new Font("Dialog", Font.PLAIN, 12));
			jButton_answerSaveDCL.addActionListener(this);
			jButton_answerSaveDCL.setEnabled(false);
		}
		return jButton_answerSaveDCL;
	}

	/**
	 * This method initializes jScrollPane_tree
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane_questions() {
		if (jScrollPane_enabledQ == null) {
			jScrollPane_enabledQ = new JScrollPane();
			jScrollPane_enabledQ
					.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder(BorderFactory
									.createEtchedBorder(EtchedBorder.RAISED),
									"Valid Questions",
									TitledBorder.DEFAULT_JUSTIFICATION,
									TitledBorder.DEFAULT_POSITION, new Font(
											"Dialog", Font.BOLD, 13),
									new Color(51, 94, 168)),
							BorderFactory.createCompoundBorder(BorderFactory
									.createEmptyBorder(3, 4, 5, 4),
									BorderFactory.createLineBorder(
											Color.lightGray, 1))));
			jScrollPane_enabledQ.setViewportView(getJList_enabledQ());
		}
		return jScrollPane_enabledQ;
	}

	/**
	 * This method initializes jList_questions
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJList_enabledQ() {
		if (jList_enabledQ == null) {
			jList_enabledQ = new JList(validQ);
			jList_enabledQ.setCellRenderer(new QuestionRender());
			jList_enabledQ
					.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jList_enabledQ.setFont(new Font("Dialog", Font.PLAIN, 13));
			jList_enabledQ.setSize(new Dimension(315, 150));
			// jList_enabledQ.setMinimumSize(new Dimension(315, 150));
			// jList_enabledQ.setPreferredSize(new Dimension(315, 150));
			// jList_enabledQ.setMaximumSize(new Dimension(2147483647,
			// 2147483647));
			jList_enabledQ.setBorder(BorderFactory
					.createEmptyBorder(4, 4, 4, 4));
			jList_enabledQ.setLayoutOrientation(JList.VERTICAL);
			jList_enabledQ.addListSelectionListener(this);// when a question not
															// answered is
															// selected
		}
		return jList_enabledQ;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	public JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.fill = GridBagConstraints.BOTH;
			gridBagConstraints11.insets = new Insets(2, 8, 5, 4);
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints11.ipadx = 60;
			gridBagConstraints11.ipady = 0;
			gridBagConstraints11.gridy = 2;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.weighty = 0.0;
			gridBagConstraints4.insets = new java.awt.Insets(10, 8, 5, 8);
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.NORTH;
			gridBagConstraints4.gridwidth = 3;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.insets = new java.awt.Insets(0, 0, 0, 0);
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			gridBagConstraints3.weighty = 0.1;
			gridBagConstraints3.weightx = 0.7;
			gridBagConstraints3.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridwidth = 1;
			gridBagConstraints2.weightx = 0.3;
			gridBagConstraints2.weighty = 0.1;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			gridBagConstraints2.gridy = 3;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.fill = GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints1.gridwidth = 1;
			gridBagConstraints1.gridheight = 1;
			gridBagConstraints1.ipadx = 80;
			gridBagConstraints1.ipady = 0;
			gridBagConstraints1.insets = new Insets(2, 4, 5, 8);
			gridBagConstraints1.gridy = 2;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints.weighty = 1.0;
			gridBagConstraints.weightx = 1.0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(2, 10, 5, 5);
			gridBagConstraints.ipadx = 0;
			gridBagConstraints.gridwidth = 1;
			gridBagConstraints.gridheight = 1;
			gridBagConstraints.gridy = 2;
			jContentPane = this;  //new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.setMinimumSize(new java.awt.Dimension(800, 600));
			jContentPane.setPreferredSize(new java.awt.Dimension(800, 600));
			jContentPane.add(getJPanel_A(), gridBagConstraints1);
			jContentPane.add(getJPanel_questions2(), gridBagConstraints2);
			jContentPane.add(getJPanel_answer2(), gridBagConstraints3);
			jContentPane.add(getJPanel_info(), gridBagConstraints4);
			jContentPane.add(getJPanel_Q(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	public JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getFileMenu());
			jJMenuBar.add(getOptionsMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			fileMenu.add(getOpenCMMenuItem());
			fileMenu.add(getLinkCMenuItem());
			fileMenu.add(getSaveModelMenuItem());
			// fileMenu.add(getLoadCMenuItem());
			fileMenu.add(getSaveCMenuItem());
			fileMenu.add(getExportMenuItem());
			fileMenu.add(getExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * This method initializes jMenu
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getOptionsMenu() {
		if (optionsMenu == null) {
			optionsMenu = new JMenu();
			optionsMenu.setText("Options");
			optionsMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			optionsMenu.add(getShowSQ_MenuItem());
			optionsMenu.add(getShowDef_MenuItem());
			optionsMenu.add(getShowMan_MenuItem());
			optionsMenu.add(getShowFI_MenuItem());
			// optionsMenu.add(getCutMenuItem());
			// optionsMenu.add(getCopyMenuItem());
			// optionsMenu.add(getPasteMenuItem());
		}
		return optionsMenu;
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
			helpMenu.setFont(new Font("Dialog", Font.PLAIN, 12));
			helpMenu.add(getAboutMenuItem());
			helpMenu.add(getLogMenuItem());
		}
		return helpMenu;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			if (System.getProperty("os.name").toLowerCase().indexOf("mac") != -1) {
				exitMenuItem.setText("Quit");
                        	exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
			} else {
				exitMenuItem.setText("Exit");
			}
			exitMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
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
			aboutMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJDialog_About().setVisible(true);
				}
			});
		}
		return aboutMenuItem;
	}

	private JDialog getJDialog_About() {
		if (jDialog_About == null) {
                        jDialog_About = new JDialog(/*Main.this, "About", true*/);
			jDialog_About.setTitle("About");
			jDialog_About.setSize(new Dimension(500, 250));
			jDialog_About.setMaximumSize(new Dimension(500, 250));
			jDialog_About.setLocation(new Point(400, 350));
			jDialog_About.setResizable(false);
			jDialog_About.setContentPane(getJScrollPane_About());
		}
		return jDialog_About;
	}

	private JDialog getJDialog_LegendC() {
		if (jDialog_LegendC == null) {
			jDialog_LegendC = new JDialog();
			jDialog_LegendC.setSize(new Dimension(414, 229));
			jDialog_LegendC.setLocation(new Point(990, 580));
			jDialog_LegendC.setMaximumSize(new Dimension(184, 126));
			jDialog_LegendC.setPreferredSize(new Dimension(184, 126));
			jDialog_LegendC.setMinimumSize(new Dimension(184, 126));
			jDialog_LegendC.setResizable(false);
			jDialog_LegendC.setTitle("Constraints Lagend");
			jDialog_LegendC.setContentPane(getJScrollPane_LegendC());
		}
		return jDialog_LegendC;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLogMenuItem() {
		if (logMenuItem == null) {
			logMenuItem = new JMenuItem();
			logMenuItem.setText("Show Log File");
			logMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			logMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getJDialog_log().setVisible(true);
				}
			});
		}
		return logMenuItem;
	}

	// /**
	// * This method initializes jMenuItem
	// *
	// * @return javax.swing.JMenuItem
	// */
	// private JMenuItem getCutMenuItem() {
	// if (cutMenuItem == null) {
	// cutMenuItem = new JMenuItem();
	// cutMenuItem.setText("Cut");
	// cutMenuItem.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN,
	// 11));
	// cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
	// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
	// }
	// return cutMenuItem;
	// }
	//
	// /**
	// * This method initializes jMenuItem
	// *
	// * @return javax.swing.JMenuItem
	// */
	// private JMenuItem getCopyMenuItem() {
	// if (copyMenuItem == null) {
	// copyMenuItem = new JMenuItem();
	// copyMenuItem.setText("Copy");
	// copyMenuItem.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN,
	// 11));
	// copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
	// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
	// }
	// return copyMenuItem;
	// }
	//
	// /**
	// * This method initializes jMenuItem
	// *
	// * @return javax.swing.JMenuItem
	// */
	// private JMenuItem getPasteMenuItem() {
	// if (pasteMenuItem == null) {
	// pasteMenuItem = new JMenuItem();
	// pasteMenuItem.setText("Paste");
	// pasteMenuItem.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN,
	// 11));
	// pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
	// Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
	// }
	// return pasteMenuItem;
	// }

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getLinkCMenuItem() {
		if (LinkCMenuItem == null) {
			LinkCMenuItem = new JMenuItem();
			LinkCMenuItem.setText("Link to Process Model");
			LinkCMenuItem.setEnabled(true);
			LinkCMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			LinkCMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
			LinkCMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							getJDialog_CG().setVisible(true);
						}
					});
		}
		return LinkCMenuItem;
	}

        /**
         * Save action.
         *
         * User invokes this by either the File menu or the Save button.
         */
	private javax.swing.Action saveAction = new javax.swing.AbstractAction() {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			try {
				fInModel.update(getPartiallyConfiguredModel());
				System.err.println("Successfully updated");
			} catch (Exception ex) {
				System.err.println("Failed to update: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	};

	/**
         * This method initializes jMenuItem
         * 
         * @return javax.swing.JMenuItem
         */
        private JMenuItem getSaveModelMenuItem() {
		if (saveModelMenuItem == null) {
			saveModelMenuItem = new JMenuItem();
			saveModelMenuItem.setText("Save Process Model");
			saveModelMenuItem.setEnabled(false);
			saveModelMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			saveModelMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
			saveModelMenuItem
					.addActionListener(saveAction);
		}
		return saveModelMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveCMenuItem() {
		if (saveCMenuItem == null) {
			saveCMenuItem = new JMenuItem();
			saveCMenuItem.setText("Save Partial Domain Configuration");
			saveCMenuItem.setEnabled(false);
			saveCMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			saveCMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | Event.SHIFT_MASK, true));
		}
		return saveCMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getExportMenuItem() {
		if (exportMenuItem == null) {
			exportMenuItem = new JMenuItem();
			exportMenuItem.setText("Export Domain Configuration");
			exportMenuItem.setEnabled(false);
			exportMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
			exportMenuItem
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							exportConfiguration();
						}
					});
		}
		return exportMenuItem;
	}

	/**
	 * This method initializes jMenuItem
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenCMMenuItem() {
		if (openCMMenuItem == null) {
			openCMMenuItem = new JMenuItem();
			openCMMenuItem.setText("Open Questionnaire Model");
			openCMMenuItem.setFont(new java.awt.Font("Dialog",
					java.awt.Font.PLAIN, 12));
			openCMMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
					Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), true));
			openCMMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent ev) {
					// clear File
					 //fInModel = null;
					 //fInMap = null;
					 //fInConf = null;

					fileChooser = new JFileChooser(".");
					fileChooser.setFont(new java.awt.Font("Dialog",
							java.awt.Font.PLAIN, 12));
					fileChooser.setLocation(100, 100);
					fileChooser.addChoosableFileFilter(new QMLFilter());
					int returnVal = fileChooser.showOpenDialog(Main.this);

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						fIn = fileChooser.getSelectedFile();
						log("Opening: " + fIn.getName() + ".");
						try {
							openQuestionnaireModel(fIn);

						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						log("Open command cancelled by user.");
					}
					getJText_log().setCaretPosition(
						getJText_log().getDocument().getLength());

				}
			});
		}
		return openCMMenuItem;
	}

	/**
	 * Attempt to open a QML file.
	 *
	 * @param fIn  an input file in QML format
	 * @throws JAXBException if the QML file can't be processed
	 */
	void openQuestionnaireModel(final File fIn) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.qml");
		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement qmlElement = (JAXBElement) u.unmarshal(fIn); // creates the root element from XML file
		qml = (QMLType) qmlElement.getValue();
		readModel();
	}

	void openUrlQuestionnaireModel(final String urlString) throws IOException, JAXBException {
                log("Opening QML model");
		URLConnection connection = new URL(urlString).openConnection();
                connection.setRequestProperty("Authorization", "Basic " + DatatypeConverter.printBase64Binary("admin:password".getBytes()));
		JAXBElement qmlElement = (JAXBElement) JAXBContext.newInstance("com.processconfiguration.qml")
		                                                  .createUnmarshaller()
		                                                  .unmarshal(connection.getInputStream()); // creates the root element from XML file
		qml = (QMLType) qmlElement.getValue();
		readModel();
                log("Opened QML model");
	}

	private void readModel() {
		log("Reading model");
		if (!first) {// clear everything if it isn't the first time
			states.clear();// clear the states list
			validQ.clear();// clear validQ JList
			answeredQ.clear();// clear answeredQ JList
			buttonsList.clear();// clear the buttonList (this is necessary
								// because an user can end a configuraiton
								// without ansering all the questions)
			mandatoryF.clear();// clear the set of mandatory facts
			XORquestions.clear();// clear the set of XOR questions
			currentS.vs.clear();
			currentS.t.clear();
			currentS.f.clear();
			currentS.qs.clear();
			getExportMenuItem().setEnabled(false);// a configuration cannot be
													// exported if just open
			cleanQInspector();
			if (getJDialog_FI().isVisible())
				cleanFInspector();
			continueC = false;
			QuestionsMap.clear();// clear the questions
			FactsMap.clear();// clear the facts
		}
                getJPanel_info();  // TODO - remove this
		jLabel_info.setText("Name: " + qml.getName());// to print the name of
														// the model
		jLabel_info2.setText("Author: " + qml.getAuthor());// to print the
															// author
		jLabel_info3.setText("Reference: " + qml.getReference());// to print the
																	// reference

		createSets();// initializes QuestionsMap and FactsMap

		currentS = new State(FactsMap.keySet());// creates a temp state used by
												// the algorithm
		log("s" + states.size() + ".qs: " + currentS.qs.toString());// updates
																				// log
																				// (done
																				// before
																				// updating
																				// list
																				// states,
																				// so
																				// as
																				// to
																				// get
																				// the
																				// correct
																				// state
																				// number
																				// starting
																				// from
																				// 0=s_init)
		states.add(new State(FactsMap.keySet()));// stores s_init in the states
													// list
		// TODO: use threads for showing a progress bar while loading the
		// constraints into memory
		// Thread rct= new ReadCThread(cm);
		// rct.start();
		initBDDC();
		retrieveMandatoryF();
		retrieveXORQ();
		if (mandatoryF.size() == 0) {
			getJDialog_AskToContinue().setVisible(true);
		} else
			updateValidQ();// start configuring
		first = false;
		log("Read model");
	}

	private void createSets() {
		QuestionsMap = new TreeMap<String, QuestionType>();
		for (QuestionType q : qml.getQuestion()) {
			q.setMapQFL(q.getMapQF());
			q.setPreQL();
			QuestionsMap.put(q.getId(), q);
		}

		FactsMap = new TreeMap<String, FactType>();
		for (FactType f : qml.getFact()) {
			f.setPreFL();
			FactsMap.put(f.getId(), f);
		}
	}

	private void initBDDC() {// sets the conjunctive normal form output + initializes the facts variables with "u+fID"
		log("Initializing BDDService");
		StringBuffer init = new StringBuffer("set cnf;\n");
		for (String fID : FactsMap.keySet()) {
			init.append(fID + " := u" + fID.substring(1) + "; ");
		}

		//bddc = new ExecBDDC(qml.getConstraints());  // launches the process bddc for constraints checking
		bddc = new JavaBDDService(qml.getConstraints());  // uses the JavaDBB library for constraints checking
		bddc.init(init.toString());// initiates the bddc variables with empty arguments
		// bddc.init("init");
		log("Initialized BDDService");
	}

	// this method retrieves all the mandatory facts
	private void retrieveMandatoryF() {
		for (FactType currentF : FactsMap.values()) {
			if (currentF.isMandatory()) {
				mandatoryF.add(currentF.getId());
			}
		}
	}

	// this method retrieves all the partial-XOR questions
	private void retrieveXORQ() {
		for (QuestionType currentQ : QuestionsMap.values()) {
			if (bddc.isXOR(currentQ.getMapQFL())) {
				XORquestions.add(currentQ.getId());
			}
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		int i, forceable = 0;
		JToggleButton b;
		JLabel l;
		JPanel p;
		FactType currentF;
		ButtonGroup groupXOR = null;
		StringBuffer buffer;
		XORQuestion = false;
		currentSelection = null;// unset the string, as a new question is about
								// to be shown

		if (e.getValueIsAdjusting() == false) {
			JList sourceList = (JList) e.getSource();
			if ((i = sourceList.getSelectedIndex()) != -1) {
				buttonsList.clear();// for each new question
				jPanel_AF.removeAll();// frees the panel of all the old facts
				jPanel_AF.repaint();
				jTextArea_QDep.setText("");
				if (sourceList.equals(jList_enabledQ)) {
					// answered=false;
					jList_answeredQ.clearSelection();
					selectedQ = validQ.get(i);
					jButton_answerUndo.setEnabled(false);

				} else if (sourceList.equals(jList_answeredQ)) {
					// answered=true;
					jList_enabledQ.clearSelection();
					selectedQ = answeredQ.get(i);
					jButton_answerPerform.setEnabled(false);
					jButton_answerDefault.setEnabled(false);
					jButton_answerUndo.setEnabled(true);
				}
				// set Question
				jLabel_AQID.setText("<html>q<sub>"
						+ selectedQ.getId().substring(1) + "</sub></html>");// OLD:
																			// jLabel_AQID.setText("ID: "+selectedQ.getId());//TODO:
																			// do
																			// we
																			// want
																			// to
																			// keep
																			// the
																			// new
																			// format
																			// for
																			// q1
																			// instead
																			// of
																			// 1?
				jLabel_AQID.setBorder(javax.swing.BorderFactory
						.createEmptyBorder(0, 10, 0, 0));
				// jLabel_AQID.setEnabled(true);
				jLabel_A.setText(selectedQ.toString());
				jTextArea_AG.setText(selectedQ.getGuidelines());
				jTextArea_AG.setCaretPosition(0);

				// set the question dependencies
				for (Iterator<ArrayList<String>> i1 = selectedQ.getPreQL()
						.iterator(); i1.hasNext();) {
					List<String> preQL_el = i1.next();
					if (preQL_el.size() == 0)
						break;
					buffer = new StringBuffer("");
					for (Iterator<String> i2 = preQL_el.iterator(); i2
							.hasNext();) {
						String currentQID = i2.next();
						// NOTE: here qX should be replaced with the subscript,
						// but HTML code doesn't work in a StringBuffer
						buffer.append(currentQID);
						if (i2.hasNext())
							buffer.append(" and ");// AND (\u2227)
					}
					if (i1.hasNext())
						buffer.append(" or ");// OR (\u2228)
					jTextArea_QDep.append(buffer.toString());
					jTextArea_QDep.setCaretPosition(0);
				}
				buffer = null;

				tempS = new State(currentS);// copy the current state into a
											// temporary one, used for checking
											// whether or not the facts setting
											// is allowed

				// if (XORQuestion)
				if (XORquestions.contains(selectedQ.getId())) {
					XORQuestion = true;
					groupXOR = new ButtonGroup();// to be moved; JUST ONE GROUP
				}

				for (String currentFID : selectedQ.getMapQFL()) {// for each
																	// fact in
																	// the
																	// question
					currentF = retrieveFact(currentFID);
					// set the initial value according to preceeding facts:
					// check the current vs

					if (XORQuestion) {
						b = new JRadioButton(currentF.getDescription());
						groupXOR.add(b);
					} else
						b = new JCheckBox(currentF.getDescription());

					// b.setMinimumSize(new
					// Dimension(getJScrollPane_A().getWidth()-100,18));
					b.setPreferredSize(new Dimension(
							jScrollPane_A.getWidth() - 100, 20));
					b.setMaximumSize(new Dimension(
							jScrollPane_A.getWidth() - 100, 20));
					b.setSize(new Dimension(jScrollPane_A.getWidth() - 100, 20));
					b.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN,
							13));

					String currentFValue = currentS.vs.get(currentF.getId());// the
																				// fact
																				// is
																				// always
																				// contained

					// already set to true
					if (currentFValue.equals(TRUE)) {// already set to true
						b.setSelected(true);
						b.setEnabled(false);
					}
					// already set to false
					else if (currentFValue.equals(FALSE)) {// already set to
															// false
						b.setSelected(false);
						b.setEnabled(false);
					}

					// forceable to true (CONDITIONALLY EVALUATED)
					else if ((forceable = bddc.isForceable(currentF.getId())) == 1) {
						tempS.vs.put(currentF.getId(), TRUE);// update vs and t
						tempS.t.add(currentF.getId());
						b.setSelected(true);
						b.setEnabled(false);
					}
					// forceable to false (CONDITIONALLY EVALUATED)
					else if (forceable == -1) {
						tempS.vs.put(currentF.getId(), FALSE);// update vs and f
						tempS.f.add(currentF.getId());
						b.setSelected(false);
						b.setEnabled(false);
					}
					buttonsList.put(currentF.getId(), b);// always add the
															// button: this
															// allows users to
															// use the fact
															// inspector for
															// checking the
															// fact's
															// dependencies
					b.addMouseListener(this);// for the fact inspector

					p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));// TODO:
																			// in
																			// case
																			// change
																			// this
																			// in
																			// order
																			// to
																			// have
																			// icons
																			// aligned
																			// to
																			// the
																			// right
					p.setPreferredSize(new Dimension(
							jScrollPane_A.getWidth() - 25, 25));
					p.setMaximumSize(new Dimension(
							jScrollPane_A.getWidth() - 25, 25));// getJScrollPane_A().getWidth(),25
					p.setSize(new Dimension(jScrollPane_A.getWidth() - 25, 25));
					p.setAlignmentX(JPanel.LEFT_ALIGNMENT);
					p.add(b);

					// adds T icon for fact true by default
					if (showDef && currentF.isDefault()) {
						l = new JLabel(new ImageIcon(getClass().getResource(
								"/icons/default.gif")));
						l.setToolTipText("This fact is true by default");
					} else
						l = new JLabel(new ImageIcon(getClass().getResource(
								"/icons/empty.gif")));
					l.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
							8, 0, 0));
					p.add(l);

					// adds M icon for Mandatory fact
					if (showMan && currentF.isMandatory()) {
						l = new JLabel(new ImageIcon(getClass().getResource(
								"/icons/mandatory.gif")));
						l.setToolTipText("This fact must be answered");
					} else
						l = new JLabel(new ImageIcon(getClass().getResource(
								"/icons/empty.gif")));
					l.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
							8, 0, 0));
					p.add(l);

					// adds the fact ID (maybe TO REMOVE)
					l = new JLabel("<html>f<sub>"
							+ currentF.getId().substring(1) + "</sub></html>");// TODO:
																				// removed
																				// f
																				// for
																				// new
																				// format
																				// (do
																				// we
																				// want
																				// to
																				// keep
																				// it
																				// like
																				// that?)
					l.setFont(new Font("Dialog", Font.PLAIN, 12));
					l.setForeground(Color.GRAY);
					l.setToolTipText("Fact ID");
					l.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
							8, 0, 0));
					p.add(l);

					jPanel_AF.add(p);
				}// end for

				if (sourceList.equals(jList_enabledQ)) {
					int deviations = 0;
					State defS = new State(tempS);
					// Try if the default aswer for this question can be given
					// (only if there aren't deviations of any of the fact
					// setting from their default value)
					for (String fID : buttonsList.keySet()) {
						if (retrieveFact(fID).isDefault()) {// if the other
															// facts have not
															// been set yet
							if ((defS.vs.get(fID)).equals(UNSET)) {// just for
																	// facts
																	// that
																	// appear in
																	// the
																	// question
																	// for the
																	// first
																	// time
								defS.vs.put(fID, TRUE);// update vs and t
								defS.t.add(fID);
							} else if (defS.vs.get(fID).equals(FALSE))
								deviations++;// NOTE that deviations aren't
												// allowed just for giving
												// default answers, but not when
												// we have to check if all the
												// remaining unset facts can
												// assume their default value
						} else {// if the actual configuration of the facts for
								// this question deviates from their default
							if ((defS.vs.get(fID)).equals(UNSET)) {// just for
																	// facts
																	// that
																	// appear in
																	// the
																	// question
																	// for the
																	// first
																	// time
								defS.vs.put(fID, FALSE);// update vs and t
								defS.f.add(fID);
							} else if (tempS.vs.get(fID).equals(TRUE))
								deviations++;
						}
					}
					if (!bddc.isViolated(defS.vs) && deviations == 0)// removed
																		// buttonsList.keySet()
																		// as
																		// paramenter
						jButton_answerDefault.setEnabled(true);
					else
						// if all the facts have been set with a value which
						// deviates from default, then the default answer can't
						// be applied
						jButton_answerDefault.setEnabled(false);

					defS.vs.clear();
					defS.qs.clear();
					defS.t.clear();
					defS.f.clear();// empty the collections of defS

					// Try if the current answer for this question (derived from
					// already answered facts appearing in other questions
					// and/or forced ones) can be given
					for (String fID : buttonsList.keySet()) {
						if ((tempS.vs.get(fID)).equals(UNSET)) {// if the other
																// facts have
																// not been set
																// yet
							tempS.vs.put(fID, FALSE);// update vs and t
							tempS.f.add(fID);// TODO: update log file
						}
					}
					if (!bddc.isViolated(tempS.vs))// check the validity of
													// state tempS
						jButton_answerPerform.setEnabled(true);
					else
						jButton_answerPerform.setEnabled(false);

					// Note: tempS can't be cleaned because it is needed when a
					// button is pressed, otherwise it should be copied again
					// from currentS in MoudeClicked

				}
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
		// TODO: register the event in an IF condition

		StringBuffer buffer;
		JToggleButton b = (JToggleButton) e.getComponent();

		String fID = returnFact(b);// fID can't be null

		FactType currentF = retrieveFact(fID);// retrieve an IFactType given a
												// String fID

		// retrieve the fact from the button
		if (getJDialog_FI().isVisible()) {
			cleanFInspector();

			jTextField_FDescription.setText(currentF.getDescription());
			jTextField_FID.setText(currentF.getId());
			jTextField_FI.setText(currentF.getImpact());
			jTextField_FDef.setText(String.valueOf(currentF.isDefault()));
			jTextField_FMan.setText(String.valueOf(currentF.isMandatory()));

			buffer = new StringBuffer("");
			retrieveQuestions(currentF.getId(), buffer);
			jTextField_FinQ.setText(buffer.toString());
			buffer = null;

			// TODO: add inference of constrains for a fact from the general
			// constraints and uncomment the following 3 lines
			// StringTokenizer st = new
			// StringTokenizer(currentF.getConstraints(),",");
			// while (st.hasMoreTokens())
			// jTextArea_FC.append(st.nextToken()+"\n");//Constraints over that
			// fact
			jTextArea_FC.setCaretPosition(0);// in order not to have the
												// vertical scrollbar to the
												// bottom

			for (Iterator<ArrayList<String>> i1 = currentF.getPreFL()
					.iterator(); i1.hasNext();) {
				List<String> preFL_el = i1.next();
				if (preFL_el.size() == 0)
					break;
				buffer = new StringBuffer("");
				for (Iterator<String> i2 = preFL_el.iterator(); i2.hasNext();) {
					String currentFID = i2.next();
					buffer.append(currentFID + ": " + retrieveFact(currentFID)
							+ " (on ");
					// for each question that fact appears in...
					retrieveQuestions(currentFID, buffer);
					buffer.append(")");// close questions
					if (i2.hasNext())
						buffer.append(" and ");// note: this is not needed for
												// the JLabel "Questions:"
				}
				buffer.delete(buffer.length() - 4, buffer.length() - 1);
				if (i1.hasNext())
					buffer.append(" or ");// note: this is not needed for the
											// JLabel "Questions:"
				jTextArea_FDep.append(buffer.toString());
				jTextArea_FDep.setCaretPosition(0);
			}
			buffer = null;
			jTextArea_FG.setText(currentF.getGuidelines());
			jTextArea_FG.setCaretPosition(0);
		}
	}

	private String returnFact(JToggleButton b) {
		for (Entry iterable_e : buttonsList.entrySet()) {
			if (iterable_e.getValue().equals(b))
				return (String) iterable_e.getKey();
		}
		return null;
	}

	// this method appends in the input buffer all the questions the input fID
	// appears in
	private void retrieveQuestions(String fID, StringBuffer buffer) {
		for (QuestionType currentQ : QuestionsMap.values()) {
			for (String currentFID : currentQ.getMapQFL()) {
				if (currentFID.equals(fID))
					buffer.append(currentQ.getId() + ", ");
			}
		}
		buffer.delete(buffer.length() - 2, buffer.length());
	}

	// this method retrieves the FactType object related to the input fID
	private FactType retrieveFact(String fID) {
		return FactsMap.get(fID);
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		// only tick buttons are registered with this event so far
		JToggleButton b = (JToggleButton) e.getSource();
		String fID = returnFact(b); // int forceable;
		if (b.isEnabled()) {
			if (b.isSelected()) {
				tempS.vs.put(fID, TRUE);// update vs and t
				tempS.t.add(fID);
				tempS.f.remove(fID);
				if (XORQuestion && currentSelection != null) {// if the question
																// is XOR and a
																// selection has
																// already been
																// done before
					tempS.vs.put(currentSelection, FALSE);// update vs and t
					tempS.f.add(currentSelection);
					tempS.t.remove(currentSelection);
				}
				currentSelection = fID;
			} else if (!b.isSelected()) {
				tempS.vs.put(fID, FALSE);// update vs and t
				tempS.f.add(fID);
				tempS.t.remove(fID);
			}
			// check if the configuration can be valid
			if (!bddc.isViolated(tempS.vs))
				jButton_answerPerform.setEnabled(true);
			else
				jButton_answerPerform.setEnabled(false);

		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void actionPerformed(java.awt.event.ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("Answer")) {// NOTE: users can only give valid
										// answers, otherwise the button
										// "Answer" is disabled
			for (String fID : buttonsList.keySet()) {
				// override the facts valuation for the question being answered
				// with the selected values
				if ((buttonsList.get(fID)).isSelected()) {
					currentS.vs.put(fID, TRUE);
					currentS.t.add(fID);
					bddc.setFact(fID, "1");
				} else {
					currentS.vs.put(fID, FALSE);
					currentS.f.add(fID);
					bddc.setFact(fID, "0");
				}
			}
		} else if (command.equals("Default Answer")) {
			for (String fID : buttonsList.keySet()) {
				// override the facts valuation for the question being answered
				// with their default values
				if (retrieveFact(fID).isDefault()) {
					currentS.vs.put(fID, TRUE);
					currentS.t.add(fID);
					bddc.setFact(fID, "1");
				} else {
					currentS.vs.put(fID, FALSE);
					currentS.f.add(fID);
					bddc.setFact(fID, "0");
				}
			}
		} else if (command.equals("Rollback")) {
			int pos, cState;
			String valueNEW;

			pos = states.get(states.size() - 1).qs.indexOf(selectedQ.getId());// get
																				// the
																				// position
																				// of
																				// the
																				// question
																				// to
																				// rollback
																				// in
																				// the
																				// last
																				// state
			for (int i = states.size() - 1; i > pos; i--) {
				states.remove(i);
				answeredQ.remove(i - 1);
			}
			cState = states.size() - 1;
			currentS = new State(states.get(cState));// the last state after
														// removing
			log("Rolled back from " + selectedQ.getId()
						+ " onwards. Current state: s" + cState + ".qs: "
						+ currentS.qs.toString());

			for (String fID : currentS.vs.keySet()) {// restores the facts
														// values in bddc
														// exactly to the state
														// being restored
				valueNEW = currentS.vs.get(fID);
				if (valueNEW.equals("unset"))
					bddc.setFact(fID, "u" + fID);
				else if (valueNEW.equals("true"))
					bddc.setFact(fID, "1");
				else
					bddc.setFact(fID, "0");
			}

			cleanQInspector();
			if (getJDialog_FI().isVisible()) {
				cleanFInspector();
			}
			getExportMenuItem().setEnabled(false);// after a Rollback at least a
													// question changes its
													// status to unanswered, so
													// the configuration cannot
													// be exported as not
													// finished
			if (!continueC)
				checkMandatoryF();

			validQ.clear();
			updateValidQ();

		} else if (command.equals("Save model")) {
			//testLiveConnect();
			saveAction.actionPerformed(e);

		} else if (command.equals("Save DCL")) {
			try {
				ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/META-INF/spring/filestoreClientContext.xml");
				FileStoreService service = (FileStoreService) applicationContext.getAutowireCapableBeanFactory().getBean("fileStoreClientExternal");
				JFileChooser chooser = new JFileChooser(new DavFileSystemView(service));

				chooser.setDialogTitle("Save DCL");
                                chooser.setFileFilter(new FileNameExtensionFilter("DCL questionnaire responses", "dcl"));
                                if (chooser.showSaveDialog(Main.this) == JFileChooser.APPROVE_OPTION) {
                                    // Save the selected DCL file
                                    System.err.println("File chooser selected " + chooser.getSelectedFile());
                                    URI fileURI = chooser.getSelectedFile().toURI();
                                    System.err.println("File chooser selection as URI " + fileURI);
                                    URI selectedURI = new URI(fileURI.getRawPath());
                                    System.err.println("File chooser selection as relative URI " + selectedURI);
				    URI uri2 = new URI(service.getBaseURI().toString() + selectedURI.toString());
                                    System.err.println("Resolved URI in DAV store " + uri2 + " from service base URI " + service.getBaseURI());

				    ByteArrayOutputStream baos = new ByteArrayOutputStream();
				    JAXBContext.newInstance("com.processconfiguration.dcl")
                                               .createMarshaller()
                                               .marshal(getDCL(), baos);
				    service.put(uri2.toString(), baos.toByteArray(), "application/xml");

                                    JOptionPane.showMessageDialog(Main.this, "Saved questionnaire responses " + chooser.getSelectedFile());
                                }
				
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(
                                    null,
                                    "Unable to save DCL: " + ex,
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                                );
			}
		}

		if (command.equals("Answer") || command.equals("Default Answer")) {// common
																			// activities
			validQ.removeElement(selectedQ);// the question is no more valid

			currentS.qs.add(selectedQ.getId());// register the question just
												// answered
			log("s" + states.size() + ".qs: " + currentS.qs.toString());  // updates log (done before updating list
									              // states, so as to get the correct state
									              // number starting from 0=s_init)
			states.add(new State(currentS));// creates a new state with the info
											// of currentS and stores it in list
											// states
			answeredQ.addElement(selectedQ);// Now it is answered

			selectedQ = null;// just to clean memory a bit...
			updateValidQ();// calls this method to calculate the new valid
							// questions
			jButton_answerPerform.setEnabled(false);// disable the answer button
			jButton_answerDefault.setEnabled(false);// disable the default
													// answer button
			tempS.vs.clear();
			tempS.t.clear();
			tempS.f.clear();
			tempS.qs.clear();
			tempS = null;// frees the temporary state

			cleanQInspector();
			if (getJDialog_FI().isVisible()) {
				cleanFInspector();
			}
			if (validQ.size() != 0) {// doesn't make sense to check for
										// mandatory facts if all the questions
										// have been already answered
				if (!continueC)
					checkMandatoryF();
			} else {
				// getJDialog_AskToSave().setVisible(true);//prompt to export
				// the results
				cFlag = false;
				// log("Configuration process completed");
				exportConfigurationTemporarily();// create temporary DCL file
				getExportMenuItem().setEnabled(true);
				getJDialog_AskToSave().setVisible(true);// prompt to export
														// result
			}
		}

		// Saving is allowed as long as there are answered questions
		if (jButton_answerSave != null) {
			jButton_answerSave.setEnabled(answeredQ != null && answeredQ.getSize() > 0);
		}
		if (jButton_answerSaveDCL != null) {
			jButton_answerSaveDCL.setEnabled(answeredQ != null && answeredQ.getSize() > 0);
		}
                if (saveModelMenuItem != null) {
			saveModelMenuItem.setEnabled(answeredQ != null && answeredQ.getSize() > 0);
		}

		/// Hack for AotF demo
		if (fInConf != null) {
			exportConfigurationTemporarily();
		}
		if (fInMap != null && fInModel != null) {
			try {
				showModel(getPartiallyConfiguredModel());
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		/// End hack for AotF demo
	}

        /** Test code, method overridden in {@link QuaestioApplet}. */
	/*
	protected void testLiveConnect() {
		System.out.println("Triggered testLiveConnect entry point");
	}
	*/

	/**
         * @return the BPMN process model with the current questionnaire answers applied according to the cmap
         */
        private BpmnDefinitions getPartiallyConfiguredModel() throws Exception {
		
		BpmnDefinitions bpmn = fInModel.getBpmn();
		CMAP cmap = fInMap.getCMAP();
		DCL dcl = getDCL();

		System.err.println("Checkpoint 1");

		// Apply the configuration mapping for the facts provided so far from the questionnaire
		System.err.println("Checkpoint 2");
		org.apromore.bpmncmap.Main.configure(bpmn, cmap, dcl);

		// Tidy up the configured model to remove trivial gates, etc
		System.err.println("Checkpoint 3");
		com.processconfiguration.ConfigurationAlgorithm.configure(bpmn);

		return bpmn;
	}

	/**
	 * Display a C-BPMN model.
         *
	 * @param bpmn  a (partially) configured C-BPMN model
	 */
	protected void showModel(final BpmnDefinitions bpmn) throws Exception {

		if (editorURL == null) { return; }

		// Serialize the individualized BPMN
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bpmn.marshal(baos, true);
		//System.out.println("Request data: " + URLEncoder.encode(baos.toString("utf-8")));

		/*
		// Delegate remaining animation process to the animation script
		Process process = Runtime.getRuntime().exec("/Users/raboczi/Project/apromore/Extras/Quaestio/animate.sh");
		int exitCode = process.waitFor();
		*/

		// Convert the BPMN to JSON
		HttpURLConnection c = (HttpURLConnection) (new URL(editorURL, "editor/bpmnimport")).openConnection();
		c.setRequestMethod("POST");
		c.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                c.setDoOutput(true);
		OutputStream out = c.getOutputStream();
		out.write(("data=" + URLEncoder.encode(baos.toString("utf-8"))).getBytes());

		c.connect();

		System.out.println("Reponse code: " + c.getResponseCode());
		System.out.println("Reponse message: " + c.getResponseMessage());
		System.out.println("Content type: " + c.getContentType());

		assert c.getResponseCode() == HttpURLConnection.HTTP_OK;  // TODO - these ought to be error-handled, not asserted
		assert c.getContentType().equals("application/json");

		String s = "";
		InputStream in = c.getInputStream();
		int b;
		while ((b = in.read()) != -1) {
			s += (char) b;
		}
		in.close();
                System.out.println("Data: " + s);

		// Delete any previously-extant JSON
		HttpURLConnection c3 = (HttpURLConnection) (new URL(editorURL, "p/model/root-directory;Test.signavio.xml")).openConnection();
		c3.setRequestMethod("DELETE");
                c3.connect();

		System.out.println("Reponse code (3): " + c3.getResponseCode());
		System.out.println("Reponse message (3): " + c3.getResponseMessage());
		System.out.println("Content type (3): " + c3.getContentType());

		// Upload the JSON
		HttpURLConnection c2 = (HttpURLConnection) (new URL(editorURL, "p/model")).openConnection();
		c2.setRequestMethod("POST");
		c2.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		c2.setDoOutput(true);
		OutputStream out2 = c2.getOutputStream();
		out2.write(("comment=" +
                           "&description=" +
                           "&glossary_xml=[]" +
                           "&json_xml=" + URLEncoder.encode(s) +
		           "&svg_xml=NO_SVG_PROVIDED" +
		           "&name=Test" +
		           "&namespace=http://b3mn.org/stencilset/bpmn2.0#" +
		           "&parent=root-directory" +
		           "&type=BPMN2.0" +
		           "&views=[]").getBytes());

		c2.connect();

		System.out.println("Reponse code (2): " + c2.getResponseCode());
		System.out.println("Reponse message (2): " + c2.getResponseMessage());
		System.out.println("Content type (2): " + c2.getContentType());

		// Display the configured model with Apromore-Editor in the default browser
		browse(new URL(editorURL, "p/editor?id=root-directory%3BTest.signavio.xml"));
	}

        /**
         * @param url  the URL of the Apromore-Editor web application (e.g. <code>http://localhost:9000/editor/</code>)
         */
	public void setEditorURL(final URL url) {
		editorURL = url;
	}

	/**
         * @param url  an web page URL to (re)display
         */
	protected void browse(final URL url) throws Exception {
		Desktop.getDesktop().browse(url.toURI());
	}

	private void checkMandatoryF() {
		boolean unset = false;
		for (String mFID : mandatoryF) {
			if (currentS.vs.get(mFID).equals(UNSET)) {
				unset = true;// at least one mandatory fact is still unset
				break;
			}
		}
		if (!unset && checkApplicabilityDef()) {// all the mandatory facts have
												// been answered, now the
												// algorithm looks for the
												// applicability of the default
												// values to the remaining facts
			getJTextArea_ask()
					.setText(
							"All the mandatory facts have been answered and default values can be used for the remaining ones without violating the constraints.\nDo you want to continue the configuration or let the configuration be automatically completed?");
			getJButton_Continue().setText("Continue");
			getJButton_Stop().setText("Complete");
			getJDialog_AskToContinue().setVisible(true);// if applicable...
		}
	}

	private boolean checkApplicabilityDef() {
		tempS = new State(currentS);// copy in tempS the current state in order
									// to keep track of the configuration
									// process so far
		tempAQ = new QuestionTypeListModel(answeredQ.delegate);// temporary
																// Vector of
																// answered
																// questions
		for (QuestionType currentQ : QuestionsMap.values()) {
			if (!tempAQ.contains(currentQ)) {
				if (!giveDefAnswer(currentQ, false)) {// found a not applicable
														// default setting ->
														// exits
					return false;
				}
			}
		}
		return true;
	}

	private void cleanFInspector() {
		jTextField_FDescription.setText("");
		jTextField_FID.setText("");
		jTextField_FI.setText("");
		jTextField_FDef.setText("");
		jTextField_FMan.setText("");
		jTextField_FinQ.setText("");
		jTextArea_FC.setText("");
		jTextArea_FDep.setText("");
		jTextArea_FG.setText("");
	}

	private void cleanQInspector() {
		jPanel_AF.removeAll();// frees the Question Inspector of all the old
								// facts
		jPanel_AF.repaint();
		jLabel_A.setText(null);
		jLabel_AQID.setText("");// OLD: jLabel_AQID.setText("ID: "); Nothing
								// should be shown here if a question is not
								// visualized
		jTextArea_AG.setText(null);
	}

	// ------------------------------------------------------------------
	private JPanel jContentPaneCG = null;
	private JFrame jDialog_CG = null;
	private JTextArea aboutVersionAreaCG = null;
	private JPanel aboutContentPaneCG = null;
	private JDialog aboutDialogCG = null;
	private JMenuItem aboutMenuItemCG = null;
	private JMenu helpMenuCG = null;
	private JMenuBar jJMenuBarCG = null;
	private JPanel jPanel_ok = null;
	private JPanel jPanel_map = null;
	private JButton jButton_map = null;
	private JTextField jTextField_map = null;
	private JButton jButton_exit = null;
	private JButton jButton_ok = null;
	private JPanel jPanel_model = null;
	private JButton jButton_model = null;
	private JTextField jTextField_model = null;
	private JPanel jPanel_status = null;
	private JLabel jLabel_status = null;
	private JPanel jPanel_ok2 = null;

	/*
	 * File fInMap; File fInModel; File fInConf;
	 */
	/**
	 * This method initializes jDialog_CG: the window to allow users to link
	 * process model and c-mapper files to the current domain configuration
	 * 
	 * @return javax.swing.JDialog
	 */
	private JFrame getJDialog_CG() {
		if (jDialog_CG == null) {
			jDialog_CG = new JFrame();
			jDialog_CG.setSize(new Dimension(700, 250));
			jDialog_CG.setIconImage(Toolkit.getDefaultToolkit().getImage(
					getClass().getResource("/icons/CP.gif")));
			jDialog_CG.setTitle("Link domain configuration to process model");
			jDialog_CG.setLocation(new Point(200, 200));
			jDialog_CG.setContentPane(getJContentPane_CG());
		}
		return jDialog_CG;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane_CG() {
		if (jContentPaneCG == null) {
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
			jContentPaneCG = new JPanel();
			jContentPaneCG.setLayout(new GridBagLayout());
			if (fInConf == null) {
				jContentPaneCG.add(getJPanel_ok(), gridBagConstraints2);
			} else {
				jContentPaneCG.add(getJPanel_ok2(), gridBagConstraints2);
			}

			jContentPaneCG.add(getJPanel_map(), gridBagConstraints3);
			// jContentPaneCG(getJPanel_conf(), gridBagConstraints4);
			jContentPaneCG.add(getJPanel_model(), gridBagConstraints6);
			jContentPaneCG.add(getJPanel_status(), gridBagConstraints21);
		}
		return jContentPaneCG;
	}

	/**
	 * This method initializes jPanel_commit
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ok() {
		if (jPanel_ok == null) {
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
			jPanel_ok = new JPanel();
			jPanel_ok.setLayout(new GridBagLayout());
			jPanel_ok.add(getJButton_ok(), gridBagConstraints11);
			jPanel_ok.add(getJButton_exit(), gridBagConstraints12);
		}
		return jPanel_ok;
	}

	/**
	 * This method initializes jPanel_commit
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel_ok2() {
		if (jPanel_ok2 == null) {
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
			jPanel_ok2 = new JPanel();
			jPanel_ok2.setLayout(new GridBagLayout());
			jPanel_ok2.add(getJButton_Individualize(), gridBagConstraints11);
			jPanel_ok2.add(getJButton_exit(), gridBagConstraints12);
		}
		return jPanel_ok2;
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

					int returnVal = fileChooser.showOpenDialog(getJDialog_CG());
					String str, cond;
					int fStr, lStr, cStr;

					if (returnVal == JFileChooser.APPROVE_OPTION) {
						try {
							fInMap = new FileCmap(fileChooser.getSelectedFile());
							setLinkedCmap(fInMap);

						} catch (IllegalArgumentException iae) {
                                        		JOptionPane.showMessageDialog(
                                                           	null,
                                                                iae.getMessage(),
                                                                "Error",
                                                                JOptionPane.ERROR_MESSAGE);
							fInMap = null;
							getJButton_ok().setEnabled(false);

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

        /**
         * Link a configuration map.
	 *
         * @param file  a file in CMAP format
         * @throws IllegalArgumentException if <var>file</var> contains the characters \u2228, \u2227, \u00ac or \u22bb.
         */
        void setLinkedCmap(final Cmap cmap) throws Exception {
                /*
                String str, cond;
                int fStr, lStr, cStr;

                // schema verification
                schemaValidation.validate(
                                getClass().getResource("/xsd/CMAP.xsd"),
                                file);

                // validate CMAP format
                //checks that conditions in the CMAP file are in the correct format, i.e. they don't contain symbols like \u2227
                reader = new BufferedReader(new FileReader(file));
                while ((str = reader.readLine()) != null) {
                        fStr = str.indexOf("condition=");
                        lStr = str.indexOf("/", fStr);
                        if (fStr != -1 && lStr != -1) {
                                cond = str.substring(fStr + 11, lStr - 1);
                                cStr = cond.length();
                                if (cond.indexOf("\u2228") == 1
                                                || cond.indexOf("\u2227") == 1
                                                || cond.indexOf("\u00ac") == 1
                                                || cond.indexOf("\u22bb") == 1) {
                                        throw new IllegalArgumentException("Wrong condition format, please use \"+,-,.,xor,nor,=,=>\"");
                                }
                        }
                }
                reader.close();
                */

                getJTextField_map().setText(cmap.getText());
		this.fInMap = cmap;
        }

	protected void enableCommitButton() {
		if ((fInModel != null) && (fInMap != null)){
			getJButton_ok().setEnabled(true);			
		}
		getJButton_Individualize().setEnabled(true);
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
			if (flag) {
				jButton_exit.setEnabled(true);
			} else {
				jButton_exit.setEnabled(false);
			}
			jButton_exit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getJDialog_CG().setVisible(false);
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
	private JButton getJButton_ok() {
		if (jButton_ok == null) {
			jButton_ok = new JButton();
			jButton_ok.setText("OK");
			jButton_ok.setMaximumSize(new Dimension(100, 25));
			jButton_ok.setMinimumSize(new Dimension(100, 25));
			jButton_ok.setPreferredSize(new Dimension(100, 25));
			jButton_ok.setFont(new Font("Dialog", Font.PLAIN, 11));
			jButton_ok.setEnabled(false);
			jButton_ok.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jLabel_status
							.setText("  Output message: process model and c-mapping are linked");
					getJDialog_CG().setVisible(false);
					if (!flg) {
						individualize();
					}
				}
			});
		}
		return jButton_ok;
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
							// int returnVal =
							// fileChooser.showOpenDialog(Main.this);
							int returnVal = fileChooser
									.showOpenDialog(getJDialog_CG());

							if (returnVal == JFileChooser.APPROVE_OPTION) {
								fInModel = new FileProcessModel(fileChooser.getSelectedFile());
								try {
									setLinkedProcessModel(fInModel);

								} catch (Exception ev) {
									ev.printStackTrace();
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
	 * Link a process model to be configured.
	 *
	 * @param file  a file in C-BPMN format with the <code>.bpmn</code> extension,
         *              EPML format with a <code>.epml</code> extension,
         *              or in YAWL format with a <code>.yawl</code> extension
	 * @throws Exception if <var>fInModel</var> doesn't validate against the indicated XML schema
	 */
	void setLinkedProcessModel(final ProcessModel processModel) throws Exception {
	/*
		if (file.toString().endsWith(".bpmn")) {
			schemaValidation.validate(getClass().getResource("/xsd/BPMN20.xsd"), file);
		} else if (fInModel.toString().endsWith(".epml")) {
			schemaValidation.validate(getClass().getResource("/schema/EPML_2.0.xsd"), file);
		} else if (fInModel.toString().endsWith(".yawl")) {
			schemaValidation.validate(getClass().getResource("/schema/YAWL_Schema2.2.xsd"), file);
		}
	*/
		getJTextField_model().setText(/*file.getAbsolutePath()*/ processModel.getText());

		this.fInModel = processModel;

                // We only support incremental configuration with C-BPMN, since that's the only format
                // which the com.processconfiguration.ConfigurationAlgorithm class deals with
		try {
			showModel(processModel.getBpmn());
		} catch (Exception e) {
			System.err.println("Failed to show model: " + e);
		}
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

	private void individualize() {
		File fOutput = null;
		Configurator cg = new Configurator(new File(fInModel.getText()), new File(fInMap.getText()), fInConf);
		if ((fOutput = cg.commit()) != null) {
			getJDialog_CG().setVisible(false);
			try{
				AskToSave(fOutput);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(null,
					"Process model successfully individualized",
					"Individualization completed",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			getJDialog_CG().setVisible(false);
			JOptionPane.showMessageDialog(null, "Individualization failed",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	//this function throws an exception if the user decides not to save anymore, after the Save dialog has been open
	protected String AskToSave(File fOutput) throws NullPointerException{
		String filePath = null, fileType = null;
		File fExport = null;
		fileChooser = new JFileChooser(".");
		fileChooser
				.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 11));
		fileChooser.setLocation(100, 100);
		if (fOutput.getAbsolutePath().endsWith("epml")) {
			fileChooser.setFileFilter(new EPMLFilter());
		} else if (fOutput.getAbsolutePath().endsWith("yawl")){
			fileChooser.setFileFilter(new YAWLFilter());
		}
		// fileChooser.setFileFilter(new EPMLFilter());
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnVal = fileChooser.showSaveDialog(getJDialog_CG());

		if (returnVal == JFileChooser.APPROVE_OPTION) {

			try {
				if (fOutput.getAbsolutePath().endsWith("epml")) {
					fileType = ".epml";
				} else if (fOutput.getAbsolutePath().endsWith("yawl")){
					fileType = ".yawl";
				}
				filePath = fileChooser.getSelectedFile().getCanonicalPath();
				if (!filePath.endsWith(".epml") || !filePath.endsWith(".yawl"))
					filePath = filePath + fileType;
				fExport = new File(filePath);
				fOutput.renameTo(fExport);

				return fExport.getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fExport.getAbsolutePath();
	}

	public void log(String message) {
		getJText_log().append(message + "\n");
	}
} // @jve:decl-index=0:visual-constraint="10,10"

