/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

        // Convert the log to parquert
        APMLogWrapperManager apmLogComboManager = parquetExportPlugin.initAPMLogWrapperManagers(lstList);
        if (apmLogComboManager.getAPMLogComboList().size() == 1) {
            return new ParquetExporterService(apmLogComboManager.get(0))
                .exportParquetFileToOutputStream(outputStream);
        }

        return false;
    }
}
