/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd. All rights reserved.
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

package org.apromore.portal.common;

import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apromore.dao.model.Role;
import org.apromore.portal.model.FolderType;

@Getter
@Setter
public class FolderItem {

    private Integer id;
    private String name;
    private String path;
    private List<FolderType> folderPath;

    public static final Comparator<FolderItem> folderItemComparator =
        Comparator.comparing(FolderItem::getPath);

    public FolderItem(Integer id, String name, List<FolderType> folderPath) {
        this.name = name;
        this.id = id;
        if (folderPath == null) {
            this.path = name;
        } else {
            this.path = makePathFromFolderPath(folderPath);
        }
        this.folderPath = folderPath;
    }

    /**
     * Reusable makePath from a list of FolderType
     *
     * @param folderPath
     * @return String
     */
    public static String makePathFromFolderPath(List<FolderType> folderPath) {
        return makePathFromFolderPath(folderPath, " / ", "");
    }

    public static String makePathFromFolderPath(List<FolderType> folderPath, String delimiter, String prefix) {
        StringBuilder path = new StringBuilder(prefix);
        boolean started = false;

        for (FolderType folder : folderPath) {
            int folderId = folder.getId();
            if (folderId == 0) {
                continue;
            }
            if (started) {
                path.append(delimiter);
            }
            String folderName = folder.getFolderName();
            path.append(folderName);
            started = true;
        }
        return path.toString();
    }
}