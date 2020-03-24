/* 
 * $Id: JGraphLayoutProgress.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Describes the state of a long-running layout. The UI can listen to property
 * changes to inform the user of the layout progress, and it can set the
 * isStopped property to signal the layout to terminate.
 */
public class JGraphLayoutProgress {

	/**
	 * Bound property name for <code>maximum</code>.
	 */
	public final static String MAXIMUM_PROPERTY = "maximum";

	/**
	 * Bound property name for <code>progress</code>.
	 */
	public final static String PROGRESS_PROPERTY = "progress";

	/**
	 * Bound property name for <code>isStopped</code>.
	 */
	public final static String ISSTOPPED_PROPERTY = "isStopped";

	/**
	 * Property change support is delegated to this class.
	 */
	protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	/**
	 * Specifies the maximum progress, for example 100%.
	 */
	protected int maximum = 0;

	/**
	 * Specifies the current progress.
	 */
	protected int progress = 0;

	/**
	 * Specifies whether the layout was stopped in the user interface.
	 */
	protected boolean isStopped = false;

	/**
	 * Constructs a new layout progress with a maximum progress of 0.
	 */
	public JGraphLayoutProgress() {
		this(0);
	}

	/**
	 * Constructs a new layout progress for the specified maximum progress.
	 * 
	 * @param maximum
	 */
	public JGraphLayoutProgress(int maximum) {
		reset(maximum);
	}

	/**
	 * Resets the progress to 0 and sets isStopped to <code>false</code>.
	 */
	public void reset(int maximum) {
		setStopped(false);
		this.maximum=0; // forces property change
		setMaximum(maximum);
		this.progress=0;
		setProgress(0); // initialize progress
	}

	/**
	 * @return Returns the changeSupport.
	 */
	public PropertyChangeSupport getChangeSupport() {
		return changeSupport;
	}

	/**
	 * @param changeSupport
	 *            The changeSupport to set.
	 */
	public void setChangeSupport(PropertyChangeSupport changeSupport) {
		this.changeSupport = changeSupport;
	}

	/**
	 * Stoppable layouts should check this within their inner-most loops and
	 * return immediately if this returns true.
	 * 
	 * @return Returns true if the layout should terminate.
	 */
	public boolean isStopped() {
		return isStopped;
	}

	/**
	 * Signals the layout to stop running.
	 * <p>
	 * Fires a property change for the ISSTOPPED_PROPERTY.
	 * 
	 * @param isStopped
	 *            Whether the layout should stop.
	 */
	public void setStopped(boolean isStopped) {
		boolean oldValue = this.isStopped;
		this.isStopped = isStopped;
		changeSupport.firePropertyChange(ISSTOPPED_PROPERTY, oldValue,
				isStopped);
	}

	/**
	 * @return Returns the maximum progress.
	 */
	public int getMaximum() {
		return maximum;
	}

	/**
	 * Sets the maximum progress of the layout. This should be set at
	 * construction time only.
	 * <p>
	 * Fires a property change for the MAXIMUM_PROPERTY.
	 * 
	 * @param maximum
	 *            The maximum to set.
	 */
	public void setMaximum(int maximum) {
		int oldValue = this.maximum;
		this.maximum = maximum;
		changeSupport.firePropertyChange(MAXIMUM_PROPERTY, oldValue, maximum);
	}

	/**
	 * @return Returns the progress.
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * Sets the current progress of the layout.
	 * <p>
	 * Fires a property change for the PROGRESS_PROPERTY.
	 * 
	 * @param progress
	 *            The progress to set.
	 */
	public void setProgress(int progress) {
		int oldValue = this.progress;
		this.progress = progress;
		changeSupport.firePropertyChange(PROGRESS_PROPERTY, oldValue, progress);
	}

	/**
	 * Adds a property change listener.
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a property change listener.
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

}
