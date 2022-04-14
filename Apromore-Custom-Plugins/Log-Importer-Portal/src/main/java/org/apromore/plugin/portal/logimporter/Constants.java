/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2020 University of Tartu
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
package org.apromore.plugin.portal.logimporter;

interface Constants {

    char[] SUPPORTED_SEPARATORS = {',','|',';','\t'};

    String[] FILE_ENCODING = {"UTF-8",
                "UTF-16",
                "windows-1250 (Eastern European)",
                "windows-1251 (Cyrillic)",
                "windows-1252 (Latin)",
                "windows-1253 (Greek)",
                "windows-1254 (Turkish)",
                "windows-1255 (Hebrew)",
                "windows-1256 (Arabic)",
                "windows-1258 (Vietnamese)",
                "windows-31j (Japanese)",
                "ISO-2022-CN (Chinese)",
                "ISO-8859-1 (Latin Alphabet No.1)",
                "ISO-8859-2 (Latin Alphabet No.2)"
    };

    int LOG_SAMPLE_SIZE = 100;

    int COLUMN_WIDTH = 200;
    int INDEX_COLUMN_WIDTH = 50;

    String CASE_ID_LABEL = "caseId";
    String ACTIVITY_LABEL = "activity";
    String END_TIMESTAMP_LABEL = "endTimestamp";
    String START_TIMESTAMP_LABEL = "startTimestamp";
    String OTHER_TIMESTAMP_LABEL = "otherTimestamp";
    String RESOURCE_LABEL = "resource";
    String ROLE_LABEL = "role";
    String EVENT_ATTRIBUTE_LABEL = "eventAttribute";
    String CASE_ATTRIBUTE_LABEL = "caseAttribute";
    String IGNORE_LABEL = "ignoreAttribute";
    String PERSPECTIVE_LABEL = "perspective";

    String INTEGER_TYPE_LABEL = "integer";
    String REAL_TYPE_LABEL = "real";
    String STRING_TYPE_LABEL = "string";
    String TIMESTAMP_TYPE_LABEL = "timestamp";

    String RED_LABEL_CSS = "redLabel";
    String GREEN_LABEL_CSS = "greenLabel";

    // UI ids
    String SET_ENCODING_ID = "setEncoding";
    String MY_GRID_ID = "myGrid";
    String SET_TIME_ZONE_ID = "setTimeZone";

    String POP_UP_DIV_ID = "popUPBox";
    String POP_UP_HELP_ID = "popUpHelp";
    String POP_UP_FORMAT_WINDOW_ID = "pop_";
    String POP_UP_TEXT_BOX_ID = "txt_";
    String POP_UP_LABEL_ID = "lbl_";

    String ERROR_COUNT_LBL_ID = "count";
    String ERROR_MESSAGE_LBL_ID = "message";
    String INVALID_COLUMNS_LIST_LBL_ID = "invalidColumnsList";
    String IGNORED_COLUMNS_LIST_LBL_ID = "ignoredColumnsList";
    String DOWNLOAD_REPORT_BTN_ID = "downloadErrorLog";
    String SKIP_ROWS_BTN_ID = "skipInvalidRows";
    String SKIP_COLUMNS_BTN_ID = "skipInvalidColumns";
    String HANDLE_CANCEL_BTN_ID = "cancelImport";
    String IGNORE_COL_LBL_ID = "ignoreCol";
    String CAN_SKIP_INVALID_ROWS = "canSkipInvalidRows";
    String CANT_SKIP_INVALID_ROWS = "canNotSkipInvalidRows";

    String AUTO_PARSED = "autoParsed";
    String MANUAL_PARSED = "manualParsed";
    String ERROR_PARSING = "errorParsing";

    String HEADER_COLUMN_ID = "headerColumn_";
    String MASK_CHECKBOX_ID = "maskCheckbox_";
}
