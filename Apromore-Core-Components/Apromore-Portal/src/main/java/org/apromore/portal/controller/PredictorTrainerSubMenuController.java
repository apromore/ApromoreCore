/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
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

package org.apromore.portal.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apromore.dao.PredictorRepository;
import org.apromore.dao.model.PredictorDao;
import org.apromore.plugin.portal.PortalPlugin;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.PopupMenuController;
import org.apromore.portal.menu.MenuItem;
import org.apromore.portal.menu.PluginCatalog;
import org.apromore.portal.model.LogSummaryType;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

@Slf4j
public class PredictorTrainerSubMenuController extends PopupLogSubMenuController {
    private PredictorRepository predictorRepository;
    public PredictorTrainerSubMenuController(PopupMenuController popupMenuController,
                                         MainController mainController, Menupopup popupMenu,
                                         LogSummaryType logSummaryType) {
        super(popupMenuController, mainController, popupMenu, logSummaryType);
        this.predictorRepository = (PredictorRepository) SpringUtil.getBean("predictorRepository", PredictorRepository.class);
        constructMenu();
    }

    private void constructMenu() {
        String subMenuImage = "~./icons/predictor-manage.svg";
        Menu subMenu = new Menu();
        subMenu.setLabel("Manage predictor");
        subMenu.setImage(subMenuImage);
        Menupopup menuPopup = new Menupopup();
        popupMenuController.addMenuitem(menuPopup, new MenuItem(PluginCatalog.PLUGIN_PREDICTOR_TRAINER));
        fetchAndConstructMenuForDb(menuPopup, predictorRepository.findByLogId(logSummaryType.getId()), true);
        subMenu.appendChild(menuPopup);
        popupMenu.appendChild(subMenu);
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
                Map<String, Object> attrMap = new HashMap<>();
                attrMap.put("predictorId", predictor.getId());
                PortalPlugin plugin = portalPluginMap.get(PluginCatalog.PLUGIN_PREDICTOR_TRAINER);
                plugin.setSimpleParams(attrMap);
                plugin.execute(getPortalContext());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
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
                log.error(e.getMessage(), e);
            }
        });
        menuPopup.appendChild(item);
    }
}
