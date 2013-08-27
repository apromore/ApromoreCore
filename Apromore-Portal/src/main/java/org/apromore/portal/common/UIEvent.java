package org.apromore.portal.common;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

/**
 * Home grown event for UI updates.
 *
 * @author Cameron James
 */
public class UIEvent extends Event {

    public UIEvent(String name) {
        super(name);
    }

    public UIEvent(String name, Component target) {
        super(name, target);
    }

    public UIEvent(String name, Component target, Object data) {
        super(name, target, data);
    }
}
