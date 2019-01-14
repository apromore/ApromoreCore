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

package com.aliasi.classify;

import com.aliasi.stats.Statistics;

/**
 * A <code>PrecisionRecallEvaluation</code> collects and reports a
 * suite of descriptive statistics for binary classification tasks.
 * The basis of a precision recall evaluation is a matrix of counts
 * of reference and response classifications.  Each cell in the matrix
 * corresponds to a method returning a long integer count.
 *
 * <blockquote>
 * <font size='-1'>
 * <table border='1' cellpadding='10'>
 * <tr><td colspan='2' rowspan='2' bordercolor='white'>&nbsp;</td>
 *     <td colspan='2' align='center'><b><i>Response</i></b></td>
 *     <td rowspan='2' align='center' valign='bottom'><i>Reference Totals</i></td>
 * </tr>
 * <tr>
 *     <td align='center'><i>true</i></td>
 *     <td align='center'><i>false</i></td></tr>
 * <tr><td rowspan='2'><i><b>Refer<br>-ence</b></i></td><td align='right'><i>true</i></td>
 *     <td>{@link #truePositive()} (TP)</td><td>{@link #falseNegative} (FN)</td>
 *     <td>{@link #positiveReference} (TP+FN)</td>
 * </tr>
 * <tr><td align='right'><i>false</i></td>
 *     <td>{@link #falsePositive()} (FP)</td><td>{@link #trueNegative()} (TN)</td>
 *     <td>{@link #negativeReference()} (FP+TN)</td>
 * </tr>
 * <tr><td colspan='2' align='right'><i>Response Totals</td><td>{@link #positiveResponse()} (TP+FP)</td>
 *     <td>{@link #negativeResponse()} (FN+TN)</td>
 *     <td>{@link #total()} (TP+FN+FP+TN)</td>
 * </tr>
 * </table>
 * </font>
 * </blockquote>
 *
 * The most basic statistic is accuracy, which is the number of
 * correct responses divided by the total number of cases.
 * 
 * <blockquote><code>
 * <b>accuracy</b>()</code>
 * = correct() / total()
 * </code></blockquote>
 * 
 * This class derives its name from the following four statistics,
 * which are illustrated in the four tables.  
 * 
 * <blockquote><code>
 * <b>recall</b>()
 * = truePositive() / positiveReference()
 * </code></blockquote>
 * 
 * <blockquote><code>
 * <b>precision</b>()
 *  = truePositive() / positiveResponse()
 * </code></blockquote>
 * 
 * <blockquote><code>
 * <b>rejectionRecall</b>()
 * = trueNegative() / negativeReference()
 * </code></blockquote>
 * 
 * <blockquote><code>
 * <b>rejectionPrecision</b>()
 *  = trueNegative() / negativeResponse()
 * </code></blockquote>
 * 
 * Each measure is defined to be the green count divided by the green
 * plus red count in the corresponding table:
 *
 * <blockquote>
 *
 * <table border='0' cellpadding='10'>
 *
 * <tr><td>
 *
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='2' rowspan='2' bordercolor='white' valign='top'>
 *        <b>Recall</b>
 *     </td>
 *     <td colspan='3' align='center'><i>Response</i></td></tr>
 * <tr>
 *     <td>True</td>
 *     <td>False</td></tr>
 * <tr><td rowspan='3'><i>Refer<br>-ence</i></td><td>True</td>
 *     <td bgcolor='green'><b><big>+</big></b></td><td bgcolor='red'><b><big>-</big></b></td></tr>
 * <tr><td>False</td>
 *     <td>&nbsp;</td><td>&nbsp;</td></tr>
 * </table>
 *
 * </td><td>
 *
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='2' rowspan='2' bordercolor='white' valign='top'>
 *        <b>Precision</b>
 *     </td>
 *     <td colspan='3' align='center'><i>Response</i></td></tr>
 * <tr>
 *     <td>True</td>
 *     <td>False</td></tr>
 * <tr><td rowspan='3'><i>Refer<br>-ence</i></td><td>True</td>
 *     <td bgcolor='green'><b><big>+</big></b></td><td>&nbsp;</td></tr>
 * <tr><td>False</td>
 *     <td bgcolor='red'><b><big>-</big></b></td><td>&nbsp;</td></tr>
 * </table>
 *
 * </td></tr>
 * <tr><td>
 *
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='2' rowspan='2' bordercolor='white' valign='top'>
 *        <b>Rejection <br>Recall</b>
 *     </td>
 *     <td colspan='3' align='center'><i>Response</i></td></tr>
 * <tr>
 *     <td>True</td>
 *     <td>False</td></tr>
 * <tr><td rowspan='3'><i>Refer<br>-ence</i></td><td>True</td>
 *     <td>&nbsp;</td><td>&nbsp;</td></tr>
 * <tr><td>False</td>
 *     <td bgcolor='red'><b><big>-</big></b></td><td bgcolor='green'><b><big>+</big></b></td></tr>
 * </table>
 *
 * </td><td>
 *
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='2' rowspan='2' bordercolor='white' valign='top'>
 *        <b>Rejection <br>Precision</b>
 *     </td>
 *     <td colspan='3' align='center'><i>Response</i></td></tr>
 * <tr>
 *     <td>True</td>
 *     <td>False</td></tr>
 * <tr><td rowspan='3'><i>Refer<br>-ence</i></td><td>True</td>
 *     <td>&nbsp;</td><td bgcolor='red'><b><big>-</big></b></td></tr>
 * <tr><td>False</td>
 *     <td>&nbsp;</td><td bgcolor='green'><b><big>+</big></b></td></tr>
 * </table>
 *
 * </td></tr></table>
 * </blockquote>
 *
 * This picture clearly illustrates the relevant
 * dualities.  Precision is the dual to recall if the reference and
 * response are switched (the matrix is transposed).  Similarly,
 * rejection recall is dual to recall with true and false labels
 * switched (reflection around each axis in turn); rejection precision is
 * similarly dual to precision.
 * 
 * <P>Precision and recall may be combined by weighted geometric
 * averaging by using the f-measure statistic, with
 * <code>&beta;</code> between 0 and infinity being the relative
 * weight of precision, with 1 being a neutral value.

 * <blockquote><code>
 * <b>fMeasure</b>() = fMeasure(1)
 * </code></blockquote>
 * 
 * <blockquote><code>
 * <b>fMeasure</b>(&beta;)
 *  = (1 + &beta;<sup><sup>2</sup></sup>) 
 *  * {@link #precision()}
 *  * {@link #recall()}
 *  / ({@link #recall()} + &beta;<sup><sup>2</sup></sup> * {@link #precision()})
 * </code></blockquote>
 * 
 * <P>There are four traditional measures of binary classification,
 * which are as follows.
 * 
 * <blockquote><code>
 * <b>fowlkesMallows</b>()
 * = truePositive() / (precision() * recall())<sup><sup>(1/2)</sup></sup>
 * </code></blockquote>
 *
 * <blockquote><code>
 * <b>jaccardCoefficient</b>()
 * = truePositive() / (total() - trueNegative())
 * </code></blockquote>
 *
 * <blockquote><code>
 * <b>yulesQ</b>()
 * = (truePositive() * trueNegative() - falsePositive() * falseNegative())
 * / (truePositive() * trueNegative() + falsePositive() * falsePositive())
 * </code></blockquote>
 * <blockquote><code>
 * <b>yulesY</b>()
 * = ((truePositive() * trueNegative())<sup><sup>(1/2)</sup></sup>
 *    - (falsePositive() * falseNegative())<sup><sup>(1/2)</sup></sup>)
 * <br>/ ((truePositive() * trueNegative())<sup><sup>(1/2)</sup></sup> + (falsePositive() * falsePositive())<sup><sup>(1/2)</sup></sup>)
 * </code></blockquote>
 *
 * <P>Replacing precision and recall with their definitions,
 * <code>TP/(TP+FP)</code> and <code>TP/(TP+FN)</code>:
 *
 * <font size='-1'>
 * <pre>
 *      F<sub><sub>1</sub></sub>
 *      = 2 * (TP/(TP+FP)) * (TP/(TP+FN)) 
 *        / (TP/(TP+FP) + TP/(TP+FN))     
 *      = 2 * (TP*TP / (TP+FP)(TP+FN))
 *        / (TP*(TP+FN)/(TP+FP)(TP+FN) + TP*(TP+FP)/(TP+FN)(TP+FP))
 *      = 2 * (TP / (TP+FP)(TP+FN))
 *        / ((TP+FN)/(TP+FP)(TP+FN) + (TP+FP)/(TP+FN)(TP+FP))
 *      = 2 * TP / 
 *        / ((TP+FN) + (TP+FP))
 *      = 2*TP / (2*TP + FP + FN)</pre></font>
 *
 * Thus the F<sub><sub>1</sub></sub>-measure is very closely related to the Jaccard
 * coefficient, <code>TP/(TP+FP+FN)</code>.  Like the Jaccard
 * coefficient, the F measure does not vary with varying true
 * negative counts.  Rejection precision and recall do vary with
 * changes in true negative count.
 *
 * <P>Basic reference and response likelihoods are computed by
 * frequency.
 *
 * <blockquote><code>
 * <b>referenceLikelihood</b>() = positiveReference() / total()
 * </code></blockquote>
 *
 * <blockquote><code>
 * <b>responseLikelihood</b>() = positiveResponse() / total()
 * </code></blockquote>
 *
 * An algorithm that chose responses at random according to the
 * response likelihood would have the following accuracy against
 * test cases chosen at random according to the reference likelihood:
 * 
 * <blockquote><code>
 * <b>randomAccuracy</b>()
 * = referenceLikelihood() * responseLikelihood()
 * + (1 - referenceLikelihood()) * (1 - responseLikelihood())
 * </code></blockquote>
 *
 * The two summands arise from the likelihood of true positive and the
 * likelihood of a true negative.  From random accuracy, the
 * &kappa;-statistic is defined by dividing out the random accuracy
 * from the accuracy, in some way giving a measure of performance
 * above a baseline expectation.
 * 
 * <blockquote><code>
 * <b>kappa</b>()
 * = <i>kappa</i>(accuracy(),randomAccuracy())
 * </code></blockquote>
 *
 * <blockquote><code>
 * <i><b>kappa</b></i>(p,e)
 * = (p - e) / (1 - e)
 * </code></blockquote>
 * 
 * <P>There are two alternative forms of the &kappa;-statistic, both
 * of which attempt to correct for putative bias in the estimation of
 * random accuracy.  The first involves computing the random accuracy
 * by taking the average of the reference and response likelihoods to
 * be the baseline reference and response likelihood, and squaring the
 * result to get the so-called unbiased random accuracy and the
 * unbiased &kappa;-statistic:

 * <blockquote><code>
 * <b>randomAccuracyUnbiased</b>()
 * = avgLikelihood()<sup><sup>2</sup></sup>
 * + (1 - avgLikelihood())<sup><sup>2</sup></sup>
 * <br>
 * avgLikelihood() = (referenceLikelihood() + responseLikelihood()) / 2
 * </code></blockquote>
 *
 * <blockquote><code>
 * <b>kappaUnbiased</b>()
 * = <i>kappa</i>(accuracy(),randomAccuracyUnbiased())
 * </code></blockquote>
 *
 * <P>Kappa can also be adjusted for the prevalence of positive
 * reference cases, which leads to the following simple definition:
 * 
 * <blockquote><code>
 * <b>kappaNoPrevalence</b>()
 * = (2 * accuracy()) - 1
 * </code></blockquote>
 *
 *<P>Pearson's C<sup><sup>2</sup></sup> statistic is provided by
 * the following method:
 * 
 * <blockquote><code>
 * <b>chiSquared</b>() 
 * = total() * phiSquared()
 * </code></blockquote>
 * 
 * <blockquote><code>
 * <b>phiSquared</b>()
 * = ((truePositive()*trueNegative()) * (falsePositive()*falseNegative()))<sup><sup>2</sup></sup>
 * <br>/ ((truePositive()+falseNegative()) * (falsePositive()+trueNegative()) * (truePositive()+falsePositive()) * (falseNegative()+trueNegative()))
 * </code></blockquote>
 *
 * <P>The accuracy deviation is the deviation of the average number of
 * positive cases in a binomial distribution with accuracy equal to
 * the classification accuracy and number of trials equal to the total
 * number of cases.
 * 
 * <blockquote><code>
 * <b>accuracyDeviation</b>()
 * = (accuracy() * (1 - accuracy()) / total())<sup><sup>(1/2)</sup></sup>
 * </code></blockquote>
 *
 * This number can be used to provide error intervals around the 
 * accuracy results.
 *
 * <P>Using the following three tables as examples:
 *
 * <blockquote>
 * <table border='0' cellpadding='5'>
 * <tr>

 * <td>
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='4'><b>Cab-vs-All</b></td></tr>
 * <tr><td colspan='2' rowspan='2' bordercolor='white'>&nbsp;</td>
 *     <td colspan='3' align='center'><b><i>Response</i></b></td></tr>
 * <tr>
 *     <td><i>Cab</i></td>
 *     <td><i>Other</i></td></tr>
 * <tr><td rowspan='3'><i><b>Refer<br>-ence</b></i></td><td><i>Cab</i></td>
 *     <td bgcolor='#CCCCFF'>9</td><td>3</td></tr>
 * <tr><td><i>Other</i></td>
 *     <td>4</td><td bgcolor='#CCCCFF'>11</td></tr>
 * </table>
 * </td>
 *
 * <td>
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='4'><b>Syrah-vs-All</b></td></tr>
 * <tr><td colspan='2' rowspan='2' bordercolor='white'>&nbsp;</td>
 *     <td colspan='3' align='center'><b><i>Response</i></b></td></tr>
 * <tr>
 *     <td><i>Syrah</i></td>
 *     <td><i>Other</i></td></tr>
 * <tr><td rowspan='3'><i><b>Refer<br>-ence</b></i></td><td><i>Syrah</i></td>
 *     <td bgcolor='#CCCCFF'>5</td><td>4</td></tr>
 * <tr><td><i>Other</i></td>
 *     <td>4</td><td bgcolor='#CCCCFF'>14</td></tr>
 * </table>
 * </td>
 *
 * <td>
 * <table border='1' cellpadding='3'>
 * <tr><td colspan='4'><b>Pinot-vs-All</b></td></tr>
 * <tr><td colspan='2' rowspan='2' bordercolor='white'>&nbsp;</td>
 *     <td colspan='3' align='center'><b><i>Response</i></b></td></tr>
 * <tr>
 *     <td><i>Pinot</i></td>
 *     <td><i>Other</i></td></tr>
 * <tr><td rowspan='3'><i><b>Refer<br>-ence</b></i></td><td><i>Pinot</i></td>
 *     <td bgcolor='#CCCCFF'>4</td><td>2</td></tr>
 * <tr><td><i>Other</i></td>
 *     <td>1</td><td bgcolor='#CCCCFF'>20</td></tr>
 * </table>
 * </td>
 *
 * </tr>
 * </table>
 *
 * </blockquote>
 *
 * The various statistics evaluate to the following values:
 *
 * <blockquote>
 * <table border='1' cellpadding='5'>
 * <tr><td><i>Method</i></td>
 *     <td><i>Cabernet</i></td>
 *     <td><i>Syrah</i></td>
 *     <td><i>Pinot</i></td></tr>
 * <tr><td>{@link #positiveReference()}</td>
 *     <td>12</td><td>9</td><td>6</td></tr>
 * <tr><td>{@link #negativeReference()}</td>
 *     <td>15</td><td>18</td><td>21</td></tr>
 * <tr><td>{@link #positiveResponse()}</td>
 *     <td>13</td><td>9</td><td>5</td></tr>
 * <tr><td>{@link #negativeResponse()}</td>
 *     <td>14</td><td>18</td><td>22</td></tr>
 * <tr><td>{@link #correctResponse()}</td>
 *     <td>20</td><td>19</td><td>24</td></tr>
 * <tr><td>{@link #total()}</td>
 *     <td>27</td><td>27</td><td>27</td></tr>
 * <tr><td>{@link #accuracy()}</td>
 *     <td>0.7407</td><td>0.7037</td><td>0.8889</td></tr>
 * <tr><td>{@link #recall()}</td>
 *     <td>0.7500</td><td>0.5555</td><td>0.6666</td></tr>
 * <tr><td>{@link #precision()}</td>
 *     <td>0.6923</td><td>0.5555</td><td>0.8000</td></tr>
 * <tr><td>{@link #rejectionRecall()}</td>
 *     <td>0.7333</td><td>0.7778</td><td>0.9524</td></tr>
 * <tr><td>{@link #rejectionPrecision()}</td>
 *     <td>0.7858</td><td>0.7778</td><td>0.9091</td></tr>
 * <tr><td>{@link #fMeasure()}</td>
 *     <td>0.7200</td><td>0.5555</td><td>0.7272</td></tr>
 * <tr><td>{@link #fowlkesMallows()}</td>
 *     <td>12.49</td><td>9.00</td><td>5.48</td></tr>
 * <tr><td>{@link #jaccardCoefficient()}</td>
 *     <td>0.5625</td><td>0.3846</td><td>0.5714</td></tr>
 * <tr><td>{@link #yulesQ()}</td>
 *     <td>0.7838</td><td>0.6279</td><td>0.9512</td></tr>
 * <tr><td>{@link #yulesY()}</td>
 *     <td>0.4835</td><td>0.3531</td><td>0.7269</td></tr>
 * <tr><td>{@link #referenceLikelihood()}</td>
 *     <td>0.4444</td><td>0.3333</td><td>0.2222</td></tr>
 * <tr><td>{@link #responseLikelihood()}</td>
 *     <td>0.4815</td><td>0.3333</td><td>0.1852</td></tr>
 * <tr><td>{@link #randomAccuracy()}</td>
 *     <td>0.5021</td><td>0.5556</td><td>0.6749</td></tr>
 * <tr><td>{@link #kappa()}</td>
 *     <td>0.4792</td><td>0.3333</td><td>0.6583</td></tr>
 * <tr><td>{@link #randomAccuracyUnbiased()}</td>
 *     <td>0.5027</td><td>0.5556</td><td>0.6756</td></tr>
 * <tr><td>{@link #kappaUnbiased()}</td>
 *     <td>0.4789</td><td>0.3333</td><td>0.6575</td></tr>
 * <tr><td>{@link #kappaNoPrevalence()}</td>
 *     <td>0.4814</td><td>0.4074</td><td>0.7778</td></tr>
 * <tr><td>{@link #chiSquared()}</td>
 *     <td>6.2382</td><td>3.0000</td><td>11.8519</td></tr>
 * <tr><td>{@link #phiSquared()}</td>
 *     <td>0.2310</td><td>0.1111</td><td>0.4390</td></tr>
 * <tr><td>{@link #accuracyDeviation()}</td>
 *     <td>0.0843</td><td>0.0879</td><td>0.0605</td></tr>
 * </table>
 * </blockquote>
 *
 * @author Bob Carpenter
 * @version 2.1
 * @since   LingPipe2.1
 */
public class PrecisionRecallEvaluation {

    private long mTP;
    private long mFP;
    private long mTN;
    private long mFN;

    /**
     * Construct a precision-recall evaluation with all counts set to
     * zero.
     */
    public PrecisionRecallEvaluation() { 
        this(0,0,0,0);
    }

    /**
     * Construction a precision-recall evaluation initialized with the
     * specified counts.
     *
     * @param tp True positive count.
     * @param fn False negative count.
     * @param fp False positive count.
     * @param tn True negative count.
     * @throws IllegalArgumentException If any of the counts are
     * negative.
     */
    public PrecisionRecallEvaluation(long tp, long fn, long fp, long tn) {
        validateCount("tp",tp);
        validateCount("fp",fp);
        validateCount("tn",tn);
        validateCount("fn",fn);
        mTP = tp;
        mFP = fp;
        mTN = tn;
        mFN = fn;
    }

    /**
     * Adds a case with the specified reference and response
     * classifications.
     *
     * @param reference Reference classification.
     * @param response Response classification.
     */
    public void addCase(boolean reference, boolean response) {
        if (reference && response) ++mTP;
        else if (reference && (!response)) ++mFN;
        else if ((!reference) && response) ++mFP;
        else ++mTN;
    }

    void addCase(boolean reference, boolean response, int count) {
        if (reference && response) mTP += count;
        else if (reference && (!response)) mFN += count;
        else if ((!reference) && response) mFP += count;
        else mTN += count;
    }

    /**
     * Returns the number of true positive cases.  A true positive
     * is where both the reference and response are true.
     *
     * @return The number of true positives.
     */
    public long truePositive() {
        return mTP;
    }

    /**
     * Returns the number of false positive cases.  A false positive
     * is where the reference is false and response is true.
     *
     * @return The number of false positives.
     */
    public long falsePositive() {
        return mFP;
    }

    /**
     * Returns the number of true negative cases.  A true negative
     * is where both the reference and response are false. 
     *
     * @return The number of true negatives.
     */
    public long trueNegative() {
        return mTN;
    }

    /**
     * Returns the number of false negative cases.  A false negative
     * is where the reference is true and response is false.
     *
     * @return The number of false negatives.
     */
    public long falseNegative() {
        return mFN;
    }

    /**
     * Returns the number of positive reference cases.  A positive
     * reference case is one where the reference is true.
     *
     * @return The number of positive references.
     */
    public long positiveReference() {
        return truePositive() + falseNegative();
    }
    
    /**
     * Returns the number of negative reference cases.  A negative
     * reference case is one where the reference is false.
     *
     * @return The number of negative references.
     */
    public long negativeReference() {
        return trueNegative() + falsePositive();
    }

    /**
     * Returns the sample reference likelihood, or prevalence, which
     * is the number of positive references divided * by the total
     * number of cases.
     *
     * @return The sample reference likelihood.
     */
    public double referenceLikelihood() {
        return div(positiveReference(), total());
    }

    /**
     * Returns the number of positive response cases.  A positive
     * response case is one where the response is true.
     * 
     * @return The number of positive responses.
     */
    public long positiveResponse() {
        return truePositive() + falsePositive();
    }

    /**
     * Returns the number of negative response cases.  A negative
     * response case is one where the response is false.
     * 
     * @return The number of negative responses.
     */
    public long negativeResponse() {
        return trueNegative() + falseNegative();
    }

    /**
     * Returns the sample response likelihood, which is the number of
     * positive responses divided by the total number of cases.
     *
     * @return The sample response likelihood.
     */
    public double responseLikelihood() {
        return div(positiveResponse(), total());
    }

    /**
     * Returns the number of cases where the response is correct.  A
     * correct response is one where the reference and response are
     * the same.
     *
     * @return The number of correct responses.
     */
    public long correctResponse() {
        return truePositive() + trueNegative();
    }

    /**
     * Returns the number of cases where the response is incorrect.
     * An incorrect response is one where the reference and response
     * are different.
     *
     * @return The number of incorrect responses.
     */
    public long incorrectResponse() {
        return falsePositive() + falseNegative();
    }

    /**
     * Returns the total number of cases.
     *
     * @return The total number of cases.
     */
    public long total() {
        return mTP + mFP + mTN + mFN;
    }

    /**
     * Returns the sample accuracy of the responses.  The accuracy is
     * just the number of correct responses divided by the total number
     * of respones.
     *
     * @return The sample accuracy.
     */
    public double accuracy() {
        return div(correctResponse(), total());
    }

    /**
     * Returns the recall.  The recall is the number of true positives
     * divided by the number of positive references.  This is the
     * fraction of positive reference cases that were found by the
     * classifier.
     *
     * @return The recall value.
     */
    public double recall() {
        return div(truePositive(), positiveReference());
    }

    /**
     * Returns the precision.  The precision is the number of true
     * positives divided by the number of positive respones.  This is
     * the fraction of positive responses returned by the classifier
     * that were correct.
     *
     * @return The precision value.
     */
    public double precision() {
        return div(truePositive(), positiveResponse());
    }
    
    /**
     * Returns the rejection recall, or specificity, value.
     * The rejection recall is the percentage of negative references
     * that had negative respones.
     *
     * @return The rejection recall value.
     */
    public double rejectionRecall() {
        return div(trueNegative(), negativeReference());
    }

    /**
     * Returns the rejection prection, or selectivity, value.
     * The rejection precision is the percentage of negative responses
     * that were negative references.
     *
     * @return The rejection precision value.
     */
    public double rejectionPrecision() {
        return div(trueNegative(), negativeResponse());
    }

    /**
     * Returns the F<sub><sub>1</sub></sub> measure.  This is the
     * result of applying the method {@link #fMeasure(double)} to
     * <code>1</code>.  of the method
     *
     *
     * @return The F<sub><sub>1</sub></sub> measure. 
     */
    public double fMeasure() {
        return fMeasure(1.0);
    }

    /**
     * Returns the <code>F<sub><sub>&beta;</sub></sub></code> value for
     * the specified <code>&beta;</code>.
     *
     * @param beta The <code>&beta;</code> parameter.
     * @return The <code>F<sub><sub>&beta;</sub></sub></code> value.
     */
    public double fMeasure(double beta) {
        return fMeasure(beta,recall(),precision());
    }

    /**
     * Returns the Jaccard coefficient.
     *
     * @return The Jaccard coefficient.
     */
    public double jaccardCoefficient() {
        return div(truePositive(), 
                   truePositive() + falseNegative() + falsePositive());
    }

    /**
     * Returns the &chi;<sup>2</sup> value.
     *
     * @return The &chi;<sup>2</sup> value.
     */
    public double chiSquared() {
        double tp = truePositive();
        double tn = trueNegative();
        double fp = falsePositive();
        double fn = falseNegative();
        double tot = total();
        double diff = tp * tn - fp * fn;
        return tot * diff * diff 
            / ((tp + fn) * (fp + tn) * (tp + fp) * (fn + tn));
    }

    /**
     * Returns the &phi;<sup>2</sup> value.
     *
     * @return The &phi;<sup>2</sup> value.
     */
    public double phiSquared() {
        return chiSquared() / (double) total();
    }

    /**
     * Return the value of Yule's Q statistic.
     *
     * @return The value of Yule's Q statistic.
     */
    public double yulesQ() {
        double tp = truePositive();
        double tn = trueNegative();
        double fp = falsePositive();
        double fn = falseNegative();
        return (tp*tn - fp*fn) / (tp*tn + fp*fn);
    }

    /**
     * Return the value of Yule's Y statistic.
     *
     * @return The value of Yule's Y statistic.
     */
    public double yulesY() {
        double tp = truePositive();
        double tn = trueNegative();
        double fp = falsePositive();
        double fn = falseNegative();
        return (Math.sqrt(tp*tn) - Math.sqrt(fp*fn)) 
            / (Math.sqrt(tp*tn) + Math.sqrt(fp*fn));
    }

    /**
     * Return the Fowlkes-Mallows score.
     *
     * @return The Fowlkes-Mallows score.
     */
    public double fowlkesMallows() {
        double tp = truePositive();
        return tp / Math.sqrt(precision() * recall());
    }

    /**
     * Returns the standard deviation of the accuracy.  This is
     * computed as the deviation of an equivalent accuracy generated
     * by a binomial distribution, which is just a sequence of
     * Bernoulli (binary) trials.
     *
     * @return The standard deviation of the accuracy.
     */
    public double accuracyDeviation() {
        // e.g. p = 0.05 for a 5% conf interval
        double p = accuracy();
        double total = total();
        double variance = p * (1.0 - p) / total;
        return Math.sqrt(variance);
    }

    /**
     * The probability that the reference and response are the same if
     * they are generated randomly according to the reference and
     * response likelihoods.
     *
     * @return The accuracy of a random classifier.
     */
    public double randomAccuracy() {
        double ref = referenceLikelihood();
        double resp = responseLikelihood();
        return ref * resp + (1.0 - ref) * (1.0 - resp);
    }

    /**
     * The probability that the reference and the response are the same
     * if the reference and response likelihoods are both the average
     * of the sample reference and response likelihoods.
     *
     * @return The unbiased random accuracy.
     */
    public double randomAccuracyUnbiased() {
        double avg = (referenceLikelihood() + responseLikelihood()) / 2.0;
        return avg * avg + (1.0 - avg) * (1.0 - avg);
    }

    /**
     * Returns the value of the kappa statistic.
     *
     * @return The value of the kappa statistic.
     */
    public double kappa() {
        return Statistics.kappa(accuracy(),randomAccuracy());
    }

    /**
     * Returns the value of the unbiased kappa statistic.
     *
     * @return The value of the unbiased kappa statistic.
     */
    public double kappaUnbiased() {
        return Statistics.kappa(accuracy(),randomAccuracyUnbiased());
    }

    /**
     * Returns the value of the kappa statistic adjusted for
     * prevalence.
     *
     * @return The value of the kappa statistic adjusted for
     * prevalence.
     */
    public double kappaNoPrevalence() {
        return 2.0 * accuracy() - 1.0;
    }

    /**
     * Returns a string-based representation of this evaluation.
     *
     * @return A string-based representation of this evaluation.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(2048);
        sb.append("  Total=" + total() + '\n');
        sb.append("  True Positive=" + truePositive() + '\n');
        sb.append("  False Negative=" + falseNegative() + '\n');
        sb.append("  False Positive=" + falsePositive() + '\n');
        sb.append("  True Negative=" + trueNegative() + '\n');
        sb.append("  Positive Reference=" + positiveReference() + '\n');
        sb.append("  Positive Response=" + positiveResponse() + '\n');
        sb.append("  Negative Reference=" + negativeReference() + '\n');
        sb.append("  Negative Response=" + negativeResponse() + '\n');
        sb.append("  Accuracy=" + accuracy() + '\n');
        sb.append("  Recall=" + recall() + '\n');
        sb.append("  Precision=" + precision() + '\n');
        sb.append("  Rejection Recall=" + rejectionRecall() + '\n');
        sb.append("  Rejection Precision=" + rejectionPrecision() + '\n');
        sb.append("  F(1)=" + fMeasure(1) + '\n');
        sb.append("  Fowlkes-Mallows=" + fowlkesMallows() + '\n');
        sb.append("  Jaccard Coefficient=" + jaccardCoefficient() + '\n');
        sb.append("  Yule's Q=" + yulesQ() + '\n');
        sb.append("  Yule's Y=" + yulesY() + '\n');
        sb.append("  Reference Likelihood=" + referenceLikelihood() + '\n');
        sb.append("  Response Likelihood=" + responseLikelihood() + '\n');
        sb.append("  Random Accuracy=" + randomAccuracy() + '\n');
        sb.append("  Random Accuracy Unbiased=" + randomAccuracyUnbiased() 
                  + '\n');
        sb.append("  kappa=" + kappa() + '\n');
        sb.append("  kappa Unbiased=" + kappaUnbiased() + '\n');
        sb.append("  kappa No Prevalence=" + kappaNoPrevalence() + '\n');
        sb.append("  chi Squared=" + chiSquared() + '\n');
        sb.append("  phi Squared=" + phiSquared() + '\n');
        sb.append("  Accuracy Deviation=" + accuracyDeviation());
        return sb.toString();
    }
    


    /**
     * Returns the F<sub><sub>&beta;</sub></sub> measure for
     * a specified &beta;, recall and precision values.
     *
     * @param beta Relative weighting of precision.
     * @param recall Recall value.
     * @param precision Precision value.
     * @return The F<sub><sub>&beta;</sub></sub> measure.
     */
    public static double fMeasure(double beta, 
                                  double recall, double precision) {
        double betaSq = beta * beta;
        return (1.0 + betaSq) * recall * precision 
            / (recall + (betaSq*precision));
    }

    private static void validateCount(String countName, long val) {
        if (val < 0) {
            String msg = "Count must be non-negative."
                + " Found " + countName + "=" + val;
            throw new IllegalArgumentException(msg);
        }
    }

    static double div(double x, double y) {
        return x/y;
    }

}
