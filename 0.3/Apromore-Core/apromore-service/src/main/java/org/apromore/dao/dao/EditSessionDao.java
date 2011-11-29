package org.apromore.dao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apromore.common.ConstantDB;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExceptionEditSession;
import org.apromore.model.EditSessionType;

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
		String query = null;
		try {
			query = " delete from " + ConstantDB.TABLE_EDIT_SESSIONS
			+ " where " + ConstantDB.ATTR_CODE + " = " + code ;
			conn = this.getConnection();
			stmt = conn.prepareStatement(query);
			stmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			throw new Exception("Error: EditSessionDao " + e.getMessage());
		} finally {
			Release(conn, stmt, null);
		}
	}

	public int writeEditSession (EditSessionType editSession) throws ExceptionEditSession, SQLException {

		Connection conn = null;
        PreparedStatement stmt = null;
        Statement stmt2 = null;
		String query;
		ResultSet key = null;
		int code = 0;
        String uri = null;
		String username = editSession.getUsername();
		int processId = editSession.getProcessId();
		String versionName = editSession.getVersionName();
		String nativeType = editSession.getNativeType();
		String creationDate = editSession.getCreationDate();
		String lastupdate = editSession.getLastUpdate();
		Boolean withAnnotation = editSession.isWithAnnotation();
		String annotation = editSession.getAnnotation();
		try {
			conn = this.getConnection();
            query = "select " + ConstantDB.ATTR_URI
                + " from " + ConstantDB.ATTR_CANONICAL
			    + " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
                + "   and " + ConstantDB.ATTR_VERSION_NAME + " = " + versionName;
			stmt2 = conn.createStatement();
			key = stmt2.executeQuery(query);
			if (key.next()) {
               uri = key.getString(ConstantDB.ATTR_URI);
			} else {
				throw new ExceptionEditSession("Canonical uri not found.");
			}

			query = " insert into " + ConstantDB.TABLE_EDIT_SESSIONS 
			+ "(" + ConstantDB.ATTR_RECORD_TIME
			+ "," + ConstantDB.ATTR_USERNAME
            + "," + ConstantDB.ATTR_URI
			+ "," + ConstantDB.ATTR_PROCESSID
			+ "," + ConstantDB.ATTR_VERSION_NAME
			+ "," + ConstantDB.ATTR_NAT_TYPE 
			+ "," + ConstantDB.ATTR_ANNOTATION
			+ "," + ConstantDB.ATTR_CREATION_DATE
			+ "," + ConstantDB.ATTR_LAST_UPDATE
			+ ")"
			+ "values (now(), ?, ?, ?, ?, ?, ?, ?, ?) ";

			stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, username);
            stmt.setString(2, uri);
			stmt.setInt(3, processId);
			stmt.setString(4, versionName);
			stmt.setString(5, nativeType);
			stmt.setString(7, creationDate);
			stmt.setString(8, lastupdate);
			if (withAnnotation) {
				stmt.setString(6, annotation);
			} else {
				stmt.setNull(6, java.sql.Types.VARCHAR);
			}
			stmt.executeUpdate();
			key = stmt.getGeneratedKeys() ;
			if (!key.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			} 
			code = key.getInt(1);
			conn.commit();
			return code;
		} catch (Exception e) {
			conn.rollback();
			throw new ExceptionEditSession("EditSessionDao " + e.getMessage());
		} finally {
			Release(conn, stmt, key);
            stmt2.close();
		}
	}

	public EditSessionType getEditSession (int code) throws ExceptionEditSession {
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
			+ "," + ConstantDB.ATTR_CREATION_DATE
			+ "," + ConstantDB.ATTR_LAST_UPDATE
			+ ", coalesce(" + ConstantDB.ATTR_ANNOTATION + ",'')"
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
				editSession.setCreationDate(rs.getString(7));
				editSession.setLastUpdate(rs.getString(8));
				if (rs.getString(9).compareTo("")==0) {
					editSession.setWithAnnotation(false);
				} else {
					editSession.setWithAnnotation(true);
					editSession.setAnnotation(rs.getString(9));
				}
			} else {
				throw new ExceptionEditSession("EditSession not found. ");
			}
			return editSession;
		} catch (ExceptionEditSession e) {
			throw new ExceptionEditSession(e.getMessage());
		} catch (Exception e) {
			throw new ExceptionEditSession(e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}
}
