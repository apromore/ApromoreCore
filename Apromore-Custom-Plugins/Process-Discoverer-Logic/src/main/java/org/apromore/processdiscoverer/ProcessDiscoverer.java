/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.processdiscoverer;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.processdiscoverer.abstraction.AbstractionFactory;

/**
 * A single class to represent ProcessDiscoverer logic
 * @author Bruce Nguyen
 */
public class ProcessDiscoverer {
    private AbstractionFactory absFactory;
    
    public ProcessDiscoverer(AttributeLog log) {
        absFactory = new AbstractionFactory(log);
    }
    
    public Abstraction generateDFGAbstraction(AbstractionParams params) throws Exception {
    	return absFactory.createDFGAbstraction(params.clone());
    }
    
    public Abstraction generateBPMNAbstraction(AbstractionParams params, Abstraction dfgAbstraction) throws Exception {
    	return absFactory.createBPMNAbstraction(params.clone(), dfgAbstraction);
    }
    
    // This method does not affect the internal status of ProcessDiscoverer object
    public Abstraction generateTraceAbstraction(String traceID, AbstractionParams params) throws Exception {
        AttributeLog log = absFactory.getLog();
    	if(log == null || log.getTraces().size() == 0) {
    		throw new Exception("No log abstraction has been done yet!");
    	}
        
    	AttributeTrace attTrace = log.getTraceFromTraceId(traceID);
        if (attTrace == null) {
        	throw new Exception("The trace with ID = " + traceID + " is not in the current log (may have been filtered out)!");
        }
       
        return absFactory.createTraceAbstraction(attTrace, log, params);
    }
    
}
