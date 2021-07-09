/*-
 * #%L
 * This file is part of "Apromore Core".
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
package org.apromore.portal.common.i18n;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.ApromoreZKListener;
import org.apromore.portal.ConfigBean;
import org.slf4j.Logger;
import org.zkoss.util.Locales;
import org.zkoss.util.resource.Labels;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import org.apromore.commons.datetime.Constants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class I18nSession {

    private static final String COOKIE_LANG_TAG = "langtag";
    private static final String COOKIE_DATE_TIME_PATTERN_TAG = "datetimepattern";

    private static final String DEFAULT_LANG_TAG = "en";
    private static final String DEFAULT_DATE_TIME_PATTERN = Constants.DATE_TIME_FORMAT_HUMANIZED;

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(I18nSession.class);
    @Getter private I18nConfig config;

    @Getter private Locale preferredLocale = null;
    @Getter private String persistedLangTag = null;
    @Getter private String preferredDateTimePattern = null;
    @Getter private DateTimeFormatter preferredDateTimeFormatter = null;

    public I18nSession(I18nConfig config) {
        this.config = config;
    }

    public String getPreferredLangTag() {
        if (preferredLocale != null) {
            return  preferredLocale.toLanguageTag();
        }
        return DEFAULT_LANG_TAG;
    }

    public void resetClientPreferredLocale() {
        Clients.evalJavaScript("Ap.common.removeCookie('" + COOKIE_LANG_TAG + "')");
        Clients.evalJavaScript("Ap.common.removeCookie('" + COOKIE_DATE_TIME_PATTERN_TAG + "')");
    }

    public void pushClientPreferredLocale() {
        if (preferredLocale != null) {
            String langTag = preferredLocale.toLanguageTag();
            Clients.evalJavaScript("Ap.common.setCookie('" + COOKIE_LANG_TAG + "','" + langTag + "')");
        } else {
            Clients.evalJavaScript("Ap.common.removeCookie('" + COOKIE_LANG_TAG + "')");
        }
        if (preferredDateTimePattern != null) {
            Clients.evalJavaScript("Ap.common.setCookie('" + COOKIE_DATE_TIME_PATTERN_TAG + "','" + preferredDateTimePattern + "')");
        } else {
            Clients.evalJavaScript("Ap.common.removeCookie('" + COOKIE_DATE_TIME_PATTERN_TAG + "')");
        }
    }

    public Map<String, String> pullClientPreferredLocale() {
        Map<String, String> clientPreferred = new HashMap<String, String>();
        HttpServletRequest request = (HttpServletRequest) Executions.getCurrent().getNativeRequest();
        Cookie[] cookies = request.getCookies();
        persistedLangTag = "auto";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                if (COOKIE_LANG_TAG.equals(name)) {
                    persistedLangTag = value;
                    clientPreferred.put(COOKIE_LANG_TAG, value);
                } else if (COOKIE_DATE_TIME_PATTERN_TAG.equals(name)) {
                    clientPreferred.put(COOKIE_DATE_TIME_PATTERN_TAG, value);
                }
            }
        }

        if (!clientPreferred.containsKey(COOKIE_LANG_TAG)) {
            // Just emulate what ZK does to have more control, get it from servlet request
            Locale browserLocale = request.getLocale();
            clientPreferred.put(COOKIE_LANG_TAG, browserLocale.getLanguage()); // only save the 'en' or 'ja'
        }
        return clientPreferred;
    }

    public void applyLocale(Locale locale) {
        // check if locale is in the supported list
        if (locale == null || !config.isSupported(locale)) {
            preferredLocale = Locale.forLanguageTag(DEFAULT_LANG_TAG);
        } else {
            preferredLocale = locale;
        }
        genDateTimeFormatter();
        Sessions.getCurrent().setAttribute(Attributes.PREFERRED_LOCALE, preferredLocale);
        try {
            Clients.reloadMessages(preferredLocale);
            Locales.setThreadLocal(preferredLocale);
        } catch (Exception e) {
            // ignore
            LOGGER.error("Fail to apply a selected locale", e);
        }
    }

    public void applyLocale(String languageTag) {
        Locale locale = Locale.forLanguageTag(languageTag);
        preferredDateTimePattern = null; // reset date time format
        applyLocale(locale);
    }

    public void applyLocaleFromClient() {
        if (!config.isEnabled()) {
            applyLocale(DEFAULT_LANG_TAG);
            return;
        }
        Map<String, String> clientPreferred = pullClientPreferredLocale();
        String clientDateTimePattern = clientPreferred.get(COOKIE_DATE_TIME_PATTERN_TAG);

        if (clientDateTimePattern != null) {
            preferredDateTimePattern = clientDateTimePattern;
        }
        applyLocale(clientPreferred.get(COOKIE_LANG_TAG));
    }

    public void genDateTimeFormatter() {
        if (preferredDateTimePattern == null) {
            if (preferredLocale == null) {
                preferredDateTimePattern = DEFAULT_DATE_TIME_PATTERN;
            } else {
                preferredDateTimePattern = config.getDateTimePattern(preferredLocale);
            }
        }
        preferredDateTimeFormatter = DateTimeFormatter.ofPattern(preferredDateTimePattern, preferredLocale);
    }

}
