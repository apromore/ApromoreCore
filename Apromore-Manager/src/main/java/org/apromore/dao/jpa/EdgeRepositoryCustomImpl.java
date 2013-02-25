/**
 *
 */
package org.apromore.dao.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apromore.dao.EdgeRepositoryCustom;
import org.apromore.dao.NodeRepositoryCustom;
import org.apromore.dao.dataObject.EdgeDO;
import org.apromore.dao.dataObject.NodeDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * implementation of the org.apromore.dao.EdgeRepositoryCustom interface.
 *
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class EdgeRepositoryCustomImpl implements EdgeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    public List<EdgeDO> getEdgeDOsByContent(final Integer contentId) {
        String sql = "select id, contentId, sourceNodeId, targetNodeId from edge where contentId =?";

        return this.jdbcTemplate.query(sql, new Object[] { contentId },
                new RowMapper<EdgeDO>() {
                    public EdgeDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        EdgeDO gedge = new EdgeDO();
                        gedge.setId(rs.getInt("id"));
                        gedge.setContentId(rs.getInt("contentId"));
                        gedge.setSourceId(rs.getInt("sourceNodeId"));
                        gedge.setTargetId(rs.getInt("targetNodeId"));
                        return gedge;
                    }
                });
    }

}
