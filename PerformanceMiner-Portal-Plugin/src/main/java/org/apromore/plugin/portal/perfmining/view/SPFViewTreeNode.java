/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view;

import org.zkoss.zul.DefaultTreeNode;
import org.apromore.plugin.portal.perfmining.view.SPFView;
 
public class SPFViewTreeNode extends DefaultTreeNode<SPFView> {
    private static final long serialVersionUID = -7012663776755277499L;
     
    private boolean open = false;
 
    public SPFViewTreeNode(SPFView data, DefaultTreeNode<SPFView>[] children) {
        super(data, children);
    }
 
    public SPFViewTreeNode(SPFView data, DefaultTreeNode<SPFView>[] children, boolean open) {
        super(data, children);
        setOpen(open);
    }
 
    public SPFViewTreeNode(SPFView data) {
        super(data);
 
    }
 
    public boolean isOpen() {
        return open;
    }
 
    public void setOpen(boolean open) {
        this.open = open;
    }
 
}