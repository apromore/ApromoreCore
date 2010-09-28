
package org.apromore.manager.canoniser;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 2.2.9
 * Sat Sep 18 13:04:42 CEST 2010
 * Generated source version: 2.2.9
 * 
 */

public final class CanoniserManagerPortType_CanoniserManager_Client {

    private static final QName SERVICE_NAME = new QName("http://www.apromore.org/canoniser/service_manager", "CanoniserManagerService");

    private CanoniserManagerPortType_CanoniserManager_Client() {
    }

    public static void main(String args[]) throws Exception {
        URL wsdlURL = CanoniserManagerService.WSDL_LOCATION;
        if (args.length > 0) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        CanoniserManagerService ss = new CanoniserManagerService(wsdlURL, SERVICE_NAME);
        CanoniserManagerPortType port = ss.getCanoniserManager();  
        
        {
        System.out.println("Invoking deCanoniseProcess...");
        org.apromore.manager.model_canoniser.DeCanoniseProcessInputMsgType _deCanoniseProcess_payload = new org.apromore.manager.model_canoniser.DeCanoniseProcessInputMsgType();
        _deCanoniseProcess_payload.setProcessId(879131805);
        _deCanoniseProcess_payload.setVersion("Version746937722");
        _deCanoniseProcess_payload.setNativeType("NativeType-719170787");
        javax.activation.DataHandler _deCanoniseProcess_payloadCpf = null;
        _deCanoniseProcess_payload.setCpf(_deCanoniseProcess_payloadCpf);
        javax.activation.DataHandler _deCanoniseProcess_payloadAnf = null;
        _deCanoniseProcess_payload.setAnf(_deCanoniseProcess_payloadAnf);
        org.apromore.manager.model_canoniser.DeCanoniseProcessOutputMsgType _deCanoniseProcess__return = port.deCanoniseProcess(_deCanoniseProcess_payload);
        System.out.println("deCanoniseProcess.result=" + _deCanoniseProcess__return);


        }
        {
        System.out.println("Invoking canoniseVersion...");
        org.apromore.manager.model_canoniser.CanoniseVersionInputMsgType _canoniseVersion_payload = new org.apromore.manager.model_canoniser.CanoniseVersionInputMsgType();
        javax.activation.DataHandler _canoniseVersion_payloadNative = null;
        _canoniseVersion_payload.setNative(_canoniseVersion_payloadNative);
        _canoniseVersion_payload.setEditSessionCode(Integer.valueOf(-1029203664));
        _canoniseVersion_payload.setNativeType("NativeType-1882990486");
        _canoniseVersion_payload.setProcessId(Integer.valueOf(2145539301));
        _canoniseVersion_payload.setPreVersion("PreVersion-1344771085");
        org.apromore.manager.model_canoniser.CanoniseVersionOutputMsgType _canoniseVersion__return = port.canoniseVersion(_canoniseVersion_payload);
        System.out.println("canoniseVersion.result=" + _canoniseVersion__return);


        }
        {
        System.out.println("Invoking canoniseProcess...");
        org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType _canoniseProcess_payload = new org.apromore.manager.model_canoniser.CanoniseProcessInputMsgType();
        javax.activation.DataHandler _canoniseProcess_payloadProcessDescription = null;
        _canoniseProcess_payload.setProcessDescription(_canoniseProcess_payloadProcessDescription);
        _canoniseProcess_payload.setProcessName("ProcessName461421894");
        _canoniseProcess_payload.setVersionName("VersionName-1840109359");
        _canoniseProcess_payload.setNativeType("NativeType2124468531");
        _canoniseProcess_payload.setDomain("Domain-1561833613");
        _canoniseProcess_payload.setUsername("Username1366839716");
        _canoniseProcess_payload.setDocumentation("Documentation479161169");
        _canoniseProcess_payload.setLastUpdate("LastUpdate-1613362616");
        _canoniseProcess_payload.setCreationDate("CreationDate518354258");
        org.apromore.manager.model_canoniser.CanoniseProcessOutputMsgType _canoniseProcess__return = port.canoniseProcess(_canoniseProcess_payload);
        System.out.println("canoniseProcess.result=" + _canoniseProcess__return);


        }

        System.exit(0);
    }

}
