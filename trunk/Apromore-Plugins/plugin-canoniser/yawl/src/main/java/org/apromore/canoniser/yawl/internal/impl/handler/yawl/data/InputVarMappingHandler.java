/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.yawl.data;

import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.CPFSchema;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.TaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts the Input Mapping of a YAWL Task to Object references.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class InputVarMappingHandler extends BaseVarMappingHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputVarMappingHandler.class);

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {
        final TaskType task = getConvertedParent();
        final NetType parentNet = getContext().getNetForTaskId(task.getId());

        final String xQuery = getObject().getExpression().getQuery();
        final String mapsTo = getObject().getMapsTo();

        final List<ObjectType> variables = calculateUsedNetVariables(xQuery, parentNet);

        // Create references to all Objects this mapping is referring to, note that we're missing CONSTANTs
        for (final ObjectType var : variables) {
            //TODO isConsumed??
            final ObjectRefType objectRef = createObjectRef(var, InputOutputType.INPUT, false, true);
            LOGGER.debug("Adding Object Reference for YAWL Task {} (Type: {}, Source: {}, Target: {})", new String[] {
                    getConvertedParent().getName(), objectRef.getType().toString(), objectRef.getObjectId(), null });
            task.getObjectRef().add(objectRef);
        }

        // Store the xQuery expression in a canonical way
        task.getInputExpr().add(convertXQuery(xQuery, mapsTo));
    }

    private InputExpressionType convertXQuery(final String xQuery, final String mapsTo) {
        InputExpressionType inputExpr = getContext().getCanonicalOF().createInputExpressionType();
        inputExpr.setLanguage(CPFSchema.EXPRESSION_LANGUAGE_XQUERY);
        //TODO rewrite xQuery
        inputExpr.setExpression(CPFSchema.createInputExpression(mapsTo, xQuery));
        return inputExpr;
    }

}
