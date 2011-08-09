package org.apromore.data_access.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UtilDao extends BasicDao {
	
	public UtilDao() throws Exception {
		super();
	}
	
	public String getDay () throws Exception {
		String day = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String requete = null;
				
		requete = "select date_format(now(),'%d'), date_format(now(),'%m'), "
				+ " date_format(now(),'%Y'), "
				+ " date_format(now(),'%H'), date_format(now(),'%m')" ;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(requete);
			if (rs.next()) {
				day = rs.getString(1) + " " + rs.getInt(2)
					+ " " + rs.getString(3) + " " + rs.getString(4)
					+ ":" + rs.getString(5);
				
			}
		} catch (SQLException e) {throw e;}

		finally	{
			Release (conn, stmt, rs);
			}
		return day;
	}
}
