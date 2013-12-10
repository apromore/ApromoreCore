package org.apromore.aop;

import org.apromore.dao.model.StatusEnum;
import org.apromore.service.HistoryEventService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This Aspect audits methods surrounded by {@link Event}
 * annotation.
 * <p>
 * The {@link Event#message()} parameter can be expressed with
 * a Spring Expression Language expression.<br/>
 * This message will be prefixed by the date and suffixed by the name
 * (principal) of the active user and the IP address of the user in web
 * applications. This aspect uses spring security context to retrieve the name.
 * </p>
 * <p>
 * This aspect can be activated by defining {@link org.apromore.aop.HistoryEventAspect}
 * as spring bean and assuring that AspectJ support is activated in your spring
 * configuration :
 * <p/>
 * <pre>
 * <code>
 *  &lt;aop:aspectj-autoproxy /&gt;
 *  &lt;bean class="fr.xebia.audit.AuditAspect" /&gt;
 * </code>
 * </pre>
 * <p/>
 * </p>
 * <p>
 * Using this aspect, all methods annotated with {@link Event}
 * will be logged in SLF4F <code>"fr.xebia.audit"</code> logger with :
 * </p>
 * <ul>
 * <li>INFO level for method calls</li>
 * <li>WARN level for method calls wich throw exceptions</li>
 * </ul>
 * <p>
 * The template of the message is defined as a parameter of the
 * {@link Event} annotation :
 * <code>@Event(message = "save(#{args[0]}, #{args[1]}): #{returned}")</code>
 * will produce a log entry similar to :
 * <code>...save(John Smith, john.smith@xebia.fr): 324325 by admin coming from 192.168.1.10</code>
 * </p>
 * <p>
 * In case of exception thrown, the log entry will be :
 * <code>...save(John Smith, john.smith):
 * threw 'java.lang.IllegalArgumentException: incorrect email by admin coming from 192.168.1.10</code>
 * </p>
 */
@Aspect
public class HistoryEventAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryEventAspect.class);


    @Inject
    private HistoryEventService historyEventService;


    @Around(value = "@annotation(event)", argNames = "pjp,event")
    public Object logMessage(ProceedingJoinPoint pjp, Event event) throws Throwable {
        try {
            historyEventService.addNewEvent(StatusEnum.START, event.message());
            Object returned = pjp.proceed();
            historyEventService.addNewEvent(StatusEnum.FINISHED, event.message());
            return returned;
        } catch (Throwable t) {
            historyEventService.addNewEvent(StatusEnum.ERROR, event.message());
            throw t;
        }
    }

}
