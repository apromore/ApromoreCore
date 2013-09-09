package org.apromore.portal.dialogController.similarityclusters;

import java.util.ArrayList;
import java.util.List;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusterSummaryType;
import org.apromore.portal.dialogController.BaseListboxController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.similarityclusters.renderer.SimilarityClustersItemRenderer;
import org.apromore.portal.dialogController.similarityclusters.visualisation.ClusterVisualisationController;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

/**
 * Controlling the Listbox displaying the result of a similarity cluster search
 * using 'similarityClustersListbox.zul'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersListboxController extends BaseListboxController {

    private static final String ZUL_PAGE = "macros/listbox/similarityClustersListbox.zul";
    private static final String VERTICAL_ALIGN_MIDDLE = "vertical-align:middle;";

    private final SimilarityClustersFilterController filterController;

    /**
     * Creates the Similarity Cluster Listbox.
     *
     * @param mainController      controlling the application.
     * @param filterController    controlling the current search filter.
     * @param fragmentsController controlling the listbox with fragments.
     */
    public SimilarityClustersListboxController(final MainController mainController, final SimilarityClustersFilterController filterController,
            final SimilarityClustersFragmentsListboxController fragmentsController) {
        super(mainController, ZUL_PAGE, new SimilarityClustersItemRenderer());
        this.filterController = filterController;

        Button btnShowVisualisation = (Button) getMainController().getFellow("showVisualisation");
        btnShowVisualisation.setVisible(true);
        btnShowVisualisation.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                if (getListModel().getSelection().size() > 0) {
                    // We can assume it will always be a ArrayList of ClusterType here, because we initialize it in that way.
                    @SuppressWarnings("unchecked")
                    List<ClusterSummaryType> selectedClusters = new ArrayList<ClusterSummaryType>(getListModel().getInnerList());
                    selectedClusters.retainAll(getListModel().getSelection());
                    setAttribute(ClusterVisualisationController.CLUSTER_RESULT_ATTRIBUTE_NAME, selectedClusters, SESSION_SCOPE);
                    Clients.evalJavaScript("window.open('macros/similarityclusters/index.zul','ApromoreVisualisationWindow'+new Date().getTime(),"
                            + "'left=20,top=20,width=1000,height=800,toolbar=0,resizable=1,location=0');");
                } else {
                    Messagebox.show("Please select at least one cluster!");
                }
            }
        });

        // TODO should be replaced by ListModel listener in zk 6
        getListBox().addEventListener("onSelect", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                if (getListBox().getSelectedItems().size() == 1) {
                    fragmentsController.displayClusterFragments((ClusterSummaryType) getListModel().getSelection().iterator().next());
                } else {
                    fragmentsController.clearFragments();
                }
            }
        });
    }

    /* (non-Javadoc)
      * @see org.apromore.portal.dialogController.BaseListboxController#refreshContent()
      */
    @Override
    @SuppressWarnings("unchecked")
    protected final void refreshContent() {
        getListBox().clearSelection();
        getListBox().setModel(new ListModelList<>());
        getListModel().setMultiple(true);

        List<ClusterSummaryType> summary = getService().getClusterSummaries(this.filterController.getCurrentFilter());
        if (summary!= null && !summary.isEmpty()) {
            getListModel().addAll(summary);
        }
        getMainController().displayMessage(buildRefreshMessage());
    }

    /**
     * Display similarity clusters contained in the specified filter.
     *
     * @param filter to use retrieving the clusters
     */
    public final void displaySimilarityClusters(final ClusterFilterType filter) {
        this.filterController.setCurrentFilter(filter);
        refreshContent();
    }

    /**
     * @return message for status line
     */
    private String buildRefreshMessage() {
        String message;
        message = "SimilarityClusters found " + getListModel().size();
        if (getListModel().size() > 1) {
            message += " clusters.";
        } else {
            message += " cluster.";
        }
        return message;
    }

}
