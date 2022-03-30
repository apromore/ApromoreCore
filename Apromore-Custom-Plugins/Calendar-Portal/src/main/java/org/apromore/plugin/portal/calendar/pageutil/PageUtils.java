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

package org.apromore.plugin.portal.calendar.pageutil;

import java.io.IOException;
import java.io.InputStreamReader;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.PageDefinition;

public class PageUtils {

    public static PageDefinition getPageDefinition(String uri) throws IOException {

        String url = "static/" + uri;
        Execution current = Executions.getCurrent();
        PageDefinition pageDefinition = current.getPageDefinitionDirectly(
            new InputStreamReader(PageUtils.class.getClassLoader().getResourceAsStream(url)), "zul");
        return pageDefinition;
    }
}
