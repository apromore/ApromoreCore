package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.UserDao;
import org.apromore.dao.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Hibernate implementation of the org.apromore.dao.UserDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class UserDaoJpa implements UserDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.UserDao#findUser(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public User findUser(String username) {
        return em.find(User.class, username);
    }

    /**
     * @see org.apromore.dao.UserDao#findAllUsers()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        Query query = em.createNamedQuery(NamedQueries.GET_ALL_USERS);
        return query.getResultList();
    }


    /**
     * @see org.apromore.dao.UserDao#save(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    public void save(final User user) {
        em.persist(user);
    }

    /**
     * @see org.apromore.dao.UserDao#update(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    public User update(final User user) {
        return em.merge(user);
    }

    /**
     * @see org.apromore.dao.UserDao#delete(org.apromore.dao.model.User)
     * {@inheritDoc}
     */
    @Override
    public void delete(final User user) {
        em.remove(user);
    }




    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
