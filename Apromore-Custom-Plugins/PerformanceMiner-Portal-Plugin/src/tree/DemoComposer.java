package demo.tree.dynamic_tree;
 
import java.util.HashMap;
 
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
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
 
import demo.data.ContactList;
import demo.data.pojo.Contact;
 
public class DemoComposer extends SelectorComposer<Component> {
    private static final long serialVersionUID = 3814570327995355261L;
     
    @Wire
    private Window demoWindow;
    @Wire
    private Tree tree;
 
    private AdvancedTreeModel contactTreeModel;
 
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);     
        contactTreeModel = new AdvancedTreeModel(new ContactList().getRoot());
        tree.setItemRenderer(new ContactTreeRenderer());
        tree.setModel(contactTreeModel);
    }
 
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
    private final class ContactTreeRenderer implements TreeitemRenderer<ContactTreeNode> {
        @Override
        public void render(final Treeitem treeItem, ContactTreeNode treeNode, int index) throws Exception {
            ContactTreeNode ctn = treeNode;
            Contact contact = (Contact) ctn.getData();
            Treerow dataRow = new Treerow();
            dataRow.setParent(treeItem);
            treeItem.setValue(ctn);
            treeItem.setOpen(ctn.isOpen());
 
            if (!isCategory(contact)) { // Contact Row
                Hlayout hl = new Hlayout();
                hl.appendChild(new Image("/widgets/tree/dynamic_tree/img/" + contact.getProfilepic()));
                hl.appendChild(new Label(contact.getName()));
                hl.setSclass("h-inline-block");
                Treecell treeCell = new Treecell();
                treeCell.appendChild(hl);
                dataRow.setDraggable("true");
                dataRow.appendChild(treeCell);
                dataRow.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        ContactTreeNode clickedNodeValue = (ContactTreeNode) ((Treeitem) event.getTarget().getParent())
                                .getValue();
                        Window w = new Window("ZK IM - " + ((Contact) clickedNodeValue.getData()).getName(), "normal",
                                true);
                        w.setPosition("parent");
                        w.setParent(demoWindow);
                        HashMap<String, String> dataArgs = new HashMap<String, String>();
                        dataArgs.put("name", clickedNodeValue.getData().getName());
                        Executions.createComponents("/widgets/tree/dynamic_tree/dialog.zul", w, dataArgs);
                        w.doOverlapped();
                    }
                });
            } else { // Category Row
                dataRow.appendChild(new Treecell(contact.getCategory()));
            }
            // Both category row and contact row can be item dropped
            dataRow.setDroppable("true");
            dataRow.addEventListener(Events.ON_DROP, new EventListener<Event>() {
                @SuppressWarnings("unchecked")
                @Override
                public void onEvent(Event event) throws Exception {
                    // The dragged target is a TreeRow belongs to an
                    // Treechildren of TreeItem.
                    Treeitem draggedItem = (Treeitem) ((DropEvent) event).getDragged().getParent();
                    ContactTreeNode draggedValue = (ContactTreeNode) draggedItem.getValue();
                    Treeitem parentItem = treeItem.getParentItem();
                    contactTreeModel.remove(draggedValue);
                    if (isCategory((Contact) ((ContactTreeNode) treeItem.getValue()).getData())) {
                        contactTreeModel.add((ContactTreeNode) treeItem.getValue(),
                                new DefaultTreeNode[] { draggedValue });
                    } else {
                        int index = parentItem.getTreechildren().getChildren().indexOf(treeItem);
                        if(parentItem.getValue() instanceof ContactTreeNode) {
                            contactTreeModel.insert((ContactTreeNode)parentItem.getValue(), index, index,
                                    new DefaultTreeNode[] { draggedValue });
                        }
                         
                    }
                }
            });
 
        }
 
        private boolean isCategory(Contact contact) {
            return contact.getName() == null;
        }
    }
}