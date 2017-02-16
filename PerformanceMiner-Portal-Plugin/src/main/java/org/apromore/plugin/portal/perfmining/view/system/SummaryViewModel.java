/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view.system;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
 
public class SummaryViewModel {
     
    SummaryData summaryData = new SummaryData();
     
    public SummaryData getData() {
        return summaryData;
    }
 
//    @Command
//    @NotifyChange("mailData")
//    public void revertItem() {
//        mailData.revertDeletedItems();
//    }
//     
//    @Command
//    @NotifyChange("mailData")
//    public void deleteAllItems() {
//        mailData.deleteAllItems();
//    }
//     
//    @Command
//    @NotifyChange("mailData")
//    public void removeItem(@BindingParam("mail") SummaryLineItem myItem) {
//        mailData.deleteItem(myItem);
//    }
}
