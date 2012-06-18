package org.apromore.canoniser.service;

import de.epml.TypeEPML;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.EPML2Canonical;
import org.apromore.canoniser.adapters.PNML2Canonical;
import org.apromore.canoniser.adapters.XPDL2Canonical;
import org.apromore.canoniser.da.CanoniserDataAccessClient;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.exception.CanoniserException;
import org.apromore.exception.ExceptionAdapters;
import org.apromore.exception.ExceptionStore;
import org.apromore.exception.ExceptionVersion;
import org.apromore.model.CanoniseVersionInputMsgType;
import org.apromore.model.CanoniseVersionOutputMsgType;
import org.apromore.model.EditSessionType;
import org.apromore.model.GenerateAnnotationInputMsgType;
import org.apromore.model.GenerateAnnotationOutputMsgType;
import org.apromore.model.ResultType;
import org.apromore.pnml.PnmlType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.wfmc._2008.xpdl2.PackageType;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
@Service("CanoniserManager")
public class CanoniserManagerImpl implements CanoniserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserManagerImpl.class);

    private CanoniserDataAccessClient client;

//    /* (non-Javadoc)
//      * @see org.apromore.canoniser.service.CanoniserManager#canoniseProcess(org.apromore.canoniser.model_manager.CanoniseProcessInputMsgType  payload )*
//      */
//    public CanoniseProcessOutputMsgType canoniseProcess(CanoniseProcessInputMsgType payload) {
//        LOGGER.info("Executing operation canoniseProcess");
//        CanoniseProcessOutputMsgType res = new CanoniseProcessOutputMsgType();
//        ResultType result = new ResultType();
//        res.setResult(result);
//
//        ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
//        ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();
//
//        try {
//            DataHandler handler = payload.getProcessDescription();
//            InputStream process_xml = handler.getInputStream();
//            EditSessionType editSession = payload.getEditSession();
//            String username = editSession.getUsername();
//            String nativeType = editSession.getNativeType();
//            String processName = editSession.getProcessName();
//            String domain = editSession.getDomain();
//            String versionName = editSession.getVersionName();
//            String created = editSession.getCreationDate();
//            String lastupdate = editSession.getLastUpdate();
//            String cpfURI = payload.getCpfUri();
//            Boolean addFakeEvents = payload.isAddFakeEvents();
//            Canonise(cpfURI, process_xml, nativeType, anf_xml, cpf_xml, addFakeEvents);
//            InputStream anf_is = new ByteArrayInputStream(anf_xml.toByteArray());
//            InputStream cpf_is = new ByteArrayInputStream(cpf_xml.toByteArray());
//            ProcessSummaryType process = client.storeNativeCpf(username, processName, cpfURI, domain, nativeType, versionName,
//                    "", created, lastupdate, handler.getInputStream(), cpf_is, anf_is);
//            res.setProcessSummary(process);
//            result.setCode(0);
//            result.setMessage("");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            result.setCode(-1);
//            result.setMessage("Canonisation failed: " + ex.getMessage());
//        }
//        return res;
//    }

    public GenerateAnnotationOutputMsgType generateAnnotation(GenerateAnnotationInputMsgType payload) {
        LOGGER.info("Executing operation generateAnnotation");
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
        LOGGER.info("Executing operation canoniseVersion");
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
            ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
            ByteArrayOutputStream  cpf_xml = new ByteArrayOutputStream();
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
        } catch (CanoniserException ex) {
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
     * @param cpf_uri the uri of the cpf process
     * @param process_xml the input stream to convert
     * @param nativeType the native type
     * @param anf_xml the anf output stream to put the data
     * @param cpf_xml the cpf output stream to put the data
     * @param addFakeEvents do we add fake events or not?
     * @throws ExceptionAdapters
     * @throws javax.xml.bind.JAXBException
     */
    @SuppressWarnings("unchecked")
    private void Canonise(String cpf_uri, InputStream process_xml, String nativeType,
            ByteArrayOutputStream anf_xml, ByteArrayOutputStream cpf_xml, Boolean addFakeEvents) throws CanoniserException, JAXBException {
        /**
         * native type must be supported by apromore.
         * At the moment: XPDL 2.1 and EPML 2.0
         */
        if (nativeType.compareTo(Constants.XPDL_2_1) == 0) {
            JAXBContext jc1 = JAXBContext.newInstance(Constants.XPDL2_CONTEXT);
            Unmarshaller u = jc1.createUnmarshaller();
            JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(process_xml);
            PackageType pkg = rootElement.getValue();
            XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg, Long.parseLong(cpf_uri));

            jc1 = JAXBContext.newInstance(Constants.ANF_CONTEXT);
            Marshaller m_anf = jc1.createMarshaller();
            m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<AnnotationsType> cproc_anf =
                    new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
            m_anf.marshal(cproc_anf, anf_xml);

            jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
            Marshaller m_cpf = jc1.createMarshaller();
            m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<CanonicalProcessType> cproc_cpf =
                    new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
            m_cpf.marshal(cproc_cpf, cpf_xml);

        } else if (nativeType.compareTo(Constants.EPML_2_0) == 0) {
            JAXBContext jc1 = JAXBContext.newInstance(Constants.EPML_CONTEXT);
            Unmarshaller u = jc1.createUnmarshaller();
            JAXBElement<TypeEPML> rootElement = (JAXBElement<TypeEPML>) u.unmarshal(process_xml);
            TypeEPML epml = rootElement.getValue();
            EPML2Canonical epml2canonical = new EPML2Canonical(epml, Long.parseLong(cpf_uri));

            jc1 = JAXBContext.newInstance(Constants.ANF_CONTEXT);
            Marshaller m_anf = jc1.createMarshaller();
            m_anf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<AnnotationsType> cproc_anf =
                    new org.apromore.anf.ObjectFactory().createAnnotations(epml2canonical.getANF());
            m_anf.marshal(cproc_anf, anf_xml);

            jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
            Marshaller m_cpf = jc1.createMarshaller();
            m_cpf.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<CanonicalProcessType> cproc_cpf =
                    new org.apromore.cpf.ObjectFactory().createCanonicalProcess(epml2canonical.getCPF());
            m_cpf.marshal(cproc_cpf, cpf_xml);

        } else if (nativeType.compareTo(Constants.PNML_1_3_2)==0) {
            JAXBContext jc1 = JAXBContext.newInstance(Constants.PNML_CONTEXT);
            Unmarshaller u = jc1.createUnmarshaller();
            JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) u.unmarshal(process_xml);
            PnmlType pnml = rootElement.getValue();
            PNML2Canonical pnml2canonical = new PNML2Canonical(pnml, Long.parseLong(cpf_uri));

            jc1 = JAXBContext.newInstance(Constants.ANF_CONTEXT);
            Marshaller m_anf = jc1.createMarshaller();
            m_anf.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            JAXBElement<AnnotationsType> cproc_anf =
                    new org.apromore.anf.ObjectFactory().createAnnotations(pnml2canonical.getANF());
            m_anf.marshal(cproc_anf, anf_xml);

            jc1 = JAXBContext.newInstance(Constants.CPF_CONTEXT);
            Marshaller m_cpf = jc1.createMarshaller();
            m_cpf.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            JAXBElement<CanonicalProcessType> cproc_cpf =
                    new org.apromore.cpf.ObjectFactory().createCanonicalProcess(pnml2canonical.getCPF());
            m_cpf.marshal(cproc_cpf, cpf_xml);

        } else {
            throw new CanoniserException("Native type not supported.");
        }
    }


    public void setClient(CanoniserDataAccessClient client) {
        this.client = client;
    }

}
