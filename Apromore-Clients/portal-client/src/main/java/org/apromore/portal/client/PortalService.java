package org.apromore.portal.client;

import java.io.IOException;

import org.apromore.portal.model.WriteAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewAnnotationOutputMsgType;
import org.apromore.portal.model.WriteNewProcessOutputMsgType;
import org.apromore.portal.model.WriteProcessOutputMsgType;

/**
 * Portal interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface PortalService {

    /**
     * Refresh the server list of processes.
     */
    void refresh();

    /**
     * Returns the native process from the session code.
     * @param sessionCode the users login name
     * @return the UserType from the webservice
     * @throws IOException If the data communication between the client and portal fails.
     */
    String readNativeProcess(Integer sessionCode) throws IOException;

    /**
     * Informs the portal we need to write a new process.
     * @param nativeProcess the native process.
     * @param sessionCode the session code of this transaction.
     * @param processName the process name of the process we are committing
     * @param versionName the version name of this version.
     * @return the response to the writing a new process
     * @throws IOException If the data communication between the client and portal fails.
     */
    WriteNewProcessOutputMsgType writeNewProcess(String nativeProcess, Integer sessionCode, String processName, String versionName)
            throws IOException;

    /**
     * Update a process that already exists in the system.
     * @param nativeProcess the native process.
     * @param sessionCode the session code of this transaction.
     * @param processName the process name of the process we are committing
     * @param versionName the version name of this version.
     * @return the response to the writing a process
     * @throws IOException If the data communication between the client and portal fails.
     */
    WriteProcessOutputMsgType writeProcess(String nativeProcess, Integer sessionCode, String processName, String versionName) throws IOException;

    /**
     * Update an annotation that already exists in the system.
     * @param nativeProcess the native process.
     * @param sessionCode the session code of this transaction.
     * @return the response to the writing an annotation file to apromore
     * @throws IOException If the data communication between the client and portal fails.
     */
    WriteAnnotationOutputMsgType writeAnnotation(String nativeProcess, Integer sessionCode) throws IOException;

    /**
     * Informs the portal we need to write a new annotation.
     * @param nativeProcess the native process.
     * @param sessionCode the session code of this transaction.
     * @param annotationName the annotation name of the new annotation
     * @return the response to the writing a new annotation
     * @throws IOException If the data communication between the client and portal fails.
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
