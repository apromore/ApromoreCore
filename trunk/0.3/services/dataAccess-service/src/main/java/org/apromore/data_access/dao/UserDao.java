package org.apromore.data_access.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.exception.ExceptionUnknownUser;
import org.apromore.data_access.model_manager.SearchHistoriesType;
import org.apromore.data_access.model_manager.UserType;
import org.apromore.data_access.model_manager.UsernamesType;


public class UserDao extends BasicDao {

	private static UserDao instance ;

	public static UserDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new UserDao();
			}
			catch (Exception e) {
				throw new ExceptionDao("Error: not able to get instance for DAO");
			}
		}
		return instance;
	}

	public UserDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * TODO: check credentials
	 * returns user whose username is username and passwd passwd
	 * @param id
	 * @return UserType
	 * @throws Exception is incorrect username/passwd or other exceptions
	 */
	public UserType readUser (String username) throws Exception {
		UserType user=null;
		SearchHistoriesType history = null;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		Statement stmtS = null;
		ResultSet rsS = null;
		String queryS = null;

		try {
			conn = this.getConnection();
			stmt = conn.createStatement();

			query = " SELECT " + ConstantDB.ATTR_LASTNAME 
			+ ", " + ConstantDB.ATTR_FIRSTNAME 
			+ ", " + ConstantDB.ATTR_USERNAME 
			+ ", " + ConstantDB.ATTR_EMAIL 
			+ ", " + ConstantDB.ATTR_PASSWD
			+ " FROM " + ConstantDB.TABLE_USERS
			+ " WHERE " + ConstantDB.ATTR_USERNAME + "= '" + username +"'" ;
//			+       " and " + ConstantDB.ATTR_PASSWD + "='" + passwd + "'";

			rs = stmt.executeQuery(query);

			if (rs.next()) {
				user = new UserType ();
				user.setLastname(rs.getString(1));
				user.setFirstname(rs.getString(2));
				user.setUsername(rs.getString(3));
				user.setEmail(rs.getString(4));
				user.setPasswd(rs.getString(5));
				stmtS = conn.createStatement();

				queryS = "select " + ConstantDB.ATTR_SEARCH + "," + ConstantDB.ATTR_NUM
				+ " from " + ConstantDB.TABLE_SEARCH_HISTORIES
				+ " where " + ConstantDB.ATTR_USERNAME + "= '" + user.getUsername() + "'" 
				+ " order by " + ConstantDB.ATTR_SEARCH;

				rsS = stmtS.executeQuery(queryS);

				while (rsS.next()) {
					history = new SearchHistoriesType();
					history.setSearch(rsS.getString(1));
					history.setNum(rsS.getInt(2));
					user.getSearchHistories().add(history);
				}
			} else {
				throw new ExceptionUnknownUser("Error: user not found");
			}
		}
		catch (SQLException e) {
			throw new Exception("Error: UserDao " + e.getMessage());
		}
		finally {
			Release(conn, stmt, rs);
		}
		return user;
	}

	/**
	 * Return the list of all registered users.
	 * @return
	 * @throws org.apromore.data_access.exception.ExceptionDao
	 */
	public UsernamesType getAllUsers() throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		UsernamesType allUsers = new UsernamesType();
		try {
			conn = this.getConnection();
			query = " select "  + ConstantDB.ATTR_LASTNAME 
			+ ", " + ConstantDB.ATTR_FIRSTNAME 
			+ ", " + ConstantDB.ATTR_USERNAME 
			+ ", " + ConstantDB.ATTR_EMAIL 
			+ ", " + ConstantDB.ATTR_PASSWD
			+ " FROM " + ConstantDB.TABLE_USERS
			+ " order by " + ConstantDB.ATTR_USERNAME;
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				allUsers.getUsername().add(rs.getString(3));
			}
			return allUsers;
		}catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("SQL error: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("SQL error: " + e.getMessage());
		}
	}

	/**
	 * Update a user's search history: replace the previous one (in the db) 
	 * with the new one (associated with user)
	 * @throws Exception 
	 * TODO: if user already exists: update details and search history
	 * otherwise create user.
	 */
	public void writeUser (UserType user) throws Exception {
		Connection conn = null;
		Statement stmt1 = null, 
		stmt2 = null;
		try {
			String query = "delete from " + ConstantDB.TABLE_SEARCH_HISTORIES
			+ " where " + ConstantDB.ATTR_USERNAME
			+       " = '" + user.getUsername() + "'";
			conn = this.getConnection();
			stmt1 = conn.createStatement();
			int res = stmt1.executeUpdate(query);
			Iterator itU = user.getSearchHistories().iterator();
			while (itU.hasNext()){
				String search = ((SearchHistoriesType) itU.next()).getSearch();		
				query = "insert into " + ConstantDB.TABLE_SEARCH_HISTORIES
				+ "(" + ConstantDB.ATTR_USERNAME + "," + ConstantDB.ATTR_SEARCH + ")"
				+ " values ( '" +  user.getUsername() + "', '" + search + "')";
				stmt2 = conn.createStatement();
				res = stmt2.executeUpdate(query);
				stmt2.close();
			}
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// user does not exist, foreign key fails
				if (e1.getSQLState().compareTo("23000")==0) {
					throw new ExceptionUnknownUser ("Error: user not found");
				} else {
					throw new ExceptionDao (e1);
				}
			}
			throw new Exception(e);
		} finally {
			Release(conn, stmt1, null);
		}


	}
}
