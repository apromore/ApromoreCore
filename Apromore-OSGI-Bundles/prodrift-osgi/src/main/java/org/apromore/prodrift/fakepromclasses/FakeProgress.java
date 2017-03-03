package org.apromore.prodrift.fakepromclasses;

import org.processmining.framework.plugin.Progress;

/**
 * Created by conforti on 29/10/2014.
 */

public class FakeProgress implements Progress {

    public FakeProgress() {
        cancelled = false;
    }

    public void cancel() {
        cancelled = true;
    }

    public String getCaption() {
        return "";
    }

    public int getMaximum() {
        return 0;
    }

    public int getMinimum() {
        return 0;
    }

    public int getValue() {
        return 0;
    }

    public void inc() {
    }

    public boolean isCanceled() {
        return cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isIndeterminate() {
        return true;
    }

    public void setCaption(String s) {
    }

    public void setIndeterminate(boolean flag) {
    }

    public void setMaximum(int i) {
    }

    public void setMinimum(int i) {
    }

    public void setValue(int i) {
    }

    private boolean cancelled;
}