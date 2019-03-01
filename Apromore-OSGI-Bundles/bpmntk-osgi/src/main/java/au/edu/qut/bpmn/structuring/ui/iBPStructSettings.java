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

package au.edu.qut.bpmn.structuring.ui;

import org.processmining.framework.util.ui.widgets.ProMPropertiesPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.*;

/**
 * Created by Adriano on 29/02/2016.
 */
public class iBPStructSettings extends ProMPropertiesPanel {

    private static final long serialVersionUID = 1L;

    private static final String DIALOG_NAME = "Setup Structuring Policy";

    final iBPStructUIResult result = null;


    public iBPStructSettings() {
        super(DIALOG_NAME);


    }

    public iBPStructUIResult getSelections() {
        return result;
    }

    private class BPSItemListener implements ChangeListener, ActionListener {

        @Override
        public void stateChanged(ChangeEvent e) {
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if( e.getSource() instanceof JComboBox ) {
                switch( ((JComboBox)e.getSource()).getSelectedIndex() ) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
        }
    }

}
