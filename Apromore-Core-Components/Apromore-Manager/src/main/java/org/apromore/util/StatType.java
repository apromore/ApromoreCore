package org.apromore.util;

/**
 * Parameter type for the {@link org.apromore.service.impl.EventLogServiceImpl#storeStatsByType} method.
 */
public enum StatType {

    FILTER,
    CASE,
    ACTIVITY,
    RESOURCE,
    VARIANT,
    CHART_POINT,
    CHART_CATEGORY
}
