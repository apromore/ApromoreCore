package org.apromore.portal.dialogController;

// Java 2 Standard packages
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

// Third party packages
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Applet;
import org.zkoss.zul.Window;

// Local packages
//import com.processconfiguration.cmap.CMAP;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.ExportFormatResultType;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TDocumentation;
import org.omg.spec.bpmn._20100524.model.TRootElement;

/**
 * Invoke the Quaestio questionnaire UI to configure a process model version.
 */
public class ConfigureController extends BaseController {

    private static final String NATIVE_TYPE = "BPMN 2.0";
    private static final long serialVersionUID = 1L;
    private MainController mainC;
    private Window configureW;

    /** The list of process versions to configure. */
    private Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions;

    /**
     * Sole constructor.
     */
    public ConfigureController(MainController mainC, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions) {
        this.mainC = mainC;

        this.configureW = (Window) Executions.createComponents("macros/configure.zul", null, null);
        this.configureW.setTitle("Configure process model");

        Applet configureA = (Applet) this.configureW.getFellow("quaestio");

        URL cmapURL = null;
        URL qmlURL  = null;

        for (ProcessSummaryType process: selectedProcessVersions.keySet()) {
            for (VersionSummaryType version: selectedProcessVersions.get(process)) {
                /*
                for (AnnotationsType annotation: version.getAnnotations()) {
                    System.err.println("  Annotation nativeType=" + annotation.getNativeType());
                }
                */

                // Look for the cmap URL in the C-BPMN document
                try {
                    ExportFormatResultType exportFormatResult = getService().exportFormat(
                        process.getId(),             // process ID
                        null,                        // process name
                        version.getName(),           // branch
                        version.getVersionNumber(),  // version number,
                        NATIVE_TYPE,                 // nativeType,
                        null,                        // annotation name,
                        false,                       // with annotations?
                        null,                        // owner
                        Collections.EMPTY_SET        // canoniser properties
                    );
                    JAXBContext context = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                                  org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                                  org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                                  org.omg.spec.dd._20100524.di.ObjectFactory.class,
                                                                  com.processconfiguration.ObjectFactory.class,
                                                                  com.signavio.ObjectFactory.class);
                    TDefinitions bpmn = ((JAXBElement<TDefinitions>) context.createUnmarshaller().unmarshal(new StreamSource(exportFormatResult.getNative().getInputStream()))).getValue();
                    String cmapURLString = null;
                    for (JAXBElement<? extends TRootElement> root: bpmn.getRootElement()) {
                        for (TDocumentation documentation: root.getValue().getDocumentation()) {
                            for (Object object: documentation.getContent()) {
                                cmapURLString = object.toString();
                            }
                        }
                    }
                    System.err.println("Cmap URL from C-BPMN: " + cmapURLString);
                    cmapURL = new URL(cmapURLString);
                } catch (Exception e) {
                    System.err.println("Unable to run cmap URL from C-BPMN");
                    e.printStackTrace();
                }

                // Look for the qml URL in the Cmap document
                if (cmapURL == null) {
                    System.err.println("Not attempting to find QML URL from Cmap");
                } else {
                    try {
                        /*
                        HttpURLConnection c = (HttpURLConnection) cmapURL.openConnection();
                        c.setRequestMethod("GET");
                        c.addRequestProperty("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=");  // Base64 encoded "admin:password"
                        c.connect();
                        System.err.println("Reponse code: " + c.getResponseCode());
                        System.err.println("Reponse message: " + c.getResponseMessage());
                        System.err.println("Content type: " + c.getContentType());
                        CMAP cmap = (CMAP) JAXBContext.newInstance(com.processconfiguration.cmap.ObjectFactory.class)
                                                      .createUnmarshaller()
                                                      .unmarshal(new StreamSource(c.getInputStream()));
                        System.err.println("QML field from Cmap: " + cmap.getQml());
                        qmlURL = new URL(cmapURL, cmap.getQml());
                        */
                        qmlURL = new URL(cmapURL, "Airport.qml");  // gave up fighting with OSGI, just hardcode the QML filename for now
                        System.err.println("QML URL from Cmap: " + qmlURL);
                    } catch (Exception e) {
                        System.err.println("Unable to run QML URL from Cmap");
                        e.printStackTrace();
                    }
                }

                // Set the applet parameters
                configureA.setParam("apromore_model", process.getId() + " " + version.getName() + " " + version.getVersionNumber());
                configureA.setParam("qml_url", qmlURL.toString());
                configureA.setParam("cmap_url", cmapURL.toString());
            }
        }
        System.err.println("New params=" + configureA.getParams());

        this.configureW.doModal();

        System.err.println("Entered Quaestio applet mode");
    }
}
