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

import org.apromore.dao.NodeRepositoryCustom;
import org.apromore.dao.dataObject.ContentDO;
import org.apromore.dao.dataObject.NodeDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * implementation of the org.apromore.dao.NodeRepositoryCustom interface.
 *
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class NodeRepositoryCustomImpl implements NodeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    public List<NodeDO> getNodeDOsByContent(final Integer contentId) {
        String sql = "select id, contentId, name, graphType, nodeType from node where content_id =?";

        return this.jdbcTemplate.query(sql, new Object[] { contentId },
                new RowMapper<NodeDO>() {
                    public NodeDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        NodeDO gnode = new NodeDO();
                        gnode.setId(rs.getInt("id"));
                        gnode.setContentId(rs.getInt("contentId"));
                        gnode.setName(rs.getString("name"));
                        gnode.setNodeType(rs.getString("nodeType"));
                        gnode.setGraphType(rs.getString("graphType"));
                        return gnode;
                    }
                });
    }

}
