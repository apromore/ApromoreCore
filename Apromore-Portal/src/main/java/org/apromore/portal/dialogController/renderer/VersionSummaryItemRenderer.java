package org.apromore.portal.dialogController.renderer;

import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class VersionSummaryItemRenderer implements ListitemRenderer {

    /*
     * (non-Javadoc)
     * @see org.zkoss.zul.ListitemRenderer#render(org.zkoss.zul.Listitem, java.lang.Object, int)
     */
    @Override
    public void render(Listitem listItem, Object obj, int index) {
        renderVersionSummary(listItem, (VersionSummaryType) obj);
    }

    private void renderVersionSummary(Listitem listItem, VersionSummaryType version) {
        listItem.appendChild(renderVersionName(version));
        listItem.appendChild(renderVersionVersion(version));
        listItem.appendChild(renderVersionLastUpdate(version));
        listItem.appendChild(renderVersionAnnotations(version));
    }

    private Component renderVersionVersion(VersionSummaryType version) {
        return wrapIntoListCell(new Label(version.getVersionNumber().toString()));
    }

    private Component renderVersionAnnotations(VersionSummaryType version) {
        Listbox annotationLB = new Listbox();
        Listitem annotationsI = new Listitem();
        annotationLB.setMold("select");
        annotationLB.setRows(1);
        annotationLB.setWidth("100%");
        annotationLB.setStyle(Constants.UNSELECTED_VERSION);
        if (version.getAnnotations().size() > 0) {
            for (int i = 0; i < version.getAnnotations().size(); i++) {
                String language = version.getAnnotations().get(i).getNativeType();
                for (int k = 0; k < version.getAnnotations().get(i).getAnnotationName().size(); k++) {
                    annotationLB.appendChild(annotationsI);
                    annotationsI.setLabel(version.getAnnotations().get(i).getAnnotationName().get(k) + " (" + language + ")");
                }
            }
            annotationLB.selectItem(annotationLB.getItemAtIndex(version.getAnnotations().size() - 1));
        } else {
            annotationLB.appendChild(annotationsI);
            annotationsI.setLabel("No Annotations Found!");
            annotationLB.selectItem(annotationLB.getItemAtIndex(0));
        }
        return wrapIntoListCell(annotationLB);
    }

    private Component renderVersionLastUpdate(VersionSummaryType version) {
        if (version.getLastUpdate() == null || version.getLastUpdate().isEmpty()) {
            return wrapIntoListCell(new Label(version.getCreationDate()));
        } else {
            return wrapIntoListCell(new Label(version.getLastUpdate()));
        }
    }

    private Component renderVersionName(VersionSummaryType version) {
        return wrapIntoListCell(new Label(version.getName()));
    }

    private Listcell wrapIntoListCell(Component cp) {
        Listcell lc = new Listcell();
        lc.appendChild(cp);
        return lc;
    }

//    /**
//     * Display in hbox versionRanking, 5 stars according to ranking (0...5).
//     * Pre-condition: ranking is a non empty string. TODO: allow users to rank a
//     * process version directly by interacting with the stars displayed.
//     *
//     * @param ranking
//     */
//    private void displayRanking(Hbox rankingHb, String ranking) {
//        String imgFull = Constants.STAR_FULL_ICON;
//        String imgMid = Constants.STAR_MID_ICON;
//        String imgBlank = Constants.STAR_BLK_ICON;
//        Image star;
//        Float rankingF = Float.parseFloat(ranking);
//        int fullStars = rankingF.intValue();
//        int i;
//        for (i = 1; i <= fullStars; i++) {
//            star = new Image();
//            rankingHb.appendChild(star);
//            star.setSrc(imgFull);
//        }
//        if (i <= 5) {
//            if (Math.floor(rankingF) != rankingF) {
//                star = new Image();
//                star.setSrc(imgMid);
//                rankingHb.appendChild(star);
//                i = i + 1;
//            }
//            for (int j = i; j <= 5; j++) {
//                star = new Image();
//                star.setSrc(imgBlank);
//                rankingHb.appendChild(star);
//            }
//        }
//    }

}
