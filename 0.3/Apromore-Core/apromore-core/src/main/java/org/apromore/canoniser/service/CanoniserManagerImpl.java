package org.apromore.canoniser.service;

import de.epml.TypeEPML;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.Canonical2EPML;
import org.apromore.canoniser.adapters.Canonical2XPDL;
import org.apromore.canoniser.adapters.EPML2Canonical;
import org.apromore.canoniser.adapters.XPDL2Canonical;
import org.apromore.canoniser.da.CanoniserDataAccessClient;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.ExceptionAdapters;
import org.apromore.exception.ExceptionStore;
import org.apromore.exception.ExceptionVersion;
import org.apromore.model.CanoniseProcessInputMsgType;
import org.apromore.model.CanoniseProcessOutputMsgType;
import org.apromore.model.CanoniseVersionInputMsgType;
import org.apromore.model.CanoniseVersionOutputMsgType;
import org.apromore.model.DeCanoniseProcessInputMsgType;
import org.apromore.model.DeCanoniseProcessOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.GenerateAnnotationInputMsgType;
import org.apromore.model.GenerateAnnotationOutputMsgType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.ResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wfmc._2008.xpdl2.PackageType;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 *
 */
@Service("CanoniserManager")
public class CanoniserManagerImpl implements CanoniserManager {

    private static final Logger LOG = Logger.getLogger(CanoniserManagerImpl.class.getName());

    private CanoniserDataAccessClient client;

    public DeCanoniseProcessOutputMsgType deCanoniseProcess(DeCanoniseProcessInputMsgType payload) {
        LOG.info("Executing operation deCanoniseProcess");
        System.out.println(payload);
        DeCanoniseProcessOutputMsgType res = new DeCanoniseProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            InputStream cpf_is = null, anf_is = null;
            AnnotationsType anf = null;
            DataHandler handler_cpf = payload.getCpf();
            DataHandler handler_anf = payload.getAnf();
            String nativeType = payload.getNativeType();
            int processId = payload.getProcessId();
            String version = payload.getVersion();

            JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
            Unmarshaller u = jc.createUnmarshaller();
            cpf_is = handler_cpf.getInputStream();
            JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_is);
            CanonicalProcessType cpf = rootElement.getValue();
            if (handler_anf != null) {
                anf_is = handler_anf.getInputStream();
                jc = JAXBContext.newInstance("org.apromore.anf");
                u = jc.createUnmarshaller();
                JAXBElement<AnnotationsType> rootAnf = (JAXBElement<AnnotationsType>) u.unmarshal(anf_is);
                anf = rootAnf.getValue();
            }

            ByteArrayOutputStream native_xml = new ByteArrayOutputStream();

            /**
             * native type must be supported by apromore.
             * At the moment: XPDL 2.1 and EPML 2.0
             */
            if (nativeType.compareTo("XPDL 2.1") == 0 || nativeType.compareTo("EPML 2.0") == 0) {
                if (nativeType.compareTo("XPDL 2.1") == 0) {
                    Canonical2XPDL canonical2xpdl;
                    if (anf == null) {
                        canonical2xpdl = new Canonical2XPDL(cpf);
                    } else {
                        canonical2xpdl = new Canonical2XPDL(cpf, anf);
                    }
                    jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<PackageType> rootxpdl = new org.wfmc._2008.xpdl2.ObjectFactory().createPackage(canonical2xpdl.getXpdl());
                    m.marshal(rootxpdl, native_xml);

                } else if (nativeType.compareTo("EPML 2.0") == 0) {
                    Canonical2EPML canonical2epml;
                    if (anf == null) {
                        canonical2epml = new Canonical2EPML(cpf);
                    } else {
                        canonical2epml = new Canonical2EPML(cpf, anf);
                    }
                    jc = JAXBContext.newInstance("de.epml");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<TypeEPML> rootepml = new de.epml.ObjectFactory().createEpml(canonical2epml.getEPML());
                    m.marshal(rootepml, native_xml);
                }

                InputStream native_xml_is = new ByteArrayInputStream(native_xml.toByteArray());
                //client.StoreNative(processId, version, nativeType, native_xml_is);
                //native_xml_is = new ByteArrayInputStream(native_xml.toByteArray());
                DataSource native_ds = new ByteArrayDataSource(native_xml_is, "text/xml");
                res.setNativeDescription(new DataHandler(native_ds));

                result.setCode(0);
                result.setMessage("");

            } else {
                result.setCode(-1);
                result.setMessage("Native type not supported.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("De-canonisation failed: " + ex.getMessage());
        }
        return res;
    }


    /* (non-Javadoc)
      * @see org.apromore.canoniser.service.CanoniserManager#canoniseProcess(org.apromore.canoniser.model_manager.CanoniseProcessInputMsgType  payload )*
      */
    public CanoniseProcessOutputMsgType canoniseProcess(CanoniseProcessInputMsgType payload) {
        LOG.info("Executing operation canoniseProcess");
        System.out.println(payload);
        CanoniseProcessOutputMsgType res = new CanoniseProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            DataHandler handler = payload.getProcessDescription();
            InputStream process_xml = handler.getInputStream();
            EditSessionType editSession = payload.getEditSession();
            String username = editSession.getUsername();
            String nativeType = editSession.getNativeType();
            String processName = editSession.getProcessName();
            String domain = editSession.getDomain();
            String versionName = editSession.getVersionName();
            String created = editSession.getCreationDate();
            String lastupdate = editSession.getLastUpdate();
            String cpfURI = payload.getCpfUri();
            Boolean addFakeEvents = payload.isAddFakeEvents();
            ByteArrayOutputStream anf_xml = new ByteArrayOutputStream(),
                    cpf_xml = new ByteArrayOutputStream();
            Canonise(cpfURI, process_xml, nativeType, anf_xml, cpf_xml, addFakeEvents);
            InputStream anf_is = new ByteArrayInputStream(anf_xml.toByteArray());
            InputStream cpf_is = new ByteArrayInputStream(cpf_xml.toByteArray());
            ProcessSummaryType process = client.storeNativeCpf(username, processName, cpfURI,
                    domain, nativeType, versionName,
                    "", created, lastupdate, handler.getInputStream(), cpf_is, anf_is);
            res.setProcessSummary(process);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("Canonisation failed: " + ex.getMessage());
        }
        return res;
    }

    public GenerateAnnotationOutputMsgType generateAnnotation(GenerateAnnotationInputMsgType payload) {
        LOG.info("Executing operation generateAnnotation");
        System.out.println(payload);
        GenerateAnnotationOutputMsgType res = new GenerateAnnotationOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer editSessionCode = payload.getEditSessionCode();
            String annotationName = payload.getAnnotationName();
            Boolean isNew = payload.isIsNew();
            DataHandler handler = payload.getNative();
            InputStream npf_is = handler.getInputStream();
            Integer processId = payload.getProcessId();
            String version = payload.getVersion();
            String nativeType = payload.getNativeType();
            String cpf_uri = client.GetCpfUri(processId, version);
            ByteArrayOutputStream anf_xml = new ByteArrayOutputStream(),
                    cpf_xml = new ByteArrayOutputStream();

            Canonise(cpf_uri, npf_is, nativeType, anf_xml, cpf_xml, false);

            npf_is.reset();
            InputStream anf_is = new ByteArrayInputStream(anf_xml.toByteArray());
            client.WriteAnnotation(editSessionCode, annotationName, isNew,
                    processId, version, cpf_uri, nativeType,
                    npf_is, anf_is);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage("Generation of annotation failed: " + ex.getMessage());
        }
        return res;
    }

    public CanoniseVersionOutputMsgType canoniseVersion(CanoniseVersionInputMsgType payload) {
        LOG.info("Executing operation canoniseVersion");
        System.out.println(payload);
        CanoniseVersionOutputMsgType res = new CanoniseVersionOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);

        try {
            DataHandler handler = payload.getNative();
            InputStream process_xml = handler.getInputStream();
            int editSessionCode = payload.getEditSessionCode();
            EditSessionType editSessionM = payload.getEditSession();
            EditSessionType editSessionDA = new EditSessionType();
            String nativeType = editSessionM.getNativeType();
            editSessionDA.setProcessId(editSessionM.getProcessId());
            editSessionDA.setNativeType(editSessionM.getNativeType());
            editSessionDA.setAnnotation(editSessionM.getAnnotation());
            editSessionDA.setCreationDate(editSessionM.getCreationDate());
            editSessionDA.setLastUpdate(editSessionM.getLastUpdate());
            editSessionDA.setProcessName(editSessionM.getProcessName());
            editSessionDA.setUsername(editSessionM.getUsername());
            editSessionDA.setVersionName(editSessionM.getVersionName());

            String cpfURI = payload.getCpfUri();
            ByteArrayOutputStream anf_xml = new ByteArrayOutputStream(),
                    cpf_xml = new ByteArrayOutputStream();
            Canonise(cpfURI, process_xml, nativeType, anf_xml, cpf_xml, false);
            InputStream anf_is = new ByteArrayInputStream(anf_xml.toByteArray());
            InputStream cpf_is = new ByteArrayInputStream(cpf_xml.toByteArray());
            client.StoreVersion(editSessionCode, editSessionDA, cpfURI,
                    handler.getInputStream(), anf_is, cpf_is);
            result.setCode(0);
            result.setMessage("");

        } catch (ExceptionStore ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (IOException ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        } catch (JAXBException ex) {
            result.setCode(-1);
            result.setMessage("Error JAXB: " + ex.getMessage());
        } catch (ExceptionAdapters ex) {
            result.setCode(-1);
            result.setMessage("Error Adapter: " + ex.getMessage());
        } catch (ExceptionVersion ex) {
            result.setCode(-3);
            result.setMessage(ex.getMessage());
        } catch (Exception ex) {
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return res;
    }

    /**
     * Generate cpf_xml and anf_xml from process_xml which is specified in language nativeType.
     * If cpf_uri is equal to 0, take it from process_xml
     *
     * @param process_xml
     * @param nativeType
     * @param anf_xml
     * @param cpf_xml
     * @param cpf_uri
     * @throws ExceptionAdapters
     * @throws javax.xml.bind.JAXBException
     */
    private void Canonise(String cpf_uri, InputStream process_xml, String nativeType,
                          ByteArrayOutputStream anf_xml, ByteArrayOutputStream cpf_xml, Boolean addFakeEvents) throws ExceptionAdapters, JAXBException {
        /**
         * native type must be supported by apromore.
         * At the moment: XPDL 2.1 and EPML 2.0
         */
        if (nativeType.compareTo("XPDL 2.1") == 0) {

            JAXBContext jc1 = JAXBContext.newInstance(Constants.JAXB_CONTEXT_XPDL);
            Unmarshaller u = jc1.createUnmarshaller();
            JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
            PackageType pkg = rootElement.getValue();
            XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg, Long.parseLong(cpf_uri));

            jc1 = JAXBContext.newInstance(Constants.JAXB_CONTEXT_ANF);
            Marshaller m_anf = jc1.createMarshaller();
            m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<AnnotationsType> cproc_anf =
                    new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
            m_anf.marshal(cproc_anf, anf_xml);

            jc1 = JAXBContext.newInstance(Constants.JAXB_CONTEXT_CPF);
            Marshaller m_cpf = jc1.createMarshaller();
            m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<CanonicalProcessType> cproc_cpf =
                    new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
            m_cpf.marshal(cproc_cpf, cpf_xml);

        } else if (nativeType.compareTo("EPML 2.0") == 0) {
            JAXBContext jc1 = JAXBContext.newInstance("de.epml");
            Unmarshaller u = jc1.createUnmarshaller();
            JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
            TypeEPML epml = rootElement.getValue();
            EPML2Canonical epml2canonical = new EPML2Canonical(epml, Long.parseLong(cpf_uri));

            jc1 = JAXBContext.newInstance(Constants.JAXB_CONTEXT_ANF);
            Marshaller m_anf = jc1.createMarshaller();
            m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<AnnotationsType> cproc_anf =
                    new org.apromore.anf.ObjectFactory().createAnnotations(epml2canonical.getANF());
            m_anf.marshal(cproc_anf, anf_xml);

            jc1 = JAXBContext.newInstance(Constants.JAXB_CONTEXT_CPF);
            Marshaller m_cpf = jc1.createMarshaller();
            m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<CanonicalProcessType> cproc_cpf =
                    new org.apromore.cpf.ObjectFactory().createCanonicalProcess(epml2canonical.getCPF());
            m_cpf.marshal(cproc_cpf, cpf_xml);

        } else {
            throw new ExceptionAdapters("Native type not supported.");
        }
    }


    public void setClient(CanoniserDataAccessClient client) {
        this.client = client;
    }

}
