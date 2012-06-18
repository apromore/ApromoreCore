package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
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
 * Test the FragmentVersion DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class FragmentVersionDaoJpaUnitTest {

    private FragmentVersionDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new FragmentVersionDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FragmentVersionDaoJpa.class, "em");
    }


    @Test
    public final void testFindFragmentVersionDag() {
        String id = "1";
        FragmentVersion v = new FragmentVersion();

        expect(manager.find(FragmentVersion.class, id)).andReturn(v);
        replay(manager);

        FragmentVersion result = dao.findFragmentVersion(id);

        verify(manager);
        assertThat(v, equalTo(result));
    }

    @Test
    public final void testGetAllFragmentVersion() {
        List<FragmentVersion> fv = new ArrayList<FragmentVersion>();
        fv.add(createFragmentVersion());
        fv.add(createFragmentVersion());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ALL_FRAGMENT_VERSION)).andReturn(query);
        expect(query.getResultList()).andReturn(fv);

        replay(manager, query);

        List<FragmentVersion> result = dao.getAllFragmentVersion();

        verify(manager, query);
        assertThat(result, equalTo(fv));
    }

    @Test
    public final void testGetMatchingFragmentVersionId() {
        String contentId = "12";
        String mappingCode = "2";

        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>(0);
        FragmentVersion fv = createFragmentVersion();
        fvs.add(fv);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_FRAGMENT_BY_CONTENT_MAPPING)).andReturn(query);
        expect(query.setParameter("contentId", contentId)).andReturn(query);
        expect(query.setParameter("mappingCode", mappingCode)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);

        FragmentVersion result = dao.getMatchingFragmentVersionId(contentId, mappingCode);

        verify(manager, query);
        assertThat(result, equalTo(fv));
    }



    @Test
    public final void testSaveFragmentVersionDag() {
        FragmentVersion v = createFragmentVersion();
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateFragmentVersion() {
        FragmentVersion v = createFragmentVersion();
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteFragmentVersion() {
        FragmentVersion v = createFragmentVersion();
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }


    private FragmentVersion createFragmentVersion() {
        FragmentVersion e = new FragmentVersion();
        e.setClusterId("clusterid");
        e.setContent(new Content());
        e.setDerivedFromFragment("1");
        return e;
    }
}
