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
     *
     * @param filterWrapper
     * @param externalScrollListener
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

    /**
     * @param currentFilter
     */
    public void setCurrentFilter(ClusterFilterType currentFilter) {
        this.currentFilter = currentFilter;
        refreshSliderPositionFromFilter();
        refreshLabelsFromFilter();
    }

    private void initFilterConstraints() {
        ClusteringSummaryType summary = getService().getClusteringSummary();
        this.minAverageFragmentSize.setMaxpos(convertFromMinAvgFragmentSize(summary.getMaxAvgFragmentSize()));
        this.maxAverageFragmentSize.setMaxpos(convertFromMaxAvgFragmentSize(summary.getMaxAvgFragmentSize()));
        this.minClusterSize.setMaxpos(convertFromClusterSize(summary.getMaxClusterSize()));
        this.maxClusterSize.setMaxpos(convertFromClusterSize(summary.getMaxClusterSize()));
        this.minBenefitCostRatio.setMaxpos(convertFromBCR(summary.getMaxBCR()));
        this.maxBenefitCostRatio.setMaxpos(convertFromBCR(summary.getMaxBCR()));
    }

    private void refreshSliderPositionFromFilter() {
        int currentMinAvgFragmentSize = convertFromMinAvgFragmentSize(currentFilter.getMinAvgFragmentSize());
        this.minAverageFragmentSize.setCurpos(currentMinAvgFragmentSize);

        int currentMaxAvgFragmentSize = convertFromMaxAvgFragmentSize(currentFilter.getMaxAvgFragmentSize());
        this.maxAverageFragmentSize.setCurpos(currentMaxAvgFragmentSize);

        int currentMinClusterSize = convertFromClusterSize(currentFilter.getMinClusterSize());
        this.minClusterSize.setCurpos(currentMinClusterSize);

        int currentMaxClusterSize = convertFromClusterSize(currentFilter.getMaxClusterSize());
        this.maxClusterSize.setCurpos(currentMaxClusterSize);

        int currentMinBCR = convertFromBCR(currentFilter.getMinBCR());
        this.minBenefitCostRatio.setCurpos(currentMinBCR);

        int currentMaxBCR = convertFromBCR(currentFilter.getMaxBCR());
        this.maxBenefitCostRatio.setCurpos(currentMaxBCR);
    }

    private void refreshLabelsFromFilter() {

        int currentMinAvgFragmentSize = convertFromMinAvgFragmentSize(currentFilter.getMinAvgFragmentSize());
        updateLabel(this.minAverageFragmentSize, currentMinAvgFragmentSize);

        int currentMaxAvgFragmentSize = convertFromMaxAvgFragmentSize(currentFilter.getMaxAvgFragmentSize());
        updateLabel(this.maxAverageFragmentSize, currentMaxAvgFragmentSize);

        int currentMinClusterSize = convertFromClusterSize(currentFilter.getMinClusterSize());
        updateLabel(this.minClusterSize, currentMinClusterSize);

        int currentMaxClusterSize = convertFromClusterSize(currentFilter.getMaxClusterSize());
        updateLabel(this.maxClusterSize, currentMaxClusterSize);

        int currentMinBCR = convertFromBCR(currentFilter.getMinBCR());
        updateLabel(this.minBenefitCostRatio, currentMinBCR);

        int currentMaxBCR = convertFromBCR(currentFilter.getMaxBCR());
        updateLabel(this.maxBenefitCostRatio, currentMaxBCR);
    }

    /**
     * Updates the Label next to a Slider
     *
     * @param slider
     * @param currentMinAvgFragmentSize
     */
    private void updateLabel(Component slider, int currentMinAvgFragmentSize) {
        ((Label) slider.getNextSibling()).setValue(String.valueOf(currentMinAvgFragmentSize));
    }

    /**
     * @return
     */
    public ClusterFilterType getCurrentFilter() {
        return currentFilter;
    }

    /**
     * @return
     */
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

    private int convertFromBCR(final double bcr) {
        long roundedValue = Math.round(bcr);
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
