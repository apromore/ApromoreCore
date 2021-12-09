package org.apromore.portal.dialogController;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.model.UserType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

public class KeepAliveController extends SelectorComposer<Component> {

    @Wire
    private Div divKeepAlive;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        divKeepAlive.addEventListener("onKeepAlive", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                UserType user = (UserType) Sessions.getCurrent().getAttributes().get("USER");
                PortalLoggerFactory.getLogger(this.getClass()).debug("Keep BPMN Editor alive for user: " + user.getUsername());
            }
        });
    }


}
