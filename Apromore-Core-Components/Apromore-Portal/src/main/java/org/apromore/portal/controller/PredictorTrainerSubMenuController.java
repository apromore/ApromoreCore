package org.apromore.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apromore.dao.PredictorRepository;
import org.apromore.dao.model.PredictorDao;
import org.apromore.plugin.portal.PortalPlugin;
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
    private PredictorRepository predictorRepository;
    public PredictorTrainerSubMenuController(PopupMenuController popupMenuController,
                                         MainController mainController, Menupopup popupMenu,
                                         LogSummaryType logSummaryType) {
        super(popupMenuController, mainController, popupMenu, logSummaryType);
        this.predictorRepository = (PredictorRepository) SpringUtil.getBean("predictorRepository", PredictorRepository.class);

        System.out.println("===> predictorRepository All: " + predictorRepository.findByLogId(logSummaryType.getId()));

        constructMenu();
    }

    private void constructMenu() {
        String subMenuImage = "~./icons/predictor-trainer-icon.svg";
        if (subMenuImage != null) {
            Menu subMenu = new Menu();
            subMenu.setLabel("Manage predictor");
            subMenu.setImage(subMenuImage);
            Menupopup menuPopup = new Menupopup();
            popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_PREDICTOR_TRAINER));
            fetchAndConstructMenuForDb(menuPopup, predictorRepository.findByLogId(logSummaryType.getId()), true);
            subMenu.appendChild(menuPopup);
            popupMenu.appendChild(subMenu);
        }
    }

    private void fetchAndConstructMenuForDb(Menupopup menuPopup, List<PredictorDao> predictors, boolean separatorRequired) {
        if (!predictors.isEmpty()) {
            if (separatorRequired) {
                popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.ITEM_SEPARATOR));
            }
            int index = 1;
            for (PredictorDao predictor : predictors) {
                if (index++ <= SUBMENU_SIZE) {
                    addMenuItemForDb(menuPopup, predictor, true);
                    if (index == SUBMENU_SIZE && index < predictors.size()) {
                        addOptionToViewMoreMenuItemsForDb(menuPopup);
                        break;
                    }
                }
            }
        }
    }

    private void addMenuItemForDb(Menupopup popup, PredictorDao predictor, boolean visibleOnLoad) {
        Menuitem item = new Menuitem();
        item.setLabel(predictor.getName());
        item.addEventListener(ON_CLICK, event -> {
            try {
                System.out.println("====> clicking for: " + predictor);
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("predictorId", predictor.getId());
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_PREDICTOR_TRAINER);
                plugin.setSimpleParams(attrMap);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        item.setVisible(visibleOnLoad);
        popup.appendChild(item);
    }

    private void addOptionToViewMoreMenuItemsForDb(Menupopup menuPopup) {
        Menuitem item = new Menuitem();
        item.setLabel("...");
        item.setStyle(CENTRE_ALIGN);
        item.addEventListener(ON_CLICK, event -> {
            try {
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_PREDICTOR_MANAGER);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        menuPopup.appendChild(item);
    }
}
