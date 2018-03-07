/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package au.qut.graph.transitivity;

public class MatrixBasedTransitivity {

	public static void print(boolean[][] m) {
		int n = m.length;

		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++)
				System.out.printf("%3d", m[k][i]?1:0);
			System.out.println();
		}
	}
	public static void transitiveClosure(boolean[][] m) {
		int n = m.length;

		for (int k = 0; k < n; k++)
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					m[i][j] |= (m[i][k] & m[k][j]);
	}

	public static boolean[][] transitiveReduction(boolean[][] m) {
		int n = m.length;

		boolean[][] originalMatrix = new boolean[n][n];
		copyMatrix(m, originalMatrix);

		for (int j = 0; j < n; ++j)
			for (int i = 0; i < n; ++i)
				if (originalMatrix[i][j])
					for (int k = 0; k < n; ++k)
						if (originalMatrix[j][k])
							m[i][k] = false;
		
		return originalMatrix;
	}

	public static boolean[][] transitiveReduction(boolean[][] m, int size) {
		int n = m.length;

		boolean[][] originalMatrix = new boolean[n][n];
		copyMatrix(m, originalMatrix);

		for (int j = 0; j < size; ++j)
			for (int i = 0; i < size; ++i)
				if (originalMatrix[i][j])
					for (int k = 0; k < size; ++k)
						if (originalMatrix[j][k])
							m[i][k] = false;
		
		return originalMatrix;
	}

	
	private static void copyMatrix(boolean[][] m, boolean[][] copyOfM) {
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m.length; j++)
				copyOfM[i][j] = m[i][j];
		
	}
}
