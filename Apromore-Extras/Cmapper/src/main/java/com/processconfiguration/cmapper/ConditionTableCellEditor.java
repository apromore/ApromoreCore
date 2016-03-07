/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

// Local classes
import com.processconfiguration.qml.FactType;
import com.processconfiguration.qml.QuestionType;

/**
 * Editing of logical conditions in a JTable cell on a popup panel rather than inline.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @see http://docs.oracle.com/javase/tutorial/uiswing/components/table.html#editor
 */
public class ConditionTableCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor {

    private JButton button;
    private Cmapper cmapper;
    private JDialog dialog;
    private GridBagLayout layout = new GridBagLayout();
    private JTextField textField;
    private String currentString;
    protected static final String EDIT = "edit";

    /** Sole constructor. */
    public ConditionTableCellEditor(Cmapper cmapper) {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        this.cmapper = cmapper;
    }

    // Implementation of TableCellEditor

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentString = (String) value;


        dialog = new JDialog(SwingUtilities.getWindowAncestor(table), APPLICATION_MODAL);
        dialog.setLayout(layout);
        dialog.setUndecorated(true);

        textField = new JTextField();
        textField.setText(currentString);
        textField.addActionListener(this);
        textField.addKeyListener(new KeyAdapter() {
            @Override public void keyTyped(KeyEvent e) {
                if ("Escape".equals(KeyEvent.getKeyText(e.getKeyChar()))) {
                    dialog.setVisible(false);
                    fireEditingStopped();
                }
            }
        });
        layout.setConstraints(textField, new GridBagConstraints(
            0, 0,                                 // grid x, y
            4,                                    // cells wide
            1,                                    // cells high
            0.0, 0.0,                             // weight x, y
            GridBagConstraints.LINE_START,        // anchor
            GridBagConstraints.HORIZONTAL,        // fill
            new Insets(0, 0, 0, 0),               // insets (top, left, bottom, right)
            0, 0                                  // padding x, y
        ));
        dialog.add(textField);
       
        addButton(".",   ".",      "And",   0, 1);
        addButton("+",   "+",      "Or",    0, 2);
        addButton("xor", "xor(,)", "Xor",   0, 3);
        addButton("1",   "1",      "True",  0, 4);
        addButton("-",   "-",      "Not",   1, 1);
        addButton("=",   "=",      "Iff",   1, 2);
        addButton("=>",  "=>",     "If",    1, 3);
        addButton("0",   "0",      "False", 1, 4);

        if (cmapper != null && cmapper.isQmlSet() && cmapper.getQml().getFact().size() > 0) {
            JPanel factPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 3));
            factPanel.setPreferredSize(new Dimension(200, 100));
            layout.setConstraints(factPanel, new GridBagConstraints(
                0, 5,                                 // grid x, y
                4,                                    // cells wide
                1,                                    // cells high
                0.0, 0.0,                             // weight x, y
                GridBagConstraints.LINE_START,        // anchor
                GridBagConstraints.HORIZONTAL,        // fill
                new Insets(0, 0, 0, 0),               // insets (top, left, bottom, right)
                0, 0                                  // padding x, y
            ));
            dialog.add(factPanel);

            for (final FactType fact: cmapper.getQml().getFact()) {
                JButton factButton = new JButton(new AbstractAction(fact.getId()) {
                    public void actionPerformed(ActionEvent event) {
                        textField.setText(textField.getText() + fact.getId());
                        textField.requestFocusInWindow();
                    }
                });
                factButton.setPreferredSize(new Dimension(30, 20));

                // Try to compose tooltip text with the form "Question? -- Answer"
                String toolTipText = fact.getDescription();
                outer: for (QuestionType question: cmapper.getQml().getQuestion()) {
                    StringTokenizer st = new StringTokenizer(question.getMapQF(), " ");
                    while(st.hasMoreTokens()){
                        String token = st.nextToken();
                        if (token.startsWith("#") && token.length() > 1) {
                            String answerId = token.substring(1);
                            if (answerId.equals(fact.getId())) {
                                toolTipText = question.getDescription() + " \u2014 " + toolTipText;
                                break outer;
                            }
                        } 
                    }
                }
                factButton.setToolTipText(toolTipText);

                factPanel.add(factButton);
            }
        }

        return button;
    }

    private void addButton(final String keycap, final String text, final String label, int column, int row) {
        JButton xorButton = new JButton(new AbstractAction(keycap) {
            public void actionPerformed(ActionEvent event) {
                textField.setText(textField.getText() + text);
                textField.requestFocusInWindow();
            }
        });
        xorButton.setPreferredSize(new Dimension(30, 20));
        layout.setConstraints(xorButton, new GridBagConstraints(
            column * 2, row,                      // grid x, y
            1,                                    // cells wide
            1,                                    // cells high
            0.0, 0.0,                             // weight x, y
            GridBagConstraints.LINE_START,        // anchor
            GridBagConstraints.NONE,              // fill
            new Insets(3, 3, 3, 3),               // insets (top, left, bottom, right)
            0, 0                                  // padding x, y
        ));
        dialog.add(xorButton);

        JLabel xorLabel = new JLabel(label);
        layout.setConstraints(xorLabel, new GridBagConstraints(
            column * 2 + 1, row,                  // grid x, y
            1,                                    // cells wide
            1,                                    // cells high
            1.0, 0.0,                             // weight x, y
            GridBagConstraints.LINE_START,        // anchor
            GridBagConstraints.HORIZONTAL,        // fill
            new Insets(0, 0, 0, 0),               // insets (top, left, bottom, right)
            0, 0                                  // padding x, y
        ));
        dialog.add(xorLabel);
    }

    // Implementation of CellEditor (superinterface of TableCellEditor)

    public Object getCellEditorValue() {
        return currentString;
    }

    // Implementation of ActionListener

    public void actionPerformed(ActionEvent e) {

        if (textField.equals(e.getSource())) {
            dialog.setVisible(false);

            currentString = e.getActionCommand();

            fireEditingStopped();  // Make the renderer reappear.
        }

        if (EDIT.equals(e.getActionCommand())) {
            // The user has clicked the cell, so bring up the dialog.
            textField.setText("dummy text for spacing");
            dialog.pack();
            textField.setText(currentString);
            dialog.setLocation(button.getLocationOnScreen());
            dialog.setVisible(true);

            fireEditingStopped();  // Make the renderer reappear.

        } else { //User pressed dialog's "OK" button.
            currentString = textField.getText();
        }
    }
}
