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

import java.io.IOException;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.Assertions;

import org.apromore.portal.menu.MenuConfigLoader;

/**
 * Load menu config
 */
class MenuConfigLoaderTest {

    private static Stream<Arguments> TEST_CASES() {

        return Stream.of(
                Arguments.of("menu-config/nonexistent-menu.json", IOException.class),
                Arguments.of("menu-config/invalid-menu.json", JsonMappingException.class),
                Arguments.of("menu-config/default-menu.json", null)
        );
    }

    @ParameterizedTest
    @MethodSource("TEST_CASES")
    void loadMenuConfig_ShouldThrowProperException(String menuConfigPath, Class<Exception> e) {
        Resource resource = new ClassPathResource(menuConfigPath, this.getClass().getClassLoader());
        MenuConfigLoader menuConfig = new MenuConfigLoader(resource);
        if (e == null) {
            Assertions.assertDoesNotThrow(() -> {
                menuConfig.load();
            });
        } else {
            Assertions.assertThrows(e, () -> {
                menuConfig.load();
            });
        }
    }

}


