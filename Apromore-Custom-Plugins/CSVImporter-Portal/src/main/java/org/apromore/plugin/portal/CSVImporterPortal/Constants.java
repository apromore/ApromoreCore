package org.apromore.plugin.portal.CSVImporterPortal;

interface Constants {

    char[] supportedSeparators = {',','|',';','\t'};
    double maxErrorFraction = 0.2;  // Accept up to 20% error rate
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


    // UI ids

    String setEncodingId = "setEncoding";
    String myGridId = "myGrid";
    String popUPBoxId = "popUPBox";
    String setOtherAllBtnId = "setOtherAll";
    String setIgnoreAllBtnId = "setIgnoreAll";
    String toXESBtnId = "toXESButton";
}
