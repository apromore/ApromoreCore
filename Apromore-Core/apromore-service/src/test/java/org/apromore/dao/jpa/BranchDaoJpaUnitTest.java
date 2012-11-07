package org.apromore.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Branch DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class BranchDaoJpaUnitTest {

    private ProcessBranchDaoJpa dao;
    private EntityManager manager;


    @Before
    public final void setUp() throws Exception {
        dao = new ProcessBranchDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessBranchDaoJpa.class, "em");
    }


    @Test
    public final void testFindProcessBranch() {
        Integer id = 0;
        ProcessBranch b = new ProcessBranch();

        expect(manager.find(ProcessBranch.class, id)).andReturn(b);

        replay(manager);

        ProcessBranch result = dao.findProcessBranch(id);

        verify(manager);

        assertThat(b, equalTo(result));
    }

    @Test
    public final void testGetProcessBranchByProcessBranchName() {
        List<ProcessBranch> lbs = new ArrayList<ProcessBranch>(0);
        ProcessBranch b = new ProcessBranch();
        lbs.add(b);
        Integer processId = 1;
        String branchName = "branchName";

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_BRANCH_BY_PROCESS_BRANCH_NAME)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("name", branchName)).andReturn(query);
        expect(query.getResultList()).andReturn(lbs);

        replay(manager, query);

        ProcessBranch branch = dao.getProcessBranchByProcessBranchName(processId, branchName);

        verify(manager, query);

        assertThat(branch, equalTo(b));
    }

    @Test
    public final void testSaveBranch() {
        ProcessBranch b = createProcessBranch();
        manager.persist(b);
        replay(manager);
        dao.save(b);
        verify(manager);
    }

    @Test
    public final void testUpdateBranch() {
        ProcessBranch b = createProcessBranch();
        expect(manager.merge(b)).andReturn(b);
        replay(manager);
        dao.update(b);
        verify(manager);
    }


    @Test
    public final void testDeleteBranch() {
        ProcessBranch b = createProcessBranch();
        manager.remove(b);
        replay(manager);
        dao.delete(b);
        verify(manager);
    }


    private ProcessBranch createProcessBranch() {
        ProcessBranch b = new ProcessBranch();
        b.setProcess(new Process());
        b.setId(1);
        b.setBranchName("name");
        return b;
    }


}
