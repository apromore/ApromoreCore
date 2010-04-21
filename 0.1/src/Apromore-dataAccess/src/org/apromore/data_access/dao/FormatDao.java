package org.apromore.data_access.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.model_manager.FormatsType;


public class FormatDao extends BasicDao {

	public FormatDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private static FormatDao instance ;

	public static FormatDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new FormatDao();
			}
			catch (Exception e) {
				throw new ExceptionDao("Error: not able to get instance for DAO");
			}
		}
		return instance;
	}

	public FormatsType getFormats() throws Exception {
		
		FormatsType formats = new FormatsType();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String requete = null;
		String format ;

		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			requete = " select " + ConstantDB.ATTR_NAT_TYPE + " from " + ConstantDB.TABLE_NATIVE_TYPES;
			rs = stmt.executeQuery(requete);
			formats.getFormat().clear();
			while (rs.next()) {
				format = rs.getString(ConstantDB.ATTR_NAT_TYPE);	
				formats.getFormat().add(format);
			}			
			return formats;
		} catch (Exception e) {
			throw new Exception("Error: FormatDao " + e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}
	
	
}
