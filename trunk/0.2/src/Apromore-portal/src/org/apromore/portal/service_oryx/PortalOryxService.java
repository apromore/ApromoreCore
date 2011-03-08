
/*
 * 
 */

package org.apromore.portal.service_oryx;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by Apache CXF 2.2.9
 * Tue Feb 01 09:34:33 CET 2011
 * Generated source version: 2.2.9
 * 
 */


@WebServiceClient(name = "PortalOryxService", 
                  wsdlLocation = "http://localhost:8080/Apromore-portal/services?wsdl",
                  targetNamespace = "http://www.apromore.org/portal/service_oryx") 
public class PortalOryxService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("http://www.apromore.org/portal/service_oryx", "PortalOryxService");
    public final static QName PortalOryx = new QName("http://www.apromore.org/portal/service_oryx", "PortalOryx");
    static {
        URL url = null;
        try {
            url = new URL("http://localhost:8080/Apromore-portal/services/PortalOryx?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://localhost:8080/Apromore-portal/services/PortalOryx?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public PortalOryxService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public PortalOryxService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PortalOryxService() {
        super(WSDL_LOCATION, SERVICE);
    }

    /**
     * 
     * @return
     *     returns PortalOryxPortType
     */
    @WebEndpoint(name = "PortalOryx")
    public PortalOryxPortType getPortalOryx() {
        return super.getPort(PortalOryx, PortalOryxPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PortalOryxPortType
     */
    @WebEndpoint(name = "PortalOryx")
    public PortalOryxPortType getPortalOryx(WebServiceFeature... features) {
        return super.getPort(PortalOryx, PortalOryxPortType.class, features);
    }

}
