package org.apromore.portal.controller;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.CreateEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Window;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * All the components that are defined here and have a corresponding
 * component with the same 'id' in the zul-file are getting autowired by our
 * 'extends BaseCtrl' class wich extends Window and implements AfterCompose.
 *
 * @author Cameron James
 */
public class BaseController extends Window implements AfterCompose, Serializable {

	private static final long serialVersionUID = -2179229704315045689L;

	protected transient AnnotateDataBinder binder;
	protected transient Map<String, Object> args;

    /**
     * ...
     * @param w
     * @throws Exception
     */
	public void doOnCreateCommon(Window w) throws Exception {
		binder = new AnnotateDataBinder(w);
		binder.loadAll();
	}

    /**
     * ...
     * @param w
     * @throws Exception
     */
	public void doOnCreateCommon(Window w, Event fe) throws Exception {
		doOnCreateCommon(w);
		CreateEvent ce = (CreateEvent) ((ForwardEvent) fe).getOrigin();
		args = (Map<String, Object>) ce.getArg();
	}

    /**
     * Enabled Autowiring of the components on the page.
     */
    @Override
    public void afterCompose() {
		processRecursive(this, this);
		Components.wireVariables(this, this); // auto wire variables
		Components.addForwards(this, this); // auto forward
	}

	/*
	 * Are there inner window components that need wiring as well.
	 */
	private void processRecursive(Window main, Window child) {
		Components.wireVariables(main, child);
		Components.addForwards(main, this);
		List<Component> winList = (List<Component>) child.getChildren();
		for (Component window : winList) {
			if (window instanceof Window) {
				processRecursive(main, (Window) window);
			}
		}
	}
}
