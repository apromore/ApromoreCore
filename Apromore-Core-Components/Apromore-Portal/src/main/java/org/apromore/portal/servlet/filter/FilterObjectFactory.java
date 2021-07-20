/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
/// *-
// * #%L
// * This file is part of "Apromore Core".
// * %%
// * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
// * %%
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Lesser Public License for more details.
// *
// * You should have received a copy of the GNU General Lesser Public
// * License along with this program. If not, see
// * <http://www.gnu.org/licenses/lgpl-3.0.html>.
// * #L%
// */
// package org.apromore.portal.servlet.filter;
//
// import javax.servlet.Filter;
// import javax.servlet.ServletContext;
// import org.keycloak.adapters.servlet.KeycloakOIDCFilter;
// import org.springframework.beans.factory.FactoryBean;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;
// import org.springframework.web.context.ServletContextAware;
//
// @Component("keycloakFilter")
// public class FilterObjectFactory implements FactoryBean<Filter>, ServletContextAware {
//
// @Value("${site.useKeycloakSso}")
// private boolean useKeyCloak = true;
//
// @Autowired
// @Qualifier("springSecurityFilterChain")
// Filter securityFilter;
//
// ServletContext context;
//
// @Override
// public Filter getObject() throws Exception {
//
//
// if (useKeyCloak) {
// KeycloakOIDCFilter kcFilter = new CustomOIDCFilter(context);
// kcFilter.init(null);
// return kcFilter;
// }
// return securityFilter;
// }
//
// @Override
// public Class<?> getObjectType() {
//
// return Filter.class;
// }
//
// @Override
// public boolean isSingleton() {
// // TODO Auto-generated method stub
// return true;
// }
//
// @Override
// public void setServletContext(ServletContext servletContext) {
// this.context = servletContext;
//
// }
//
// }
