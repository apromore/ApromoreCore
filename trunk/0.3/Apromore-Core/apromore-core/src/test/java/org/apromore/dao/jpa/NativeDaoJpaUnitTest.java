package org.apromore.dao.jpa;

import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.exception.NativeFormatNotFoundException;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the Native DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class NativeDaoJpaUnitTest {

    private NativeDaoJpa dao;
    private EntityManager manager;


    @Before
    public final void setUp() throws Exception {
        dao = new NativeDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManagerFactory(factory);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NativeDaoJpa.class);
    }


    @Test
    public final void testGetAllProcesses() {
        long processId = 123;
        String versionName = "123";
        List<Native> nats = new ArrayList<Native>();
        nats.add(createNative());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Native.FIND_NATIVE_TYPES)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getResultList()).andReturn(nats);

        replay(manager, query);

        List<Native> natives = dao.findNativeByCanonical(processId, versionName);

        verify(manager, query);

        assertThat(nats.size(), equalTo(natives.size()));
    }


    @Test
    public final void testGetAllProcessesNonFound() {
        long processId = 123;
        String versionName = "123";
        List<Native> nats = new ArrayList<Native>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Native.FIND_NATIVE_TYPES)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.getResultList()).andReturn(nats);

        replay(manager, query);

        List<Native> natives = dao.findNativeByCanonical(processId, versionName);

        verify(manager, query);

        assertThat(natives, equalTo(nats));
    }

    @Test
    public final void testGetNative() throws Exception {
        long processId = 123;
        String versionName = "123";
        String nativeType = "BPMN";

        String nativeXML = "<XML/>";

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Native.GET_NATIVE)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.setParameter("nativeType", nativeType)).andReturn(query);
        expect(query.getSingleResult()).andReturn(nativeXML);

        replay(manager, query);

        String nativeStr = dao.getNative(processId, versionName, nativeType);

        verify(manager, query);

        assertThat(nativeXML, equalTo(nativeStr));
    }

    @Test(expected = NativeFormatNotFoundException.class)
    public final void testGetNativeNotFound() throws Exception {
        long processId = 123;
        String versionName = "123";
        String nativeType = "BPMN";

        String nativeXML = null;

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(Native.GET_NATIVE)).andReturn(query);
        expect(query.setParameter("processId", processId)).andReturn(query);
        expect(query.setParameter("versionName", versionName)).andReturn(query);
        expect(query.setParameter("nativeType", nativeType)).andReturn(query);
        expect(query.getSingleResult()).andReturn(nativeXML);

        replay(manager, query);

        dao.getNative(processId, versionName, nativeType);

        verify(manager, query);
    }



    @Test
    public final void testSaveNative() {
        Native ann = createNative();
        manager.persist(ann);
        replay(manager);
        dao.save(ann);
        verify(manager);
    }

    @Test
    public final void testUpdateNative() {
        Native ann = createNative();
        expect(manager.merge(ann)).andReturn(ann);
        replay(manager);
        dao.update(ann);
        verify(manager);
    }


    @Test
    public final void testDeleteNative() {
        Native ann = createNative();
        manager.remove(ann);
        replay(manager);
        dao.delete(ann);
        verify(manager);
    }


    private Native createNative() {
        Native n = new Native();

        n.setContent("12345");
        n.setUri(12425535);
        n.setCanonical(new Canonical());
        n.setNativeType(new NativeType());

        Set<Annotation> anns = new HashSet<Annotation>();
        anns.add(new Annotation());

        n.setAnnotations(anns);

        return n;
    }
}
