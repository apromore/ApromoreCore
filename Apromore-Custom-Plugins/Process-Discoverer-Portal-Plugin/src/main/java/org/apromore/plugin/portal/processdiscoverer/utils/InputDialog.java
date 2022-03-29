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

package org.apromore.plugin.portal.processdiscoverer.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import org.apromore.commons.item.Constants;
import org.apromore.commons.item.ItemNameUtils;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class InputDialog {
    /**
     * Bruce added 21.05.2019
     * Display an input dialog
     *
     * @param title             title of the dialog
     * @param message           the message regarding the input to enter
     * @param initialValue      initial value for the input
     * @param conditionText     text to display if a conditional checkbox is meant to be displayed
     * @param footnote          Any other additional information to be displayed to the user
     * @param returnValueHander callback event listener, notified with onOK (containing return value as string)
     *                          and onCancel event
     * @throws IOException exception to be thrown in the event of an error.
     */
    public static void showInputDialog(
        final String title,
        final String message,
        final String initialValue,
        final String conditionText,
        final String footnote,
        final EventListener<Event> returnValueHander) throws IOException {

        Window win =
            (Window) Executions.createComponents(getPageDefination("static/processdiscoverer/zul/inputDialog.zul"),
                null, null);

        Window dialog = (Window) win.getFellow("inputDialog");
        dialog.setTitle(title);

        Label labelMessage = (Label) dialog.getFellow("labelMessage");
        labelMessage.setValue(message);

        Textbox txtValue = (Textbox) dialog.getFellow("txtValue");
        txtValue.setValue(initialValue);

        Label labelError = (Label) dialog.getFellow("labelError");
        labelError.setValue("");

        // Set the condition checkbox if available
        Checkbox conditionalCheckbox = (Checkbox) dialog.getFellow("chkConditionCheck");
        if (conditionText != null && !conditionText.isEmpty()) {
            conditionalCheckbox.setLabel(conditionText);

            Row footNoteRow = (Row) dialog.getFellow("rowConditionCheck");
            footNoteRow.setVisible(true);
        }

        // Add footnote messages if available
        if (footnote != null && !footnote.isEmpty()) {
            Label footnoteLabel = (Label) dialog.getFellow("labelFootnote");
            footnoteLabel.setValue(footnote);

            Row footNoteRow = (Row) dialog.getFellow("rowFootnote");
            footNoteRow.setVisible(true);
        }

        dialog.doModal();

        dialog.getFellow("btnCancel").addEventListener("onClick", event -> {
            dialog.detach();
            returnValueHander.onEvent(new Event("onCancel"));
        });

        dialog.getFellow("btnOK").addEventListener("onClick",
            event -> okHandler(txtValue, labelError, dialog, conditionalCheckbox, returnValueHander));

        win.addEventListener("onOK",
            event -> okHandler(txtValue, labelError, dialog, conditionalCheckbox, returnValueHander));

    }

    public static void okHandler(
        final Textbox txtValue,
        final Label labelError,
        final Window dialog,
        final Checkbox checkbox,
        final EventListener<Event> returnValueHander) throws Exception {
        String allowedValues = Constants.VALID_NAME_MESSAGE;

        if (txtValue.getValue().trim().isEmpty()) {
            labelError.setValue("Please enter a value!");
        } else if (!ItemNameUtils.hasValidName(txtValue.getValue())) {
            labelError.setValue("The entered value is not valid! Allowed characters: " + allowedValues);
        } else {
            dialog.detach();
            if (checkbox.isVisible() && checkbox.isChecked()) {
                returnValueHander.onEvent(new Event("onOKChecked", null, txtValue.getValue()));
            } else {
                returnValueHander.onEvent(new Event("onOK", null, txtValue.getValue()));
            }
        }
    }

    private static PageDefinition getPageDefination(String uri) throws IOException {
        Execution current = Executions.getCurrent();
        PageDefinition pageDefinition = current.getPageDefinitionDirectly(new InputStreamReader(
            InputDialog.class.getClassLoader().getResourceAsStream(uri)), "zul");
        return pageDefinition;
    }
}
