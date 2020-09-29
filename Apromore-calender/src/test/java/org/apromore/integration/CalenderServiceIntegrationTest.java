package org.apromore.integration;



import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.apromore.calender.exception.CalenderAlreadyExistsException;
import org.apromore.calender.service.CustomCalenderService;
import org.apromore.dao.CustomCalenderRepository;
import org.apromore.dao.model.CustomCalender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;



public class CalenderServiceIntegrationTest extends BaseTestClass{
  
  @Autowired
  CustomCalenderService calenderService;

  @Test
  public void testCreateCalender() throws CalenderAlreadyExistsException {
    calenderService.createGenericCalender("Generic", true);
//    Need to add Asserts 
  }

}
