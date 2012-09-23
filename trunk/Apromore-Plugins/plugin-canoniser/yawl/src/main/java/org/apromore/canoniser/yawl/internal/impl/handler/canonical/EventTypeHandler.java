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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.cpf.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ExternalConditionFactsType;
import org.yawlfoundation.yawlschema.NetFactsType;
import org.yawlfoundation.yawlschema.OutputConditionFactsType;

/**
 * Converts an EventType.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public class EventTypeHandler extends CanonicalElementHandler<EventType, NetFactsType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeHandler.class.getName());

    /*
     * (non-Javadoc)
     * 
     * @see org.apromore.canoniser.yawl.internal.impl.handler.ConversionHandler#convert()
     */
    @Override
    public void convert() throws CanoniserException {

        // TODO for now the same as in StateTypeHandler

        if (isInputCondition(getObject())) {
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

}
