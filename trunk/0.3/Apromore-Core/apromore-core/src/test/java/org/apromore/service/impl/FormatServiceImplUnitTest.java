package org.apromore.service.impl;

import org.apromore.dao.jpa.NativeTypeDaoJpa;
import org.apromore.dao.jpa.UserDaoJpa;
import org.apromore.dao.model.EditSessionMapping;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

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
@PrepareForTest({ NativeTypeDaoJpa.class })
public class FormatServiceImplUnitTest {

    @Autowired
    private NativeTypeDaoJpa natDAOJpa;

    private FormatServiceImpl formatServiceImpl;

    @Before
    public final void setUp() throws Exception {
        formatServiceImpl = new FormatServiceImpl();
        natDAOJpa = createMock(NativeTypeDaoJpa.class);
        formatServiceImpl.setNativeTypeDao(natDAOJpa);
    }

    @Test
    public void getAllFormats() {
        List<NativeType> natTypes = new ArrayList<NativeType>();

        expect(natDAOJpa.findAllFormats()).andReturn(natTypes);
        replay(natDAOJpa);

        List<NativeType> serviceNatTypes = formatServiceImpl.findAllFormats();
        verify(natDAOJpa);
        assertThat(serviceNatTypes, equalTo(natTypes));
    }
}
