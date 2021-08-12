/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2020 University of Tartu
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

    char[] supportedSeparators = {',','|',';','\t'};

    String[] fileEncoding = {"UTF-8",
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

    int logSampleSize = 100;

    int columnWidth = 200;
    int indexColumnWidth = 50;

    String caseIdLabel = "caseId";
    String activityLabel = "activity";
    String endTimestampLabel = "endTimestamp";
    String startTimestampLabel = "startTimestamp";
    String otherTimestampLabel = "otherTimestamp";
    String resourceLabel = "resource";
    String eventAttributeLabel = "eventAttribute";
    String caseAttributeLabel = "caseAttribute";
    String ignoreLabel = "ignoreAttribute";

    String redLabelCSS = "redLabel";
    String greenLabelCSS = "greenLabel";
    String couldNotParseMessage = "Could not parse as timestamp!";
    String parsedMessage = "Parsed correctly!";
    String parsedAutoMessage = "Timestamp format\nautomatically detected. Override?";

    // UI ids
    String setEncodingId = "setEncoding";
    String myGridId = "myGrid";
    String setTimeZoneId = "setTimeZone";

    String ignoreToEventBtnId = "setOtherAll";
    String eventToIgnoreBtnId = "setIgnoreAll";
    String toXESBtnId = "toXESButton";
    String cancelBtnId = "cancelButton";

    String popUpDivId = "popUPBox";
    String popUpHelpId = "popUpHelp";
    String popUpFormatWindowId = "pop_";
    String popUpTextBoxId = "txt_";
    String popUpLabelId = "lbl_";

    String errorCountLblId = "count";
    String invalidColumnsListLblId = "invalidColumnsList";
    String ignoredColumnsListLblId = "ignoredColumnsList";
    String downloadReportBtnId = "downloadErrorLog";
    String skipRowsBtnId = "skipInvalidRows";
    String skipColumnsBtnId = "skipInvalidColumns";
    String handleCancelBtnId = "cancelImport";
    String ignoreColLblId = "ignoreCol";

    String autoParsed = "autoParsed";
    String manualParsed = "manualParsed";
    String errorParsing = "errorParsing";

    String specifyTimestampformat= "Specify timestamp format";
}
