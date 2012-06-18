package org.apromore.dao.dao;

import de.epml.TypeEPML;
import org.apromore.common.ConstantDB;
import org.apromore.common.Constants;
import org.apromore.exception.ExceptionAnntotationName;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExceptionStoreVersion;
import org.apromore.exception.ExceptionSyncNPF;
import org.apromore.model.AnnotationsType;
import org.apromore.model.CanonicalType;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ProcessVersionType;
import org.apromore.model.VersionSummaryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wfmc._2008.xpdl2.Author;
import org.wfmc._2008.xpdl2.Created;
import org.wfmc._2008.xpdl2.Documentation;
import org.wfmc._2008.xpdl2.ModificationDate;
import org.wfmc._2008.xpdl2.PackageHeader;
import org.wfmc._2008.xpdl2.PackageType;
import org.wfmc._2008.xpdl2.RedefinableHeader;
import org.wfmc._2008.xpdl2.Version;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ProcessDao extends BasicDao {

    private static Logger LOGGER = LoggerFactory.getLogger(ProcessDao.class);

    private static ProcessDao instance;


    public ProcessDao() throws Exception {
        super();
    }

    public static ProcessDao getInstance() throws ExceptionDao {
        if (instance == null) {
            try {
                instance = new ProcessDao();
            } catch (Exception e) {
                throw new ExceptionDao("Error: not able to get instance for DAO: " + e.getMessage() + "\n");
            }
        }
        return instance;
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
                throw new ExceptionDao("Error: cannot retrieve date.");
            }
            today = rs.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmt, rs);
        }
        return today;
    }

    /**
     * Return an inputstream which is the result of writing parameters in anf_xml.
     *
     * @return
     * @throws javax.xml.bind.JAXBException
     */
    private InputStream copyParam2ANF(InputStream anf_xml, String name) throws JAXBException {
        InputStream res = null;
        JAXBContext jc = JAXBContext.newInstance("org.apromore.anf");
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<org.apromore.anf.AnnotationsType> rootElement = (JAXBElement<org.apromore.anf.AnnotationsType>) u.unmarshal(anf_xml);
        org.apromore.anf.AnnotationsType annotations = rootElement.getValue();
        annotations.setName(name);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
        m.marshal(rootElement, xpdl_xml);
        res = new ByteArrayInputStream(xpdl_xml.toByteArray());

        return res;
    }

    /**
     * Return an inputstream which is cpf_xml where attributes are set to parameter values
     *
     * @param cpf_xml
     * @param processName
     * @param version
     * @param username
     * @param creationDate
     * @param lastUpdate
     * @return InputStream
     * @throws javax.xml.bind.JAXBException
     */
    private InputStream copyParam2CPF(InputStream cpf_xml,
                                      String cpf_uri,
                                      String processName,
                                      String version, String username, String creationDate,
                                      String lastUpdate) throws JAXBException {
        InputStream res = null;

        JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement<org.apromore.cpf.CanonicalProcessType> rootElement = (JAXBElement<org.apromore.cpf.CanonicalProcessType>) u.unmarshal(cpf_xml);
        org.apromore.cpf.CanonicalProcessType cpf = rootElement.getValue();
        cpf.setAuthor(username);
        cpf.setName(processName);
        cpf.setVersion(version);
        cpf.setCreationDate(creationDate);
        cpf.setModificationDate(lastUpdate);
        cpf.setUri(cpf_uri);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream xpdl_xml = new ByteArrayOutputStream();
        m.marshal(rootElement, xpdl_xml);
        res = new ByteArrayInputStream(xpdl_xml.toByteArray());
        return res;
    }

    /**
     * Modify pkg (npf of type xpdl) with parameters values if not null.
     *
     * @param pkg
     * @param processName
     * @param version
     * @param username
     * @param creationDate
     * @param lastUpdate
     * @return
     */
    private void copyParam2xpdl(PackageType pkg, String processName, String version, String username,
                                String creationDate, String lastUpdate) {

        if (pkg.getRedefinableHeader() == null) {
            RedefinableHeader header = new RedefinableHeader();
            pkg.setRedefinableHeader(header);
            Version v = new Version();
            header.setVersion(v);
            Author a = new Author();
            header.setAuthor(a);
        } else {
            if (pkg.getRedefinableHeader().getVersion() == null) {
                Version v = new Version();
                pkg.getRedefinableHeader().setVersion(v);
            }
            if (pkg.getRedefinableHeader().getAuthor() == null) {
                Author a = new Author();
                pkg.getRedefinableHeader().setAuthor(a);
            }
        }
        if (pkg.getPackageHeader() == null) {
            PackageHeader pkgHeader = new PackageHeader();
            pkg.setPackageHeader(pkgHeader);
            Created created = new Created();
            pkgHeader.setCreated(created);
            ModificationDate modifDate = new ModificationDate();
            pkgHeader.setModificationDate(modifDate);
            Documentation doc = new Documentation();
            pkgHeader.setDocumentation(doc);
        } else {
            if (pkg.getPackageHeader().getCreated() == null) {
                Created created = new Created();
                pkg.getPackageHeader().setCreated(created);
            }
            if (pkg.getPackageHeader().getModificationDate() == null) {
                ModificationDate modifDate = new ModificationDate();
                pkg.getPackageHeader().setModificationDate(modifDate);
            }
            if (pkg.getPackageHeader().getDocumentation() == null) {
                Documentation doc = new Documentation();
                pkg.getPackageHeader().setDocumentation(doc);
            }
        }
        if (processName != null) pkg.setName(processName);
        if (version != null) pkg.getRedefinableHeader().getVersion().setValue(version);
        if (username != null) pkg.getRedefinableHeader().getAuthor().setValue(username);
        if (creationDate != null) pkg.getPackageHeader().getCreated().setValue(creationDate);
        if (lastUpdate != null) pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
    }

//    /**
//     * Retrieve annotation named annotationNamed (if exist) associated with the process version
//     * identified by <processId, version>
//     *
//     * @param processId
//     * @param version
//     * @param annotationName
//     * @return TODO needs to be modified because of annotations
//     */
//    public String getAnnotation(Integer processId, String version, String annotationName) throws ExceptionDao {
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        String query = null;
//        try {
//            conn = this.getConnection();
//            stmt = conn.createStatement();
//            query = " select " + " A." + ConstantDB.ATTR_CONTENT
//                    + " from " + ConstantDB.TABLE_ANNOTATIONS + " A "
//                    + " join " + ConstantDB.TABLE_CANONICALS + " C "
//                    + " on (" + " A." + ConstantDB.ATTR_CANONICAL + " = " + " C." + ConstantDB.ATTR_URI + ")"
//                    + " where " + " C." + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
//                    + " and " + " C." + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'"
//                    + " and " + " A." + ConstantDB.ATTR_NAME + " = '" + annotationName + "'";
//            rs = stmt.executeQuery(query);
//            if (rs.next()) {
//                return rs.getString(1);
//            } else {
//                throw new ExceptionDao("Cannot retrieve annotation file (" + annotationName + ")");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
//        } finally {
//            Release(conn, stmt, rs);
//        }
//
//    }

//    /**
//     * Return the canonical identified by <processId, version>
//     *
//     * @param processId
//     * @param version
//     * @return
//     */
//    public String getCanonical(Integer processId, String version) throws ExceptionDao {
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        String query = null;
//        try {
//            conn = this.getConnection();
//            stmt = conn.createStatement();
//            query = " select " + ConstantDB.ATTR_CONTENT
//                    + " from " + ConstantDB.TABLE_CANONICALS
//                    + " where " + ConstantDB.ATTR_PROCESSID + " = " + processId.toString()
//                    + " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
//            rs = stmt.executeQuery(query);
//            if (rs.next()) {
//                return rs.getString(1);
//            } else {
//                throw new ExceptionDao("Canonical not found.");
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
//        } finally {
//            Release(conn, stmt, rs);
//        }
//    }

    /**
     * Store a native format for a process version whose canonical already exists.
     *
     * @param nativeType
     * @param processId
     * @param version
     * @param native_xml
     * @throws java.sql.SQLException
     */
    // orphan. To be checked.
    public void storeNative(String nativeType, int processId, String version,
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
                    + " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'";
            rs1 = stmt1.executeQuery(query);
            if (rs1.next()) {
                canonical = rs1.getInt(1);
            } else {
                throw new ExceptionDao("Canonical process not found.");
            }
            stmt1.close();
            rs1.close();
            String native_string = inputStream2String(native_xml);

            /* does a native already exist for this canonical for this native type ?
                */
            query = " select " + "N." + ConstantDB.ATTR_URI
                    + " from " + ConstantDB.TABLE_NATIVES + " N "
                    + " join " + ConstantDB.TABLE_CANONICALS + " C "
                    + " on (" + "N." + ConstantDB.ATTR_URI + " = " + "C." + ConstantDB.ATTR_CANONICAL + ")"
                    + " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
                    + " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + version + "'"
                    + " and " + ConstantDB.ATTR_NAT_TYPE + " = '" + nativeType + "'";
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
                        + ConstantDB.ATTR_NAT_TYPE + ","
                        + ConstantDB.ATTR_CANONICAL + ")"
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
            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmt1, rs1);
            stmt2.close();
        }

    }

    /**
     * Return the inputstream as a string. The inputstream must be positioned so
     * it can be read.
     *
     * @param is
     * @return
     * @throws java.io.IOException
     */
    public String inputStream2String(InputStream is) throws IOException, ExceptionDao {
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
     * Store in the database a new version for the process whose meta date are in editSession.
     * This call is correlated with the
     * edit session whose code is editSessionCode.
     * Data associated with the version are in the NPF inputstream.
     * If the version does not exist already the new version is derived from the
     * version preVersion, for the same process, otherwise previous values
     * are overridden.
     *
     * @param editSessionCode
     * @param editSession
     * @param npf_is
     * @param cpf_is
     * @param apf_is
     */
    public void storeVersion(int editSessionCode, EditSessionType editSession,
                             String cpf_uri, InputStream npf_is, InputStream cpf_is, InputStream apf_is)
            throws ExceptionDao, SQLException, ExceptionSyncNPF, ExceptionStoreVersion {
        String query;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String newVersion = editSession.getVersionName();
            String creationDate = editSession.getCreationDate();
            String lastUpdate = editSession.getLastUpdate();
            String username = editSession.getUsername();
            String processName = editSession.getProcessName();
            int processId = editSession.getProcessId();
            String nativeType = editSession.getNativeType();
            String preVersion = "TBA";

            conn = this.getConnection();
            query = " select " + ConstantDB.ATTR_VERSION_NAME
                    + " from " + ConstantDB.TABLE_EDIT_SESSIONS
                    + " where " + ConstantDB.ATTR_CODE + " = " + editSessionCode;
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                preVersion = rs.getString(1);
                rs.close();
                stmt.close();
            } else {
                throw new ExceptionStoreVersion("Edit session data not found.");
            }
            if ("0.0".compareTo(preVersion) != 0 && newVersion.compareTo(preVersion) != 0) {
                // version "0.0" is the one created at process creation time. 
                // Should be overridden by the next one.
                // if preVersion != newVersion: try to derive newVersion from preVersion
                InputStream newCpf_is =
                        copyParam2CPF(cpf_is, cpf_uri, processName, newVersion, username, now(), now());
                deriveVersion(cpf_uri, processId, preVersion, newVersion, nativeType, npf_is, newCpf_is, apf_is,
                        editSessionCode, now());
            } else {

                // if preVersion = newVersion
                // check whether preVersion is leaf in the derivation tree
                // if yes, override its previous values, otherwise raise an exception
                //query = " select * from " + ConstantDB.TABLE_DERIVED_VERSIONS
                //        + " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
                //        + " and " + ConstantDB.ATTR_VERSION + " = '" + preVersion + "'";
                query = " select * from " + ConstantDB.TABLE_DERIVED_VERSIONS + " d, "
                        + ConstantDB.TABLE_CANONICALS + " c "
                        + " where d.uri_source_version = " + cpf_uri
                        + " and d.uri_source_version = c.uri "
                        + " and c.version_name = " + preVersion;
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                if (rs.next()) {
                    throw new ExceptionStoreVersion("Version " + preVersion + " cannot be overridden.");
                } else {
                    if ("0.0".compareTo(preVersion) != 0) {
                        newVersion = null;
                    }
                    InputStream newCpf_is =
                            copyParam2CPF(cpf_is, cpf_uri, processName, newVersion, username, creationDate, lastUpdate);
                    overrideVersion(cpf_uri, processId, preVersion, nativeType, npf_is, newCpf_is, apf_is,
                            creationDate, now(), newVersion);
                }
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            throw new ExceptionDao("SQL error: " + e.getMessage());
        } catch (ExceptionSyncNPF e) {
            e.printStackTrace();
            throw new ExceptionSyncNPF(e.getMessage());
        } catch (ExceptionStoreVersion e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            throw new ExceptionStoreVersion(e.getMessage());
        } catch (ExceptionDao e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            throw new ExceptionDao("SQL error: " + e.getMessage());
        } catch (JAXBException e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            throw new ExceptionDao("JAXB error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            throw new ExceptionDao("IO error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) conn.rollback();
            throw new ExceptionDao("SQL error: " + e.getMessage());
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
     *
     * @param processId
     * @param versionName
     * @param nativeType
     *
     * @throws java.sql.SQLException
     */
    private void overrideVersion(String cpf_uri, int processId, String versionName,
                                 String nativeType, InputStream npf_is, InputStream cpf_is,
                                 InputStream apf_is, String creationDate,
                                 String lastUpdate, String initialVersion) throws ExceptionDao, SQLException {

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
                    + " , " + ConstantDB.ATTR_CONTENT + "= ? "
                    + " , " + ConstantDB.ATTR_URI + "= ? "
                    + " where " + ConstantDB.ATTR_PROCESSID + " = ? "
                    + " and " + ConstantDB.ATTR_VERSION_NAME + " = ? ";
            stmtp = conn.prepareStatement(query);
            stmtp.setString(1, lastUpdate);
            stmtp.setString(2, cpf_string);
            stmtp.setString(3, cpf_uri);
            stmtp.setInt(4, processId);
            stmtp.setString(5, versionName);
            int rs1 = stmtp.executeUpdate();
            stmtp.close();
            // update anf and npf
            // delete all natives associated with process version
            query = " delete from " + ConstantDB.TABLE_NATIVES
                    + " where " + ConstantDB.ATTR_CANONICAL + " = ? ";
            stmtp = conn.prepareStatement(query);
            stmtp.setString(1, cpf_uri);
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
                    + ConstantDB.ATTR_NAT_TYPE + ","
                    + ConstantDB.ATTR_CANONICAL + ")"
                    + " values (?,?,?) ";
            stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmtp.setString(1, npf_string);
            stmtp.setString(2, nativeType);
            stmtp.setString(3, cpf_uri);
            Integer rs6 = stmtp.executeUpdate();
            ResultSet keys = stmtp.getGeneratedKeys();
            if (!keys.next()) {
                throw new ExceptionDao("Error: cannot retrieve NPF id.");
            }
            int nat_uri = keys.getInt(1);
            keys.close();
            stmtp.close();

            query = " insert into " + ConstantDB.TABLE_ANNOTATIONS
                    + "(" + ConstantDB.ATTR_NATIVE + ","
                    + ConstantDB.ATTR_CANONICAL + ","
                    + ConstantDB.ATTR_NAME + ","
                    + ConstantDB.ATTR_CONTENT + ")"
                    + " values (?,?,?,?) ";
            stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmtp.setInt(1, nat_uri);
            stmtp.setString(2, cpf_uri);
            stmtp.setString(3, Constants.INITIAL_ANNOTATION);
            stmtp.setString(4, apf_string);
            stmtp.executeUpdate();
            stmtp.close();
            if (initialVersion != null) {
                // rename versionName into initialVersion
                query = " update " + ConstantDB.TABLE_CANONICALS
                        + " set " + ConstantDB.ATTR_VERSION_NAME + " = ? "
                        + " , " + ConstantDB.ATTR_LAST_UPDATE + " = null "
                        + " where " + ConstantDB.ATTR_PROCESSID + " = ? "
                        + " and " + ConstantDB.ATTR_VERSION_NAME + " = ? ";
                stmtp = conn.prepareStatement(query);
                stmtp.setString(1, initialVersion);
                stmtp.setInt(2, processId);
                stmtp.setString(3, versionName);
                stmtp.executeUpdate();
                stmtp.close();
            }
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao(e.getMessage());
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
     *
     * @param processId
     * @param preVersion
     * @param newVersion
     * @param nativeType
     * @param editSessionCode
     * @param creationDate
     * @throws java.io.IOException
     */
    private void deriveVersion(String cpf_uri, Integer processId, String preVersion,
                               String newVersion, String nativeType, InputStream npf_is, InputStream cpf_is,
                               InputStream apf_is, int editSessionCode, String creationDate)
            throws ExceptionDao, SQLException, ExceptionStoreVersion, IOException {
        Connection conn = null, conn1 = null, conn2 = null;
        Statement stmt0 = null, stmt1 = null, stmt2 = null;
        ResultSet rs0 = null, rs1 = null, rs2 = null;
        PreparedStatement stmtp = null;
        String query = null;
        String originalURI = null;
        String annotation = Constants.INITIAL_ANNOTATION;
        String npf_string = inputStream2String(npf_is);
        String apf_string = inputStream2String(apf_is);
        String cpf_string = inputStream2String(cpf_is);
        try {
            conn = this.getConnection();
            // does newVersion already exist?
            query = " select " + ConstantDB.ATTR_URI
                    + " from " + ConstantDB.TABLE_CANONICALS
                    + " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
                    + "   and " + ConstantDB.ATTR_VERSION_NAME + " = '" + newVersion + "'";
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
                        + "," + ConstantDB.ATTR_NAME
                        + "," + ConstantDB.ATTR_CPF
                        + "," + ConstantDB.ATTR_APF
                        + "," + ConstantDB.ATTR_NPF + ")"
                        + " values (?, now(), ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmtp = conn1.prepareStatement(query);
                stmtp.setInt(1, editSessionCode);
                stmtp.setInt(2, processId);
                stmtp.setString(3, preVersion);
                stmtp.setString(4, newVersion);
                stmtp.setString(5, creationDate);
                stmtp.setString(6, creationDate);
                stmtp.setString(7, annotation);
                stmtp.setString(8, cpf_string);
                stmtp.setString(9, apf_string);
                stmtp.setString(10, npf_string);
                stmtp.executeUpdate();
                stmtp.close();
                conn1.commit();
                throw new ExceptionStoreVersion("Version " + newVersion + " already exists.");
            } else {
                // newVersion does not exist. Derive a new version.
                // add a new version of the process identified by processId + newVersion
                String query2 = " insert into " + ConstantDB.TABLE_CANONICALS
                        + "(" + ConstantDB.ATTR_URI + ","
                        + ConstantDB.ATTR_PROCESSID + ","
                        + ConstantDB.ATTR_VERSION_NAME + ","
                        + ConstantDB.ATTR_CREATION_DATE + ","
                        + ConstantDB.ATTR_CONTENT + ")"
                        + " values (?, ?, ?, date_format(now(), '%Y-%m-%dT%k-%i-%s'), ?) ";
                //+ " values (?, ?, str_to_date(?,'%Y-%c-%d %k:%i:%s'), str_to_date(?,'%Y-%c-%d %k:%i:%s'), ?, ?) ";
                stmtp = conn.prepareStatement(query2);
                stmtp.setString(1, cpf_uri);
                stmtp.setInt(2, processId);
                stmtp.setString(3, newVersion);
                stmtp.setString(4, cpf_string);
                stmtp.executeUpdate();
                stmtp.close();

                String query5 = " insert into " + ConstantDB.TABLE_NATIVES
                        + "(" + ConstantDB.ATTR_CONTENT + ","
                        + ConstantDB.ATTR_NAT_TYPE + ","
                        + ConstantDB.ATTR_CANONICAL + ")"
                        + " values (?,?,?) ";
                stmtp = conn.prepareStatement(query5, Statement.RETURN_GENERATED_KEYS);
                //stmt6.setAsciiStream(1, process_xml);
                stmtp.setString(1, npf_string);
                stmtp.setString(2, nativeType);
                stmtp.setString(3, cpf_uri);
                stmtp.executeUpdate();
                ResultSet keys = stmtp.getGeneratedKeys();
                if (!keys.next()) {
                    throw new ExceptionDao("Error: cannot retrieve generated key.");
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
                stmtp.setInt(1, npfId);
                stmtp.setString(2, cpf_uri);
                stmtp.setString(3, annotation);
                stmtp.setString(4, apf_string);
                stmtp.executeUpdate();
                stmtp.close();

                conn2 = getConnection();
                query5 = " SELECT " + ConstantDB.ATTR_URI
                        + " FROM " + ConstantDB.TABLE_CANONICALS
                        + " where " + ConstantDB.ATTR_PROCESSID + " = " + processId
                        + " group by " + ConstantDB.ATTR_PROCESSID
                        + " having min(" + ConstantDB.ATTR_CREATION_DATE + ")";
                stmt2 = conn.createStatement();
                rs2 = stmt2.executeQuery(query5);
                if (rs2.next()) {
                    originalURI = rs2.getString(ConstantDB.ATTR_URI);
                    stmt2.close();

                    query2 = " insert into " + ConstantDB.TABLE_DERIVED_VERSIONS
                            + "(uri_source_version, uri_derived_version)"
                            + " values (?,?)";
                    stmtp = conn.prepareStatement(query2);
                    stmtp.setString(1, originalURI);
                    stmtp.setString(2, cpf_uri);
                    stmtp.executeUpdate();
                    stmtp.close();
                }
//                query2 = " insert into " + ConstantDB.TABLE_DERIVED_VERSIONS
//                        + "(" + ConstantDB.ATTR_PROCESSID + ","
//                        + ConstantDB.ATTR_VERSION + ","
//                        + ConstantDB.ATTR_DERIVED_VERSION + ")"
//                        + " values (?,?,?)";


                // modify version name in edit session
                query = "update " + ConstantDB.TABLE_EDIT_SESSIONS
                        + " set " + ConstantDB.ATTR_VERSION_NAME + " = ? "
                        + " where " + ConstantDB.ATTR_CODE + " = ? ";
                stmtp = conn.prepareStatement(query);
                stmtp.setString(1, newVersion);
                stmtp.setInt(2, editSessionCode);
                stmtp.executeUpdate();
                stmtp.close();
                conn.commit();
            }
        } catch (ExceptionDao e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao(e.getMessage());
        } catch (ExceptionStoreVersion e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionStoreVersion(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("SQL error: " + e.getMessage());
        } finally {
            Release(conn, stmt0, rs0);
            Release(conn1, stmt1, rs1);
            Release(conn2, stmt2, rs2);
        }
    }


    /**
     * For each given process, delete each of its given versions from the database.
     *
     * @param processes
     * @throws java.sql.SQLException
     */
    public void deleteProcessVersions(HashMap<Integer, List<String>> processes) throws ExceptionDao, SQLException {
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
                    String cpf_uri;
                    query = " select " + ConstantDB.ATTR_URI
                            + " from " + ConstantDB.TABLE_CANONICALS
                            + " where " + ConstantDB.ATTR_PROCESSID + " = " + pId.toString()
                            + " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + v + "'";
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(query);
                    if (rs.next()) {
                        cpf_uri = rs.getString(1);
                        stmt.close();
                        rs.close();
                    } else {
                        stmt.close();
                        rs.close();
                        throw new ExceptionDao("Version " + v + " for process " + pId.toString() + " not found. \n");
                    }

                    /* delete process version identified by <p, v>
                     * retrieve r in derived_versions such as r[processId]=p */
                    query = " select uri_source_version"
                            + " from " + ConstantDB.TABLE_DERIVED_VERSIONS
                            + " where uri_source_version = " + cpf_uri;
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(query);
                    if (!rs.next()) {
                        /* r doesn't exist: v is the only version that exists for p,
                         * delete p from processes (thanks to FKs, related tuple in
                         * process_versions will be deleted too) */
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
                         * Retrieve r2 in derived_versions such as r2[derived_version,processId] = <v,p> */
                        query = " select uri_derived_version"
                                + " from " + ConstantDB.TABLE_DERIVED_VERSIONS
                                + " where uri_derived_version = " + cpf_uri;
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(query);
                        if (rs.next()) {
                            /* <p,v> has children. Delete <p, v> */
                            query = " delete from " + ConstantDB.TABLE_DERIVED_VERSIONS
                                    + " where uri_derived_version = " + cpf_uri;
                            stmtp = conn.prepareStatement(query);
                            int r = stmtp.executeUpdate();
                            stmtp.close();
                            /* Update all child versions of <p, v> (if any).
                             * Replace, for each r3 in derived_version
                             * such as r3[version,processId] = <v,p>, replace v with r2[version] */
                            query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS
                                    + " set uri_derived_version = '" + rs.getString(1) + "'"
                                    + " where uri_source_version = " + cpf_uri;
                            stmtp = conn.prepareStatement(query);
                            r = stmtp.executeUpdate();
                            stmtp.close();
                        } else {
                            // <p, v> has no children. Delete <p,v> 
                            query = " delete from " + ConstantDB.TABLE_DERIVED_VERSIONS
                                    + " where uri_source_version = " + cpf_uri;
                            stmtp = conn.prepareStatement(query);
                            int r = stmtp.executeUpdate();
                            stmtp.close();
                        }
                        /* delete the process version. Thanks to foreign keys, canonical,
                         * natives and annotations will be deleted on cascade */
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
            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("Error " + e.getMessage() + "\n");
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
            if (ranking == null) {
                stmtp.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmtp.setInt(2, Integer.parseInt(ranking));
            }
            stmtp.setInt(3, processId);
            stmtp.setString(4, preVersion);
            stmtp.executeUpdate();
            stmtp.close();

//            // update derived_versions
//            query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS
//                    + " set " + ConstantDB.ATTR_VERSION + " = ? "
//                    + " where " + ConstantDB.ATTR_VERSION + " = ? ";
//            stmtp = conn.prepareStatement(query);
//            stmtp.setString(1, newVersion);
//            stmtp.setString(2, preVersion);
//            stmtp.executeUpdate();
//            stmtp.close();
//            query = " update " + ConstantDB.TABLE_DERIVED_VERSIONS
//                    + " set " + ConstantDB.ATTR_DERIVED_VERSION + " = ? "
//                    + " where " + ConstantDB.ATTR_DERIVED_VERSION + " = ? ";
//            stmtp = conn.prepareStatement(query);
//            stmtp.setString(1, newVersion);
//            stmtp.setString(2, preVersion);
//            stmtp.executeUpdate();

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
                if (nativeType.compareTo(Constants.XPDL_2_1) == 0) {
                    jc = JAXBContext.newInstance(Constants.XPDL2_CONTEXT);
                    u = jc.createUnmarshaller();
                    JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(npf);
                    PackageType npf_o = rootElement.getValue();
                    copyParam2xpdl(npf_o, processName, newVersion, username, null, lastUpdate);
                    m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<PackageType> rootnpf = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(npf_o);
                    m.marshal(rootnpf, npf_xml);
                } else if (nativeType.compareTo("EPML 2.0") == 0) {
                    jc = JAXBContext.newInstance("de.epml");
                    u = jc.createUnmarshaller();
                    JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(npf);
                    TypeEPML npf_o = rootElement.getValue();

                    m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//                    JAXBElement<TypeEPML> rootnpf = new de.epml.ObjectFactory().createEpml(npf_o);
//                    m.marshal(rootnpf, npf_xml);
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
            stmt.close();
            rs.close();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmtp, null);
        }
    }

    /**
     * Create a process whose name is processName, first version is versionName. The process is created by
     * username and its cpf is cpf_is. Doesn't have neither anf nor npf.
     *
     * @param processName
     * @param versionName
     * @param username
     * @param cpf_is
     * @return processSummary
     * @throws java.sql.SQLException
     */
    public ProcessSummaryType
    storeCpf(String processName, String versionName, String domain, String username,
             InputStream cpf_is, String cpf_uri,
             Map<Integer, String> sources)
            throws SQLException, ExceptionDao {
        Connection conn = null;
        Statement stmt0 = null;
        PreparedStatement stmtp = null;
        ResultSet rs0 = null, keys = null;
        String query = null;
        ProcessSummaryType process = new ProcessSummaryType();
        VersionSummaryType first_version = new VersionSummaryType();
        process.getVersionSummaries().clear();
        process.getVersionSummaries().add(first_version);
        try {
            conn = this.getConnection();
            // store process details to get processId.
            query = " insert into " + ConstantDB.TABLE_PROCESSES
                    + "(" + ConstantDB.ATTR_NAME + ","
                    + ConstantDB.ATTR_OWNER + ","
                    + ConstantDB.ATTR_DOMAIN + ")"
                    + " values (?, ?, ?) ";
            stmtp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmtp.setString(1, processName);
            stmtp.setString(2, username);
            stmtp.setString(3, domain);
            int rs1 = stmtp.executeUpdate();
            keys = stmtp.getGeneratedKeys();
            if (!keys.next()) {
                throw new ExceptionDao("Error: cannot retrieve generated key.");
            }
            Integer processId = keys.getInt(1);
            keys.close();
            stmtp.close();

            // Store informations given as parameters in both NPF and ANF
            // creationDate might be null or empty. 
            String creationDate = now();
            String lastUpdate = "";
            String documentation = "";
            InputStream sync_cpf = copyParam2CPF(cpf_is, cpf_uri, processName, versionName,
                    username, creationDate, lastUpdate);
            String cpf_string = inputStream2String(sync_cpf).trim();
            query = " insert into " + ConstantDB.TABLE_CANONICALS
                    + "(" + ConstantDB.ATTR_URI + ","
                    + ConstantDB.ATTR_PROCESSID + ","
                    + ConstantDB.ATTR_VERSION_NAME + ","
                    + ConstantDB.ATTR_CREATION_DATE + ","
                    + ConstantDB.ATTR_LAST_UPDATE + ","
                    + ConstantDB.ATTR_CONTENT + ","
                    + ConstantDB.ATTR_DOCUMENTATION + ")"
                    + " values (?, ?, ?, ?, ?, ?, ?) ";
            //+ " values (?, ?, str_to_date(?,'%Y-%c-%d %k:%i:%s'), str_to_date(?,), ?, ?) ";
            stmtp = conn.prepareStatement(query);
            stmtp.setString(1, cpf_uri);
            stmtp.setInt(2, processId);
            stmtp.setString(3, versionName);
            stmtp.setString(4, creationDate);
            stmtp.setString(5, lastUpdate);
            stmtp.setString(6, cpf_string);
            stmtp.setString(7, documentation);
            Integer rs2 = stmtp.executeUpdate();
            stmtp.close();
            process.setId(processId);
            process.setName(processName);
            process.setOwner(username);
            process.setLastVersion(versionName);
            first_version.setName(versionName);
            first_version.setLastUpdate(lastUpdate);
            first_version.setCreationDate(creationDate);

            Iterator sourcesIterator = sources.entrySet().iterator();

            while (sourcesIterator.hasNext()) {
                Entry<Integer, String> item = (Entry<Integer, String>) sourcesIterator.next();
                query = " select " + ConstantDB.ATTR_URI
                        + " from " + ConstantDB.TABLE_CANONICALS
                        + " where " + ConstantDB.ATTR_PROCESSID + " = " + item.getKey()
                        + " and " + ConstantDB.ATTR_VERSION_NAME + " = '" + item.getValue() + "'";
                stmt0 = conn.createStatement();
                rs0 = stmt0.executeQuery(query);

                query = " insert into " + ConstantDB.TABLE_MERGED_VERSIONS
                        + "(" + ConstantDB.ATTR_URI_MERGED + ", "
                        + ConstantDB.ATTR_URI_SOURCE + ")"
                        + " values (?, ?)";
                stmtp = conn.prepareStatement(query);
                stmtp.setString(1, cpf_uri);
                rs0.next();
                stmtp.setString(2, rs0.getString(1));
                rs2 = stmtp.executeUpdate();
                stmt0.close();
                rs0.close();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmt0, rs0);
        }
        return process;
    }

    /**
     * Store an annotation: it is associated with the process version identified by
     * (processId, version), and native process identified by the process version uri and
     * the native type nat_type. Its content is content. If isNew then a new annotation
     * is stored (its name must not exist for the given process  version), otherwise
     * the existing annotation is overridden.
     *
     * @param name
     * @param processId
     * @param version
     * @param nat_type
     * @param content
     * @param isNew
     * @throws java.sql.SQLException
     */
    public void storeAnnotation(String name, Integer processId, String version,
                                String cpfUri, String nat_type, InputStream content,
                                Boolean isNew)
            throws SQLException, ExceptionDao, ExceptionAnntotationName {
        Connection conn = null;
        PreparedStatement stmtp = null;
        Statement stmt = null;
        String query = null;
        ResultSet rs = null;
        Integer npf_uri = null;
        try {
            conn = this.getConnection();
            query = " select " + ConstantDB.ATTR_URI
                    + " from " + ConstantDB.TABLE_NATIVES
                    + " where " + ConstantDB.ATTR_CANONICAL + " = '" + cpfUri + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            // should return one tuple only
            if (rs.next()) {
                npf_uri = rs.getInt(1);
            } else {
                throw new ExceptionDao("Cannot retrieve native.");
            }
            String anf_str = inputStream2String(copyParam2ANF(content, name));
            if (isNew) {
                // store new annotation
                query = " insert into " + ConstantDB.TABLE_ANNOTATIONS
                        + "(" + ConstantDB.ATTR_NATIVE
                        + "," + ConstantDB.ATTR_CANONICAL
                        + "," + ConstantDB.ATTR_NAME
                        + "," + ConstantDB.ATTR_CONTENT + ") values (?,?,?,?) ";
                stmtp = conn.prepareStatement(query);
                stmtp.setInt(1, npf_uri);
                stmtp.setString(2, cpfUri);
                stmtp.setString(3, name);
                stmtp.setString(4, anf_str);
                stmtp.executeUpdate();
            } else {
                // update annotation
                query = " update " + ConstantDB.TABLE_ANNOTATIONS
                        + " set " + ConstantDB.ATTR_CONTENT + " = ? "
                        + " where " + ConstantDB.ATTR_CANONICAL + " = ? "
                        + " and " + ConstantDB.ATTR_NAME + " = ? ";
                stmtp = conn.prepareStatement(query);
                stmtp.setString(1, anf_str);
                stmtp.setString(2, cpfUri);
                stmtp.setString(3, name);
                stmtp.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();
            if (e.getErrorCode() == ConstantDB.ERROR_UNIQUE) {
                throw new ExceptionAnntotationName("Annotation name already exists");
            } else {
                throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmtp, null);
            Release(conn, stmt, rs);
        }
    }

    /**
     * Return all canonicals
     */
    public List<CanonicalType> getCanonicals(
            List<ProcessVersionType> pvIds, Boolean latestVersions)
            throws ExceptionDao {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
        List<CanonicalType> toReturn = new ArrayList<CanonicalType>();
        try {
            conn = this.getConnection();
            stmt = conn.createStatement();
            query = " select " + ConstantDB.ATTR_PROCESSID + ", " +
                    ConstantDB.ATTR_VERSION_NAME + ", " + ConstantDB.ATTR_CONTENT
                    + " from " + ConstantDB.TABLE_CANONICALS
                    + " where true ";
            if (pvIds.size() > 0) {
                String condition = " and " + ConstantDB.ATTR_PROCESSID + " = " + pvIds.get(0).getProcessId();
                condition += " and " + ConstantDB.ATTR_VERSION_NAME + " = " + pvIds.get(0).getVersionName();
                for (int i = 1; i < pvIds.size(); i++) {
                    condition += " or " + ConstantDB.ATTR_PROCESSID + " = " + pvIds.get(i).getProcessId();
                    condition += " and " + ConstantDB.ATTR_VERSION_NAME + " = " + pvIds.get(i).getVersionName();
                }
                query += condition;
            }
            if (latestVersions) {
                query += " and "
                        + "(" + ConstantDB.ATTR_PROCESSID + ", "
                        + ConstantDB.ATTR_CREATION_DATE + ") in "
                        + " (select " + ConstantDB.ATTR_PROCESSID + ", "
                        + "max(" + ConstantDB.ATTR_CREATION_DATE + ")"
                        + " from " + ConstantDB.TABLE_CANONICALS
                        + " group by " + ConstantDB.ATTR_PROCESSID + ")"
                ;
            }
            query += " order by " + ConstantDB.ATTR_PROCESSID + ", " +
                    ConstantDB.ATTR_VERSION_NAME;
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                CanonicalType cpf = new CanonicalType();
                cpf.setProcessId(rs.getInt(1));
                cpf.setVersionName(rs.getString(2));
                String cpf_str = rs.getString(3);
                ByteArrayDataSource sourceCpf = new ByteArrayDataSource(cpf_str, "text/xml");
                cpf.setCpf(new DataHandler(sourceCpf));
                toReturn.add(cpf);
            }
            return toReturn;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmt, rs);
        }
    }

    /**
     * Returns a list of process summaries whose id are in processIds. For each of which only versions in versionNames
     * are returned, given score is associated with summary.
     *
     * @param processIds
     * @param versionNames
     * @return
     */
    public ProcessSummariesType getProcessSummaries(
            List<Integer> processIds, List<String> versionNames, List<Double> scores) throws ExceptionDao {

        ProcessSummariesType toReturn =
                new ProcessSummariesType();

        if (processIds.size() != 0 && versionNames.size() == processIds.size() && scores.size() == processIds.size()) {
            Connection conn = null;
            Statement stmt = null, stmtV = null, stmtA = null, stmtB = null;
            ResultSet rs = null, rsV = null, rsA = null, rsB = null;
            String query = null;

            // build a list of <integer, string> such as <p, LV> belongs to it
            // iff for each v in LV, let j be the index of v in versionNames, p=processIds[j]
            HashMap<Integer, String> processes = new HashMap<Integer, String>();
            processes.put(processIds.get(0), "'" + versionNames.get(0) + "'");
            for (int i = 1; i < processIds.size(); i++) {
                if (processes.keySet().contains(processIds.get(i))) {
                    String l = processes.get(processIds.get(i)) + ", '" + versionNames.get(i) + "'";
                    processes.remove(processIds.get(i));
                    processes.put(processIds.get(i), l);
                } else {
                    processes.put(processIds.get(i), "'" + versionNames.get(i) + "'");
                }
            }
            try {
                conn = this.getConnection();
                stmt = conn.createStatement();
                for (Integer k : processes.keySet()) {
                    query = " SELECT distinct " + ConstantDB.ATTR_PROCESSID + ", "
                            + ConstantDB.ATTR_NAME + ", "
                            + ConstantDB.ATTR_DOMAIN + ", "
                            + ConstantDB.ATTR_ORIGINAL_TYPE + ", "
                            + " coalesce(R." + ConstantDB.ATTR_RANKING + ",''),"
                            + ConstantDB.ATTR_OWNER
                            + " FROM " + ConstantDB.TABLE_PROCESSES
                            + " natural join " + ConstantDB.TABLE_CANONICALS + " C "
                            + "    join " + ConstantDB.VIEW_PROCESS_RANKING + " R using (" + ConstantDB.ATTR_PROCESSID + ")"
                            + " where " + ConstantDB.ATTR_PROCESSID + " = " + k;
                    query += " order by " + ConstantDB.ATTR_PROCESSID;
                    rs = stmt.executeQuery(query);
                    while (rs.next()) {
                        int processId = rs.getInt(1);
                        ProcessSummaryType processSummary =
                                new ProcessSummaryType();
                        toReturn.getProcessSummary().add(processSummary);
                        processSummary.setId(processId);
                        processSummary.setName(rs.getString(2));
                        processSummary.setDomain(rs.getString(3));
                        processSummary.setOriginalNativeType(rs.getString(4));
                        processSummary.setRanking(rs.getString(5));
                        processSummary.setOwner(rs.getString(6));
                        stmtV = conn.createStatement();
                        query = " select " + ConstantDB.ATTR_VERSION_NAME + ", "
                                + ConstantDB.ATTR_PROCESSID + ","
                                //+ "date_format(" + ConstantDB.ATTR_CREATION_DATE + ", '%d/%c/%Y %k:%i:%f')" + ",  "
                                //+ "date_format(" + ConstantDB.ATTR_LAST_UPDATE  + ", '%d/%c/%Y %k:%i:%f')" + ",  "
                                + ConstantDB.ATTR_CREATION_DATE + ", "
                                + ConstantDB.ATTR_LAST_UPDATE + ", "
                                + " coalesce(" + ConstantDB.ATTR_RANKING + ",''),"
                                + ConstantDB.ATTR_DOCUMENTATION
                                + " from " + ConstantDB.TABLE_CANONICALS
                                + " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId
                                + " and    " + ConstantDB.ATTR_VERSION_NAME + " in (" + processes.get(k) + ")"
                                + " order by  " + ConstantDB.ATTR_CREATION_DATE;
                        rsV = stmtV.executeQuery(query);
                        String lastVersion = "";
                        while (rsV.next()) {
                            VersionSummaryType version =
                                    new VersionSummaryType();
                            String vName = rsV.getString(1);
                            version.setName(vName);
                            Integer pId = rsV.getInt(2);
                            lastVersion = vName;
                            version.setCreationDate(rsV.getString(3));
                            version.setLastUpdate(rsV.getString(4));
                            version.setRanking(rsV.getString(5));
                            // retrieve score associated with <pId, vName> in scores
                            for (int i = 0; i < scores.size(); i++) {
                                if (processIds.get(i).equals(pId) && vName.compareTo(versionNames.get(i)) == 0) {
                                    version.setScore(scores.get(i));
                                }
                            }
                            processSummary.getVersionSummaries().add(version);
                            // for each version (a canonical process), retrieve for each of its native process
                            // the list of corresponding annotations
                            query = " select " + "N." + ConstantDB.ATTR_URI + ", N." + ConstantDB.ATTR_NAT_TYPE
                                    + " from " + ConstantDB.TABLE_CANONICALS + " C "
                                    + " join " + ConstantDB.TABLE_NATIVES + " N "
                                    + " on (" + "C." + ConstantDB.ATTR_URI + "=" + "N." + ConstantDB.ATTR_CANONICAL + ")"
                                    + " where " + "C." + ConstantDB.ATTR_PROCESSID + " = " + processId
                                    + "  and " + "C." + ConstantDB.ATTR_VERSION_NAME + " = '" + rsV.getString(1) + "'";
                            stmtA = conn.createStatement();
                            rsA = stmtA.executeQuery(query);
                            while (rsA.next()) {
                                // For each native, retrieve annotations
                                AnnotationsType annotations =
                                        new AnnotationsType();
                                version.getAnnotations().add(annotations);
                                annotations.setNativeType(rsA.getString(2));
                                query = " select " + ConstantDB.ATTR_NAME
                                        + " from " + ConstantDB.TABLE_ANNOTATIONS
                                        + " where " + ConstantDB.ATTR_NATIVE + " = " + rsA.getInt(1);
                                stmtB = conn.createStatement();
                                rsB = stmtB.executeQuery(query);
                                while (rsB.next()) {
                                    annotations.getAnnotationName().add(rsB.getString(1));
                                }
                                rsB.close();
                                stmtB.close();
                            }
                            rsA.close();
                            stmtA.close();
                        }
                        processSummary.setLastVersion(lastVersion);
                        rsV.close();
                        stmtV.close();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new ExceptionDao("SQL error: " + e.getMessage() + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                throw new ExceptionDao("Error: " + e.getMessage() + "\n");
            } finally {
                Release(conn, stmt, rs);
            }
        }
        return toReturn;
    }

    /**
     * Return the cpf_uri associated with canonical identified by processId, version
     *
     * @param processId
     * @param version
     * @return
     */
    public String getCpfUri(Integer processId, String version) throws ExceptionDao {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String query = null;
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
                return rs.getString(1);
            } else {
                throw new ExceptionDao("Cannot retrieve canonical.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionDao("Error: " + e.getMessage() + "\n");
        } finally {
            Release(conn, stmt, rs);
        }
    }

}

