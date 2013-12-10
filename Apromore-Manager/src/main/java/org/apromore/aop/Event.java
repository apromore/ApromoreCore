package org.apromore.aop;

import org.apromore.dao.model.HistoryEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to audit it using {@link org.apromore.aop.HistoryEventAspect}.
 * Here is a code sample for method audit :
 * <pre>
 * <code>
 * &#064;(message = "START_OPERATION")
 *  public int startOperation(String arg1, String arg2) { ... }
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Event {

    /**
     * <p>
     * PLEASE only use variables from HistoryEnum.
     * </p>
     * <p>
     * Sample :<code>"HistoryEnum.START_OP"</code>
     * </p>
     */
    HistoryEnum message();

}
