/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.menu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;
import lombok.Getter;

@Component("menuConfigLoader")
public class MenuConfigLoader {

	private static final String DEFAULT_MENU_CONFIG = "default-menus.json";

	private Resource menuConfigUrl;

	@Getter
	private MenuConfigs menuConfigs = null;

	@Getter
	private Map<String, MenuConfig> menuConfigMap;

	public MenuConfigLoader(@Value("${portal.menu.config.url}") Resource menuConfigUrl) {
		this.menuConfigUrl = menuConfigUrl;
	}

	public MenuConfig getMenuConfig(String id) {
		return menuConfigMap.get(id);
	}

	public void load() throws IOException {
		if (menuConfigs != null) {
			return;
		}
		menuConfigMap  = new HashMap<>();
		InputStream is;

		if (menuConfigUrl == null) {
			is = getClass().getClassLoader().getResourceAsStream(DEFAULT_MENU_CONFIG);
		} else {
			is = menuConfigUrl.getInputStream();
		}
		String jsonString = new String(is.readAllBytes(), StandardCharsets.UTF_8);

		ObjectMapper mapper = new ObjectMapper();
		menuConfigs = mapper.readValue(jsonString, MenuConfigs.class);
		menuConfigMap = menuConfigs
				.getConfigs()
				.stream()
				.collect(Collectors.toMap(MenuConfig::getId, Function.identity()));
	}
}

