package org.apromore.aop;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Timer interceptor for class and methods but also stores and reports stats.
 *
 * FOR DEVELOPMENT uses only, not for production.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Aspect
public class TimerStatsInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerInterceptor.class);

    private static ConcurrentHashMap<String, MethodStats> methodStats = new ConcurrentHashMap<>();


    /**
     * This goes around the DAO methods and times how long to query and construct objects.
     * @param pjp the proceeding point cut, this contains the details of the method to wrap around.
     * @return the return object of this method we are wrapping around.
     * @throws Throwable if the method throws an exception.
     */
    @Around("execution(* org.apromore.manager..*.*(..))")
    public Object logMethodStats(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object retVal = pjp.proceed();

        stopWatch.stop();
        String str = pjp.getTarget().toString();
        String className = str.substring(str.lastIndexOf(".")+1, str.lastIndexOf("@"));
        String methodName = pjp.getSignature().getName();

        updateStats(className, methodName, stopWatch.getTime());

        return retVal;
    }

    /* Records and outputs data to the logs. */
    private void updateStats(String className, String methodName, long elapsedTime) {
        MethodStats stats = methodStats.get(methodName);
        if(stats == null) {
            stats = new MethodStats();
            methodStats.put(methodName,stats);
        }
        stats.count++;
        stats.totalTime += elapsedTime;
        if(elapsedTime > stats.maxTime) {
            stats.maxTime = elapsedTime;
        }

        LOGGER.debug(className + " - " + methodName + ": " + elapsedTime + "ms");

        long methodWarningThreshold = 1000;
        if(elapsedTime > methodWarningThreshold) {
            LOGGER.warn("method warning: " + methodName + "(), cnt = " + stats.count + ", lastTime = " + elapsedTime + ", maxTime = " + stats.maxTime);
        }

        long statLogFrequency = 10;
        if(stats.count % statLogFrequency == 0) {
            long avgTime = stats.totalTime / stats.count;
            long runningAvg = (stats.totalTime-stats.lastTotalTime) / statLogFrequency;
            LOGGER.debug("method: " + methodName + "(), cnt = " + stats.count + ", lastTime = " + elapsedTime + ", avgTime = " + avgTime + ", runningAvg = " + runningAvg + ", maxTime = " + stats.maxTime);

            stats.lastTotalTime = stats.totalTime;
        }
    }


    /**
     * Private class for storing of the Method Stats.
     */
    private static class MethodStats {
        public long count;
        public long totalTime;
        public long lastTotalTime;
        public long maxTime;
    }

}
