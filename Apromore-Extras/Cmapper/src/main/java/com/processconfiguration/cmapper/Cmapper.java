/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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
import java.util.Iterator;
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

import com.processconfiguration.ConfigurationMapping;
import com.processconfiguration.MyTraverser;
import com.processconfiguration.cmap.*;
import com.processconfiguration.quaestio.ProcessModel;
import com.processconfiguration.qml.FactType;
import com.processconfiguration.qml.QMLType;
import net.sf.javabdd.BDD;
import org.apromore.bpmncmap.parser.ParseException;
import org.apromore.bpmncmap.parser.Parser;
import org.apromore.canoniser.bpmn.bpmn.BpmnDefinitions;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;
import org.omg.spec.bpmn._20100524.model.DepthFirstTraverserImpl;
import org.omg.spec.bpmn._20100524.model.TBaseElement;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TEventBasedGateway;
import org.omg.spec.bpmn._20100524.model.TExclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TExtensionElements;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TInclusiveGateway;
import org.omg.spec.bpmn._20100524.model.TParallelGateway;
import org.omg.spec.bpmn._20100524.model.TProcess;
import org.omg.spec.bpmn._20100524.model.TraversingVisitor;

/**
 * Model of an editable configuration mapping.
 */
class Cmapper extends Observable {

    private static final Logger LOGGER = Logger.getLogger(Cmapper.class.getName());

    // Instance variables
    private CMAP                 cmap      = null;
    private URI                  cmapURI   = null;
    private ProcessModel         model     = null;
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
    void setModel(final ProcessModel model) throws Exception {
        final TDefinitions definitions = model.getBpmn();
        final Map<String, BpmnGatewayVariationPoint> vpMap = new HashMap<>();

        this.model = model;

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

    //
    // The methods beyond this point aren't really pure "model" in the MVC sense....
    //

    /**
     * Insert a link from the C-BPMN model to the configuration mapping document.
     *
     * @throws IllegalStateException if no cmap has been set yet
     */
    void link() throws Exception {
        if (cmapURI == null) {
            throw new IllegalStateException("Cannot link because no cmap is set yet");
        }

        LOGGER.info("Linking model to " + cmapURI);
        BpmnDefinitions definitions = model.getBpmn();

        definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
            @Override public void visit(final TProcess process) {
                LOGGER.info("  Traverse process " + process.getId());

                // Remove any existing configuration mappings
                TExtensionElements extensionElements = process.getExtensionElements();
                if (extensionElements != null) {
                    Iterator i = extensionElements.getAny().iterator();
                    while (i.hasNext()) {
                        Object object = i.next();
                        if (object instanceof ConfigurationMapping) {
                            ConfigurationMapping cmapping = (ConfigurationMapping) object;
                            i.remove();
                        }
                    }
                }

                // Add the new configuration mapping
                com.processconfiguration.ObjectFactory factory = new com.processconfiguration.ObjectFactory();
                ConfigurationMapping cmapping = factory.createConfigurationMapping();
                cmapping.setHref(cmapURI.toString());
                extensionElements.getAny().add(cmapping);
            }
        }));
            
        model.update(definitions);
        LOGGER.info("Linked model to " + cmapURI);
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
        cBpmn.setHref(model.getText());
        root.getCBpmnOrCEpcOrCYawl().add(cBpmn);

        String qmlConstraints = "1";
        if (isQmlSet() && getQml().getConstraints() != null) {
            qmlConstraints = getQml().getConstraints();
        }

        for (VariationPoint vp: variationPoints) {
            vp.simplify(qmlConstraints);  // The cmap format only supports simplified configurations

            CBpmnType.Configurable configurable = factory.createCBpmnTypeConfigurable();
            configurable.setBpmnid(vp.getId());
            cBpmn.getConfigurable().add(configurable);

            for (VariationPoint.Configuration vc: vp.getConfigurations()) {
                CBpmnType.Configurable.Configuration configuration = factory.createCBpmnTypeConfigurableConfiguration();
                configurable.getConfiguration().add(configuration);
                
                // Set condition attribute
                configuration.setCondition(vc.getCondition());

                // Set sourceRefs or targetRefs attribute
                List<String> flowRefs = new ArrayList<>();
                for (int flowIndex = 0; flowIndex < vp.getFlowCount(); ++flowIndex) {
                    BDD bdd = BpmnGatewayVariationPoint.toBDD(vc.getFlowCondition(flowIndex));
                    if (bdd.isOne()) {
                        flowRefs.add(vp.getFlowId(flowIndex));
                    } else {
                        assert bdd.isZero() : "Condition for " + vp.getFlowId(flowIndex) + " was non-boolean value " + vc.getFlowCondition(flowIndex);
                    }
                }
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
        }
 
        // Perform the serialization
        JAXBContext jc = JAXBContext.newInstance("com.processconfiguration.cmap");
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        OutputStream out = cmap.getOutputStream();
        m.marshal(root, out);
        out.close();
    }

    /**
     * @param condition  a condition expression
     * @return <code>null</code> if the <var>condition</var> is valid, a user-legible error message otherwise
     */
    public String findConditionError(final String condition) {
        try {
            Parser parser = new Parser(new StringBufferInputStream((String) condition));
            parser.init();
            parser.AdditiveExpression();
            Set undefinedFactIdSet = new HashSet<>(parser.getVariableMap().keySet());
            undefinedFactIdSet.removeAll(qmlFactIdSet);
            if (undefinedFactIdSet.isEmpty()) {
                return null;
            } else if (!isQmlSet()) {
                return "No questionnaire chosen to provide facts: " + undefinedFactIdSet;
            } else {
                return "Questionnaire does not provide facts: " + undefinedFactIdSet;
            }

        } catch (ParseException e) {
            return "Syntax error: " + e.getMessage();
        }
    }
}
