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

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;
import org.apache.commons.text.RandomStringGenerator;
import org.apromore.plugin.parquet.export.ParquetExportPlugin;
import org.apromore.plugin.parquet.export.core.data.APMLogWrapper;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.Sessions;

@Component("xesToParquetConverterService")
public class XesToParquetConverterService {
    public static final int TEMP_FILE_NAME_LENGTH = 50;
    public static final String UNDER_SCORE = "_";
    public static final String ENCODING_LOG_PARQUET = "encodingLogParquet";

    private RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
        .withinRange('0', 'z')
        .filteredBy(LETTERS, DIGITS)
        .build();

    @Inject
    private ParquetExportPlugin parquetExportPlugin;

    public Path exportXesToParquet(int logId) {
        List<Integer> lstList = new ArrayList<>();
        lstList.add(logId);

        APMLogWrapperManager apmLogComboManager = parquetExportPlugin.initAPMLogWrapperManagers(lstList);
        if (apmLogComboManager.getAPMLogComboList().size() == 1) {
            APMLogWrapper apmLogWrapper = apmLogComboManager.get(0);

            return new ParquetExporterService(apmLogWrapper)
                .saveParquetFile(
                    (String) Sessions.getCurrent().getAttribute(ENCODING_LOG_PARQUET),
                    apmLogWrapper.getLabel()
                        + UNDER_SCORE
                        + randomStringGenerator.generate(TEMP_FILE_NAME_LENGTH).toLowerCase(Locale.ROOT)
                        + UNDER_SCORE
                        + logId
                );
        }

        return null;
    }
}
