package org.apromore.dao.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

import org.apromore.dao.ProcessRepositoryCustom;
import org.apromore.dao.model.Process;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ProcessRepositoryCustomImpl implements ProcessRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    private static final String GET_ALL_PROCESSES_JPA = "SELECT p FROM Process p ";
    private static final String GET_ALL_PRO_SORT_JPA = " ORDER by p.id";


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Process> findAllProcesses(final String conditions) {
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_PROCESSES_JPA);
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(" where ").append(conditions);
        }
        strQry.append(GET_ALL_PRO_SORT_JPA);

        Query query = em.createQuery(strQry.toString());
        return query.getResultList();
    }



    /* ************************** JDBC Template / native SQL Queries ******************************* */

}
