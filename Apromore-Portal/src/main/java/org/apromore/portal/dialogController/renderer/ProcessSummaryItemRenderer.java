package org.apromore.portal.dialogController.renderer;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.util.List;

public class ProcessSummaryItemRenderer implements ListitemRenderer {

    /* (non-Javadoc)
      * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object)
      */
    @Override
    public void render(Listitem listItem, Object obj) {
        renderProcessSummary(listItem, (ProcessSummaryType) obj);
    }

    private void renderProcessSummary(Listitem listItem, final ProcessSummaryType process) {
        listItem.appendChild(new Listcell()); // Built-In Checkbox
        listItem.appendChild(renderProcessScore(process));
        listItem.appendChild(renderProcessId(process));
        listItem.appendChild(renderProcessName(process));
        listItem.appendChild(renderProcessNativeType(process));
        listItem.appendChild(renderProcessDomain(process));
        listItem.appendChild(renderProcessRankingHB(process));
        listItem.appendChild(renderProcessLastVersion(process));
        listItem.appendChild(renderProcessOwner(process));
    }

    protected Listcell renderProcessOwner(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOwner()));
    }

    protected Listcell renderProcessLastVersion(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getLastVersion()));
    }

    protected Listcell renderProcessDomain(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getDomain()));
    }

    protected Listcell renderProcessNativeType(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getOriginalNativeType()));
    }

    protected Listcell renderProcessName(final ProcessSummaryType process) {
        return wrapIntoListCell(new Label(process.getName()));
    }

    protected Listcell renderProcessRankingHB(final ProcessSummaryType process) {
        Hbox processRankingHB = new Hbox();
        if (process.getRanking() != null && process.getRanking().toString().compareTo("") != 0) {
            displayRanking(processRankingHB, process.getRanking());
        }
        return wrapIntoListCell(processRankingHB);
    }

    protected Listcell renderProcessId(final ProcessSummaryType process) {
        Label processIdLb = new Label(process.getId().toString());
        return wrapIntoListCell(processIdLb);
    }

    protected Listcell renderProcessScore(final ProcessSummaryType process) {
        Label processScoreLb = new Label();

        List<VersionSummaryType> processVersions = process.getVersionSummaries();
        // find the score of the latest version, if any: this a one which will  be displayed with the process
        int i = 0;
        while (i < processVersions.size() && processVersions.get(i).getName() != null && processVersions.get(i).getName().compareTo(process.getLastVersion()) != 0) {
            i++;
        }

        i = i - 1;

        // Each process should have at least one version. So it should have a legal value which
        // is the index of the process latest version. But some are faulty!!!
        if (i < processVersions.size() && processVersions.get(i).getScore() != null) {
            processScoreLb.setValue(roundToDecimals(processVersions.get(i).getScore(), 4).toString());
        } else {
            processScoreLb.setValue("1.0");
        }

        return wrapIntoListCell(processScoreLb);
    }

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        lc.appendChild(cp);
        return lc;
    }

    /**
     * Display in hbox versionRanking, 5 stars according to ranking (0...5).
     * Pre-condition: ranking is a non empty string. TODO: allow users to rank a
     * process version directly by interacting with the stars displayed.
     *
     * @param ranking
     */
    private void displayRanking(Hbox rankingHb, String ranking) {
        String imgFull = Constants.STAR_FULL_ICON;
        String imgMid = Constants.STAR_MID_ICON;
        String imgBlank = Constants.STAR_BLK_ICON;
        Image star;
        Float rankingF = Float.parseFloat(ranking);
        int fullStars = rankingF.intValue();
        int i;
        for (i = 1; i <= fullStars; i++) {
            star = new Image();
            rankingHb.appendChild(star);
            star.setSrc(imgFull);
        }
        if (i <= 5) {
            if (Math.floor(rankingF) != rankingF) {
                star = new Image();
                star.setSrc(imgMid);
                rankingHb.appendChild(star);
                i = i + 1;
            }
            for (int j = i; j <= 5; j++) {
                star = new Image();
                star.setSrc(imgBlank);
                rankingHb.appendChild(star);
            }
        }
    }

    public static Double roundToDecimals(Double num, int places) {
        int temp = (int) ((num * Math.pow(10, places)));
        return ((double) temp) / Math.pow(10, places);
    }

}
