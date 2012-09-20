package org.apromore.canoniser.yawl.internal.impl.handler.yawl.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.yawl.YAWLConversionHandler;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.VariableBaseType;

/**
 * Converts a Task scope variable
 * 
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class TaskVariableHandler extends YAWLConversionHandler<VariableBaseType, TaskType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskVariableHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // TODO remove

        final SoftType alreadyConvertedObject = getContext().getObjectByName(getObject().getName(), getConvertedParent());
        if (alreadyConvertedObject == null) {
            final SoftType canonicalObject = getContext().getCanonicalOF().createSoftType();
            final String generatedOriginalId = ConversionUtils.buildObjectId(getConvertedParent(), getObject().getName());
            canonicalObject.setId(generateUUID(DATA_ID_PREFIX, generatedOriginalId));
            canonicalObject.setOriginalID(generatedOriginalId);
            canonicalObject.setName(getObject().getName());
            canonicalObject.setType(getObject().getType());
            // getConvertedParent().getObject().add(canonicalObject);
            LOGGER.debug("Adding Object for YAWL Task {} (Name: {}, Type: {})",
                    new String[] { getConvertedParent().getName(), canonicalObject.getName(), canonicalObject.getType() });
            getContext().addObjectForTask(canonicalObject, getConvertedParent());
        }

    }

}
