package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.*;

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
    public final void testFindProcessModelVersionByBranch() {
        Integer branchId = 1;
        String branchName = "MAIN";
        ProcessModelVersion pmv = new ProcessModelVersion();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_PROCESS_MODEL_VERSION_BY_BRANCH)).andReturn(query);
        expect(query.setParameter("id", branchId)).andReturn(query);
        expect(query.setParameter("name", branchName)).andReturn(query);
        expect(query.getSingleResult()).andReturn(pmv);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.findProcessModelVersionByBranch(branchId, branchName);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetUsedProcessModelVersions() {
        Integer fragmentVersionId = 1;
        ProcessModelVersion pmv = createProcessModelVersion();
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(pmv);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_USED_PROCESS_MODEL_VERSIONS)).andReturn(query);
        expect(query.setParameter("id", fragmentVersionId)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        List<ProcessModelVersion> pmvData = dao.getUsedProcessModelVersions(fragmentVersionId);

        verify(manager, query);
        assertThat(pmvData.size(), equalTo(pmvs.size()));
    }

    @Test
    public final void testGetCurrentVersion() {
        Integer processId = 1;
        String versionName = "MAIN";
        ProcessModelVersion pmv = createProcessModelVersion();
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(pmv);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getCurrentVersion(processId, versionName);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetCurrentVersionNothingFound() {
        Integer processId = 1;
        String versionName = "MAIN";
        ProcessModelVersion pmv = null;
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getCurrentVersion(processId, versionName);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }


    @Test(expected = NonUniqueResultException.class)
    public final void testGetCurrentVersionNotUnique() {
        Integer processId = 1;
        String versionName = "MAIN";
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(createProcessModelVersion());
        pmvs.add(createProcessModelVersion());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        dao.getCurrentVersion(processId, versionName);
        verify(manager, query);
    }

    @Test
    public final void testGetCurrentProcessModelVersionA() {
        Integer branchId = 1;
        ProcessModelVersion pmv = new ProcessModelVersion();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION_A)).andReturn(query);
        expect(query.setParameter("branchId", branchId)).andReturn(query);
        expect(query.getSingleResult()).andReturn(pmv);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getCurrentProcessModelVersion(branchId);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetCurrentProcessModelVersion() {
        Integer processId = 1;
        String versionName = "MAIN";
        ProcessModelVersion pmv = new ProcessModelVersion();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getSingleResult()).andReturn(pmv);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getCurrentProcessModelVersion(processId, versionName);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetCurrentProcessModelVersionB() {
        String processName = "1.0";
        String branchName = "MAIN";
        ProcessModelVersion pmv = new ProcessModelVersion();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION_B)).andReturn(query);
        expect(query.setParameter("processName", processName)).andReturn(query);
        expect(query.setParameter("branchName", branchName)).andReturn(query);
        expect(query.getSingleResult()).andReturn(pmv);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getCurrentProcessModelVersion(processName, branchName);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetCurrentProcessModelVersionC() {
        String processName = "Test PM";
        String branchName = "MAIN";
        String versionName = "1.0";
        ProcessModelVersion pmv = new ProcessModelVersion();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION_C)).andReturn(query);
        expect(query.setParameter("processName", processName)).andReturn(query);
        expect(query.setParameter("branchName", branchName)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getSingleResult()).andReturn(pmv);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getCurrentProcessModelVersion(processName, branchName, versionName);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetMaxVersionProcessModel() {
        ProcessBranch pb = createProcessBranch();
        ProcessModelVersion pmv = new ProcessModelVersion();
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(pmv);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_MAX_VERSION_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("branchId", pb.getId())).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getMaxVersionProcessModel(pb);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }

    @Test
    public final void testGetMaxVersionProcessModelNothingFound() {
        ProcessBranch pb = createProcessBranch();
        ProcessModelVersion pmv = null;
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_MAX_VERSION_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("branchId", pb.getId())).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        ProcessModelVersion pmvData = dao.getMaxVersionProcessModel(pb);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmv));
    }


    @Test(expected = NonUniqueResultException.class)
    public final void testGetMaxVersionProcessModelNotUnique() {
        ProcessBranch pb = createProcessBranch();
        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(new ProcessModelVersion());
        pmvs.add(new ProcessModelVersion());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_MAX_VERSION_PROCESS_MODEL)).andReturn(query);
        expect(query.setParameter("branchId", pb.getId())).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        dao.getMaxVersionProcessModel(pb);
        verify(manager, query);
    }

    @Test
    public final void testGetRootFragments() {
        int minSize = 1;
        List<Integer> pmvs = new ArrayList<Integer>();
        pmvs.add(1);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_ROOT_FRAGMENT_IDS_ABOVE_SIZE)).andReturn(query);
        expect(query.setParameter("minSize", minSize)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        List<Integer> pmvData = dao.getRootFragments(minSize);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmvs));
    }

    @Test
    public final void testGetAllProcessModelVersionsFalse() {
        boolean isLatestVersion = false;
        String str = "SELECT pmv FROM ProcessModelVersion pmv, ProcessBranch pb WHERE pb.branchId = pmv.processBranch.branchId " +
                " ORDER by pb.branchId, pb.creationDate ";

        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(new ProcessModelVersion());

        Query query = createMock(Query.class);
        expect(manager.createQuery(str)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        List<ProcessModelVersion> pmvData = dao.getAllProcessModelVersions(isLatestVersion);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmvs));
    }

    @Test
    public final void testGetAllProcessModelVersionsTrue() {
        boolean isLatestVersion = true;
        String str = "SELECT pmv FROM ProcessModelVersion pmv, ProcessBranch pb WHERE pb.branchId = pmv.processBranch.branchId " +
                "AND pb.creationDate in (SELECT max(pb2.creationDate) FROM ProcessBranch pb2 WHERE pb2.branchId = pmv.processBranch.branchId " +
                "GROUP BY pb2.branchId) ORDER by pb.branchId, pb.creationDate ";

        List<ProcessModelVersion> pmvs = new ArrayList<ProcessModelVersion>();
        pmvs.add(new ProcessModelVersion());

        Query query = createMock(Query.class);
        expect(manager.createQuery(str)).andReturn(query);
        expect(query.getResultList()).andReturn(pmvs);

        replay(manager, query);
        List<ProcessModelVersion> pmvData = dao.getAllProcessModelVersions(isLatestVersion);

        verify(manager, query);
        assertThat(pmvData, equalTo(pmvs));
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
        e.setVersionNumber(2d);
        return e;
    }

    private ProcessBranch createProcessBranch() {
        ProcessBranch pb = new ProcessBranch();
        pb.setId(1);
        pb.setBranchName("MAIN");
        return pb;
    }
}
