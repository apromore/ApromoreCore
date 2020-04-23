package org.apromore.plugin.portal.CSVImporterPortal;

interface Constants {

    char[] supportedSeparators = {',','|',';','\t'};
    String[] allowedExtensions = {"csv", "xls", "xlsx"};
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

    Integer columnWidth = 180;
    Integer indexColumnWidth = 50;

    String caseIdLabel = "caseId";
    String activityLabel = "activity";
    String endTimestampLabel = "endTimestamp";
    String startTimestampLabel = "startTimestamp";
    String otherTimestampLabel = "otherTimestamp";
    String resourceLabel = "resource";
    String eventAttributeLabel = "eventAttribute";
    String caseAttributeLabel = "caseAttribute";
    String ignoreLabel = "ignoreAttribute";

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

    String handleErrorCount = "count";
    String handleInvalidColumnsList = "invalidColumnsList";
    String handleIgnoredColumnsList = "ignoredColumnsList";
    String handleDownloadBtnId = "downloadErrorLog";
    String handleSkipRowsBtnId = "skipInvalidRows";
    String handleSkipColumnsBtnId = "skipInvalidColumns";
    String handleCancelBtnId = "cancelImport";
    String handleIgnoreColLbl = "ignoreCol";



    String redLabelCSS = "redLabel";
    String greenLabelCSS = "greenLabel";
    String couldNotParse = "Could not parse as timestamp!";
    String parsedMessage = "Parsed correctly!";
    String parsedAuto = "Timestamp format automatically detected. Override?";

}
