/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */

package org.apromore.jgraph.layout.hierarchical;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.hierarchical.model.JGraphAbstractHierarchyCell;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyEdge;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyModel;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyNode;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyRank;

/**
 * Sets the horizontal locations of node and edge dummy nodes on each layer.
 * Uses median down and up weighings as well heuristic to straighten edges as
 * far as possible.
 */
public class JGraphCoordinateAssignment implements JGraphHierarchicalLayoutStep {

	/**
	 * The minimum buffer between cells on the same rank
	 */
	protected double intraCellSpacing = 30.0;

	/**
	 * The minimum distance between cells on adjacent ranks
	 */
	protected double interRankCellSpacing = 30.0;

	/**
	 * The distance between each parallel edge on each ranks for long edges
	 */
	protected double parallelEdgeSpacing = 10.0;

	/**
	 * The number of heuristic iterations to run
	 */
	protected int maxIterations = 8;

	/**
	 * The position of the root ( start ) node(s) relative to the rest of the
	 * laid out graph
	 */
	protected int orientation = SwingConstants.NORTH;

	/**
	 * The minimum x position node placement starts at
	 */
	protected double initialX;

	/**
	 * The maximum x value this positioning lays up to
	 */
	protected double limitX;

	/**
	 * The sum of x-displacements for the current iteration
	 */
	protected double currentXDelta;

	/**
	 * The rank that has the widest x position
	 */
	protected int widestRank;

	/**
	 * The X-coordinate of the edge of the widest rank
	 */
	protected double widestRankValue;

	/**
	 * The width of all the ranks
	 */
	protected double[] rankWidths;

	/*
	 * The y co-ordinate of all the ranks
	 */
	protected double[] rankY;

	/**
	 * Whether or not to perform local optimisations and iterate multiple times
	 * through the algorithm
	 */
	protected boolean fineTuning = true;

	/**
	 * Whether or not to pull together sections of layout into empty space
	 */
	protected boolean compactLayout = false;

	/**
	 * A store of connections to the layer above for speed
	 */
	protected JGraphAbstractHierarchyCell[][] nextLayerConnectedCache;

	/**
	 * A store of connections to the layer below for speed
	 */
	protected JGraphAbstractHierarchyCell[][] previousLayerConnectedCache;

	/** The logger for this class */
	private static Logger logger = Logger
			.getLogger("com.jgraph.layout.hierarchical.JGraphCoordinateAssignment");

	/**
	 * Creates a JGraphCoordinateAssignment
	 * 
	 * @param intraCellSpacing
	 *            the minimum buffer between cells on the same rank
	 * @param interRankCellSpacing
	 *            the minimum distance between cells on adjacent ranks
	 * @param orientation
	 *            the position of the root node(s) relative to the graph
	 * @param initialX
	 *            the leftmost coordinate node placement starts at
	 */
	public JGraphCoordinateAssignment(double intraCellSpacing,
			double interRankCellSpacing, int orientation,
			boolean compactLayout, double initialX, double parallelEdgeSpacing) {
		this.intraCellSpacing = intraCellSpacing;
		this.interRankCellSpacing = interRankCellSpacing;
		this.orientation = orientation;
		this.compactLayout = compactLayout;
		this.initialX = initialX;
		this.parallelEdgeSpacing = parallelEdgeSpacing;
		setLoggerLevel(Level.OFF);
	}

	/**
	 * A basic horizontal coordinate assignment algorithm
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 * @return the updated hierarchy model
	 */
	public JGraphHierarchyModel run(JGraphFacade facade,
			JGraphHierarchyModel model) {
		currentXDelta = 0.0;
		initialise(model);
		initialCoords(facade, model);
		if (fineTuning) {
			minNode(model);
		}
		double bestXDelta = 100000000.0;
		if (fineTuning) {
			for (int i = 0; i < maxIterations; i++) {
				// Median Heuristic
				if (i != 0) {
					medianPos(i, model);
					minNode(model);
				}
				// if the total offset is less for the current positioning,
				// there
				// are less heavily angled edges and so the current positioning
				// is used
				if (currentXDelta < bestXDelta) {
					for (int j = 0; j < model.ranks.size(); j++) {
						JGraphHierarchyRank rank = (JGraphHierarchyRank) model.ranks
								.get(new Integer(j));
						Iterator iter = rank.iterator();
						while (iter.hasNext()) {
							JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
									.next();
							cell.setX(j, cell.getGeneralPurposeVariable(j));
						}
					}
					bestXDelta = currentXDelta;
				} else {
					// Restore the best positions
					for (int j = 0; j < model.ranks.size(); j++) {
						JGraphHierarchyRank rank = (JGraphHierarchyRank) model.ranks
								.get(new Integer(j));
						Iterator iter = rank.iterator();
						while (iter.hasNext()) {
							JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
									.next();
							cell.setGeneralPurposeVariable(j,
									(int) cell.getX(j));
						}
					}
				}
				minPath(model);
				currentXDelta = 0;
			}
		}
		if (compactLayout) {
			// Not yet working
			//compactLayout(model);
		}
		setCellLocations(facade, model);
		return model;
	}

	/**
	 * Performs one median positioning sweep in both directions
	 * 
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	private void minNode(JGraphHierarchyModel model) {
		// Queue all nodes
		LinkedList nodeList = new LinkedList();
		// Need to be able to map from cell to cellWrapper
		Map map = new Hashtable();
		Object[][] rank = new Object[model.maxRank + 1][];
		for (int i = 0; i <= model.maxRank; i++) {
			JGraphHierarchyRank rankSet = (JGraphHierarchyRank) model.ranks
					.get(new Integer(i));
			rank[i] = rankSet.toArray();
			for (int j = 0; j < rank[i].length; j++) {
				// Use the weight to store the rank and visited to store whether
				// or not the cell is in the list
				JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) rank[i][j];
				WeightedCellSorter cellWrapper = new WeightedCellSorter(cell, i);
				cellWrapper.rankIndex = j;
				cellWrapper.visited = true;
				nodeList.add(cellWrapper);
				map.put(cell, cellWrapper);
			}
		}
		// Set a limit of the maximum number of times we will access the queue
		// in case a loop appears
		int maxTries = nodeList.size() * 10;
		int count = 0;
		// Don't move cell within this value of their median
		int tolerance = 1;
		while (!nodeList.isEmpty() && count <= maxTries) {
			WeightedCellSorter cellWrapper = (WeightedCellSorter) nodeList
					.getFirst();
			JGraphAbstractHierarchyCell cell = cellWrapper.cell;
			int rankValue = cellWrapper.weightedValue;
			int rankIndex = cellWrapper.rankIndex;
			Object[] nextLayerConnectedCells = cell.getNextLayerConnectedCells(
					rankValue).toArray();
			Object[] previousLayerConnectedCells = cell
					.getPreviousLayerConnectedCells(rankValue).toArray();
			int numNextLayerConnected = nextLayerConnectedCells.length;
			int numPreviousLayerConnected = previousLayerConnectedCells.length;

			int medianNextLevel = medianXValue(nextLayerConnectedCells,
					rankValue + 1);
			int medianPreviousLevel = medianXValue(previousLayerConnectedCells,
					rankValue - 1);

			int numConnectedNeighbours = numNextLayerConnected
					+ numPreviousLayerConnected;
			int currentPosition = cell.getGeneralPurposeVariable(rankValue);
			double cellMedian = currentPosition;
			if (numConnectedNeighbours > 0) {
				cellMedian = (medianNextLevel * numNextLayerConnected + medianPreviousLevel
						* numPreviousLayerConnected)
						/ numConnectedNeighbours;
			}

			// Flag storing whether or not position has changed
			boolean positionChanged = false;
			if (cellMedian < currentPosition - tolerance) {
				if (rankIndex == 0) {
					cell.setGeneralPurposeVariable(rankValue, (int) cellMedian);
					positionChanged = true;
				} else {
					JGraphAbstractHierarchyCell leftCell = (JGraphAbstractHierarchyCell) rank[rankValue][rankIndex - 1];
					int leftLimit = leftCell
							.getGeneralPurposeVariable(rankValue);
					leftLimit = leftLimit + (int) leftCell.width / 2
							+ (int) intraCellSpacing + (int) cell.width / 2;
					if (leftLimit < cellMedian) {
						cell.setGeneralPurposeVariable(rankValue,
								(int) cellMedian);
						positionChanged = true;
					} else if (leftLimit < cell
							.getGeneralPurposeVariable(rankValue) - tolerance) {
						cell.setGeneralPurposeVariable(rankValue, leftLimit);
						positionChanged = true;
					}
				}
			} else if (cellMedian > currentPosition + tolerance) {
				int rankSize = rank[rankValue].length;
				if (rankIndex == rankSize - 1) {
					cell.setGeneralPurposeVariable(rankValue, (int) cellMedian);
					positionChanged = true;
				} else {
					JGraphAbstractHierarchyCell rightCell = (JGraphAbstractHierarchyCell) rank[rankValue][rankIndex + 1];
					int rightLimit = rightCell
							.getGeneralPurposeVariable(rankValue);
					rightLimit = rightLimit - (int) rightCell.width / 2
							- (int) intraCellSpacing - (int) cell.width / 2;
					if (rightLimit > cellMedian) {
						cell.setGeneralPurposeVariable(rankValue,
								(int) cellMedian);
						positionChanged = true;
					} else if (rightLimit > cell
							.getGeneralPurposeVariable(rankValue) + tolerance) {
						cell.setGeneralPurposeVariable(rankValue, rightLimit);
						positionChanged = true;
					}
				}
			}
			if (positionChanged) {
				// Add connected nodes to map and list
				for (int i = 0; i < nextLayerConnectedCells.length; i++) {
					JGraphAbstractHierarchyCell connectedCell = (JGraphAbstractHierarchyCell) nextLayerConnectedCells[i];
					WeightedCellSorter connectedCellWrapper = (WeightedCellSorter) map
							.get(connectedCell);
					if (connectedCellWrapper != null) {
						if (connectedCellWrapper.visited == false) {
							connectedCellWrapper.visited = true;
							nodeList.add(connectedCellWrapper);
						}
					}
				}
				// Add connected nodes to map and list
				for (int i = 0; i < previousLayerConnectedCells.length; i++) {
					JGraphAbstractHierarchyCell connectedCell = (JGraphAbstractHierarchyCell) previousLayerConnectedCells[i];
					WeightedCellSorter connectedCellWrapper = (WeightedCellSorter) map
							.get(connectedCell);
					if (connectedCellWrapper != null) {
						if (connectedCellWrapper.visited == false) {
							connectedCellWrapper.visited = true;
							nodeList.add(connectedCellWrapper);
						}
					}
				}
			}
			nodeList.removeFirst();
			cellWrapper.visited = false;
			count++;
		}
	}

	/**
	 * Performs one median positioning sweep in one direction
	 * 
	 * @param i
	 *            the iteration of the whole process
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	private void medianPos(int i, JGraphHierarchyModel model) {
		// Reverse sweep direction each time through this method
		boolean downwardSweep = (i % 2 == 0);
		if (downwardSweep) {
			for (int j = model.maxRank; j > 0; j--) {
				rankMedianPosition(j - 1, model, j);
			}
		} else {
			for (int j = 0; j < model.maxRank - 1; j++) {
				rankMedianPosition(j + 1, model, j);
			}
		}
	}

	/**
	 * Performs median minimisation over one rank.
	 * 
	 * @param rankValue
	 *            the layer number of this rank
	 * @param model
	 *            an internal model of the hierarchical layout
	 * @param nextRankValue
	 *            the layer number whose connected cels are to be laid out
	 *            relative to
	 */
	protected void rankMedianPosition(int rankValue,
			JGraphHierarchyModel model, int nextRankValue) {
		JGraphHierarchyRank rankSet = (JGraphHierarchyRank) model.ranks
				.get(new Integer(rankValue));
		Object[] rank = rankSet.toArray();
		// Form an array of the order in which the cell are to be processed
		// , the order is given by the weighted sum of the in or out edges,
		// depending on whether we're travelling up or down the hierarchy.
		WeightedCellSorter[] weightedValues = new WeightedCellSorter[rank.length];
		Map cellMap = new Hashtable(rank.length);

		for (int i = 0; i < rank.length; i++) {
			JGraphAbstractHierarchyCell currentCell = (JGraphAbstractHierarchyCell) rank[i];
			weightedValues[i] = new WeightedCellSorter();
			weightedValues[i].cell = currentCell;
			weightedValues[i].rankIndex = i;
			cellMap.put(currentCell, weightedValues[i]);
			Collection nextLayerConnectedCells = null;
			if (nextRankValue < rankValue) {
				nextLayerConnectedCells = currentCell
						.getPreviousLayerConnectedCells(rankValue);
			} else {
				nextLayerConnectedCells = currentCell
						.getNextLayerConnectedCells(rankValue);
			}

			// Calcuate the weighing based on this node type and those this
			// node is connected to on the next layer
			weightedValues[i].weightedValue = calculatedWeightedValue(
					currentCell, nextLayerConnectedCells);
		}

		Arrays.sort(weightedValues);
		// Set the new position of each node within the rank using
		// its temp variable
		for (int i = 0; i < weightedValues.length; i++) {
			int numConnectionsNextLevel = 0;
			JGraphAbstractHierarchyCell cell = weightedValues[i].cell;
			Object[] nextLayerConnectedCells = null;
			int medianNextLevel = 0;

			if (nextRankValue < rankValue) {
				nextLayerConnectedCells = cell.getPreviousLayerConnectedCells(
						rankValue).toArray();
			} else {
				nextLayerConnectedCells = cell.getNextLayerConnectedCells(
						rankValue).toArray();
			}

			if (nextLayerConnectedCells != null) {
				numConnectionsNextLevel = nextLayerConnectedCells.length;
				if (numConnectionsNextLevel > 0) {
					medianNextLevel = medianXValue(nextLayerConnectedCells,
							nextRankValue);
				} else {
					// For case of no connections on the next level set the
					// median to be the current position and try to be
					// positioned there
					medianNextLevel = cell.getGeneralPurposeVariable(rankValue);
				}
			}

			double leftBuffer = 0.0;
			double leftLimit = -100000000.0;
			for (int j = weightedValues[i].rankIndex - 1; j >= 0;) {
				WeightedCellSorter weightedValue = (WeightedCellSorter) cellMap
						.get(rank[j]);
				if (weightedValue != null) {
					JGraphAbstractHierarchyCell leftCell = weightedValue.cell;
					if (weightedValue.visited) {
						// The left limit is the right hand limit of that
						// cell
						// plus any allowance for unallocated cells
						// in-between
						leftLimit = leftCell
								.getGeneralPurposeVariable(rankValue)
								+ leftCell.width
								/ 2.0
								+ intraCellSpacing
								+ leftBuffer + cell.width / 2.0;
						;
						j = -1;
					} else {
						leftBuffer += leftCell.width + intraCellSpacing;
						j--;
					}
				}
			}
			double rightBuffer = 0.0;
			double rightLimit = 100000000.0;
			for (int j = weightedValues[i].rankIndex + 1; j < weightedValues.length;) {
				WeightedCellSorter weightedValue = (WeightedCellSorter) cellMap
						.get(rank[j]);
				if (weightedValue != null) {
					JGraphAbstractHierarchyCell rightCell = weightedValue.cell;
					if (weightedValue.visited) {
						// The left limit is the right hand limit of that
						// cell
						// plus any allowance for unallocated cells
						// in-between
						rightLimit = rightCell
								.getGeneralPurposeVariable(rankValue)
								- rightCell.width
								/ 2.0
								- intraCellSpacing
								- rightBuffer - cell.width / 2.0;
						j = weightedValues.length;
					} else {
						rightBuffer += rightCell.width + intraCellSpacing;
						j++;
					}
				}
			}
			if (medianNextLevel >= leftLimit && medianNextLevel <= rightLimit) {
				cell.setGeneralPurposeVariable(rankValue, (int) medianNextLevel);
			} else if (medianNextLevel < leftLimit) {
				// Couldn't place at median value, place as close to that
				// value as possible
				cell.setGeneralPurposeVariable(rankValue, (int) leftLimit);
				currentXDelta += leftLimit - medianNextLevel;
			} else if (medianNextLevel > rightLimit) {
				// Couldn't place at median value, place as close to that
				// value as possible
				cell.setGeneralPurposeVariable(rankValue, (int) rightLimit);
				currentXDelta += medianNextLevel - rightLimit;
			}

			weightedValues[i].visited = true;
		}
	}

	/**
	 * Calculates the priority the specified cell has based on the type of its
	 * cell and the cells it is connected to on the next layer
	 * 
	 * @param currentCell
	 *            the cell whose weight is to be calculated
	 * @param collection
	 *            the cells the specified cell is connected to
	 * @return the total weighted of the edges between these cells
	 */
	private int calculatedWeightedValue(
			JGraphAbstractHierarchyCell currentCell, Collection collection) {
		int totalWeight = 0;
		Iterator iter = collection.iterator();
		while (iter.hasNext()) {
			JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
					.next();
			if (currentCell.isVertex() && cell.isVertex()) {
				totalWeight++;
			} else if (currentCell.isEdge() && cell.isEdge()) {
				totalWeight += 8;
			} else {
				totalWeight += 2;
			}
		}
		return totalWeight;
	}

	/**
	 * Calculates the median position of the connected cell on the specified
	 * rank
	 * 
	 * @param connectedCells
	 *            the cells the candidate connects to on this level
	 * @param rankValue
	 *            the layer number of this rank
	 * @return the median rank order ( not x position ) of the connected cells
	 */
	private int medianXValue(Object[] connectedCells, int rankValue) {
		if (connectedCells.length == 0) {
			return 0;
		}
		int[] medianValues = new int[connectedCells.length];
		for (int i = 0; i < connectedCells.length; i++) {
			medianValues[i] = ((JGraphAbstractHierarchyCell) connectedCells[i])
					.getGeneralPurposeVariable(rankValue);
		}
		Arrays.sort(medianValues);
		if (connectedCells.length % 2 == 1) {
			// For odd numbers of adjacent vertices return the median
			return medianValues[connectedCells.length / 2];
		} else {
			int medianPoint = connectedCells.length / 2;
			int leftMedian = medianValues[medianPoint - 1];
			int rightMedian = medianValues[medianPoint];
			return ((leftMedian + rightMedian) / 2);
		}
	}

	/**
	 * Sets up cached information for speed
	 * 
	 * @param model
	 *            the model to cache
	 */
	private void initialise(JGraphHierarchyModel model) {
		// for (int i = 0; i < model.maxRank; i++) {
		// JGraphHierarchyRank rankSet = (JGraphHierarchyRank) model.ranks
		// .get(new Integer(i));
		// rank[i] = rankSet.toArray();
		// for (int j = 0; j < rank[i].length; j++) {
		// // Use the weight to store the rank and visited to store whether
		// // or not the cell is in the list
		// JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell)
		// rank[i][j];
		// WeightedCellSorter cellWrapper = new WeightedCellSorter(cell, i);
		// cellWrapper.rankIndex = j;
		// cellWrapper.visited = true;
		// nodeList.add(cellWrapper);
		// map.put(cell, cellWrapper);
		// }
		// }
		//
		// nextLayerConnectedCache
	}

	/**
	 * Sets up the layout in an initial positioning. The ranks are all centered
	 * as much as possible along the middle vertex in each rank. The other cells
	 * are then placed as close as possible on either side.
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	private void initialCoords(JGraphFacade facade, JGraphHierarchyModel model) {
		calculateWidestRank(facade, model);
		// Sweep up and down from the widest rank
		for (int i = widestRank; i >= 0; i--) {
			if (i < model.maxRank) {
				rankCoordinates(i, facade, model);
			}
		}

		for (int i = widestRank + 1; i <= model.maxRank; i++) {
			if (i > 0) {
				rankCoordinates(i, facade, model);
			}
		}
	}

	/**
	 * Sets up the layout in an initial positioning. All the first cells in each
	 * rank are moved to the left and the rest of the rank inserted as close
	 * together as their size and buffering permits. This method works on just
	 * the specified rank.
	 * 
	 * @param rankValue
	 *            the current rank being processed
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	protected void rankCoordinates(int rankValue, JGraphFacade facade,
			JGraphHierarchyModel model) {
		JGraphHierarchyRank rank = (JGraphHierarchyRank) model.ranks
				.get(new Integer(rankValue));
		// Pad out the initial cell spacing to give a better chance of a cell
		// not being restricted in one direction.
		double extraCellSpacing = (widestRankValue - rankWidths[rankValue])
				/ (rank.size() + 1);
		double localIntraCellSpacing = intraCellSpacing + extraCellSpacing;
		// Check this doesn't make the rank too wide, if it does, reduce it
		if (extraCellSpacing * (rank.size() + 1) + rankWidths[rankValue] > widestRankValue) {
			localIntraCellSpacing = intraCellSpacing;
		}
		double maxY = 0.0;
		double localX = initialX + extraCellSpacing;
		Iterator iter = rank.iterator();
		// Store whether or not any of the cells' bounds were unavailable so
		// to only issue the warning once for all cells
		boolean boundsWarning = false;
		while (iter.hasNext()) {
			JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
					.next();
			if (cell.isVertex()) {
				JGraphHierarchyNode node = (JGraphHierarchyNode) cell;
				Rectangle2D bounds = facade.getBounds(node.cell);
				if (bounds != null) {
					if (orientation == SwingConstants.NORTH
							|| orientation == SwingConstants.SOUTH) {
						cell.width = bounds.getWidth();
						cell.height = bounds.getHeight();
					} else {
						cell.width = bounds.getHeight();
						cell.height = bounds.getWidth();
					}
				} else {
					boundsWarning = true;
				}
				maxY = Math.max(maxY, cell.height);
			} else if (cell.isEdge()) {
				JGraphHierarchyEdge edge = (JGraphHierarchyEdge) cell;
				// The width is the number of additional parallel edges
				// time the parallel edge spacing
				int numEdges = 1;
				if (edge.edges != null) {
					numEdges = edge.edges.size();
				} else {
					logger.info("edge.edges is null");
				}
				cell.width = (numEdges - 1) * parallelEdgeSpacing;
			}
			// Set the initial x-value as being the best result so far
			localX += cell.width / 2.0;
			cell.setX(rankValue, localX);
			cell.setGeneralPurposeVariable(rankValue, (int) localX);
			localX += cell.width / 2.0;
			localX += localIntraCellSpacing;
		}
		if (boundsWarning == true) {
			logger.info("At least one cell has no bounds");
		}
	}

	/**
	 * Calculates the width rank in the hierarchy. Also set the y value of each
	 * rank whilst performing the calculation
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	protected void calculateWidestRank(JGraphFacade facade,
			JGraphHierarchyModel model) {
		// Starting y co-ordinate
		double y = -interRankCellSpacing;
		// Track the widest cell on the last rank since the y
		// difference depends on it
		double lastRankMaxCellHeight = 0.0;
		rankWidths = new double[model.maxRank + 1];
		rankY = new double[model.maxRank + 1];

		for (int rankValue = model.maxRank; rankValue >= 0; rankValue--) {
			// Keep track of the widest cell on this rank
			double maxCellHeight = 0.0;

			JGraphHierarchyRank rank = (JGraphHierarchyRank) model.ranks
					.get(new Integer(rankValue));
			double localX = initialX;
			Iterator iter = rank.iterator();
			// Store whether or not any of the cells' bounds were unavailable so
			// to only issue the warning once for all cells
			boolean boundsWarning = false;
			while (iter.hasNext()) {
				JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
						.next();
				if (cell.isVertex()) {
					JGraphHierarchyNode node = (JGraphHierarchyNode) cell;
					Rectangle2D bounds = facade.getBounds(node.cell);
					if (bounds != null) {
						if (orientation == SwingConstants.NORTH
								|| orientation == SwingConstants.SOUTH) {
							cell.width = bounds.getWidth();
							cell.height = bounds.getHeight();
						} else {
							cell.width = bounds.getHeight();
							cell.height = bounds.getWidth();
						}
					} else {
						boundsWarning = true;
					}
					maxCellHeight = Math.max(maxCellHeight, cell.height);
				} else if (cell.isEdge()) {
					JGraphHierarchyEdge edge = (JGraphHierarchyEdge) cell;
					// The width is the number of additional parallel edges
					// time the parallel edge spacing
					int numEdges = 1;
					if (edge.edges != null) {
						numEdges = edge.edges.size();
					} else {
						logger.info("edge.edges is null");
					}
					cell.width = (numEdges - 1) * parallelEdgeSpacing;
				}
				// Set the initial x-value as being the best result so far
				localX += cell.width / 2.0;
				cell.setX(rankValue, localX);
				cell.setGeneralPurposeVariable(rankValue, (int) localX);
				localX += cell.width / 2.0;
				localX += intraCellSpacing;
				if (localX > widestRankValue) {
					widestRankValue = localX;
					widestRank = rankValue;
				}
				rankWidths[rankValue] = localX;
			}
			if (boundsWarning == true) {
				logger.info("At least one cell has no bounds");
			}
			rankY[rankValue] = y;
			double distanceToNextRank = maxCellHeight / 2.0
					+ lastRankMaxCellHeight / 2.0 + interRankCellSpacing;
			lastRankMaxCellHeight = maxCellHeight;
			if (orientation == SwingConstants.NORTH
					|| orientation == SwingConstants.WEST) {
				y += distanceToNextRank;
			} else {
				y -= distanceToNextRank;
			}
			iter = rank.iterator();
			while (iter.hasNext()) {
				JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
						.next();
				cell.setY(rankValue, y);
			}
		}
	}

	/**
	 * Removes empty space between parts of the layout
	 * 
	 * @param model
	 */
	private void compactLayout(JGraphHierarchyModel model) {
		// List of separate areas in layout
		Set areas = new HashSet();
		for (int i = 0; i < model.ranks.size(); i++) {
			JGraphHierarchyRank rank = (JGraphHierarchyRank) model.ranks
					.get(new Integer(i));
			Iterator iter = rank.iterator();
			while (iter.hasNext()) {
				JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
						.next();
				double positionX = 0;
				if (cell.isVertex()) {
					JGraphHierarchyNode node = (JGraphHierarchyNode) cell;
					positionX = node.x[0] - node.width / 2;
					Rectangle2D area = new Rectangle2D.Double(positionX
							- intraCellSpacing, 0, node.width
							+ (intraCellSpacing * 2), 100000);
					integrateNewArea(area, areas, cell);
				} else if (cell.isEdge()) {
					JGraphHierarchyEdge edge = (JGraphHierarchyEdge) cell;
					// For parallel edges we need to seperate out the points a
					// little
					int numParallelEdges = edge.edges.size();
					int parallelEdgeBuffer = (int) parallelEdgeSpacing
							* numParallelEdges / 2;
					for (int j = edge.x.length - 1; j >= 0; j--) {
						positionX = edge.x[j];
						if (orientation == SwingConstants.EAST
								|| orientation == SwingConstants.WEST) {
							positionX = edge.y[j];
						}
						Rectangle2D area = new Rectangle2D.Double(positionX
								- intraCellSpacing, 0, parallelEdgeBuffer
								+ (intraCellSpacing * 2), 100000);
						integrateNewArea(area, areas, cell);
					}
				}
			}
		}
		// If there is more than one area need to compact sections
		if (areas.size() > 1) {
			Iterator iter = areas.iterator();
			while (iter.hasNext()) {
				Rectangle2D area = (Rectangle2D) iter.next();
			}
		}
	}

	/**
	 * Adds a new rectangle to any intersecting rectangles stored in areas. If
	 * no intersection a new area is created from its values.
	 * 
	 * @param area
	 * @param areas
	 */
	private void integrateNewArea(Rectangle2D area, Set areas,
			JGraphAbstractHierarchyCell cell) {
		Iterator iter = areas.iterator();
		// Whether or not a cached area is found that contains area
		boolean areaFound = false;
		while (iter.hasNext()) {
			AreaSpatialCache cachedArea = (AreaSpatialCache) iter.next();
			if (cachedArea.intersects(area)) {
				cachedArea.setRect(cachedArea.createUnion(area));
				if (areaFound == false) {
					cachedArea.cells.add(cell);
					areaFound = true;
				}
			}
		}
		if (areaFound == false) {
			// Create new area to hold cell's area
			AreaSpatialCache newArea = new AreaSpatialCache();
			newArea.setRect(area.getX(), 0, area.getWidth(), 100000);
			areas.add(newArea);
			newArea.cells.add(cell);

		}
		// Check if any of the cached areas now overlap, if they do, merge them
		Set removedAreas = new HashSet();
		while (iter.hasNext()) {
			AreaSpatialCache cachedArea = (AreaSpatialCache) iter.next();
			// Skip area if already flagged for removal
			if (!removedAreas.contains(cachedArea)) {
				Iterator iter2 = areas.iterator();
				while (iter2.hasNext()) {
					AreaSpatialCache cachedArea2 = (AreaSpatialCache) iter2
							.next();
					if (cachedArea.intersects(cachedArea2)) {
						cachedArea.setRect(cachedArea.createUnion(cachedArea2));
						removedAreas.add(cachedArea2);
					}
				}
			}
		}
		areas.removeAll(removedAreas);
	}

	/**
	 * Sets the cell locations in the facade to those stored after this layout
	 * processing step has completed.
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	private void setCellLocations(JGraphFacade facade,
			JGraphHierarchyModel model) {
		// Stores any translation needs to separate this context properly
		// from any last context
		double contextTranslation = 0.0;
		// Store all the vertices in case we need to translate them
		List vertices = new ArrayList();
		// Run through the ranks twice, once for vertices, then for edges
		// The reason for this is if the vertices need to offset again from
		// the last context
		for (int cellType = 0; cellType < 2; cellType++) {
			for (int i = 0; i < model.ranks.size(); i++) {
				JGraphHierarchyRank rank = (JGraphHierarchyRank) model.ranks
						.get(new Integer(i));
				Iterator iter = rank.iterator();
				while (iter.hasNext()) {
					JGraphAbstractHierarchyCell cell = (JGraphAbstractHierarchyCell) iter
							.next();
					if (cellType == 0 && cell.isVertex()) {
						JGraphHierarchyNode node = (JGraphHierarchyNode) cell;
						Object realCell = node.cell;
						vertices.add(realCell);
						double positionX = node.x[0] - node.width / 2;
						double positionY = node.y[0] - node.height / 2;
						if (orientation == SwingConstants.NORTH
								|| orientation == SwingConstants.SOUTH) {
							facade.setLocation(realCell, positionX, positionY);
						} else {
							facade.setLocation(realCell, positionY, positionX);
						}
						// Stores the positive X limit of this graph context to
						// know where the next context can start
						limitX = Math.max(limitX, positionX + node.width);
						// It is possible that a rank sticks out further in the
						// -ve x direction than the widest rank. In this case
						// store a shift that is required to apply to all cells
						// after positioning is complete.
						if (positionX + 1 < initialX) {
							contextTranslation = initialX - positionX;
						}
					} else if (cellType == 1 && cell.isEdge()) {
						JGraphHierarchyEdge edge = (JGraphHierarchyEdge) cell;
						// For parallel edges we need to seperate out the points
						// a
						// little
						Iterator parallelEdges = edge.edges.iterator();
						double offsetX = 0.0;
						// Only set the edge control points once
						if (edge.temp[0] != 101207) {
							while (parallelEdges.hasNext()) {
								Object realEdge = parallelEdges.next();
								List oldPoints = facade.getPoints(realEdge);
								List newPoints = new ArrayList(
										(edge.x.length) + 2);
								newPoints.add(oldPoints.get(0));
								if (edge.isReversed()) {
									// Reversed edges need the points inserted
									// in
									// reverse order
									for (int j = 0; j < edge.x.length; j++) {
										double positionX = edge.x[j] + offsetX
												+ contextTranslation;
										if (orientation == SwingConstants.NORTH
												|| orientation == SwingConstants.SOUTH) {
											newPoints.add(new Point2D.Double(
													positionX, edge.y[j]));
										} else {
											newPoints.add(new Point2D.Double(
													edge.y[j], positionX));
										}
										limitX = Math.max(limitX, positionX);
									}
									processReversedEdge(edge, realEdge);
								} else {
									for (int j = edge.x.length - 1; j >= 0; j--) {
										double positionX = edge.x[j] + offsetX
												+ contextTranslation;
										if (orientation == SwingConstants.NORTH
												|| orientation == SwingConstants.SOUTH) {
											newPoints.add(new Point2D.Double(
													positionX, edge.y[j]));
										} else {
											newPoints.add(new Point2D.Double(
													edge.y[j], positionX));
										}
										limitX = Math.max(limitX, positionX);
									}
								}
								newPoints
										.add(oldPoints.get(oldPoints.size() - 1));
								facade.setPoints(realEdge, newPoints);
								facade.disableRouting(realEdge);
								// Increase offset so next edge is drawn next to
								// this one
								if (offsetX == 0.0) {
									offsetX = parallelEdgeSpacing;
								} else if (offsetX > 0) {
									offsetX = -offsetX;
								} else {
									offsetX = -offsetX + parallelEdgeSpacing;
								}
							}
							edge.temp[0] = 101207;
						}
					}
				}
			}
		}
		// Move the context by the amount it's overlapping the last context
		if (contextTranslation >= 1.0) {
			if (orientation == SwingConstants.NORTH
					|| orientation == SwingConstants.SOUTH) {
				facade.translateCells(vertices, contextTranslation, 0);
			} else if (orientation == SwingConstants.EAST
					|| orientation == SwingConstants.WEST) {
				facade.translateCells(vertices, 0, contextTranslation);
			}
		}
		// Increase the limit of this context accordingly
		limitX += contextTranslation;
	}

	/**
	 * Hook to add additional processing
	 * 
	 * @param edge
	 *            The hierarchical model edge
	 * @param realEdge
	 *            The real edge in the graph
	 */
	private void processReversedEdge(JGraphHierarchyEdge edge, Object realEdge) {
		// Added as hook for customer
	}

	/**
	 * A utility class used to track cells whilst sorting occurs on the weighted
	 * sum of their connected edges. Does not violate (x.compareTo(y)==0) ==
	 * (x.equals(y))
	 */
	protected class WeightedCellSorter implements Comparable {

		/**
		 * The weighted value of the cell stored
		 */
		public int weightedValue = 0;

		/**
		 * Whether or not to flip equal weight values.
		 */
		public boolean nudge = false;

		/**
		 * Whether or not this cell has been visited in the current assignment
		 */
		public boolean visited = false;

		/**
		 * The index this cell is in the model rank
		 */
		public int rankIndex;

		/**
		 * The cell whose median value is being calculated
		 */
		public JGraphAbstractHierarchyCell cell = null;

		public WeightedCellSorter() {
			this(null, 0);
		}

		public WeightedCellSorter(JGraphAbstractHierarchyCell cell,
				int weightedValue) {
			this.cell = cell;
			this.weightedValue = weightedValue;
		}

		/**
		 * comparator on the medianValue
		 * 
		 * @param arg0
		 *            the object to be compared to
		 * @return the standard return you would expect when comparing two
		 *         double
		 */
		public int compareTo(Object arg0) {
			if (arg0 instanceof WeightedCellSorter) {
				return Double.compare(
						((WeightedCellSorter) arg0).weightedValue,
						weightedValue);
				// if (weightedValue > ((WeightedCellSorter)
				// arg0).weightedValue) {
				// return -1;
				// } else if (weightedValue < ((WeightedCellSorter)
				// arg0).weightedValue) {
				// return 1;
				// } else {
				// if (arg0==this) {
				// return 0;
				// }
				// if (nudge) {
				// return -1;
				// } else {
				// return 1;
				// }
				// }
			} else {
				return 0;
			}
		}

	}

	/**
	 * Utility class that stores a collection of vertices and edge points within
	 * a certain area. This area includes the buffer lengths of cells.
	 */
	protected class AreaSpatialCache extends Rectangle2D.Double {
		public Set cells = new HashSet();
	}

	/**
	 * @return Returns the interRankCellSpacing.
	 */
	public double getInterRankCellSpacing() {
		return interRankCellSpacing;
	}

	/**
	 * @param interRankCellSpacing
	 *            The interRankCellSpacing to set.
	 */
	public void setInterRankCellSpacing(double interRankCellSpacing) {
		this.interRankCellSpacing = interRankCellSpacing;
	}

	/**
	 * @return Returns the intraCellSpacing.
	 */
	public double getIntraCellSpacing() {
		return intraCellSpacing;
	}

	/**
	 * @param intraCellSpacing
	 *            The intraCellSpacing to set.
	 */
	public void setIntraCellSpacing(double intraCellSpacing) {
		this.intraCellSpacing = intraCellSpacing;
	}

	/**
	 * @return Returns the orientation.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation
	 *            The orientation to set.
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return Returns the limitX.
	 */
	public double getLimitX() {
		return limitX;
	}

	/**
	 * @param limitX
	 *            The limitX to set.
	 */
	public void setLimitX(double limitX) {
		this.limitX = limitX;
	}

	/**
	 * @return Returns the fineTuning.
	 */
	public boolean isFineTuning() {
		return fineTuning;
	}

	/**
	 * @param fineTuning
	 *            The fineTuning to set.
	 */
	public void setFineTuning(boolean fineTuning) {
		this.fineTuning = fineTuning;
	}

	/**
	 * @return Returns the compactLayout.
	 */
	public boolean isCompactLayout() {
		return compactLayout;
	}

	/**
	 * @param compactLayout
	 *            The compactLayout to set.
	 */
	public void setCompactLayout(boolean compactLayout) {
		this.compactLayout = compactLayout;
	}

	/**
	 * Sets the logging level of this class
	 * 
	 * @param level
	 *            the logging level to set
	 */
	public void setLoggerLevel(Level level) {
		try {
			logger.setLevel(level);
		} catch (SecurityException e) {
			// Probably running in an applet
		}
	}

	/**
	 * Straightens out chains of virtual nodes where possible
	 * 
	 * @param model
	 *            an internal model of the hierarchical layout
	 */
	protected void minPath(JGraphHierarchyModel model) {
		// Work down and up each edge with at least 2 control points
		// trying to straighten each one out. If the same number of
		// straight segments are formed in both directions, the
		// preferred direction used is the one where the final
		// control points have the least offset from the connectable
		// region of the terminating vertices
		Map<Object, JGraphHierarchyEdge> edges = model.getEdgeMapper();

		for (JGraphAbstractHierarchyCell cell : edges.values()) {
			if (cell.maxRank > cell.minRank + 2) {
				int numEdgeLayers = cell.maxRank - cell.minRank - 1;
				// At least two virtual nodes in the edge
				// Check first whether the edge is already straight
				int referenceX = cell
						.getGeneralPurposeVariable(cell.minRank + 1);
				boolean edgeStraight = true;
				int refSegCount = 0;

				for (int i = cell.minRank + 2; i < cell.maxRank; i++) {
					int x = cell.getGeneralPurposeVariable(i);

					if (referenceX != x) {
						edgeStraight = false;
						referenceX = x;
					} else {
						refSegCount++;
					}
				}

				if (edgeStraight) {
					continue;
				}

				int upSegCount = 0;
				int downSegCount = 0;
				double upXPositions[] = new double[numEdgeLayers - 1];
				double downXPositions[] = new double[numEdgeLayers - 1];

				double currentX = cell.getX(cell.minRank + 1);

				for (int i = cell.minRank + 1; i < cell.maxRank - 1; i++) {
					// Attempt to straight out the control point on the
					// next segment up with the current control point.
					double nextX = cell.getX(i + 1);

					if (currentX == nextX) {
						upXPositions[i - cell.minRank - 1] = currentX;
						upSegCount++;
					} else if (repositionValid(model, cell, i + 1, currentX)) {
						upXPositions[i - cell.minRank - 1] = currentX;
						upSegCount++;
						// Leave currentX at same value
					} else {
						upXPositions[i - cell.minRank - 1] = nextX;
						currentX = nextX;
					}
				}

				currentX = cell.getX(cell.maxRank - 1);

				for (int i = cell.maxRank - 1; i > cell.minRank + 1; i--) {
					// Attempt to straight out the control point on the
					// next segment down with the current control point.
					double nextX = cell.getX(i - 1);

					if (currentX == nextX) {
						downXPositions[i - cell.minRank - 2] = currentX;
						downSegCount++;
					} else if (repositionValid(model, cell, i - 1, currentX)) {
						downXPositions[i - cell.minRank - 2] = currentX;
						downSegCount++;
						// Leave currentX at same value
					} else {
						downXPositions[i - cell.minRank - 2] = cell.getX(i - 1);
						currentX = nextX;
					}
				}

				if (downSegCount <= refSegCount && upSegCount <= refSegCount) {
					// Neither of the new calculation provide a straighter edge
					continue;
				}

				if (downSegCount >= upSegCount) {
					// Apply down calculation values
					for (int i = cell.maxRank - 2; i > cell.minRank; i--) {
						cell.setX(i, (int) downXPositions[i - cell.minRank - 1]);
					}
				} else if (upSegCount > downSegCount) {
					// Apply up calculation values
					for (int i = cell.minRank + 2; i < cell.maxRank; i++) {
						cell.setX(i, (int) upXPositions[i - cell.minRank - 2]);
					}
				} else {
					// Neither direction provided a favourable result
					// But both calculations are better than the
					// existing solution, so apply the one with minimal
					// offset to attached vertices at either end.

				}
			}
		}
	}

	/**
	 * Determines whether or not a node may be moved to the specified x position
	 * on the specified rank
	 * 
	 * @param model
	 *            the layout model
	 * @param cell
	 *            the cell being analysed
	 * @param rank
	 *            the layer of the cell
	 * @param position
	 *            the x position being sought
	 * @return whether or not the virtual node can be moved to this position
	 */
	protected boolean repositionValid(JGraphHierarchyModel model,
			JGraphAbstractHierarchyCell cell, int rank, double position) {
		JGraphHierarchyRank rankSet = (JGraphHierarchyRank) model.ranks
				.get(new Integer(rank));
		JGraphAbstractHierarchyCell[] rankArray = (JGraphAbstractHierarchyCell[]) rankSet
				.toArray(new JGraphAbstractHierarchyCell[rankSet.size()]);
		int rankIndex = -1;

		for (int i = 0; i < rankArray.length; i++) {
			if (cell == rankArray[i]) {
				rankIndex = i;
				break;
			}
		}

		if (rankIndex < 0) {
			return false;
		}

		int currentX = cell.getGeneralPurposeVariable(rank);

		if (position < currentX) {
			// Trying to move node to the left.
			if (rankIndex == 0) {
				// Left-most node, can move anywhere
				return true;
			}

			JGraphAbstractHierarchyCell leftCell = rankArray[rankIndex - 1];
			int leftLimit = leftCell.getGeneralPurposeVariable(rank);
			leftLimit = leftLimit + (int) leftCell.width / 2
					+ (int) intraCellSpacing + (int) cell.width / 2;

			if (leftLimit <= position) {
				return true;
			} else {
				return false;
			}
		} else if (position > currentX) {
			// Trying to move node to the right.
			if (rankIndex == rankArray.length - 1) {
				// Right-most node, can move anywhere
				return true;
			}

			JGraphAbstractHierarchyCell rightCell = rankArray[rankIndex + 1];
			int rightLimit = rightCell.getGeneralPurposeVariable(rank);
			rightLimit = rightLimit - (int) rightCell.width / 2
					- (int) intraCellSpacing - (int) cell.width / 2;

			if (rightLimit >= position) {
				return true;
			} else {
				return false;
			}
		}

		return true;
	}

}
