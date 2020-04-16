/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer.utils;

import java.util.regex.Pattern;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class InputDialog {
    /**
     * Bruce added 21.05.2019
     * Display an input dialog
     * @param title: title of the dialog
     * @param message: the message regarding the input to enter
     * @param initialValue: initial value for the input
     * @param valuePattern: the expression pattern to check validity of the input
     * @param allowedValues: message about valid values allowed  
     * @returnValueHander: callback event listener, notified with onOK (containing return value as string) and onCancel event
     */
    public static void showInputDialog(String title, String message, String initialValue, 
                                String valuePattern,
                                String allowedValues, 
                                EventListener<Event> returnValueHander) {
        Window win = (Window) Executions.createComponents("/zul/inputDialog.zul", null, null);
        Window dialog = (Window) win.getFellow("inputDialog");
        dialog.setTitle(title);
        Label labelMessage = (Label)dialog.getFellow("labelMessage"); 
        Textbox txtValue = (Textbox)dialog.getFellow("txtValue");
        Label labelError = (Label)dialog.getFellow("labelError"); 
        labelMessage.setValue(message);
        txtValue.setValue(initialValue);
        labelError.setValue("");
        
        dialog.doModal();
        
        ((Button)dialog.getFellow("btnCancel")).addEventListener("onClick", new EventListener<Event>() {
             @Override
             public void onEvent(Event event) throws Exception {
                 dialog.detach();
                 returnValueHander.onEvent( new Event("onCancel"));
             }
         });
         
         ((Button)dialog.getFellow("btnOK")).addEventListener("onClick", new EventListener<Event>() {
             @Override
             public void onEvent(Event event) throws Exception {
                 if (txtValue.getValue().trim().isEmpty()) {
                     labelError.setValue("Please enter a value!");
                 }
                 else if (!Pattern.matches(valuePattern, txtValue.getValue())) {
                     labelError.setValue("The entered value is not valid! Allowed characters: " + allowedValues);
                 }
                 else {
                     dialog.detach();
                     returnValueHander.onEvent( new Event("onOK", null, txtValue.getValue()));
                 }
             }
        });
        
    }
}
