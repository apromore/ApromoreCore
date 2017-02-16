package org.apromore.service.perfmining.database;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Interval;

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */

/**
 * 
 * @author Administrator
 */
public class ConnectionManager {

	public static int CUM_TYPE_ARRIVAL = 1;
	public static int CUM_TYPE_DEPARTURE = 2;
	public static int CUM_TYPE_EXIT = 3;

	private static String driverName = "";
	private static String conURL = "";
	private static String username = "";
	private static String password = "";
	private static Connection con;
	private static ConnectionManager me = null;

	//key:stage,value:list of activity intervals for every activity in a stage
	public static Map<String, List<Interval>> stageActivityIntervalMap = new HashMap<String, List<Interval>>();

	//key:stage,value:list of intervals for every case at a stage (from start to end of service) 
	public static Map<String, List<Interval>> stageServiceIntervalMap = new HashMap<String, List<Interval>>();

	//key:stage,value:list of intervals for every case at a stage (from queue to end of service)
	public static Map<String, List<Interval>> stageTotalIntervalMap = new HashMap<String, List<Interval>>();

	public static void main(String[] args) {
		//        String str="";
		//        try {
		//            getConnection();
		//            str = ConnectionManager.executeSQL("select * from Employees");
		//            
		//        } catch (Exception ex) {
		////            ex.printStackTrace();
		//        }
		//        System.out.println(str);

	}

	public static ConnectionManager getInstance() {
		if (me == null) {
			me = new ConnectionManager();
		}
		return me;
	}

	public static void close() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void initConnectionProperties(Map<String, String> conParams) {
		driverName = conParams.get("DBDriverName");
		conURL = conParams.get("DBConnectionURL");
		username = conParams.get("DBUsername");
		password = conParams.get("DBPassword");

		if ((conParams.get("DBDriverName") != null) && (conParams.get("DBConnectionURL") != null)
				&& (conParams.get("DBUsername") != null) && (conParams.get("DBPassword") != null)) {
			driverName = conParams.get("DBDriverName");
			conURL = conParams.get("DBConnectionURL");
			username = conParams.get("DBUsername");
			password = conParams.get("DBPassword");
		} else {
			System.out.println("Initialisation failed for DB Connection Parameters");
		}
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {

		if (con != null) {
			return con;
		}

		try {
                    Class.forName(driverName);
                    System.out.println("jdbc.driver: " + driverName);
                    System.out.println("jdbc.url: " + conURL);
                    System.out.println("jdbc.username: " + username);
                    System.out.println("jdbc.password: " + password);

                    con = DriverManager.getConnection(conURL, username, password);
                    con.setAutoCommit(false);
                    return con;
		}catch (ClassNotFoundException ex) {
                    throw ex;
                } catch (SQLException ex) {
			// log an exception. for example:
			//            System.out.println("Driver not found."); 
			throw ex;
		}

	}

	/*
	 * Initialize parameters from an XML file This file is located in the same
	 * folder as the root folder of this application
	 */
	public static void initParametersFromFile() {
		// Read database connection properties
		DBConnectionParamReader paramReader;
		try {
			paramReader = new DBConnectionParamReader(System.getProperty("user.dir") + "\\properties.xml");
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage());
			return;
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			return;
		}

		Map<String, String> mapDBParams = new Hashtable<String, String>();
		mapDBParams.put("DBDriverName", paramReader.getDriverName());
		mapDBParams.put("DBConnectionURL", paramReader.getUrl());
		mapDBParams.put("DBUsername", paramReader.getUsername());
		mapDBParams.put("DBPassword", paramReader.getPassword());
		ConnectionManager.initConnectionProperties(mapDBParams);
	}

	public static ResultSet executeSQL(String sql) throws ClassNotFoundException, SQLException {
		ResultSet rs;
		Statement stmt;

		if (con == null) {
			getConnection();
		} else if (con.isClosed()) {
			getConnection();
		}

		stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		rs = stmt.executeQuery(sql);

		return rs;
	}

	public static void executeStatement(String sql) throws ClassNotFoundException, SQLException {
		Statement stmt;

		if (con == null) {
			getConnection();
		} else if (con.isClosed()) {
			getConnection();
		}

		stmt = con.createStatement();
		stmt.executeUpdate(sql);
		con.commit();
		stmt.close();

	}

	//    public static void insertEvents(String caseID, String activity, String start, String complete, String resource, 
	//    								String stage, String type) throws SQLException, ClassNotFoundException {
	//    	PreparedStatement pstmt = null;
	//        try {
	//	    	if (con == null) {
	//	            getConnection();
	//	        }
	//	        else if (con.isClosed()) {
	//	            getConnection(); 
	//	        }
	//	        
	//	    	String sql = "";
	//			sql += "INSERT INTO APP.EVENT(CASEID, ACTIVITY, START, COMPLETE, RESOURCE, STAGE, TYPE)";
	//			sql += "VALUES(?,?,?,?,?,?,?)";
	//	    	pstmt = con.prepareStatement(sql);
	//	        pstmt.setString(1, caseID);
	//	        pstmt.setString(2, activity);
	//	        pstmt.setString(3, start);
	//	        pstmt.setString(4, complete);
	//	        pstmt.setString(5, resource);
	//	        pstmt.setString(6, stage);
	//	        pstmt.setString(7, type);
	//	        
	//	        pstmt.executeUpdate();
	//        }
	//    	finally {
	//    		try {
	//    			if (pstmt != null) pstmt.close();
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//    	}
	//    }

	public static void insertStage(String caseID, String stage, Long queuestart, Long servicestart,
			Long servicecomplete, String status, String lastStage) throws ClassNotFoundException, SQLException {
		//removed input param Long resWorkTime
		PreparedStatement pstmt = null;
		try {
			if (con == null) {
				getConnection();
			} else if (con.isClosed()) {
				getConnection();
			}

			String sql = "";
			sql += "INSERT INTO APP.STAGE(CASEID, STAGE, QUEUESTART, SERVICESTART, SERVICECOMPLETE, STATUS, LASTSTAGE)";
			sql += "VALUES(?,?,?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, caseID);
			pstmt.setString(2, stage);
			pstmt.setLong(3, queuestart);
			pstmt.setLong(4, servicestart);
			pstmt.setLong(5, servicecomplete);
			pstmt.setString(6, status);
			pstmt.setString(7, lastStage);
			//			pstmt.setLong(8, resWorkTime);

			pstmt.executeUpdate();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static void insertActivity(String caseID, String stage, String res, Long start, Long complete)
			throws ClassNotFoundException, SQLException {
		//removed input param Long resWorkTime
		PreparedStatement pstmt = null;
		try {
			if (con == null) {
				getConnection();
			} else if (con.isClosed()) {
				getConnection();
			}

			String sql = "";
			sql += "INSERT INTO APP.ACTIVITY(CASEID, STAGE, RES, START, COMPLETE)";
			sql += "VALUES(?,?,?,?,?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, caseID);
			pstmt.setString(2, stage);
			pstmt.setString(3, res);
			pstmt.setLong(4, start);
			pstmt.setLong(5, complete);
			//			pstmt.setLong(8, resWorkTime);

			pstmt.executeUpdate();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public static int selectCumulativeCount(String stageName, DateTime timePoint, int cumulativeType, String exitType,
			boolean isLastStage) throws ClassNotFoundException, SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;
		try {
			if (con == null) {
				getConnection();
			} else if (con.isClosed()) {
				getConnection();
			}

			String sql = "SELECT count(*) as total FROM APP.STAGE ";
			if (cumulativeType == ConnectionManager.CUM_TYPE_ARRIVAL) {
				sql += "WHERE stage = ? AND servicestart<= ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, stageName);
				pstmt.setLong(2, timePoint.getMillis());
			} else if (cumulativeType == ConnectionManager.CUM_TYPE_DEPARTURE) {
				sql += "WHERE stage = ? AND servicecomplete<= ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, stageName);
				pstmt.setLong(2, timePoint.getMillis());
			} else if (cumulativeType == ConnectionManager.CUM_TYPE_EXIT) {
				sql += "WHERE stage = ? AND laststage = ? AND servicecomplete<= ? AND STATUS = '" + exitType + "'";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, stageName);
				pstmt.setString(2, stageName);
				pstmt.setLong(3, timePoint.getMillis());
			}
			//		else if (cumulativeType == ConnectionManager.CUM_TYPE_PASSED) {
			//			if (!isLastStage) {
			//				sql += "WHERE stage = ? AND servicecomplete<= ? AND laststage <> ?";
			//			} else {
			//				sql += "WHERE stage = ? AND servicecomplete<= ? AND laststage = ? AND status='completed'";
			//			}
			//			pstmt = con.prepareStatement(sql);
			//			pstmt.setString(1, stageName);
			//			pstmt.setLong(2, timePoint.getMillis());
			//			pstmt.setString(3, stageName);
			//							
			//		}

			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt("total");
			}
			return result;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param stageName
	 * @param timePoint
	 * @return number of cumulative seconds for all cases within this stage
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	//  public static long selectCumulativeCaseTime(String stageName, DateTime timePoint) throws ClassNotFoundException, SQLException {
	//	PreparedStatement pstmt=null;
	//	ResultSet rs = null;
	//	long result = 0;
	//	try {
	//    	if (con == null) {
	//			getConnection();
	//		}
	//		else if (con.isClosed()) {
	//			getConnection(); 
	//		}    
	//		
	//    	// select MIN(timePoint,servicestop) - servicestart
	//		String sql = "SELECT SUM(((" + timePoint.getMillis() + " + servicecomplete) - ABS(" + 
	//								+ timePoint.getMillis() + " - servicecomplete))/2 - servicestart)/1000 as total FROM APP.STAGE ";
	//		sql += "WHERE stage = ? AND servicestart<= ?";
	//		pstmt = con.prepareStatement(sql);
	//		pstmt.setString(1, stageName);
	//		pstmt.setLong(2, timePoint.getMillis());				
	//		
	//		rs = pstmt.executeQuery();
	//		if (rs.next()) {
	//			result = rs.getLong("total");
	//		} 
	//		return result;
	//	}
	//	finally {
	//		try {
	//			if (rs != null) rs.close();
	//			if (pstmt != null) pstmt.close();
	//		} catch (SQLException e) {
	//			e.printStackTrace();
	//		}
	//	}
	//  }    

	/**
	 * @param stageName
	 * @param timePoint
	 * @return number of cumulative seconds for all res within this stage
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	//  public static long selectCumulativeResTime(String stageName, DateTime timePoint) throws ClassNotFoundException, SQLException {
	//		PreparedStatement pstmt=null;
	//		ResultSet rs = null;
	//		long result = 0;
	//		try {
	//	    	if (con == null) {
	//				getConnection();
	//			}
	//			else if (con.isClosed()) {
	//				getConnection(); 
	//			}    
	//			
	//	    	// select MIN(timePoint,stop) - start
	//			String sql = "SELECT SUM(((" + timePoint.getMillis() + " + complete) - ABS(" + 
	//									+ timePoint.getMillis() + " - complete))/2 - start)/1000 as total FROM APP.ACTIVITY ";
	//			sql += "WHERE stage = ? AND start<= ?";
	//			pstmt = con.prepareStatement(sql);
	//			pstmt.setString(1, stageName);
	//			pstmt.setLong(2, timePoint.getMillis());				
	//			
	//			rs = pstmt.executeQuery();
	//			if (rs.next()) {
	//				result = rs.getLong("total");
	//			} 
	//			return result;
	//		}
	//		finally {
	//			try {
	//				if (rs != null) rs.close();
	//				if (pstmt != null) pstmt.close();
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//  }     

	public static int selectCumulativeArrivalCount(String stageName, DateTime timePoint) {
		int totalCount = 0;
		long timePointMillis = timePoint.getMillis();

		// Must check since the SPF filter may return no data for certain stages.
		if (stageServiceIntervalMap.containsKey(stageName)) {
			for (Interval interval : stageServiceIntervalMap.get(stageName)) {
				if (interval.getStartMillis() <= timePointMillis) {
					totalCount++;
				}

			}
		}
		return totalCount;
	}

	public static int selectCumulativeDepartureCount(String stageName, DateTime timePoint) {
		int totalCount = 0;
		long timePointMillis = timePoint.getMillis();

		// Must check since the SPF filter may return no data for certain stages.
		if (stageServiceIntervalMap.containsKey(stageName)) {
			for (Interval interval : stageServiceIntervalMap.get(stageName)) {
				if (interval.getEndMillis() <= timePointMillis) {
					totalCount++;
				}

			}
		}
		return totalCount;
	}

	public static long selectCumulativeCaseTime2(String stageName, DateTime timePoint) {
		long totalTime = 0;
		long timePointMillis = timePoint.getMillis();

		// Must check since the SPF filter may return no data for certain stages.
		if (stageTotalIntervalMap.containsKey(stageName)) {
			for (Interval interval : stageTotalIntervalMap.get(stageName)) {
				if (interval.getStartMillis() < timePointMillis) {
					totalTime += (Math.min(timePointMillis, interval.getEndMillis()) - interval.getStartMillis()) / 1000;
				}
			}
		}
		return totalTime;
	}

	public static long selectCumulativeResTime2(String stageName, DateTime timePoint) {
		long totalTime = 0;
		long timePointMillis = timePoint.getMillis();

		// Must check since the SPF filter may return no data for certain stages.
		if (stageActivityIntervalMap.containsKey(stageName)) {
			for (Interval interval : stageActivityIntervalMap.get(stageName)) {
				if (interval.getStartMillis() < timePointMillis) {
					totalTime += (Math.min(timePointMillis, interval.getEndMillis()) - interval.getStartMillis()) / 1000;
				}
			}
		}
		return totalTime;
	}

	/**
	 * Select the total stage time and resource work time in the stage until a
	 * given time point
	 * 
	 * @param stageName
	 * @param timePoint
	 * @return: array of two elements, 1st element is the stage time, 2nd is the
	 *          work time, in seconds
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	//  public static Long[] selectStageAndWorkTime(String stageName, DateTime timePoint) throws ClassNotFoundException, SQLException {
	//		PreparedStatement pstmt=null;
	//		ResultSet rs = null;
	//		Long[] result = new Long[2];
	//		try {
	//	    	if (con == null) {
	//				getConnection();
	//			}
	//			else if (con.isClosed()) {
	//				getConnection(); 
	//			}    
	//			
	//			String sql = "SELECT sum(SERVICECOMPLETE-QUEUESTART) as TotalStageTime, sum(WORKTIME) as TotalWorkTime FROM APP.STAGE ";
	//			sql += "WHERE stage = ? AND servicecomplete <= ?";
	//			pstmt = con.prepareStatement(sql);
	//			pstmt.setString(1, stageName);
	//			pstmt.setLong(2, timePoint.getMillis());	
	//			rs = pstmt.executeQuery();
	//			while (rs.next()) {
	//				result[0] = (rs.getLong("TotalStageTime")/1000);
	//				result[1] = rs.getLong("TotalWorkTime");
	//			}
	//			return result;
	//		}
	//		finally {
	//			try {
	//				if (rs != null) rs.close();
	//				if (pstmt != null) pstmt.close();
	//			} catch (SQLException e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	  }

}
