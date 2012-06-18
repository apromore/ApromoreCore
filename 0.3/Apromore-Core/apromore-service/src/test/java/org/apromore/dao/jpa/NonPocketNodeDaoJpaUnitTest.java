package org.apromore.dao.jpa;

import org.apromore.dao.model.NonPocketNode;
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
 * Test the NonPocketNode DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class NonPocketNodeDaoJpaUnitTest {

    private NonPocketNodeDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new NonPocketNodeDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NonPocketNodeDaoJpa.class, "em");
    }


    @Test
    public final void testSaveNonPocketNode() {
        NonPocketNode v = createNonPocketNode();
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateNonPocketNode() {
        NonPocketNode v = createNonPocketNode();
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteNonPocketNode() {
        NonPocketNode v = createNonPocketNode();
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }


    private NonPocketNode createNonPocketNode() {
        NonPocketNode e = new NonPocketNode();
        e.setVid(1);
        return e;
    }
}
