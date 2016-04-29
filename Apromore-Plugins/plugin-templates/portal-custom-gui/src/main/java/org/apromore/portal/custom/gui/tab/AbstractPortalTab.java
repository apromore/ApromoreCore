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

    protected boolean isNew = true;
//    protected Tab tab;
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
        this.portalContext = portalContext;

//        this.tab = new Tab(tabName);
//        this.tab.setClosable(true);
//        this.tab.setSelected(true);
//        this.tab.setTooltiptext("Double click to show more info");
//        this.tab.setImage("img/info25.png");
//
//        this.tab.addEventListener(Events.ON_CLOSE,new EventListener<Event>() {
//            @Override
//            public void onEvent(Event event) throws Exception {
//                AbstractPortalTab.this.remove();
//            }
//        });

        this.setClosable(true);
        this.setSelected(true);
//        this.setTooltiptext("Double click to show more info");
//        this.setImage("img/info25.png");

        this.addEventListener(Events.ON_CLOSE,new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                AbstractPortalTab.this.remove();
            }
        });
    }

    public abstract AbstractPortalTab clone();

    @Override
    public Tabpanel getTabpanel(){
        return tabpanel;
    }

    @Override
    public Tab getTab(){
        return this;
    }

    @Override
    public void remove(){
        SessionTab.getSessionTab(portalContext).removeTabFromSessionNoRefresh(userID, this);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
