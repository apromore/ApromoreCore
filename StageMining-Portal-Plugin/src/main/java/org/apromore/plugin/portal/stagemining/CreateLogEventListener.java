/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.stagemining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Window;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeImpl;
import org.processmining.stagemining.models.DecompositionTree;import org.zkoss.zul.ext.Selectable;
;

/**
 *
 * @author Administrator
 * Based on http://zkfiddle.org/sample/2vah9aj/29-add-new-row#source-2
 */
public class CreateLogEventListener implements EventListener<Event> {
    Window parentW = null;
    XLog log = null;
    DecompositionTree tree = null;

    
    
    public CreateLogEventListener(Window window, XLog log, DecompositionTree tree) {
        this.parentW = window;
        this.log = log;
        this.tree = tree;
    }
    public void onEvent(Event event) throws Exception {
        
    }
    

}
