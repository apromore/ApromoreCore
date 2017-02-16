/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apromore.plugin.portal.perfmining.view;

import java.io.IOException;
import java.util.HashMap;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.perfmining.DatasetFactory;
import org.apromore.plugin.portal.perfmining.Visualization;
import org.apromore.service.perfmining.models.SPF;
 
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
 
public class ResultWindowController {
    private static final long serialVersionUID = 3814570327995355261L;
    private final PortalContext portalContext;
    private Window resultW;
    private Tree tree;
    private AdvancedTreeModel spfViewTreeModel;
    
    public ResultWindowController(PortalContext portalContext, SPF spf) throws IOException {
        this.portalContext = portalContext;
        this.resultW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/result.zul", null, null);
        this.resultW.setTitle("Performance Mining Result");
        
        // Set up UI elements
        tree = (Tree) this.resultW.getFellow("tree");
        spfViewTreeModel = new AdvancedTreeModel(new SPFViewList(spf).getRoot());
        tree.setItemRenderer(new SPFViewTreeRenderer());
        tree.setModel(spfViewTreeModel);        
        
        // Show the window
        this.resultW.doModal();        
    }
 
//    public void doAfterCompose(Component comp) throws Exception {
//        super.doAfterCompose(comp);     
//        contactTreeModel = new AdvancedTreeModel(new SPFViewList().getRoot());
//        tree.setItemRenderer(new SPFViewTreeRenderer());
//        tree.setModel(contactTreeModel);
//    }
 
    /**
     * The structure of tree
     * 
     * <pre>
     * &lt;treeitem>
     *   &lt;treerow>
     *     &lt;treecell>...&lt;/treecell>
     *   &lt;/treerow>
     *   &lt;treechildren>
     *     &lt;treeitem>...&lt;/treeitem>
     *   &lt;/treechildren>
     * &lt;/treeitem>
     * </pre>
     */
    private final class SPFViewTreeRenderer implements TreeitemRenderer<SPFViewTreeNode> {
        @Override
        public void render(final Treeitem treeItem, SPFViewTreeNode treeNode, int index) throws Exception {
            SPFViewTreeNode ctn = treeNode;
            SPFView spfView = (SPFView) ctn.getData();
            Treerow dataRow = new Treerow();
            dataRow.setParent(treeItem);
            treeItem.setValue(ctn);
            treeItem.setOpen(ctn.isOpen());
 
            if (!spfView.isCategory) { // SPFView Row
                Hlayout hl = new Hlayout();
                //hl.appendChild(new Image("/widgets/tree/dynamic_tree/img/"));
                hl.appendChild(new Label(spfView.getFullName()));
                hl.setSclass("h-inline-block");
                Treecell treeCell = new Treecell();
                treeCell.appendChild(hl);
                dataRow.setDraggable("false");
                dataRow.appendChild(treeCell);
                dataRow.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        SPFViewTreeNode clickedNodeValue = (SPFViewTreeNode) ((Treeitem) event.getTarget().getParent())
                                .getValue();
                        ((SPFView)clickedNodeValue.getData()).showChart(portalContext);
                    }
                });
            } else { // Category Row
                dataRow.appendChild(new Treecell(spfView.getFullName()));
            }
 
        }
    }
}
