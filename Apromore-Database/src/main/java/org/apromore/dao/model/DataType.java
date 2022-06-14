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

package org.apromore.dao.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Locale;

/**
 * Each column must have a specified data_type from the following list:
 * "REAL" (double-precision floating-point number)
 * "INTEGER" (integer number)
 * "STRING" (text)
 * "TIMESTAMP" (timestamp)
 * These values were chosen to match https://www.xes-standard.org/_media/xes/xesstandarddefinition-2.0.pdf §2.2,
 * which also includes some we’re not using: “boolean”, “id”, “list”.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public enum DataType {
    STRING,
    TIMESTAMP,
    REAL,
    INTEGER;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    @JsonValue
    public String getDataType() {
        return toString();
    }
}
