package au.edu.qut.processmining.miners.splitminer.ui.dfgp;


import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

/**
 * Created by Adriano on 29/02/2016.
 */
public class DFGPSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "Select DFG+ Params";


    final DFGPUIResult result = null;


    public DFGPSettings() {
        super(DIALOG_NAME);

    }

    public DFGPUIResult getSelections() {
        return result;
    }

    private class DFGPItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if( e.getSource() instanceof JComboBox) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:

                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        }
    }
}
