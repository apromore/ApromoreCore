/*
 * Copyright (c) 2010 Xebia and the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apromore.aop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author David Galichet
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:*/**/applicationContext-*-TEST.xml")
public class AuditAspectTest {

    @Autowired
    private SimpleAuditedService simpleAuditedService;

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }

    @Before
    public void before() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.1");
        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("ze-principal", "ze-credentials");
        authentication.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testMethodWithoutException() {
        SimpleAuditedService.Customer customer = new SimpleAuditedService.Customer();
        customer.setName("John Smith");
        customer.setEmail("john.smith@xebia.fr");
        simpleAuditedService.save(customer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMethodWithException() {
        SimpleAuditedService.Customer customer = new SimpleAuditedService.Customer();
        customer.setName("John Smith");
        customer.setEmail("john.smith");
        simpleAuditedService.save(customer);
    }

}
