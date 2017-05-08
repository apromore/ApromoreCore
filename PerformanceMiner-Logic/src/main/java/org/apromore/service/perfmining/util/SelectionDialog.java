/*
 * Bruce: 02.06.2014
 */

package org.apromore.service.perfmining.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/*
 * DialogDemo.java requires these files: CustomDialog.java images/middle.gif
 */
public class SelectionDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3010763598545140849L;
	private JLabel label;
	private final JPanel contentPanel = new JPanel();
	private JRadioButton[] radioButtons;
	private ButtonGroup group;
	private JButton okButton;

	final String boseCommand = "bose";
	final String davidCommand = "david";
	final String runCommand = "run";

	int selection; // 0: bose, 1: discriminative    

	/** Creates the GUI shown inside the frame's content pane. */
	public SelectionDialog() {
		initComponents();

		setContentPane(contentPanel);

		pack();

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2);
		this.setLocation(x, y);

		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		setVisible(true);
	}

	/** Sets the text displayed at the bottom of the frame. */
	void setLabel(String newText) {
		label.setText(newText);
	}

	private void initComponents() {
		/*
		 * Radio buttons
		 */
		int numButtons = 3;
		radioButtons = new JRadioButton[numButtons];
		group = new ButtonGroup();

		radioButtons[0] = new JRadioButton("Generate (Alphabet)Tandem Repeats, (Alphabet) Maximum Repeats,...(Bose)");
		radioButtons[0].setActionCommand(boseCommand);

		radioButtons[1] = new JRadioButton("Generate dataset for Discriminative Pattern Mining (David Lo)");
		radioButtons[1].setActionCommand(davidCommand);

		radioButtons[2] = new JRadioButton("Run Signature Discovery Plug-in");
		radioButtons[2].setActionCommand(runCommand);

		for (int i = 0; i < numButtons; i++) {
			group.add(radioButtons[i]);
		}
		radioButtons[0].setSelected(true);

		/*
		 * OK button and action performed
		 */
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});

		/*
		 * Build panel contains radio buttons and OK button
		 */
		int numChoices = radioButtons.length;
		JPanel box = new JPanel();
		JLabel label = new JLabel("Select a pattern");

		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(label);

		for (int i = 0; i < numChoices; i++) {
			box.add(radioButtons[i]);
		}
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(box, BorderLayout.PAGE_START);
		buttonPanel.add(okButton, BorderLayout.PAGE_END);
		Border padding = BorderFactory.createEmptyBorder(20, 20, 5, 20);
		buttonPanel.setBorder(padding);

		/*
		 * Add all components to a main panel
		 */
		contentPanel.setOpaque(true); //content panes must be opaque
		contentPanel.setLayout(new BorderLayout());

		label = new JLabel("Please select a type of patterns", SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		contentPanel.add(buttonPanel, BorderLayout.CENTER);
		contentPanel.add(label, BorderLayout.PAGE_END);

	}

	public int getSelection() {
		return selection;
	}

	public void okButtonActionPerformed(ActionEvent e) {
		String command = group.getSelection().getActionCommand();

		if (command == boseCommand) {
			selection = 0;
		} else if (command == davidCommand) {
			selection = 1;
		} else if (command == runCommand) {
			selection = 2;
		}

		setVisible(false);

		return;

	}

}