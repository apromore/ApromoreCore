/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

// Java 2 Standard classes
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;

// Third party classes
import ee.ut.core.comparison.Comparator;
import ee.ut.core.comparison.ComparatorGED;
import ee.ut.core.comparison.differences.Difference;
import ee.ut.core.comparison.differences.Differences;
import ee.ut.core.comparison.differences.Run;
import ee.ut.core.comparison.differences.Runs;
import ee.ut.core.comparison.verbalizer.VerbalizerGraphical;
import ee.ut.core.models.reader.TypeModel;
import ee.ut.runner.ModelAbstractions;
import hub.top.petrinet.Node;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zul.Messagebox;

// Local classes
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.exception.DialogException;

/**
 * Apply Abel Armas-Cervantes' BP-diff comparison to two process model versions.
 *
 * @see <a href="http://diffbp-bpdiff.rhcloud.com">BP-Diff online demo</a>
 */
public class CompareController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(CompareController.class.getCanonicalName());

    public CompareController(MainController mainC, MenuController menuC,
                             ProcessSummaryType process1, VersionSummaryType version1,
                             ProcessSummaryType process2, VersionSummaryType version2)
            throws SuspendNotAllowedException, InterruptedException, DialogException {

        try {
            ModelAbstractions<Node> model1 = toModelAbstractions(process1, version1);
            ModelAbstractions<Node> model2 = toModelAbstractions(process2, version2);

            HashSet<String> commonLabels = new HashSet<String>(model1.getLabels());
            commonLabels.retainAll(model2.getLabels());
            model1.computePES(commonLabels);
            model2.computePES(commonLabels);
            model1.computeFoldedAES();
            model2.computeFoldedAES();
            Comparator<Node> diff = new ComparatorGED<Node>(model1, model2, commonLabels, new VerbalizerGraphical<Node>());
            Differences differences = diff.getDifferences();

            Set<RequestParameterType<?>> requestParameters = new HashSet<>();
            requestParameters.add(new RequestParameterType<Integer>("m1_pes_size", model1.getPES().getEvents().size()));
            requestParameters.add(new RequestParameterType<Integer>("m2_pes_size", model2.getPES().getEvents().size()));
            requestParameters.add(new RequestParameterType<String>("m1_differences_json", Differences.toJSON(differences)));
            mainC.compareProcesses(process1, version1, process2, version2, "BPMN 2.0", null, null, requestParameters);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to perform comparison", e);
            Messagebox.show("Unable to perform comparison", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private static String toString(Runs runs) {
        if (runs == null) {
            return null;
        } else {
            String s = "";
            for (Run run: runs.getRuns()) {
                s += (" " + run);
            }
            return s;
        }
    }

    /**
     * Create the corresponding BP-diff model for a process model version stored in Apromore.
     */
    private ModelAbstractions<Node> toModelAbstractions(ProcessSummaryType process, VersionSummaryType version) throws Exception {
        ExportFormatResultType result = getService().exportFormat(
            process.getId(),             // process ID
            null,                        // process name
            version.getName(),           // branch
            version.getVersionNumber(),  // version number,
            "BPMN 2.0",                  // nativeType,
            null,                        // annotation name,
            false,                       // with annotations?
            null,                        // owner
            Collections.EMPTY_SET        // canoniser properties
        );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TransformerFactory.newInstance().newTransformer().transform(new StreamSource(result.getNative().getInputStream()), new StreamResult(baos));
        return new ModelAbstractions<Node>(baos.toByteArray(), TypeModel.BPMN);
    }
}
