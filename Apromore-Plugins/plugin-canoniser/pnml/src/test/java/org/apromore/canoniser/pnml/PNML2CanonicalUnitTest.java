package org.apromore.canoniser.pnml;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.pnml.internal.PNML2Canonical;
import org.apromore.canoniser.pnml.internal.pnml2canonical.NamespaceFilter;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.pnml.PnmlType;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;

import static org.junit.Assert.assertTrue;

@Ignore
public class PNML2CanonicalUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PNML2CanonicalUnitTest.class.getName());

    @Test
    public void testNothing() {
        assertTrue(true);
    }

    /**
     * @param args
     */
    @SuppressWarnings("unchecked")
    public void main(String[] args) {
        File foldersave = new File("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_mapped_cpf_anf");
        File folder = new File("Apromore-Core/apromore-service/src/test/resources/PNML_models/woped_cases_original_pnml");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };

        int n = 0;
        File[] folderContent = folder.listFiles(fileFilter);
        for (int i = 0; i < folderContent.length; i++) {
            File file = folderContent[i];
            String filename = file.getName();
            StringTokenizer tokenizer = new StringTokenizer(filename, ".");
            String filename_without_path = tokenizer.nextToken();
            String extension = filename.split("\\.")[filename.split("\\.").length - 1];

            if (extension.compareTo("pnml") == 0) {
                LOGGER.debug("Analysing " + filename);
                n++;
                try {
                    JAXBContext jc = JAXBContext.newInstance("org.apromore.pnml");
                    Unmarshaller u = jc.createUnmarshaller();
                    XMLReader reader = XMLReaderFactory.createXMLReader();

                    // Create the filter (to add namespace) and set the xmlReader as its parent.
                    NamespaceFilter inFilter = new NamespaceFilter("pnml.apromore.org", true);
                    inFilter.setParent(reader);

                    // Prepare the input, in this case a java.io.File (output)
                    InputSource is = new InputSource(new FileInputStream(file));

                    // Create a SAXSource specifying the filter
                    SAXSource source = new SAXSource(inFilter, is);
                    JAXBElement<PnmlType> rootElement = (JAXBElement<PnmlType>) u.unmarshal(source);
                    PnmlType pnml = rootElement.getValue();

                    PNML2Canonical pn = new PNML2Canonical(pnml, filename_without_path);

                    jc = JAXBContext.newInstance("org.apromore.cpf");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<CanonicalProcessType> cprocRootElem = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(pn.getCPF());
                    m.marshal(cprocRootElem, new File(foldersave, filename_without_path + ".cpf"));

                    jc = JAXBContext.newInstance("org.apromore.anf");
                    m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(pn.getANF());
                    m.marshal(annsRootElem, new File(foldersave, filename_without_path + ".anf"));

                } catch (JAXBException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.debug("Skipping " + filename);
            }
        }
        LOGGER.debug("Analysed " + n + " files.");
    }

}
