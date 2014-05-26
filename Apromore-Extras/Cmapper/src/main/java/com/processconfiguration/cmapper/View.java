package com.processconfiguration.cmapper;

// Java 2 Standard packages
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.StringBufferInputStream;
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
import net.sf.javabdd.BDD;
import org.apromore.bpmncmap.parser.ParseException;
import org.apromore.bpmncmap.parser.Parser;

class View extends JPanel {

    private static Logger LOGGER = Logger.getLogger(View.class.getName());

    /**
     * This is the model being viewed.
     */
    private VariationPoint vp;

    /**
     * Sole constructor.
     *
     * @param vp  the variation point to be viewed and edited
     */
    View(final VariationPoint newVp) {
        super(new GridLayout(1, 0));

        // Initialize instance methods
        vp = newVp;

        // Identify the variation point
        JLabel nameLabel = new JLabel(vp.getName());
        add(nameLabel);

        // Create table of configurations
        final AbstractTableModel tableModel = new AbstractTableModel() {

            public int getColumnCount() {
                return 2 + vp.getFlowCount();
            }

            public int getRowCount() {
                return vp.getConfigurations().size();
            }

            public String getColumnName(int col) {
                switch (col) {
                case 0: return "Condition";
                case 1: return "Gateway type";
                default: return vp.getFlowName(col - 2);
                }
            }

            public Object getValueAt(int row, int col) {
                VariationPoint.Configuration c = vp.getConfigurations().get(row);
                switch (col) {
                case 0: return c.getCondition();
                case 1: return c.getGatewayType();
                default: return c.isFlowActive(col - 2);
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
                case 0:  c.setCondition((String) value);             break;
                case 1:  c.setGatewayType((TGatewayType) value);     break;
                default: c.setFlowActive(col - 2, (Boolean) value);  break;
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
        initConditionColumn(table, table.getColumnModel().getColumn(0));
        initGatewayTypeColumn(table, table.getColumnModel().getColumn(1));

        //Add the scroll pane to this panel.
        add(scrollPane);

        // Add controls for creating/deleting extra configurations
        add(new JButton(new AbstractAction("Add configuration") {
            public void actionPerformed(ActionEvent event) {
                vp.addConfiguration();
                int n = vp.getConfigurations().size();
                tableModel.fireTableRowsInserted(n - 1, n - 1);
            };
        }));

        add(new JButton(new AbstractAction("Remove configuration") {
            public void actionPerformed(ActionEvent event) {
                final int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "No configuration selected", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    vp.removeConfiguration(selectedRow);
                    tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
                }
            };
        }));
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
            default: dummy = new Boolean("false");
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

    public void initConditionColumn(JTable table, TableColumn column) {

        JTextField textField = new JTextField();
        column.setCellEditor(new DefaultCellEditor(textField));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                try {
                    Parser parser = new Parser(new StringBufferInputStream((String) value));
                    parser.init();
                    BDD bdd = parser.AdditiveExpression();
                } catch (ParseException e) {
                    component.setBackground(Color.RED);
                }

                return component;
            }
        };
        renderer.setToolTipText("BDDC formatted field");
        column.setCellRenderer(renderer);
    }

    public void initGatewayTypeColumn(JTable table, TableColumn column) {

        JComboBox<TGatewayType> comboBox = new JComboBox<>(TGatewayType.values());
        column.setCellEditor(new DefaultCellEditor(comboBox));

        // Set up tooltips
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        column.setCellRenderer(renderer);
    }
}
