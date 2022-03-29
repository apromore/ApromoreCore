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

package org.apromore.zk.label;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;
import org.slf4j.LoggerFactory;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Sessions;

public interface LabelSupplier {
    default String getBundleName() {
        return "default";
    }

    default ResourceBundle getLabels() {
        Locale locale = (Locale) Sessions.getCurrent().getAttribute(Attributes.PREFERRED_LOCALE);
        if (locale == null) {
          locale = Locale.getDefault();
          LoggerFactory.getLogger(LabelSupplier.class)
                       .warn("ZK user session has no preferred locale; using JVM default {}", locale);
        }

        return ResourceBundle.getBundle(this.getBundleName(), locale, this.getClass().getClassLoader());
    }

    default String getLabel(String key) {
        String label;
        label = getLabel(key, null);
        if (label == null) {
            label = key;
        }
        return label;
    }

    default String getLabel(String key, String defaultVal) {
        String label;
        try {
            label = getLabels().getString(key);
        } catch (MissingResourceException e) {
            label = null;
        }
        if (label == null) {
            label = defaultVal;
        }
        return label;
    }

}
