package org.apromore.portal.dialogController;

import org.apromore.model.ProcessSummariesType;
import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

public class NavigationController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NavigationController.class.getName());

    private MainController mainC;
    private Panel navigationP;
    private Tree tree;
    private Window treeW;
    private ProcessSummariesType processSummaries;

    public NavigationController(MainController mainC) throws Exception {
        this.mainC = mainC;

        this.navigationP = (Panel) this.mainC.getFellow("navigationcomp").getFellow("navigationPanel");
        this.treeW = (Window) this.navigationP.getFellow("treeW");
        this.tree = (Tree) this.navigationP.getFellow("treeW").getFellow("tree");

        setHflex("true");
        setVflex("true");
    }

    public void loadWorkspace() {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false).getRoot());
        tree.setItemRenderer(new FolderTreeRenderer(mainC));
        tree.setModel(model);
    }

    /**
     * process of domains associated with opened branches are selected
     */
    protected void displayProcessRowDetails(Event event) throws Exception {
        LOGGER.debug(this.tree.getSelectedItem().getLabel());
    }
    /*
     private void buildDomainIndex(ProcessSummariesType processSummaries) {
         DomainIndex index = this.mainC.getDomainIndex();

         for (int i=0;i<processSummaries.getProcessSummary().size();i++){
             ProcessSummaryType process = processSummaries.getProcessSummary().get(i);
             *//**
     * insert process in index (ordered on domains)
     *//*
			// Is process domain present?
			int j = 0;
			// while process domain less than indexed value
			while (j<index.getEntries().size() 
					&& process.getDomain().compareTo(index.getEntries().get(j).getDomain()) > 0) {
				j++;
			}
			if (j==index.getEntries().size()) {
				// process index does not exist in index and has the highest domain
				DomainEntry entry = new DomainEntry(process.getDomain());
				index.getEntries().add(entry);
			} else {
				if (process.getDomain().compareTo(index.getEntries().get(j).getDomain()) == 0) {
					// process index exists in index at position j
					index.getEntries().elementAt(j).getProcessSummaries().add(process);
				} else {
					// new entry must inserted before j

					DomainEntry entry = new DomainEntry(process.getDomain());
					index.getEntries().add(j,entry);
				}
			}
		}

	}*/

}
