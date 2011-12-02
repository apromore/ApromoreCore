package org.apromore.dao.jpa;

import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Process DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class ProcessDaoJpaUnitTest {

    private ProcessDaoJpa prsJpa;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        prsJpa = new ProcessDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        prsJpa.setEntityManagerFactory(factory);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessDaoJpa.class);
    }

    @Test
    public final void testGetAllProcesses() {
        List<Process> prss = new ArrayList<Process>();
        prss.add(createProcess());
        prss.add(createProcess());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Process.GET_ALL_PROCESSES)).andReturn(query);
        expect(query.getResultList()).andReturn(prss);

        replay(manager, query);

        List<Object[]> processes = prsJpa.getAllProcesses();

        verify(manager, query);

        assertThat(prss.size(), equalTo(processes.size()));
    }


    @Test
    public final void testGetAllProcessesNonFound() {
        List<Object[]> procs = new ArrayList<Object[]>(0);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Process.GET_ALL_PROCESSES)).andReturn(query);
        expect(query.getResultList()).andReturn(procs);

        replay(manager, query);

        List<Object[]> processes = prsJpa.getAllProcesses();

        verify(manager, query);

        assertThat(processes, equalTo(procs));
    }

    @Test
    public final void testGetAllDomains() {
        List<Object> doms = new ArrayList<Object>();
        doms.add("domain1");
        doms.add("domain2");

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Process.GET_All_DOMAINS)).andReturn(query);
        expect(query.getResultList()).andReturn(doms);

        replay(manager, query);

        List<String> domains = prsJpa.getAllDomains();

        verify(manager, query);

        assertThat(doms.size(), equalTo(domains.size()));
    }


    @Test
    public final void testGetAllDomainsNonFound() {
        List<String> doms = new ArrayList<String>(0);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Process.GET_All_DOMAINS)).andReturn(query);
        expect(query.getResultList()).andReturn(doms);

        replay(manager, query);

        List<String> domains = prsJpa.getAllDomains();

        verify(manager, query);

        assertThat(domains, equalTo(doms));
    }


    @Test
    public final void testSaveProcess() {
        Process usr = createProcess();
        manager.persist(usr);
        replay(manager);
        prsJpa.save(usr);
        verify(manager);
    }

    @Test
    public final void testUpdateProcess() {
        Process usr = createProcess();
        expect(manager.merge(usr)).andReturn(usr);
        replay(manager);
        prsJpa.update(usr);
        verify(manager);
    }


    @Test
    public final void testDeleteProcess() {
        Process usr = createProcess();
        manager.remove(usr);
        replay(manager);
        prsJpa.delete(usr);
        verify(manager);
    }


    private Process createProcess() {
        Process prs = new Process();

        prs.setDomain("airport");
        prs.setProcessId(1234567890);
        prs.setName("testProcess");
        prs.setNativeType(new NativeType());
        prs.setUser(new User());

        return prs;
    }
}
