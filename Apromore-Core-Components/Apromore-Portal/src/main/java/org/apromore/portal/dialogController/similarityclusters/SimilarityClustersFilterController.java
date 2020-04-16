/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

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


    final class FilterScrollListener implements EventListener<Event> {
        @Override
        public void onEvent(final Event event) throws Exception {
            if (event instanceof ScrollEvent) {
                refreshListbox();
            }
        }
    }

}
