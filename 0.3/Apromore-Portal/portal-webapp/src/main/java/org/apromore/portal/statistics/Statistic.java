package org.apromore.portal.statistics;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.util.Monitor;

/**
 * An implementation of {@link org.zkoss.zk.ui.util.Monitor} to accumulate statistic data in memory.
 * <p/>
 * <p/>
 * It has no effect until you specify it in WEB-INF/zk.xml.
 *
 * @author tomyeh
 * @changes 16.07.2009 / sge(at)forsthaus(dot)de <br>
 * addapted for static access to the values. <br>
 */
public class Statistic implements Monitor, Serializable {

    private static final long serialVersionUID = 1L;

    private transient final long _startTime;
    private transient int _nsess, _actsess, _ndt, _actdt, _nupd, _actupd;

    // new
    private static Statistic stat;

    public Statistic() {
        _startTime = System.currentTimeMillis();
        stat = this;
    }

    public static Statistic getStatistic() {
        return stat;
    }

    public double getRuningHours() {
        long v = System.currentTimeMillis() - getStartTime();
        return ((double) v) / 3600000;
    }

    /**
     * Returns when the server (actually, this monitor) started.
     */
    public long getStartTime() {
        return _startTime;
    }

    /**
     * Returns the total number of sessions that have been created since the
     * server started.
     */
    public int getTotalSessionCount() {
        return _nsess;
    }

    /**
     * Returns the number of active sessions.
     */
    public int getActiveSessionCount() {
        return _actsess;
    }

    /**
     * Returns the average number of sessions being created in an hour.
     */
    public double getAverageSessionCount() {
        return _nsess / getEscapedHours();
    }

    /**
     * Returns the total number of desktops that have been created since the
     * server started.
     */
    public int getTotalDesktopCount() {
        return _ndt;
    }

    /**
     * Returns the number of active desktops.
     */
    public int getActiveDesktopCount() {
        return _actdt;
    }

    /**
     * Returns the average number of desktops being created in an hour.
     */
    public double getAverageDesktopCount() {
        return _ndt / getEscapedHours();
    }

    /**
     * Returns the total number of asynchronous updates that have been received
     * since the server started.
     */
    public int getTotalUpdateCount() {
        return _nupd;
    }

    /**
     * Returns the number of active asynchronous updates.
     */
    public int getActiveUpdateCount() {
        return _actupd;
    }

    /**
     * Returns the average number of asynchronous updates being created in an
     * hour.
     */
    public double getAverageUpdateCount() {
        return _nupd / getEscapedHours();
    }

    /**
     * Returns how many hours escaped since the server started.
     */
    private double getEscapedHours() {
        long v = System.currentTimeMillis() - _startTime;
        return ((double) v) / 3600000;
    }

    // -- Monitor --//
    synchronized public void sessionCreated(Session sess) {
        ++_nsess;
        ++_actsess;
    }

    synchronized public void sessionDestroyed(Session sess) {
        --_actsess;
    }

    synchronized public void desktopCreated(Desktop desktop) {
        ++_ndt;
        ++_actdt;
    }

    synchronized public void desktopDestroyed(Desktop desktop) {
        --_actdt;
    }

    synchronized public void beforeUpdate(Desktop desktop, List requests) {
        ++_nupd;
        ++_actupd;
    }

    synchronized public void afterUpdate(Desktop desktop) {
        --_actupd;
    }
}
