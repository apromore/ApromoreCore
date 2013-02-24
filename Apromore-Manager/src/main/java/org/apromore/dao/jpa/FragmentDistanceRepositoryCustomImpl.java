/**
 *
 */
package org.apromore.dao.jpa;

import org.apromore.dao.FragmentDistanceRepositoryCustom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * implementation of the org.apromore.dao.ClusteringDao interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class FragmentDistanceRepositoryCustomImpl implements FragmentDistanceRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    public Double getDistance(Integer FragmentId1, Integer FragmentId2) {
        String sql = "select ged from fragment_distance where (fragmentVersionId1 = ? and fragmentVersionId2 = ?) or " +
                "(fragmentVersionId1 = ? and fragmentVersionId2 = ?)";

        List<Double> geds = this.jdbcTemplate.query(sql, new Object[] {FragmentId1, FragmentId2, FragmentId1, FragmentId2},
                new RowMapper<Double>() {
                    public Double mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getDouble("fs_ged");
                    }
                });
        if (geds.isEmpty()) {
            return 1d;
        } else {
            return geds.get(0);
        }
    }

}
