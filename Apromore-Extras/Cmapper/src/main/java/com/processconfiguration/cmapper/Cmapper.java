package com.processconfiguration.cmapper;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringBufferInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Observable;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.w3c.dom.Element;

import com.processconfiguration.MyTraverser;
import com.processconfiguration.cmap.*;
import com.processconfiguration.quaestio.ProcessModel;
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
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TraversingVisitor;

/**
 * Model of an editable configuration mapping.
 */
class Cmapper extends Observable {

    private static final Logger LOGGER = Logger.getLogger(Cmapper.class.getName());

    // Instance variables
    private CMAP                 cmap      = null;
    private URI                  cmapURI   = null;
    private String               modelText = null;
    private QMLType              qml       = null;
    private URI                  qmlURI    = null;
    private Set<String>          qmlFactIdSet = new HashSet<>();
    private List<VariationPoint> variationPoints = new ArrayList<>();

    /** Sole constructor. */
    Cmapper() {
        // null implementation
    }

    /** @return variation points contained within the C-BPMN file */
    List<VariationPoint> getVariationPoints() {
        return Collections.unmodifiableList(variationPoints);
    }

    /**
     * Set the C-BPMN model.
     *
     * If a <var>cmap</var> has been assigned, it will be used to provide initial configurations.
     * Otherwise, configurable gateways are assigned a single configuration which allows all flows under all conditions.
     *
     * Notifies any observers via the {@link Observable interface}.
     *
     * @param model  a C-BPMN process model, never <code>null</code>
     */
    void setBpmn(final ProcessModel model) throws Exception {
        final TDefinitions definitions = model.getBpmn();
        final Map<String, BpmnGatewayVariationPoint> vpMap = new HashMap<>();

        this.modelText = modelText;

        // Find elements with <pc:configurable> children and add them as VariationPoint instances
        variationPoints.clear();
        model.getBpmn().accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
            @Override public void visit(final TEventBasedGateway gateway) {
                addGateway(gateway, TGatewayType.EVENT_BASED_EXCLUSIVE);
            }
            @Override public void visit(final TExclusiveGateway gateway) {
                addGateway(gateway, TGatewayType.DATA_BASED_EXCLUSIVE);
            }
            @Override public void visit(final TInclusiveGateway gateway) {
                addGateway(gateway, TGatewayType.INCLUSIVE);
            }
            @Override public void visit(final TParallelGateway gateway) {
                addGateway(gateway, TGatewayType.PARALLEL);
            }

            private void addGateway(TGateway gateway, TGatewayType gatewayType) {
                if (isConfigurable(gateway)) {
                    BpmnGatewayVariationPoint vp = new BpmnGatewayVariationPoint(gateway, definitions, gatewayType);
                    vpMap.put(vp.getId(), vp);
                    variationPoints.add(vp);
                }
            }
        }));

        // If we have a configuration mapping for any of the variation points, use that instead
        if (cmap != null) {
            for (Object object: cmap.getCBpmnOrCEpcOrCYawl()) {
                if (object instanceof CBpmnType) {
                    for (CBpmnType.Configurable configurable: ((CBpmnType) object).getConfigurable()) {
                        BpmnGatewayVariationPoint vp = vpMap.get(configurable.getBpmnid());
                        if (vp != null) {
                            vp.getConfigurations().clear();  // blow away any default configuration

                            // Add the configurations from the configuration mapping
                            for (CBpmnType.Configurable.Configuration configuration: configurable.getConfiguration()) {
                                VariationPoint.Configuration vc = vp.new Configuration(configuration.getCondition());
                                if (configuration.getType() != null) {  // if there's only a single flow, it won't have a type
                                    vc.setGatewayType(configuration.getType());
                                }
                                for (int flowIndex = 0; flowIndex < vp.getFlowCount(); flowIndex++) {
                                    List<String> activeFlows;
                                    switch (vp.getGatewayDirection()) {
                                    case CONVERGING:
                                        activeFlows = configuration.getSourceRefs();
                                        break;
                                    case DIVERGING:
                                        activeFlows = configuration.getTargetRefs();
                                        break;
                                    default:
                                        throw new Exception("Variation point " + vp.getId() +
                                            " has unsupported direction " + vp.getGatewayDirection());
                                    }
                                    vc.setFlowCondition(
                                        flowIndex,
                                        activeFlows.contains(vp.getFlowId(flowIndex)) ? "1" : "0"
                                    );
                                }
                                vp.getConfigurations().add(vc);
                            }
                        }
                    }
                    break;  // if there's more than one c-bpmn section in the cmap, skip the subsequent ones
                }
            }
        }

        setChanged();
        notifyObservers();
    }

    /**
     * @param baseElement  an arbitrary BPMN element
     * @return whether <var>baseElement</var> contains a <code>&lt;pc:configurable&gt;</code> extension element
     */
    private boolean isConfigurable(final TBaseElement baseElement) {
        if (baseElement.getExtensionElements() != null) {
            for (Object any: baseElement.getExtensionElements().getAny()) {
                if (any instanceof com.processconfiguration.Configurable) {
                    return true;
                }
                /* If the C-BPMN extension classes aren't available, JAXB will unmarshal DOM Element instances instead.

                else if (any instanceof Element) {
                    Element element = (Element) any;
                    if ("configurable".equals(element.getLocalName()) && "http://www.processconfiguration.com".equals(element.getNamespaceURI())) {
                        return true;
                    }
                }
                */
            }
        }

        return false;
    }

    /** @param cmap */
    void setCmap(Cmap cmap) throws Exception {
        this.cmap    = (cmap == null) ? null : cmap.getCmap();
        this.cmapURI = (cmap == null) ? null : cmap.getURI();

        setChanged();
        notifyObservers();
    }

    /** @param qml */
    void setQml(Qml qml) throws Exception {
        this.qml    = (qml == null) ? null : qml.getQml();
        this.qmlURI = (qml == null) ? null : qml.getURI();

        qmlFactIdSet.clear();
        if (this.qml != null) {
            for (FactType fact: this.qml.getFact()) {
                qmlFactIdSet.add(fact.getId());
            }
        }

        setChanged();
        notifyObservers();
    }

    /** @return whether the questionnaire has been assigned by {@link setQml} yet */
    boolean isQmlSet() {
        return qml != null;
    }

    /** @return the JAXB model of the QML questionnaire, or <code>null</code> if no questionnaire is currently set */
    QMLType getQml() {
        return qml;
    }

    /**
     * Serialize the cmap
     *
     * @throws JAXBException if unable to serialize the cmap
     * @throws ParserException if any of the conditions in the cmap are ungrammatical
     */
    void save(Cmap cmap) throws Exception {
        LOGGER.info("Writing cmap to " + cmap.getURI());

        // Cmap
        ObjectFactory factory = new ObjectFactory();
        CMAP root = factory.createCMAP();
        if (qmlURI != null) {
            root.setQml(qmlURI.toString());
        }

        CBpmnType cBpmn = factory.createCBpmnType();
        cBpmn.setHref(modelText);
        root.getCBpmnOrCEpcOrCYawl().add(cBpmn);

        for (VariationPoint vp: variationPoints) {
            CBpmnType.Configurable configurable = factory.createCBpmnTypeConfigurable();
            configurable.setBpmnid(vp.getId());
            cBpmn.getConfigurable().add(configurable);

            for (VariationPoint.Configuration vc: vp.getConfigurations()) {
                recursivelyAddConfigurations(configurable, vp, vc, vc.getCondition(), 0, new ArrayList<String>());
            }
        }
 
        // Perform the serialization
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        OutputStream out = cmap.getOutputStream();
        m.marshal(root, out);
        out.close();
    }

    private void recursivelyAddConfigurations(final CBpmnType.Configurable       configurable,
                                              final VariationPoint               vp,
                                              final VariationPoint.Configuration vc,
                                              final String                       condition,
                                              final int                          flowIndex,
                                              final List<String>                 flowRefs) throws ParseException {

        if (flowIndex == vp.getFlowCount()) {

            // If the configuration can never occur, skip adding an XML element for it
            if (qml != null && toBDD("(" + qml.getConstraints() + ").(" + condition + ")" ).isZero()) {
                return;
            }

            // Create the configuration XML element
            ObjectFactory factory = new ObjectFactory();
            CBpmnType.Configurable.Configuration configuration = factory.createCBpmnTypeConfigurableConfiguration();
            configurable.getConfiguration().add(configuration);

            // Set condition attribute
            configuration.setCondition(condition);
            
            // Set sourceRefs or targetRefs attribute
            switch (vp.getGatewayDirection()) {
            case CONVERGING: configuration.getSourceRefs().addAll(flowRefs);  break;
            case DIVERGING:  configuration.getTargetRefs().addAll(flowRefs);  break;
            default:         throw new RuntimeException("Unsupported gateway direction in variation point " + vp.getId() + ": " + vp.getGatewayDirection());
            }

            // Set type attribute
            if (flowRefs.size() > 1) {
                configuration.setType(vc.getGatewayType());
            }
        }
        else {
            String flowCondition = vc.getFlowCondition(flowIndex);
            BDD bdd = toBDD(flowCondition);
            if (!bdd.isZero()) {  // Flow may be present
                List<String> newFlowRefs = new ArrayList<>(flowRefs);
                newFlowRefs.add(vp.getFlowId(flowIndex));
                if (bdd.isOne()) {  // Flow is certainly present
                    recursivelyAddConfigurations(configurable, vp, vc, condition, flowIndex + 1, newFlowRefs);
                } else {
                    recursivelyAddConfigurations(configurable, vp, vc, "(" + condition + ").(" + flowCondition + ")", flowIndex + 1, newFlowRefs);
                }
            }
            if (!bdd.isOne()) {  // Flow may be absent
                if (bdd.isZero()) {  // Flow is certainly absent
                    // don't add the absent flow
                    recursivelyAddConfigurations(configurable, vp, vc, condition, flowIndex + 1, flowRefs);
                } else {
                    recursivelyAddConfigurations(configurable, vp, vc, "(" + condition + ").-(" + flowCondition + ")", flowIndex + 1, flowRefs);
                }
            }
        }
    }

    static private BDD toBDD(final String condition) throws ParseException {
        Parser parser = new Parser(new StringBufferInputStream((String) condition));
        parser.init();
        return parser.AdditiveExpression();
    }

    public boolean isValidCondition(final String condition) {
        try {
            Parser parser = new Parser(new StringBufferInputStream((String) condition));
            parser.init();
            parser.AdditiveExpression();
            return qmlFactIdSet.containsAll(parser.getVariableMap().keySet());

        } catch (ParseException e) {
            return false;
        }
    }
}
