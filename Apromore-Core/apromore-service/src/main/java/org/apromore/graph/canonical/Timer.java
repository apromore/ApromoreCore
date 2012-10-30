package org.apromore.graph.canonical;

import java.util.GregorianCalendar;

/**
 * Implementation of the Canonical Timer Event.
 *
 * @author Cameron James
 */
public class Timer extends Event implements ITimer {

    protected Expression timeExpression;
    protected String timeDuration;
    protected GregorianCalendar timeDate;

    /**
     * Empty constructor.
     */
    public Timer() {
        super();
    }

    /**
     * Constructor with label of the place parameter.
     * @param label String to use as a label of this place.
     */
    public Timer(String label) {
        super(label);
    }

    /**
     * Constructor with label and description of the place parameters.
     * @param label String to use as a label of this place.
     * @param desc  String to use as a description of this place.
     */
    public Timer(String label, String desc) {
        super(label, desc);
    }


    /**
     * Returns the Time Expression.
     * @return the time expression
     */
    public Expression getTimeExpression() {
        return timeExpression;
    }

    /**
     * Set the Time Expression
     * @param newExpr the time expression
     */
    public void setTimeExpression(Expression newExpr) {
        timeExpression = newExpr;
    }

    /**
     * Returns the Time Duration.
     * @return the time duration.
     */
    public String getTimeDuration() {
        return timeDuration;
    }

    /**
     * Sets the time Duration.
     * @param newTimeDuration the time duration
     */
    public void setTimeDuration(String newTimeDuration) {
        timeDuration = newTimeDuration;
    }

    /**
     * Return the Time Date.
     * @return the Calendar
     */
    public GregorianCalendar getTimeDate() {
        return timeDate;
    }

    /**
     * The Time Date.
     * @param newTimeDate the time date
     */
    public void setTimeDate(GregorianCalendar newTimeDate) {
        timeDate = newTimeDate;
    }

}
