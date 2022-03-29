/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2013 Felix Mannhardt.
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.provider.impl;

import org.apromore.plugin.Plugin;


public class TestPlugin implements Plugin {

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getType() {
        return "org.apromore.plugin.provider.services.test";
    }

    @Override
    public String getDescription() {
        return "test";
    }

    @Override
    public String getAuthor() {
        return "test";
    }

    @Override
    public String getEMail() {
        return "test@test.com";
    }

}
