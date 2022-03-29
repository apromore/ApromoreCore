/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.aop;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This Aspect audits methods surrounded by {@link org.apromore.aop.Audited}
 * annotation.
 * <p>
 * The {@link org.apromore.aop.Audited#message()} parameter can be expressed with
 * a Spring Expression Language expression.<br/>
 * This message will be prefixed by the date and suffixed by the name
 * (principal) of the active user and the IP address of the user in web
 * applications. This aspect uses spring security context to retrieve the name.
 * </p>
 * <p>
 * This aspect can be activated by defining {@link org.apromore.aop.AuditAspect}
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
 * Using this aspect, all methods annotated with {@link org.apromore.aop.Audited}
 * will be logged in SLF4F <code>"fr.xebia.audit"</code> LOGGER with :
 * </p>
 * <ul>
 * <li>INFO level for method calls</li>
 * <li>WARN level for method calls wich throw exceptions</li>
 * </ul>
 * <p>
 * The template of the message is defined as a parameter of the
 * {@link org.apromore.aop.Audited} annotation :
 * <code>@Audited(message = "save(#{args[0]}, #{args[1]}): #{returned}")</code>
 * will produce a log entry similar to :
 * <code>...save(John Smith, john.smith@xebia.fr): 324325 by admin coming from 192.268.1.10</code>
 * </p>
 * <p>
 * In case of exception thrown, the log entry will be :
 * <code>...save(John Smith, john.smith):
 * threw 'java.lang.IllegalArgumentException: incorrect email by admin coming from 192.268.1.10</code>
 * </p>
 */
@Aspect
public class AuditAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditAspect.class);

    private SimpleDateFormat dateFormatPrototype = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    private Map<String, Expression> expressionCache = new ConcurrentHashMap<>();
    private ExpressionParser expressionParser = new SpelExpressionParser();
    private ParserContext parserContext = new TemplateParserContext();


    protected static void appendThrowableCauses(Throwable throwable, String separator, StringBuilder toAppendTo) {
        List<Throwable> alreadyAppendedThrowables = new ArrayList<>();

        while (throwable != null) {
            // append
            toAppendTo.append(throwable.toString());
            alreadyAppendedThrowables.add(throwable);

            // cause
            Throwable cause = throwable.getCause();
            if (cause == null || alreadyAppendedThrowables.contains(cause)) {
                break;
            } else {
                throwable = cause;
                toAppendTo.append(separator);
            }
        }
    }

    protected String buildMessage(String template, Object invokedObject, Object[] args, Object returned, Throwable throwned, long durationInNanos) {
        try {
            Expression expression = expressionCache.get(template);
            if (expression == null) {
                expression = expressionParser.parseExpression(template, parserContext);
                expressionCache.put(template, expression);
            }

            String evaluatedMessage = expression.getValue(new RootObject(invokedObject, args, returned, throwned), String.class);

            StringBuilder msg = new StringBuilder();

            SimpleDateFormat simpleDateFormat = (SimpleDateFormat) dateFormatPrototype.clone();
            msg.append(simpleDateFormat.format(new Date()));
            msg.append(" ").append(evaluatedMessage);

            if (throwned != null) {
                msg.append(" threw '");
                appendThrowableCauses(throwned, ", ", msg);
                msg.append("'");
            }
            msg.append(" by ");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                msg.append("anonymous");
            } else {
                msg.append(authentication.getName());
                if (authentication.getDetails() instanceof WebAuthenticationDetails) {
                    WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
                    msg.append(" coming from ").append(details.getRemoteAddress());
                }
            }
            msg.append(" in ").append(TimeUnit.MILLISECONDS.convert(durationInNanos, TimeUnit.NANOSECONDS)).append(" ms");
            return msg.toString();
        } catch (RuntimeException e) {
            StringBuilder msg = new StringBuilder("Exception evaluating template '" + template + "': ");
            appendThrowableCauses(e, ", ", msg);
            return msg.toString();
        }
    }

    @Around(value = "execution(* *(..)) && @annotation(audited)", argNames = "pjp,audited")
    public Object logMessage(ProceedingJoinPoint pjp, Audited audited) throws Throwable {

        long nanosBefore = System.nanoTime();
        try {
            Object returned = pjp.proceed();
            long durationInNanos = System.nanoTime() - nanosBefore;
            String message = buildMessage(audited.message(), pjp.getThis(), pjp.getArgs(), returned, null, durationInNanos);
            LOGGER.info(message);
            return returned;
        } catch (Throwable t) {
            long durationInNanos = System.nanoTime() - nanosBefore;
            String message = buildMessage(audited.message(), pjp.getThis(), pjp.getArgs(), null, t, durationInNanos);
            LOGGER.warn(message);
            throw t;
        }
    }


    protected static class RootObject {
        private final Object[] args;
        private final Object invokedObject;
        private final Object returned;
        private final Throwable throwned;

        private RootObject(Object invokedObject, Object[] args, Object returned, Throwable throwned) {
            super();
            this.invokedObject = invokedObject;
            this.args = args;
            this.returned = returned;
            this.throwned = throwned;
        }

        public Object[] getArgs() {
            return args;
        }

        public Object getInvokedObject() {
            return invokedObject;
        }

        public Object getReturned() {
            return returned;
        }

        public Throwable getThrowned() {
            return throwned;
        }
    }

    private static class TemplateParserContext implements ParserContext {
        public String getExpressionPrefix() {
            return "#{";
        }

        public String getExpressionSuffix() {
            return "}";
        }

        public boolean isTemplate() {
            return true;
        }
    }

}
