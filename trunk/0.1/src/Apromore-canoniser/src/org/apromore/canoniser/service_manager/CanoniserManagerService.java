
/*
 * 
 */

package org.apromore.canoniser.service_manager;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;

/**
 * This class was generated by Apache CXF 2.2.7
 * Wed May 12 17:01:13 EST 2010
 * Generated source version: 2.2.7
 * 
 */


@WebServiceClient(name = "CanoniserManagerService", 
                  wsdlLocation = "http://localhost:8080/Apromore-canoniser/services/CanoniserManager?wsdl",
                  targetNamespace = "http://www.apromore.org/canoniser/service_manager") 
public class CanoniserManagerService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://www.apromore.org/canoniser/service_manager", "CanoniserManagerService");
    public final static QName CanoniserManager = new QName("http://www.apromore.org/canoniser/service_manager", "CanoniserManager");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/Apromore-canoniser/services/CanoniserManager?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://localhost:8080/Apromore-canoniser/services/CanoniserManager?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public CanoniserManagerService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public CanoniserManagerService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public CanoniserManagerService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns CanoniserManagerPortType
     */
    @WebEndpoint(name = "CanoniserManager")
    public CanoniserManagerPortType getCanoniserManager() {
        return super.getPort(CanoniserManager, CanoniserManagerPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CanoniserManagerPortType
     */
    @WebEndpoint(name = "CanoniserManager")
    public CanoniserManagerPortType getCanoniserManager(WebServiceFeature... features) {
        return super.getPort(CanoniserManager, CanoniserManagerPortType.class, features);
    }

}
