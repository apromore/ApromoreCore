package org.apromore.data_access.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.model_manager.EditSessionType;

public class EditSessionDao extends BasicDao {


	public EditSessionDao() throws Exception {
		// TODO Auto-generated constructor stub
		super();
	}
	private static EditSessionDao instance ;

	public static EditSessionDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new EditSessionDao();
			}
			catch (Exception e) {
				throw new ExceptionDao("Error: not able to get instance for DAO");
			}
		}
		return instance;
	}

	public void deleteEditSession (int code) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		int rs = 0;
		String query = null;
		try {
			query = " delete from " + ConstantDB.TABLE_EDIT_SESSIONS
			+ " where " + ConstantDB.ATTR_CODE + " = " + code ;
			conn = this.getConnection();
			stmt = conn.prepareStatement(query);
			rs = stmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new Exception("Error: EditSessionDao " + e.getMessage());
		} finally {
			Release(conn, stmt, null);
		}
	}

	public int writeEditSession (EditSessionType editSession) {

		Connection conn = null;
		PreparedStatement stmt = null;
		String query = null;
		ResultSet key = null;
		int code = 0;
		String username = editSession.getUsername();
		int processId = editSession.getProcessId();
		String versionName = editSession.getVersionName();
		String nativeType = editSession.getNativeType();
		String processName = editSession.getProcessName();
		try {
			conn = this.getConnection();
			query = " insert into " + ConstantDB.TABLE_EDIT_SESSIONS 
			+ "(" + ConstantDB.ATTR_RECORD_TIME
			+ "," + ConstantDB.ATTR_USERNAME
			+ "," + ConstantDB.ATTR_PROCESSID
			+ "," + ConstantDB.ATTR_VERSION_NAME
			+ "," + ConstantDB.ATTR_NAT_TYPE + ")"
			+ "values (now(),?, ?, ?, ?) ";

			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, username);
			stmt.setInt(2, processId);
			stmt.setString(3, versionName);
			stmt.setString(4, nativeType);

			int rs = stmt.executeUpdate();
			key = stmt.getGeneratedKeys() ;
			if (!key.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			} 
			code = key.getInt(1);
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new Exception("Error: EditSessionDao " + e.getMessage());
		} finally {
			Release(conn, stmt, key);
			return code;
		}
	}

	public EditSessionType getEditSession (int code) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		EditSessionType editSession = new EditSessionType();
		try {
			conn = this.getConnection();
			query = " select " + ConstantDB.ATTR_USERNAME
			+ "," + ConstantDB.ATTR_PROCESSID
			+ "," + ConstantDB.ATTR_NAME
			+ "," + ConstantDB.ATTR_VERSION_NAME
			+ "," + ConstantDB.ATTR_NAT_TYPE
			+ "," + ConstantDB.ATTR_DOMAIN
			+ " from " + ConstantDB.TABLE_EDIT_SESSIONS
			+ " natural join " + ConstantDB.TABLE_PROCESSES
			+ " where " + ConstantDB.ATTR_CODE + " = " + code ;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				editSession.setUsername(rs.getString(1));
				editSession.setProcessId(rs.getInt(2));
				editSession.setProcessName(rs.getString(3));
				editSession.setVersionName(rs.getString(4));
				editSession.setNativeType(rs.getString(5));
				editSession.setDomain(rs.getString(6));
			} else {
				throw new Exception("Error: EditSessionDao: EditSession not found. ");
			}
		} catch (Exception e) {

		} finally {
			return editSession;
		}
	}
}
