package org.apromore.plugin.portal.CSVImporterPortal;

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
                "ISO-2022-CN (Chinese)"
    };

    int logSampleSize = 100;

    int columnWidth = 180;
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
}
