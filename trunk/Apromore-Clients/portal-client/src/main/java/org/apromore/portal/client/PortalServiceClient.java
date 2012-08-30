package org.apromore.portal.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBElement;

import org.apromore.portal.client.util.EditSessionHolder;
import org.apromore.portal.model.EditSessionType;
import org.apromore.portal.model.ObjectFactory;
import org.apromore.portal.model.ReadNativeInputMsgType;
import org.apromore.portal.model.ReadNativeOutputMsgType;
import org.apromore.portal.model.WriteAnnotationInputMsgType;
import org.apromore.portal.model.WriteAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewAnnotationInputMsgType;
import org.apromore.portal.model.WriteNewAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewProcessInputMsgType;
import org.apromore.portal.model.WriteNewProcessOutputMsgType;
import org.apromore.portal.model.WriteProcessInputMsgType;
import org.apromore.portal.model.WriteProcessOutputMsgType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Performance Test for the Apromore Portal Client.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class PortalServiceClient implements PortalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalServiceClient.class);
    private static final ObjectFactory WS_CLIENT_FACTORY = new ObjectFactory();

    private WebServiceTemplate webServiceTemplate;

    /**
     * Default Constructor.
     *
     * @param newWebServiceTemplate the webservice template
     */
    public PortalServiceClient(final WebServiceTemplate newWebServiceTemplate) {
        this.webServiceTemplate = newWebServiceTemplate;
    }


    /**
     * @see org.apromore.portal.client.PortalService#refresh()
     * {@inheritDoc}
     */
    @Override
    public void refresh() {  }


    /**
     * @see PortalService#readNativeProcess(Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public String readNativeProcess(final Integer sessionCode) throws IOException {
        LOGGER.debug("Invoking Read native process for session code " + sessionCode);

        ReadNativeInputMsgType msg = new ReadNativeInputMsgType();
        msg.setEditSessionCode(sessionCode);

        JAXBElement<ReadNativeInputMsgType> request = WS_CLIENT_FACTORY.createReadNativeRequest(msg);
        JAXBElement<ReadNativeOutputMsgType> response = (JAXBElement<ReadNativeOutputMsgType>) webServiceTemplate.marshalSendAndReceive(request);

        EditSessionHolder.addEditSession(sessionCode, response.getValue().getEditSession());

        DataHandler handler = response.getValue().getNative();
        InputStream nativeIs = handler.getInputStream();
        DataSource sourceNative = new ByteArrayDataSource(nativeIs, "text/xml");

        return convertStreamToString(sourceNative.getInputStream());
    }

    /**
     * @see PortalService#writeNewProcess(String, Integer, String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public WriteNewProcessOutputMsgType writeNewProcess(final String nativeProcess, final Integer sessionCode, final String processName,
            final String versionName) throws IOException {
        LOGGER.debug(String.format("Invoking Write new process - ProcessName: %s, VersionName: %s, Native: %s", processName, versionName,
                nativeProcess.replaceAll("\n", "")));

        DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCode);
        editSession.setProcessName(processName);
        editSession.setVersionName(versionName);

        WriteNewProcessInputMsgType msg = new WriteNewProcessInputMsgType();
        msg.setNative(new DataHandler(nativeProcessDataSource));
        msg.setEditSessionCode(sessionCode);
        msg.setEditSession(editSession);

        JAXBElement<WriteNewProcessInputMsgType> request = WS_CLIENT_FACTORY.createWriteNewProcessRequest(msg);

        JAXBElement<WriteNewProcessOutputMsgType> response = (JAXBElement<WriteNewProcessOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue();
    }

    /**
     * @see PortalService#writeNewProcess(String, Integer, String, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public WriteProcessOutputMsgType writeProcess(final String nativeProcess, final Integer sessionCode, final String processName,
            final String versionName) throws IOException {
        LOGGER.debug(String.format("Invoking Write process - ProcessName: %s, VersionName: %s, Native: %s", processName, versionName,
                nativeProcess.replaceAll("\n", "")));

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

    /**
     * @see PortalService#writeAnnotation(String, Integer)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public WriteAnnotationOutputMsgType writeAnnotation(final String nativeProcess, final Integer sessionCode) throws IOException {
        LOGGER.debug(String.format("Invoking Write new annotation - Native: %s", nativeProcess.replaceAll("\n", "")));

        DataSource nativeAnnotationDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");

        WriteAnnotationInputMsgType msg = new WriteAnnotationInputMsgType();
        msg.setNative(new DataHandler(nativeAnnotationDataSource));
        msg.setEditSessionCode(sessionCode);

        JAXBElement<WriteAnnotationInputMsgType> request = WS_CLIENT_FACTORY.createWriteAnnotationRequest(msg);

        JAXBElement<WriteAnnotationOutputMsgType> response = (JAXBElement<WriteAnnotationOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue();
    }

    /**
     * @see PortalService#writeNewAnnotation(String, Integer, String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public WriteNewAnnotationOutputMsgType writeNewAnnotation(final String nativeProcess, final Integer sessionCode, final String annotationName)
            throws IOException {
        LOGGER.debug(String.format("Invoking Write new annotation - Annotation: %s, Native: %s", annotationName, nativeProcess.replaceAll("\n", "")));

        DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml");

        WriteNewAnnotationInputMsgType msg = new WriteNewAnnotationInputMsgType();
        msg.setNative(new DataHandler(nativeProcessDataSource));
        msg.setEditSessionCode(sessionCode);
        msg.setAnnotationName(annotationName);

        JAXBElement<WriteNewAnnotationInputMsgType> request = WS_CLIENT_FACTORY.createWriteNewAnnotationRequest(msg);

        JAXBElement<WriteNewAnnotationOutputMsgType> response = (JAXBElement<WriteNewAnnotationOutputMsgType>)
                webServiceTemplate.marshalSendAndReceive(request);

        return response.getValue();
    }

    /**
     * @see PortalService#getProcessVersion(String)
     * {@inheritDoc}
     */
    @Override
    public String getProcessVersion(final String sessionCode) throws IOException {
        int sessionCodeInt = Integer.parseInt(sessionCode);
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCodeInt);
        if (editSession == null) {
            return null;
        }
        return editSession.getVersionName();
    }

    /**
     * @see PortalService#getProcessName(String)
     * {@inheritDoc}
     */
    @Override
    public String getProcessName(final String sessionCode) throws IOException {
        int sessionCodeInt = Integer.parseInt(sessionCode);
        EditSessionType editSession = EditSessionHolder.getEditSession(sessionCodeInt);
        if (editSession == null) {
            return null;
        }
        return editSession.getProcessName();
    }


    /**
     * creates a String representing the content of the stream.
     * @param is the input Stream to convert
     * @return The Stream as a String.
     * @throws IOException if the stream is corrupt.
     */
    private String convertStreamToString(final InputStream is) throws IOException {
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
