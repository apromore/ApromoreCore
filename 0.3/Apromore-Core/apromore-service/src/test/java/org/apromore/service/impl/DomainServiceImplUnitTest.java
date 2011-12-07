package org.apromore.service.impl;

import org.apromore.dao.jpa.NativeTypeDaoJpa;
import org.apromore.dao.jpa.ProcessDaoJpa;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.*;

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
public class DomainServiceImplUnitTest {

    @Autowired
    private ProcessDaoJpa procDAOJpa;

    private DomainServiceImpl domainServiceImpl;

    @Before
    public final void setUp() throws Exception {
        domainServiceImpl = new DomainServiceImpl();
        procDAOJpa = createMock(ProcessDaoJpa.class);
        domainServiceImpl.setProcDao(procDAOJpa);
    }

    @Test
    public void getAllDomains() {
        List<String> processes = new ArrayList<String>();
        processes.add("test1");
        processes.add("test2");

        expect(procDAOJpa.getAllDomains()).andReturn(processes);
        replay(procDAOJpa);

        List<String> serviceProcesses = domainServiceImpl.findAllDomains();
        verify(procDAOJpa);
        assertThat(serviceProcesses.size(), equalTo(processes.size()));
        assertThat(serviceProcesses.get(0), equalTo(processes.get(0).toString()));
    }
}
