/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.service.logimporter.services.legacy;

import java.util.Map;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.apromore.service.logimporter.constants.Constants.*;

@Component
public class LogImporterProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogImporterProvider.class);

    @Autowired
    @Qualifier("logImporterMap")
    Map<String, LogImporter> logImporterMap;

    /**
     * @param map  keys are lowercase log file extensions, values are the corresponding log importers
     */
    public void setLogImporterMap(Map<String, LogImporter> map) {
        this.logImporterMap = map;
    }

    public LogImporter getLogReader(String fileExtension) {

        LOGGER.info("File Format: " + fileExtension);
        return logImporterMap.get(fileExtension.toLowerCase());
    }
}
