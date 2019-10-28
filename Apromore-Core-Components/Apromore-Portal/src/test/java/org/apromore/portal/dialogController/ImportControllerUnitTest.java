package org.apromore.portal.dialogController;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.junit.Ignore;
import org.junit.Test;
import org.zkoss.util.media.Media;

/** Test suite for {@link ImportController}. */
public class ImportControllerUnitTest {

    /** Mock CSV importer. */
    private FileImporterPlugin fileImporterPlugin = new FileImporterPlugin() {
        @Override
        public Set<String> getFileExtensions() {
            return Collections.singleton("csv");
        }

        @Override
        public void importFile(Media media, PortalContext context, boolean isPublic) {
            System.out.println("Importing " + media);
        }
    };


    // Test cases.

    /** Test the {@link ImportController#importFile} method with no file importers and a CSV log. */
    @Test
    public void testImportFilei_CSV() throws Exception {
        testImportFile(Collections.emptyList(), "CallcenterExample.csv");
    }

    /** Test the {@link ImportController#importFile} method with no file importers and a zipped CSV log. */
    @Test
    public void testImportFile_CSVzip() throws Exception {
        testImportFile(Collections.emptyList(), "CallcenterExample.zip");
    }

    /** Test the {@link ImportController#importFile} method with a mock CSV importer and a zipped CSV log. */
    @Test
    @Ignore("Need to inject the ZK Execution instance for this to be testable")
    public void testImportFile_CSVzip2() throws Exception {
        testImportFile(Collections.singletonList(fileImporterPlugin), "CallcenterExample.zip");
    }


    // Internal method

    private void testImportFile(List<FileImporterPlugin> fileImporterPlugins, String path) throws Exception {
        MainController mainController = new MainController(null);
        ImportController controller = new ImportController(mainController, null, fileImporterPlugins, (message) -> { System.out.println(message); });

        InputStream in = ImportControllerUnitTest.class.getResourceAsStream("/" + path);
        assert in != null;

        controller.importFile(new MediaImpl(path, in, Charset.forName("UTF-8")));
    }
}
