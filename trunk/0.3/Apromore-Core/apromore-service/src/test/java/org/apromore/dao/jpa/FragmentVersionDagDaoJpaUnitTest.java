package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.FragmentVersionDagId;
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
 * Test the FragmentVersionDag DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class FragmentVersionDagDaoJpaUnitTest {

    private FragmentVersionDagDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new FragmentVersionDagDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(FragmentVersionDagDaoJpa.class, "em");
    }


    @Test
    public final void testFindFragmentVersionDag() {
        String id = "1";
        FragmentVersionDag v = new FragmentVersionDag();

        expect(manager.find(FragmentVersionDag.class, id)).andReturn(v);

        replay(manager);

        FragmentVersionDag result = dao.findFragmentVersionDag(id);

        verify(manager);

        assertThat(v, equalTo(result));
    }

    @Test
    public final void testGetChildMappings() {
        String fragmentId = "1";
        List<FragmentVersionDag> fvs = new ArrayList<FragmentVersionDag>();
        fvs.add(new FragmentVersionDag());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CHILD_MAPPINGS)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);

        List<FragmentVersionDag> childFv = dao.getChildMappings(fragmentId);

        verify(manager, query);

        assertThat(fvs.size(), equalTo(childFv.size()));
    }

    @Test
    public final void testGetChildFragmentsByFragmentVersion() {
        String fragmentVersionId = "1";
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(new FragmentVersion());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CHILD_FRAGMENTS_BY_FRAGMENT_VERSION)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);

        List<FragmentVersion> childFv = dao.getChildFragmentsByFragmentVersion(fragmentVersionId);

        verify(manager, query);

        assertThat(fvs.size(), equalTo(childFv.size()));
    }

    @Test
    public final void testSaveFragmentVersionDag() {
        FragmentVersionDag v = createFragmentVersionDag();
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateFragmentVersionDag() {
        FragmentVersionDag v = createFragmentVersionDag();
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteFragmentVersionDag() {
        FragmentVersionDag v = createFragmentVersionDag();
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }


    private FragmentVersionDag createFragmentVersionDag() {
        FragmentVersionDag e = new FragmentVersionDag();
        e.setId(new FragmentVersionDagId());
        e.setFragmentVersionByFragVerId(new FragmentVersion());
        return e;
    }
}
