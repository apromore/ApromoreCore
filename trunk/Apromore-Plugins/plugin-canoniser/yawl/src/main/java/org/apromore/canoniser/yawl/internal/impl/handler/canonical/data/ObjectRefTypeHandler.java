package org.apromore.canoniser.yawl.internal.impl.handler.canonical.data;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.apromore.cpf.ObjectRefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.WebServiceGatewayFactsType;

public class ObjectRefTypeHandler extends CanonicalElementHandler<ObjectRefType, WebServiceGatewayFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRefTypeHandler.class);

    @Override
    public void convert() throws CanoniserException {
        LOGGER.warn("Ignoring at the moment!");
    }


}
