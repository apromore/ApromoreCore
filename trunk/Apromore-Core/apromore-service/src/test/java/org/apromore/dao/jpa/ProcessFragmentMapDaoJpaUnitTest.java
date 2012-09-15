package org.apromore.dao.jpa;

import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.ProcessFragmentMap;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the ProcessFragmentMap DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class ProcessFragmentMapDaoJpaUnitTest {

    private ProcessFragmentMapDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new ProcessFragmentMapDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessFragmentMapDaoJpa.class, "em");
    }


    @Test
    public final void testSaveProcessFragmentMap() {
        ProcessFragmentMap v = createProcessFragmentMap();
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateProcessFragmentMap() {
        ProcessFragmentMap v = createProcessFragmentMap();
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteProcessFragmentMap() {
        ProcessFragmentMap v = createProcessFragmentMap();
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }


    private ProcessFragmentMap createProcessFragmentMap() {
        ProcessFragmentMap e = new ProcessFragmentMap();
        e.setFragmentVersion(new FragmentVersion());
        e.setId(1);
        e.setProcessModelVersion(new ProcessModelVersion());
        return e;
    }
}
