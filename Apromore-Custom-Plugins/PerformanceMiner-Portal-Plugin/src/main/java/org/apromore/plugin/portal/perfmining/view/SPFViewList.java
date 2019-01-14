/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view;

import java.util.ArrayList;
import java.util.Collection;
import org.apromore.plugin.portal.perfmining.view.SPFView;
import org.apromore.plugin.portal.perfmining.view.queue.SPFQueueArrivalRateView;
import org.apromore.plugin.portal.perfmining.view.queue.SPFQueueCIPView;
import org.apromore.plugin.portal.perfmining.view.queue.SPFQueueDepartureRateView;
import org.apromore.plugin.portal.perfmining.view.queue.SPFQueueTISView;
import org.apromore.plugin.portal.perfmining.view.service.SPFArrivalRateView;
import org.apromore.plugin.portal.perfmining.view.service.SPFCIPView;
import org.apromore.plugin.portal.perfmining.view.service.SPFDepartureRateView;
import org.apromore.plugin.portal.perfmining.view.service.SPFExitRateView;
import org.apromore.plugin.portal.perfmining.view.service.SPFFlowEfficiencyView;
import org.apromore.plugin.portal.perfmining.view.system.SPFAllFlowEfficiencyView;
import org.apromore.plugin.portal.perfmining.view.system.SPFMainView;
import org.apromore.plugin.portal.perfmining.view.system.SPFMultipleTISView;
import org.apromore.service.perfmining.models.SPF;
import org.apromore.service.perfmining.models.Stage;
import sun.security.krb5.JavaxSecurityAuthKerberosAccess;
import sun.tools.jar.resources.jar;
 
public class SPFViewList {
    private SPFViewTreeNode root;
    
    public SPFViewList(SPF spf) {
        SPFViewTreeNode[] stageNodes = new SPFViewTreeNode[spf.getStages().size() + 1];
        
        // SPF Overall nodes
        int i=0;
        stageNodes[i++] = new SPFViewTreeNode(new SPFMainView(spf, "", "", "SPF", false));
//        stageNodes[i++] = new SPFViewTreeNode(new SPFMultipleTISView(spf, "", "", "All TIS View", false));
//        stageNodes[i++] = new SPFViewTreeNode(new SPFAllFlowEfficiencyView(spf, "", "", "All Flow Efficiency", false));
        
        for (int j=0;j<spf.getStages().size();j++) {
            Stage stage = spf.getStages().get(j);
            //Queue
            SPFViewTreeNode[] queueNodes = new SPFViewTreeNode[4];
            queueNodes[0] = new SPFViewTreeNode(
                            new SPFQueueArrivalRateView(spf, stage.getName(), "Queue", "Arrival Rate", false));
            queueNodes[1] = new SPFViewTreeNode(
                            new SPFQueueDepartureRateView(spf, stage.getName(), "Queue", "Departure Rate", false));
            queueNodes[2] = new SPFViewTreeNode(
                            new SPFQueueCIPView(spf, stage.getName(), "Queue", "Cases in Process", false));
            queueNodes[3] = new SPFViewTreeNode(
                            new SPFQueueTISView(spf, stage.getName(), "Queue", "Time in Stage", false));
            SPFViewTreeNode queueNode = new SPFViewTreeNode(new SPFView(spf, stage.getName(), "Queue", "", true), queueNodes);
            
            //Service
            SPFViewTreeNode[] serviceNodes = new SPFViewTreeNode[6];
            serviceNodes[0] = new SPFViewTreeNode(
                                new SPFArrivalRateView(spf, stage.getName(), "Service", "Arrival Rate", false));
            serviceNodes[1] = new SPFViewTreeNode(
                                new SPFDepartureRateView(spf, stage.getName(), "Service", "Departure Rate", false));
            serviceNodes[2] = new SPFViewTreeNode(
                                new SPFExitRateView(spf, stage.getName(), "Service", "Exit Rate", false));
            serviceNodes[3] = new SPFViewTreeNode(
                                new SPFCIPView(spf, stage.getName(), "Service", "Cases in Process", false));
            serviceNodes[4] = new SPFViewTreeNode(
                                new SPFQueueTISView(spf, stage.getName(), "Service", "Time in Stage", false));
            serviceNodes[5] = new SPFViewTreeNode(
                                new SPFFlowEfficiencyView(spf, stage.getName(), "Service", "Flow Efficiency", false));
            SPFViewTreeNode serviceNode = new SPFViewTreeNode(new SPFView(spf, stage.getName(), "Service", "", true), serviceNodes);
            
            SPFViewTreeNode stageNode = new SPFViewTreeNode(new SPFView(spf, stage.getName(), "", "", true), 
                                                               new SPFViewTreeNode[] {queueNode, serviceNode});
            stageNodes[i+j] = stageNode;
        }
        
        root = new SPFViewTreeNode(new SPFView(spf, "", "", "System", true), stageNodes);
    }
    public SPFViewTreeNode getRoot() {
        return root;
    }
}
