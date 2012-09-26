package org.apromore.canoniser.yawl.internal.impl.handler.canonical.data;

import org.apromore.canoniser.yawl.internal.impl.handler.canonical.CanonicalElementHandler;
import org.yawlfoundation.yawlschema.DecompositionFactsType;
import org.yawlfoundation.yawlschema.ExternalTaskFactsType;

public abstract class ExpressionTypeHandler<T> extends CanonicalElementHandler<T, ExternalTaskFactsType> {

    protected DecompositionFactsType getDecomposition(final ExternalTaskFactsType task) {
        if (task.getDecomposesTo() == null) {
            return null;
        } else {
            return getContext().getConvertedDecomposition(task.getId());
        }
    }

}
