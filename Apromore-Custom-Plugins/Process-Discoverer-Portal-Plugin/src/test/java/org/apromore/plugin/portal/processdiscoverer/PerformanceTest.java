/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
import org.junit.Test;

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
    public void test_Performance_Once() {
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
    
    private void test_PerformanceIteration(XLog[] logs, String[] logNames) {;
        for (int i=0;i<logs.length;i++) {
            System.out.println();
            System.out.println("==========  TESTING FOR: " + logNames[i] + " ==========");
            discoverProcessFromXLog(logs[i]);
        }
    }

    @Test
    public void test_Performance_Iterations() {
        System.out.println("Read all XLog data objects used in this test into memory");
        XLog[] logs = new XLog[7];
        String[] logNames = new String[7];
    
        logs[0] = readRealLog_BPI15();
        logNames[0] = "BPIC15";
    
        logs[1] = readRealLog_teys();
        logNames[1] = "teys";
    
        logs[2] = readRealLog_BPI17_Offer();
        logNames[2] = "BPIC17 Offers";
    
        logs[3] = readRealLog_BPI17_Application();
        logNames[3] = "BPIC17 Applications";
    
        logs[4] = readRealLog_BPI12();
        logNames[4] = "BPIC12";
    
        logs[5] = readRealLog_BPI18();
        logNames[5] = "BPIC18";
    
        logs[6] = readRealLog_procmin();
        logNames[6] = "Procmin";
    
        for (int i=0;i<=5;i++) {
            if (i==0) {
                System.out.println("WARM-UP ITERATION - NOT USED IN TEST RESULTS");
            }
            else {
                System.out.println("++++++++++++++++++++++++++++++++++++++++");
                System.out.println("+                                      +");
                System.out.println("+           ITERATION " + i + "                 +");
                System.out.println("+                                      +");
                System.out.println("++++++++++++++++++++++++++++++++++++++++");
            }
            test_PerformanceIteration(logs, logNames);
        }
    }
}
    
