package org.apromore.dao.jpa;

import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the ProcessModelVersion DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class ProcessModelVersionDaoJpaUnitTest {

    private ProcessModelVersionDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new ProcessModelVersionDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ProcessModelVersionDaoJpa.class, "em");
    }


    @Test
    public final void testFindFragmentVersionDag() {
        Integer id = 1;
        ProcessModelVersion v = new ProcessModelVersion();

        expect(manager.find(ProcessModelVersion.class, id)).andReturn(v);
        replay(manager);

        ProcessModelVersion result = dao.findProcessModelVersion(id);

        verify(manager);
        assertThat(v, equalTo(result));
    }


    @Test
    public final void testSaveProcessModelVersion() {
        ProcessModelVersion v = createProcessModelVersion();
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateProcessModelVersion() {
        ProcessModelVersion v = createProcessModelVersion();
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteProcessModelVersion() {
        ProcessModelVersion v = createProcessModelVersion();
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }


    private ProcessModelVersion createProcessModelVersion() {
        ProcessModelVersion e = new ProcessModelVersion();
        e.setChangePropagation(1);
        e.setLockStatus(1);
        e.setNumEdges(2);
        e.setNumVertices(2);
        e.setVersionName("name");
        e.setVersionNumber(2);
        return e;
    }
}
