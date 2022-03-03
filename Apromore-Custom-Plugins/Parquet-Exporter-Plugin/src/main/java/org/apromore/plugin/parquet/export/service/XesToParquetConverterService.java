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
        List<Integer> lstList = new ArrayList<>() {{
            add(logId);
        }};

        // Convert the log to parquert
        APMLogWrapperManager apmLogComboManager = parquetExportPlugin.initAPMLogWrapperManagers(lstList);
        if (apmLogComboManager.getAPMLogComboList().size() == 1) {
            return new ParquetExporterService(apmLogComboManager.get(0))
                .exportParquetFileToOutputStream(outputStream);
        }

        return false;
    }
}
