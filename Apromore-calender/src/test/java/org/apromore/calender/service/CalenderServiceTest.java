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
