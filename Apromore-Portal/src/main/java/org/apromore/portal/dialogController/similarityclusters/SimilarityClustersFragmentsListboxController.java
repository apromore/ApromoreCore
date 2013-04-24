package org.apromore.portal.dialogController.similarityclusters;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apromore.model.ClusterSummaryType;
import org.apromore.model.FragmentData;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.BaseDetailController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.similarityclusters.renderer.SimilarityFragmentsItemRenderer;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * Controlling the Listbox displaying the fragments that belong to a selected cluster
 * using 'fragmentDetail.zul'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersFragmentsListboxController extends BaseDetailController {

    private static final String ZUL_PAGE = "macros/detail/fragmentDetail.zul";

    private static final long serialVersionUID = 7365269088573309617L;

    private final Listbox listBox;

    /**
     * @param mainController the main controller of the application.
     */
    public SimilarityClustersFragmentsListboxController(final MainController mainController) {
        super(mainController);

        this.listBox = ((Listbox) Executions.createComponents(ZUL_PAGE, getMainController(), null));

        getListBox().setItemRenderer(new SimilarityFragmentsItemRenderer());
        getListBox().setModel(new ListModelList());

        appendChild(getListBox());
    }

    /**
     * @return actual instance of Listbox.
     */
    public final Listbox getListBox() {
        return listBox;
    }

    /**
     * Display the Fragments belonging to a Cluster
     *
     * @param cluster which's fragments should be displayed.
     */
    public final void displayClusterFragments(final ClusterSummaryType cluster) {
        getListModel().clearSelection();
        getListModel().clear();
        List<FragmentData> fragments = getService().getCluster(cluster.getClusterId()).getFragments();
        // Should be sorted in backend
        Collections.sort(fragments, new Comparator<FragmentData>() {

            @Override
            public int compare(FragmentData o1, FragmentData o2) {
                return Double.valueOf(o1.getDistance()).compareTo(o2.getDistance());
            }

        });
        getListModel().addAll(fragments);
    }

    /**
     * @return the underlying model
     */
    public final ListModelList getListModel() {
        return (ListModelList) listBox.getListModel();
    }

    /**
     * Empty the Listbox
     */
    public void clearFragments() {
        getListModel().clear();
    }

}
