package org.apromore.aop;

import org.apache.commons.lang.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPA DAO AspectJ Interceptor. Used to auto magically log the time taken to execute DB Queries.
 * This is useful to determine if queries, Spring data or eclipselink are slowing the development.
 *
 * FOR DEVELOPMENT uses only, not for production.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Aspect
public class TimerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerInterceptor.class);

    /**
     * This goes around the DAO methods and times how long to query and construct objects.
     * @param pjp the proceeding point cut, this contains the details of the method to wrap around.
     * @return the return object of this method we are wrapping around.
     * @throws Throwable if the method throws an exception.
     * //|| execution(* org.springframework.data.repository.CrudRepository..*.*(..))
     */
    @Around("execution(* org.apromore.manager..*.*(..)) || " +
            "execution(* org.apromore.service..*.*(..)) || " +
            "execution(* org.apromore.dao..*.*(..)) || " +
            "(execution(* org.apromore.toolbox..*.*(..)) && !within(*..*.InMemoryGEDMatrix))")
    public Object logQueryTimes(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object retVal = pjp.proceed();

        stopWatch.stop();
        String str = pjp.getTarget().toString();

        LOGGER.debug("### " + str.substring(str.lastIndexOf(".")+1, str.lastIndexOf("@")) + " - " + pjp.getSignature().getName() + ": " + stopWatch.getTime() + "ms");

        return retVal;
    }
}
