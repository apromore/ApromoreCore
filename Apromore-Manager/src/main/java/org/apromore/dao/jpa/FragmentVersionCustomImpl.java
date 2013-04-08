package org.apromore.dao.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apromore.dao.FragmentVersionRepositoryCustom;
import org.apromore.dao.dataObject.FragmentVersionDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class FragmentVersionCustomImpl implements FragmentVersionRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */


    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    public List<FragmentVersionDO> getAllSimilarFragmentsBySize(int minSize, int maxSize) {
        String sql = "SELECT fv.* FROM fragment_version fv WHERE fv.fragment_size > ? AND fv.fragment_size < ?";

        return jdbcTemplate.query(sql, new Object[] {minSize, maxSize},
                new RowMapper<FragmentVersionDO>() {
                    public FragmentVersionDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        FragmentVersionDO fragVerDO = new FragmentVersionDO();
                        fragVerDO.setId(rs.getInt("id"));
                        fragVerDO.setFragmentId(rs.getInt("fragmentId"));
                        fragVerDO.setContentId(rs.getInt("contentId"));
                        fragVerDO.setClusterId(rs.getInt("clusterId"));
                        fragVerDO.setUri(rs.getString("uri"));
                        fragVerDO.setChildMappingCode(rs.getString("child_mapping_code"));
                        fragVerDO.setDerivedFromFragment(rs.getInt("derived_from_fragment"));
                        fragVerDO.setLockStatus(rs.getInt("lock_status"));
                        fragVerDO.setLockCount(rs.getInt("lock_count"));
                        fragVerDO.setFragmentSize(rs.getInt("fragment_size"));
                        fragVerDO.setFragmentType(rs.getString("fragment_type"));
                        fragVerDO.setNewestNeighbor(rs.getString("newest_neighbor"));
                        return fragVerDO;
                    }
                });

    }

}
