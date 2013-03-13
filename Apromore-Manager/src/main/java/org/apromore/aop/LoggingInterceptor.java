package org.apromore.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging AspectJ Interceptor. Used to auto magically log the entry, exit and exceptions within apromore.
 * This is useful as exceptions are hidden and due the large nature of the code base it can be hard tracking execution.
 * FOR DEVELOPMENT uses only, not for production.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Aspect
public class LoggingInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);


    /**
     * Log the Entry to a method.
     * @param joinPoint the joining point we are intercepting to inject our logs.
     */
    @Before("execution(* org.apromore..*.*(..))")
    public void logEntry(JoinPoint joinPoint) {
        LOGGER.debug("--> Entering: " + joinPoint.getSignature().getName());
    }


    /**
     * Log the Exit to a method.
     * @param joinPoint the joining point we are intercepting to inject our logs.
     */
    @After("execution(* org.apromore..*.*(..))")
    public void logExit(JoinPoint joinPoint) {
        LOGGER.debug("<-- Exiting: " + joinPoint.getSignature().getName());
    }


    /**
     * Log the the exception from a method.
     * @param joinPoint the joining point we are intercepting to inject our logs.
     * @param error the error that was thrown.
     */
    @AfterThrowing(pointcut = "execution(* org.apromore..*.*(..))", throwing= "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        LOGGER.debug("*** Exception in: " + joinPoint.getSignature().getName() + " - Threw: " + error);
    }

}
