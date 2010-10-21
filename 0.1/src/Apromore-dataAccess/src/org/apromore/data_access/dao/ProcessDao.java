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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.commons.Constants;
import org.apromore.data_access.exception.ExceptionAnntotationName;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.exception.ExceptionStoreVersion;
import org.apromore.data_access.exception.ExceptionSyncNPF;
import org.apromore.data_access.model_canoniser.AnnotationsType;
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
		Statement stmtA = null;
		ResultSet rsA = null;
		Statement stmtV = null;
		ResultSet rsV = null;
		String requeteV = null;
		try {
			conn = this.getConnection();
			stmtP = conn.createStatement();
			/* query returns, for each process version which satisfies the search condition, 
			 * the process Id, process name, domain, original type, ranking, owner and
			 * latest version name (the latest version is the one associated by the more
			 * recent creation date.
			 */
			query = "SELECT distinct " + ConstantDB.ATTR_PROCESSID + "," 
			+             ConstantDB.ATTR_NAME + ", "
			+             ConstantDB.ATTR_DOMAIN + "," 
			+             ConstantDB.ATTR_ORIGINAL_TYPE + ","
			+     " coalesce(R." + ConstantDB.ATTR_RANKING  + ",''),"
			+             ConstantDB.ATTR_OWNER
			+     " FROM " + ConstantDB.TABLE_PROCESSES
			+     " natural join " + ConstantDB.TABLE_CANONICALS + " C "
			+     "    join " + ConstantDB.VIEW_PROCESS_RANKING + " R using (" + ConstantDB.ATTR_PROCESSID + ")" ;

			if (condition.compareTo("")!=0) {
				query += " where " + condition;
			} 
			query += " order by " + ConstantDB.ATTR_PROCESSID ;
			String ranking = null;
			rsP = stmtP.executeQuery(query);
			while (rsP.next()) {
				int processId = rsP.getInt(1);
				ProcessSummaryType processSummary = new ProcessSummaryType();
				processSummaries.getProcessSummary().add(processSummary);
				processSummary.setId(processId);
				processSummary.setName(rsP.getString(2));
				processSummary.setDomain(rsP.getString(3));
				processSummary.setOriginalNativeType(rsP.getString(4));
				processSummary.setRanking(rsP.getString(5));
				processSummary.setOwner(rsP.getString(6));

				stmtV = conn.createStatement();
				requeteV = " select " + ConstantDB.ATTR_VERSION_NAME + ", "
				//+ "date_format(" + ConstantDB.ATTR_CREATION_DATE + ", '%d/%c/%Y %k:%i:%f')" + ",  "
				//+ "date_format(" + ConstantDB.ATTR_LAST_UPDATE  + ", '%d/%c/%Y %k:%i:%f')" + ",  "
				+ ConstantDB.ATTR_CREATION_DATE + ", "
				+ ConstantDB.ATTR_LAST_UPDATE + ", "
				+ " coalesce(" + ConstantDB.ATTR_RANKING + ",''),"
				+ ConstantDB.ATTR_DOCUMENTATION
				+ " from " + ConstantDB.TABLE_CANONICALS
				+ " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId 
				+ " order by  " + ConstantDB.ATTR_CREATION_DATE ;
				rsV = stmtV.executeQuery(requeteV);
				String lastVersion="";
				while (rsV.next()){
					query = " select " + ConstantDB.ATTR_NAME
					+ " from " + ConstantDB.TABLE_ANNOTATIONS + " A "
					+ " join " + ConstantDB.TABLE_CANONICALS + " C "
					+ " on (" + "A." + ConstantDB.ATTR_CANONICAL + " = " + "C." + ConstantDB.ATTR_URI + ")"
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
					+ "  and " + ConstantDB.ATTR_VERSION_NAME + " = '" + rsV.getString(1) + "'";
					stmtA = conn.createStatement();
					rsA = stmtA.executeQuery(query);
					List<String> listAnnotations = new ArrayList();
					while(rsA.next()) {
						listAnnotations.add(rsA.getString(1));
					}
					VersionSummaryType version = new VersionSummaryType();
					version.setName(rsV.getString(1));
					lastVersion = version.getName();
					version.setCreationDate(rsV.getString(2));
					version.setLastUpdate(rsV.getString(3));
					version.setRanking(rsV.getString(4));
					version.setDocumentation(rsV.getString(5));
					version.getAnnotations().addAll(listAnnotations);
					processSummary.getVersionSummaries().add(version);
				}
				processSummary.setLastVersion(lastVersion);
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
	 * Annotation is named "Initial" (cf Constants.INITIAL_ANNOTATION)
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
		String annotationName = Constants.INITIAL_ANNOTATION;
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
			Integer processId = keys.getInt(1);
			keys.close(); stmtp.close();

			// Store informations given as parameters in NPF
			// creationDate might be null or empty. 
			if (creationDate==null || "".compareTo(creationDate)==0) {
				creationDate = now();
			}
			if (lastUpdate==null) lastUpdate="";
			if (documentation==null) documentation="";
			String cpf_uri = newCpf(processId, version);
			// copy parameters values in npf 
			InputStream sync_npf = copyParam2NPF(process_xml, nativeType, cpf_uri, processName, version,
					username, creationDate, lastUpdate, documentation);
			String process_string = inputStream2String(sync_npf).trim();
			String cpf_string = inputStream2String(cpf_xml).trim();
			String anf_string = inputStream2String(anf_xml).trim();
			query = " insert into " + ConstantDB.TABLE_CANONICALS
			+ "(" + ConstantDB.ATTR_URI + ","
			+     ConstantDB.ATTR_PROCESSID + ","
			+     ConstantDB.ATTR_VERSION_NAME + ","
			+     ConstantDB.ATTR_CREATION_DATE + ","
			+     ConstantDB.ATTR_LAST_UPDATE + ","
			+     ConstantDB.ATTR_CONTENT + "," 
			+	  ConstantDB.ATTR_DOCUMENTATION + ")"
			+ " values (?, ?, ?, ?, ?, ?, ?) ";
			//+ " values (?, ?, str_to_date(?,'%Y-%c-%d %k:%i:%s'), str_to_date(?,), ?, ?) ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, cpf_uri);
			stmtp.setInt(2, processId);
			stmtp.setString(3, version);
			stmtp.setString(4,creationDate);
			stmtp.setString(5,lastUpdate);
			stmtp.setString(6, cpf_string);
			stmtp.setString(7, documentation);
			Integer rs2 = stmtp.executeUpdate();


			query = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?,?,?) ";
			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmtp.setString(1, process_string);
			stmtp.setString(2, nativeType);
			stmtp.setString(3, cpf_uri);
			Integer rs6 = stmtp.executeUpdate();
			keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve NPF id.");
			}
			int natId = keys.getInt(1);
			keys.close(); stmtp.close();

			query = " insert into " + ConstantDB.TABLE_ANNOTATIONS
			+ "(" + ConstantDB.ATTR_NATIVE  + ","
			+ ConstantDB.ATTR_CANONICAL + ","
			+ ConstantDB.ATTR_NAME + ","
			+ ConstantDB.ATTR_CONTENT + ")"
			+ " values (?,?,?,?) ";
			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmtp.setInt(1, natId);
			stmtp.setString(2, cpf_uri);
			stmtp.setString (3, annotationName);
			stmtp.setString (4, anf_string);
			stmtp.executeUpdate();
			stmtp.close();

			process.setDomain(domain);
			process.setId(processId);
			process.setLastVersion(version);
			process.setName(processName);
			process.setOriginalNativeType(nativeType);
			process.setRanking("");
			process.setOwner(username);
			first_version.setName(version);
			first_version.setCreationDate(creationDate);
			first_version.setLastUpdate(lastUpdate);
			first_version.setRanking("");
			AnnotationsType annotType = new AnnotationsType();
			annotType.setNativeType(nativeType);
			annotType.getAnnotationName().add(Constants.INITIAL_ANNOTATION);
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt0, rs0);
		}
		return process;
	}

	/**
	 * Generate a cpf uri for version of processId
	 * @param processId
	 * @param version
	 * @return
	 */
	private String newCpf(Integer processId, String version) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmS");
		Date date = new Date();
		String time = dateFormat.format(date);
		return processId.toString() + version.replaceAll("\\.", "") + time;
	}

	private String now() throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		String today = null;
		try {
			conn = this.getConnection();
			query = " select date_format(now(), '%Y-%m-%dT%k-%i-%s') ";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			if (!rs.next()) {
				throw new ExceptionDao ("Error: cannot retrieve date.");
			}
			today = rs.getString(1);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}
		return today;
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
			String nativeType, String cpf_uri, String processName,
			String version, String username, String creationDate,
			String lastUpdate, String documentation) throws JAXBException {

		InputStream res = null;
		if (nativeType.compareTo("XPDL 2.1")==0) {
			JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
			Unmarshaller u = jc.createUnmarshaller();
			JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
			PackageType pkg = rootElement.getValue();
			copyParam2xpdl (pkg, cpf_uri, processName, version, username, creationDate, lastUpdate, documentation);

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
	private void copyParam2xpdl(PackageType pkg, String cpf_uri,
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
		if (cpf_uri!=null) pkg.setId(cpf_uri);
		if (version!=null) pkg.getRedefinableHeader().getVersion().setValue(version);
		if (username!=null) pkg.getRedefinableHeader().getAuthor().setValue(username);
		if (creationDate!=null)	pkg.getPackageHeader().getCreated().setValue(creationDate);
		if (lastUpdate!=null)pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
		if (documentation!=null)pkg.getPackageHeader().getDocumentation().setValue(documentation);
	}

	/**
	 * Return the native format for process version identified by <processId, version>
	 * in native type nativeType
	 * @param processId
	 * @param version
	 * @param nativeType
	 * @return
	 * @throws ExceptionDao
	 */
	public String getNative (Integer processId, String version, String nativeType) throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			query = " select " + "A." + ConstantDB.ATTR_CONTENT
			+ " from " + ConstantDB.TABLE_NATIVES + " A "
			+ " join "
			+            ConstantDB.TABLE_CANONICALS + " C "
			+ " on (" + "A." + ConstantDB.ATTR_CANONICAL + " = " + "C." + ConstantDB.ATTR_URI + ")"
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'"
			+   " and " + ConstantDB.ATTR_NAT_TYPE  + " = '" + nativeType + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				throw new ExceptionDao ("Cannot access the native format.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}
	}


	/**
	 * Retrieve annotation named annotationNamed (if exist) associated with the process version 
	 * identified by <processId, version>
	 * @param processId
	 * @param version
	 * @param annotationName
	 * @return
	 * @throws ExceptionDao
	 * TODO needs to be modified because of annotations
	 */
	public String getAnnotation(Integer processId, String version, String annotationName) throws ExceptionDao {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		try {
			conn = this.getConnection();
			stmt = conn.createStatement();
			query = " select " + " A." + ConstantDB.ATTR_CONTENT
			+ " from " + ConstantDB.TABLE_ANNOTATIONS + " A "
			+ " join " + ConstantDB.TABLE_CANONICALS + " C "
			+         " on (" + " A." + ConstantDB.ATTR_CANONICAL + " = " + " C." + ConstantDB.ATTR_URI + ")"
			+ " where " + " C." + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
			+   " and " + " C." + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'"
			+   " and " + " A." + ConstantDB.ATTR_NAME + " = '" + annotationName + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				throw new ExceptionDao("Cannot retrieve annotation file (" + annotationName + ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}

	}

	/**
	 * Return the canonical identified by <processId, version>
	 * @param processId
	 * @param version
	 * @return
	 * @throws ExceptionDao
	 */
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
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			} else {
				throw new ExceptionDao ("Canonical not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt, rs);
		}
	}

	/**
	 * Store a native format for a process version whose canonical already exists.
	 * @param nativeType
	 * @param processId
	 * @param version
	 * @param native_xml
	 * @throws SQLException
	 * @throws ExceptionDao
	 */
	// orphan. To be checked.
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
			String query = " select " + ConstantDB.ATTR_URI
			+ " from " + ConstantDB.TABLE_CANONICALS
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+   " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
			rs1 = stmt1.executeQuery(query);
			if (rs1.next()) {
				canonical = rs1.getInt(1);
			} else {
				throw new ExceptionDao ("Canonical process not found.");
			}
			stmt1.close();rs1.close();
			String native_string = inputStream2String(native_xml);

			/* does a native already exist for this canonical for this native type ?
			 */
			query = " select " + "N." + ConstantDB.ATTR_URI 
			+ " from " + ConstantDB.TABLE_NATIVES + " N "
			+ " join " + ConstantDB.TABLE_CANONICALS + " C "
			+ " on (" + "N." + ConstantDB.ATTR_URI + " = " + "C." + ConstantDB.ATTR_CANONICAL + ")"
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
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmt1, rs1);
			stmt2.close();
		}

	}
	/**
	 * Return the inputstream as a string. The inputstream must be positioned so
	 * it can be read.
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws ExceptionDao
	 */
	public String inputStream2String (InputStream is) throws IOException, ExceptionDao  {
		String line = null;
		try {
			StringBuilder sb0 = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb0.append(line).append("\n");
			}
			return sb0.toString().trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ExceptionDao("IO error: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ExceptionDao("IO error: " + e.getMessage());
		} finally {
			is.close();
		}
	}
	/**
	 * Store in the database a new version for the process identified by 
	 * processId whose type is nativeType. This call is correlated with the
	 * edit session whose code is editSessionCode.
	 * Data associated with the version are in the NPF inputstream.
	 * If the version does not exist already the new version is derived from the 
	 * version named preVersion, for the same process, otherwise previous values
	 * are overridden.
	 * @param editSessionCode
	 * @param processId
	 * @param nativeType
	 * @param domain
	 * @param npf_is
	 * @param cpf_is
	 * @param apf_is
	 * @throws ExceptionDao
	 * @throws SQLException
	 * @throws ExceptionStoreVersion 
	 * @throws ExceptionSyncNPF 
	 */
	public void storeVersion (int editSessionCode, Integer processId, String preVersion, String nativeType, 
			InputStream npf_is, InputStream cpf_is, InputStream apf_is) 
	throws ExceptionDao, SQLException, ExceptionSyncNPF, ExceptionStoreVersion {
		String query;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String newVersion = null;
			String creationDate = null;
			String lastUpdate = null;
			String documentation = null;
			String username = null;
			String processName = null;
			String cpf_uri = null;
			// read the data above from native_is
			npf_is.mark(0);
			if (nativeType.compareTo("XPDL 2.1")==0) {
				JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
				Unmarshaller u = jc.createUnmarshaller();
				JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(npf_is);
				PackageType pkg = rootElement.getValue();
				try {
					username = pkg.getRedefinableHeader().getAuthor().getValue();
					processName = pkg.getName();
					newVersion = pkg.getRedefinableHeader().getVersion().getValue().trim();
					creationDate = pkg.getPackageHeader().getCreated().getValue().trim();
					cpf_uri = pkg.getId();
					if (pkg.getPackageHeader().getModificationDate()!=null) {
						lastUpdate = pkg.getPackageHeader().getModificationDate().getValue().trim();
					} else {
						lastUpdate = "";
					}
					if (pkg.getPackageHeader().getDocumentation()!=null) {
						documentation = pkg.getPackageHeader().getDocumentation().getValue().trim();
					} else {
						documentation = "";
					}
				} catch (NullPointerException e) {
					throw new ExceptionSyncNPF ("Missing information in NPF.");
				}
				if (preVersion.compareTo("0.0")!=0 && newVersion.compareTo(preVersion)!=0) {
					// version "0.0" is the one created at process creation time. 
					// Should be overridden by the next one.
					// if preVersion != newVersion: try to derive newVersion from preVersion
					String newCpf_uri = newCpf(processId, newVersion);
					copyParam2xpdl (pkg, newCpf_uri, processName, newVersion, username, creationDate, lastUpdate, documentation);
					pkg.setId(newCpf_uri);
					Marshaller m = jc.createMarshaller();
					m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
					ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
					m.marshal(rootxpdl, xpdl_xml);
					InputStream newNpf_is = new ByteArrayInputStream(xpdl_xml.toByteArray());
					deriveVersion (newCpf_uri, processId, preVersion, newVersion, nativeType, newNpf_is, cpf_is, apf_is,
							editSessionCode, lastUpdate, documentation);
				} else {
					conn = this.getConnection();
					// if preVersion = newVersion
					// check whether preVersion is leaf in the derivation tree
					// if yes, override its previous values, otherwise raise an exception
					query = " select * from " + ConstantDB.TABLE_DERIVED_VERSIONS
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
					+ " and " + ConstantDB.ATTR_VERSION + " = '" + preVersion + "'";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						throw new ExceptionStoreVersion ("Version " + preVersion + " cannot be overridden.");
					} else {
						stmt.close();rs.close();
						// cpf_uri copied by Oryx in native is wrong....
						// get it from canonicals....
						query = " select " + ConstantDB.ATTR_URI
						+ " from " + ConstantDB.TABLE_CANONICALS
						+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
						+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + preVersion + "'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						if (!rs.next()) {
							throw new ExceptionStoreVersion ("Version " + preVersion + " cannot be overridden.");
						} else {
							cpf_uri = rs.getString(1);
							if (preVersion.compareTo("0.0")==0) {
								// version "0.0" is the one created at process creation time. 
								// Should be overridden by the next one.
								copyParam2xpdl (pkg, cpf_uri, processName, newVersion, username, creationDate, lastUpdate, documentation);
								Marshaller m = jc.createMarshaller();
								m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
								JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
								ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
								m.marshal(rootxpdl, xpdl_xml);
								InputStream newNpf_is = new ByteArrayInputStream(xpdl_xml.toByteArray());
								overrideVersion (processId, preVersion, nativeType, newNpf_is, cpf_is, apf_is,
										creationDate, lastUpdate, documentation, newVersion);
							} else {
								copyParam2xpdl (pkg, cpf_uri, processName, preVersion, username, creationDate, lastUpdate, documentation);
								Marshaller m = jc.createMarshaller();
								m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
								JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(pkg);
								ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
								m.marshal(rootxpdl, xpdl_xml);
								InputStream newNpf_is = new ByteArrayInputStream(xpdl_xml.toByteArray());
								overrideVersion (processId, preVersion, nativeType, newNpf_is, cpf_is, apf_is,
										creationDate, lastUpdate, documentation, null);
							}
						}
					}
					conn.commit();
				}
			} else if (nativeType.compareTo("EPML 2.0")==0) {
				JAXBContext jc = JAXBContext.newInstance("de.epml");
				Unmarshaller u = jc.createUnmarshaller();
				JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(npf_is);
				TypeEPML epml = rootElement.getValue();
				// TODO: to be completed with EPML

				if (preVersion.compareTo("0.0")!=0 && newVersion.compareTo(preVersion)!=0) {
					// if preVersion != newVersion: try to derive newVersion from preVersion
					String newCpf_uri = newCpf(processId, preVersion);
					deriveVersion (newCpf_uri, processId, preVersion, newVersion, nativeType, npf_is, cpf_is, apf_is,
							editSessionCode, lastUpdate, documentation);
					// TODO: copy the newCpf in epml object and synchronise data with npf.
				} else {
					conn = this.getConnection();
					// if preVersion = newVersion
					// check whether preVersion is leaf in the derivation tree
					// if yes, override its previous values, otherwise raise an exception
					query = " select * from " + ConstantDB.TABLE_DERIVED_VERSIONS
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
					+ " and " + ConstantDB.ATTR_VERSION + " = '" + preVersion + "'";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						throw new ExceptionStoreVersion ("Version " + preVersion + " cannot be overridden.");
					} else {
						if (preVersion.compareTo("0.0")==0) {
							// version "0.0" is the one created at process creation time. 
							// Should be overridden by the next one.
							overrideVersion (processId, preVersion, nativeType, npf_is, cpf_is, apf_is,
									creationDate, lastUpdate, documentation, newVersion);
							// TODO: synchronise data with npf.
						} else {

							overrideVersion (processId, preVersion, nativeType, npf_is, cpf_is, apf_is,
									creationDate, lastUpdate, documentation, null);
						}
					}
					conn.commit();
				}
			} else {
				throw new ExceptionSyncNPF("Couldn't read information in NPF.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage());
		} catch (ExceptionSyncNPF e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionSyncNPF (e.getMessage());
		} catch (ExceptionStoreVersion e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionStoreVersion (e.getMessage());
		} catch (ExceptionDao e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage());
		} catch (JAXBException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("JAXB error: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("IO error: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}

	/**
	 * Override the existing version versionName of process whose id is processId with the
	 * values nativeType, npf_string, cpf_string, anf_string, creationDate, lastUpdate, 
	 * annotation and documentation.
	 * If initialVersion has a value, then versionName was created at creation time and
	 * should be renamed with initialVersion
	 * @param processId
	 * @param versionName
	 * @param nativeType
	 * @param npf_string
	 * @param cpf_string
	 * @param apf_string
	 * @param creationDate
	 * @param lastUpdate
	 * @param annotation
	 * @param documentation
	 * @param initialVersion
	 * @throws ExceptionDao 
	 * @throws SQLException 
	 */
	private void overrideVersion(int processId, String versionName,
			String nativeType, InputStream npf_is, InputStream cpf_is,
			InputStream apf_is, String creationDate,
			String lastUpdate, String documentation, String initialVersion) throws ExceptionDao, SQLException {

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement stmtp = null;
		ResultSet rs = null;
		String query = null;
		try {
			String cpf_string = inputStream2String(cpf_is);
			String apf_string = inputStream2String(apf_is);
			String npf_string = inputStream2String(npf_is);

			conn = this.getConnection();
			// update data in table process_versions
			query = " update " + ConstantDB.TABLE_CANONICALS
			+ " set " + ConstantDB.ATTR_LAST_UPDATE + " = ? "
			+ " , "   + ConstantDB.ATTR_DOCUMENTATION  + " = ? "
			+ " , "   + ConstantDB.ATTR_CONTENT + "= ? "
			+ " where " + ConstantDB.ATTR_PROCESSID  + " = ? "
			+    " and " + ConstantDB.ATTR_VERSION_NAME  + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, lastUpdate);
			stmtp.setString(2,documentation);
			stmtp.setString(3, cpf_string);
			stmtp.setInt(4,processId);
			stmtp.setString(5,versionName);
			int rs1 = stmtp.executeUpdate();
			stmtp.close();
			// update anf and npf
			// need cpf uri
			query = " select " + ConstantDB.ATTR_URI
			+ " from " + ConstantDB.TABLE_CANONICALS
			+ " where " + ConstantDB.ATTR_PROCESSID + " = ? "
			+ " and "   + ConstantDB.ATTR_VERSION_NAME + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setInt(1,processId);
			stmtp.setString(2, versionName);
			rs = stmtp.executeQuery();
			if (!rs.next()) throw new ExceptionDao ("Cannot access canonical.");
			String cpf_uri = rs.getString(1);
			stmtp.close();
			// delete all natives associated with process version
			query = " delete from " + ConstantDB.TABLE_NATIVES
			+ " where " + ConstantDB.ATTR_CANONICAL + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1,cpf_uri);
			rs1 = stmtp.executeUpdate();
			stmtp.close();			
			// delete all annotations associated with process version
			/* query = " delete from " + ConstantDB.TABLE_ANNOTATIONS
			+ " where " + ConstantDB.ATTR_CANONICAL + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setInt(1,cpf_uri);
			rs1 = stmtp.executeUpdate();
			stmtp.close();*/
			// record new native			
			query = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?,?,?) ";
			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmtp.setString(1, npf_string);
			stmtp.setString(2, nativeType);
			stmtp.setString(3, cpf_uri);
			Integer rs6 = stmtp.executeUpdate();
			ResultSet keys = stmtp.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve NPF id.");
			}
			int nat_uri = keys.getInt(1);
			keys.close(); stmtp.close();

			query = " insert into " + ConstantDB.TABLE_ANNOTATIONS
			+ "(" + ConstantDB.ATTR_NATIVE  + ","
			+ ConstantDB.ATTR_CANONICAL + ","
			+ ConstantDB.ATTR_NAME + ","
			+ ConstantDB.ATTR_CONTENT + ")"
			+ " values (?,?,?,?) ";
			stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			stmtp.setInt(1, nat_uri);
			stmtp.setString(2, cpf_uri);
			stmtp.setString (3, Constants.INITIAL_ANNOTATION);
			stmtp.setString (4, apf_string);
			stmtp.executeUpdate();
			stmtp.close();
			if (initialVersion != null){
				// rename versionName into initialVersion
				query = " update " + ConstantDB.TABLE_CANONICALS
				+ " set " + ConstantDB.ATTR_VERSION_NAME + " = ? "
				+ " , " + ConstantDB.ATTR_LAST_UPDATE + " = null "
				+ " where " + ConstantDB.ATTR_PROCESSID + " = ? "
				+   " and " + ConstantDB.ATTR_VERSION_NAME + " = ? ";
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1,initialVersion);
				stmtp.setInt(2,processId);
				stmtp.setString(3, versionName);
				stmtp.executeUpdate();
				stmtp.close();
			}
			conn.commit();			
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao (e.getMessage());
		} finally {
			Release(conn, stmt, rs);
		}
	}

	/**
	 * Derive a new version name newVersion from preVersion for process identified by processId.
	 * Pre-condition: preVersion != newVersion 
	 * If newVersion is not a valid name (i.e. it already exists), then all informations are 
	 * temporary stored (for subsequent used) and an exception is raised.
	 * id newVersion is valid, the derived version is created and the version name in the edit 
	 * session structure is set to newVersion.
	 * @param processId
	 * @param preVersion
	 * @param newVersion
	 * @param nativeType
	 * @param npf_string
	 * @param cpf_string
	 * @param apf_string
	 * @param editSessionCode
	 * @param creationDate
	 * @param lastUpdate
	 * @param annotation
	 * @param documentation
	 * @throws ExceptionDao
	 * @throws SQLException
	 * @throws ExceptionStoreVersion
	 * @throws IOException 
	 */
	private void deriveVersion(String cpf_uri, Integer processId, String preVersion,
			String newVersion, String nativeType, InputStream npf_is, InputStream cpf_is,
			InputStream apf_is, int editSessionCode,
			String lastUpdate, String documentation) throws ExceptionDao, SQLException, ExceptionStoreVersion, IOException {
		Connection conn = null, conn1 = null;
		Statement stmt0 = null, stmt1 = null;
		PreparedStatement stmtp = null;
		ResultSet rs0 = null, rs1 = null ;
		String query = null;
		String annotation = Constants.INITIAL_ANNOTATION;
		String npf_string = inputStream2String(npf_is);
		String apf_string = inputStream2String(apf_is);
		String cpf_string = inputStream2String(cpf_is);
		try {
			conn = this.getConnection();
			// does newVersion already exist?
			query = " select " + ConstantDB.ATTR_URI
			+       " from " + ConstantDB.TABLE_CANONICALS
			+       " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+       "   and " + ConstantDB.ATTR_VERSION_NAME + " = '" + newVersion + "'";
			stmt0 = conn.createStatement();
			rs0 = stmt0.executeQuery(query);
			if (rs0.next()) {
				conn1 = this.getConnection();
				// store the data in a temporary table
				query = " insert into " + ConstantDB.TABLE_TEMP_VERSIONS 
				+ "(" + ConstantDB.ATTR_CODE
				+ "," + ConstantDB.ATTR_RECORD_TIME
				+ "," + ConstantDB.ATTR_PROCESSID
				+ "," + ConstantDB.ATTR_PRE_VERSION_NAME
				+ "," + ConstantDB.ATTR_NEW_VERSION_NAME
				+ "," + ConstantDB.ATTR_CREATION_DATE
				+ "," + ConstantDB.ATTR_LAST_UPDATE
				+ "," + ConstantDB.ATTR_DOCUMENTATION
				+ "," + ConstantDB.ATTR_NAME
				+ "," + ConstantDB.ATTR_CPF
				+ "," + ConstantDB.ATTR_APF
				+ "," + ConstantDB.ATTR_NPF + ")"
				+ " values (?, now(), ?, ?, ?, date_format(now(), '%Y-%m-%dT%k-%i-%s'), ?, ?, ?, ?, ?, ?)";
				stmtp = conn1.prepareStatement(query);
				stmtp.setInt(1, editSessionCode);
				stmtp.setInt(2, processId);
				stmtp.setString(3, preVersion);
				stmtp.setString(4, newVersion);
				stmtp.setString(5, lastUpdate);
				stmtp.setString(6, annotation);
				stmtp.setString(7, documentation);
				stmtp.setString(8, cpf_string);
				stmtp.setString(9, apf_string);
				stmtp.setString(10, npf_string);
				Integer rs2 = stmtp.executeUpdate();
				stmtp.close();
				conn1.commit();
				throw new ExceptionStoreVersion("Version " + newVersion + " already exists.");
			} else {
				// newVersion does not exist. Derive a new version.
				// add a new version of the process identified by processId + newVersion
				String query2 = " insert into " + ConstantDB.TABLE_CANONICALS
				+ "(" + ConstantDB.ATTR_URI + ","
				+     ConstantDB.ATTR_PROCESSID + ","
				+     ConstantDB.ATTR_VERSION_NAME + ","
				+     ConstantDB.ATTR_CREATION_DATE + ","
				+     ConstantDB.ATTR_DOCUMENTATION + ","
				+     ConstantDB.ATTR_CONTENT + ")"
				+ " values (?, ?, ?, date_format(now(), '%Y-%m-%dT%k-%i-%s'), ?, ?) ";
				//+ " values (?, ?, str_to_date(?,'%Y-%c-%d %k:%i:%s'), str_to_date(?,'%Y-%c-%d %k:%i:%s'), ?, ?) ";
				stmtp = conn.prepareStatement(query2);
				stmtp.setString(1, cpf_uri);
				stmtp.setInt(2, processId);
				stmtp.setString(3, newVersion);
				stmtp.setString(4, documentation);
				stmtp.setString(5, cpf_string);
				Integer rs2 = stmtp.executeUpdate();
				stmtp.close();

				String query5 = " insert into " + ConstantDB.TABLE_NATIVES
				+ "(" + ConstantDB.ATTR_CONTENT + ","
				+       ConstantDB.ATTR_NAT_TYPE + ","
				+       ConstantDB.ATTR_CANONICAL + ")"
				+ " values (?,?,?) ";
				stmtp = conn.prepareStatement(query5, Statement.RETURN_GENERATED_KEYS);
				//stmt6.setAsciiStream(1, process_xml);
				stmtp.setString(1, npf_string);
				stmtp.setString(2, nativeType);
				stmtp.setString(3, cpf_uri);
				Integer rs6 = stmtp.executeUpdate();
				ResultSet keys = stmtp.getGeneratedKeys() ;
				if (!keys.next()) {
					throw new ExceptionDao ("Error: cannot retrieve generated key.");
				}
				int npfId = keys.getInt(1);
				keys.close(); 
				stmtp.close();

				query5 = " insert into " + ConstantDB.TABLE_ANNOTATIONS
				+ "(" + ConstantDB.ATTR_NATIVE
				+ "," + ConstantDB.ATTR_CANONICAL
				+ "," + ConstantDB.ATTR_NAME
				+ "," + ConstantDB.ATTR_CONTENT + ")"
				+ " values (?,?,?,?) ";
				stmtp = conn.prepareStatement(query5);
				stmtp.setInt (1, npfId);
				stmtp.setString (2, cpf_uri);
				stmtp.setString (3, annotation);
				stmtp.setString (4, apf_string);
				Integer rs5 = stmtp.executeUpdate();
				stmtp.close();

				query2 = " insert into " + ConstantDB.TABLE_DERIVED_VERSIONS
				+ "(" + ConstantDB.ATTR_PROCESSID + ","
				+     ConstantDB.ATTR_VERSION + ","
				+     ConstantDB.ATTR_DERIVED_VERSION + ")"
				+ " values (?,?,?)";
				stmtp = conn.prepareStatement(query2);
				stmtp.setInt(1, processId);
				stmtp.setString(2, preVersion);
				stmtp.setString(3, newVersion);
				rs2 = stmtp.executeUpdate();
				stmtp.close();

				// modify version name in edit session
				query = "update " + ConstantDB.TABLE_EDIT_SESSIONS 
				+ " set " + ConstantDB.ATTR_VERSION_NAME	+ " = ? "
				+ " where " + ConstantDB.ATTR_CODE + " = ? ";
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1, newVersion);
				stmtp.setInt(2, editSessionCode);
				rs2 = stmtp.executeUpdate();
				stmtp.close();
				conn.commit();
			}
		} catch (ExceptionDao e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao (e.getMessage());
		} catch (ExceptionStoreVersion e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionStoreVersion (e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage());
		} finally {
			Release(conn, stmt0, rs0);
			Release(conn1, stmt1, rs1);
		}
	}

	/**
	 * Override process version data related to editSessionCode, processId, versionName with those
	 * temporary stored in temp_versions table
	 * @param editSessionCode
	 * @param processId
	 * @param versionName
	 * @throws SQLException
	 * @throws ExceptionDao
	 */
	public void confirmStoreVersion (int editSessionCode, int processId, String nativeType, String preVersion, String newVersion) 
	throws SQLException, ExceptionDao {

		Connection conn = null;
		PreparedStatement stmtp = null;
		ResultSet rs0 = null;
		String query = null;

		try {
			conn = this.getConnection();
			// newVersion already exists. User has confirmed that new data override preceding one.
			// get information from temp_versions
			query = " select " + ConstantDB.ATTR_CODE
			+ "," + ConstantDB.ATTR_RECORD_TIME
			+ "," + ConstantDB.ATTR_PROCESSID
			+ "," + ConstantDB.ATTR_PRE_VERSION_NAME
			+ "," + ConstantDB.ATTR_NEW_VERSION_NAME
			+ "," + ConstantDB.ATTR_CREATION_DATE
			+ "," + ConstantDB.ATTR_LAST_UPDATE
			+ "," + ConstantDB.ATTR_ANNOTATION
			+ "," + ConstantDB.ATTR_DOCUMENTATION
			+ "," + ConstantDB.ATTR_CPF
			+ "," + ConstantDB.ATTR_APF
			+ "," + ConstantDB.ATTR_NPF
			+ " from " + ConstantDB.TABLE_TEMP_VERSIONS
			+ " where " + ConstantDB.ATTR_CODE + " = ? "
			+ "   and " + ConstantDB.ATTR_PROCESSID + " = ? "
			+ "   and " + ConstantDB.ATTR_PRE_VERSION_NAME + " = ? "
			+ "   and " + ConstantDB.ATTR_NEW_VERSION_NAME + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setInt(1, editSessionCode);
			stmtp.setInt(2, processId);
			stmtp.setString(3, preVersion);
			stmtp.setString(4, newVersion);
			rs0 = stmtp.executeQuery();
			if (!rs0.next()) {
				throw new ExceptionDao ("Coudn't retrieve temporary data.");
			} else {
				// should return only one tuple....
				String creationDate = rs0.getString(5);
				String lastupdate = rs0.getString(6);
				String annotation = rs0.getString(8);
				String documentation = rs0.getString(8);
				String cpf = rs0.getString(9);
				String apf = rs0.getString(10);
				String npf  = rs0.getString(11);
				stmtp.close();
				ByteArrayInputStream cpf_is = new ByteArrayInputStream(cpf.getBytes());
				ByteArrayInputStream apf_is = new ByteArrayInputStream(apf.getBytes());
				ByteArrayInputStream npf_is = new ByteArrayInputStream(npf.getBytes());
				storeVersion(editSessionCode, processId, newVersion, nativeType, npf_is, cpf_is, apf_is);
			} 
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmtp, null);
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
					String cpf_uri ;
					query = " select " + ConstantDB.ATTR_URI
					+ " from " + ConstantDB.TABLE_CANONICALS
					+ " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString()
					+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + v + "'";
					stmt = conn.createStatement();
					rs = stmt.executeQuery(query);
					if (rs.next()) {
						cpf_uri = rs.getString(1);
						stmt.close(); rs.close();
					} else {
						stmt.close(); rs.close();
						throw new ExceptionDao ("Version " + v 
								+ " for process " + pId.toString() + " not found. \n");
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
						/* r exists: at least two versions exist for p. Derivation tree
						 * needs to be updated.
						 * Retrieve parent version of <p,v> if exists (if not, v is root)
						 * Retrieve r2 in derived_versions such as r2[derived_version,processId] = <v,p>
						 */
						query = " select " +  ConstantDB.ATTR_DERIVED_VERSION 
						+ " from " + ConstantDB.TABLE_DERIVED_VERSIONS
						+ " where " + ConstantDB.ATTR_VERSION + " = '" +  v + "'"
						+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
						stmt = conn.createStatement();
						rs = stmt.executeQuery(query);
						if (rs.next()) {
							/* <p,v> has children. Delete <p, v> 
							 */
							query = " delete from " + ConstantDB.TABLE_DERIVED_VERSIONS
							+ " where " + ConstantDB.ATTR_VERSION + " = '" +  v + "'"
							+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
							stmtp = conn.prepareStatement(query);
							int r = stmtp.executeUpdate();
							stmtp.close();
							/* Update all child versions of <p, v> (if any).
							 * Replace, for each r3 in derived_version
							 * such as r3[version,processId] = <v,p>, replace v with r2[version]
							 */
							query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS 
							+ " set " + ConstantDB.ATTR_DERIVED_VERSION + " = '" + rs.getString(1) + "'"
							+ " where " + ConstantDB.ATTR_DERIVED_VERSION + " = '" + v + "'"
							+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
							stmtp = conn.prepareStatement(query);
							r = stmtp.executeUpdate();
							stmtp.close();
						} else {
							// <p, v> has no children. Delete <p,v> 
							query = " delete from " + ConstantDB.TABLE_DERIVED_VERSIONS
							+ " where " + ConstantDB.ATTR_DERIVED_VERSION + " = '" +  v + "'"
							+ " and " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString();
							stmtp = conn.prepareStatement(query);
							int r = stmtp.executeUpdate();
							stmtp.close();
						}
						/* delete the process version. Thanks to foreign keys, canonical,
						 * natives and annotations will be deleted on cascade
						 */
						query = " delete from " + ConstantDB.TABLE_CANONICALS
						+ " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString()
						+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + v + "'";
						stmtp = conn.prepareStatement(query);
						int r = stmtp.executeUpdate();
						stmtp.close();
					}
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error " + e.getMessage() + "\n");
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
			String preVersion, String newVersion, String ranking) throws SQLException, ExceptionDao {

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
			query = " update " + ConstantDB.TABLE_CANONICALS
			+ " set " + ConstantDB.ATTR_VERSION_NAME + " = ? , "
			+ ConstantDB.ATTR_RANKING + " = ? , "
			+ ConstantDB.ATTR_LAST_UPDATE + " = date_format(now(), '%Y-%c-%dT%k:%i:%s') "
			+ " where " + ConstantDB.ATTR_PROCESSID + " = ? "
			+ " and " + ConstantDB.ATTR_VERSION_NAME + " = ? ";
			stmtp = conn.prepareStatement(query);
			stmtp.setString(1, newVersion);
			if (ranking==null) {
				stmtp.setNull(2, java.sql.Types.INTEGER);
			} else {
				stmtp.setInt(2, Integer.parseInt(ranking));
			}
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

			// synchronise with all npf associated with the process version, if any
			query = " select " + "N." + ConstantDB.ATTR_CONTENT + "," + "N." + ConstantDB.ATTR_URI
			+ " , " + ConstantDB.ATTR_NAT_TYPE
			+ " , " + ConstantDB.ATTR_LAST_UPDATE
			+ " , C." + ConstantDB.ATTR_URI
			+ " from " + ConstantDB.TABLE_NATIVES + " N "
			+ " join " + ConstantDB.TABLE_CANONICALS + " C "
			+ " on (" + "N." + ConstantDB.ATTR_CANONICAL + " = " + "C." + ConstantDB.ATTR_URI + ")"
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + newVersion + "'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				InputStream npf = rs.getAsciiStream(1);
				Integer uri = rs.getInt(2);
				String nativeType = rs.getString(3);
				String lastUpdate = rs.getString(4);
				String cpf_uri = rs.getString(5);
				ByteArrayOutputStream npf_xml = new ByteArrayOutputStream();
				JAXBContext jc;
				Unmarshaller u;
				Marshaller m;
				if (nativeType.compareTo("XPDL 2.1")==0) {
					jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
					u = jc.createUnmarshaller();
					JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(npf);
					PackageType npf_o = rootElement.getValue();
					copyParam2xpdl(npf_o, cpf_uri, processName, newVersion, username, null, lastUpdate, null);
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
			}
			stmt.close(); rs.close();
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmtp, null);
		}
	}

	/**
	 * 
	 * @param processName
	 * @param versionName
	 * @param username
	 * @param cpf_is
	 */
	public void storeCpf(String processName, String versionName,
			String username, InputStream cpf_is) {
		// TODO
	}

	/**
	 * Store an annotation: it is associated with the process version identified by 
	 * (processId, version), and native process identified by the process version uri and 
	 * the native type nat_type. Its content is content. If isNew then a new annotation
	 * is stored (its name must not exist for the given process  version), otherwise
	 * the existing annotation is overridden.
	 * @param name
	 * @param processId
	 * @param version
	 * @param nat_type
	 * @param content
	 * @param isNew
	 * @throws SQLException 
	 * @throws ExceptionDao
	 * TODO 
	 * @throws ExceptionAnntotationName 
	 */
	public void storeAnnotation (String name, Integer processId, String version, String nat_type, InputStream content,
			Boolean isNew) 
	throws SQLException, ExceptionDao, ExceptionAnntotationName {
		Connection conn = null;
		PreparedStatement stmtp = null;
		Statement stmt = null ;
		String query = null;	
		ResultSet rs = null;
		String cpf_uri = null;
		Integer npf_uri = null;
		try {
			conn = this.getConnection();
			// retrieve uri of the associated canonical process
			query = " select " + ConstantDB.ATTR_URI
			+ " from " + ConstantDB.TABLE_CANONICALS
			+ " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
			+ " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			// should return one tuple only
			if (rs.next()) {
				cpf_uri = rs.getString(1);
			} else {
				throw new ExceptionDao ("Cannot retrieve canonical.");
			}
			// retrieve uri of the associated native process
			rs.close(); stmt.close();
			query = " select " + ConstantDB.ATTR_URI
			+ " from " + ConstantDB.TABLE_NATIVES
			+ " where " + ConstantDB.ATTR_CANONICAL + " = '" + cpf_uri + "'";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			// should return one tuple only
			if (rs.next()) {
				npf_uri = rs.getInt(1);
			} else {
				throw new ExceptionDao ("Cannot retrieve native.");
			}
			if (isNew) {
				// store new annotation
				query = " insert into " + ConstantDB.TABLE_ANNOTATIONS
				+ "(" + ConstantDB.ATTR_NATIVE 
				+ "," + ConstantDB.ATTR_CANONICAL
				+ "," + ConstantDB.ATTR_NAME
				+ "," + ConstantDB.ATTR_CONTENT + ") values (?,?,?,?) ";
				stmtp = conn.prepareStatement(query);
				stmtp.setInt(1, npf_uri);
				stmtp.setString(2, cpf_uri);
				stmtp.setString(3, name);
				stmtp.setString(4, inputStream2String(content));
				stmtp.executeUpdate();
			} else {
				// update annotation
				query = " update " + ConstantDB.TABLE_ANNOTATIONS
				+ " set " + ConstantDB.ATTR_CONTENT + " = ? "
				+ " where " + ConstantDB.ATTR_CANONICAL + " = ? "
				+ " and "   + ConstantDB.ATTR_NAME + " = ? " ;
				stmtp = conn.prepareStatement(query);
				stmtp.setString(1, inputStream2String(content));
				stmtp.setString(2, cpf_uri);
				stmtp.setString(3, name);
				stmtp.executeUpdate();
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			if (e.getErrorCode()==ConstantDB.ERROR_UNIQUE) {
				throw new ExceptionAnntotationName ("Annotation name already exists");
			} else {
				throw new ExceptionDao ("SQL error: " + e.getMessage() + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error: " + e.getMessage() + "\n");
		} finally {
			Release(conn, stmtp, null);
			Release(conn, stmt, rs);
		}
	}
}

