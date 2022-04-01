/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.integration;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.ZoneId;
import java.util.List;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.Log;
import org.apromore.service.EventLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EventLogServiceTest extends BaseTest {

  @Autowired
  CustomCalendarRepository calendarRepository;

  @Autowired
  LogRepository logRepository;

  @Autowired
  EventLogService eventLogService;

  @Test
  void testEventLogsForCalendar() {
    // Given
    CustomCalendar calendar = new CustomCalendar("Test Calendar for log", ZoneId.of("UTC"));
    calendar = calendarRepository.saveAndFlush(calendar);
    assertThat(calendar.getId()).isNotNull();
    Log log1=createLog("log1",calendar);
    Log log2=createLog("log2",calendar);
    
    log1=logRepository.saveAndFlush(log1);
    log2=logRepository.saveAndFlush(log2);
    assertThat(log1.getId()).isNotNull();
    assertThat(log2.getId()).isNotNull();
    // When
    
    List<Log> logs= eventLogService.getLogListFromCalendarId(calendar.getId());

    // Then
    assertThat(logs).hasSize(2);
    assertThat(logs).extracting("name").contains(log1.getName(),log2.getName());
    

  }

  private Log createLog(String name,CustomCalendar calendar) {
    Log log1=new Log();
    log1.setName(name);
    log1.setFilePath(name);
    log1.setCalendar(calendar);
    return log1;
  }

}
