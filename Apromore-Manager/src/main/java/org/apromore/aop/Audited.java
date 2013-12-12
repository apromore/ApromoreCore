package org.apromore.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to audit it using {@link org.apromore.aop.AuditAspect}.
 * Here is a code sample for method audit :
 * <pre>
 * <code>
 * &#064;(message = "save(#{args[0]}, #{args[1]}): #{returned}")
 *  public int save(String arg1, String arg2) { ... }
 * </code>
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD })
public @interface Audited {

    /**
     * <p>
     * Available variables
     * </p>
     * <ul>
     * <li><code>args</code></li>
     * <li><code>invokedObject</code></li>
     * <li><code>throwned</code></li>
     * <li><code>returned</code></li>
     * </ul>
     * <p>
     * Sample :<code>"save(#{args[0]}, #{args[1]}): #{returned}"</code>
     * </p>
     */
    String message() default "";
}
