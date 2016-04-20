package org.apromore.portal.custom.gui.tab;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 24/03/2016.
 */
public abstract class AbstractPortalTab extends Tab implements PortalTab {

    protected boolean isNew;
    protected Tabpanel tabpanel;
    protected String userID;
    protected PortalContext portalContext;

    public AbstractPortalTab(String tabName, String userID, PortalContext portalContext) {
        this(tabName, portalContext);
        this.userID = userID;
    }

    public AbstractPortalTab(String tabName, PortalContext portalContext) {
        super(tabName);
        this.userID = portalContext.getCurrentUser().getId();
        this.isNew = true;
        this.portalContext = portalContext;

        setClosable(true);
        setSelected(true);
        setTooltiptext("Double click to show more info");
        setImage("img/info25.png");

        addEventListener(Events.ON_CLOSE,new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                AbstractPortalTab.this.remove();
            }
        });
    }

    @Override
    public Tabpanel getTabpanel(){
        return tabpanel;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public void remove(){
        SessionTab.getSessionTab(portalContext).removeTabFromSession(userID, this);
    }

}
