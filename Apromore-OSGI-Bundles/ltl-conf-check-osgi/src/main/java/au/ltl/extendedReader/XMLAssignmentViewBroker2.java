package au.ltl.extendedReader;

import java.io.InputStream;

import org.processmining.plugins.declare.visualizing.AssignmentModel;
import org.processmining.plugins.declare.visualizing.AssignmentModelView;
import org.processmining.plugins.declare.visualizing.AssignmentViewBroker;
import org.w3c.dom.Element;

public class XMLAssignmentViewBroker2 extends XMLAssignmentBroker2 implements AssignmentViewBroker {
    private final AssignmentViewElementFactory2A factory = new AssignmentViewElementFactory2A(this);

    public XMLAssignmentViewBroker2(InputStream file, String name) {
        super(file, name);
    }

    public void addAssignmentAndView(AssignmentModel model, AssignmentModelView view) {
        Element newAssignment = this.factory.createAssignmentElement(model, view);
        Element root = this.getAssignmentElement();
        root.appendChild(newAssignment);
        this.writeDocument();
    }

    public Element assignmentAndViewElement(AssignmentModel model, AssignmentModelView view) {
        Element newAssignment = this.factory.createAssignmentElement(model, view);
        Element root = this.getAssignmentElement();
        root.appendChild(newAssignment);
        return root;
    }

    public Element getAssignmentAndView() {
        this.readDocument();
        Element root = this.getDocumentRoot();
        return root;
    }

    public void readAssignmentGraphical(AssignmentModel model, AssignmentModelView view) {
        this.readDocument();
        Element root = this.getDocumentRoot();

        try {
            this.factory.elementToAssignmentGraphical(view, model, root);
        } catch (Exception var6) {
            AssignmentViewElementFactory2A temp = new AssignmentViewElementFactory2A(this);
            temp.elementToAssignmentGraphical(view, model, root);
        }

    }

    public void readAssignmentGraphicalFromString(AssignmentModel model, AssignmentModelView view, String documentString) {
        this.readDocumentString(documentString);
        Element root = this.getDocumentRoot();

        try {
            this.factory.elementToAssignmentGraphical(view, model, root);
        } catch (Exception var7) {
            AssignmentViewElementFactory2A temp = new AssignmentViewElementFactory2A(this);
            temp.elementToAssignmentGraphical(view, model, root);
        }

    }
}