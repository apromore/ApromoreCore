/*
 * Copyright (c) 2001-2006, Gaudenz Alder
 * Copyright (c) 2005-2006, David Benson
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.navigation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.event.GraphLayoutCacheEvent;
import org.apromore.jgraph.event.GraphLayoutCacheListener;
import org.apromore.jgraph.event.GraphModelEvent;
import org.apromore.jgraph.event.GraphModelListener;
import org.apromore.jgraph.graph.GraphLayoutCache;

public class GraphNavigator extends JPanel implements GraphLayoutCacheListener,
		GraphModelListener, PropertyChangeListener, AdjustmentListener {

	/**
	 * Shared cursor objects to avoid expensive constructor calls.
	 */
	protected static final Cursor CURSOR_DEFAULT = new Cursor(
			Cursor.DEFAULT_CURSOR),
			CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);

	/**
	 * Component listener to udpate the scale.
	 */
	protected ComponentListener componentListener = new ComponentAdapter() {

		/**
		 * (non-Javadoc)
		 */
		public void componentResized(ComponentEvent e) {
			updateScale();
		}

	};

	/**
	 * References the inital layout cache of the backing graph.
	 */
	protected transient GraphLayoutCache initialLayoutCache;

	/**
	 * Holds the backing graph and references the displayed (current) graph.
	 */
	protected JGraph backingGraph;

	/**
	 * Weak reference to the current graph.
	 */
	protected WeakReference currentGraph;

	/**
	 * Holds the navigator pane the displays the backing graph.
	 */
	protected NavigatorPane navigatorPane;

	/**
	 * Specifies the maximum scale for the navigator view. Default is 0.5
	 */
	protected double maximumScale = 0.5;

	/**
	 * Constructs a new graph navigator using <code>backingGraph</code> to
	 * display the graph in {@link #currentGraph}.
	 * 
	 * @param backingGraph
	 *            The backing graph to render the display.
	 */
	public GraphNavigator(JGraph backingGraph) {
		super(new BorderLayout());
		setDoubleBuffered(true);
		setBackingGraph(backingGraph);
		initialLayoutCache = backingGraph.getGraphLayoutCache();
		backingGraph.setOpaque(false);
		backingGraph.setScale(maximumScale);
		navigatorPane = new NavigatorPane(backingGraph);
		backingGraph.addMouseListener(navigatorPane);
		backingGraph.addMouseMotionListener(navigatorPane);

		// Configures the navigator
		setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		add(navigatorPane, BorderLayout.CENTER);
		setFocusable(false);

		// Updates the size when the component is resized
		addComponentListener(componentListener);
	}

	/**
	 * Returns the navigator pane that contains the backing graph.
	 * 
	 * @return Returns the navigator pane.
	 */
	public NavigatorPane getScrollPane() {
		return navigatorPane;
	}

	/**
	 * Returns the maximum scale to be used for the backing graph.
	 * 
	 * @return Returns the maximumScale.
	 */
	public double getMaximumScale() {
		return maximumScale;
	}

	/**
	 * Sets the maximum scale to be used for the backing graph.
	 * 
	 * @param maximumScale
	 *            The maximumScale to set.
	 */
	public void setMaximumScale(double maximumScale) {
		this.maximumScale = maximumScale;
	}

	/**
	 * Returns the backing graph that is used to display {@link #currentGraph}.
	 * 
	 * @return Returns the backing graph.
	 */
	public JGraph getBackingGraph() {
		return backingGraph;
	}

	/**
	 * Sets the backing graph that is used to display {@link #currentGraph}.
	 * 
	 * @param backingGraph
	 *            The backing graph to set.
	 */
	public void setBackingGraph(JGraph backingGraph) {
		this.backingGraph = backingGraph;
	}

	/**
	 * Returns the graph that is currently displayed.
	 * 
	 * @return Returns the backing graph.
	 */
	public JGraph getCurrentGraph() {
		return (JGraph) ((currentGraph != null) ? currentGraph.get() : null);
	}

	/**
	 * Sets the graph that is currently displayed.
	 * 
	 * @param sourceGraph
	 *            The current graph to set.
	 */
	public void setCurrentGraph(JGraph sourceGraph) {
		if (sourceGraph == null || getParentGraph(sourceGraph) == null) {
			if (sourceGraph != null) {
				JGraph oldValue = getCurrentGraph();

				// Removes listeners from the previous graph
				if (oldValue != null && sourceGraph != oldValue) {
					oldValue.getModel().removeGraphModelListener(this);
					oldValue.getGraphLayoutCache()
							.removeGraphLayoutCacheListener(this);
					oldValue.removePropertyChangeListener(this);
					JScrollPane scrollPane = getParentScrollPane(oldValue);
					if (scrollPane != null) {
						scrollPane.removeComponentListener(componentListener);
						scrollPane.getVerticalScrollBar()
								.removeAdjustmentListener(this);
						scrollPane.getHorizontalScrollBar()
								.removeAdjustmentListener(this);
						scrollPane.removePropertyChangeListener(this);
					}

					// Restores the layout cache of the backing graph
					backingGraph.setGraphLayoutCache(initialLayoutCache);
				}
				this.currentGraph = new WeakReference(sourceGraph);

				// Installs change listeners to update the size
				if (sourceGraph != null) {
					sourceGraph.getModel().addGraphModelListener(this);
					sourceGraph.getGraphLayoutCache()
							.addGraphLayoutCacheListener(this);
					sourceGraph.addPropertyChangeListener(this);
					JScrollPane currentScrollPane = getParentScrollPane(sourceGraph);
					if (currentScrollPane != null) {
						currentScrollPane
								.addComponentListener(componentListener);
						currentScrollPane.getVerticalScrollBar()
								.addAdjustmentListener(this);
						currentScrollPane.getHorizontalScrollBar()
								.addAdjustmentListener(this);
						currentScrollPane.addPropertyChangeListener(this);
					}
					backingGraph.setGraphLayoutCache(sourceGraph
							.getGraphLayoutCache());
				}
				updateScale();
			}
		}
	}

	/**
	 * Updates the scale of the backing graph.
	 */
	protected void updateScale() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JGraph graph = getCurrentGraph();
				if (graph != null) {
					Dimension d = graph.getPreferredSize();
					Dimension b = graph.getBounds().getSize();
					d.width = Math.max(d.width, b.width);
					b.height = Math.max(d.height, b.height);
					double scale = graph.getScale();
					d.setSize(d.width * 1 / scale, d.height * 1 / scale);
					Dimension s = getScrollPane().getViewport().getSize();
					double sx = s.getWidth() / d.getWidth();
					double sy = s.getHeight() / d.getHeight();
					scale = Math.min(Math.min(sx, sy), getMaximumScale());
					getBackingGraph().setScale(scale);
					repaint();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 */
	public void graphLayoutCacheChanged(GraphLayoutCacheEvent e) {
		updateScale();
	}

	/*
	 * (non-Javadoc)
	 */
	public void graphChanged(GraphModelEvent e) {
		updateScale();
	}

	/*
	 * (non-Javadoc)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		updateScale();
	}

	/*
	 * (non-Javadoc)
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		navigatorPane.repaint();
	}

	/**
	 * Helper method that returns the parent scrollpane for the specified
	 * component in the component hierarchy. If the component is itself a
	 * scrollpane then it is returned.
	 * 
	 * @return Returns the parent scrollpane or component.
	 */
	public static JScrollPane getParentScrollPane(Component component) {
		while (component != null) {
			if (component instanceof JScrollPane)
				return (JScrollPane) component;
			component = component.getParent();
		}
		return null;
	}

	/**
	 * Helper method that returns the parent JGraph for the specified component
	 * in the component hierarchy. The component itself is never returned.
	 * 
	 * @return Returns the parent scrollpane or component.
	 */
	public static JGraph getParentGraph(Component component) {
		do {
			component = component.getParent();
			if (component instanceof JGraph)
				return (JGraph) component;
		} while (component != null);
		return null;
	}

	/**
	 * Scrollpane that implements special painting used for the navigator
	 * preview.
	 */
	public class NavigatorPane extends JScrollPane implements MouseListener,
			MouseMotionListener {

		/**
		 * Holds the bounds of the finder (red box).
		 */
		protected Rectangle currentViewport = new Rectangle();

		/**
		 * Holds the location of the last mouse event.
		 */
		protected Point lastPoint = null;

		/**
		 * Constructs a new navigator pane using the specified backing graph to
		 * display the preview.
		 * 
		 * @param backingGraph
		 *            The backing graph to use for rendering.
		 */
		public NavigatorPane(JGraph backingGraph) {
			super(backingGraph);
			setOpaque(false);
			getViewport().setOpaque(false);
		}

		/**
		 * Paints the navigator pane on the specified graphics.
		 * 
		 * @param g
		 *            The graphics to paint the navigator to.
		 */
		public void paint(Graphics g) {
			JGraph graph = getCurrentGraph();
			JScrollPane scrollPane = getParentScrollPane(graph);
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, getWidth(), getHeight());
			if (scrollPane != null && graph != null) {
				JViewport viewport = scrollPane.getViewport();
				Rectangle rect = viewport.getViewRect();
				double scale = backingGraph.getScale() / graph.getScale();

				Dimension pSize = graph.getSize();
				g.setColor(getBackground());
				g.fillRect(0, 0, (int) (pSize.width * scale),
						(int) (pSize.height * scale));
				g.setColor(Color.WHITE);
				currentViewport.setFrame((int) (rect.getX() * scale),
						(int) (rect.getY() * scale),
						(int) (rect.getWidth() * scale), (int) (rect
								.getHeight() * scale));
				g.fillRect(currentViewport.x, currentViewport.y,
						currentViewport.width, currentViewport.height);

				// No background image required, draw background
				super.paint(g);
				g.setColor(Color.RED);
				g.drawRect(currentViewport.x, currentViewport.y,
						currentViewport.width, currentViewport.height);
			}
		}

		/*
		 * (non-Javadoc)
		 */
		public void mouseClicked(MouseEvent e) {
			// empty
		}

		/*
		 * (non-Javadoc)
		 */
		public void mousePressed(MouseEvent e) {
			if (currentViewport.contains(e.getX(), e.getY()))
				lastPoint = e.getPoint();
		}

		/*
		 * (non-Javadoc)
		 */
		public void mouseReleased(MouseEvent e) {
			lastPoint = null;
		}

		/*
		 * (non-Javadoc)
		 */
		public void mouseEntered(MouseEvent e) {
			// empty

		}

		/*
		 * (non-Javadoc)
		 */
		public void mouseExited(MouseEvent e) {
			// empty

		}

		/*
		 * (non-Javadoc)
		 */
		public void mouseDragged(MouseEvent e) {
			if (lastPoint != null) {
				JGraph graph = getCurrentGraph();
				JScrollPane scrollPane = getParentScrollPane(graph);
				if (scrollPane != null && currentGraph != null) {
					JViewport viewport = scrollPane.getViewport();
					Rectangle rect = viewport.getViewRect();
					double scale = backingGraph.getScale() / graph.getScale();
					double x = (e.getX() - lastPoint.getX()) / scale;
					double y = (e.getY() - lastPoint.getY()) / scale;
					lastPoint = e.getPoint();
					x = rect.getX() + ((x > 0) ? rect.getWidth() : 0) + x;
					y = rect.getY() + ((y > 0) ? rect.getHeight() : 0) + y;
					Point2D pt = new Point2D.Double(x, y);
					graph.scrollPointToVisible(pt);
					navigatorPane.repaint();
				}
			}
		}

		/*
		 * (non-Javadoc)
		 */
		public void mouseMoved(MouseEvent e) {
			if (currentViewport.contains(e.getPoint()))
				setCursor(CURSOR_HAND);
			else
				setCursor(CURSOR_DEFAULT);
		}

	}

	/*
	 * (non-Javadoc)
	 */
	public static GraphNavigator createInstance(JGraph graph) {
		graph.setEnabled(false);
		graph.setFocusable(false);
		graph.setAntiAliased(false);
		return new GraphNavigator(graph);
	}

}
