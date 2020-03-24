/* This code is PUBLIC DOMAIN */
package org.apromore.jgraph.util2;

import java.util.Arrays;

/**
 * Interpolates given values by B-Splines.
 * 
 * @author krueger
 */
public class Spline {

	private double[] xx;
	private double[] yy;

	private double[] a;
	private double[] b;
	private double[] c;
	private double[] d;

	/** tracks the last index found since that is mostly commonly the next one used */
	private int storageIndex = 0;

	/**
	 * Creates a new Spline.
	 * @param xx
	 * @param yy
	 */
	public Spline(double[] xx, double[] yy) {
		setValues(xx, yy);
	}

	/**
	 * Set values for this Spline.
	 * @param xx
	 * @param yy
	 */
	public void setValues(double[] xx, double[] yy) {
		this.xx = xx;
		this.yy = yy;
		if (xx.length > 1) {
			calculateCoefficients();
		}
	}

	/**
	 * Returns an interpolated value.
	 * @param x
	 * @return the interpolated value
	 */
	public double getValue(double x) {
		if (xx.length == 0) {
			return Double.NaN;
		}

		if (xx.length == 1) {
			if (xx[0] == x) {
				return yy[0];
			} else {
				return Double.NaN;
			}
		}

		int index = Arrays.binarySearch(xx, x);
		if (index > 0) {
			return yy[index];
		}

		index = - (index + 1) - 1;
		//TODO linear interpolation or extrapolation
		if (index < 0) {
			return yy[0];
		}

		return a[index]
			+ b[index] * (x - xx[index])
			+ c[index] * Math.pow(x - xx[index], 2)
			+ d[index] * Math.pow(x - xx[index], 3);
	}

	/**
	 * Returns an interpolated value. To be used when a long sequence of values
	 * are required in order, but ensure checkValues() is called beforehand to
	 * ensure the boundary checks from getValue() are made
	 * @param x
	 * @return the interpolated value
	 */
	public double getFastValue(double x) {
		// Fast check to see if previous index is still valid
		if (storageIndex > -1 && storageIndex < xx.length-1 && x > xx[storageIndex] && x < xx[storageIndex + 1]) {

		} else {
			int index = Arrays.binarySearch(xx, x);
			if (index > 0) {
				return yy[index];
			}
			index = - (index + 1) - 1;
			storageIndex = index;
		}
	
		//TODO linear interpolation or extrapolation
		if (storageIndex < 0) {
			return yy[0];
		}
		double value = x - xx[storageIndex];
		return a[storageIndex]
					+ b[storageIndex] * value
					+ c[storageIndex] * (value * value)
					+ d[storageIndex] * (value * value * value);
	}

	/**
	 * Used to check the correctness of this spline
	 */
	public boolean checkValues() {
		if (xx.length < 2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns the first derivation at x.
	 * @param x
	 * @return the first derivation at x
	 */
	public double getDx(double x) {
		if (xx.length == 0 || xx.length == 1) {
			return 0;
		}

		int index = Arrays.binarySearch(xx, x);
		if (index < 0) {
			index = - (index + 1) - 1;
		}

		return b[index]
			+ 2 * c[index] * (x - xx[index])
			+ 3 * d[index] * Math.pow(x - xx[index], 2);
	}

	/**
	 * Calculates the Spline coefficients.
	 */
	private void calculateCoefficients() {
		int N = yy.length;
		a = new double[N];
		b = new double[N];
		c = new double[N];
		d = new double[N];

		if (N == 2) {
			a[0] = yy[0];
			b[0] = yy[1] - yy[0];
			return;
		}

		double[] h = new double[N - 1];
		for (int i = 0; i < N - 1; i++) {
			a[i] = yy[i];
			h[i] = xx[i + 1] - xx[i];
			// h[i] is used for division later, avoid a NaN
			if (h[i] == 0.0) {
				h[i] = 0.01;
			}
		}
		a[N - 1] = yy[N - 1];

		double[][] A = new double[N - 2][N - 2];
		double[] y = new double[N - 2];
		for (int i = 0; i < N - 2; i++) {
			y[i] =
				3
					* ((yy[i + 2] - yy[i + 1]) / h[i
						+ 1]
						- (yy[i + 1] - yy[i]) / h[i]);

			A[i][i] = 2 * (h[i] + h[i + 1]);

			if (i > 0) {
				A[i][i - 1] = h[i];
			}

			if (i < N - 3) {
				A[i][i + 1] = h[i + 1];
			}
		}
		solve(A, y);

		for (int i = 0; i < N - 2; i++) {
			c[i + 1] = y[i];
			b[i] = (a[i + 1] - a[i]) / h[i] - (2 * c[i] + c[i + 1]) / 3 * h[i];
			d[i] = (c[i + 1] - c[i]) / (3 * h[i]);
		}
		b[N - 2] =
			(a[N - 1] - a[N - 2]) / h[N
				- 2]
				- (2 * c[N - 2] + c[N - 1]) / 3 * h[N
				- 2];
		d[N - 2] = (c[N - 1] - c[N - 2]) / (3 * h[N - 2]);
	}

	/**
	 * Solves Ax=b and stores the solution in b.
	 */
	public void solve(double[][] A, double[] b) {
		int n = b.length;
		for (int i = 1; i < n; i++) {
			A[i][i - 1] = A[i][i - 1] / A[i - 1][i - 1];
			A[i][i] = A[i][i] - A[i - 1][i] * A[i][i - 1];
			b[i] = b[i] - A[i][i - 1] * b[i - 1];
		}

		b[n - 1] = b[n - 1] / A[n - 1][n - 1];
		for (int i = b.length - 2; i >= 0; i--) {
			b[i] = (b[i] - A[i][i + 1] * b[i + 1]) / A[i][i];
		}
	}
}
