
package org.oryx.portal;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.namespace.QName;

import org.oryx.model_portal.ReadNativeInputMsgType;
import org.oryx.model_portal.ReadNativeOutputMsgType;
import org.oryx.model_portal.WriteNewProcessInputMsgType;
import org.oryx.model_portal.WriteNewProcessOutputMsgType;
import org.oryx.model_portal.WriteProcessInputMsgType;
import org.oryx.model_portal.WriteProcessOutputMsgType;


/**
 * facade to communicate with apromore portal
 * @author mehrad
 *
 */
public final class APROMOREInterfaceImpl implements APROMOREInterface {

	private static final QName SERVICE_NAME = new QName("http://www.apromore.org/portal/service_oryx", "PortalOryxService");

	private PortalOryxPortType port;
	private URL wsdlURL;
	public APROMOREInterfaceImpl(String WSDL_URL) throws MalformedURLException {
		wsdlURL = new URL(WSDL_URL);
		PortalOryxService ss = new PortalOryxService(wsdlURL, SERVICE_NAME);
		port = ss.getPortalOryx();  
	}

	
	/* (non-Javadoc)
	 * @see au.edu.qut.apromore.facade.APROMOREInterface#readNativeProcess(java.lang.Integer)
	 */
	public String readNativeProcess(Integer sessionCode) throws IOException
	{
		ReadNativeInputMsgType _readNative_payload = new ReadNativeInputMsgType();
		_readNative_payload.setEditSessionCode(Integer.valueOf(sessionCode));
		ReadNativeOutputMsgType _readNative__return = port.readNative(_readNative_payload);

		DataHandler handler = _readNative__return.getNative();
		InputStream native_is = handler.getInputStream();
		DataSource sourceNative = new ByteArrayDataSource(native_is, "text/xml"); 
		return convertStreamToString(sourceNative.getInputStream());
	}
	
	/* (non-Javadoc)
	 * @see au.edu.qut.apromore.facade.APROMOREInterface#writeNewProcess(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String)
	 */
	public WriteNewProcessOutputMsgType writeNewProcess(String nativeProcess,Integer sessionCode,String processName,String versionName) throws IOException
	{
		DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml"); 
		WriteNewProcessInputMsgType _writeNewProcess_payload = 
			new WriteNewProcessInputMsgType();
		
		_writeNewProcess_payload.setNative(new DataHandler(nativeProcessDataSource));
		_writeNewProcess_payload.setEditSessionCode(sessionCode);
		_writeNewProcess_payload.setProcessName(processName);
		_writeNewProcess_payload.setVersionName(versionName);
		WriteNewProcessOutputMsgType _writeNewProcess__return = port.writeNewProcess(_writeNewProcess_payload);
		
		return _writeNewProcess__return;

	}
	
	@Override
	public WriteProcessOutputMsgType writeProcess(String nativeProcess, Integer sessionCode,
			String processName, String versionName) throws IOException {

		DataSource nativeProcessDataSource = new ByteArrayDataSource(nativeProcess, "text/xml"); 
		WriteProcessInputMsgType _writeProcess_payload = 
			new WriteProcessInputMsgType();
		
		_writeProcess_payload.setNative(new DataHandler(nativeProcessDataSource));
		_writeProcess_payload.setEditSessionCode(sessionCode);
//		_writeProcess_payload.setProcessName(processName);
		_writeProcess_payload.setVersionName(versionName);
		WriteProcessOutputMsgType _writeProcess__return = port.writeProcess(_writeProcess_payload);
		return _writeProcess__return;

	}

	/**
	 * creates a String representing the content of the stream
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
