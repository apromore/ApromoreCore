package org.apromore.data_access.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.model_manager.DomainsType;


public class DomainDao extends BasicDao {

	public DomainDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private static DomainDao instance ;

	public static DomainDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new DomainDao();
			}
			catch (Exception e) {
				throw new ExceptionDao("Error: not able to get instance for DAO");
			}
		}
		return instance;
	}

	public DomainsType getDomains() throws Exception {
		
		DomainsType domains = new DomainsType();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String requete = null;
		
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			requete = " select distinct " + ConstantDB.ATTR_DOMAIN + " from " + ConstantDB.TABLE_PROCESSES
				+ " order by 1";
			rs = stmt.executeQuery(requete);
			domains.getDomain().clear();
			while (rs.next()) {
				domains.getDomain().add(rs.getString(1));
			}			
			return domains;
		} catch (Exception e) {
			throw new Exception("Error: DomainDao " + e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}
	
	
}
