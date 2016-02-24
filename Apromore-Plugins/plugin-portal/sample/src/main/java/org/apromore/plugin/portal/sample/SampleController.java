package org.apromore.plugin.portal.sample;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Textbox;

/**
 * Sample Controller
 */
public class SampleController extends GenericForwardComposer {

    private Textbox sampleText;

    public void onClick$ok(Event evt) throws InterruptedException {
        sampleText.setText("Hello World!");
    }

}
