package org.apromore.portal.controller;

import java.util.List;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PopupMenuController;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.LogSummaryType;
//import org.apromore.ppm.logic.services.PredictorStorage;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

public class PredictorTrainerSubMenuController extends PopupLogSubMenuController {
//    private PredictorStorage predictorStorage;
    protected PredictorTrainerSubMenuController(PopupMenuController popupMenuController,
            MainController mainController, Menupopup popupMenu, LogSummaryType logSummaryType) {
        super(popupMenuController, mainController, popupMenu, logSummaryType);
//        this.predictorStorage = (PredictorStorage) SpringUtil.getBean("predictorStorage");
        constructMenu();
    }

    private void constructMenu() {
        String subMenuImage = "~./icons/predictor-trainer-icon.svg";
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel("Manage predictor");
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_CREATE_NEW_DASHBOARD));
            fetchAndConstructMenuForDb(menuPopup,
                List.of("predict A", "predict B", "predict C"), true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void fetchAndConstructMenuForDb(Menupopup menuPopup, List<String> predictors, boolean separatorRequired) {
        if (!predictors.isEmpty()) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            int index = 1;
            for (String predictor : predictors) {
                if (index <= SUBMENU_SIZE) {
                    addMenuItemForDb(menuPopup, predictor, true);
                    if (index == SUBMENU_SIZE && index < predictors.size()) {
//                        addOptionToViewMoreMenuItemsForDb(menuPopup);
                        break;
                    }
                }
                index++;
            }
        }
    }

    private void addMenuItemForDb(Menupopup popup, String predictor, boolean visibleOnLoad) {
        Menuitem item = new Menuitem();
        item.setLabel(predictor);
        item.addEventListener(ON_CLICK, event -> {
            try {
                System.out.println("====> clicking for: " + predictor);
//                UserMetadataSummaryType umData =
//                    (UserMetadataSummaryType) event.getTarget().getAttribute(USER_META_DATA);
//                Sessions.getCurrent().setAttribute("logSummaries", Collections.singletonList(logSummaryType));
//                Sessions.getCurrent()
//                    .setAttribute("userMetadata_dash", userMetaDataUtilService.getUserMetaDataById(umData.getId()));
//                Clients.evalJavaScript("window.open('" + PortalUrlWrapper.getUrlWithReference("dashboard/index.zul")+"')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        item.setVisible(visibleOnLoad);
        popup.appendChild(item);
    }
}
