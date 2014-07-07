package com.processconfiguration.cmapper;

// Java 2 Standard Edition classes
import java.awt.Component;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

/**
 * Editing of logical conditions in a JTable cell on a popup panel rather than inline.
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @see http://docs.oracle.com/javase/tutorial/uiswing/components/table.html#editor
 */
public class ConditionTableCellEditor extends AbstractCellEditor implements ActionListener, TableCellEditor {

    private JButton button;
    private JDialog dialog;
    private int row, column;
    private JTextField textField;
    private String currentString;
    private TableModel tableModel;
    protected static final String EDIT = "edit";

    /** Sole constructor. */
    public ConditionTableCellEditor() {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
    }

    // Implementation of TableCellEditor

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentString = (String) value;
        this.tableModel = table.getModel();
        this.row = row;
        this.column  = column;

        textField = new JTextField();
        textField.setText(currentString);
        textField.addActionListener(this);
         
        dialog = new JDialog(SwingUtilities.getWindowAncestor(table), APPLICATION_MODAL);
        dialog.setUndecorated(true);
        dialog.add(textField);

        return button;
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
            //tableModel.setValueAt(e.getActionCommand(), row, column);

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
