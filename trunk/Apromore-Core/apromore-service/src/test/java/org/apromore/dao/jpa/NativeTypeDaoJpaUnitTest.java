package org.apromore.dao.jpa;

import org.apromore.common.Constants;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.NativeType;
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.easymock.PowerMock.*;

/**
 * Test the Native DAO JPA class.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class NativeTypeDaoJpaUnitTest {

    private NativeTypeDaoJpa dao;
    private EntityManager manager;


    @Before
    public final void setUp() throws Exception {
        dao = new NativeTypeDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(NativeTypeDaoJpa.class, "em");
    }


    @Test
    public final void testGetAllFormats() {
        List<NativeType> nats = new ArrayList<NativeType>();
        nats.add(createNativeType());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_NATIVE_TYPE_FORMAT)).andReturn(query);
        expect(query.getResultList()).andReturn(nats);

        replay(manager, query);

        List<NativeType> natives = dao.findAllFormats();

        verify(manager, query);

        assertThat(nats.size(), equalTo(natives.size()));
    }


    @Test
    public final void testGetAllProcessesNonFound() {
        List<NativeType> nats = new ArrayList<NativeType>();

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_NATIVE_TYPE_FORMAT)).andReturn(query);
        expect(query.getResultList()).andReturn(nats);

        replay(manager, query);

        List<NativeType> natives = dao.findAllFormats();

        verify(manager, query);

        assertThat(natives, equalTo(nats));
    }


    @Test
    public final void testGetNativeType() {
        String type = "XPDL 2.1";

        List<NativeType> types = new ArrayList<NativeType>(0);
        types.add(createNativeType());

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_NATIVE_TYPE_FORMATS)).andReturn(query);
        expect(query.setParameter("name", type)).andReturn(query);
        expect(query.getResultList()).andReturn(types);

        replay(manager, query);

        dao.findNativeType(type);

        verify(manager, query);

        assertThat(types.size(), equalTo(1));
    }


    @Test
    public final void testGetNativeTypeNonFound() {
        String type = "XPDL 2.1";
        NativeType nat = null;
        List<NativeType> types = new ArrayList<NativeType>(0);

        Query query = createMock(Query.class);
        expect(manager.createNamedQuery(NamedQueries.GET_NATIVE_TYPE_FORMATS)).andReturn(query);
        expect(query.setParameter("name", type)).andReturn(query);
        expect(query.getResultList()).andReturn(types);

        replay(manager, query);

        NativeType natve = dao.findNativeType(type);

        verify(manager, query);

        assertThat(natve, equalTo(nat));
    }



    private NativeType createNativeType() {
        NativeType n = new NativeType();
        n.setExtension("12345");
        n.setNatType("12425535");
        return n;
    }
}
