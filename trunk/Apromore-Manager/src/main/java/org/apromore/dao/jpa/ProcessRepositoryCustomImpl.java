package org.apromore.dao.jpa;

import org.apromore.dao.model.Process;
import org.apromore.dao.ProcessRepositoryCustom;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ProcessRepositoryCustomImpl implements ProcessRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    /** The start of the manual search query. */
    public static final String GET_ALL_PROCESSES = "SELECT p FROM Process p ";
    /** The order by for the manual search query. */
    public static final String GET_ALL_PRO_SORT = " ORDER by p.id";


    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Process> findAllProcesses(final String conditions) {
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_PROCESSES);
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(conditions);
        }
        strQry.append(GET_ALL_PRO_SORT);

        Query query = em.createQuery(strQry.toString());
        return query.getResultList();
    }

}
