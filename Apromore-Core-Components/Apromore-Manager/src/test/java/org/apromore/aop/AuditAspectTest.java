/*-
 * #%L
 * This file is part of "Apromore Core".
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
///*-
// * #%L
// * This file is part of "Apromore Core".
// * 
// * Copyright (C) 2013 Queensland University of Technology.
// * %%
// * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
// * %%
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Lesser Public License for more details.
// * 
// * You should have received a copy of the GNU General Lesser Public
// * License along with this program.  If not, see
// * <http://www.gnu.org/licenses/lgpl-3.0.html>.
// * #L%
// */
//
//package org.apromore.aop;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.WebAuthenticationDetails;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
///**
// * @author David Galichet
// */
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(locations = "classpath:*/**/applicationContext-*-TEST.xml")
//public class AuditAspectTest {
//
//    @Autowired
//    private SimpleAuditedService simpleAuditedService;
//
//    @AfterEach
//    public void after() {
//        SecurityContextHolder.clearContext();
//    }
//
//    @BeforeEach
//    public void before() {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setRemoteAddr("10.0.0.1");
//        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("ze-principal", "ze-credentials");
//        authentication.setDetails(details);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//    @Test
//    public void testMethodWithoutException() {
//        SimpleAuditedService.Customer customer = new SimpleAuditedService.Customer();
//        customer.setName("John Smith");
//        customer.setEmail("john.smith@xebia.fr");
//        simpleAuditedService.save(customer);
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testMethodWithException() {
//        SimpleAuditedService.Customer customer = new SimpleAuditedService.Customer();
//        customer.setName("John Smith");
//        customer.setEmail("john.smith");
//        simpleAuditedService.save(customer);
//    }
//
//}
