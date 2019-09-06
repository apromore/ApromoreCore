package org.apromore.logman.stats.calculators;

import org.apromore.logman.log.LogVisitor;
import org.apromore.logman.log.durationaware.AXTrace;
import org.apromore.logman.log.durationaware.Activity;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.log.event.PerspectiveChangedEvent;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XEvent;

public class ActiveCaseStats implements LogVisitor, LogFilterListener {
    @Override
    public void beforeVisit() {
        
    }
    
    @Override
    public void visit(XAttributable element) {
        if (element instanceof AXTrace) {
            
        }
        else if (element instanceof Activity) {
            
        }
        else if (element instanceof XEvent) {
            
        }
    }

    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPerspectiveChanged(PerspectiveChangedEvent event) {
        // TODO Auto-generated method stub
        
    }



}
