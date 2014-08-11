package org.apromore.canoniser.xpdl;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.xpdl.internal.XPDL2Canonical;
import org.apromore.cpf.CanonicalProcessType;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wfmc._2009.xpdl2.PackageType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileFilter;
import java.util.StringTokenizer;

import static org.junit.Assert.assertTrue;

@Ignore
public class XPDL2CanonicalUnitTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(XPDL2CanonicalUnitTest.class.getName());

    @Test
    public void testNothing() {
        assertTrue(true);
    }


    /**
     * @param args
     */
    public void main(String[] args) {
        File folder = new File("work_package");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isFile();
            }
        };
        File[] folderContent = folder.listFiles(fileFilter);
        int n = 0;
        for (int i = 0; i < folderContent.length; i++) {
            File file = folderContent[i];
            String filename = file.getName();
            StringTokenizer tokenizer = new StringTokenizer(filename, ".");
            String filename_without_path = tokenizer.nextToken();
            String extension = filename.split("\\.")[filename.split("\\.").length - 1];
            if (extension.compareTo("xpdl") == 0) {
                LOGGER.debug("Analysing " + filename);
                n++;
                try {
                    JAXBContext jc = JAXBContext.newInstance("org.wfmc._2009.xpdl2");
                    Unmarshaller u = jc.createUnmarshaller();
                    JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(file);
                    PackageType pkg = rootElement.getValue();

                    XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg);

                    jc = JAXBContext.newInstance("org.apromore.cpf");
                    Marshaller m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<CanonicalProcessType> cprocRootElem = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
                    m.marshal(cprocRootElem, new File(folder, "111" + filename_without_path + ".cpf"));

                    jc = JAXBContext.newInstance("org.apromore.anf");
                    m = jc.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
                    m.marshal(annsRootElem, new File(folder, "111" + filename_without_path + ".anf"));

                } catch (JAXBException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                LOGGER.debug("Skipping " + filename_without_path);
            }
        }
        LOGGER.debug("Analysed " + n + " files.");
    }
}
