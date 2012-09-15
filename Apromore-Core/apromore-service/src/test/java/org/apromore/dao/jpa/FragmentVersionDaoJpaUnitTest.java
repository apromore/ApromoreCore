package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Cluster;
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
import static org.powermock.api.easymock.PowerMock.*;

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
        Integer id = 0;
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
        fv.add(createFragmentVersion(1));
        fv.add(createFragmentVersion(2));

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
        Integer contentId = 12;
        String mappingCode = "2";

        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>(0);
        FragmentVersion fv = createFragmentVersion(1);
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
    public final void testGetMatchingFragmentVersionIdNothingFound() {
        Integer contentId = 12;
        String mappingCode = "2";
        FragmentVersion fv = null;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();

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
    public final void testGetUsedProcessModels() {
        Integer fragmentVersionId = 12;
        Integer fvCount = 2;
        List<Integer> fvs = new ArrayList<Integer>();
        fvs.add(fvCount);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_USED_PROCESS_MODEL_FOR_FRAGMENT)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);

        Integer result = dao.getUsedProcessModels(fragmentVersionId);

        verify(manager, query);
        assertThat(result, equalTo(fvCount));
    }

    @Test
    public final void testGetUsedProcessModelsNothingFound() {
        Integer fragmentVersionId = 12;
        Integer fvCount = null;
        List<Integer> fvs = new ArrayList<Integer>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_USED_PROCESS_MODEL_FOR_FRAGMENT)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);

        Integer result = dao.getUsedProcessModels(fragmentVersionId);

        verify(manager, query);
        assertThat(result, equalTo(fvCount));
    }

    @Test
    public final void testGetParentFragments() {
        Integer fragmentVersionId = 12;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(new FragmentVersion());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_PARENT_FRAGMENT_VERSIONS)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<FragmentVersion> childFv = dao.getParentFragments(fragmentVersionId);

        verify(manager, query);
        assertThat(fvs.size(), equalTo(childFv.size()));
    }

    @Test
    public final void testGetLockedParentFragments() {
        Integer fragmentVersionId = 12;
        List<Integer> fvs = new ArrayList<Integer>();
        fvs.add(1);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_LOCKED_PARENT_FRAGMENTS)).andReturn(query);
        expect(query.setParameter("childFragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<Integer> childFv = dao.getLockedParentFragmentIds(fragmentVersionId);

        verify(manager, query);
        assertThat(fvs.size(), equalTo(childFv.size()));
    }

    @Test
    public final void testGetContentId() {
        Integer fragmentVersionId = 12;
        Integer cntId = 11;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(createFragmentVersion(cntId));

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_FRAGMENT_VERSION)).andReturn(query);
        expect(query.setParameter("id", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        Integer contentId = dao.getContentId(fragmentVersionId);

        verify(manager, query);
        assertThat(cntId, equalTo(contentId));
    }

    @Test
    public final void testGetContentIdNothingFound() {
        Integer fragmentVersionId = 12;
        Integer cntId = null;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_FRAGMENT_VERSION)).andReturn(query);
        expect(query.setParameter("id", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        Integer contentId = dao.getContentId(fragmentVersionId);

        verify(manager, query);
        assertThat(cntId, equalTo(contentId));
    }

    @Test
    public final void testGetFragmentDataOfProcessModel() {
        Integer fragmentVersionId = 12;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(createFragmentVersion(1));

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_FRAGMENT_DATA_OF_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("procModelId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<FragmentVersion> fragData = dao.getFragmentDataOfProcessModel(fragmentVersionId);

        verify(manager, query);
        assertThat(fragData, equalTo(fvs));
    }

    @Test
    public final void testGetFragmentData() {
        Integer fragmentVersionId = 12;
        FragmentVersion fv = createFragmentVersion(1);
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(fv);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_FRAGMENT_DATA)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        FragmentVersion fragData = dao.getFragmentData(fragmentVersionId);

        verify(manager, query);
        assertThat(fragData, equalTo(fv));
    }

    @Test
    public final void testGetFragmentDataFoundNothing() {
        Integer fragmentVersionId = 12;
        FragmentVersion fv = null;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_FRAGMENT_DATA)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        FragmentVersion fragData = dao.getFragmentData(fragmentVersionId);

        verify(manager, query);
        assertThat(fragData, equalTo(fv));
    }

    @Test
    public final void testGetContainingFragments() {
        List<Integer> fragIds = new ArrayList<Integer>();

        replay(manager);
        List<Integer> fragData = dao.getContainingFragments(fragIds);

        verify(manager);
        assertThat(fragData, equalTo(null));
    }

    @Test
    public final void testGetContainedProcessModels() {
        Integer fragmentVersionId = 12;
        List<Integer> fvs = new ArrayList<Integer>();
        fvs.add(1);
        fvs.add(2);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CONTAINED_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("fragVersionId", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<Integer> fragData = dao.getContainedProcessModels(fragmentVersionId);

        verify(manager, query);
        assertThat(fragData, equalTo(fvs));
    }

    @Test
    public final void testGetUsedFragmentIds() {
        Integer matchingContentId = 12;
        List<Integer> fvs = new ArrayList<Integer>();
        fvs.add(1);
        fvs.add(2);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_USED_FRAGMENT_IDS)).andReturn(query);
        expect(query.setParameter("contentId", matchingContentId)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<Integer> fragData = dao.getUsedFragmentIds(matchingContentId);

        verify(manager, query);
        assertThat(fragData, equalTo(fvs));
    }

    @Test
    public final void testSimilarFragmentsBySize() {
        int minSize = 1;
        int maxSize = 100;
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(createFragmentVersion(1));
        fvs.add(createFragmentVersion(1));

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_SIMILAR_FRAGMENTS_BY_SIZE)).andReturn(query);
        expect(query.setParameter("min", minSize)).andReturn(query);
        expect(query.setParameter("max", maxSize)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<FragmentVersion> fragData = dao.getSimilarFragmentsBySize(minSize, maxSize);

        verify(manager, query);
        assertThat(fragData, equalTo(fvs));
    }

    @Test
    public final void testSimilarFragmentsBySizeAndType() {
        int minSize = 1;
        int maxSize = 100;
        String type = "TheType";
        List<FragmentVersion> fvs = new ArrayList<FragmentVersion>();
        fvs.add(createFragmentVersion(1));
        fvs.add(createFragmentVersion(1));

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_SIMILAR_FRAGMENTS_BY_SIZE_AND_TYPE)).andReturn(query);
        expect(query.setParameter("min", minSize)).andReturn(query);
        expect(query.setParameter("max", maxSize)).andReturn(query);
        expect(query.setParameter("type", type)).andReturn(query);
        expect(query.getResultList()).andReturn(fvs);

        replay(manager, query);
        List<FragmentVersion> fragData = dao.getSimilarFragmentsBySizeAndType(minSize, maxSize, type);

        verify(manager, query);
        assertThat(fragData, equalTo(fvs));
    }





    @Test
    public final void testSaveFragmentVersionDag() {
        FragmentVersion v = createFragmentVersion(1);
        manager.persist(v);
        replay(manager);
        dao.save(v);
        verify(manager);
    }

    @Test
    public final void testUpdateFragmentVersion() {
        FragmentVersion v = createFragmentVersion(1);
        expect(manager.merge(v)).andReturn(v);
        replay(manager);
        dao.update(v);
        verify(manager);
    }


    @Test
    public final void testDeleteFragmentVersion() {
        FragmentVersion v = createFragmentVersion(1);
        manager.remove(v);
        replay(manager);
        dao.delete(v);
        verify(manager);
    }



    private FragmentVersion createFragmentVersion(Integer id) {
        FragmentVersion e = new FragmentVersion();
        Cluster cl = new Cluster();
        cl.setId(id);

        e.setCluster(cl);
        e.setDerivedFromFragment("1");

        Content c = new Content();
        c.setId(id);
        e.setContent(c);

        return e;
    }
}
