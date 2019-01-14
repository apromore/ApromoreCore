package org.apromore.plugin.portal.prodrift.Util;

// obtained from www.zkoss.org
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.sys.DesktopCache;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.sys.WebAppCtrl;

public abstract class LongOperation implements Runnable {
    private String desktopId;
    private DesktopCache desktopCache;
    private Thread thread;
    private AtomicBoolean cancelled = new AtomicBoolean(false);

    /**
     * asynchronous callback for your long operation code
     * @throws InterruptedException
     */
    protected abstract void execute() throws InterruptedException;

    /**
     * optional callback method when the task has completed successfully
     */
    protected void onFinish() {};
    /**
     * optional callback method when the task has been cancelled or was interrupted otherwise
     */
    protected void onCancel() {};
    /**
     * optional callback method when the task has completed with an uncaught RuntimeException
     * @param exception
     */
    protected void onException(RuntimeException exception) {
        exception.printStackTrace();
    };
    /**
     * optional callback method when the task has completed (always called)
     */
    protected void onCleanup() {};

    /**
     * set the cancelled flag and try to interrupt the thread
     */
    public final void cancel() {
        cancelled.set(true);
        thread.interrupt();
    }

    /**
     * check the cancelled flag
     * @return
     */
    public final boolean isCancelled() {
        return cancelled.get();
    }

    /**
     * activate the thread (and cached desktop) for UI updates
     * call {@link #deactivate()} once done updating the UI
     * @throws InterruptedException
     */
    protected final void activate() throws InterruptedException {
        Executions.activate(getDesktop());
    }

    /**
     * deactivate the current active (see: {@link #activate()}) thread/desktop after updates are done
     */
    protected final void deactivate() {
        Executions.deactivate(getDesktop());
    }

    /**
     * Checks if the task thread has been interrupted. Use this to check whether or not to exit a busy operation in case.
     * @throws InterruptedException when the current task has been cancelled/interrupted
     */
    protected final void checkCancelled() throws InterruptedException {
        if(Thread.currentThread() != this.thread) {
            throw new IllegalStateException("this method can only be called in the worker thread (i.e. during execute)");
        }
        boolean interrupted = Thread.interrupted();
        if(interrupted || cancelled.get()) {
            cancelled.set(true);
            throw new InterruptedException();
        }
    }

    /**
     * launch the long operation
     */
    public final void start() {
        //not caching the desktop directly to enable garbage collection, in case the desktop destroyed during the long operation
        this.desktopId = Executions.getCurrent().getDesktop().getId();
        this.desktopCache = ((WebAppCtrl) WebApps.getCurrent()).getDesktopCache(Sessions.getCurrent());
        enableServerPushForThisTask();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public final void run() {
        try {
            try {
                checkCancelled(); //avoid unnecessary execution
                execute();
                checkCancelled(); //final cancelled check before calling onFinish
                activate();
                onFinish();
            } catch (InterruptedException e) {
                try {
                    cancelled.set(true);
                    activate();
                    onCancel();
                } catch (InterruptedException e1) {
                    throw new RuntimeException("interrupted onCancel handling", e1);
                } finally {
                    deactivate();
                }
            } catch (RuntimeException rte) {
                try {
                    activate();
                    onException(rte);
                } catch (InterruptedException e1) {
                    throw new RuntimeException("interrupted onException handling", e1);
                } finally {
                    deactivate();
                }
                throw rte;
            } finally {
                deactivate();
            }
        } finally {
            try {
                activate();
                onCleanup();
            } catch (InterruptedException e1) {
                throw new RuntimeException("interrupted onCleanup handling", e1);
            } finally {
                deactivate();
                disableServerPushForThisTask();
            }
        }
    }

    private UUID taskId = UUID.randomUUID();

    private void enableServerPushForThisTask() {
        ((DesktopCtrl)getDesktop()).enableServerPush(true, taskId);
    }

    private void disableServerPushForThisTask() {
        ((DesktopCtrl)getDesktop()).enableServerPush(false, taskId);
    }

    private Desktop getDesktop() {
        return desktopCache.getDesktop(desktopId);
    }
}
