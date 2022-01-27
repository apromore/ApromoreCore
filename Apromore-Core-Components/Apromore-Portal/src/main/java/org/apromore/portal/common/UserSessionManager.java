/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.portal.common;

import java.util.Objects;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.User;
import org.apromore.manager.client.ManagerService;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.i18n.I18nSession;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.dialogController.UserAuthenticationHelper;
import org.apromore.portal.dialogController.dto.ApromoreSession;
import org.apromore.portal.model.UserType;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zkplus.spring.SpringUtil;

/**
 * Static methods for typed access to user session attributes.
 *
 * These methods are a type-safe way to manipulate the ZK
 * {@link org.zkoss.zk.ui.Session).
 */
public abstract class UserSessionManager {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(UserSessionManager.class);

    public static final String USER = "USER";
    public static final String CURRENT_FOLDER = "CURRENT_FOLDER";
    public static final String CURRENT_SECURITY_ITEM = "CURRENT_SECURITY_ITEM";
    public static final String CURRENT_SECURITY_TYPE = "CURRENT_SECURITY_TYPE";
    public static final String CURRENT_SECURITY_OWNERSHIP = "CURRENT_SECURITY_OWNERSHIP";
    public static final String PREVIOUS_FOLDER = "PREVIOUS_FOLDER";
    public static final String TREE = "TREE";
    public static final String MAIN_CONTROLLER = "MAIN_CONTROLLER";
    public static final String SELECTED_FOLDER_IDS = "SELECTED_FOLDER_IDS";
    public static final String SELECTED_PROCESS_IDS = "SELECTED_PROCESS_IDS";
    public static final String LOCALE = "LOCALE";

    public static void setCurrentUser(UserType user) {

	// Update the user on the ZK session
	setAttribute(USER, user);
	LOGGER.debug("Set user session current user attribute to {}", user == null ? null : user.getUsername());

	// Broadcast that the user has changed for this ZK session, although not the
	// changed value
	EventQueues.lookup(Constants.EVENT_QUEUE_REFRESH_SCREEN, Sessions.getCurrent(), true)
	        .publish(new Event(Constants.EVENT_QUEUE_SESSION_ATTRIBUTES, null, USER));
    }

    private static Object getAttribute(String attribute) {
	return Sessions.getCurrent().getAttribute(attribute);
    }

    private static void setAttribute(String attribute, Object value) {
	Sessions.getCurrent().setAttribute(attribute, value);
    }

    public static void setCurrentI18nSession(I18nSession i18nSession) {
	setAttribute(LOCALE, i18nSession);
    }

    public static I18nSession getCurrentI18nSession() {
	Object attribute = getAttribute(LOCALE);

	return attribute instanceof I18nSession ? (I18nSession) attribute : null;
    }

    public static UserType getCurrentUser() {
	Object attribute = getAttribute(USER);

	if (attribute == null) {
		ManagerService managerService = (ManagerService) SpringUtil.getBean("managerClient");
		initializeUser(managerService, null, null, null);
		attribute = getAttribute(USER);
	}

	return attribute instanceof UserType ? (UserType) attribute : null;
    }

    public static void initializeUser(ManagerService manager, ConfigBean config, UserType userType, User userAcct) {
	// No initialization required if the user is already set
	if (getAttribute(USER) != null) {
	    LOGGER.debug("No initialization required as the user is already set");
	    return;
	}

	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	if (authentication != null) {
	    Object details = authentication.getDetails();
	    // KC User
	    // get user from principle.
	    // if username is null set email as username
	    // get user by username or email address
	    // if user exist then set user
	    // else create user and set user

	    if (authentication.getClass().equals(KeycloakAuthenticationToken.class)) {
		KeycloakPrincipal<KeycloakSecurityContext> kcPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) ((KeycloakAuthenticationToken) authentication)
		        .getPrincipal();
		String userName = kcPrincipal.getName();

		AccessToken token = kcPrincipal.getKeycloakSecurityContext().getToken();
		String email = token.getEmail();
		userName = Objects.requireNonNullElse(userName, email);

		UserType user = manager.readUserByUsername(userName);

		try {
		    user = user == null ? manager.readUserByEmail(email) : user;

		    if (user == null) {
			user = new UserType(userName, email, token.getGivenName(), token.getFamilyName());

			user = manager.writeUser(user);

		    }
		    setCurrentUser(user);
		} catch (Exception e) {

		    LOGGER.error("Exception in init user " + ExceptionUtils.getStackTrace(e));
		    LOGGER.debug("No User Found");
		}

	    } else if (details instanceof WebAuthenticationDetails) {
		UserAuthenticationHelper userAuthenticationHelper = new UserAuthenticationHelper(userAcct);
		try {
		    authentication = userAuthenticationHelper.attemptAuthentication(null, null);
		    SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
		    LOGGER.error("Failed login", e);
		}

		if (userType != null) { // e.g. SSO SAML
		    setCurrentUser(userType);
		    LOGGER.debug("User \"{}\" login via SSO SAML account", userType.getUsername());
		}
	    } else if (details instanceof UserType) { // Locally created user (via password)
		UserType user = (UserType) details;
		setCurrentUser(user);
		LOGGER.debug("User \"{}\" login via local account", user.getUsername());

	    } else if (userType != null) { // e.g. SSO SAML
		setCurrentUser(userType);
		LOGGER.debug("User \"{}\" login via SSO SAML account", userType.getUsername());

	    } else if (details != null) {
		LOGGER.warn("Unsupported details class " + details.getClass());

	    } else {
		LOGGER.warn("User's authentication has null details");
	    }

	} else {
	    LOGGER.warn("Current user neither set on the security context, nor authenticated");
	}
    }

    // Lifted from NewUserRegistrationHttpServletRequestHandler.java

    private static String findAttributeFieldByName(Attributes attributes, String fieldName) throws NamingException {
	Attribute attr = attributes.get(fieldName);
	NamingEnumeration e = attr.getAll();
	while (e.hasMore()) {
	    return (String) e.next();
	}
	return null;
    }

    // TODO: fix the memory leak by reclaiming stale sessions
    public static void setEditSession(String id, ApromoreSession session) {
	setAttribute(id, session);
    }

    public static ApromoreSession getEditSession(String id) {
	return (ApromoreSession) getAttribute(id);
    }

    public static void removeEditSession(String id) {
	Session session = Sessions.getCurrent();
	if (session != null) {
	    session.removeAttribute(id);
	}
    }

    /*
     * public static void setCurrentFolder(FolderType folder) {
     * setAttribute(CURRENT_FOLDER, folder); if (folder != null &&
     * getMainController() != null) {
     * getMainController().setBreadcrumbs(folder.getId()); } }
     * 
     * public static FolderType getCurrentFolder() { FolderType folder =
     * (FolderType) getAttribute(CURRENT_FOLDER); if (folder == null) { folder = new
     * FolderType(); folder.setId(0); folder.setFolderName("Home");
     * setCurrentFolder(folder); }
     * 
     * return folder; }
     * 
     * public static void setSelectedFolderIds(List<Integer> folderIds) {
     * setAttribute(SELECTED_FOLDER_IDS, folderIds); }
     * 
     * @SuppressWarnings("unchecked") public static List<Integer>
     * getSelectedFolderIds() { if (getAttribute(SELECTED_FOLDER_IDS) != null) {
     * return (List<Integer>) getAttribute(SELECTED_FOLDER_IDS); }
     * 
     * return new ArrayList<>(); }
     * 
     * public static void setSelectedProcessIds(List<Integer> processIds) {
     * setAttribute(SELECTED_PROCESS_IDS, processIds); }
     * 
     * @SuppressWarnings("unchecked") public static List<Integer>
     * getSelectedProcessIds() { if (getAttribute(SELECTED_PROCESS_IDS) != null) {
     * return (List<Integer>) getAttribute(SELECTED_PROCESS_IDS); }
     * 
     * return new ArrayList<>(); }
     * 
     * public static void setPreviousFolder(FolderType folder) {
     * setAttribute(PREVIOUS_FOLDER, folder); }
     * 
     * public static FolderType getPreviousFolder() { if
     * (getAttribute(PREVIOUS_FOLDER) != null) { return (FolderType)
     * getAttribute(PREVIOUS_FOLDER); }
     * 
     * return null; }
     * 
     * public static void setTree(List<FolderType> folders) { setAttribute(TREE,
     * folders); }
     * 
     * @SuppressWarnings("unchecked") public static List<FolderType> getTree() { if
     * (getAttribute(TREE) != null) { return (List<FolderType>) getAttribute(TREE);
     * }
     * 
     * return null; }
     */

    public static void setMainController(MainController mainController) {
	setAttribute(MAIN_CONTROLLER, mainController);
    }

    public static MainController getMainController() {
	if (getAttribute(MAIN_CONTROLLER) != null) {
	    return (MainController) getAttribute(MAIN_CONTROLLER);
	}

	return null;
    }

    public static void setCurrentSecurityItem(Integer id) {
	setAttribute(CURRENT_SECURITY_ITEM, id);
    }

    public static Integer getCurrentSecurityItem() {
	if (getAttribute(CURRENT_SECURITY_ITEM) != null) {
	    return (Integer) getAttribute(CURRENT_SECURITY_ITEM);
	}

	return null;
    }

    public static void setCurrentSecurityType(FolderTreeNodeTypes type) {
	setAttribute(CURRENT_SECURITY_TYPE, type);
    }

    public static FolderTreeNodeTypes getCurrentSecurityType() {
	if (getAttribute(CURRENT_SECURITY_TYPE) != null) {
	    return (FolderTreeNodeTypes) getAttribute(CURRENT_SECURITY_TYPE);
	}

	return FolderTreeNodeTypes.Folder;
    }

    public static void setCurrentSecurityOwnership(boolean hasOwnership) {
	setAttribute(CURRENT_SECURITY_OWNERSHIP, hasOwnership);
    }

    public static boolean getCurrentSecurityOwnership() {
	if (getAttribute(CURRENT_SECURITY_OWNERSHIP) != null) {
	    return (Boolean) getAttribute(CURRENT_SECURITY_OWNERSHIP);
	}

	return false;
    }
}
