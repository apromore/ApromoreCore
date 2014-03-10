package org.apromore.canoniser.pnml;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.pnml.internal.Canonical2PNML;
import org.apromore.canoniser.pnml.internal.pnml2canonical.NamespaceFilter;
import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.CPFSchema;
import org.apromore.pnml.ObjectFactory;
import org.apromore.pnml.PNMLSchema;
import org.apromore.pnml.PnmlType;
import org.apromore.pnml.NetType;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertTrue;

public class Canonical2PNMLUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(Canonical2PNMLUnitTest.class.getName());

    File anf_file = null;
    File cpf_file = null;
    File foldersave = new File("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_pnml");
    File output = null;

    /**
     * Decanonize <code>Basic.cpf</code>.
     */
    @Test
    public void testBasic() throws Exception {

        PnmlType pnml = decanonise("Basic");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(2, net.getArc().size());
    }

    /**
     * Decanonize <code>ANDJoin.cpf</code>.
     */
    @Test
    public void testANDJoin() throws Exception {

        PnmlType pnml = decanonise("ANDJoin");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(3, net.getArc().size());
    }

    /**
     * Decanonize <code>ANDSplit.cpf</code>.
     */
    @Test
    public void testANDSplit() throws Exception {

        PnmlType pnml = decanonise("ANDSplit");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(3, net.getArc().size());
    }

    /**
     * Decanonize <code>ORJoin.cpf</code>.
     */
    @Test
    public void testORJoin() throws Exception {

        PnmlType pnml = decanonise("ORJoin");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(3, net.getArc().size());
    }

    /**
     * Decanonize <code>ORSplit.cpf</code>.
     */
    @Test
    public void testORSplit() throws Exception {

        PnmlType pnml = decanonise("ORSplit");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(3, net.getArc().size());
    }

    /**
     * Decanonize <code>XORJoin.cpf</code>.
     */
    @Test
    public void testXORJoin() throws Exception {

        PnmlType pnml = decanonise("XORJoin");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(3, net.getArc().size());
    }

    /**
     * Decanonize <code>XORSplit.cpf</code>.
     */
    @Test
    public void testXORSplit() throws Exception {

        PnmlType pnml = decanonise("XORSplit");

        // Inspect the result
        assertEquals(1, pnml.getNet().size());
        NetType net = pnml.getNet().get(0);
        assertEquals(3, net.getArc().size());
    }

    // Internal methods

    private PnmlType decanonise(final String fileName) throws FileNotFoundException, JAXBException, SAXException {

        CanonicalProcessType cpf = CPFSchema.unmarshalCanonicalFormat(
            new FileInputStream(new File("src/test/resources/CPF_testcases/" + fileName + ".cpf")),
            true  // validate?
        ).getValue();

        System.out.println("CPF nets: " + cpf.getNet().size());
        System.out.println("CPF edges on first net: " + cpf.getNet().get(0).getEdge().size());

        /*
        AnnotationsType anf = ANFSchema.unmarshalAnnotationFormat(
            new FileInputStream(new File("src/test/resources/CPF_testcases/" + fileName + ".anf")),
            true  // validate?
        ).getValue();
        */

        PnmlType pnml = (new Canonical2PNML(cpf /*, anf, "dummy-filename"*/)).getPNML();

        // Serialize the decanonized PNML for inspection
        PNMLSchema.marshalPNMLFormat(
            new FileOutputStream(new File("target/" + fileName + ".pnml")),
            pnml,
            false  // validate?
        );

        return pnml;
    }


    // Older tests -- these have been salvaged to run, but don't actually test anything other than parsing

    @Test
    public void testWoped() {

        String cpf_file_without_path = null;
        String anf_file_without_path = null;

        File anf_file = null;
        File cpf_file = null;
        File foldersave = new File("target");
        File output = null;
        File folder = new File("src/test/resources/PNML_testcases/woped_cases_expected_cpf");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        File[] folderContent = folder.listFiles(fileFilter);
        int n = 0;

        for (File file : folderContent) {
            String filename = file.getName();
            StringTokenizer tokenizer = new StringTokenizer(filename, ".");
            String filename_without_path = tokenizer.nextToken();

            String extension = filename.split("\\.")[filename.split("\\.").length - 1];

            output = new File(foldersave + "/" + filename_without_path + ".pnml");

            if (!filename.contains("subnet")) {
                if (extension.compareTo("cpf") == 0 && extension.compareTo("anf") == 0) {
                    LOGGER.debug("Skipping " + filename);
                }

                if (extension.compareTo("anf") == 0) {
                    LOGGER.debug("Analysing " + filename);
                    n++;
                    anf_file = new File(folder + "/" + filename);
                    anf_file_without_path = filename_without_path;
                }

                if (extension.compareTo("cpf") == 0) {
                    LOGGER.debug("Analysing " + filename);
                    n++;
                    cpf_file = new File(folder + "/" + filename);
                    cpf_file_without_path = filename_without_path;

                }
            }

            if (anf_file != null && cpf_file != null && anf_file_without_path != null && cpf_file_without_path != null && anf_file_without_path.equals(cpf_file_without_path) && !filename.contains("subnet")) {

                try {
                    JAXBContext jc = JAXBContext.newInstance("org.apromore.cpf");
                    Unmarshaller u = jc.createUnmarshaller();
                    JAXBElement<CanonicalProcessType> rootElement = (JAXBElement<CanonicalProcessType>) u.unmarshal(cpf_file);
                    CanonicalProcessType cpf = rootElement.getValue();

                    jc = JAXBContext.newInstance("org.apromore.anf");
                    u = jc.createUnmarshaller();
                    JAXBElement<AnnotationsType> anfRootElement = (JAXBElement<AnnotationsType>) u.unmarshal(anf_file);
                    AnnotationsType anf = anfRootElement.getValue();

                    // Canonical2EPML canonical2epml_1 = new
                    // Canonical2EPML(cpf,true);

                    jc = JAXBContext.newInstance("org.apromore.pnml");

                    Canonical2PNML canonical2pnml = new Canonical2PNML(cpf, anf, filename_without_path);

                    Marshaller m1 = jc.createMarshaller();

                    NamespaceFilter outFilter = new NamespaceFilter(null, false);

                    OutputFormat format = new OutputFormat();
                    format.setIndent(true);
                    format.setNewlines(true);
                    format.setXHTML(true);
                    format.setExpandEmptyElements(true);
                    format.setNewLineAfterDeclaration(false);

                    XMLWriter writer = null;
                    try {
                        writer = new XMLWriter(new FileOutputStream(output), format);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // Attach the writer to the filter
                    outFilter.setContentHandler(writer);

                    // Tell JAXB to marshall to the filter which in turn will
                    // call the writer
                    m1.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<PnmlType> cprocRootElem1 = new ObjectFactory().createPnml(canonical2pnml.getPNML());
                    m1.marshal(cprocRootElem1, outFilter);

                } catch (JAXBException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        LOGGER.debug("Analysed " + n + " files.");
    }

}
