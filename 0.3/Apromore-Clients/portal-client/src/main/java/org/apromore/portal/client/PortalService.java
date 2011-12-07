package org.apromore.portal.client;

import org.apromore.portal.model.WriteAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewProcessOutputMsgType;
import org.apromore.portal.model.WriteProcessOutputMsgType;

import java.io.IOException;

/**
 * Portal interface.
 */
public interface PortalService {

    /**
     * Refresh the server list of processes.
     */
    void refresh();

    /**
     * ??
     *
     * @param sessionCode the users login name
     * @return the UserType from the webservice
     */
    String readNativeProcess(Integer sessionCode) throws IOException;

    /**
     * ??
     *
     * @return the UsernameType from the Webservice
     */
    WriteNewProcessOutputMsgType writeNewProcess(String nativeProcess, Integer sessionCode, String processName, String versionName) throws IOException;

    /**
     * ??
     *
     * @return the DomainsType from the WebService
     */
    WriteProcessOutputMsgType writeProcess(String nativeProcess, Integer sessionCode, String processName, String versionName) throws IOException;

    /**
     * ??
     *
     * @return the NativeTypesType from the WebService
     */
    WriteAnnotationOutputMsgType writeAnnotation(String nativeProcess, Integer sessionCode) throws IOException;

    /**
     * ??
     *
     * @return the EditSessionType from the WebService
     */
    WriteNewAnnotationOutputMsgType writeNewAnnotation(String nativeProcess, Integer sessionCode, String annotationName) throws IOException;


    /* **************************************************************************************** */
    /* Support Methods Used by Oryx and Apromore to share information                           */
    /* **************************************************************************************** */

    /**
     * From the Session code find out the Process Version.
     * @param sessionCode the session code
     * @return the version number of that process
     * @throws IOException if the lookup fails
     */
    String getProcessVersion(String sessionCode) throws IOException;

    /**
     * From the Session code find out the Process Name.
     * @param sessionCode the session code
     * @return the name of that process
     * @throws IOException if the lookup fails
     */
    String getProcessName(String sessionCode) throws IOException;
}
