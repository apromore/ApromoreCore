/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.stats;

import java.util.Arrays;
import java.util.Random;

/**
 * The <code>Statistics</code> class provides static utility methods
 * for statistical computations.
 *
 * @author  Bob Carpenter
 * @version 3.5
 * @since   LingPipe2.0
 */
public class Statistics {

    // don't allow instances
    private Statistics() {
        /* do nothing */
    }

    /**
     * Returns the Kullback-Leibler divergence of the second specified
     * Dirichlet distribution relative to the first.  The Dirichlet
     * distributions are specified by their distribution and concentration,
     * which are folded into a single count argument.
     *
     * <p>The KL divergence between two Dirichlet distributions with
     * parameters <code>xs</code> and <code>ys</code> of dimensionality
     * <code>n</code> is:
     *
     * <blockquote><pre>
     * D<sub><sub>KL</sub></sub>(Dirichlet(xs) || Dirichlet(ys))
     * = <big><big>&#8747;</big></big>  p(&theta;|xs) log ( p(&theta;|xs) / p(&theta;|ys) ) <i>d</i>&theta;
     * = log &Gamma;(<big><big>&Sigma;</big></big><sub><sub>i &lt; n</sub></sub> xs[i])
     *   - log &Gamma;(<big><big>&Sigma;</big></big><sub><sub>i &lt; n</sub></sub> ys[i])
     *   - <big><big>&Sigma;</big></big><sub><sub>i &lt; n</sub></sub> log &Gamma;(xs[i])
     *   + <big><big>&Sigma;</big></big><sub><sub>i &lt; n</sub></sub> log &Gamma;(ys[i])
     *   + <big><big>&Sigma;</big></big><sub><sub>i &lt; n</sub></sub> (xs[i] - ys[i]) * (&Psi;(xs[i]) - &Psi;(<big>&Sigma;</big><sub><sub>k &lt; n</sub></sub> xs[i]))</pre></blockquote>
     *
     * where <code>&Gamma;</code> is the gamma function (see {@link
     * com.aliasi.util.Math#log2Gamma(double)}), and where
     * <code>&Psi;</code> is the digamma function defined in {@link
     * com.aliasi.util.Math#digamma(double)}.
     *
     * <p>This method in keeping with other information-theoretic
     * methods returns the answer in bits (base 2) rather than nats (base <code><i>e</i></code>).
     * The return is rescaled from the natural-log based divergence:
     *
     * <blockquote><pre>
     * klDivergenceDirichlet(xs,ys)
     * = D<sub><sub>KL</sub></sub>(Dirichlet(xs) || Dirichlet(ys)) / log<sub><sub>2</sub></sub> <i>e</i></pre></blockquote>
     *
     * <p>Further descriptions of the computation of KL-divergence between
     * Dirichlets may be found in:
     *
     * <ul>

     * <li>W.D. Penny. 2001. <a
     * href="http://www.fil.ion.ucl.ac.uk/~wpenny/publications/densities.ps">Kullback-Liebler
     * Divergences of Normal, Gamma, Dirichlet and Wishart
     * Densities.</a> Technical report, Wellcome Department of
     * Cognitive Neurology, 2001.</li> <li>

     * <li>Blei, Franks, Jordan and Mian. 2006. <a href="http://www.pubmedcentral.nih.gov/articlerender.fcgi?artid=1533868">Statistical
     * modeling of biomedical corpora</a>.  <i>BMC Bioinformatics</i>
     * <b>7</b>:250.</li>
     * </ul>
     *
     * @param xs Dirichlet parameter for the first distribution.
     * @param ys Dirichlet parameter for the second distribution.
     * @return The KL-divergence of the second Dirichlet distribution
     * relative to the first.
     */
    public static double klDivergenceDirichlet(double[] xs, double[] ys) {
        verifyDivergenceDirichletArgs(xs,ys);
        // verifyDivergenceArgs(xs,ys);
        double sumXs = sum(xs);
        double sumYs = sum(ys);
        double divergence
            = (logGamma(sumXs)
               - logGamma(sumYs));
        double digammaSumXs = com.aliasi.util.Math.digamma(sumXs);
        for (int i = 0; i < xs.length; ++i) {
            divergence +=
                (logGamma(ys[i])
                 - logGamma(xs[i]))
                + (xs[i] - ys[i]) * (com.aliasi.util.Math.digamma(xs[i]) - digammaSumXs);
        }
        return divergence;
    }

    static void verifyDivergenceDirichletArgs(double[] xs, double[] ys) {
        if (xs.length != ys.length) {
            String msg = "Parameter arrays must be the same length."
                + " Found xs.length=" + xs.length
                + " ys.length=" + ys.length;
            throw new IllegalArgumentException(msg);
        }
        for (int i = 0; i < xs.length; ++i) {
            if (xs[i] <= 0.0 || Double.isInfinite(xs[i]) || Double.isNaN(xs[i])) {
                String msg = "All parameters must be positive and finite."
                    + " Found xs[" + i + "]=" + xs[i];
                throw new IllegalArgumentException(msg);
            }
        }
        for (int i = 0; i < ys.length; ++i) {
            if (ys[i] <= 0.0 || Double.isInfinite(ys[i]) || Double.isNaN(ys[i])) {
                String msg = "All parameters must be positive and finite."
                    + " Found ys[" + i + "]=" + ys[i];
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * Returns the symmetric version of the Kullback-Leibler divergence
     * between the Dirichlet distributions determined by the specified
     * parameters.
     *
     * <p>The symmetrized KL divergence for Dirichlets is just the
     * average of both relative divergences:
     *
     * <blockquote><pre>
     * D<sub><sub>SKL</sub></sub>(Dirichlet(xs), Dirichlet(ys))
     * = (D<sub><sub>KL</sub></sub>(Dirichlet(xs) || Dirichlet(ys)) + D<sub><sub>KL</sub></sub>(Dirichlet(ys) || Dirichlet(xs))) / 2
     * </pre></blockquote>
     *
     * <p>See the method documentation for {@link
     * #klDivergenceDirichlet(double[],double[])} for a definition of
     * the relative divergence for Dirichlet distributions.
     *
     * @param xs Dirichlet parameter for the first distribution.
     * @param ys Dirichlet parameter for the second distribution.
     * @return The symmetrized KL-divergence of the distributions.
     */
    public static double symmetrizedKlDivergenceDirichlet(double[] xs, double[] ys) {
        return (klDivergenceDirichlet(xs,ys) + klDivergenceDirichlet(ys,xs)) / 2.0;
    }

    static double logGamma(double x) {
        return com.aliasi.util.Math.log2Gamma(x)
            / com.aliasi.util.Math.log2(java.lang.Math.E);
    }

    static double sum(double[] xs) {
        double sum = 0.0;
        for (int i = 0; i < xs.length; ++i)
            sum += xs[i];
        return sum;
    }

    /**
     * Returns the Kullback-Leibler divergence of the second
     * specified multinomial relative to the first.
     *
     * <p>The K-L divergence of a multinomial <code>q</code> relative
     * to a multinomial <code>p</code>, both with <code>n</code>
     * outcomes, is:
     *
     * <blockquote><pre>
     * D<sub><sub>KL</sub></sub>(p||q)
     * = <big><big><big>&Sigma;</big></big></big><sub><sub>i &lt; n</sub></sub>  p(i) log<sub>2</sub> (p(i) / q(i))</pre></blockquote>
     *
     * The value is guaranteed to be non-negative, and will be 0.0
     * only if the two distributions are identicial.  If any outcome
     * has zero probability in <code>q</code> and non-zero probability
     * in <code>p</code>, the result is infinite.
     *
     * <p>KL divergence is not symmetric.  That is, there are
     * <code>p</code> and <code>q</code> such that
     * <code>D<sub><sub>KL</sub></sub>(p||q) !=
     * D<sub><sub>KL</sub></sub>(q||p)</code>.  See {@link
     * #symmetrizedKlDivergence(double[],double[])} and {@link
     * #jsDivergence(double[],double[])} for symmetric variants.
     *
     * <p>KL divergence is equivalent to conditional entropy, although
     * it is written in the opposite order.  If <code>H(p,q)</code> is
     * the joint entropy of the distributions <code>p</code> and
     * <code>q</code>, and <code>H(p)</code> is the entropy of
     * <code>p</code>, then:
     *
     * <blockquote><pre>
     * D<sub><sub>KL</sub></sub>(p||q) = H(p,q) - H(p)</pre></blockquote>
     *
     * @param p First multinomial distribution.
     * @param q Second multinomial distribution.
     * @throws IllegalArgumentException If the distributions are not
     * the same length or have entries less than zero or greater than
     * 1.
     */
    public static double klDivergence(double[] p, double[] q) {
        verifyDivergenceArgs(p,q);
        double divergence = 0.0;
        int len = p.length;
        for (int i = 0; i < len; ++i) {
            if (p[i] > 0.0 && p[i] != q[i])
                divergence += p[i] * com.aliasi.util.Math.log2(p[i] / q[i]);
        }
        return divergence;
    }

    static void verifyDivergenceArgs(double[] p, double[] q) {
        if (p.length != q.length) {
            String msg = "Input distributions must have same length."
                + " Found p.length=" + p.length
                + " q.length=" + q.length;
            throw new IllegalArgumentException(msg);
        }
        int len = p.length;
        for (int i = 0; i < len; ++i) {
            if (p[i] < 0.0
                || p[i] > 1.0
                || Double.isNaN(p[i])
                || Double.isInfinite(p[i])) {
                String msg = "p[i] must be between 0.0 and 1.0 inclusive."
                    + " found p[" + i + "]=" + p[i];
                throw new IllegalArgumentException(msg);
            }
            if (q[i] < 0.0
                || q[i] > 1.0
                || Double.isNaN(q[i])
                || Double.isInfinite(q[i])) {
                String msg = "q[i] must be between 0.0 and 1.0 inclusive."
                    + " found q[" + i + "] =" + q[i];
                throw new IllegalArgumentException(msg);
            }
        }
    }

    /**
     * Returns the symmetrized KL-divergence between the specified distributions.
     * The symmetrization is carried out by averaging their relative
     * divergences:
     *
     * <blockquote><pre>
     * D<sub><sub>SKL</sub></sub>(p,q)
     * = ( D<sub><sub>KL</sub></sub>(p,q) + D<sub><sub>KL</sub></sub>(q,p) ) / 2
     * </pre></blockquote>
     *
     * @param p First multinomial.
     * @param q Second multinomial.
     * @return The Symmetrized KL divergence between the multinomials.
     * @throws IllegalArgumentException If the distributions are not
     * the same length or have entries less than zero or greater than
     * 1.
     */
    public static double symmetrizedKlDivergence(double[] p, double[] q) {
        verifyDivergenceArgs(p,q);
        return (klDivergence(p,q) + klDivergence(q,p)) / 2.0;
    }

    /**
     * Return the Jenson-Shannon divergence between the specified
     * multinomial distributions.  The JS divergence is defined by
     *
     * <blockquote><pre>
     * D<sub><sub>JS</sub></sub>(p,q)
     * = ( D<sub><sub>KL</sub></sub>(p,m) + D<sub><sub>KL</sub></sub>(q,m) ) / 2</pre></blockquote>
     *

     * where <code>m</code> is defined as the balanced linear
     * interpolation (that is, the average) of <code>p</code> and
     * <code>q</code>:
     *
     * <pre><blockquote>
     * m[i] = (p[i] + q[i]) / 2</pre></blockquote>
     *
     * The JS divergence is non-zero, equal to zero only if <code>p</code>
     * and <code>q</code> are the same distribution, and symmetric.
     *
     * @param p First multinomial.
     * @param q Second multinomial.
     * @return The JS divergence between the multinomials.
     * @throws IllegalArgumentException If the distributions are not
     * the same length or have entries less than zero or greater than
     * 1.
     */
    public static double jsDivergence(double[] p, double[] q) {
        verifyDivergenceArgs(p,q);
        double[] m = new double[p.length];
        for (int i = 0; i < p.length; ++i)
            m[i] = (p[i] + q[i])/2.0;
        return (klDivergence(p,m) + klDivergence(q,m)) / 2.0;
    }


    /**
     * Returns a permutation of the integers between 0 and
     * the specified length minus one.
     *
     * @param length Size of permutation to return.
     * @return Permutation of the specified length.
     */
    public static int[] permutation(int length) {
        return permutation(length,new Random());
    }

    /**
     * Returns a permutation of the integers between 0 and
     * the specified length minus one using the specified
     * randomizer.
     *
     * @param length Size of permutation to return.
     * @param random Randomizer to use for permutation.
     * @return Permutation of the specified length.
     */
    public static int[] permutation(int length, Random random) {
        int[] xs = new int[length];
        for (int i = 0; i < xs.length; ++i)
            xs[i] = i;
        for (int i = xs.length; --i > 0; ) {
            int pos = random.nextInt(i);
            int temp = xs[pos];
            xs[pos] = xs[i];
            xs[i] = temp;
        }
        return xs;
    }

    /**
     * Returns Pearson's C<sub><sub>2</sub></sub> goodness-of-fit
     * statistic for independence over the specified binary
     * contingency matrix.  Asymptotically, this statistic has a
     * &chi;<sup>2</sup> distribution with one degree of freedom.  The
     * higher the value, the <i>less</i> likely the two outcomes are
     * independent.  Note that this method is just a special case of
     * the general chi-squared independence test:
     *
     * <pre>
     * chiSquaredIndependence(both,oneOnly,twoOnly,neither)
     * = chiSquaredIndependence(new double[][] { {both, oneOnly},
     *                                           {twoOnly, neither} });
     * </pre>
     *
     * The specified values make up the following contingency matrix
     * for boolean outcomes labeled <code>One</code> and
     * <code>Two</code>:
     *
     * <blockquote>
     * <table border='1' cellpadding='5'>
     * <tr><td>&nbsp;</td><td>+Two</td><td>-Two</td></tr>
     * <tr><td>+One</td><td><code>both</code></td><td><code>oneOnly</code></td></tr>
     * <tr><td>-One</td><td><code>twoOnly</code></td><td><code>neither</code></td></tr>
     * </table>
     * </blockquote>
     *
     * <P>The value <code>both</code> is the count of events where both
     * outcomes occurred, <code>oneOnly</code> for where only the
     * first outcome occurred, <code>twoOnly</code> for where only
     * the second outcome occurred, and <code>neither</code> for
     * when neither occurred.  Let <code>totalCount</code> be
     * the sum of all of the cells in the matrix.
     *
     * <P>From the contingency matrix, marginal probabilities for
     * the two outcomes may be computed:
     *
     * <blockquote><code>
     * P(One) = (both + oneOnly) / totalCount
     * <br>
     * P(Two) = (both + twoOnly) / totalCount
     * </code></blockquote>
     *
     * If these probabilities are independent, the expected values of
     * the matrix cells are:
     *
     * <blockquote><code>
     *  E(both)= totalCount * P(One) * P(Two)
     *  <br>
     *  E(oneOnly) = totalCount * P(One) * (1-P(Two))
     *  <br>
     *  E(twoOnly) = totalCount * (1-P(One)) * P(Two)
     *  <br>
     *  E(neither) = totalCount * (1-P(One)) * (1-P(Two))
     * </code></blockquote>
     *
     * These are used to derive the independence test statistic, which
     * is the square differences between observed and expected values
     * under the independence assumption, normalized by the expected
     * values:
     *
     * <blockquote><code>
     * C<sub><sub>2</sub></sub>
     *  = (both - E(both))<sup><sup>2</sup></sup> / E(both)
     * <br> &nbsp; &nbsp; &nbsp; &nbsp;
     * + (oneOnly - E(oneOnly))<sup><sup>2</sup></sup> / E(oneOnly)
     * <br> &nbsp; &nbsp; &nbsp; &nbsp;
     * + (twoOnly - E(twoOnly))<sup><sup>2</sup></sup> / E(twoOnly)
     * <br> &nbsp; &nbsp; &nbsp; &nbsp;
     * + (neither - E(neither))<sup><sup>2</sup></sup> / E(neither)
     * </code></blockquote>
     *
     * Unlike the higher dimensional case, this statistic applies as a
     * hypothesis test only in the case when all expected values are
     * at least 10.

     * @param both Count of samples of both outcomes.
     * @param oneOnly Count of samples with the first and not the
     * second outcome.
     * @param twoOnly Count of samples with the second and not the
     * first outcome.
     * @param neither Count of samples with with neither outcome.
     * @throws IllegalArgumentException If any of the arguments are not
     * non-negative finite numbers.
     * @return Pearson's C<sub><sub>2</sub></sub> goodness-of-fit
     * statistic for independence over the specified sample counts.
     */
    public static double chiSquaredIndependence(double both, double oneOnly,
                                                double twoOnly,
                                                double neither) {

        assertNonNegative("both",both);
        assertNonNegative("oneOnly",oneOnly);
        assertNonNegative("twoOnly",twoOnly);
        assertNonNegative("neither",neither);
        double n = both + oneOnly + twoOnly + neither;
        double p1 = (both + oneOnly) / n;
        double p2 = (both + twoOnly) / n;
        double eBoth = n * p1 * p2;
        double eOneOnly = n * p1 * (1.0 - p2);
        double eTwoOnly = n * (1.0 - p1) * p2;
        double eNeither = n * (1.0 - p1) * (1.0 - p2);
        return csTerm(both,eBoth)
            + csTerm(oneOnly,eOneOnly)
            + csTerm(twoOnly,eTwoOnly)
            + csTerm(neither,eNeither);
    }




    /**
     * Returns a two-element array of lineary regression coefficients
     * for the specified x and y values.  The coefficients returned,
     * <code>{ &beta;0, &beta;1 }</code>, define a linear function:
     *
     * <blockquote><code>
     * f(x) = &beta;1 * x + &beta;0
     * </code></blockquote>
     *
     * The coefficients returned produce the linear function <code>f(x)</code>
     * with the smallest squared error:
     *
     * <blockquote><code>
     * sqErr(f,xs,ys) =
     * <big><big>&Sigma;</big></big><sub><sub>i</sub></sub>
     * (f(xs[i]) - ys[i])<sup>2</sup>
     * </code></blockquote>
     *
     * where all sums are for <code>0 &lt;< i &lt xs.length</code>.
     *
     * The funciton requires only a single pass through the two
     * arrays, with <code>&beta;0</code> and <code>&beta;1</code>
     * given by:
     *
     * <blockquote><pre>
     * &beta;1 = n * <big><big>&Sigma;</big></big><sub>i</sub> x[i] * y[i]  -  (<big><big>&Sigma;</big></big><sub>i</sub> x[i])(<big><big>&Sigma;</big></big><sub>i</sub> y[i])
     *      ------------------------------------------
     *          n * <big><big>&Sigma;</big></big><sub>i</sub> x[i]*x[i]  -  (<big><big>&Sigma;</big></big><sub>i</sub> x[i])<sup>2</sup>
     * </pre></blockquote>
     *
     * <blockquote><pre>
     * &beta;0 = <big><big>&Sigma;</big></big><sub>i</sub> y[i] - &beta;1 <big><big>&Sigma;</big></big><sub>i</sub> x[i]
     *      ---------------------
     *              n
     * </pre></blockquote>
     *
     * where <code>n = xs.length = ys.length</code>.
     *
     * @param xs Array of x values.
     * @param ys Parallel array of y values.
     * @return Pair of regression coefficients.
     * @throws IllegalArgumentException If the arrays are of length
     * less than 2, or if the arrays are not of the same length.
     */
    public static double[] linearRegression(double[] xs, double[] ys) {
        if (xs.length != ys.length) {
            String msg = "Require parallel arrays of x and y values."
                + " Found xs.length=" + xs.length
                + " ys.length=" + ys.length;
            throw new IllegalArgumentException(msg);
        }
        if (xs.length < 2) {
            String msg = "Require arrays of length >= 2."
                + " Found xs.length=" + xs.length;
            throw new IllegalArgumentException(msg);
        }
        double n = xs.length;
        double xSum = 0.0;
        double ySum = 0.0;
        double xySum = 0.0;
        double xxSum = 0.0;
        for (int i = 0; i < xs.length; ++i) {
            double x = xs[i];
            double y = ys[i];
            xSum += x;
            ySum += y;
            xxSum += x * x;
            xySum += x * y;
        }
        double denominator = n * xxSum - xSum * xSum;
        if (denominator == 0.0) {
            String msg = "Ill formed input. Denominator for beta1 is zero."
                + " Most likely cause is fewer than 2 distinct inputs.";
            throw new IllegalArgumentException(msg);
        }
        double beta1 = (n * xySum - xSum * ySum) / denominator;

        double beta0 = (ySum - beta1 * xSum) / n;
        return new double[] { beta0, beta1 };
    }

    /**
     * Returns a two-element array of logistic regression coefficients
     * for the specified x and y values.  The coefficients returned,
     * <code>{ &beta;0, &beta;1 }</code>, define the logistic function:
     *
     * <blockquote><pre>
     *                L
     * f(x) =  ---------------
     *         1 + <i>e</i> <sup><sup>&beta;1 * x + &beta;0</sup></sup>
     * </pre></blockquote>
     *
     * with the minimum squared error.  See {@link
     * #linearRegression(double[],double[])} for a definition of
     * squared error.  This function takes real values in the the open
     * interval <code>(0, L)</code>.
     *
     * <p>Logistic regression coefficients are computed using
     * linear regression, after transforming the y values.  This
     * is possible because of the following linear relation:
     *
     * <blockquote><pre>
     * log ((L - y) / y) = &beta;1 * x + &beta;0
     * </pre></blockquote>
     *
     * @param xs Array of x values.
     * @param ys Array of y values.
     * @param maxValue Maximum value of function.
     * @return Binary array of logistic regression coordinates.
     * @throws IllegalArgumentException If the maximum value is not
     * positive and finite, if the arrays are of length less than 2,
     * or if the arrays are not of the same length.
     */
    public static double[] logisticRegression(double[] xs, double[] ys,
                                              double maxValue) {
        if (maxValue <= 0.0 || Double.isInfinite(maxValue) || Double.isNaN(maxValue)) {
            String msg = "Require finite max value > 0."
                + " Found maxValue=" + maxValue;
            throw new IllegalArgumentException(msg);
        }
        double[] logisticYs = new double[ys.length];
        for (int i = 0; i < ys.length; ++i)
            logisticYs[i] = java.lang.Math.log((maxValue - ys[i]) / ys[i]);
        return linearRegression(xs,logisticYs);
    }

    /**
     * Returns the value of Pearson's C<sub><sub>2</sub></sub>
     * goodness of fit statistic for independence over the specified
     * contingency matrix.  Asymptotically, this statistic has a
     * &chi;<sup>2</sup> distribution with
     * <code>(numRows-1)*(numCols-1)</code> degrees of freedom.  The
     * higher the value, the <i>less</i> likely the two outcomes are
     * independent.
     *
     * Pearson's C<sub><sub>2</sub></sub> statistic is defined as follows:
     *
     * <blockquote><code>
     * C<sub><sub>2</sub></sub>
     * = <big><big><big>&Sigma;</big></big></big><sub><sub>i</sub></sub>
     *   <big><big><big>&Sigma;</big></big></big><sub><sub>j</sub></sub>
     *   (matrix[i][j] - expected(i,j,matrix))<sup>2</sup> / expectedCount(i,j,matrix)
     * </code></blockquote>
     *
     * where the expected count is the total count times the max
     * likelihood estimates of row <code>i</code> probability times
     * column <code>j</code> probability:
     *
     * <blockquote><code>
     *  expectedCount(i,j,matrix)
     *  <br>
     *  = totalCount(matrix)
     *  <br> &nbsp; &nbsp;
     *       * rowCount(i,matrix)/totalCount(matrix)
     *  <br> &nbsp; &nbsp;
     *       * colCount(j,matrix)/totalCount(matrix)
     * <br>
     * = rowCount(i,matrix) * colCount(j,matrix) / totalCount(matrix)
     * </code></blockquote>
     *
     * where
     *
     * <blockquote><code>
     * rowCount(i,matrix)
     * = <big><big>&Sigma;</big></big><sub><sub>0&lt;=j&lt;=numCols</sub></sub>
     *   matrix[i][j]
     *
     * <br>
     * colCount(j,matrix)
     * = <big><big>&Sigma;</big></big><sub><sub>0&lt;=i&lt;=numRows</sub></sub>
     *   matrix[i][j]
     * <br>
     * totalCount(matrix)
     * = <big><big>&Sigma;</big></big><sub><sub>0&lt;=i&lt;=numRows</sub></sub>
     * = <big><big>&Sigma;</big></big><sub><sub>0&lt;=j&lt;=numCols</sub></sub>
     *   matrix[i][j]
     * </code></blockquote>
     *
     * <P>The &chi;<sup>2</sup> test is a large sample test and is only
     * valid if all of the expected counts are at least 5.  This restriction
     * is often ignored for ranking purposes.
     *
     * @param contingencyMatrix The specified contingency matrix.
     * @return Pearson's C<sub><sub>2</sub></sub> statistic for the independence
     * testing over the contingency matrix.
     * @throws Illegal argument exception if the matrix is not rectangular
     * or if all values are not non-negative finite numbers.
     */
    public static double chiSquaredIndependence(double[][] contingencyMatrix) {
        int numRows = contingencyMatrix.length;
        if (numRows < 2) {
            String msg = "Require at least two rows."
                + " Found numRows=" + numRows;
            throw new IllegalArgumentException(msg);
        }
        int numCols = contingencyMatrix[0].length;
        if (numCols < 2) {
            String msg = "Require at least two cols."
                + " Found numCols=" + numCols;
            throw new IllegalArgumentException(msg);
        }
        double[] rowSums = new double[numRows];
        Arrays.fill(rowSums,0.0);
        double[] colSums = new double[numCols];
        Arrays.fill(colSums,0.0);
        double totalCount = 0.0;
        for (int i = 0; i < numRows; ++i) {
            if (contingencyMatrix[i].length != numCols) {
                String msg = "Matrix must be rectangular."
                    + "Row 0 length=" + numCols
                    + "Row " + i + " length=" + contingencyMatrix[i].length;
                throw new IllegalArgumentException(msg);
            }
            for (int j = 0; j < numCols; ++j) {
                double val = contingencyMatrix[i][j];
                if (Double.isInfinite(val) || val < 0.0
                    || Double.isNaN(val)) {
                    String msg = "Values must be finite non-negative."
                        + " Found matrix[" + i + "][" + j + "]="
                        + val;
                    throw new IllegalArgumentException(msg);
                }
                rowSums[i] += val;
                colSums[j] += val;
                totalCount += val;
            }
        }
        double result = 0.0;
        for (int i = 0; i < numRows; ++i)
            for (int j = 0; j < numCols; ++j)
                result += csTerm(contingencyMatrix[i][j],
                                 rowSums[i] * colSums[j] / totalCount);
        return result;
    }

    /**
     * Return an array of probabilities resulting from normalizing the
     * specified probability ratios.  The resulting array of
     * probabilities is the same length as the input ratio array and
     * each probability is simply the input array's value divided by
     * the sum of the ratios.
     *
     * <P><b>Warning:</b> This method is implemented by summing the
     * probability ratios and then dividing each element by the sum.
     * Because of the limited precision of <code>double</code>-based
     * arithmetic, if the largest ratio is much larger than the next
     * largest ratio, then the largest normalized probability will be
     * one and all others will be zero.  Java double values follow the
     * IEEE 754 arithmetic standard and thus use 52 bits for their
     * mantissas.  Thus only ratios within
     * <code>2<sup><sup>52</sup></sup>~10<sup><sup>16</sup></sup> of
     * the maximum ratio will be non-zero.
     *
     * @param probabilityRatios Ratios of probabilities.
     * @return Probabilities resulting from normalizing the ratios.
     * @throws IllegalArgumentException If the input contains a value
     * that is not a finite non-negative number, or if the input does
     * not contain at least one non-zero entry.
     */
    public static double[] normalize(double[] probabilityRatios) {
        for (int i = 0; i < probabilityRatios.length; ++i) {
            if (probabilityRatios[i] < 0.0
                || Double.isInfinite(probabilityRatios[i])
                || Double.isNaN(probabilityRatios[i])) {
                String msg = "Probabilities must be finite non-negative."
                    + " Found probabilityRatios[" + i + "]="
                    + probabilityRatios[i];
                throw new IllegalArgumentException(msg);
            }
        }
        double sum = com.aliasi.util.Math.sum(probabilityRatios);
        if (sum <= 0.0) {
            String msg = "Ratios must sum to number greater than zero."
                + " Found sum=" + sum;
            throw new IllegalArgumentException(msg);
        }
        double[] result = new double[probabilityRatios.length];
        for (int i = 0; i < probabilityRatios.length; ++i)
            result[i] = probabilityRatios[i]/sum;
        return result;
    }

    /**
     * Returns the value of the kappa statistic for the specified
     * observed and expected probabilities.  The kappa statistic
     * provides a kind of adjustment for the exptected (random)
     * difficulty of a problem.  It is defined by:
     *
     * <blockquote><code>
     * kappa(p,e) = (p - e) / (1 - e)
     * </code></blockquote>
     *
     * <P>The most typical use for kappa is in evaluating
     * classification problems of a machine versus a gold standard or
     * between two human annotators to assess inter-annotator
     * agreement.
     *
     * @param observedProb Observed probability.
     * @param expectedProb Expected probability.
     * @return The value of the kappa statistic for the specified
     * probability and expected probability.
     */
    public static double kappa(double observedProb, double expectedProb) {
        return (observedProb - expectedProb)/(1 - expectedProb);
    }

    /**
     * Returns the mean of the specified array of input values.  The mean
     * of an array is defined by:
     *
     * <blockquote><code>
     *   mean(xs)
     *   = <big><big>&Sigma;</big></big><sub><sub>i < xs.length</sub></sub>
     *     xs[i] / xs.length
     * </code></blockquote>
     *
     * If the array is of length zero, the result is {@link Double#NaN}.
     *
     * @param xs Array of values.
     * @return Mean of array of values.
     */
    public static double mean(double[] xs) {
        return com.aliasi.util.Math.sum(xs) / (double) xs.length;
    }


    /**
     * Returns the variance of the specified array of input values.
     * The variance of an array of values is the mean of the
     * squared differences between the values and the mean:
     *
     * <blockquote><code>
     *  variance(xs)
     *   = <big><big>&Sigma;</big></big><sub><sub>i < xs.length</sub></sub>
     *     (mean(xs) - xs[i])<sup><sup>2</sup></sup> / xs.length
     * </code></blockquote>
     *
     * If the array is of length zero, the result is {@link Double#NaN}.
     *
     * @param xs Array of values.
     * @return Variance of array of values.
     */
    public static double variance(double[] xs) {
        return variance(xs,mean(xs));
    }


    /**
     * Returns the standard deviation of the specified array of input
     * values. The standard deviation is just the square root of the
     * variance:
     *
     * <blockquote><code>
     * standardDeviation(xs) = variance(xs)<sup><sup>(1/2)</sup></sup>
     * </code></blockquote>
     *
     * If the array is of length zero, the result is {@link Double#NaN}.
     *
     * @param xs Array of values.
     * @return Standard deviation of array of values.
     */
    public static double standardDeviation(double[] xs) {
        return java.lang.Math.sqrt(variance(xs));
    }

    /**
     * Returns (Pearson's) correlation coefficient between two
     * specified arrays.  The correlation coefficient, traditionally
     * notated as <code><i>r</i><sup>2</sup></code>,  measures the
     * square error between a best linear fit between the two arrays
     * of data.  Rescaling either array by a positive constant will
     * not affect the result.
     *
     * <P>The square root <code><i>r</i></code> of the correlation
     * coefficient <code><i>r</i><sup>2</sup></code> is the variance
     * in the second array explained by a linear relation with the
     * the first array.
     *
     * <P>The definition of the correlation coefficient is:
     *
     * <blockquote><code>
     *   correlation(xs,ys)<sup><sup>2</sup></sup>
     *   <br>= sumSqDiff(xs,ys)<sup><sup>2</sup></sup>
     *   <br>&nbsp; / (sumSqDiff(xs,xs) * sumSqDiff(xs,xs))
     * </code></blockquote>
     *
     * where
     *
     * <blockquote><code>
     * sumSqDiff(xs,ys)
     * <br>= <big><big>&Sigma;</big></big><sub><sub>i&lt;xs.length</sub></sub>
     *       (xs[i] - mean(xs)) * (ys[i] - mean(ys))
     * </code></blockquote>
     *
     * and thus the terms in the denominator above reduce using:
     *
     * <blockquote><code>
     * sumSqDiffs(xs,xs)
     * <br>= <big><big>&Sigma;</big></big><sub><sub>i&lt;xs.length</sub></sub>
     *       (xs[i] - mean(xs))<sup><sup>2</sup></sup>
     * </code></blockquote>
     *
     * <P>See the following for more details:
     *
     * <UL>
     *
     * <LI>Eric W. Weisstein.  "Correlation Coefficient." From
     * MathWorld--A Wolfram Web Resource.  <a
     * href="http://mathworld.wolfram.com/CorrelationCoefficient.html"
     * >http://mathworld.wolfram.com/CorrelationCoefficient.html</a>
     *
     * <LI>Wikipedia. <a
     * href="http://en.wikipedia.org/wiki/Pearson_product-moment_correlation_coefficient">Pearson
     * product-moment correlation coefficient</a>.
     *
     * </UL>
     *
     * @param xs First array of values.
     * @param ys Second array of values.
     * @return The correlation coefficient of the two arrays.
     * @throws IllegalArgumentException If the arrays are not the same
     * length.
     */
    public static double correlation(double[] xs, double[] ys) {
        if (xs.length != ys.length) {
            String msg = "xs and ys must be the same length."
                + " Found xs.length=" + xs.length
                + " ys.length=" + ys.length;
            throw new IllegalArgumentException(msg);
        }
        double meanXs = mean(xs);
        double meanYs = mean(ys);
        double ssXX = sumSquareDiffs(xs,meanXs);
        double ssYY = sumSquareDiffs(ys,meanYs);
        double ssXY = sumSquareDiffs(xs,ys,meanXs,meanYs);

        return java.lang.Math.sqrt((ssXY*ssXY) / (ssXX * ssYY));
    }


    /**
     * Returns a sample from the discrete distribution represented by the
     * specified cumulative probability ratios, using the specified random
     * number generator.
     *
     * <p>The cumulative probability ratios represent unnormalized probabilities
     * of generating the value of their index or less, that is, unnormalized
     * cumulative probabilities.  For instance, consider
     * the cumulative probability ratios <code>{ 0.5, 0.5, 3.9, 10.1}</code>:
     *
     * <blockquote><table border='1' cellpadding="5">
     * <tr><th>Outcome</th><th>Value</th><th>Unnormalized Prob</th><th>Prob</th></tr>
     * <tr><td>0</td> <td>0.5</td>  <td>0.5</td> <td>0.5/10.1</td></tr>
     * <tr><td>1</td> <td>0.5</td>  <td>0.0</td> <td>0.0/10.1</td></tr>
     * <tr><td>2</td> <td>3.9</td>  <td>3.4</td> <td>3.4/10.1</td></tr>
     * <tr><td>3</td> <td>10.1</td> <td>6.2</td> <td>6.2/10.1</td></tr>
     * </table></blockquote>
     *
     * A sample is taken by generating a random number x between 0.0 and
     * the value of the last element (here 10.1).  The value returned is
     * the index i such that:
     *
     * <blockquote><pre>
     * cumulativeProbRatios[i-1] < x <= cumulativeProbRatios[i]</pre></blockquote>
     *
     * The corresponding probabilities given the sample input are
     * listed in the last column in the table above.
     *
     * <p>Note that if two
     * elements have the same value, there is no chance of generating
     * the outcome with the higher index.  In the example above, this
     * corresponds to outcome 1 having probaiblity 0.0
     *
     * <p><b>Warning</b>: The cumulative probability ratios are required to meet
     * two conditions.  First, all values must be finite and non-negative.  Second,
     * the values must be non-decreasing, so that
     * <code>cumulativeProbRatios[i] <= cumulativeProbRatios[i+1]</code>.
     * If either of these conditions is not met, the result is undefined.
     *
     * @param cumulativeProbRatios Cumulative probability for outcome less than
     * or equal to index.
     * @param random Random number generator for sampling.
     * @return A sample from the specified distribution.
     */
    public static int sample(double[] cumulativeProbRatios, Random random) {
        int low = 0;
        int high = cumulativeProbRatios.length - 1;
        double x = random.nextDouble() * cumulativeProbRatios[high];
        while (low < high) {
            int mid = (high + low)/2;
            if (x > cumulativeProbRatios[mid]) {
                low = mid+1;
            } else if (high == mid) {
                return (x > cumulativeProbRatios[low]) ? mid : low;
            } else {
                high = mid;
            }
        }
        return low;
    }

    /**
     * Returns the log (base 2) of the probability of the specified
     * discrete distribution given the specified uniform Dirichlet
     * with concentration parameters equal to the specified value.
     *
     * <p>See {@link #dirichletLog2Prob(double[],double[])} for
     * more information on the Dirichlet distribution.  This method
     * returns a result equivalent to the following (though is more
     * efficiently implemented):
     *
     * <blockquote><pre>
     * double[] alphas = new double[xs.length];
     * java.util.Arrays.fill(alphas,alpha);
     * assert(dirichletLog2Prob(alpha,xs) == dirichletLog2Prob(alphas,xs));</pre></blockquote>
     *
     * <p>For the uniform Dirichlet, the distribution simplifies to
     * the following form:
     *
     * <blockquote><pre>
     * p(xs | Dirichlet(&alpha;)) = (1/Z(&alpha;)) <big><big><big>&Pi;</big></big></big><sub><sub>i &lt; k</sub></sub> xs[i]<sup>&alpha;-1</sup></pre></blockquote>
     *
     * where
     *
     * <blockquote><pre>
     * Z(&alpha;) = &Gamma;(&alpha;)<sup>k</sup> / &Gamma;(k * &alpha;)</pre></blockquote>
     *
     * <p><i>Warning:</i> The probability distribution must be proper
     * in the sense of having values between 0 and 1 inclusive and
     * summing to 1.0.  This property is not checked by this method.
     *
     * @param alpha Dirichlet concentration parameter to use for each
     * dimension.
     * @param xs The distribution whose probability is returned.
     * @return The log (base 2) probability of the specified
     * distribution in the uniform Dirichlet distribution with concentration
     * parameters equal to <code>alpha</code>.
     * @throws IllegalArgumentException If the values in the distribution
     * are not between 0 and 1 inclusive or if the concentration parameter is
     * not positive and finite.
     */
    public static double dirichletLog2Prob(double alpha, double[] xs) {
        verifyAlpha(alpha);
        verifyDistro(xs);
        int k = xs.length;
        // normalizing term
        double result = com.aliasi.util.Math.log2Gamma(k * alpha)
            - k * com.aliasi.util.Math.log2Gamma(alpha);
        double alphaMinus1 = alpha - 1;
        for (int i = 0; i < k; ++i)
            result += alphaMinus1 * com.aliasi.util.Math.log2(xs[i]);
        return result;
    }


    /**
     * Returns the log (base 2) of the probability of the specified
     * discrete distribution given the specified Dirichlet
     * distribution concentration parameters.  A Dirichlet
     * distribution is a distribution over <code>k</code>-dimensional
     * discrete distributions.
     *
     * <p>The Dirichlet is widely used because it is the conjugate
     * prior for multinomials in a Bayesian setting, and thus may
     * be used to specify a convenient distribution over discrete
     * distributions.</p>
     *
     * <p>A Dirichlet distribution is specified by a dimensionality
     * <code>k</code> and a concentration parameter <code>&alpha;[i] &gt; 0</code>
     * for each <code>i &lt; k</code>.
     * The probability of the distribution <code>xs</code>
     * in a Dirichlet distribution of dimension <code>k</code>
     * and concentration parameters <code>&alpha;</code> is
     * given (up to a normalizing constant) by:
     *
     * <blockquote><pre>
     * p(xs | Dirichlet(&alpha;))
     * <big>&#8733;</big> <big><big><big>&Pi;</big></big></big><sub><sub>i &lt; k</sub></sub> xs[i]<sup>&alpha;[i]-1</sup></pre></blockquote>
     *
     * The full distribution is:
     *
     * <blockquote><pre>
     * p(xs | Dirichlet(&alpha;))
     * = (1/Z(&alpha;)) * <big><big><big>&Pi;</big></big></big><sub><sub>i &lt; k</sub></sub> xs[i]<sup>&alpha;[i]-1</sup></pre></blockquote>
     *
     * where the normalizing constant is given by:
     *
     * <blockquote><pre>
     * Z(&alpha;) = <big><big>&Pi;</big></big><sub><sub>i &lt; k</sub></sub> &Gamma;(&alpha;[i])
     *        / &Gamma;(<big><big>&Sigma;</big></big><sub><sub>i &lt; k</sub></sub> &alpha;[i])</pre></blockquote>
     *
     * <p><i>Warning:</i> The probability distribution must be proper
     * in the sense of having values between 0 and 1 inclusive and
     * summing to 1.0.  This property is not checked by this method.
     *
     * @param alphas The concentration parameters for the uniform Dirichlet.
     * @param xs The outcome distribution
     * @return The probability of the outcome distribution given the
     * Dirichlet concentratioin parameter.
     * @throws IllegalArgumentException If the Dirichlet parameters and
     * distribution are not arrays of the same length or if the distribution
     * parameters in xs are not between 0 and 1 inclusive, or if any of
     * the concentration parameters is not positive and finite.
     */
    public static double dirichletLog2Prob(double[] alphas, double[] xs) {
        if (alphas.length != xs.length) {
            String msg = "Dirichlet prior alphas and distribution xs must be the same length."
                + " Found alphas.length=" + alphas.length
                + " xs.length=" + xs.length;
            throw new IllegalArgumentException(msg);
        }
        for (int i = 0; i < alphas.length; ++i)
            verifyAlpha(alphas[i]);
        verifyDistro(xs);
        int k = xs.length;
        double result = 0.0;
        double alphaSum = 0.0;
        for (int i = 0; i < alphas.length; ++i) {
            alphaSum += alphas[i];
            result -= com.aliasi.util.Math.log2Gamma(alphas[i]);
        }
        result += com.aliasi.util.Math.log2Gamma(alphaSum);
        for (int i = 0; i < k; ++i)
            result += (alphas[i] - 1) * com.aliasi.util.Math.log2(xs[i]);
        return result;
    }


    static void verifyAlpha(double alpha) {
        if (Double.isNaN(alpha) || Double.isInfinite(alpha) || alpha <= 0.0) {
            String msg = "Concentration parameter must be positive and finite."
                + " Found alpha=" + alpha;
            throw new IllegalArgumentException(msg);
        }
    }

    static void verifyDistro(double[] xs) {
        for (int i = 0; i < xs.length; ++i) {
            if (xs[i] < 0.0 || xs[i] > 1.0 || Double.isNaN(xs[i]) || Double.isInfinite(xs[i])) {
                String msg = "All xs must be betwee 0.0 and 1.0 inclusive."
                    + " Found xs[" + i + "]=" + xs[i];
                throw new IllegalArgumentException(msg);
            }
        }
    }



    // = sumSquareDiffs(xs,mean,xs,mean);
    static double sumSquareDiffs(double[] xs, double mean) {
        double sum = 0.0;
        for (int i = 0; i < xs.length; ++i) {
            double diff = xs[i] - mean;
            sum += diff * diff;
        }
        return sum;
    }

    static double sumSquareDiffs(double[] xs, double[] ys, double meanXs, double meanYs) {
        double sum = 0.0;
        for (int i = 0; i < xs.length; ++i)
            sum += (xs[i] - meanXs) * (ys[i] - meanYs);
        return sum;
    }

    static double variance(double[] xs, double mean) {
        return sumSquareDiffs(xs,mean) / (double) xs.length;
    }




    static void assertNonNegative(String variableName, double value) {
        if (Double.isInfinite(value) || Double.isNaN(value) || value < 0.0) {
            String msg = "Require finite non-negative value."
                + " Found " + variableName + " =" + value;
            throw new IllegalArgumentException(msg);
        }
    }

    private static double csTerm(double found, double expected) {
        double diff = found - expected;
        return diff * diff / expected;
    }

}
