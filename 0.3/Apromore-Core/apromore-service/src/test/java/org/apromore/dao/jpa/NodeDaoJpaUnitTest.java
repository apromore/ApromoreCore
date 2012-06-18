package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Content;
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
 * Test the Node DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class NodeDaoJpaUnitTest {

    private NodeDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new NodeDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NodeDaoJpa.class, "em");
    }



    @Test
    public final void testFindVertex() {
        Integer id = 1;
        Node v = new Node();

        expect(manager.find(Node.class, id)).andReturn(v);

        replay(manager);

        Node result = dao.findNode(id);

        verify(manager);

        assertThat(v, equalTo(result));
    }

    @Test
    public final void testGetContentIDs() {
        List<String> edges = new ArrayList<String>();
        edges.add("edge1");

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CONTENT_IDS)).andReturn(query);
        expect(query.getResultList()).andReturn(edges);

        replay(manager, query);

        List<String> result = dao.getContentIDs();

        verify(manager, query);

        assertThat(edges, equalTo(result));
    }

    @Test
    public final void testGetVertexByContent() {
        String contentID = "1234";
        List<Node> vertices = new ArrayList<Node>();
        vertices.add(new Node());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_VERTICES_BY_CONTENT)).andReturn(query);
        expect(query.setParameter("contentId", contentID)).andReturn(query);
        expect(query.getResultList()).andReturn(vertices);

        replay(manager, query);

        List<Node> results = dao.getVertexByContent(contentID);

        verify(manager, query);

        assertThat(vertices, equalTo(results));
    }

    @Test
    public final void testGetStoredVertices() {
        int count = 1;

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_STORED_VERTICES)).andReturn(query);
        expect(query.getSingleResult()).andReturn(count);

        replay(manager, query);

        int result = dao.getStoredVertices();

        verify(manager, query);

        assertThat(count, equalTo(result));
    }


    @Test
    public final void testSaveVertex() {
        Node v = createVertex();
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateVertex() {
        Node v = createVertex();
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteVertex() {
        Node v = createVertex();
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }


    private Node createVertex() {
        Node e = new Node();
        e.setContent(new Content());
        e.setVid(1);
        e.setVname("name");
        e.setVtype("type");
        return e;
    }
}
