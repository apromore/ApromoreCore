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

package org.apromore.portal.dialogController;

import org.apromore.commons.item.ItemNameUtils;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.zkoss.util.media.Media;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test suite for {@link ImportController}. */
class ImportControllerUnitTest {

    /** Mock CSV importer. */
    private FileImporterPlugin fileImporterPlugin = new FileImporterPlugin() {
        @Override
        public Set<String> getFileExtensions() {
            return Collections.singleton("csv");
        }

        @Override
        public void importFile(Media media, boolean isPublic) {
            System.out.println("Importing " + media);
        }
    };


    // Test cases.

    /** Test the {@link ImportController#importFile} method with no file importers and a CSV log. */
    @Test
    void testImportFile_CSV() throws Exception {
        testImportFile(Collections.emptyList(), "CallcenterExample.csv");
    }

    /** Test the {@link ImportController#importFile} method with no file importers and a zipped CSV log. */
    @Test
    void testImportFile_CSVzip() throws Exception {
        testImportFile(Collections.emptyList(), "CallcenterExample.zip");
    }

    /** Test the {@link ImportController#importFile} method with a mock CSV importer and a zipped CSV log. */
    @Test
    @Disabled("Need to inject the ZK Execution instance for this to be testable")
    void testImportFile_CSVzip2() throws Exception {
        testImportFile(Collections.singletonList(fileImporterPlugin), "CallcenterExample.zip");
    }


    // Internal method

    private void testImportFile(List<FileImporterPlugin> fileImporterPlugins, String path) throws Exception {
        MainController mainController = new MainController(null);
        ImportController controller = new ImportController(mainController, null, fileImporterPlugins,
                System.out::println);

        InputStream in = ImportControllerUnitTest.class.getResourceAsStream("/" + path);
        assert in != null;

        controller.importFile(new MediaImpl(path, in, Charset.forName("UTF-8"), ItemNameUtils.findExtension(path)));
    }
}
