package org.apromore.data_access.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.commons.Constants;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.model_manager.ProcessSummariesType;
import org.apromore.data_access.model_manager.ProcessSummaryType;
import org.apromore.data_access.model_manager.VersionSummaryType;


public class ProcessDao extends BasicDao {

	public ProcessDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private static ProcessDao instance;

	public static ProcessDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new ProcessDao();
			} catch (Exception e) {
				throw new ExceptionDao(
						"Error: not able to get instance for DAO: " + e.getMessage() + "\n");
			}
		}
		return instance;
	}

	/**
	 * Get from the database summaries of all processes (IncomingMessages details and IncomingMessages version
	 * details)
	 * @return ProcessSummariesType
	 * @throws Exception
	 */
	public ProcessSummariesType getProcessSummaries() throws Exception {

		ProcessSummariesType processSummaries = new ProcessSummariesType();
		Connection conn = null;
		Statement stmtP = null;
		ResultSet rsP = null;
		String requeteP = null;
		Statement stmtV = null;
		ResultSet rsV = null;
		String requeteV = null;

		try {
			conn = this.getConnection();
			stmtP = conn.createStatement();

			requeteP = "SELECT " + ConstantDB.ATTR_PROCESSID + ", P."+ ConstantDB.ATTR_NAME + ", "
			+            "P." + ConstantDB.ATTR_DOMAIN + ", P." + ConstantDB.ATTR_ORIGINAL_TYPE 
			+           ", R." + ConstantDB.ATTR_RANKING
			+          ", V." + ConstantDB.ATTR_VERSION_NAME
			+          ", P." + ConstantDB.ATTR_USERNAME
			+ " FROM " + ConstantDB.TABLE_PROCESSES + " natural join " + ConstantDB.VIEW_PROCESS_RANKING + " R "
			+ "      join " + ConstantDB.TABLE_VERSIONS + " V using(" + ConstantDB.ATTR_PROCESSID + ") "
			+ " where (" + ConstantDB.ATTR_PROCESSID + ", V." + ConstantDB.ATTR_CREATION_DATE + ")"
			+ "in (select " + ConstantDB.ATTR_PROCESSID + " , max(" + ConstantDB.ATTR_CREATION_DATE + ") "
			+ "        from " + ConstantDB.TABLE_VERSIONS
			+ "        group by " + ConstantDB.ATTR_PROCESSID + ") "
			+ " order by " + ConstantDB.ATTR_PROCESSID ;

			rsP = stmtP.executeQuery(requeteP);
			while (rsP.next()) {
				int processId = rsP.getInt(1);
				ProcessSummaryType processSummary = new ProcessSummaryType();
				processSummaries.getProcessSummary().add(processSummary);
				processSummary.setId(processId);
				processSummary.setName(rsP.getString(2));
				processSummary.setDomain(rsP.getString(3));
				processSummary.setOriginalNativeType(rsP.getString(4));
				processSummary.setRanking(rsP.getInt(5));
				processSummary.setLastVersion(rsP.getString(6));
				processSummary.setOwner(rsP.getString(7));

				stmtV = conn.createStatement();
				requeteV = " select " + ConstantDB.ATTR_VERSION_NAME + ", "
				+ ConstantDB.ATTR_CREATION_DATE + ",  "
				+ ConstantDB.ATTR_LAST_UPDATE + ",  "
				+ ConstantDB.ATTR_RANKING + " "
				+ " from " + ConstantDB.TABLE_VERSIONS 
				+ " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId 
				+ " order by  " + ConstantDB.ATTR_CREATION_DATE ;
				rsV = stmtV.executeQuery(requeteV);
				while (rsV.next()){
					VersionSummaryType version = new VersionSummaryType();
					version.setName(rsV.getString(1));
					version.setCreationDate(rsV.getTimestamp(2));
					version.setLastUpdate(rsV.getTimestamp(3));
					version.setRanking(rsV.getInt(4));
					processSummary.getVersionSummaries().add(version);
				}
				rsV.close(); stmtV.close();	
			} 
		}
		catch (SQLException e) {
			throw new Exception("Error: ProcessDao " + e.getMessage() + "\n");
		}
		finally {
			Release(conn, stmtP, rsP);
		}
		return processSummaries;
	}

	/**
	 * returns IncomingMessages summaries which match at one of the keywords 
	 * @param String of the form "a , b ; c ; (d,n)"
	 * @return ProcessSummariesType
	 */
	public ProcessSummariesType getProcessSummaries(String keywordssearch) throws Exception {

		/**
		 * built the search condition 
		 */

		String condition = "";
		if (keywordssearch != null && keywordssearch.compareTo("") != 0) {
			// map search expression into a list of terms
			// example: (yawl;protos),invoicing => [(,yawl,or,protos,),and,invoicing]
			Vector<String> expression = mapQuery(keywordssearch) ;

			condition = "and ( ";
			for (int i = 0; i<expression.size();i++){
				String current = expression.elementAt(i);
				if (current.compareTo(" and ")==0 || current.compareTo(" or ")==0 ||
						current.compareTo(" ) ")==0 || current.compareTo(" ( ")==0) {
					condition += current ;
				} else {
					condition +=  ConstantDB.ATTR_PROCESSID + " in (select " + ConstantDB.ATTR_PROCESSID 
					+    " from " + ConstantDB.VIEW_KEYWORDS 
					+    " where " + ConstantDB.ATTR_WORD + " like '%" + current + "%' )";
				}
			}
			condition += " )";
		} 

		// if keywordssearch empty thus condition = ""

		ProcessSummariesType processSummaries = new ProcessSummariesType();
		Connection conn = null;
		Statement stmtP = null;
		ResultSet rsP = null;
		String requeteP = null;
		Statement stmtV = null;
		ResultSet rsV = null;
		String requeteV = null;

		try {
			conn = this.getConnection();
			stmtP = conn.createStatement();

			requeteP = "SELECT " + ConstantDB.ATTR_PROCESSID + "," 
			+             ConstantDB.ATTR_NAME + ", "
			+             ConstantDB.ATTR_DOMAIN + "," 
			+             ConstantDB.ATTR_ORIGINAL_TYPE + ","
			+     " R." + ConstantDB.ATTR_RANKING  + ","
			+             ConstantDB.ATTR_VERSION_NAME + ","
			+             ConstantDB.ATTR_OWNER
			+     " FROM " + ConstantDB.TABLE_PROCESSES + " P "
			+	"    join " + ConstantDB.TABLE_VERSIONS + " V using(" + ConstantDB.ATTR_PROCESSID + ") "
			+       "  join " + ConstantDB.VIEW_PROCESS_RANKING + " R using (" + ConstantDB.ATTR_PROCESSID + ")" 
			+   " where "
			+          "  (" + ConstantDB.ATTR_PROCESSID + ", V." + ConstantDB.ATTR_CREATION_DATE + ")"
			+				 "in (select " + ConstantDB.ATTR_PROCESSID + " , max(" + ConstantDB.ATTR_CREATION_DATE + ") "
			+			 "        from " + ConstantDB.TABLE_VERSIONS
			+			 "        group by " + ConstantDB.ATTR_PROCESSID + ") "
			+ condition 
			+ " order by " + ConstantDB.ATTR_PROCESSID ;

			rsP = stmtP.executeQuery(requeteP);
			while (rsP.next()) {
				int processId = rsP.getInt(1);
				ProcessSummaryType processSummary = new ProcessSummaryType();
				processSummaries.getProcessSummary().add(processSummary);
				processSummary.setId(processId);
				processSummary.setName(rsP.getString(2));
				processSummary.setDomain(rsP.getString(3));
				processSummary.setOriginalNativeType(rsP.getString(4));
				processSummary.setRanking(rsP.getInt(5));
				processSummary.setLastVersion(rsP.getString(6));
				processSummary.setOwner(rsP.getString(7));

				stmtV = conn.createStatement();
				requeteV = " select " + ConstantDB.ATTR_VERSION_NAME + ", "
				+ ConstantDB.ATTR_CREATION_DATE + ",  "
				+ ConstantDB.ATTR_LAST_UPDATE + ",  "
				+ ConstantDB.ATTR_RANKING + " "
				+ " from " + ConstantDB.TABLE_VERSIONS 
				+ " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId 
				+ " order by  " + ConstantDB.ATTR_CREATION_DATE ;

				rsV = stmtV.executeQuery(requeteV);
				while (rsV.next()){
					VersionSummaryType version = new VersionSummaryType();
					version.setName(rsV.getString(1));
					version.setCreationDate(rsV.getTimestamp(2));
					version.setLastUpdate(rsV.getTimestamp(3));
					version.setRanking(rsV.getInt(4));
					processSummary.getVersionSummaries().add(version);
				}
				rsV.close(); stmtV.close();	
			} 
		}
		catch (SQLException e) {
			throw new Exception("Error: ProcessDao " + e.getMessage() + "\n");
		}
		finally {
			Release(conn, stmtP, rsP);
		}
		return processSummaries;
	}

	/**
	 * Interpretation of the query received by customer
	 * "," => and
	 * ";" => or
	 * "(" and ")" remain
	 * each term of the query is an element in the result
	 * a,b;(d,e) => [a, and, b, or, (, a, and, e, )]
	 * @param  String keywordssearch 
	 * @return Vector<String> : the SQL condition corresponding to keywordssearch
	 * @throws UnsupportedEncodingException 
	 */
	private Vector<String> mapQuery(String keywordssearch) throws UnsupportedEncodingException {
		Vector<String> res = new Vector<String>();
		String term = "" ;
		int state = 1;	// initial state in the recognition automaton
		String currentChar = "" ;
		for (int i=0; i<keywordssearch.length();i++) {
			currentChar = keywordssearch.substring(i, i+1);
			if (state==1) {
				if (currentChar.compareTo(",")==0) {
					// and
					res.add(" and ");
				} else {
					if (currentChar.compareTo(";")==0) {
						// or
						res.add(" or ");
					} else {
						if (currentChar.compareTo(")") ==0) {
							res.add(" ) ");
						} else {
							if (currentChar.compareTo("(") ==0) {
								res.add(" ( ");
							} else {
								if (currentChar.compareTo(" ") != 0) {
									// not an operator, not a space
									term = currentChar;
									state = 2;
								}	}	}	} }
			} else {
				if (state ==2) {
					if (currentChar.compareTo(",")==0) {
						// and
						res.add(term);
						res.add(" and ");
						state = 1;
					} else {
						if (currentChar.compareTo(";")==0) {
							// or
							res.add(term);
							res.add(" or ");
							state = 1;
						} else {
							if (currentChar.compareTo(")") ==0) {
								res.add(term);
								res.add(" ) ");
								state = 1;
							} else {
								if (currentChar.compareTo("(") ==0) {
									res.add(term);
									res.add(" ( ");
									state = 1;
								} else {
									if (currentChar.compareTo(" ") != 0) {
										// not an operator, not a space
										term += currentChar;
									}	else {
										state = 3;
									}	}	} } }
				} else {
					// state = 3
					if (currentChar.compareTo(",")==0) {
						// and
						res.add(term);
						res.add(" and ");
						state = 1;
					} else {
						if (currentChar.compareTo(";")==0) {
							// or
							res.add(term);
							res.add(" or ");
							state = 1;
						} else {
							if (currentChar.compareTo(")") ==0) {
								res.add(term);
								res.add(" ) ");
								state = 1;
							} else {
								if (currentChar.compareTo("(") ==0) {
									res.add(term);
									res.add(" ( ");
									state = 1;
								} else {
									if (currentChar.compareTo(" ") != 0) {
										// not an operator, not a space
										term += " " + currentChar;
										state = 2;
									}	}	} }
					}
				}
			}
		}
		if (state == 2 || state == 3) res.add(term);
		return res;
	}

	/**
	 * Store in the database the model with name processName and version,  
	 * whose native description is process_xml in format nativeType, 
	 * cpf is cpf_xml, anf is anf_xml. This process is owned by the user username, 
	 * it belongs to domain.
	 * @param username
	 * @param processName
	 * @param domain
	 * @param nativeType
	 * @param version
	 * @param process_xml
	 * @param cpf_xml
	 * @param anf_xml
	 * @return
	 * @throws ExceptionDao
	 * @throws SQLException
	 * @throws IOException
	 */
	public org.apromore.data_access.model_canoniser.ProcessSummaryType storeNativeCpf 
	(String username, String processName, String domain, 
			String nativeType, String version, InputStream process_xml,
			InputStream cpf_xml, InputStream anf_xml) throws ExceptionDao, SQLException, IOException {

		Connection conn = null;
		Statement stmt0 = null;
		PreparedStatement
		stmtp = null;
		ResultSet rs0 = null;
		org.apromore.data_access.model_canoniser.ProcessSummaryType process = 
			new org.apromore.data_access.model_canoniser.ProcessSummaryType();
		org.apromore.data_access.model_canoniser.VersionSummaryType first_version =
			new org.apromore.data_access.model_canoniser.VersionSummaryType();
		process.getVersionSummaries().clear();
		process.getVersionSummaries().add(first_version);
		try {
			if (version == null || version.compareTo("")==0) {
				version = Constants.DEFAULT_VERSION_NAME;
			}
			StringBuilder sb0 = new StringBuilder();
			String line ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process_xml, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb0.append(line).append("\n");
				}
			} finally {
				process_xml.close();
			}
			String process_string = sb0.toString();
			System.out.println("native size: " + process_string.length());
			StringBuilder sb1 = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(cpf_xml, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb1.append(line).append("\n");
				}
			} finally {
				cpf_xml.close();
			}
			String cpf_string = sb1.toString();
			System.out.println("cpf size: " + cpf_string.length());

			StringBuilder sb2 = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(anf_xml, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb2.append(line).append("\n");
				}
			} finally {
				anf_xml.close();
			}

			String anf_string = sb2.toString();
			System.out.println("anf size: " + anf_string.length());
			conn = this.getConnection();


			String query3 = " insert into " + ConstantDB.TABLE_CANONICALS
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";

			stmtp = conn.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS);
			//stmt3.setAsciiStream(1, cpf_xml_is);
			stmtp.setString(1, cpf_string);

			int rs3 = stmtp.executeUpdate();
			ResultSet keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int cpfId = keys.getInt(1);
			stmtp.close();
			keys.close();
			String query5 = " insert into " + ConstantDB.TABLE_ANNOTATIONS
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";
			stmtp = conn.prepareStatement(query5, Statement.RETURN_GENERATED_KEYS);

			//stmt5.setAsciiStream(1, anf_xml_is);
			stmtp.setString (1, anf_string);
			Integer rs5 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int anfId = keys.getInt(1);
			keys.close();
			stmtp.close();

			String query6 = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?,?,?) ";
			stmtp = conn.prepareStatement(query6, Statement.RETURN_GENERATED_KEYS);
			stmtp.setString(1, process_string);
			stmtp.setString(2, nativeType);
			stmtp.setInt(3, cpfId);
			Integer rs6 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int natId = keys.getInt(1);
			keys.close();
			stmtp.close();

			query6 = " insert into " + ConstantDB.TABLE_ANFOFCPF
			+ " values (?,?)";
			stmtp = conn.prepareStatement(query6);
			stmtp.setInt(1,cpfId);
			stmtp.setInt(2,anfId);
			stmtp.executeUpdate();
			stmtp.close();
			
			query6 = " insert into " + ConstantDB.TABLE_PROCESSES
			+ "(" + ConstantDB.ATTR_NAME + ","
			+       ConstantDB.ATTR_DOMAIN + ","
			+		ConstantDB.ATTR_OWNER + ","
			+		ConstantDB.ATTR_ORIGINAL_TYPE + ")"
			+ " values (?, ?, ?, ?) ";
			stmtp = conn.prepareStatement(query6, Statement.RETURN_GENERATED_KEYS);
			stmtp.setString(1, processName);
			stmtp.setString(2, domain);
			stmtp.setString(3, username);
			stmtp.setString(4,nativeType);
			int rs1 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int processId = keys.getInt(1);
			keys.close();

			String query2 = " select now() ";
			stmt0 = conn.createStatement();
			rs0 = stmt0.executeQuery(query2);
			if (!rs0.next()) {
				throw new ExceptionDao ("Error: cannot retrieve date of the day.");
			}
			Timestamp now = rs0.getTimestamp(1);

			query2 = " insert into " + ConstantDB.TABLE_VERSIONS
			+ "(" + ConstantDB.ATTR_PROCESSID + ","
			+     ConstantDB.ATTR_VERSION_NAME + ","
			+     ConstantDB.ATTR_CREATION_DATE + ","
			+     ConstantDB.ATTR_LAST_UPDATE + ","
			+     ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?, ?, ?, ?, ?) ";
			stmtp = conn.prepareStatement(query2);
			stmtp.setInt(1, processId);
			stmtp.setString(2, version);
			stmtp.setTimestamp(3,now);
			stmtp.setTimestamp(4,now);
			stmtp.setInt(5, cpfId);
			Integer rs2 = stmtp.executeUpdate();


			process.setDomain(domain);
			process.setId(processId);
			process.setLastVersion(version);
			process.setName(processName);
			process.setOriginalNativeType(nativeType);
			process.setRanking(0);
			process.setOwner(username);
			first_version.setName(version);
			first_version.setCreationDate(now);
			first_version.setLastUpdate(now);
			first_version.setRanking(0);

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error ProcessDAO (storeNative): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error ProcessDAO (storeNative): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt0, rs0);
			return process;
		}
	}

	public String getNative(Integer processId, String version, String nativeType) throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			query = " select " + ConstantDB.ATTR_CONTENT
			+ " from " + ConstantDB.TABLE_NATIVES
			+ " natural join "
			+            ConstantDB.TABLE_VERSIONS
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'"
			+   " and " + ConstantDB.ATTR_NAT_TYPE  + " = '" + nativeType + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				throw new ExceptionDao ("ProcessDAO (getNative): couldn't access to native format.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("ProcessDAO (getNative): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("ProcessDAO (getNative): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}
	}


	/**
	 * Retrieve annotations (if exist) produced
	 * @param processId
	 * @param version
	 * @return
	 * @throws ExceptionDao
	 */
	public String getAnnotation(Integer processId, String version) throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			query = " select " + ConstantDB.ATTR_CONTENT
			+ " from " + ConstantDB.TABLE_VERSIONS
			+ " left outer join " + ConstantDB.TABLE_ANFOFCPF
			+           " on (" + ConstantDB.ATTR_CPF + " = " + ConstantDB.ATTR_CANONICAL + ")"
			+ " left outer join " + ConstantDB.TABLE_ANNOTATIONS
			+         " on (" + ConstantDB.ATTR_ANF + " = " + ConstantDB.ATTR_URI + ")"
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				return "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("SQL error ProcessDAO (getAnnotation): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error ProcessDAO (getAnnotation): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}

	}

	public String getCanonical (Integer processId, String version) throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			query = " select " + ConstantDB.ATTR_CONTENT
			+ " from " + ConstantDB.TABLE_CANONICALS
			+ " join "
			+            ConstantDB.TABLE_VERSIONS
			+ " on (" + ConstantDB.ATTR_CANONICAL + "=" + ConstantDB.ATTR_URI + ")"
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				throw new ExceptionDao ("SQL error ProcessDAO (getCanonical): canonical not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("SQL error ProcessDAO (getCanonical): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error ProcessDAO (getCanonical): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}
	}

	public void storeNative (String nativeType, int processId, String version,
			InputStream native_xml) throws SQLException, ExceptionDao {
		Connection conn = null;
		Statement stmt1 = null;
		PreparedStatement stmt2 = null;
		ResultSet rs1 = null;
		int canonical;

		try {
			conn = this.getConnection();
			stmt1 = conn.createStatement();
			String query = " select " + ConstantDB.ATTR_CANONICAL
			+ " from " + ConstantDB.TABLE_VERSIONS
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
			rs1 = stmt1.executeQuery(query);
			if (rs1.next()) {
				canonical = rs1.getInt(1);
			} else {
				throw new ExceptionDao ("SQL error ProcessDAO (storeNative): canonical process not found.");
			}

			if (version == null || version.compareTo("")==0) {
				version = Constants.DEFAULT_VERSION_NAME;
			}
			StringBuilder sb = new StringBuilder();
			String line ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(native_xml, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				native_xml.close();
			}
			String native_string = sb.toString();
			query = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?,?,?) ";

			stmt2 = conn.prepareStatement(query);
			stmt2.setString(1, native_string);
			stmt2.setString(2, nativeType);
			stmt2.setInt(3, canonical);
			stmt2.executeUpdate();
			conn.commit();

		} catch (SQLException e) {
			conn.rollback();
			e.printStackTrace();
			throw new ExceptionDao ("SQL error ProcessDAO (storeNative): " + e.getMessage() + "\n");
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
			throw new ExceptionDao ("Error ProcessDAO (storeNative): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt1, rs1);
			stmt2.close();
		}

	}

	public void storeVersion(int processId, String preVersion,
			String newVersion, String username, String nativeType,
			String domain, InputStream native_is, InputStream cpf_is,
			InputStream anf_is) throws ExceptionDao, SQLException {
		Connection conn = null;
		Statement stmt0 = null;
		PreparedStatement stmtp = null;
		ResultSet rs0 = null;
		try {

			StringBuilder sb0 = new StringBuilder();
			String line ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(native_is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb0.append(line).append("\n");
				}
			} finally {
				native_is.close();
			}
			String process_string = sb0.toString();

			StringBuilder sb1 = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(cpf_is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb1.append(line).append("\n");
				}
			} finally {
				cpf_is.close();
			}
			String cpf_string = sb1.toString();

			StringBuilder sb2 = new StringBuilder();
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(anf_is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb2.append(line).append("\n");
				}
			} finally {
				anf_is.close();
			}

			String anf_string = sb2.toString();

			conn = this.getConnection();


			String query3 = " insert into " + ConstantDB.TABLE_CANONICALS
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";

			stmtp = conn.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS);
			stmtp.setString(1, cpf_string);

			int rs3 = stmtp.executeUpdate();
			ResultSet keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int cpfId = keys.getInt(1);
			keys.close(); stmtp.close();
			String query5 = " insert into " + ConstantDB.TABLE_ANNOTATIONS
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";
			stmtp = conn.prepareStatement(query5, Statement.RETURN_GENERATED_KEYS);

			stmtp.setString (1, anf_string);
			Integer rs5 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int anfId = keys.getInt(1);
			keys.close(); stmtp.close();

			String query6 = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?,?,?) ";
			stmtp = conn.prepareStatement(query6, Statement.RETURN_GENERATED_KEYS);
			//stmt6.setAsciiStream(1, process_xml);
			stmtp.setString(1, process_string);
			stmtp.setString(2, nativeType);
			stmtp.setInt(3, cpfId);
			Integer rs6 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int natId = keys.getInt(1);
			keys.close(); stmtp.close();

			query6 = " insert into " + ConstantDB.TABLE_ANFOFCPF
			+ "values (?,?)";
			stmtp.setInt(1, cpfId);
			stmtp.setInt(2,anfId);
			rs6 = stmtp.executeUpdate();
			stmtp.close();
			
			// add a new version of the process identified by processId + newVersion
			String query2 = " insert into " + ConstantDB.TABLE_VERSIONS
			+ "(" + ConstantDB.ATTR_PROCESSID + ","
			+     ConstantDB.ATTR_VERSION_NAME + ","
			+     ConstantDB.ATTR_CREATION_DATE + ","
			+     ConstantDB.ATTR_LAST_UPDATE + ","
			+     ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?, ?, now(), now(), ?) ";
			stmtp = conn.prepareStatement(query2);
			stmtp.setInt(1, processId);
			stmtp.setString(2, newVersion);
			stmtp.setInt(3, cpfId);
			Integer rs2 = stmtp.executeUpdate();

			// newVersion is derived from preVersion
			query2 = " insert into " + ConstantDB.TABLE_DERIVED_VERSIONS
			+ "(" + ConstantDB.ATTR_PROCESSID + ","
			+     ConstantDB.ATTR_VERSION + ","
			+     ConstantDB.ATTR_DERIVED_VERSION + ")"
			+ " values (?,?,?)";

			stmtp = conn.prepareStatement(query2);
			stmtp.setInt(1, processId);
			stmtp.setString(2, newVersion);
			stmtp.setString(3, preVersion);

			rs2 = stmtp.executeUpdate();
			stmtp.close();
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error ProcessDAO (storeVersion): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error ProcessDAO (storeVersion): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt0, rs0);
		}
	}

	/**
	 * For each given process, delete each of its given versions from the database. 
	 * @param processes
	 * @throws ExceptionDao 
	 * @throws SQLException 
	 */
	public void deleteProcessVersions (HashMap<Integer, List<String>> processes) throws ExceptionDao, SQLException {
		Connection conn = null;
		PreparedStatement stmtp = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		
		try {
			conn = this.getConnection();

			Set<Integer> keys = processes.keySet();
			Iterator itP = keys.iterator();
			while (itP.hasNext()) {
				// for each selected process
				Integer pId = (Integer) itP.next();
				List<String> versions = processes.get(pId);
				Iterator itV = versions.iterator();
				while (itV.hasNext()) {
					// for each selected version(s) of the current process
					String v = (String) itV.next();
					// retrieve cpf associated with (pId, v)
					Integer cpf_uri ;
					query = " select " + ConstantDB.ATTR_CANONICAL
					+ " from " + ConstantDB.TABLE_VERSIONS
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString()
					+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + v + "'";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						cpf_uri = rs.getInt(1);
						stmt.close(); rs.close();
					} else {
						stmt.close(); rs.close();
						throw new ExceptionDao ("DeleteProcessVersions: error, version " + v 
								+ "for process " + pId.toString() + " not found. \n");
					}
					
					/* delete process version identified by <p, v>
					 * retrieve r in derived_versions such as r[processId]=p
					 */
					query = " select " + ConstantDB.ATTR_PROCESSID 
					+ " from " + ConstantDB.TABLE_DERIVED_VERSIONS
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					if (!rs.next()) {
						/* r doesn't exist: v is the only version that exists for p,
						 * delete p from processes (thanks to FKs, related tuple in 
						 * process_versions will be deleted too) 
						 */
						query = " delete from " + ConstantDB.TABLE_PROCESSES
						+ " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
						stmtp = conn.prepareStatement(query);
						int r = stmtp.executeUpdate();
						stmtp.close();
						rs.close();
						stmt.close();
					} else {
						/* r exists: at least two versions exist for p. Derivation list
						 * needs to be updated.
						 * Retrieve r1 in derived_versions such as r1[derived_version]=v
						 */
						rs.close();
						stmt.close();
						query = " select " + ConstantDB.ATTR_PROCESSID 
						+ " from " + ConstantDB.TABLE_DERIVED_VERSIONS
						+ " where " + ConstantDB.ATTR_DERIVED_VERSION + " = '" + v + "'"
						+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						if (rs.next()) {
							/* r1 exists:
							 * Retrieve r2 in derived_versions such as r2[version] = v
							 */
							rs.close();
							stmt.close();
							query = " select " +  ConstantDB.ATTR_DERIVED_VERSION 
							+ " from " + ConstantDB.TABLE_DERIVED_VERSIONS
							+ " where " + ConstantDB.ATTR_VERSION + " = '" +  v + "'"
							+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
							stmt = conn.createStatement();
							rs = stmt.executeQuery(query);
							if (rs.next()) {
								// Delete r2
								query = " delete from " + ConstantDB.TABLE_DERIVED_VERSIONS
								+ " where " + ConstantDB.ATTR_VERSION + " = '" +  v + "'"
								+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
								stmtp = conn.prepareStatement(query);
								int r = stmtp.executeUpdate();
								stmtp.close();
								
								// Update r1 
								query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS 
								+ " set " + ConstantDB.ATTR_DERIVED_VERSION + " = '" + rs.getString(1) + "'"
								+ " where " + ConstantDB.ATTR_DERIVED_VERSION + " = '" + v + "'"
								+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
								stmtp = conn.prepareStatement(query);
								r = stmtp.executeUpdate();
								stmtp.close();
								
							} else {
								// Delete r1
								query = " delete from " + ConstantDB.TABLE_DERIVED_VERSIONS
								+ " where " + ConstantDB.ATTR_DERIVED_VERSION + " = '" +  v + "'"
								+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
								stmtp = conn.prepareStatement(query);
								int r = stmtp.executeUpdate();
								stmtp.close();
							}
						}
					}
					// delete the process version
					query = " delete from " + ConstantDB.TABLE_VERSIONS 
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString()
					+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + v + "'";
					stmtp = conn.prepareStatement(query);
					int r = stmtp.executeUpdate();
					stmtp.close();
					
					// delete associated annotation, canonical and native
					query = " delete from " + ConstantDB.TABLE_ANNOTATIONS 
					+ " where " + ConstantDB.ATTR_URI + " in (select " + ConstantDB.ATTR_ANF
					+              " from " + ConstantDB.TABLE_ANFOFCPF 
					+              " where " + ConstantDB.ATTR_CPF + " = " + cpf_uri.toString() + ")";
					stmtp = conn.prepareStatement(query);
					r = stmtp.executeUpdate();
					stmtp.close();
					
					query = " delete from " + ConstantDB.TABLE_NATIVES 
					+ " where " + ConstantDB.ATTR_CANONICAL + " = " + cpf_uri.toString();
					stmtp = conn.prepareStatement(query);
					r = stmtp.executeUpdate();
					stmtp.close();
					
					query = " delete from " + ConstantDB.TABLE_CANONICALS 
					+ " where " + ConstantDB.ATTR_URI + " = " + cpf_uri.toString();
					stmtp = conn.prepareStatement(query);
					r = stmtp.executeUpdate();
					stmtp.close();
					
				}
			}

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error ProcessDAO (deleteProcessVersions): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error ProcessDAO (deleteProcessVersions): " + e.getMessage() + "\n");
		} finally {
			Release(conn, null, null);
		}
	}
}

