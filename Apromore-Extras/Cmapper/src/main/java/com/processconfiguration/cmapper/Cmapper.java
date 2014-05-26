package com.processconfiguration.cmapper;

import java.io.File;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Element;

import com.processconfiguration.MyTraverser;
import com.processconfiguration.cmap.*;
import com.processconfiguration.qml.FactType;
import com.processconfiguration.qml.QMLType;
import net.sf.javabdd.BDD;
import org.apromore.bpmncmap.parser.ParseException;
import org.apromore.bpmncmap.parser.Parser;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;
import org.omg.spec.bpmn._20100524.model.DepthFirstTraverserImpl;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEventBasedGateway;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TraversingVisitor;

/**
 * Model of an editable configuration mapping.
 */
class Cmapper {

    private static final Logger LOGGER = Logger.getLogger(Cmapper.class.getName());

    // Instance variables
    private File                 cmapFile = null;
    private List<VariationPoint> variationPoints = new ArrayList<>();

    /** Sole constructor. */
    Cmapper() {
        // null implementation
    }

    /** @return variation points contained within the C-BPMN file */
    List<VariationPoint> getVariationPoints() {
        return variationPoints;
    }

    /** @param file  a file in C-BPMN format */
    void setBpmn(File file) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(org.omg.spec.bpmn._20100524.model.ObjectFactory.class,
                                                      org.omg.spec.bpmn._20100524.di.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.dc.ObjectFactory.class,
                                                      org.omg.spec.dd._20100524.di.ObjectFactory.class);

        JAXBElement<TDefinitions> element = (JAXBElement<TDefinitions>) context.createUnmarshaller().unmarshal(file);
        final TDefinitions definitions = element.getValue();

        // Find elements with <pc:configurable> children and add them as VariationPoint instances
        variationPoints.clear();
        definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
            @Override public void visit(final TEventBasedGateway gateway) {
                if (isConfigurable(gateway)) {
                    variationPoints.add(new VariationPoint(gateway, definitions, TGatewayType.EVENT_BASED_EXCLUSIVE));
                }
            }
            @Override public void visit(final TExclusiveGateway gateway) {
                if (isConfigurable(gateway)) {
                    variationPoints.add(new VariationPoint(gateway, definitions, TGatewayType.DATA_BASED_EXCLUSIVE));
                }
            }
            @Override public void visit(final TInclusiveGateway gateway) {
                if (isConfigurable(gateway)) {
                    variationPoints.add(new VariationPoint(gateway, definitions, TGatewayType.INCLUSIVE));
                }
            }
            @Override public void visit(final TParallelGateway gateway) {
                if (isConfigurable(gateway)) {
                    variationPoints.add(new VariationPoint(gateway, definitions, TGatewayType.PARALLEL));
                }
            }
        }));
    }

    /**
     * @param baseElement  an arbitrary BPMN element
     * @return whether <var>baseElement</var> contains a <code>&lt;pc:configurable&gt;</code> extension element
     */
    private boolean isConfigurable(final TBaseElement baseElement) {
        if (baseElement.getExtensionElements() != null) {
            for (Object any: baseElement.getExtensionElements().getAny()) {
                if (any instanceof Element) {
                    Element element = (Element) any;
                    if ("configurable".equals(element.getLocalName()) && "http://www.processconfiguration.com".equals(element.getNamespaceURI())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    File getCmap() {
        return cmapFile;
    }

    /** @param file  a file in Cmap format */
    void setCmap(File file) throws JAXBException {
        cmapFile = file;
    }

    /** @param file  a file in QML format */
    void setQml(File file) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.qml");
        Unmarshaller u = jc.createUnmarshaller();
        JAXBElement qmlElement = (JAXBElement) u.unmarshal(file);
        QMLType qml = (QMLType) qmlElement.getValue();

        for (FactType fact: qml.getFact()) {
            LOGGER.fine("Fact id=" + fact.getId());
        }
    }

    /**
     * Serialize the cmap
     *
     * @param file  the output file, not <code>null</code>
     * @throws JAXBException if unable to serialize the cmap
     * @throws ParserException if any of the conditions in the cmap are ungrammatical
     */
    void writeCmap(File file) throws IllegalStateException, JAXBException, ParseException {

        // Cmap
        ObjectFactory factory = new ObjectFactory();
        CMAP cmap = factory.createCMAP();
        cmap.setQml("dummy.qml");

        CBpmnType cBpmn = factory.createCBpmnType();
        cBpmn.setHref("dummy.bpmn");
        cmap.getCBpmnOrCEpcOrCYawl().add(cBpmn);

        for (VariationPoint vp: variationPoints) {
            CBpmnType.Configurable configurable = factory.createCBpmnTypeConfigurable();
            configurable.setBpmnid(vp.getId());
            cBpmn.getConfigurable().add(configurable);

            for (VariationPoint.Configuration vc: vp.getConfigurations()) {
                CBpmnType.Configurable.Configuration configuration = factory.createCBpmnTypeConfigurableConfiguration();
                List<String> flowRefs;
                switch (vp.getGatewayDirection()) {
                case CONVERGING: flowRefs = configuration.getSourceRefs();  break;
                case DIVERGING:  flowRefs = configuration.getTargetRefs();  break;
                default:         throw new RuntimeException("Unsupported gateway direction in variation point " + vp.getId() + ": " + vp.getGatewayDirection());
                }
                assert flowRefs != null;

                // Validate the condition against the BDDC grammar
                Parser parser = new Parser(new StringBufferInputStream(vc.getCondition()));
                parser.init();
                BDD bdd = parser.AdditiveExpression();  // throws ParserException if malformed condition
                LOGGER.info("Parsed " + vc.getCondition() + " into " + bdd);

                configuration.setCondition(vc.getCondition());

                for (int flowIndex = 0; flowIndex < vp.getFlowCount(); flowIndex++) {
                    if (vc.isFlowActive(flowIndex)) {
                        flowRefs.add(vp.getFlowId(flowIndex));
                    }
                }
                if (flowRefs.size() > 1) {
                    configuration.setType(vc.getGatewayType());
                }
                configurable.getConfiguration().add(configuration);
            }
        }

        // Perform the serialization
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Marshaller m = jc.createMarshaller();
        m.marshal(cmap, file);
    }
}
