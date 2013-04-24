package org.apromore.portal.dialogController.similarityclusters.renderer;

import java.text.NumberFormat;

import org.apromore.model.ClusterSummaryType;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Responsible for rendering one row of the cluster result listbox defined in 'similarityClustersListbox.zul'.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt</a>
 */
public class SimilarityClustersItemRenderer implements ListitemRenderer {

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object)
      */
    @Override
    public void render(Listitem listItem, Object obj) {
        renderSimilarityCluster(listItem, (ClusterSummaryType) obj);
    }

    private void renderSimilarityCluster(Listitem listItem, final ClusterSummaryType obj) {
        listItem.appendChild(new Listcell()); // Built-In Checkbox
        listItem.appendChild(renderClusterId(obj));
        listItem.appendChild(renderClusterName(obj));
        listItem.appendChild(renderClusterSize(obj));
        listItem.appendChild(renderAvgFragmentSize(obj));
        listItem.appendChild(renderRefactoringGain(obj));
        listItem.appendChild(renderStandardizationEffort(obj));
    }

    private Listcell renderClusterId(final ClusterSummaryType obj) {
        return new Listcell(obj.getClusterId().toString());
    }

    private Listcell renderClusterName(final ClusterSummaryType obj) {
        return new Listcell(obj.getClusterLabel());
    }

    private Listcell renderClusterSize(final ClusterSummaryType obj) {
        return new Listcell(String.valueOf(obj.getClusterSize()));
    }

    private Listcell renderAvgFragmentSize(final ClusterSummaryType obj) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        String fragmentSize = numberInstance.format(obj.getAvgFragmentSize());
        return new Listcell(fragmentSize);
    }

    private Listcell renderRefactoringGain(final ClusterSummaryType obj) {
        return new Listcell(String.valueOf(obj.getRefactoringGain()));
    }

    private Listcell renderStandardizationEffort(final ClusterSummaryType obj) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(2);
        String standardizationEffort = numberInstance.format(obj.getStandardizationEffort());
        return new Listcell(standardizationEffort);
    }

}
