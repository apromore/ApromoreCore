/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
 
public class SummaryData {
    List<SummaryLineItem> items = new ArrayList<SummaryLineItem>();
 
    public SummaryData() {
        initData();
    }
 
    private void initData() {
        if (!items.isEmpty())
            items.clear();
        items.add(new SummaryLineItem(1, "zk Spreadsheet RC Released Check this out", "2010/10/17 20:37:12", ((int) ((int) ((Math.random() * 128) + 1))) ));
        items.add(new SummaryLineItem(2, "[zk 5 - Help] RE: SelectedItemConverter Question 3", "2010/10/17 18:31:12", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(3, "[zk 5 - Help] RE: SelectedItemConverter Question 2", "2010/10/17 17:30:12", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(4, "Times_Series Chart help", "2010/10/17 15:26:37", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(5, "RE: Times_Series Chart help", "2010/10/17 14:22:37", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(6, "RE: Times_Series Chart help(Updated)", "2010/10/17 13:26:37", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(7, "[zk 5 - General] Grid Rendering problem", "2010/10/17 10:41:33", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(8, "[zk 5 - Help] RE: SelectedItemConverter Question", "2010/10/17 10:14:27", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(9, "[Personal] RE: requirement of new project", "2010/10/16 13:34:37", ((int) ((Math.random() * 128) + 1)) ));       
        items.add(new SummaryLineItem(10, "[zk 3 - Feature] Client programming Question", "2010/10/15 04:31:12", ((int) ((Math.random() * 128) + 1)) ));       
        items.add(new SummaryLineItem(11, "[zk 5 - Feature] Hlayout/Vlayout Usage", "2010/10/15 04:31:12", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(12, "RE: Times_Series Chart help(Updated)", "2010/10/15 03:26:37", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(13, "[zk 3 - Feature] JQuery support", "2010/10/14 04:31:12", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(14, "[zk 5 - Help] RE: Times_Series Chart help", "2010/10/14 02:43:34", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(15, "[Personal] requirement of new project", "2010/10/14 02:44:35", ((int) ((Math.random() * 128) + 1)) ));
        items.add(new SummaryLineItem(16, "[zk 5 - Help] RE: SelectedItemConverter Question", "2010/10/13 02:14:27", ((int) ((Math.random() * 128) + 1)) ));       
    }
 
    public void revertDeletedSummaryLineItems() {
        initData();
    }
 
    public void deleteAllSummaryLineItems() {
        items.clear();
    }
 
    public void addSummaryLineItems(Collection<SummaryLineItem> c) {
        items.addAll(c);
    }
 
    public List<SummaryLineItem> getSummaryLineItems() {
        return items;
    }
 
    public void deleteSummaryLineItem(SummaryLineItem m) {
        items.remove(m);
    }
     
    public int getSize(){
        return items.size();
    }
}
