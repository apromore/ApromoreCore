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
package org.apromore.service.csvimporter.common;

import org.apromore.dao.model.Log;
import org.apromore.service.EventLogService;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;

import static org.apromore.service.csvimporter.constants.Constants.XES_EXTENSION;

@Service("eventLogImporter")
public class EventLogImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogImporter.class);
    @Inject
    EventLogService eventLogService;

    public void setEventLogService(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    public Log importXesLog(XLog xLog, String username, Integer folderId, String logName) throws Exception {
        LOGGER.info("Importing log " + logName + " by " + username + " at folder ID " + folderId);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        eventLogService.exportToStream(outputStream, xLog);

        return eventLogService.importLog(
                username,
                folderId,
                logName,
                new ByteArrayInputStream(outputStream.toByteArray()),
                XES_EXTENSION,
                "",  // domain
                DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                false  // public
        );
    }
}
