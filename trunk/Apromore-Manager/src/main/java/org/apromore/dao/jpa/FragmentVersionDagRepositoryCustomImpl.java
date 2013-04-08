package org.apromore.dao.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.dao.FragmentVersionDagRepositoryCustom;
import org.apromore.dao.dataObject.FragmentVersionDagDO;
import org.apromore.dao.model.FragmentVersionDag;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * implementation of the org.apromore.dao.FragmentVersionDagDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class FragmentVersionDagRepositoryCustomImpl implements FragmentVersionDagRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */

    /**
     * @see org.apromore.dao.FragmentVersionDagRepository#getAllParentChildMappings()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getAllParentChildMappings() {
        Query query = em.createQuery("SELECT fvd FROM FragmentVersionDag fvd");
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<Integer, List<Integer>> parentChildMap = new HashMap<Integer, List<Integer>>();
        for (FragmentVersionDag mapping : mappings) {
            Integer pid = mapping.getFragmentVersion().getId();
            Integer cid = mapping.getChildFragmentVersion().getId();
            if (parentChildMap.containsKey(pid)) {
                parentChildMap.get(pid).add(cid);
            } else {
                List<Integer> childIds = new ArrayList<Integer>();
                childIds.add(cid);
                parentChildMap.put(pid, childIds);
            }
        }
        return parentChildMap;
    }

    /**
     * @see org.apromore.dao.FragmentVersionDagRepository#getAllChildParentMappings()
     *  {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getAllChildParentMappings() {
        Query query = em.createQuery("SELECT fvd FROM FragmentVersionDag fvd");
        List<FragmentVersionDag> mappings = (List<FragmentVersionDag>) query.getResultList();

        Map<Integer, List<Integer>> childParentMap = new HashMap<Integer, List<Integer>>();
        for (FragmentVersionDag mapping : mappings) {
            Integer pid = mapping.getFragmentVersion().getId();
            Integer cid = mapping.getChildFragmentVersion().getId();
            if (childParentMap.containsKey(cid)) {
                childParentMap.get(cid).add(pid);
            } else {
                List<Integer> parentIds = new ArrayList<Integer>();
                parentIds.add(pid);
                childParentMap.put(cid, parentIds);
            }
        }
        return childParentMap;
    }




    /* ************************** JDBC Template / native SQL Queries ******************************* */

    /**
     * @see org.apromore.dao.FragmentVersionDagRepository#getChildMappingsDO(Integer)
     * {@inheritDoc}
     */
    @Override
    public List<FragmentVersionDagDO> getChildMappingsDO(Integer fragmentId) {
        String sql = "SELECT id, fragmentVersionId, childFragmentVersionId, pocketId FROM fragment_version_dag WHERE fragmentVersionId = ?";

        return jdbcTemplate.query(sql, new Object[] { fragmentId },
            new RowMapper<FragmentVersionDagDO>() {
                public FragmentVersionDagDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    FragmentVersionDagDO cmap = new FragmentVersionDagDO();
                    cmap.setId(rs.getInt("id"));
                    cmap.setFragmentVersionId(rs.getInt("fragmentVersionId"));
                    cmap.setChildFragmentVersionId(rs.getInt("childFragmentVersionId"));
                    cmap.setPocketId(rs.getString("pocketId"));
                    return cmap;
                }
            });
    }


    /**
     * @see org.apromore.dao.FragmentVersionDagRepository#getAllDAGEntriesBySize(int)
     * {@inheritDoc}
     */
    @Override
    public List<FragmentVersionDagDO> getAllDAGEntriesBySize(int minimumChildFragmentSize) {
        String sql = "SELECT fvd.* " +
                "FROM fragment_version_dag fvd JOIN fragment_version fv ON fvd.childFragmentVersionId = fv.id " +
                "WHERE fv.fragment_size > ?";

        return jdbcTemplate.query(sql, new Object[] { minimumChildFragmentSize },
                new RowMapper<FragmentVersionDagDO>() {
                    public FragmentVersionDagDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        FragmentVersionDagDO cmap = new FragmentVersionDagDO();
                        cmap.setId(rs.getInt("id"));
                        cmap.setFragmentVersionId(rs.getInt("fragmentVersionId"));
                        cmap.setChildFragmentVersionId(rs.getInt("childFragmentVersionId"));
                        cmap.setPocketId(rs.getString("pocketId"));
                        return cmap;
                    }
                });
    }

}
