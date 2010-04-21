package org.apromore.data_access.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;


public class XMLfilesDao extends BasicDao {

	public XMLfilesDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}


	private static XMLfilesDao instance;

	public static XMLfilesDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new XMLfilesDao();
			} catch (Exception e) {
				throw new ExceptionDao(
				"Error: not able to get instance for DAO");
			}
		}
		return instance;
	}

	/**
	 * found at http://www.java2s.com/Code/Java/Database-SQL-JDBC/ReadBLOBsdatafromdatabase.htm
	 * read/write blob from a database
	 */
	/**
	 * return the content of the file identified by uri which is the description of type (commons.ConstantDB.TABLE_NATIVES,
	 * commons.ConstantDB.TABLE_ANNOTATIONS, commons.ConstantDB.TABLE_RELATIONS). 
	 * @param String uri
	 * @param String type
	 * @return InputStream
	 * @throws ExceptionDao 
	 */
	public InputStream read (String uri, String type) throws ExceptionDao {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		String query = " select " + ConstantDB.ATTR_CONTENT 
		+ " from " + type
		+ " where " + ConstantDB.ATTR_URI + " = '" + uri + "'";
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				InputStream is = rs.getAsciiStream(1);
				return is;
			} else {
				throw new ExceptionDao ("Data not found.");
			}
		} catch (SQLException e) {
			throw new ExceptionDao ("Cannot access DBMS:" + e.getMessage());
		} catch (Exception e) {
			throw new ExceptionDao (e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}

	/**
	 * store the file content identified by uri. This file is a description of type
	 * (native, annotation or relation)
	 * @param uri
	 * @param type
	 * @param tableRelations 
	 * @param string 
	 * @param content
	 * @throws SQLException 
	 * @throws ExceptionDao 
	 */
	public void write(String uri, String username, String passwd, 
			String table, InputStream content) throws SQLException, ExceptionDao {
// TODO: check username/passwd
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String query = " insert into " + table
		+ "(" + ConstantDB.ATTR_URI + ","
		+		ConstantDB.ATTR_CONTENT + ")"
		+ " values (?, ?) ";
		try {
			stmt = conn.prepareStatement(query);
			stmt.setString(1, uri);
			stmt.setAsciiStream(2, content);
			stmt.execute();
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw new ExceptionDao ("Cannot access DBMS:" + e.getMessage());
		} catch (Exception e) {
			throw new ExceptionDao (e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}
}
