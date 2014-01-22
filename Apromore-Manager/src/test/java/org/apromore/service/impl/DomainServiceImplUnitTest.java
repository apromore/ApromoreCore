package org.apromore.service.impl;

import org.apromore.dao.ProcessRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
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
public class DomainServiceImplUnitTest {

    private DomainServiceImpl domainServiceImpl;
    private ProcessRepository procDAOJpa;


    @Before
    public final void setUp() throws Exception {
        procDAOJpa = createMock(ProcessRepository.class);
        domainServiceImpl = new DomainServiceImpl(procDAOJpa);
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
        assertThat(serviceProcesses.get(0), equalTo(processes.get(0)));
    }
}
