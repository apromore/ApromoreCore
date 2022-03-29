/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.plugin.parquet.export.util;

import org.zkoss.zul.Messagebox;

public class ZKMessageCtrl {

    private static final String TITLE = LabelUtil.getLabel("dash_msgbox_title");

    public static void showInternalError() {
        showError(LabelUtil.getLabelProperties().get("dash_internal_error_contact_admin").toString());
    }

    public static void showError(String text) {
        Messagebox.show(text, TITLE, Messagebox.OK, Messagebox.ERROR);
    }

    public static void showInfo(String text) {
        Messagebox.show(text, TITLE, Messagebox.OK, Messagebox.INFORMATION);
    }

    public static void showExclamation(String text) {
        Messagebox.show(text, TITLE, Messagebox.OK, Messagebox.EXCLAMATION);
    }
}
