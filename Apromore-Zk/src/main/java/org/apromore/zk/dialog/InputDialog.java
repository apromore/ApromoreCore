/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.zk.dialog;

import org.apromore.commons.item.ItemNameUtils;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class InputDialog {

    private InputDialog () {
        throw new IllegalStateException("InputDialog class");
    }

    /**
     * Bruce added 21.05.2019 Display an input dialog
     *
     * @param title:        title of the dialog
     * @param message:      the message regarding the input to enter
     * @param initialValue: initial value for the input
     * @param valuePattern: the expression pattern to check validity of the input
     * @returnValueHander: callback event listener, notified with onOK (containing
     * return value as string) and onCancel event
     */
    public static void showInputDialog(
            String title,
            String message,
            String initialValue,
            String valuePattern,
            String invalidMessage,
            EventListener<Event> returnValueHander) {
        Window win = null;
        try {
            win = (Window) Executions.createComponents(
                    getPageDefinition("apromore-zk/zul/inputDialog.zul"), null, null);
        } catch (IOException e) {
            // pass
        }
        if (win == null) {
            Messagebox.show("Failed to launched input dialog");
            return;
        }
        Window dialog = (Window) win.getFellow("inputDialog");
        dialog.setTitle(title);
        Label labelMessage = (Label) dialog.getFellow("labelMessage");
        Textbox txtValue = (Textbox) dialog.getFellow("txtValue");
        Label labelError = (Label) dialog.getFellow("labelError");
        labelMessage.setValue(message);
        txtValue.setValue(initialValue);
        labelError.setValue("");
        dialog.doModal();

        dialog.getFellow("btnCancel").addEventListener(
                Events.ON_CLICK,
                (Event event) -> {
                    dialog.detach();
                    returnValueHander.onEvent(new Event("onCancel"));
                }
        );

        dialog.getFellow("btnOK").addEventListener(
                Events.ON_CLICK, (Event event) ->
                    okHandler(txtValue, labelError, valuePattern, invalidMessage, dialog, returnValueHander)
        );

        win.addEventListener(
                Events.ON_OK, (Event event) ->
                    okHandler(txtValue, labelError, valuePattern, invalidMessage, dialog, returnValueHander)
        );
    }

    public static void okHandler(Textbox txtValue, Label labelError, String valuePattern, String invalidMessage, Window dialog, EventListener<Event> returnValueHander) throws Exception {
        String errorMessage = null;

        if (txtValue.getValue().trim().isEmpty()) {
            errorMessage = "Please enter a value!";
        } else if (valuePattern != null && !Pattern.matches(valuePattern, txtValue.getValue())) {
            errorMessage = invalidMessage;
        } else if (!ItemNameUtils.hasValidName(txtValue.getValue())) {
            errorMessage = invalidMessage;
        }

        if (errorMessage != null) {
            labelError.setValue(errorMessage);
        } else {
            dialog.detach();
            returnValueHander.onEvent(new Event("onOK", null, txtValue.getValue()));
        }
    }

    private static PageDefinition getPageDefinition(String uri) throws IOException {
        Execution current = Executions.getCurrent();
        return current.getPageDefinitionDirectly(new InputStreamReader(
                InputDialog.class.getClassLoader().getResourceAsStream(uri)), "zul");
    }
}
