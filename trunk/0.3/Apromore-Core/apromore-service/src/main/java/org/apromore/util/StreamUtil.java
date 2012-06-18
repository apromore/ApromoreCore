package org.apromore.util;

import org.apromore.anf.AnnotationsType;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.wfmc._2008.xpdl2.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.bind.*;
import java.io.*;

/**
 * Helps with debugging and seeing the data travel between services.
 */
public class StreamUtil {

    private static final String ANF_URI = "org.apromore.anf";
    private static final String CPF_URI = "org.apromore.cpf";
    private static final String XPDL_URI = "org.wfmc._2008.xpdl2";

    /**
     * Convert a InputStream to a String
     * @param is the inputStream to convert
     * @return the string for that input stream
     */
    public static String convertStreamToString(InputStream is) {
        return inputStream2String(is);
    }

    /**
     * Convert a DataHandler to a String
     * @param dh the DataHandler to convert
     * @return the string for that DataHandler
     */
    public static String convertStreamToString(DataHandler dh) {
        try {
            return inputStream2String(dh.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataHandler: " + e.toString();
        }
    }

    /**
     * Convert a DataHandler to a String
     * @param ds the DataSource to convert
     * @return the string for that DataSource
     */
    public static String convertStreamToString(DataSource ds) {
        try {
            return inputStream2String(ds.getInputStream());
        } catch (IOException e) {
            return "error in readin the DataSource: " + e.toString();
        }
    }


    /**
     * Return an inputstream which is the result of writing parameters in anf_xml.
     *
     * @return The modified input stream.
     * @throws javax.xml.bind.JAXBException if it fails
     */
    @SuppressWarnings("unchecked")
    public static InputStream copyParam2ANF(InputStream anf_xml, String name) throws JAXBException {
        InputStream res;

        JAXBContext jc = JAXBContext.newInstance(ANF_URI);
        Unmarshaller u = jc.createUnmarshaller();

        JAXBElement<AnnotationsType> rootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_xml);
        AnnotationsType annotations = rootElement.getValue();
        annotations.setName(name);

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        m.marshal(rootElement, xml);
        res = new ByteArrayInputStream(xml.toByteArray());

        return res;
    }

    /**
     * Return an input stream which is cpf_xml where attributes are set to parameter values
     *
     * @param cpf_xml      the cpf xml
     * @param cpf_uri      the cpf id for the DB
     * @param processName  the process name
     * @param version      the process version
     * @param username     the user doing the change
     * @param creationDate the date created
     * @param lastUpdate   the updated date
     * @return The modified input stream.
     * @throws javax.xml.bind.JAXBException if it fails
     */
    @SuppressWarnings("unchecked")
    public static InputStream copyParam2CPF(InputStream cpf_xml, Integer cpf_uri, String processName, String version, String username,
            String creationDate, String lastUpdate) throws JAXBException {
        InputStream res;

        JAXBContext jc = JAXBContext.newInstance(CPF_URI);
        Unmarshaller u = jc.createUnmarshaller();

        JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_xml);
        CanonicalProcessType cpf = rootElement.getValue();
        cpf.setAuthor(username);
        cpf.setName(processName);
        cpf.setVersion(version);
        cpf.setCreationDate(creationDate);
        cpf.setModificationDate(lastUpdate);
        cpf.setUri(String.valueOf(cpf_uri));

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        ByteArrayOutputStream xml = new ByteArrayOutputStream();
        m.marshal(rootElement, xml);
        res = new ByteArrayInputStream(xml.toByteArray());
        return res;
    }


    /**
     * Generate a new npf which is the result of writing parameters in process_xml.
     *
     * @param process_xml  the given npf to be synchronised
     * @param nativeType   npf native type
     * @param processName  the process name
     * @param version      the process version
     * @param username     the user doing the change
     * @param creationDate the date created
     * @param lastUpdate   the updated date
     * @return The modified input stream.
     * @throws javax.xml.bind.JAXBException if it fails
     */
    @SuppressWarnings("unchecked")
    public static InputStream copyParam2NPF(InputStream process_xml, String nativeType, String processName, String version, String username,
            String creationDate, String lastUpdate) throws JAXBException {
        InputStream res = null;

        if (nativeType.compareTo(Constants.XPDL_2_1) == 0) {
            JAXBContext jc = JAXBContext.newInstance(XPDL_URI);
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
            copyParam2XPDL(rootElement.getValue(), processName, version, username, creationDate, lastUpdate);

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            m.marshal(rootElement, xml);
            res = new ByteArrayInputStream(xml.toByteArray());
        } else if (nativeType.compareTo(Constants.EPML_2_0) == 0) {
            res = process_xml;
        } else if (nativeType.compareTo("PNML 1.3.2") == 0) {
            res = process_xml;
        }
        return res;
    }

    /**
     * Modify pkg (npf of type xpdl) with parameters values if not null.
     *
     * @param pkg the package to change to
     * @param processName  the process name
     * @param version      the process version
     * @param username     the user doing the change
     * @param creationDate the date created
     * @param lastUpdate   the updated date
     */
    public static void copyParam2XPDL(PackageType pkg, String processName, String version, String username, String creationDate, String lastUpdate) {
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
        if (processName != null) {
            pkg.setName(processName);
        }
        if (version != null) {
            pkg.getRedefinableHeader().getVersion().setValue(version);
        }
        if (username != null) {
            pkg.getRedefinableHeader().getAuthor().setValue(username);
        }
        if (creationDate != null) {
            pkg.getPackageHeader().getCreated().setValue(creationDate);
        }
        if (lastUpdate != null) {
            pkg.getPackageHeader().getModificationDate().setValue(lastUpdate);
        }
    }


    /**
     * Converts an input stream to a string.
     * @param is the input stream
     * @return the String that was the input stream
     */
    public static String inputStream2String(InputStream is) {
        try {
            if (is != null) {
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } finally {
                    is.close();
                }
                return sb.toString();
            }
        } catch (IOException e) {
            return "error in reading the input streams: " + e.toString();
        }
        return "";
    }
}
