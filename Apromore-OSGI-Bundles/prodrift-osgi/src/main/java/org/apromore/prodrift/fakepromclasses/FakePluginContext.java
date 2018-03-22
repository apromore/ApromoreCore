package org.apromore.prodrift.fakepromclasses;

import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionID;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginExecutionResult;
import org.processmining.framework.plugin.ProMFuture;
import org.processmining.framework.plugin.Progress;

import java.util.Iterator;

/**
 * Created by conforti on 29/10/2014.
 */

public class FakePluginContext extends UIPluginContext {

    public FakePluginContext() {
        this(MAIN_PLUGINCONTEXT, "Fake Plugin Context");
    }

    public FakePluginContext(UIPluginContext context, String label) {
        super(context, label);
    }

    public FakePluginContext(PluginContext context) {
        this(MAIN_PLUGINCONTEXT, "Fake Plugin Context");
        for (Iterator<ConnectionID> iterator = context.getConnectionManager().getConnectionIDs().iterator(); iterator.hasNext(); ) {
            ConnectionID cid = iterator.next();
            try {
                org.processmining.framework.connections.Connection connection = context.getConnectionManager().getConnection(cid);
                addConnection(connection);
            } catch (ConnectionCannotBeObtained connectioncannotbeobtained) {
            }
        }

    }

    public Progress getProgress() {
        return new FakeProgress();
    }

    public ProMFuture getFutureResult(int i) {
        return new ProMFuture(String.class, "Fake Future") {

            @Override
            protected Object doInBackground() throws Exception {
                return null;
            }
        };
    }

    public void setFuture(PluginExecutionResult pluginexecutionresult) {
    }

    private static UIPluginContext MAIN_PLUGINCONTEXT;

    static {
        UIContext MAIN_CONTEXT = new UIContext();
        MAIN_PLUGINCONTEXT = MAIN_CONTEXT.getMainPluginContext().createChildContext("");
    }
}
