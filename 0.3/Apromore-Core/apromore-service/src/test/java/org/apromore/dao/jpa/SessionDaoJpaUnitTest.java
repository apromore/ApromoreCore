package org.apromore.dao.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.test.heuristic.JavaBeanHeuristic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

/**
 * Test the User DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class SessionDaoJpaUnitTest {

    private SessionDaoJpa dao;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        dao = new SessionDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        dao.setEntityManager(manager);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(SessionDaoJpa.class, "em");
    }

    @Test
    public final void testFindSessionFound() {
        Integer id = 1;
        EditSession sess = new EditSession();

        expect(manager.find(EditSession.class, id)).andReturn(sess);

        replay(manager);

        EditSession ses = dao.findSession(id);
        verify(manager);
        assertThat(sess, equalTo(ses));
    }

    @Test
    public final void testFindSessionNotFound() {
        Integer id = 123;
        EditSession sess = null;

        expect(manager.find(EditSession.class, id)).andReturn(sess);

        replay(manager);

        EditSession ses = dao.findSession(id);
        verify(manager);
        assertThat(sess, equalTo(ses));
    }



    @Test
    public final void testSaveSession() {
        EditSession ses = createEditSession();
        manager.persist(ses);
        replay(manager);
        dao.save(ses);
        verify(manager);
    }

    @Test
    public final void testUpdateSession() {
        EditSession ses = createEditSession();
        expect(manager.merge(ses)).andReturn(ses);
        replay(manager);
        dao.update(ses);
        verify(manager);
    }


    @Test
    public final void testDeleteSession() {
        EditSession ses = createEditSession();
        manager.remove(ses);
        replay(manager);
        dao.delete(ses);
        verify(manager);
    }


    private EditSession createEditSession() {
        EditSession usr = new EditSession();

        usr.setAnnotation("ann");
        usr.setCode(1);
        usr.setCreationDate("date1");
        usr.setLastUpdate("date2");
        usr.setNatType("native");
        usr.setProcess(new Process());
        usr.setProcessModelVersion(new ProcessModelVersion());
        usr.setRecordTime(new Date());
        usr.setRemoveFakeEvents(Boolean.FALSE);
        usr.setUser(new User());
        usr.setVersionName("version1");

        return usr;
    }
}
