/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController.similarityclusters;

import org.apromore.model.ClusterSummaryType;
import org.apromore.model.FragmentData;
import org.apromore.portal.dialogController.BaseDetailController;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.similarityclusters.renderer.SimilarityFragmentsItemRenderer;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.South;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        ((South) getMainController().getFellow("leftSouthPanel")).setTitle("Cluster Details");

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
