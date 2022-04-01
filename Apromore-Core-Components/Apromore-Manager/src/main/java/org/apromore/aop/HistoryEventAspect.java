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

import org.apromore.dao.model.StatusEnum;
import org.apromore.service.HistoryEventService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;


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

    @Autowired
    private HistoryEventService historyEventService;


    @Around("@annotation(event)")
    public Object logMessage(ProceedingJoinPoint pjp, Event event) throws Throwable {
        try {
            historyEventService.addNewEvent(StatusEnum.START, org.apromore.dao.model.HistoryEnum.valueOf(event.message().toString()));
            Object returned = pjp.proceed();
            historyEventService.addNewEvent(StatusEnum.FINISHED, org.apromore.dao.model.HistoryEnum.valueOf(event.message().toString()));
            return returned;
        } catch (Throwable t) {
            historyEventService.addNewEvent(StatusEnum.ERROR, org.apromore.dao.model.HistoryEnum.valueOf(event.message().toString()));
            throw t;
        }
    }

}
