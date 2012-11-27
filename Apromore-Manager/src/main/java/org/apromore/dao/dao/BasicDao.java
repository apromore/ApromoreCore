package org.apromore.dao.dao;

import org.apromore.common.Constants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Basic DAO used by EditSession Dao and Process Dao.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class BasicDao {

    private static DataSource ds;

    /**
     * Public Constructor.
     * @throws Exception if something fails.....
     */
    public BasicDao() throws Exception {
        ds = null;
        initPool();
    }

    /**
     * Initialise the data source pool.
     * @throws Exception
     */
    public void initPool() throws Exception {
        ds = (DataSource) new InitialContext().lookup(Constants.CONTEXT);
    }

    /**
     * Get a connection.
     * @return a connection to the db.
     * @throws Exception if method fails to get a connection.
     */
    public Connection getConnection() throws Exception {
        Connection conn;

        if (ds == null) {
            try {
                initPool();
            } catch (Exception e) {
                throw new Exception();
            }
        }
        conn = ds.getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * release a connection back to the pool.
     * @param conn the connection.
     * @param stmt the db statement
     * @param rs the result set.
     */
    public void Release(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {  }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {  }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {  }
        }
    }
}
