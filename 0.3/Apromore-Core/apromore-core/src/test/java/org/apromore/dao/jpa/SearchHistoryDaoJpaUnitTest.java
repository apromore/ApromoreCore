package org.apromore.dao.jpa;

import org.apromore.dao.model.SearchHistory;
import org.apromore.dao.model.User;
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
 * Test the Search History DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class SearchHistoryDaoJpaUnitTest {

    private SearchHistoryDaoJpa srhJpa;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        srhJpa = new SearchHistoryDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        srhJpa.setEntityManagerFactory(factory);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(SearchHistoryDaoJpa.class);
    }



    @Test
    public final void testSaveUser() {
        SearchHistory search = createSearchHistory();
        manager.persist(search);
        replay(manager);
        srhJpa.save(search);
        verify(manager);
    }

    @Test
    public final void testUpdateUser() {
        SearchHistory search = createSearchHistory();
        expect(manager.merge(search)).andReturn(search);
        replay(manager);
        srhJpa.update(search);
        verify(manager);
    }


    @Test
    public final void testDeleteUser() {
        SearchHistory search = createSearchHistory();
        manager.remove(search);
        replay(manager);
        srhJpa.delete(search);
        verify(manager);
    }


    private SearchHistory createSearchHistory() {
        SearchHistory sch = new SearchHistory();

        sch.setNum(1);
        sch.setSearch("test");
        sch.setUser(createUser());

        return sch;
    }

    private User createUser() {
        User usr = new User();

        usr.setUsername("jamesc");
        usr.setFirstname("test");
        usr.setLastname("user");
        usr.setEmail("test@apromore.com");
        usr.setPasswd("password");

        return usr;
    }
}
