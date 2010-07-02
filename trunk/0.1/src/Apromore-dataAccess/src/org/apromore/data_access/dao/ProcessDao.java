package org.apromore.data_access.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.cpf.CanonicalProcessType;
import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.model_manager.ProcessSummariesType;
import org.apromore.data_access.model_manager.ProcessSummaryType;
import org.apromore.data_access.model_manager.VersionSummaryType;
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.Documentation;
import org.wfmc._2008.xpdl2.ModificationDate;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;

import de.epml.TypeEPML;


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
	 * returns process version summaries which match at least one of the keywords 
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
		} 

		// if keywordssearch empty thus condition = ""

		ProcessSummariesType processSummaries = new ProcessSummariesType();
		Connection conn = null;
		Statement stmtP = null;
		ResultSet rsP = null;
		String query = null;
		Statement stmtV = null;
		ResultSet rsV = null;
		String requeteV = null;

		try {
			conn = this.getConnection();
			stmtP = conn.createStatement();

			/* query returns, for each process version which satisfies the search condition, 
			 * the process Id, process name, domain, original type, ranking, owner and
			 * latest version name.
			 */

			query = "SELECT " + ConstantDB.ATTR_PROCESSID + "," 
			+             ConstantDB.ATTR_NAME + ", "
			+             ConstantDB.ATTR_DOMAIN + "," 
			+             ConstantDB.ATTR_ORIGINAL_TYPE + ","
			+     " R." + ConstantDB.ATTR_RANKING  + ","
			+             ConstantDB.ATTR_VERSION + ","
			+             ConstantDB.ATTR_OWNER
			+     " FROM " + ConstantDB.TABLE_PROCESSES + " P "
			+     "    join " + ConstantDB.VIEW_PROCESS_RANKING + " R using (" + ConstantDB.ATTR_PROCESSID + ")" 
			+     "    join " + ConstantDB.VIEW_HEAD_VERSIONS + " H using (" + ConstantDB.ATTR_PROCESSID + ")" ;

			if (condition.compareTo("")!=0) {
				query += " where " + condition;
			} 
			query += " order by " + ConstantDB.ATTR_PROCESSID ;

			rsP = stmtP.executeQuery(query);
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
				//+ "date_format(" + ConstantDB.ATTR_CREATION_DATE + ", '%d/%c/%Y %k:%i:%f')" + ",  "
				//+ "date_format(" + ConstantDB.ATTR_LAST_UPDATE  + ", '%d/%c/%Y %k:%i:%f')" + ",  "
				+ ConstantDB.ATTR_CREATION_DATE + ", "
				+ ConstantDB.ATTR_LAST_UPDATE + ", "
				+ ConstantDB.ATTR_RANKING + ", "
				+ ConstantDB.ATTR_DOCUMENTATION
				+ " from " + ConstantDB.TABLE_VERSIONS 
				+ " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId 
				+ " order by  " + ConstantDB.ATTR_CREATION_DATE ;

				rsV = stmtV.executeQuery(requeteV);
				while (rsV.next()){
					VersionSummaryType version = new VersionSummaryType();
					version.setName(rsV.getString(1));
					version.setCreationDate(rsV.getString(2));
					version.setLastUpdate(rsV.getString(3));
					version.setRanking(rsV.getInt(4));
					version.setDocumentation(rsV.getString(5));
					processSummary.getVersionSummaries().add(version);
				}
				rsV.close(); stmtV.close();	
			} 
		}
		catch (SQLException e) {
			throw new ExceptionDao("Error: ProcessDao " + e.getMessage() + "\n");
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
	public org.apromore.data_access.model_canoniser.ProcessSummaryType 
	storeNativeCpf 
	(String username, String processName, String domain, String documentation,
			String nativeType, String version, String creationDate, String lastUpdate, InputStream process_xml,
			InputStream cpf_xml, InputStream anf_xml) throws SQLException, ExceptionDao {

		Connection conn = null;
		Statement stmt0 = null;
		PreparedStatement stmtp = null;
		ResultSet rs0 = null, keys = null;
		String query = null;
		org.apromore.data_access.model_canoniser.ProcessSummaryType process = 
			new org.apromore.data_access.model_canoniser.ProcessSummaryType();
		org.apromore.data_access.model_canoniser.VersionSummaryType first_version =
			new org.apromore.data_access.model_canoniser.VersionSummaryType();
		process.getVersionSummaries().clear();
		process.getVersionSummaries().add(first_version);
		try {

			conn = this.getConnection();

			// store process details to get processId.
			query = " insert into " + ConstantDB.TABLE_PROCESSES
			+ "(" + ConstantDB.ATTR_NAME + ","
			+       ConstantDB.ATTR_DOMAIN + ","
			+		ConstantDB.ATTR_OWNER + ","
			+		ConstantDB.ATTR_ORIGINAL_TYPE + ")"
			+ " values (?, ?, ?, ?) ";
			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
			keys.close(); stmtp.close();

			// Store informations given as parameters in NPF
			// creationDate and/or lastUpdate, might be empty. 
			if (creationDate.compareTo("")==0 || lastUpdate.compareTo("")==0) {
				query = " select date_format(now(), '%Y-%c-%d %k:%i:%s') ";
				stmt0 = conn.createStatement();
				rs0 = stmt0.executeQuery(query);
				if (!rs0.next()) {
					throw new ExceptionDao ("Error: cannot retrieve date.");
				}
				if (creationDate.compareTo("")==0) {
					creationDate = rs0.getString(1);
					lastUpdate = rs0.getString(1);
				} else {
					lastUpdate = rs0.getString(1);
				}
				stmt0.close();rs0.close();
			}

			// copy parameters values in npf 
			InputStream sync_npf = copyParam2NPF(process_xml, nativeType, processId, processName, version,
					username, creationDate, lastUpdate, documentation);
			StringBuilder sb0 = new StringBuilder();
			String line ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(sync_npf, "UTF-8"));
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



			query = " insert into " + ConstantDB.TABLE_CANONICALS
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";

			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			//stmt3.setAsciiStream(1, cpf_xml_is);
			stmtp.setString(1, cpf_string);

			int rs3 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
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

			query = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?,?,?) ";
			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmtp.setString(1, process_string);
			stmtp.setString(2, nativeType);
			stmtp.setInt(3, cpfId);
			Integer rs6 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error (ProcessDao): cannot retrieve generated key.");
			}
			int natId = keys.getInt(1);
			keys.close();
			stmtp.close();

			query = " insert into " + ConstantDB.TABLE_ANFOFCPF
			+ " values (?,?)";
			stmtp = conn.prepareStatement(query);
			stmtp.setInt(1,cpfId);
			stmtp.setInt(2,anfId);
			stmtp.executeUpdate();
			stmtp.close();

			query = " insert into " + ConstantDB.TABLE_VERSIONS
			+ "(" + ConstantDB.ATTR_PROCESSID + ","
			+     ConstantDB.ATTR_VERSION_NAME + ","
			+     ConstantDB.ATTR_CREATION_DATE + ","
			+     ConstantDB.ATTR_LAST_UPDATE + ","
			+     ConstantDB.ATTR_CANONICAL + ","
			+	  ConstantDB.ATTR_DOCUMENTATION + ")"
			+ " values (?, ?, ?, ?, ?, ?) ";
			//+ " values (?, ?, str_to_date(?,'%Y-%c-%d %k:%i:%s'), str_to_date(?,), ?, ?) ";
			stmtp = conn.prepareStatement(query);
			stmtp.setInt(1, processId);
			stmtp.setString(2, version);
			stmtp.setString(3,creationDate);
			stmtp.setString(4,lastUpdate);
			stmtp.setInt(5, cpfId);
			stmtp.setString(6, documentation);
			Integer rs2 = stmtp.executeUpdate();

			process.setDomain(domain);
			process.setId(processId);
			process.setLastVersion(version);
			process.setName(processName);
			process.setOriginalNativeType(nativeType);
			process.setRanking(0);
			process.setOwner(username);
			first_version.setName(version);
			first_version.setCreationDate(creationDate);
			first_version.setLastUpdate(lastUpdate);
			first_version.setRanking(0);
			first_version.setDocumentation(documentation);			

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error ProcessDAO (storeNativeCpf): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error ProcessDAO (storeNativeCpf): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt0, rs0);
		}
		return process;
	}

	/**
	 * Generate a new npf which is the result of writing parameters in process_xml.
	 * @param process_xml the given npf to be synchronised
	 * @param nativeType npf native type
	 * @param processId id generated by database
	 * @param processName
	 * @param version
	 * @param username
	 * @param creationDate
	 * @param lastUpdate
	 * @return
	 * @throws JAXBException 
	 */
	private InputStream copyParam2NPF(InputStream process_xml,
			String nativeType, Integer processId, String processName,
			String version, String username, String creationDate,
			String lastUpdate, String documentation) throws JAXBException {

		InputStream res = null;
		if (nativeType.compareTo("XPDL 2.1")==0) {
			JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
			PackageType pkg = rootElement.getValue();
			pkg = copyParam2xpdl (pkg, processId, processName, version, username, creationDate, lastUpdate, documentation);

			Marshaller m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
			m.marshal(rootElement, xpdl_xml);
			res = new ByteArrayInputStream(xpdl_xml.toByteArray());

		} else if (nativeType.compareTo("EPML 2.0")==0) {
			JAXBContext jc = JAXBContext.newInstance("de.epml");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
			TypeEPML epml = rootElement.getValue();

			// TODO

			Marshaller m = jc.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
			m.marshal(rootElement, xpdl_xml);
			res = new ByteArrayInputStream(xpdl_xml.toByteArray());

		}
		return res;
	}

	/**
	 * Modify pkg (npf of type xpdl) with parameters values if not null.
	 * @param pkg
	 * @param processId
	 * @param processName
	 * @param version
	 * @param username
	 * @param creationDate
	 * @param lastUpdate
	 * @param documentation
	 * @return
	 */
	private PackageType copyParam2xpdl(PackageType pkg, Integer processId,
			String processName, String version, String username,
			String creationDate, String lastUpdate, String documentation) {

		if (pkg.getRedefinableHeader()==null) {
			RedefinableHeader header = new RedefinableHeader();
			pkg.setRedefinableHeader(header);
			Version v = new Version();
			header.setVersion(v);
			Author a = new Author();
			header.setAuthor(a);
		} else {
			if (pkg.getRedefinableHeader().getVersion()==null) {
				Version v = new Version();
				pkg.getRedefinableHeader().setVersion(v);
			}
			if (pkg.getRedefinableHeader().getAuthor()==null) {
				Author a = new Author();
				pkg.getRedefinableHeader().setAuthor(a);
			}
		}
		if (pkg.getPackageHeader()==null) {
			PackageHeader pkgHeader = new PackageHeader();
			pkg.setPackageHeader(pkgHeader);
			Created created = new Created();
			pkgHeader.setCreated(created);
			ModificationDate modifDate = new ModificationDate();
			pkgHeader.setModificationDate(modifDate);
			Documentation doc = new Documentation();
			pkgHeader.setDocumentation(doc);
		} else {
			if (pkg.getPackageHeader().getCreated()==null) {
				Created created = new Created();
				pkg.getPackageHeader().setCreated(created);
			}
			if (pkg.getPackageHeader().getModificationDate()==null) {
				ModificationDate modifDate = new ModificationDate();
				pkg.getPackageHeader().setModificationDate(modifDate);
			}
			if (pkg.getPackageHeader().getDocumentation()==null) {
				Documentation doc = new Documentation();
				pkg.getPackageHeader().setDocumentation(doc);
			}
		}
		if (processName!=null) pkg.setName(processName);
		if (processId!=null) pkg.setId(processId.toString());
		if (version!=null) pkg.getRedefinableHeader().getVersion().setValue(version);
		if (username!=null) pkg.getRedefinableHeader().getAuthor().setValue(username);
		if (creationDate!=null)	pkg.getPackageHeader().getCreated().setValue(creationDate);
		if (lastUpdate!=null)pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
		if (documentation!=null)pkg.getPackageHeader().getDocumentation().setValue(documentation);
		return pkg;
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
	 * TODO needs to be modified because of annotations
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
		int rs2;
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
			stmt1.close();rs1.close();
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

			query = " select " + ConstantDB.ATTR_URI 
			+ " from " + ConstantDB.TABLE_NATIVES
			+ " natural join " + ConstantDB.TABLE_VERSIONS
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'"
			+   " and " + ConstantDB.ATTR_NAT_TYPE + " = '" + nativeType + "'";
			stmt1 = conn.createStatement();
			rs1 = stmt1.executeQuery(query);
			if (rs1.next()) {
				// if native already present, overwrite it
				query = " update " + ConstantDB.TABLE_NATIVES
				+ " set " + ConstantDB.ATTR_CONTENT + " = ? "
				+ " where " + ConstantDB.ATTR_URI + " = ? ";
				stmt2 = conn.prepareStatement(query);
				stmt2.setString(1, native_string);
				stmt2.setInt(2, rs1.getInt(1));
				rs2 = stmt2.executeUpdate();
			} else {
				// native absent
				query = " insert into " + ConstantDB.TABLE_NATIVES
				+ "(" + ConstantDB.ATTR_CONTENT + ","
				+       ConstantDB.ATTR_NAT_TYPE + ","
				+       ConstantDB.ATTR_CANONICAL + ")"
				+ " values (?,?,?) ";
				stmt2 = conn.prepareStatement(query);
				stmt2.setString(1, native_string);
				stmt2.setString(2, nativeType);
				stmt2.setInt(3, canonical);
				rs2 = stmt2.executeUpdate();
			}
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

	/**
	 * store in the database a new version for processId whose type is nativeType.
	 * Data associated with the version are in the NPF inputstream.
	 * If the version already exists, new data overwrite those currently stored
	 * otherwise, the new version is derived from the head version of processId.
	 * version name, creation date, last update, documentation to be found
	 * in npf.
	 * @param processId
	 * @param nativeType
	 * @param domain
	 * @param npf_is
	 * @param cpf_is
	 * @param anf_is
	 * @throws ExceptionDao
	 * @throws SQLException
	 */
	public void storeVersion(int processId, String nativeType,
			String domain, InputStream npf_is, InputStream cpf_is,
			InputStream anf_is) throws ExceptionDao, SQLException {
		Connection conn = null;
		Statement stmt0 = null, stmt1 = null;
		PreparedStatement stmtp = null;
		ResultSet rs0 = null, rs1 = null ;
		String query = null;
		try {
			String newVersion = null;
			String creationDate = null;
			String lastUpdate = null;
			String documentation = null;
			// read the 4 data above from native_is
			npf_is.mark(0);
			if (nativeType.compareTo("XPDL 2.1")==0) {
				JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
				Unmarshaller u = jc.createUnmarshaller();
				JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(npf_is);
				PackageType pkg = rootElement.getValue();
				try {
					newVersion = pkg.getRedefinableHeader().getVersion().getValue().trim();
					creationDate = pkg.getPackageHeader().getCreated().getValue().trim();
					lastUpdate = pkg.getPackageHeader().getModificationDate().getValue().trim();
					if (pkg.getPackageHeader().getDocumentation()!=null) {
						documentation = pkg.getPackageHeader().getDocumentation().getValue().trim();
					} else {
						documentation = "";
					}
				} catch (NullPointerException e) {
					throw new ExceptionDao ("Missing information in NPF.");
				}

			} else if (nativeType.compareTo("EPML 2.0")==0) {
				JAXBContext jc = JAXBContext.newInstance("de.epml");
				Unmarshaller u = jc.createUnmarshaller();
				JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(npf_is);
				TypeEPML epml = rootElement.getValue();
				// TODO: to be completed with EPML

			} else {
				throw new ExceptionDao("Couldn't read information in NPF.");
			}
			npf_is.reset();
			StringBuilder sb0 = new StringBuilder();
			String line ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(npf_is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb0.append(line).append("\n");
				}
			} finally {
				npf_is.close();
			}
			String npf_string = sb0.toString();

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

			// does newVersion already exist?
			query = " select " + ConstantDB.ATTR_CANONICAL
			+       " from " + ConstantDB.TABLE_VERSIONS
			+       " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+       "   and " + ConstantDB.ATTR_VERSION_NAME + " = '" + newVersion + "'";
			stmt0 = conn.createStatement();
			rs0 = stmt0.executeQuery(query);

			if (rs0.next()) {
				// newVersion already exists. New data overwrite preceding one.
				// update canonicals
				int cpf_id = rs0.getInt(1);
				stmt0.close();rs0.close();
				query = " update " + ConstantDB.TABLE_CANONICALS
				+ " set " + ConstantDB.ATTR_CONTENT + " = ? "
				+ " where " + ConstantDB.ATTR_URI + " = " + cpf_id;
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1, cpf_string);
				stmtp.executeUpdate();
				stmtp.close();

				// update natives
				query = " update " + ConstantDB.TABLE_NATIVES
				+ " set " + ConstantDB.ATTR_CONTENT + " = ? "
				+ " where " + ConstantDB.ATTR_CANONICAL + " = " + cpf_id;
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1, npf_string);
				stmtp.executeUpdate();
				stmtp.close();
				// update annotations
				// TODO will need to be fixed when annotations properly captured: how many tuples
				// will be returned?
				// retrieve anf_id
				query = " select " + ConstantDB.ATTR_ANF
				+       " from " + ConstantDB.TABLE_ANFOFCPF
				+       " where " + ConstantDB.ATTR_CPF + " = " + cpf_id;
				stmt0 = conn.createStatement();
				rs0 = stmt0.executeQuery(query);
				if (!rs0.next()) {
					throw new ExceptionDao ("Error: cannot retrieve annotations.");
				}
				int anf_id = rs0.getInt(1);
				stmt0.close();rs0.close();
				query = " update " + ConstantDB.TABLE_ANNOTATIONS
				+ " set " + ConstantDB.ATTR_CONTENT + " = ? "
				+ " where " + ConstantDB.ATTR_URI + " = " + anf_id;
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1, anf_string);
				stmtp.executeUpdate();
				stmtp.close();
				// update process_versions: modify lastupdate and documentation
				query = " update " + ConstantDB.TABLE_VERSIONS
				+ " set " + ConstantDB.ATTR_DOCUMENTATION + " = ? , "
				+           ConstantDB.ATTR_LAST_UPDATE + " = ? "
				+ " where " + ConstantDB.ATTR_PROCESSID + " = ? "
				+  "  and " + ConstantDB.ATTR_VERSION_NAME + " = ? ";
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1, documentation);
				stmtp.setString(2, lastUpdate);
				stmtp.setInt(3, processId);
				stmtp.setString(4,newVersion);
				stmtp.executeUpdate();
				stmtp.close();				
			} else {
				// newVersion does not exist. Derive a new version.
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
				stmtp.setString(1, npf_string);
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
				+ " values (?,?)";
				stmtp = conn.prepareStatement(query6);
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
				+     ConstantDB.ATTR_CANONICAL + ","
				+     ConstantDB.ATTR_DOCUMENTATION + ")"
				+ " values (?, ?, ?, ?, ?, ?) ";
				//+ " values (?, ?, str_to_date(?,'%Y-%c-%d %k:%i:%s'), str_to_date(?,'%Y-%c-%d %k:%i:%s'), ?, ?) ";
				stmtp = conn.prepareStatement(query2);
				stmtp.setInt(1, processId);
				stmtp.setString(2, newVersion);
				stmtp.setString(3, creationDate);
				stmtp.setString(4, lastUpdate);
				stmtp.setInt(5, cpfId);
				stmtp.setString(6, documentation);
				Integer rs2 = stmtp.executeUpdate();

				// newVersion is derived from head version of processId.
				query2 = " select " + ConstantDB.ATTR_VERSION
				+ " from " + ConstantDB.VIEW_HEAD_VERSIONS
				+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId;
				stmt1 = conn.createStatement();
				rs1 = stmt1.executeQuery(query2);
				if (!rs1.next()){
					throw new ExceptionDao("Couldn't retrieve head version.");
				}

				query2 = " insert into " + ConstantDB.TABLE_DERIVED_VERSIONS
				+ "(" + ConstantDB.ATTR_PROCESSID + ","
				+     ConstantDB.ATTR_VERSION + ","
				+     ConstantDB.ATTR_DERIVED_VERSION + ")"
				+ " values (?,?,?)";

				stmtp = conn.prepareStatement(query2);
				stmtp.setInt(1, processId);
				stmtp.setString(2, rs1.getString(1));
				stmtp.setString(3, newVersion);
				rs2 = stmtp.executeUpdate();
				stmtp.close();
			}

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
			Release(null, stmt1, rs1);
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

	/**
	 * Record in the database the new values for the process meta data detailed in
	 * parameter.
	 * Synchronise the new data with those present in npf descriptions associated
	 * with the process versions.
	 * Sync with cpf not done. To be done? TODO
	 * @param process
	 * @throws SQLException 
	 * @throws ExceptionDao 
	 */
	public void editDataProcesses(Integer processId, String processName, String domain, String username,
			String preVersion, String newVersion, Integer ranking) throws SQLException, ExceptionDao {

		Connection conn = null;
		PreparedStatement stmtp = null;
		String query = null;	
		try {
			conn = this.getConnection();

			// update meta data associated with the process			
			query = " update " + ConstantDB.TABLE_PROCESSES
			+ " set " + ConstantDB.ATTR_NAME + " = ? , "
			+ ConstantDB.ATTR_DOMAIN + " = ? , "
			+ ConstantDB.ATTR_OWNER + " = ?"
			+ " where " + ConstantDB.ATTR_PROCESSID + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, processName);
			stmtp.setString(2, domain);
			stmtp.setString(3, username);
			stmtp.setInt(4, processId);
			stmtp.executeUpdate();
			stmtp.close();

			// update process_versions
			query = " update " + ConstantDB.TABLE_VERSIONS
			+ " set " + ConstantDB.ATTR_VERSION_NAME + " = ? , "
			+ ConstantDB.ATTR_RANKING + " = ? , "
			+ ConstantDB.ATTR_LAST_UPDATE + " = date_format(now(), '%Y-%c-%d %k:%i:%s') "
			+ " where " + ConstantDB.ATTR_PROCESSID + " = ? "
			+ " and " + ConstantDB.ATTR_VERSION_NAME + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, newVersion);
			stmtp.setInt(2, ranking);
			stmtp.setInt(3, processId);
			stmtp.setString(4, preVersion);
			stmtp.executeUpdate();
			stmtp.close();

			// update derived_versions
			query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS
			+ " set " + ConstantDB.ATTR_VERSION + " = ? "
			+ " where " + ConstantDB.ATTR_VERSION + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, newVersion);
			stmtp.setString(2, preVersion);
			stmtp.executeUpdate();
			stmtp.close();
			query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS
			+ " set " + ConstantDB.ATTR_DERIVED_VERSION + " = ? "
			+ " where " + ConstantDB.ATTR_DERIVED_VERSION + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, newVersion);
			stmtp.setString(2, preVersion);
			stmtp.executeUpdate();

			// synchronise with npf
			query = " select " + ConstantDB.ATTR_CONTENT + "," + ConstantDB.ATTR_URI
			+ " , " + ConstantDB.ATTR_NAT_TYPE
			+ " , " + ConstantDB.ATTR_LAST_UPDATE
			+ " from " + ConstantDB.TABLE_NATIVES
			+ " join " + ConstantDB.TABLE_VERSIONS
			+ " using (" + ConstantDB.ATTR_CANONICAL + ")"
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + newVersion + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next()) {
				throw new Exception("Error ProcessDAO (editDataProcesses): cannot retrieve CPF.");
			} else {
				do {
					InputStream npf = rs.getAsciiStream(1);
					Integer uri = rs.getInt(2);
					String nativeType = rs.getString(3);
					String lastUpdate = rs.getString(4);
					ByteArrayOutputStream npf_xml = new ByteArrayOutputStream();
					JAXBContext jc;
					Unmarshaller u;
					Marshaller m;
					if (nativeType.compareTo("XPDL 2.1")==0) {
						jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
						u = jc.createUnmarshaller();
						JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(npf);
						PackageType npf_o = rootElement.getValue();

						npf_o = copyParam2xpdl(npf_o, processId, processName, newVersion, username, null, lastUpdate, null);

						m = jc.createMarshaller();
						m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
						JAXBElement<PackageType> rootnpf = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(npf_o);
						m.marshal(rootnpf, npf_xml);
					} else if (nativeType.compareTo("EPML 2.0")==0) {
						jc = JAXBContext.newInstance("de.epml");
						u = jc.createUnmarshaller();
						JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(npf);
						TypeEPML npf_o = rootElement.getValue();

						m = jc.createMarshaller();
						m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
						JAXBElement<TypeEPML> rootnpf = new de.epml.ObjectFactory().createEpml(npf_o);
						m.marshal(rootnpf, npf_xml);
					}
					query = " update " + ConstantDB.TABLE_NATIVES
					+ " set " + ConstantDB.ATTR_CONTENT + " = ? "
					+ " where " + ConstantDB.ATTR_URI + " = ? ";
					stmtp = conn.prepareStatement(query);
					stmtp.setString(1, npf_xml.toString());
					stmtp.setInt(2, uri);
					stmtp.executeUpdate();
					stmtp.close();
				} while (rs.next());
				stmt.close(); rs.close();
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error ProcessDAO (editDataProcesses): " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error ProcessDAO (editDataProcesses): " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmtp, null);
		}
	}
}

