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
package org.apromore.plugin.parquet.export;


import java.util.List;
import java.util.Properties;
import javax.validation.constraints.NotNull;
import org.apromore.plugin.parquet.export.service.ParquetExporterService;
import org.apromore.plugin.parquet.export.types.ParquetCell;
import org.apromore.plugin.parquet.export.types.ParquetCol;
import org.apromore.plugin.parquet.export.types.EncodeOption;
import org.apromore.plugin.parquet.export.util.LoggerUtil;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.zul.Window;

public class ParquetExporterViewModel {

    private ParquetExporterService service;

    @Init
    public void init(@ExecutionArgParam("service") @NotNull ParquetExporterService parquetExporterService) {
        service = parquetExporterService;
    }

    @Command
    @NotifyChange({"headers", "parquetRows", "downloadAllowed"})
    public void onCheckHeader() {
        service.checkHeaderDuplication();
        LoggerUtil.getLogger(this.getClass()).info("Set Parquet column enabled/disabled");
    }

    @Command
    @NotifyChange({"headers", "downloadAllowed"})
    public void onChangeHeaderLabel() {
        service.checkHeaderDuplication();
        LoggerUtil.getLogger(this.getClass()).info("Change Parquet column name");
    }

    @Command
    public void downloadFile(@SelectorParam("#parquetExportWindow") final Window window) {
        if (service.downloadParquetFile())
            window.detach();
    }

    @Command
    public void cancelDownload(@SelectorParam("#parquetExportWindow") final Window window) {
        window.detach();
    }

    public boolean isDownloadAllowed() {
        return service.isDownloadAllowed();
    }

    public String getWindowTitle() {
        return getLabels().get("dash_download_parquet") + ": " + service.getLogLabel();
    }

    public Properties getLabels() {
        return service.getLabels();
    }

    public List<ParquetCol> getHeaders() {
        return service.getHeaders();
    }

    public List<List<ParquetCell>> getParquetRows() {
        return service.getParquetRows();
    }

    public List<EncodeOption> getEncodeOptions() {
        return service.getEncodeOptions();
    }
}
