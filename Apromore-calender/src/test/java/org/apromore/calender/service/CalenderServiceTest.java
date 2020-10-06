/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.calender.service;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.apromore.calender.exception.CalenderAlreadyExistsException;
import org.apromore.dao.CustomCalenderRepository;
import org.apromore.dao.model.CustomCalender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CalenderServiceTest {

  @Mock
  CustomCalenderRepository calenderRepository;


  @InjectMocks
  CustomCalenderService calenderService;

 
  @Test
  public void testCreateCalender() throws CalenderAlreadyExistsException {
    // Given
    CustomCalender calender = new CustomCalender("Test Desc");
    calender.setId(1l);
    when(calenderRepository.findByDescription(calender.getDescription())).thenReturn(null);
    when(calenderRepository.saveAndFlush(any(CustomCalender.class))).thenReturn(calender);

    // When
    Long id = calenderService.createGenericCalender(calender.getDescription(), true);

    // Then
    assertThat(id).isEqualTo(calender.getId());
    verify(calenderRepository,times(1)).findByDescription(calender.getDescription());
    verify(calenderRepository,times(1)).saveAndFlush(any(CustomCalender.class));
    
  }


  @Test(expected = CalenderAlreadyExistsException.class)
  public void testCreateCalenderWithException() throws CalenderAlreadyExistsException {
    // Given
    CustomCalender calender = new CustomCalender("Test Desc");
    calender.setId(1l);
    when(calenderRepository.findByDescription(calender.getDescription())).thenReturn(calender);
  
    
    // When
    Long id = calenderService.createGenericCalender(calender.getDescription(), true);

    // Then
//    exception thrown
    
  }

}
