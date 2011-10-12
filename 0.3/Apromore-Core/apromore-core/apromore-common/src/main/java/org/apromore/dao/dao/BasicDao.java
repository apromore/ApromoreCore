package org.apromore.dao.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apromore.common.Constants;


public class BasicDao {

	private static DataSource ds;
	
	public BasicDao() throws Exception {
		this.ds = null;
		initPool();
	}

	public void initPool() throws Exception {
		ds = (DataSource) new InitialContext().lookup(Constants.CONTEXT);
	}

	public Connection getConnection() throws Exception {
		Connection conn = null;

		if (this.ds == null) {
			try {
				initPool();
			}
			catch (Exception e) {
				throw new Exception();
			}
		}	
		conn = this.ds.getConnection();
		conn.setAutoCommit(false);
		return conn;
	}
	
	public void Release (Connection conn, Statement stmt, ResultSet rs){
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				;
			}
			rs = null;
		}
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {
				;
			}
			stmt = null;
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				;
			}
			conn = null;
		}
	}
}
