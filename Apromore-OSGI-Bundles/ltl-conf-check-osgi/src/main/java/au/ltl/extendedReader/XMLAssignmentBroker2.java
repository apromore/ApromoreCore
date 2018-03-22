package au.ltl.extendedReader;

import org.processmining.plugins.declare.visualizing.AssignmentBroker;
import org.processmining.plugins.declare.visualizing.AssignmentModel;
import org.w3c.dom.Element;

import java.io.InputStream;

/**
 * Created by armascer on 17/11/2017.
 */
public class XMLAssignmentBroker2 extends XMLBroker2 implements AssignmentBroker {
        private final AssignmentElementFactory2 factory = new AssignmentElementFactory2(this);

        public XMLAssignmentBroker2(InputStream file, String name) {
            super(file, name);
        }

        public void addAssignment(AssignmentModel model) {
            Element newAssignment = this.factory.createAssignmentElement(model);
            Element root = this.getAssignmentElement();
            root.appendChild(newAssignment);
            this.writeDocument();
        }

        public AssignmentModel readAssignment() {
            this.readDocument();
            Element root = this.getDocumentRoot();
            AssignmentModel model = this.factory.elementToAssignmentModel(root);
            return model;
        }

        public AssignmentModel readAssignmentfromString(String documentString) {
            this.readDocumentString(documentString);
            Element root = this.getDocumentRoot();
            AssignmentModel model = this.factory.elementToAssignmentModel(root);
            return model;
        }

        public Element getAssignmentElement() {
            return this.getDocumentRoot();
        }

}
