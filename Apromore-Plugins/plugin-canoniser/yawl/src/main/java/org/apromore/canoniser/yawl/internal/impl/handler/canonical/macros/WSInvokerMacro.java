package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.cpf.CanonicalProcessType;

public class WSInvokerMacro extends ContextAwareRewriteMacro {

    public WSInvokerMacro(final CanonicalConversionContext context) {
        super(context);
    }

    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        // TODO rewrite WSInvoker
        return false;
    }

}
