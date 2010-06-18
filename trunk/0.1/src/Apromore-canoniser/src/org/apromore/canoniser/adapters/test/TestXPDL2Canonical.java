package org.apromore.canoniser.adapters.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.adapters.XPDL2Canonical;
import org.apromore.canoniser.exception.ExceptionStore;
import org.apromore.cpf.CanonicalProcessType;
import org.wfmc._2008.xpdl2.PackageType;

public class TestXPDL2Canonical {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		File folder = new File("/home/fauvet/models/xpdl");
		FileFilter fileFilter = new FileFilter() { 
			public boolean accept(File file) { 
				return file.isFile(); 
			} 
		}; 
		File[] folderContent = folder.listFiles (fileFilter);
		int n =0;
		for (int i=0;i<folderContent.length;i++) {
			File file = folderContent[i];
			String filename = file.getName();
			String extension = filename.split("\\.")[filename.split("\\.").length-1];
			if (extension.compareTo("xpdl")==0) {
				System.out.println ("Analysing " + filename);
				n++;
				try{
					JAXBContext jc = JAXBContext.newInstance("org.wfmc._2008.xpdl2");
					Unmarshaller u = jc.createUnmarshaller();
					JAXBElement<PackageType> rootElement = (JAXBElement<PackageType>) u.unmarshal(file);
					PackageType pkg = rootElement.getValue();

					XPDL2Canonical xpdl2canonical = new XPDL2Canonical(pkg);

					jc = JAXBContext.newInstance("org.apromore.cpf");
					Marshaller m = jc.createMarshaller();
					m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					JAXBElement<CanonicalProcessType> cprocRootElem = 
						new org.apromore.cpf.ObjectFactory().createCanonicalProcess(xpdl2canonical.getCpf());
					m.marshal(cprocRootElem, new File(folder,filename + ".cpf"));

					jc = JAXBContext.newInstance("org.apromore.anf");
					m = jc.createMarshaller();
					m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					JAXBElement<AnnotationsType> annsRootElem = new org.apromore.anf.ObjectFactory().createAnnotations(xpdl2canonical.getAnf());
					m.marshal(annsRootElem, new File (folder,filename + ".anf"));

				} catch (JAXBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			} else {
				System.out.println ("Skipping " + filename);
			}	
		}
		System.out.println ("Analysed " + n + " files.");
	} 
}
