package org.apromore.portal.client;

import org.apromore.portal.client.util.EditSessionHolder;
import org.apromore.portal.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Performance Test for the Apromore Portal Client.
 */
public class PortalServiceClient implements PortalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     *
     * @param webServiceTemplate the webservice template
     */
    public PortalServiceClient(WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }


    @Override
    public void refresh() {
        // do something
    }


    @Override
    public String readNativeProcess(Integer sessionCode) throws IOException {
        LOGGER.debug("Invoking Read native process for session code " + sessionCode);

        ReadNativeInputMsgType msg = new ReadNativeInputMsgType();
        msg.setEditSessionCode(sessionCode);

        JAXBElement<ReadNativeInputMsgType> request = WS_CLIENT_FACTORY.createReadNativeRequest(msg);
        JAXBElement<ReadNativeOutputMsgType> response = (JAXBElement<ReadNativeOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        EditSessionHolder.addEditSession(sessionCode, response.getValue().getEditSession());

        DataHandler handler = response.getValue().getNative();
        InputStream native_is = handler.getInputStream();
        DataSource sourceNative = new ByteArrayDataSource(native_is, "text/xml");

        return convertStreamToString(sourceNative.getInputStream());
    }

    @Override
    public WriteNewProcessOutputMsgType writeNewProcess(String nativeProcess, Integer sessionCode, String processName, String versionName) throws IOException {
        LOGGER.debug(String.format("Invoking Write new process - ProcessName: %s, VersionName: %s, Native: %s", processName, versionName, nativeProcess.replaceAll("\n", "")));

        DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCode);
        editSession.setProcessName(processName);
        editSession.setVersionName(versionName);

        WriteNewProcessInputMsgType msg = new WriteNewProcessInputMsgType();
        msg.setNative(new DataHandler(nativeProcessDataSource));
        msg.setEditSessionCode(sessionCode);
        msg.setEditSession(editSession);

        JAXBElement<WriteNewProcessInputMsgType> request = WS_CLIENT_FACTORY.createWriteNewProcessRequest(msg);

        JAXBElement<WriteNewProcessOutputMsgType> response = (JAXBElement<WriteNewProcessOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue();
    }

    @Override
    public WriteProcessOutputMsgType writeProcess(String nativeProcess, Integer sessionCode, String processName, String versionName) throws IOException {
        LOGGER.debug(String.format("Invoking Write process - ProcessName: %s, VersionName: %s, Native: %s", processName, versionName, nativeProcess.replaceAll("\n", "")));

        DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCode);
        editSession.setVersionName(versionName);

        WriteProcessInputMsgType msg = new WriteProcessInputMsgType();
        msg.setNative(new DataHandler(nativeProcessDataSource));
        msg.setEditSessionCode(sessionCode);
        msg.setEditSession(editSession);

        JAXBElement<WriteProcessInputMsgType> request = WS_CLIENT_FACTORY.createWriteProcessRequest(msg);

        JAXBElement<WriteProcessOutputMsgType> response = (JAXBElement<WriteProcessOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue();
    }

    @Override
    public WriteAnnotationOutputMsgType writeAnnotation(String nativeProcess, Integer sessionCode) throws IOException {
        LOGGER.debug(String.format("Invoking Write new annotation - Native: %s", nativeProcess.replaceAll("\n", "")));

        DataSource nativeAnnotationDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");

        WriteAnnotationInputMsgType msg = new WriteAnnotationInputMsgType();
        msg.setNative(new DataHandler(nativeAnnotationDataSource));
        msg.setEditSessionCode(sessionCode);

        JAXBElement<WriteAnnotationInputMsgType> request = WS_CLIENT_FACTORY.createWriteAnnotationRequest(msg);

        JAXBElement<WriteAnnotationOutputMsgType> response = (JAXBElement<WriteAnnotationOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue();
    }

    @Override
    public WriteNewAnnotationOutputMsgType writeNewAnnotation(String nativeProcess, Integer sessionCode, String annotationName) throws IOException {
		LOGGER.debug(String.format("Invoking Write new annotation - Annotation: %s, Native: %s", annotationName, nativeProcess.replaceAll("\n", "")));

		DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");

		WriteNewAnnotationInputMsgType msg = new WriteNewAnnotationInputMsgType();
		msg.setNative(new DataHandler(nativeProcessDataSource));
		msg.setEditSessionCode(sessionCode);
		msg.setAnnotationName(annotationName);

        JAXBElement<WriteNewAnnotationInputMsgType> request = WS_CLIENT_FACTORY.createWriteNewAnnotationRequest(msg);

        JAXBElement<WriteNewAnnotationOutputMsgType> response = (JAXBElement<WriteNewAnnotationOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

		return response.getValue();
    }

    @Override
    public String getProcessVersion(String sessionCode) throws IOException {
        int sessionCodeInt = Integer.parseInt(sessionCode);
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCodeInt);
        if (editSession == null) {
            return null;
        }
        return editSession.getVersionName();
    }

    @Override
    public String getProcessName(String sessionCode) throws IOException {
        int sessionCodeInt = Integer.parseInt(sessionCode);
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCodeInt);
        if (editSession == null) {
            return null;
        }
        return editSession.getProcessName();
    }


    /**
     * creates a String representing the content of the stream
     *
     * @param is
     * @return
     * @throws IOException
     */
    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

}
