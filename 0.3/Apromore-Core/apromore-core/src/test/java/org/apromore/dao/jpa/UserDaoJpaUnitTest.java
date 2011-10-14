package org.apromore.dao.jpa;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

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

/**
 * Test the User DAO JPA class.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@RunWith(PowerMockRunner.class)
public class UserDaoJpaUnitTest {

    private UserDaoJpa usrJpa;
    private EntityManager manager;

    @Before
    public final void setUp() throws Exception {
        usrJpa = new UserDaoJpa();
        EntityManagerFactory factory = createMock(EntityManagerFactory.class);
        manager = createMock(EntityManager.class);
        usrJpa.setEntityManagerFactory(factory);
        expect(factory.createEntityManager()).andReturn(manager).anyTimes();
        replay(factory);
    }

    @Test
    public final void testIsAPOJO() {
        JavaBeanHeuristic.assertLooksLikeJavaBean(UserDaoJpa.class);
    }

    @Test
    public final void testFindUserFound() {
        String username = "jamesc";
        User user = new User();

        expect(manager.find(User.class, username)).andReturn(user);

        replay(manager);

        User usr = usrJpa.findUser(username);
        verify(manager);
        assertThat(user, equalTo(usr));
    }

    @Test
    public final void testFindUserNotFound() {
        String username = "XXXX";
        User user = null;

        expect(manager.find(User.class, username)).andReturn(user);

        replay(manager);

        User usr = usrJpa.findUser(username);
        verify(manager);
        assertThat(user, equalTo(usr));
    }

    @Test
    public final void testFindAllUsersSomeFound() {
        Query query = createMock(Query.class);

        expect(manager.createNamedQuery(User.FIND_ALL_USERS)).andReturn(query);

        List<User> users = new ArrayList<User>();
        User usr = createUser();
        users.add(usr);
        expect(query.getResultList()).andReturn(users);

        replay(manager);
        replay(query);

        List<User> userList = usrJpa.findAllUsers();
        verify(query);
        assertThat(users, equalTo(userList));
    }

    @Test
    public final void testFindAllUsersNoOneFound() {
        Query query = createMock(Query.class);

        expect(manager.createNamedQuery(User.FIND_ALL_USERS)).andReturn(query);

        List<User> users = new ArrayList<User>();
        expect(query.getResultList()).andReturn(users);

        replay(manager);
        replay(query);

        List<User> userList = usrJpa.findAllUsers();
        verify(query);
        assertThat(null, equalTo(userList));
    }



    @Test
    public final void testSaveUser() {
        User usr = createUser();
        manager.persist(usr);
        replay(manager);
        usrJpa.save(usr);
        verify(manager);
    }

    @Test
    public final void testUpdateUser() {
        User usr = createUser();
        expect(manager.merge(usr)).andReturn(usr);
        replay(manager);
        usrJpa.update(usr);
        verify(manager);
    }


    @Test
    public final void testDeleteUser() {
        User usr = createUser();
        manager.remove(usr);
        replay(manager);
        usrJpa.delete(usr);
        verify(manager);
    }


    private User createUser() {
        User usr = new User();

        usr.setUsername("jamesc");
        usr.setFirstname("test");
        usr.setLastname("user");
        usr.setEmail("test@apromore.com");
        usr.setPassword("password");

        return usr;
    }
}
