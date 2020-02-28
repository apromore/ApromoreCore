/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

package org.apromore.portal.dialogController.similarityclusters;

import org.apromore.model.ClusterFilterType;
import org.apromore.model.ClusteringSummaryType;
import org.apromore.portal.dialogController.BaseController;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.ScrollEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Slider;

/**
 * Controlling just the current filter properties, not the actual Window.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersFilterProperties extends BaseController {

    private static final long serialVersionUID = 3023944678410096666L;
    private final Slider minClusterSize;
    private final Slider maxClusterSize;
    private final Slider minAverageFragmentSize;
    private final Slider maxAverageFragmentSize;
    private final Slider minBenefitCostRatio;
    private final Slider maxBenefitCostRatio;

    private ClusterFilterType currentFilter;

    /**
     * Creates the Filter controller using the specified Component and EventListener.
     */
    public SimilarityClustersFilterProperties(final Component filterWrapper, final EventListener<Event> externalScrollListener) {
        super();

        // Wire Slider
        this.minClusterSize = (Slider) filterWrapper.getFellow("minclustersize");
        this.maxClusterSize = (Slider) filterWrapper.getFellow("maxclustersize");
        this.minAverageFragmentSize = (Slider) filterWrapper.getFellow("minavgfragmentsize");
        this.maxAverageFragmentSize = (Slider) filterWrapper.getFellow("maxavgfragmentsize");
        this.minBenefitCostRatio = (Slider) filterWrapper.getFellow("minbcr");
        this.maxBenefitCostRatio = (Slider) filterWrapper.getFellow("maxbcr");

        this.currentFilter = buildClusterFilter();

        FilterScrollListener internalScrollListener = new FilterScrollListener();

        this.minClusterSize.addEventListener("onScroll", internalScrollListener);
        this.maxClusterSize.addEventListener("onScroll", internalScrollListener);
        this.minAverageFragmentSize.addEventListener("onScroll", internalScrollListener);
        this.maxAverageFragmentSize.addEventListener("onScroll", internalScrollListener);
        this.minBenefitCostRatio.addEventListener("onScroll", internalScrollListener);
        this.maxBenefitCostRatio.addEventListener("onScroll", internalScrollListener);

        this.minClusterSize.addEventListener("onScroll", externalScrollListener);
        this.maxClusterSize.addEventListener("onScroll", externalScrollListener);
        this.minAverageFragmentSize.addEventListener("onScroll", externalScrollListener);
        this.maxAverageFragmentSize.addEventListener("onScroll", externalScrollListener);
        this.minBenefitCostRatio.addEventListener("onScroll", externalScrollListener);
        this.maxBenefitCostRatio.addEventListener("onScroll", externalScrollListener);

        initFilterConstraints();
    }

    public void setCurrentFilter(ClusterFilterType currentFilter) {
        this.currentFilter = currentFilter;
        refreshSliderPositionFromFilter();
        refreshLabelsFromFilter();
    }

    private void initFilterConstraints() {
        ClusteringSummaryType summary = getService().getClusteringSummary();
        this.minClusterSize.setCurpos(convertFromClusterSize(summary.getMinClusterSize()));
        this.minClusterSize.setMaxpos(convertFromClusterSize(summary.getMaxClusterSize()));
        this.maxClusterSize.setCurpos(convertFromClusterSize(summary.getMinClusterSize()));
        this.maxClusterSize.setMaxpos(convertFromClusterSize(summary.getMaxClusterSize()));

        this.minAverageFragmentSize.setCurpos(convertFromMinAvgFragmentSize(summary.getMinAvgFragmentSize()));
        this.minAverageFragmentSize.setMaxpos(convertFromMaxAvgFragmentSize(summary.getMaxAvgFragmentSize()));
        this.maxAverageFragmentSize.setCurpos(convertFromMinAvgFragmentSize(summary.getMinAvgFragmentSize()));
        this.maxAverageFragmentSize.setMaxpos(convertFromMaxAvgFragmentSize(summary.getMaxAvgFragmentSize()));

        this.minBenefitCostRatio.setCurpos(convertFromMinBCR(summary.getMinBCR()));
        this.minBenefitCostRatio.setMaxpos(convertFromMaxBCR(summary.getMaxBCR()));
        this.maxBenefitCostRatio.setCurpos(convertFromMinBCR(summary.getMinBCR()));
        this.maxBenefitCostRatio.setMaxpos(convertFromMaxBCR(summary.getMaxBCR()));
    }

    private void refreshSliderPositionFromFilter() {
        this.minClusterSize.setCurpos(convertFromClusterSize(currentFilter.getMinClusterSize()));
        this.maxClusterSize.setCurpos(convertFromClusterSize(currentFilter.getMaxClusterSize()));
        this.minAverageFragmentSize.setCurpos(convertFromMinAvgFragmentSize(currentFilter.getMinAvgFragmentSize()));
        this.maxAverageFragmentSize.setCurpos(convertFromMaxAvgFragmentSize(currentFilter.getMaxAvgFragmentSize()));
        this.minBenefitCostRatio.setCurpos(convertFromMinBCR(currentFilter.getMinBCR()));
        this.maxBenefitCostRatio.setCurpos(convertFromMaxBCR(currentFilter.getMaxBCR()));
    }

    private void refreshLabelsFromFilter() {
        updateLabel(this.minClusterSize, convertFromClusterSize(currentFilter.getMinClusterSize()));
        updateLabel(this.maxClusterSize, convertFromClusterSize(currentFilter.getMaxClusterSize()));
        updateLabel(this.minAverageFragmentSize, convertFromMinAvgFragmentSize(currentFilter.getMinAvgFragmentSize()));
        updateLabel(this.maxAverageFragmentSize, convertFromMaxAvgFragmentSize(currentFilter.getMaxAvgFragmentSize()));
        updateLabel(this.minBenefitCostRatio, convertFromMinBCR(currentFilter.getMinBCR()));
        updateLabel(this.maxBenefitCostRatio, convertFromMaxBCR(currentFilter.getMaxBCR()));
    }



    /**
     * Updates the Label next to a Slider
     */
    private void updateLabel(Component slider, int sliderPos) {
        ((Label) slider.getNextSibling()).setValue(String.valueOf(sliderPos));
    }

    public ClusterFilterType getCurrentFilter() {
        return currentFilter;
    }

    public ClusterFilterType buildClusterFilter() {
        ClusterFilterType filter = new ClusterFilterType();
        filter.setMinClusterSize(convertToClusterSize(this.minClusterSize.getCurpos()));
        filter.setMaxClusterSize(convertToClusterSize(this.maxClusterSize.getCurpos()));
        filter.setMinAvgFragmentSize(convertToAvgFragmentSize(this.minAverageFragmentSize.getCurpos()));
        filter.setMaxAvgFragmentSize(convertToAvgFragmentSize(this.maxAverageFragmentSize.getCurpos()));
        filter.setMinBCR(convertToBCR(this.minBenefitCostRatio.getCurpos()));
        filter.setMaxBCR(convertToBCR(this.maxBenefitCostRatio.getCurpos()));
        return filter;
    }

    private static float convertToAvgFragmentSize(final int sliderPos) {
        return sliderPos;
    }

    private int convertToClusterSize(final int sliderPos) {
        return sliderPos;
    }

    private double convertToBCR(final int sliderPos) {
        return sliderPos;
    }

    private int convertFromMinBCR(final double bcr) {
        long roundedValue = (int) Math.floor(bcr);
        if (roundedValue > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) roundedValue;
        }
    }

    private int convertFromMaxBCR(final double bcr) {
        long roundedValue = (int) Math.ceil(bcr);
        if (roundedValue > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return (int) roundedValue;
        }
    }

    private int convertFromMaxAvgFragmentSize(final float avgFragmentSize) {
        if (avgFragmentSize > 0.0) {
            return (int) Math.floor(avgFragmentSize);
        }
        return 1;
    }

    private int convertFromMinAvgFragmentSize(final float avgFragmentSize) {
        if (avgFragmentSize > 0.0) {
            return (int) Math.ceil(avgFragmentSize);
        }
        return 1;
    }

    private int convertFromClusterSize(final int clusterSize) {
        return clusterSize;
    }



    /**
     * Event used onScroll
     */
    private final class FilterScrollListener implements EventListener<Event> {
        @Override
        public void onEvent(final Event event) throws Exception {

            if (event instanceof ScrollEvent) {
                refreshLabelsFromFilter();
            }

        }
    }
}
