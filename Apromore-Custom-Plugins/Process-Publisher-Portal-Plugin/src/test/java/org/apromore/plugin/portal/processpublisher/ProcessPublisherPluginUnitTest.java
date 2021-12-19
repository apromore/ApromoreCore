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
