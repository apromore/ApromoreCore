package org.apromore.portal.dialogController;

import org.apromore.model.ProcessSummariesType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

public class NavigationController extends Window {

    private MainController mainC;
    private Panel navigationP;
    private Tree tree;
    private Listbox treeChoice;
    private Window treeW;
    private ProcessSummariesType processSummaries;

    public NavigationController(MainController mainC) throws Exception {
        this.mainC = mainC;

        /**
         * Get commponents
         */
        this.navigationP = (Panel) this.mainC.getFellow("navigationcomp").getFellow("navigationPanel");
        this.treeChoice = (Listbox) this.navigationP.getFellow("treeChoice");
        this.treeW = (Window) this.navigationP.getFellow("treeW");
        this.tree = (Tree) this.navigationP.getFellow("treeW").getFellow("tree");


    }

    protected void displayProcessRowDetails(Event event) throws Exception {

        /**
         * process of domains associated with opened branches are selected
         */

        System.out.println(this.tree.getSelectedItem().getLabel());

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
