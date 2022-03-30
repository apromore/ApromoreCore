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
package org.apromore.plugin.parquet.export.types;


import org.apromore.plugin.parquet.export.service.ParquetExporterService;
import org.apromore.plugin.parquet.export.util.Util;

public class ParquetCol {
    private String label;
    private final String value;
    private final String type;
    private boolean checked = true;
    private final boolean primary;
    private boolean invalidLabel;

    public ParquetCol(String label, String value, String type, boolean primary) {
        this.label = label;
        this.value = value;
        this.type = type;
        this.primary = primary;
        invalidLabel = getTestLabel(label).isEmpty();
    }

    private String getTestLabel(String label) {
        String tl = label.replaceAll("[^A-Za-z0-9_]+", "");

        if (!tl.isEmpty() && Util.isNumeric(String.valueOf(label.charAt(0))))
            tl = "";

        return tl;
    }

    public void setLabel(String inputLabel) {
        if (inputLabel == null || inputLabel.isEmpty())
            return;

        String testLabel = ParquetExporterService.getValidParquetLabel(inputLabel);
        invalidLabel = testLabel.isEmpty();
        this.label = !testLabel.isEmpty() ? testLabel : inputLabel;
    }

    public void setInvalidLabel(boolean invalidLabel) {
        if (!primary)
            this.invalidLabel = invalidLabel;
    }

    public boolean isInvalidLabel() {
        return invalidLabel;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public void setChecked(boolean checked) {
        if (!primary)
            this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isPrimary() {
        return primary;
    }

    public String getType() {
        return type;
    }
}
