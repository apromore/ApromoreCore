package com.processconfiguration.cmapper;

// Java 2 Standard packages
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

// Third party classes
import net.sf.javabdd.BDD;

// Local classes
import com.processconfiguration.MyTraverser;
import com.processconfiguration.cmap.TGatewayType;
import com.processconfiguration.qml.QMLType;
import org.apromore.bpmncmap.parser.ParseException;
import org.apromore.bpmncmap.parser.Parser;
import org.omg.spec.bpmn._20100524.model.BaseVisitor;
import org.omg.spec.bpmn._20100524.model.DepthFirstTraverserImpl;
import org.omg.spec.bpmn._20100524.model.TDataOutputAssociation;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.omg.spec.bpmn._20100524.model.TFlowNode;
import org.omg.spec.bpmn._20100524.model.TGateway;
import org.omg.spec.bpmn._20100524.model.TGatewayDirection;
import org.omg.spec.bpmn._20100524.model.TraversingVisitor;
import org.omg.spec.bpmn._20100524.model.TSequenceFlow;
import org.omg.spec.bpmn._20100524.model.TTask;
import org.omg.spec.bpmn._20100524.model.Visitor;

/**
 * A configurable process model element.
 *
 * Currently, always a BPMN ExclusiveGateway.
 */
class BpmnGatewayVariationPoint implements VariationPoint {

    private static Logger LOGGER = Logger.getLogger(VariationPoint.class.getName());

    // Instance variables
    private String                             id;
    private String                             name;
    private List<String>                       flowIds   = new ArrayList<>();
    private List<String>                       flowNames = new ArrayList<>();
    private List<VariationPoint.Configuration> configurations = new ArrayList<>();
    private TGatewayDirection                  gatewayDirection;
    private TGatewayType                       gatewayType;

    /**
     * Create a variation point from a C-BPMN Gateway element.
     *
     * @param name  human-legible name
     */
    BpmnGatewayVariationPoint(final TGateway gateway, final TDefinitions definitions, final TGatewayType newGatewayType) {

        this.id          = gateway.getId();
        this.gatewayType = newGatewayType;

        // Figure out a human-legible name for this gateway
        this.name = isEmpty(gateway.getName()) ? abbreviate(gateway.getId()) : gateway.getName();

        // Figure out the gateway direction
        switch (gateway.getGatewayDirection()) {
        case CONVERGING:
            gatewayDirection = TGatewayDirection.CONVERGING;
            definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
                @Override public void visit(final TSequenceFlow flow) {
                    addFlow(flow, flow.getTargetRef(), flow.getSourceRef(), gateway);
                }
            }));
            break;
        case DIVERGING:
            gatewayDirection = TGatewayDirection.DIVERGING;
            definitions.accept(new TraversingVisitor(new MyTraverser(), new BaseVisitor() {
                @Override public void visit(final TSequenceFlow flow) {
                    addFlow(flow, flow.getSourceRef(), flow.getTargetRef(), gateway);
                }
            }));
            break;
        default:
            throw new RuntimeException("Gateway " + gateway.getId() + " has unsupported direction: " + gateway.getGatewayDirection());
        }

        // Initial configuration
        addConfiguration();
    }

    /**
     * @param s  a candidate human-legible name
     * @return either <var>s</var>, or an abbreviated version of <var>s</var> if it was too long
     */
    private String abbreviate(String s) {
        if (s.length() < 10) {
            return s;
        } else {
            return s.substring(0, 7) + "...";
        }
    }

    /**
     * @param s  arbitrary text
     * @return whether <var>s</var> is non-<code>null</code> and contains printable characters
     */
    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * @param flow  an incoming flow if <var>gateway</var> is converging, an outgoing flow if it is diverging
     * @param node  the element at the other end of the <var>flow</var>
     * @param gateway  a gateway
     */
    private void addFlow(TSequenceFlow flow, TFlowNode node, TFlowNode node2, TGateway gateway) {
        if (node != null && node.getId().equals(gateway.getId())) {
            flowIds.add(flow.getId());

            // Figure out a human-legible name for the flow
            if (!isEmpty(flow.getName())) {
                flowNames.add(flow.getName());
            } else if (!isEmpty(node2.getName())) {
                flowNames.add(node2.getName());
            } else {
                flowNames.add(abbreviate(flow.getId()));
            }
        }
    }

    // Methods implementing VariationPoint

    /** @return BPMN identifier */
    public String getId() {
        return id;
    }

    /** @return human-legible name */
    public String getName() {
        return name;
    }

    public int getFlowCount() {
        return flowNames.size();
    }

    public String getFlowId(int flowIndex) {
        return flowIds.get(flowIndex);
    }

    public String getFlowName(int flowIndex) {
        return flowNames.get(flowIndex);
    }

    public List<VariationPoint.Configuration> getConfigurations() {
        return configurations;
    }

    public void addConfiguration() {
        configurations.add(new Configuration("1"));
    }

    public void removeConfiguration(int configurationIndex) {
        configurations.remove(configurationIndex);
    }

    public TGatewayDirection getGatewayDirection() {
        return gatewayDirection;
    }

    public TGatewayType getGatewayType() {
        return gatewayType;
    }

    public void simplify(String qmlConstraints) throws ParseException {
        List<VariationPoint.Configuration> originalConfigurations = new ArrayList<>(configurations);
        configurations.clear();
        for (VariationPoint.Configuration vc: originalConfigurations) {
            recursivelyAddConfigurations(vc, 0, qmlConstraints);
        }
    }

    static BDD toBDD(final String condition) throws ParseException {
        Parser parser = new Parser(new StringBufferInputStream((String) condition));
        parser.init();
        return parser.AdditiveExpression();
    }

    private void recursivelyAddConfigurations(final VariationPoint.Configuration vc, final int flowIndex, final String qmlConstraints) throws ParseException {

        if (flowIndex == getFlowCount()) {

            String s = "(" + vc.getCondition() + ").(" + qmlConstraints + ")";
            LOGGER.info("Elision check is " + toBDD(s).isZero() + " for " + s);

            // If the configuration can never occur, elide it
            if (!toBDD(s).isZero()) {
                this.configurations.add(vc);
            }
        }
        else {
            String flowCondition = vc.getFlowCondition(flowIndex);
            BDD bdd = toBDD(flowCondition);
            if (!bdd.isZero()) {  // Flow may be present

                VariationPoint.Configuration newVc = new Configuration((BpmnGatewayVariationPoint.Configuration) vc);
                newVc.setFlowCondition(flowIndex, "1");

                if (bdd.isOne()) {  // Flow is certainly present
                    recursivelyAddConfigurations(vc, flowIndex + 1, qmlConstraints);
                } else {
                    newVc.setCondition(conjoin(vc.getCondition(), flowCondition));
                    recursivelyAddConfigurations(newVc, flowIndex + 1, qmlConstraints);
                }
            }
            if (!bdd.isOne()) {  // Flow may be absent

                VariationPoint.Configuration newVc = new Configuration((BpmnGatewayVariationPoint.Configuration) vc);
                newVc.setFlowCondition(flowIndex, "0");

                if (bdd.isZero()) {  // Flow is certainly absent
                    // don't add the absent flow
                    recursivelyAddConfigurations(newVc, flowIndex + 1, qmlConstraints);
                } else {
                    newVc.setCondition(conjoin(vc.getCondition(), negate(flowCondition)));
                    recursivelyAddConfigurations(newVc, flowIndex + 1, qmlConstraints);
                }
            }
        }
    }

    private String conjoin(String lhs, String rhs) throws ParseException {
        BDD lhsBDD = toBDD(lhs);
        BDD rhsBDD = toBDD(rhs);

        if (lhsBDD.isZero() || rhsBDD.isZero()) { return "0"; }

        String s = "(" + lhs + ").(" + rhs + ")";
        BDD sBDD = toBDD(s);

        // If either the LHS or RHS are redundant, elide them
        if (lhsBDD.equals(sBDD)) { return lhs; }
        if (rhsBDD.equals(sBDD)) { return rhs; }

        // See if we can skip parentheses anywhere
        if (toBDD(lhs + "." + rhs).equals(sBDD)) { return lhs + "." + rhs; }
        if (toBDD("(" + lhs + ")." + rhs).equals(sBDD)) { return "(" + lhs + ")." + rhs; }
        if (toBDD(lhs + ".(" + rhs + ")").equals(sBDD)) { return lhs + ".(" + rhs + ")"; }

        // Final resort, just parentheses around everything
        return s;
    }

    private String negate(String s) throws ParseException {
        String withoutParens = "-" + s;
        String withParens    = "-(" + s + ")";
        return toBDD(withoutParens).equals(toBDD(withoutParens)) ? withoutParens : withParens;
    }

    /**
     * A configuration of this variation point.
     */
    class Configuration implements VariationPoint.Configuration {
        private String       condition;
        private TGatewayType gatewayType;
        private String[]     flowCondition;

        Configuration(final String initialCondition) {
            this.condition     = initialCondition;
            this.gatewayType   = BpmnGatewayVariationPoint.this.gatewayType;
            this.flowCondition = new String[getFlowCount()];
            for (int i = 0; i < getFlowCount(); i++) {
                this.flowCondition[i] = "1";
            }
        }

        Configuration(final BpmnGatewayVariationPoint.Configuration configuration) {
            this.condition     = configuration.condition;
            this.gatewayType   = configuration.gatewayType;
            this.flowCondition = Arrays.copyOf(configuration.flowCondition, configuration.flowCondition.length);
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(final String newCondition) {
            condition = newCondition;
        }

        public TGatewayType getGatewayType() {
            return gatewayType;
        }

        public void setGatewayType(final TGatewayType newGatewayType) {
            gatewayType = newGatewayType;
        }

        public String getFlowCondition(int flowIndex) {
            return flowCondition[flowIndex];
        }

        public void setFlowCondition(int flowIndex, String newCondition) {
            flowCondition[flowIndex] = newCondition;
        }
    }
}
