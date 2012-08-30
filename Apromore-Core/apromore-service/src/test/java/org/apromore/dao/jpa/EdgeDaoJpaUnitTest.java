package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Node;
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
 * Test the Edge DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class EdgeDaoJpaUnitTest {

    private EdgeDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new EdgeDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(EdgeDaoJpa.class, "em");
    }

    @Test
    public final void testGetEdgesByContent() {
        String id = "1";
        List<Edge> edges = new ArrayList<Edge>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_EDGES_BY_CONTENT)).andReturn(query);
        expect(query.setParameter("contentId", id)).andReturn(query);
        expect(query.getResultList()).andReturn(edges);

        replay(manager, query);

        List<Edge> result = dao.getEdgesByContent(id);

        verify(manager, query);

        assertThat(edges, equalTo(result));
    }

    @Test
    public final void testGetStoredEdges() {
        int count = 1;

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_STORED_EDGES)).andReturn(query);
        expect(query.getSingleResult()).andReturn(count);

        replay(manager, query);

        int result = dao.getStoredEdges();

        verify(manager, query);

        assertThat(count, equalTo(result));
    }

    @Test
    public final void testGetEdgesByFragment() {
        String id = "1";
        List<Edge> edges = new ArrayList<Edge>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_EDGES_BY_FRAGMENT)).andReturn(query);
        expect(query.setParameter("fragmentId", id)).andReturn(query);
        expect(query.getResultList()).andReturn(edges);

        replay(manager, query);

        List<Edge> result = dao.getEdgesByFragment(id);

        verify(manager, query);

        assertThat(edges, equalTo(result));
    }



    @Test
    public final void testSaveEdge() {
        Edge e = createEdge();
        manager.persist(e);
        replay(manager);
        dao.save(e);
        verify(manager);
    }

    @Test
    public final void testUpdateEdge() {
        Edge e = createEdge();
        expect(manager.merge(e)).andReturn(e);
        replay(manager);
        dao.update(e);
        verify(manager);
    }


    @Test
    public final void testDeleteEdge() {
        Edge e = createEdge();
        manager.remove(e);
        replay(manager);
        dao.delete(e);
        verify(manager);
    }


    private Edge createEdge() {
        Edge e = new Edge();
        e.setContent(new Content());
        e.setEdgeId(123);
        e.setVerticesBySourceVid(new Node());
        e.setVerticesByTargetVid(new Node());
        return e;
    }
}
