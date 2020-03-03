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

/* ThemeWebAppInit.java

	Purpose:
		
	Description:
		
	History:
		Jun 29, 2010 11:56:16 AM , Created by Sam

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.theme.${theme.name};

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.util.WebAppInit;
//import org.zkoss.zkmax.theme.ResponsiveThemeRegistry;
import org.zkoss.zul.theme.Themes;

/**
 * Initial the theme relative setting, includes
 * Library property setting, Theme provider setting and Component definition setting 
 * 
 */
public class ThemeWebAppInit implements WebAppInit {

	private final static String THEME_NAME = "${theme.name}";
	private final static String THEME_DISPLAY = "${theme.display}";
	private final static int THEME_PRIORITY = 700;
	
	public void init(WebApp webapp) throws Exception {
		Themes.register(THEME_NAME, THEME_DISPLAY, THEME_PRIORITY);
		// Bug ZK-2963: register sapphire theme for tablet responsive theme
		//String edition = WebApps.getEdition();
		//if ("EE".equals(edition)) {
		//	Themes.register(ResponsiveThemeRegistry.TABLET_PREFIX + THEME_NAME, THEME_DISPLAY, THEME_PRIORITY);
		//}
	}
}
