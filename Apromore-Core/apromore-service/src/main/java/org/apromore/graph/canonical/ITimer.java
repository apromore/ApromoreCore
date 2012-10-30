package org.apromore.graph.canonical;

import java.util.GregorianCalendar;

/**
 * Interface to a Canonical Timer.
 *
 * @author Cameron James
 */
public interface ITimer extends IEvent {

    /**
     * Returns the Time Expression.
     * @return the time expression
     */
    Expression getTimeExpression();

    /**
     * Set the Time Expression
     * @param newExpr the time expression
     */
    void setTimeExpression(Expression newExpr);

    /**
     * Returns the Time Duration.
     * @return the time duration.
     */
    String getTimeDuration();

    /**
     * Sets the time Duration.
     * @param newTimeDuration the time duration
     */
    void setTimeDuration(String newTimeDuration);

    /**
     * Return the Time Date.
     * @return the Calendar
     */
    GregorianCalendar getTimeDate();

    /**
     * The Time Date.
     * @param newTimeDate the time date
     */
    void setTimeDate(GregorianCalendar newTimeDate);
}