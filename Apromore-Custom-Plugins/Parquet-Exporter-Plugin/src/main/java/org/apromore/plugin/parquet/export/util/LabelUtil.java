/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
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
package org.apromore.plugin.parquet.export.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import org.zkoss.util.Locales;

public class LabelUtil {
    private static final String LABEL_LOCATION = "parquetexporter";

    /**
     * Get the labels in parquetexporter.properties for this bundle.
     * @return a resource bundle with a list of labels.
     */
    public static ResourceBundle getLabels() {
        return ResourceBundle.getBundle(LABEL_LOCATION,
                Locales.getCurrent(),
                LabelUtil.class.getClassLoader());
    }

    public static Properties getLabelProperties() {
        ResourceBundle rb = getLabels();
        Properties properties = new Properties(rb.keySet().size());
        for (String key : rb.keySet()) {
            properties.put(key, rb.getString(key));
        }
        return properties;
    }

    /**
     * Get a label with a specific key.
     * @param key the key of the label.
     * @return the value of the label.
     */
    public static String getLabel(final String key) {
        return getLabels().getString(key);
    }

    /**
     * Get a Map with the labels property bundle.
     * @param key the key to get the labels property bundle.
     * @return a Map with the labels property bundle.
     */
    public static Map getArgLabelMap(final String key) {
        Map arg = new HashMap();
        arg.put(key, getLabels());
        return arg;
    }

    /**
     * Get a Map with the labels property bundle with the key, "labels".
     * @return a Map with the labels property bundle.
     */
    public static Map getArgLabelMap() {
        return getArgLabelMap("labels");
    }


}
