package au.ltl.extendedReader;

import org.processmining.plugins.declare.visualizing.AssignmentViewBroker;
import org.processmining.plugins.declare.visualizing.XMLBrokerFactory;

import java.io.InputStream;

/**
 * Created by armascer on 17/11/2017.
 */
public class XMLBrokerFactory2 extends XMLBrokerFactory{

    public static AssignmentViewBroker newAssignmentBroker(InputStream file) {
        return new XMLAssignmentViewBroker2(file, "model");
    }

}
