/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.processconfiguration.cmapper;

// Java 2 Standard packages
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import com.processconfiguration.cmap.TGatewayType;

class VariationPointView extends JPanel {

    /**
     * Whether to permit arbitrary logical conditions for sequence flows in configurations.
     *
     * When this is disabled (false), flow conditions are edited with a checkbox rather than a text file.
     * The {@link #toggle} method can be used to change this property.
     */
    private boolean complexFlowConditions = false;

    private static Logger LOGGER = Logger.getLogger(VariationPointView.class.getName());
    private static ResourceBundle bundle = ResourceBundle.getBundle("com.processconfiguration.cmapper.VariationPointView");

    /**
     * This is the model being viewed.
     */
    private VariationPoint vp;

    /**
     * This is the cmapper containing the questionnaire which governs the facts available for conditions.
     */
    private Cmapper cmapper;

    /**
     * Sole constructor.
     *
     * @param vp  the variation point to be viewed and edited
     */
    VariationPointView(final VariationPoint newVp, final Cmapper newCmapper) {

        // Layout
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        // Initialize instance methods
        vp      = newVp;
        cmapper = newCmapper;

        // Identify the variation point
        JLabel nameLabel = new JLabel(vp.getName());
        nameLabel.setToolTipText(vp.getName() + "(id: " + vp.getId() + ")");
        layout.setConstraints(nameLabel, new GridBagConstraints(
            0, 0,                                 // grid x, y
            1,                                    // cells wide
            1,                                    // cells high
            0.0, 0.0,                             // weight x, y
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.NONE,              // fill
            new Insets(10, 10, 0, 10),            // insets (top, left, bottom, right)
            10, 0                                 // padding x, y
        ));
        add(nameLabel);

        // Annotate the variation point with its type
        JLabel typeLabel = new JLabel(bundle.getString(vp.getGatewayType().toString()) + bundle.getString(vp.getGatewayDirection().toString()));
        layout.setConstraints(typeLabel, new GridBagConstraints(
            0, 1,                                 // grid x, y
            1,                                    // cells wide
            GridBagConstraints.REMAINDER,         // cells high
            0.0, 0.0,                             // weight x, y
            GridBagConstraints.FIRST_LINE_START,  // anchor
            GridBagConstraints.NONE,              // fill
            new Insets(0, 10, 10, 10),            // insets (top, left, bottom, right)
            10, 0                                 // padding x, y
        ));
        add(typeLabel);
        
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
                default:
                    if (complexFlowConditions) {
                        return c.getFlowCondition(col - 2);
                    } else {
                        switch (c.getFlowCondition(col - 2)) {
                        case "0": return Boolean.FALSE;
                        case "1": return Boolean.TRUE;
                        default: throw new RuntimeException("Unable to edit flow conditions \"" + c.getFlowCondition(col - 2) + "\"");
                        }
                    }
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
                default:
                    if (complexFlowConditions) {
                        c.setFlowCondition(col - 2, (String) value);
                    } else {
                        c.setFlowCondition(col - 2, ((Boolean) value).booleanValue() ? "1" : "0");
                    }
                    break;
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

        // Each condition line has a logical condition for when it's active
        initComplexConditionColumn(table, table.getColumnModel().getColumn(0), null);

        // Gateway types might need to be editable with a combo box
        initGatewayTypeColumn(table, table.getColumnModel().getColumn(1), vp.getGatewayDirection().toString(), vp.getGatewayType());

        // Columns for each configurable sequence flow
        initFlowColumns(table);

        //Add the scroll pane to this panel.
        layout.setConstraints(scrollPane, new GridBagConstraints(
            1, 0,                           // grid x, y
            1,   // cells wide
            GridBagConstraints.REMAINDER,   // cells high
            1.0, 1.0,                       // weight x, y
            GridBagConstraints.CENTER, // anchor
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
            GridBagConstraints.NORTHWEST,   // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(10, 0, 0, 0),        // insets (top, left, bottom, right)
            0, 0                            // padding x, y
        ));
        add(addConfigurationButton);

        final JButton removeConfigurationButton = new JButton(new AbstractAction(bundle.getString("Remove_configuration")) {
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
            1,                              // cells high
            0.0, 0.0,                       // weight x, y
            GridBagConstraints.NORTHWEST,   // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0, 0, 0, 0),         // insets (top, left, bottom, right)
            0, 0                            // padding x, y
        ));
        add(removeConfigurationButton);

        removeConfigurationButton.setEnabled(table.getSelectedRow() != -1);

        JButton toggleButton = new JButton(new AbstractAction(bundle.getString("Toggle")) {
            public void actionPerformed(ActionEvent event) {
                toggle(table);
            }
        });
        layout.setConstraints(toggleButton, new GridBagConstraints(
            2, 2,                           // grid x, y
            GridBagConstraints.REMAINDER,   // cells wide
            GridBagConstraints.REMAINDER,   // cells high
            0.0, 0.0,                       // weight x, y
            GridBagConstraints.NORTHWEST,   // anchor
            GridBagConstraints.HORIZONTAL,  // fill
            new Insets(0, 0, 0, 0),         // insets (top, left, bottom, right)
            0, 0                            // padding x, y
        ));
        add(toggleButton);

        // When we lose focus, we need to deselect any selected table row
        table.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                JTable previousTable = previousTableMap.get(getParent());
                previousTableMap.put(getParent(), table);
                if (previousTable != null && previousTable.getRowCount() > 0) {
                    previousTable.removeRowSelectionInterval(0, previousTable.getRowCount()-1);
                }
            }
            public void focusLost(FocusEvent event) {}
        });

        // The "Remove configuration" button is only enabled when a configuration is selected
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                removeConfigurationButton.setEnabled(!((ListSelectionModel) event.getSource()).isSelectionEmpty());
            }
        });
    }

    /**
     * The containing CmapperView (presumably) is mapped to the last variation point table that had focus.
     *
     * This is used to cancel the focus within sibling variation points when focus is gained.
     * (Yeah, it's sort of ugly....)
     */
    private final static Map<Container,JTable> previousTableMap = new HashMap<>();

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
            default: dummy = complexFlowConditions ? "f1 & f2" : Boolean.FALSE;
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

    /** Initialize a column to display simple logical conditions that are either true or false. */
    public void initSimpleConditionColumn(JTable table, TableColumn column, String toolTip) {
        column.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        column.setCellRenderer(null /*new DefaultTableCellRenderer()*/);
    }

    /** Initialize a column to display complex logical conditions expressed as BDDC-formatted strings. */
    public void initComplexConditionColumn(JTable table, TableColumn column, String toolTip) {

        column.setCellEditor(new ConditionTableCellEditor(cmapper));
        //column.setCellEditor(new DefaultCellEditor(new JTextField()));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String conditionError = cmapper.findConditionError((String) value);
                //LOGGER.info("Condition error for " + value + " is " + conditionError);
                component.setBackground(conditionError != null ? Color.PINK :
                                                               !isSelected ? SystemColor.control :
                                                                             SystemColor.controlHighlight);
                setToolTipText(conditionError);

                return component;
            }
        };
        renderer.setToolTipText(toolTip);
        column.setCellRenderer(renderer);
    }

    public void initGatewayTypeColumn(JTable table, TableColumn column, String toolTip, TGatewayType gatewayType) {

        switch (gatewayType) {
        case INCLUSIVE:
            JComboBox<TGatewayType> comboBox = new JComboBox<>(new TGatewayType[] { TGatewayType.DATA_BASED_EXCLUSIVE,
                                                                                    TGatewayType.INCLUSIVE,
                                                                                    TGatewayType.PARALLEL });
            column.setCellEditor(new DefaultCellEditor(comboBox));
            break;

        default:
            // set no editor -- not editable
            break;
        }

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

    /**
     * Toggles editing with complex flow conditions.
     *
     * @param table  the UI for the list of configurations
     */
    private void toggle(JTable table) {
        try {
            if (complexFlowConditions) {
                String constraints = "1";  // Default to no constraints;
                if (cmapper.isQmlSet() && cmapper.getQml().getConstraints() != null) {
                    constraints = cmapper.getQml().getConstraints();
                }
                vp.simplify(constraints);  // Convert complex (per flow) conditions to simple (per configuration) conditions

                complexFlowConditions = false;

            } else {
                complexFlowConditions = true;
            }

            initFlowColumns(table);
            table.updateUI();

        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Unable to toggle complex flow conditions", e);
        }
    }

    private void initFlowColumns(JTable table) {
        for (int flowIndex = 0; flowIndex < vp.getFlowCount(); flowIndex++) {
            if (complexFlowConditions) {
                initComplexConditionColumn(
                    table,
                    table.getColumnModel().getColumn(flowIndex + 2),
                    vp.getFlowName(flowIndex) + " (id: " + vp.getFlowId(flowIndex) + ")"
                );
            } else {
                initSimpleConditionColumn(
                    table,
                    table.getColumnModel().getColumn(flowIndex + 2),
                    vp.getFlowName(flowIndex) + " (id: " + vp.getFlowId(flowIndex) + ")"
                );
            }
        }
    }
}
