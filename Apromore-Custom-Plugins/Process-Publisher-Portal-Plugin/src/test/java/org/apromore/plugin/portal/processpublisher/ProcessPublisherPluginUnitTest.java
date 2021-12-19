/**
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.processpublisher;

import org.apromore.commons.config.ConfigBean;
import org.apromore.plugin.portal.PortalPlugin;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProcessPublisherPluginUnitTest {

    @InjectMocks
    private ProcessPublisherPlugin processPublisherPlugin = new ProcessPublisherPlugin();

    @Mock
    private ConfigBean configBean;

    @Test
    public void testAvailabilityNotEnabled() {
        when(configBean.isEnableModelPublish()).thenReturn(false);
        assertEquals(PortalPlugin.Availability.UNAVAILABLE, processPublisherPlugin.getAvailability());
    }

    @Test
    public void testAvailabilityEnabled() {
        when(configBean.isEnableModelPublish()).thenReturn(true);
        assertEquals(PortalPlugin.Availability.AVAILABLE, processPublisherPlugin.getAvailability());
    }

}
