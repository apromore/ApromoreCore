package com.processconfiguration.cmapper;

// Java 2 Standard packages
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.StringBufferInputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.*;

import com.processconfiguration.cmap.TGatewayType;

class VariationPointView extends JPanel {

    private static Logger LOGGER = Logger.getLogger(VariationPointView.class.getName());
    private static ResourceBundle bundle = ResourceBundle.getBundle("com.processconfiguration.cmapper.VariationPointView");

    /**
     * This is the model being viewed.
     */
    private VariationPoint vp;

    /**
     * Sole constructor.
     *
     * @param vp  the variation point to be viewed and edited
     */
    VariationPointView(final VariationPoint newVp) {

        // Layout
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        // Initialize instance methods
        vp = newVp;

        // Identify the variation point
        JLabel nameLabel = new JLabel(vp.getName());
        nameLabel.setToolTipText(vp.getName() + "(id: " + vp.getId() + ")");
        layout.setConstraints(nameLabel, new GridBagConstraints(
            0, 0,                                 // grid x, y
            1,                                    // cells wide
            GridBagConstraints.REMAINDER,         // cells high
            0.0, 0.0,                             // weight x, y
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.NONE,              // fill
            new Insets(10, 10, 10, 10),           // insets (top, left, bottom, right)
            10, 10                                // padding x, y
        ));
        add(nameLabel);
        
        // Create table of configurations
        final AbstractTableModel tableModel = new AbstractTableModel() {

            public int getColumnCount() {
                return vp.getFlowCount() + 2;
            }

            public int getRowCount() {
                return vp.getConfigurations().size();
            }

            public String getColumnName(int col) {
                switch (col) {
                case 0: return bundle.getString("Condition");
                case 1: return bundle.getString("Gateway_type");
                default: return vp.getFlowName(col - 2);
                }
            }

            public Object getValueAt(int row, int col) {
                VariationPoint.Configuration c = vp.getConfigurations().get(row);
                switch (col) {
                case 0: return c.getCondition();
                case 1: return c.getGatewayType();
                default: return c.getFlowCondition(col - 2);
                }
            }

            public Class getColumnClass(int c) {
                return getValueAt(0, c).getClass();
            }

            public boolean isCellEditable(int row, int col) {
                return true;
            }

            public void setValueAt(Object value, int row, int col) {
                VariationPoint.Configuration c = vp.getConfigurations().get(row);
                switch (col) {
                case 0:  c.setCondition((String) value);               break;
                case 1:  c.setGatewayType((TGatewayType) value);       break;
                default: c.setFlowCondition(col - 2, (String) value);  break;
                }
                fireTableCellUpdated(row, col);
            }
        };

        final JTable table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        // Set up column sizes.
        initColumnSizes(table);

        // Gateway types need to be edited with a combo box
        initConditionColumn(table, table.getColumnModel().getColumn(0), null);
        initGatewayTypeColumn(table, table.getColumnModel().getColumn(1), vp.getGatewayDirection().toString());
        for (int flowIndex = 0; flowIndex < vp.getFlowCount(); flowIndex++) {
            initConditionColumn(
                table,
                table.getColumnModel().getColumn(flowIndex + 2),
                vp.getFlowName(flowIndex) + " (id: " + vp.getFlowId(flowIndex) + ")"
            );
        }

        //Add the scroll pane to this panel.
        layout.setConstraints(scrollPane, new GridBagConstraints(
            1, 0,                           // grid x, y
            GridBagConstraints.RELATIVE,    // cells wide
            GridBagConstraints.REMAINDER,   // cells high
            1.0, 1.0,                       // weight x, y
            GridBagConstraints.CENTER,      // anchor
            GridBagConstraints.BOTH,        // fill
            new Insets(10, 10, 10, 10),     // insets (top, left, bottom, right)
            10, 10                          // padding x, y
        ));
        add(scrollPane);

        // Add controls for creating/deleting extra configurations
        JButton addConfigurationButton = new JButton(new AbstractAction(bundle.getString("Add_configuration")) {
            public void actionPerformed(ActionEvent event) {
                vp.addConfiguration();
                int n = vp.getConfigurations().size();
                tableModel.fireTableRowsInserted(n - 1, n - 1);
            };
        });
        layout.setConstraints(addConfigurationButton, new GridBagConstraints(
            2, 0,                           // grid x, y
            GridBagConstraints.REMAINDER,   // cells wide
            1,                              // cells high
            0.0, 0.0,                       // weight x, y
            GridBagConstraints.CENTER,      // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0, 0, 0, 0),         // insets (top, left, bottom, right)
            0, 0                            // padding x, y
        ));
        add(addConfigurationButton);

        JButton removeConfigurationButton = new JButton(new AbstractAction(bundle.getString("Remove_configuration")) {
            public void actionPerformed(ActionEvent event) {
                final int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(
                        null,
                        bundle.getString("No_configuration_selected"),
                        bundle.getString("Error"),
                        JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    vp.removeConfiguration(selectedRow);
                    tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
                }
            };
        });
        layout.setConstraints(removeConfigurationButton, new GridBagConstraints(
            2, 1,                           // grid x, y
            GridBagConstraints.REMAINDER,   // cells wide
            GridBagConstraints.REMAINDER,   // cells high
            0.0, 0.0,                       // weight x, y
            GridBagConstraints.CENTER,      // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0, 0, 0, 0),         // insets (top, left, bottom, right)
            0, 0                            // padding x, y
        ));
        add(removeConfigurationButton);
    }

    /*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        TableModel model = table.getModel();
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;

        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < table.getColumnCount(); i++) {
            Object dummy;
            switch (i) {
            case 0: dummy = "f1 & f2";                           break;
            case 1: dummy = TGatewayType.EVENT_BASED_EXCLUSIVE;  break;
            default: dummy = "f1 & f2";
            }
            assert dummy != null;

            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;

            comp = table.getDefaultRenderer(model.getColumnClass(i)).
                             getTableCellRendererComponent(
                                 table, dummy,
                                 false, false, 0, i);
            cellWidth = comp.getPreferredSize().width;

            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    public void initConditionColumn(JTable table, TableColumn column, String toolTip) {

        JTextField textField = new JTextField();
        column.setCellEditor(new DefaultCellEditor(textField));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!Cmapper.isValidCondition((String) value)) {
                    component.setBackground(Color.RED);
                }

                return component;
            }
        };
        renderer.setToolTipText(toolTip);
        column.setCellRenderer(renderer);
    }

    public void initGatewayTypeColumn(JTable table, TableColumn column, String toolTip) {

        JComboBox<TGatewayType> comboBox = new JComboBox<>(TGatewayType.values());
        column.setCellEditor(new DefaultCellEditor(comboBox));

        // Set up tooltips
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public String getText() {
                try {
                    return bundle.getString(super.getText());
                } catch (MissingResourceException e) {
                    LOGGER.severe("Unable to find l10n for key " + super.getText());
                    return super.getText();
                }
            }
        };
        renderer.setToolTipText(toolTip);
        column.setCellRenderer(renderer);
    }
}
