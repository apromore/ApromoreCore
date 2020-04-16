/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore Core" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore Core" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.processdiscoverer;

import org.apromore.logman.ALog;
import org.apromore.plugin.portal.processdiscoverer.data.ConfigData;
import org.apromore.plugin.portal.processdiscoverer.data.LogData;
import org.apromore.plugin.portal.processdiscoverer.data.UserOptionsData;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDCustomFactory;
import org.apromore.plugin.portal.processdiscoverer.impl.factory.PDFactory;
import org.apromore.plugin.portal.processdiscoverer.vis.ProcessVisualizer;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.deckfour.xes.model.XLog;

public class PerformanceTest extends TestDataSetup {
    
    private void discoverProcessFromXLog(XLog xlog) {
        PDFactory pdFactory = new PDCustomFactory();
        try {
            long timer = System.currentTimeMillis();
            ALog aLog = new ALog(xlog);
            System.out.println("Create ALog: " + (System.currentTimeMillis() - timer) + " ms.");
            
            ConfigData configData = new ConfigData();
            UserOptionsData userOptions = new UserOptionsData();
            userOptions.setMainAttributeKey(configData.getDefaultAttribute());
            
            LogData logData = new LogData(configData, aLog);
            logData.setMainAttribute(configData.getDefaultAttribute());
    
            ProcessDiscoverer processDiscoverer = new ProcessDiscoverer(logData.getAttributeLog());
            AbstractionParams params = new AbstractionParams(
                    logData.getMainAttribute(), 
                    userOptions.getNodeFilterValue() / 100, 
                    userOptions.getArcFilterValue() / 100, 
                    userOptions.getParallelismFilterValue() / 100, 
                    true, true, 
                    userOptions.getInvertedNodesMode(), 
                    userOptions.getInvertedArcsMode(),
                    userOptions.getIncludeSecondary(),
                    userOptions.getFixedType(), 
                    userOptions.getFixedAggregation(), 
                    userOptions.getPrimaryType(), 
                    userOptions.getPrimaryAggregation(), 
                    userOptions.getSecondaryType(), 
                    userOptions.getSecondaryAggregation(), 
                    userOptions.getRelationReader(),
                    null);
            Abstraction dfgAbstraction = processDiscoverer.generateDFGAbstraction(params);
            params.setCorrespondingDFG(dfgAbstraction);            
            //AbstractAbstraction bpmnAbstraction = processDiscoverer.generateBPMNAbstraction(params, (DFGAbstraction)dfgAbstraction);
            
            ProcessVisualizer processVisualizer = pdFactory.createProcessVisualizer(null);
            timer = System.currentTimeMillis();
            String visualizedText = processVisualizer.generateVisualizationText(dfgAbstraction);
            System.out.println("Generate JSON data from BPMNDiagram: " + (System.currentTimeMillis() - timer) + " ms.");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    //@Test
    public void test_Performance() {
        System.out.println();
        System.out.println("==========  PERFORMANCE TEST: BPIC12 ==========");
        discoverProcessFromXLog(readRealLog_BPI12());
        
        System.out.println();
        System.out.println("==========  PERFORMANCE TEST: BPIC15-1 ==========");
        discoverProcessFromXLog(readRealLog_BPI15());
        
        System.out.println();
        System.out.println("==========  PERFORMANCE TEST: BPIC17 Application Log ==========");
        discoverProcessFromXLog(readRealLog_BPI17_Application());
        
        System.out.println();
        System.out.println("==========  PERFORMANCE TEST: BPIC17 Offer Log ==========");
        discoverProcessFromXLog(readRealLog_BPI17_Offer());
        
        System.out.println();
        System.out.println("==========  PERFORMANCE TEST: BPIC18 ==========");
        discoverProcessFromXLog(readRealLog_BPI18());
        
        System.out.println();
        System.out.println("==========  PEFORMANCE TEST: teys log ==========");
        discoverProcessFromXLog(readRealLog_teys());
        
        System.out.println();
        System.out.println("==========  PEFORMANCE TEST: proc min ==========");
        discoverProcessFromXLog(readRealLog_procmin());
    }
}
    
