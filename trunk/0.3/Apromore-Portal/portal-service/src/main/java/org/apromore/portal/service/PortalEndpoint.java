package org.apromore.portal.service;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.EditSessionType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.model.ObjectFactory;
import org.apromore.portal.model.ReadNativeInputMsgType;
import org.apromore.portal.model.ReadNativeOutputMsgType;
import org.apromore.portal.model.ResultType;
import org.apromore.portal.model.WriteAnnotationInputMsgType;
import org.apromore.portal.model.WriteAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewAnnotationInputMsgType;
import org.apromore.portal.model.WriteNewAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewProcessInputMsgType;
import org.apromore.portal.model.WriteNewProcessOutputMsgType;
import org.apromore.portal.model.WriteProcessInputMsgType;
import org.apromore.portal.model.WriteProcessOutputMsgType;
import org.apromore.portal.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import java.io.InputStream;

/**
 * The WebService Endpoint Used by the Portal.
 *
 * This is the only web service available in this application.
 */
@Endpoint
public class PortalEndpoint {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalEndpoint.class.getName());

    private static final String NAMESPACE = "urn:qut-edu-au:schema:apromore:portal";

    @Autowired
    ManagerService mgr;


    @PayloadRoot(namespace = NAMESPACE, localPart = "WriteNewAnnotationRequest")
    @ResponsePayload
    public JAXBElement<WriteNewAnnotationOutputMsgType> writeNewAnnotation(@RequestPayload final JAXBElement<WriteNewAnnotationInputMsgType> req) {
		LOGGER.info("Executing operation writeNewAnnotation");
        WriteNewAnnotationInputMsgType payload = req.getValue();
		WriteNewAnnotationOutputMsgType res = new WriteNewAnnotationOutputMsgType();
		ResultType result = new ResultType();
		res.setResult(result);
		try {
			Integer code = payload.getEditSessionCode();
			String newAnnotationName = payload.getAnnotationName();
			DataHandler handler = payload.getNative();
			InputStream native_is = handler.getInputStream();
			EditSessionType editSession = mgr.readEditSession(code);
			String nat_type = editSession.getNativeType();
			Integer processId = editSession.getProcessId();
			String version = editSession.getVersionName();

			mgr.writeAnnotation(code, newAnnotationName, true, processId, version, nat_type, native_is);
			mgr.deleteEditSession(code);

			EditSessionType newEditSession = new EditSessionType();
			newEditSession.setDomain(editSession.getDomain());
			newEditSession.setNativeType(editSession.getNativeType());
			newEditSession.setProcessId(editSession.getProcessId());
			newEditSession.setProcessName(editSession.getProcessName());
			newEditSession.setUsername(editSession.getUsername());
			newEditSession.setVersionName(editSession.getVersionName());
			newEditSession.setWithAnnotation(editSession.isWithAnnotation());
			newEditSession.setAnnotation(newAnnotationName);
			int newEditSessionCode = mgr.writeEditSession(newEditSession);
			res.setEditSessionCode(newEditSessionCode);
			result.setCode(0);
			result.setMessage("");
		} catch (Exception ex) {
			ex.printStackTrace();
			result.setCode(-1);
			result.setMessage(ex.getMessage());
		}
		return new ObjectFactory().createWriteNewAnnotationResponse(res);
	}

    @PayloadRoot(namespace = NAMESPACE, localPart = "WriteAnnotationRequest")
    @ResponsePayload
    public JAXBElement<WriteAnnotationOutputMsgType> writeAnnotation(@RequestPayload JAXBElement<WriteAnnotationInputMsgType> req) {
        LOGGER.info("Executing operation writeAnnotation");
        WriteAnnotationInputMsgType payload = req.getValue();
        WriteAnnotationOutputMsgType res = new WriteAnnotationOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            Integer code = payload.getEditSessionCode();
            DataHandler handler = payload.getNative();
            InputStream native_is = handler.getInputStream();
            EditSessionType editSession = mgr.readEditSession(code);
            String nat_type = editSession.getNativeType();
            Integer processId = editSession.getProcessId();
            String version = editSession.getVersionName();
            String annotationName = editSession.getAnnotation();
            mgr.writeAnnotation(code, annotationName, false, processId, version, nat_type, native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteAnnotationResponse(res);
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "WriteProcessRequest")
    @ResponsePayload
    public JAXBElement<WriteProcessOutputMsgType> writeProcess(@RequestPayload JAXBElement<WriteProcessInputMsgType> req) {
        LOGGER.info("Executing operation writeProcess");
        WriteProcessInputMsgType payload = req.getValue();
        WriteProcessOutputMsgType res = new WriteProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        try {
            DataHandler handler = payload.getNative();
            InputStream native_is = handler.getInputStream();
            int code = payload.getEditSessionCode();
            // request details associated with edit session
            org.apromore.portal.model.EditSessionType editSession = payload.getEditSession();
            int processId = editSession.getProcessId();
            String username = editSession.getUsername();
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String processName = editSession.getProcessName();
            String new_versionName = editSession.getVersionName();
            String created = editSession.getCreationDate();
            String lastupdate = editSession.getLastUpdate();
            String documentation = "";
            String preVersion = payload.getPreVersion();

            mgr.updateProcess(code, username, nativeType, processId, domain, processName, new_versionName, preVersion, native_is);
            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            result.setCode(-3);
            result.setMessage(ex.getMessage());
//        } catch (IOException ex) {
//            result.setCode(-1);
//            result.setMessage(ex.getMessage());
//        } catch (ExceptionUpdateProcess ex) {
//            result.setCode(-1);
//            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteProcessResponse(res);
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "WriteNewProcessRequest")
    @ResponsePayload
    public JAXBElement<WriteNewProcessOutputMsgType> writeNewProcess(@RequestPayload JAXBElement<WriteNewProcessInputMsgType> req) {
        LOGGER.info("Executing operation writeNewProcess");
        WriteNewProcessInputMsgType payload = req.getValue();
        WriteNewProcessOutputMsgType res = new WriteNewProcessOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int code = payload.getEditSessionCode();

        try {
            DataHandler handler = payload.getNative();
            InputStream native_is = handler.getInputStream();

            org.apromore.portal.model.EditSessionType editSession = payload.getEditSession();
            // editSession given by Oryx contains all meta data
            String username = editSession.getUsername();
            String nativeType = editSession.getNativeType();
            String domain = editSession.getDomain();
            String new_processName = editSession.getProcessName();
            String new_versionName = editSession.getVersionName();
            String created = editSession.getCreationDate();
            String lastupdate = editSession.getLastUpdate();
            String documentation = "";

            ProcessSummaryType newProcess = mgr.importProcess(username, nativeType, new_processName, new_versionName, native_is,
                    domain, documentation, created, lastupdate, false);
            mgr.deleteEditSession(code);

            // request a new session code for the new process and return it to Oryx
            EditSessionType newEditSession = new EditSessionType();
            newEditSession.setDomain(newProcess.getDomain());
            newEditSession.setNativeType(newProcess.getOriginalNativeType());
            newEditSession.setProcessId(newProcess.getId());
            newEditSession.setProcessName(newProcess.getName());
            newEditSession.setUsername(newProcess.getOwner());
            newEditSession.setVersionName(newProcess.getLastVersion());
            newEditSession.setWithAnnotation(true);
            newEditSession.setAnnotation(Constants.INITIAL_ANNOTATION);
            int newEditSessionCode = mgr.writeEditSession(newEditSession);
            res.setEditSessionCode(newEditSessionCode);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createWriteNewProcessResponse(res);
    }


    @PayloadRoot(namespace = NAMESPACE, localPart = "ReadNativeRequest")
    @ResponsePayload
    public JAXBElement<ReadNativeOutputMsgType> readNative(@RequestPayload JAXBElement<ReadNativeInputMsgType> req) {
        LOGGER.info("Executing operation readNative");
        ReadNativeInputMsgType payload = req.getValue();
        ReadNativeOutputMsgType res = new ReadNativeOutputMsgType();
        ResultType result = new ResultType();
        res.setResult(result);
        int code = payload.getEditSessionCode();

        try {
            EditSessionType editSessionP = mgr.readEditSession(code);
            int processId = editSessionP.getProcessId();
            String version = editSessionP.getVersionName();
            String nativeType = editSessionP.getNativeType();
            String processName = editSessionP.getProcessName();
            Boolean withAnnotation = editSessionP.isWithAnnotation();
            String annotation = editSessionP.getAnnotation();
            String owner = editSessionP.getUsername();
            String domain = editSessionP.getDomain();
            String creationDate = editSessionP.getCreationDate();
            String lastUpdate = editSessionP.getLastUpdate();

            DataHandler nativeDH = mgr.exportFormat(processId, processName, version, nativeType, annotation, withAnnotation, owner);
            LOGGER.info(StreamUtil.convertStreamToString(nativeDH));

            org.apromore.portal.model.EditSessionType editSessionO = new org.apromore.portal.model.EditSessionType();
            editSessionO.setAnnotation(annotation);
            editSessionO.setCreationDate(creationDate);
            editSessionO.setDomain(domain);
            editSessionO.setLastUpdate(lastUpdate);
            editSessionO.setNativeType(nativeType);
            editSessionO.setProcessId(processId);
            editSessionO.setProcessName(processName);
            editSessionO.setUsername(owner);
            editSessionO.setVersionName(version);
            editSessionO.setWithAnnotation(withAnnotation);

            res.setNative(nativeDH);
            res.setEditSession(editSessionO);

            result.setCode(0);
            result.setMessage("");
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setCode(-1);
            result.setMessage(ex.getMessage());
        }
        return new ObjectFactory().createReadNativeResponse(res);
    }

}
