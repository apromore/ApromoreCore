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

import org.apromore.dao.ContentRepositoryCustom;
import org.apromore.dao.dataObject.ContentDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * implementation of the org.apromore.dao.ContentRepositoryCustom interface.
 * @author <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class ContentRepositoryCustomImpl implements ContentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Resource
    private JdbcTemplate jdbcTemplate;


    /* ************************** JPA Methods here ******************************* */



    /* ************************** JDBC Template / native SQL Queries ******************************* */

    @Override
    public ContentDO getContentDOByFragmentVersion(final Integer fragVersionId) {
        String sql = "SELECT c.* FROM content c JOIN fragment_version f ON f.contentId = c.id WHERE f.id = ?";

        List<ContentDO> contents = this.jdbcTemplate.query(sql, new Object[] { fragVersionId },
                new RowMapper<ContentDO>() {
                    public ContentDO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ContentDO contentDO = new ContentDO();
                        contentDO.setId(rs.getInt("id"));
                        contentDO.setBoundaryE(rs.getString("boundary_e"));
                        contentDO.setBoundaryS(rs.getString("boundary_s"));
                        return contentDO;
                    }
                });

        if (contents.isEmpty()) {
            return null;
        } else {
            return contents.get(0);
        }
    }

}
