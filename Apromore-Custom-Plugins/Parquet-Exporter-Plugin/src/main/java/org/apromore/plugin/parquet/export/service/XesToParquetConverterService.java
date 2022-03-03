/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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

package org.apromore.plugin.parquet.export.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apromore.plugin.parquet.export.ParquetExportPlugin;
import org.springframework.stereotype.Component;

@Component("xesToParquetConverterService")
public class XesToParquetConverterService {
    @Inject
    private ParquetExportPlugin parquetExportPlugin;

    public boolean exportXesToParquetToFilesystem(int logId, OutputStream outputStream) {
        List<Integer> lstList = new ArrayList<>();
        lstList.add(logId);

        // Convert the log to parquet
        APMLogWrapperManager apmLogComboManager = parquetExportPlugin.initAPMLogWrapperManagers(lstList);
        if (apmLogComboManager.getAPMLogComboList().size() == 1) {
            return new ParquetExporterService(apmLogComboManager.get(0))
                .exportParquetFileToOutputStream(outputStream);
        }

        return false;
    }
}
