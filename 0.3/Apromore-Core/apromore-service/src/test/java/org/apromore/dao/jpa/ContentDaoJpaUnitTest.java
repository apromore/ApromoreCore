package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Content;
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
 * Test the Content DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class ContentDaoJpaUnitTest {

    private ContentDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new ContentDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(ContentDaoJpa.class, "em");
    }

    @Test
    public final void testFindContent() {
        String id = "1";
        Content c = new Content();

        expect(manager.find(Content.class, id)).andReturn(c);
        replay(manager);

        Content result = dao.findContent(id);

        verify(manager);
        assertThat(c, equalTo(result));
    }

    @Test
    public final void testGetContentByFragmentVersion() {
        String fragVersionId = "1";
        Content con = new Content();
        List<Content> contents = new ArrayList<Content>();
        contents.add(con);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CONTENT_BY_FRAGMENT_VERSION)).andReturn(query);
        expect(query.setParameter("fragVersion", fragVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(contents);
        replay(manager, query);

        Content result = dao.getContentByFragmentVersion(fragVersionId);

        verify(manager, query);
        assertThat(con, equalTo(result));
    }

    @Test
    public final void testGetContentByHash() {
        String hashCode = "121";
        Content con = new Content();
        List<Content> contents = new ArrayList<Content>();
        contents.add(con);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CONTENT_BY_HASH)).andReturn(query);
        expect(query.setParameter("code", hashCode)).andReturn(query);
        expect(query.getResultList()).andReturn(contents);
        replay(manager, query);

        Content result = dao.getContentByCode(hashCode);

        verify(manager, query);
        assertThat(con, equalTo(result));
    }



    @Test
    public final void testSaveContent() {
        Content con = createContent();
        manager.persist(con);
        replay(manager);
        dao.save(con);
        verify(manager);
    }

    @Test
    public final void testUpdateContent() {
        Content con = createContent();
        expect(manager.merge(con)).andReturn(con);
        replay(manager);
        dao.update(con);
        verify(manager);
    }


    @Test
    public final void testDeleteProcess() {
        Content con = createContent();
        manager.remove(con);
        replay(manager);
        dao.delete(con);
        verify(manager);
    }


    private Content createContent() {
        Content c = new Content();
        c.setCode("12345");
        c.setBoundaryE("1");
        c.setBoundaryS("1");
        c.setContentId("1234565");
        return c;
    }
}
