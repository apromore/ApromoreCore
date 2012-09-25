package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.utils.ExtensionUtils;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TypeAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;

public abstract class BaseEventHandler<T extends NodeType> extends CanonicalElementHandler<T, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEventHandler.class);

    @Override
    public void convert() throws CanoniserException {

        if (isArtificialInputCondition(getObject())) {
            final ExternalConditionFactsType inputCondition = createCondition(getObject());
            LOGGER.debug("Added Input Condition {} to Net {}", inputCondition.getName(), getConvertedParent().getName());
            getConvertedParent().getProcessControlElements().setInputCondition(inputCondition);
        } else if (isArtificialOutputCondition(getObject())) {
            final OutputConditionFactsType outputCondition = createOutputCondition(getObject());
            LOGGER.debug("Added Output Condition {} to Net {}", outputCondition.getName(), getConvertedParent().getName());
            getConvertedParent().getProcessControlElements().setOutputCondition(outputCondition);
        } else if (isInputCondition(getObject())) {
            final ExternalConditionFactsType inputCondition = createCondition(getObject());
            LOGGER.debug("Added Input Condition {} to Net {}", inputCondition.getName(), getConvertedParent().getName());
            getConvertedParent().getProcessControlElements().setInputCondition(inputCondition);
        } else if (isOutputCondition(getObject())) {
            final OutputConditionFactsType outputCondition = createOutputCondition(getObject());
            LOGGER.debug("Added Output Condition {} to Net {}", outputCondition.getName(), getConvertedParent().getName());
            getConvertedParent().getProcessControlElements().setOutputCondition(outputCondition);
        } else {
            final ExternalConditionFactsType condition = createCondition(getObject());
            LOGGER.debug("Added Condition {} to Net {}", condition.getName(), getConvertedParent().getName());
            getConvertedParent().getProcessControlElements().getTaskOrCondition().add(condition);
        }

    }

    private boolean isArtificialOutputCondition(final T object) {
        TypeAttribute attribute = ExtensionUtils.getFromExtensions(object, "org.apromore.canoniser.yawl.artificalOutputCondition");
        return (attribute != null) && "true".equals(attribute.getValue());
    }

    private boolean isArtificialInputCondition(final T object) {
        TypeAttribute attribute = ExtensionUtils.getFromExtensions(object, "org.apromore.canoniser.yawl.artificalInputCondition");
        return (attribute != null) && "true".equals(attribute.getValue());
    }

    protected boolean isOutputCondition(final T object) {
        return getContext().getPostSet(object.getId()).size() == 0;
    }

    protected boolean isInputCondition(final T object) {
        return getContext().getPreSet(object.getId()).size() == 0;
    }

    protected ExternalConditionFactsType createCondition(final T node) {
        final ExternalConditionFactsType inputCondition = YAWL_FACTORY.createExternalConditionFactsType();
        inputCondition.setId(generateUUID(node.getId()));
        inputCondition.setName(node.getName());
        getContext().setElement(node.getId(), inputCondition);
        return inputCondition;
    }

    protected OutputConditionFactsType createOutputCondition(final T node) {
        final OutputConditionFactsType outputCondition = YAWL_FACTORY.createOutputConditionFactsType();
        outputCondition.setId(generateUUID(node.getId()));
        outputCondition.setName(node.getName());
        getContext().setElement(node.getId(), outputCondition);
        return outputCondition;
    }

}
