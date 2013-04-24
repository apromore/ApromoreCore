package org.apromore.portal.dialogController.similarityclusters;

import org.apromore.model.ClusterFilterType;
import org.apromore.portal.dialogController.BaseFilterController;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ScrollEvent;
import org.zkoss.zul.Grid;

/**
 * Controlls the Window containing the re-filter components.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersFilterController extends BaseFilterController {

    final class FilterScrollListener implements EventListener {
        @Override
        public void onEvent(final Event event) throws Exception {
            if (event instanceof ScrollEvent) {
                refreshListbox();
            }
        }
    }

    private static final long serialVersionUID = 5542144067417950810L;
    private final SimilarityClustersFilterProperties propertiesController;

    public SimilarityClustersFilterController(MainController mainController) {
        super(mainController);
        Grid filterGrid = ((Grid) Executions.createComponents("macros/filter/similarityClustersFilter.zul", mainController, null));
        propertiesController = new SimilarityClustersFilterProperties(filterGrid, new FilterScrollListener());
        appendChild(filterGrid);
    }

    private void refreshListbox() {
        ClusterFilterType filter = propertiesController.buildClusterFilter();
        getMainController().displaySimilarityClusters(filter);
    }

    public void setCurrentFilter(ClusterFilterType filter) {
        propertiesController.setCurrentFilter(filter);
    }

    public ClusterFilterType getCurrentFilter() {
        return propertiesController.getCurrentFilter();
    }

}
