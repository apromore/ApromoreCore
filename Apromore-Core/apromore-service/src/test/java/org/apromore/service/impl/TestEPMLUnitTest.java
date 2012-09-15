package org.apromore.service.impl;

import org.apromore.TestData;
import org.apromore.service.model.CanonisedProcess;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Unit test the UserService Implementation.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:META-INF/spring/applicationContext-jpa-TEST.xml",
        "classpath:META-INF/spring/applicationContext-services-TEST.xml"
})
public class TestEPMLUnitTest {

    private CanoniserServiceImpl service;

    @Before
    public final void setUp() throws Exception {
        service = new CanoniserServiceImpl();
    }


    @Test
    public void canoniseEPML2() throws Exception {
        String uri = "1234567890";
        String nativeType = "EPML 2.0";

        InputStream data = new ByteArrayInputStream(TestData.EPML2.getBytes());
        CanonisedProcess cp = service.canonise(nativeType, uri, data);

        assertThat(cp, notNullValue());
        assertThat(cp.getCpt(), notNullValue());
    }

}
