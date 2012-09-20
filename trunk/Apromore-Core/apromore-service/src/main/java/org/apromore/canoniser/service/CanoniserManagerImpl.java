package org.apromore.canoniser.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.Canoniser;
import org.apromore.canoniser.da.CanoniserDataAccessClient;
import org.apromore.canoniser.epml.EPML20Canoniser;
import org.apromore.canoniser.pnml.PNML132Canoniser;
import org.apromore.canoniser.provider.impl.CanoniserProviderImpl;
import org.apromore.canoniser.xpdl.XPDL21Canoniser;
import org.apromore.canoniser.yawl.YAWL22Canoniser;
import org.apromore.cpf.CPFSchema;
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
import org.apromore.plugin.exception.PluginNotFoundException;
import org.apromore.plugin.property.PropertyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 *
 */
@Service("CanoniserManager")
public class CanoniserManagerImpl implements CanoniserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanoniserManagerImpl.class);

    private CanoniserDataAccessClient client;
    
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
            ByteArrayOutputStream anf_xml = new ByteArrayOutputStream(), cpf_xml = new ByteArrayOutputStream();

            Canonise(cpf_uri, npf_is, nativeType, anf_xml, cpf_xml, false);

            npf_is.reset();
            InputStream anf_is = new ByteArrayInputStream(anf_xml.toByteArray());
            client.WriteAnnotation(editSessionCode, annotationName, isNew, processId, version, cpf_uri, nativeType, npf_is, anf_is);
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
            ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();
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
     * @param cpf_uri       the uri of the cpf process
     * @param process_xml   the input stream to convert
     * @param nativeType    the native type
     * @param anf_xml       the anf output stream to put the data
     * @param cpf_xml       the cpf output stream to put the data
     * @param addFakeEvents do we add fake events or not?
     * @throws ExceptionAdapters
     * @throws javax.xml.bind.JAXBException
     */
    @Deprecated
    private void Canonise(String cpf_uri, InputStream process_xml, String nativeType,
                          ByteArrayOutputStream anf_xml, ByteArrayOutputStream cpf_xml, Boolean addFakeEvents) throws CanoniserException, JAXBException {
        //TODO why and where is this mtehod needed? there is another one in CanoniserServiceImpl
        
        
        CanoniserProviderImpl canoniserProvider = new CanoniserProviderImpl();
        //TODO replace by OSGi DI 
        // Workaround until OSGi is there
        ArrayList<Canoniser> canoniserList = new ArrayList<Canoniser>();
        EPML20Canoniser epmlCanoniser = new EPML20Canoniser();
        //TODO later all available properties can be shown to the user
        for (PropertyType prop: epmlCanoniser.getAvailableProperties()) {
            if (prop.getName().equalsIgnoreCase("addFakeProperties")) {
                prop.setValue(addFakeEvents);
            }
        }
        canoniserList.add(epmlCanoniser);
        canoniserList.add(new XPDL21Canoniser());
        canoniserList.add(new PNML132Canoniser());
        canoniserList.add(new YAWL22Canoniser());
        canoniserProvider.setCanoniserList(canoniserList);
        
        List<CanonicalProcessType> cpfList = new ArrayList<CanonicalProcessType>();
        List<AnnotationsType> anfList = new ArrayList<AnnotationsType>();
        
        try {
            canoniserProvider.canonise(nativeType, process_xml, anfList, cpfList);
        } catch (org.apromore.canoniser.exception.CanoniserException | PluginNotFoundException e) {
            throw new CanoniserException("Could not canonise "+nativeType, e);
        }
        
        if (cpfList.size() > 1 || anfList.size() > 1) {
            throw new CanoniserException("Can only process single CPF/ANF pair!");
        } else {
            
            try {
                ANFSchema.marshalAnnotationFormat(anf_xml, anfList.get(0), true);
                CPFSchema.marshalCanoncialFormat(cpf_xml, cpfList.get(0), true);
            } catch (SAXException e) {
                throw new CanoniserException("Error canonising "+nativeType+", probably an internal error in the Canoniser!", e);
            }
            
        }
        
    }


    public void setClient(CanoniserDataAccessClient client) {
        this.client = client;
    }

}
